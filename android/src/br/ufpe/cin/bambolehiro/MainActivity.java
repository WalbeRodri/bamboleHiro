package br.ufpe.cin.bambolehiro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button config_button = (Button) findViewById(R.id.btn_config_main);
        Button play_button = (Button) findViewById(R.id.btn_play_main);

        play_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FullscreenActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        config_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //TODO bambole conection screen
                Intent intent = new Intent(MainActivity.this, ConectionActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }
}
