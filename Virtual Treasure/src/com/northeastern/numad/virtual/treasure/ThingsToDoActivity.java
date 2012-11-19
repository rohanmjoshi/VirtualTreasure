package com.northeastern.numad.virtual.treasure;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class ThingsToDoActivity extends Activity {

	ArrayAdapter<String> gridAdapter;
	String[] BUTTONNAMES = { "Resume", "New Game", "Leave Message",
			"Check Messages", "Quit" };
	String TAG = "ThingsToDo";
	GridView gv1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.things_to_do);
		// initializations

		gridAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, BUTTONNAMES);

		gv1 = (GridView) findViewById(R.id.gridView1);
		gv1.setAdapter(gridAdapter);
		gv1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (arg2) {
				case 0:
					Log.i(TAG, "Resume button called");
					break;
				case 1:
					Log.i(TAG, "New game button clicked");
					newGameButtonClicked();
					break;
				case 2:
					Log.i(TAG, "Leave Message button Clicked");
					leaveMessageButtonClicked();
					break;
				case 3:
					Log.i(TAG, "Check Message button clicked");
					checkMessageButtonClicked();
					break;
				case 4:
					Log.i(TAG, "Quit Message button clicked");
					break;
				}

			}

		});
	}

	public void newGameButtonClicked() {
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

	public void checkMessageButtonClicked() {
		Intent i = new Intent(this, CheckMessagesActivity.class);
		startActivity(i);
	}

	public void leaveMessageButtonClicked() {
		Intent i = new Intent(this, LeaveMessageActivity.class);
		startActivity(i);
	}
}
