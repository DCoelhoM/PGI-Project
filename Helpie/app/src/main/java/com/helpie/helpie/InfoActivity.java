package com.helpie.helpie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

public class InfoActivity extends AppCompatActivity {

    private Button back;
    private WebView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        back = (Button) findViewById(R.id.back);
        info = (WebView) findViewById(R.id.info);

        String ContentStr = String.valueOf(Html
                .fromHtml("<![CDATA[<body style=\"text-align:justify;color:#f2f2f2;background:#3197d6; \">"
                        + "    A Helpie não se responsabiliza pelo conteúdo dos pedidos feitos, nem pela qualidade dos \"serviços\" prestados, sendo que neste caso os rankings poderão ajudar a prever se uma determinada pessoa é qualificada para o serviço. Também não se responsabiliza pela remuneração dos serviços prestados, apelando à sensatez dos intervenientes para que não haja um \"lado prejudicado\", uma boa descrição do serviço e estabelecimento de \"normas\" pode ajudar a diminuir eventuais conflitos entre os intervenientes."
                        +"</body>]]>"));

        info.loadData(ContentStr, "text/html; charset=utf-8", "utf-8");


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoActivity.this, MainMenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(InfoActivity.this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }
}
