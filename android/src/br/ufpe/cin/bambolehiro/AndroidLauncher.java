package br.ufpe.cin.bambolehiro;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication implements Game.IOpenActivity {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useWakelock = true;
		Game bambolehiro = new Game();
		bambolehiro.setOpenActivity(this);
		initialize(bambolehiro, config);
	}

	@Override
	public void openScoreActivity(double score){
		Intent intent = new Intent(this, ScoreActivity.class);
		// do whatever you want with the supplied parameters.
		intent.putExtra("score", score);
		startActivity(intent);
	}


}
