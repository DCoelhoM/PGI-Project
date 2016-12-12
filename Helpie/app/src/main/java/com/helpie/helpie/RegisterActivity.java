package com.helpie.helpie;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    private EditText name;
    private EditText email;
    private EditText contact;
    private EditText pw;
    private EditText cpw;
    private Button register;
    private Button login;

    private String validate_code = "random_generated_code";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        contact = (EditText) findViewById(R.id.contact);
        pw = (EditText) findViewById(R.id.pw);
        cpw = (EditText) findViewById(R.id.cpw);

        register = (Button) findViewById(R.id.register);
        login = (Button) findViewById(R.id.login);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String n = name.getText().toString().trim();
                final String e = email.getText().toString().trim();
                final String c = contact.getText().toString().trim();
                final String p = pw.getText().toString().trim();
                final String cp = cpw.getText().toString().trim();

                if (confirmInputs(n,e,c,p,cp)){
                    final EditText input = new EditText(RegisterActivity.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    input.setLayoutParams(lp);
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    API r = new API();
                    String result = r.confirmContact(c);
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(result);
                        if (obj.getInt("success") == 1) {
                            validate_code = obj.getString("code");
                            new AlertDialog.Builder(RegisterActivity.this)
                                    .setTitle("Código de Validação")
                                    .setMessage("Insira o código que recebeu por mensagem.")
                                    .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            String user_code = input.getText().toString().trim();
                                            if (user_code.equals(validate_code)) {
                                                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                                StrictMode.setThreadPolicy(policy);
                                                API r2 = new API();
                                                String result = r2.userRegister(n, e, c, p);
                                                JSONObject obj = null;
                                                try {
                                                    obj = new JSONObject(result);
                                                    if (obj.getInt("success") == 1) {
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
                                            }
                                        }
                                    })
                                    .setNegativeButton("Voltar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    })
                                    .setView(input)
                                    .setIcon(0)
                                    .show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Número Inválido", Toast.LENGTH_LONG).show();
                            validate_code = "random_generated_code";
                        }
                    } catch (JSONException ex) {
                        Toast.makeText(getApplicationContext(), "Algo correu mal!", Toast.LENGTH_LONG).show();
                        validate_code = "random_generated_code";
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
    private boolean confirmInputs(final String name, final String email, final String contact, final String pw, final String cpw){
        if (!name.isEmpty() && !email.isEmpty() && !contact.isEmpty() && !pw.isEmpty() && !cpw.isEmpty()) {
            if (pw.compareTo(cpw)==0) {
                if (email.matches("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+.[a-zA-Z0-9-.]+$")){
                    if (contact.length()==9 && contact.matches("[0-9]+")){
                        return true;
                    }else {
                        Toast.makeText(getApplicationContext(), "Número inválido!", Toast.LENGTH_LONG).show();
                        return false;
                    }
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
