package com.northeastern.numad.virtual.treasure;

import edu.neu.mobileclass.apis.KeyValueAPI;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SendGiftToFriendActivity extends Activity {

	private String friendName;
	private String friendId;
	EditText messageEditText;
	final static String MESSAGE_DELIMITER = "-+-";
	final static String KEY_DELIMITER = "--";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_gift_to_friend);
		TextView friendNameTextView = (TextView) findViewById(R.id.textView1);

		Intent i = getIntent();
		friendName = i.getStringExtra("friend-name");
		friendId = i.getStringExtra("friend-id");
		friendNameTextView.setText(friendName + "-" + friendId);

		messageEditText = (EditText) findViewById(R.id.editText1);
	}

	public void sendButtonClicked(View v) {
		if (KeyValueAPI.isServerAvailable()) {
			String message = messageEditText.getText().toString();
			String oldMessage = KeyValueAPI.get("teamAR", "hello123", friendId
					+ KEY_DELIMITER + friendName);
			if (oldMessage == null) {
				KeyValueAPI.put("teamAR", "hello123", friendId + KEY_DELIMITER
						+ friendName, message);
			} else {
				KeyValueAPI.put("teamAR", "hello123", friendId + KEY_DELIMITER
						+ friendName, oldMessage + MESSAGE_DELIMITER + message);
			}
		}
	}
}
