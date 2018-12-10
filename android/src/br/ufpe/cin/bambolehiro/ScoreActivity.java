package br.ufpe.cin.bambolehiro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);


        Button restart_button = (Button) findViewById(R.id.btn_restart);
        Button menu_button = (Button) findViewById(R.id.btn_menu_score);

        // SKIPPING line just to check if the intent is working
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
        restart_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScoreActivity.this, AndroidLauncher.class);
                ScoreActivity.this.startActivity(intent);
            }
        });

        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScoreActivity.this, MainActivity.class);
                ScoreActivity.this.startActivity(intent);
            }
        });

        TextView score = (TextView) findViewById(R.id.score);
        TextView highScores = (TextView) findViewById(R.id.high_scores);
        SharedPreferences bambolehiroPrefs = getSharedPreferences("bambolehiro", 0);
        score.setText(bambolehiroPrefs.getString("score", ""));
        highScores.setText(bambolehiroPrefs.getString("highScore", ""));


    }

}
