package com.northeastern.numad.virtual.treasure;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class ThingsToDoActivity extends Activity {

		

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.things_to_do);
		// initializations
		
		
		Drawable llbackground = getResources().getDrawable(R.drawable.treasure_map);
		llbackground.setAlpha(100);
	    LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout);
	    ll.setBackgroundDrawable(llbackground);
	
		
			}

	public void newGameButtonClicked(View v) {
		// set difficulty level
		final Dialog d = new Dialog(this);
		d.setContentView(R.layout.difficulty_level_dialog);
		d.setTitle("Set Difficulty Level");
		d.setCancelable(true);

		// set button click listeners
		Button okButton = (Button) d.findViewById(R.id.okButton);
		Button cancelButton = (Button) d.findViewById(R.id.cancelButton);

		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// start new activity
				d.dismiss();
				Intent i = new Intent(ThingsToDoActivity.this,
						GameActivity.class);
				startActivity(i);
			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				d.dismiss();
			}
		});

		// get hold of TextView
		final TextView dLabel = (TextView) d
				.findViewById(R.id.difficultyLevelLabel);
		dLabel.setText("1");

		// set seekBar listener
		SeekBar sb = (SeekBar) d.findViewById(R.id.seekBar1);
		sb.setMax(4);
		sb.setProgress(1);
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				dLabel.setText(" " + progress);

			}
		});

		d.show();
	}

	public void checkMessageButtonClicked(View v) {
		Intent i = new Intent(this, CheckMessagesActivity.class);
		startActivity(i);
	}

	public void leaveMessageButtonClicked(View v) {
		Intent i = new Intent(this, LeaveMessageActivity.class);
		startActivity(i);
	}
}
