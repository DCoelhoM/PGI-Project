package com.helpie.helpie;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    private EditText name;
    private EditText email;
    private EditText pw;
    private EditText cpw;
    private Button register;
    private Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        pw = (EditText) findViewById(R.id.pw);
        cpw = (EditText) findViewById(R.id.cpw);

        register = (Button) findViewById(R.id.register);
        login = (Button) findViewById(R.id.login);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n = name.getText().toString().trim();
                String e = email.getText().toString().trim();
                String p = pw.getText().toString().trim();
                String cp = cpw.getText().toString().trim();

                if (confirmInputs(n,e,p,cp)){
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    API r = new API();
                    String result = r.userRegister(n, e, p);
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(result);
                        if (obj.getInt("success")==1) {
                            Toast.makeText(getApplicationContext(), n + " registado com sucesso!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Algo correu mal (E-mail em uso)!", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException ex) {
                        Toast.makeText(getApplicationContext(), "Algo correu mal!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    pw.setText("");
                    cpw.setText("");
                }
            }
        });

        // Login button click event
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private boolean confirmInputs(final String name, final String email, final String pw, final String cpw){
        if (!name.isEmpty() && !email.isEmpty() && !pw.isEmpty() && !cpw.isEmpty()) {
            if (pw.compareTo(cpw)==0) {
                if (email.matches("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+.[a-zA-Z0-9-.]+$")){
                    return true;
                } else {
                    Toast.makeText(getApplicationContext(), "E-mail inválido!", Toast.LENGTH_LONG).show();
                    return false;
                }
            } else {
                Toast.makeText(getApplicationContext(), "Palavras passe não coincidem!", Toast.LENGTH_LONG).show();
                return false;
            }
        } else {
            Toast.makeText(getApplicationContext(), "Preencha todos os campos!", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
