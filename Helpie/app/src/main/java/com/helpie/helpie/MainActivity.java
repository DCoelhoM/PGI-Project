package com.helpie.helpie;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText pw;
    private Button login;
    private Button register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = (EditText) findViewById(R.id.email);
        pw = (EditText) findViewById(R.id.pw);

        login = (Button) findViewById(R.id.login);
        register = (Button) findViewById(R.id.register);

        if(SaveSharedPreference.getUsername(MainActivity.this).length()!=0){
            Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        }

        // Login button click event
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String e = email.getText().toString().trim();
                String p = pw.getText().toString().trim();

                if (confirmLogin(e, p)) {
                    Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    email.setText("");
                    pw.setText("");
                }
            }
        });

        // Register button click event
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }
    private boolean confirmLogin(final String email, final String pw){
        if (!email.isEmpty() && !pw.isEmpty()) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            API l = new API();
            String result = l.userLogin(email, pw);
            JSONObject obj = null;
            try {
                obj = new JSONObject(result);
                if (obj.getInt("success")==1) {
                    SaveSharedPreference.setUser(MainActivity.this,obj.getInt("id"),obj.getString("name"),obj.getString("email"));
                    Toast.makeText(getApplicationContext(), "Logado!", Toast.LENGTH_LONG).show();
                    return true;
                } else {
                    Toast.makeText(getApplicationContext(), "Confirme as credenciais!", Toast.LENGTH_LONG).show();
                    return false;
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Algo correu mal!", Toast.LENGTH_LONG).show();
                return false;
            }
        } else {
            Toast.makeText(getApplicationContext(), "Preencher todos os campos!", Toast.LENGTH_LONG).show();
            return false;
        }
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                    }
                }).create().show();
    }
}
