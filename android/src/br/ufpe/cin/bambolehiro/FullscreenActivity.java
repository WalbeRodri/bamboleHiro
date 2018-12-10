package br.ufpe.cin.bambolehiro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        Button easy_button = (Button) findViewById(R.id.btn_1);
        Button normal_button = (Button) findViewById(R.id.btn_2);
        Button hard_button = (Button) findViewById(R.id.btn_3);

        final SharedPreferences bambolehiroPrefs = getSharedPreferences("bambolehiro", 0);

        easy_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FullscreenActivity.this, AndroidLauncher.class);
                intent.putExtra("level", "1");
                SharedPreferences.Editor editor = bambolehiroPrefs.edit();
                editor.putString("level", "1");
                editor.apply();
                FullscreenActivity.this.startActivity(intent);
            }
        });

       normal_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FullscreenActivity.this, AndroidLauncher.class);
                intent.putExtra("level", "2");
                SharedPreferences.Editor editor = bambolehiroPrefs.edit();
                editor.putString("level", "2");
                editor.apply();
                FullscreenActivity.this.startActivity(intent);
            }
        });

       hard_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FullscreenActivity.this, AndroidLauncher.class);
                intent.putExtra("level", "3");
                SharedPreferences.Editor editor = bambolehiroPrefs.edit();
                editor.putString("level", "3");
                editor.apply();
                FullscreenActivity.this.startActivity(intent);
            }
        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }


}
