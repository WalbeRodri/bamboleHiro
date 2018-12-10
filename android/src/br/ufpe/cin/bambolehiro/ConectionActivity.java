package br.ufpe.cin.bambolehiro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class ConectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conection);

        Button menu_button = (Button) findViewById(R.id.btn_menu_cnt);
        Button play_button = (Button) findViewById(R.id.btn_play_cnt);
        Button conection_button = (Button) findViewById(R.id.btn_conection);

        //TODO implements conection behavior
        conection_button.setClickable(false);

        play_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConectionActivity.this, FullscreenActivity.class);
                ConectionActivity.this.startActivity(intent);
            }
        });

        menu_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConectionActivity.this, MainActivity.class);
                ConectionActivity.this.startActivity(intent);
            }
        });

    }





}
