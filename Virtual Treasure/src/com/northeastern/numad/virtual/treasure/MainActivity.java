package com.northeastern.numad.virtual.treasure;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class MainActivity extends Activity {

	String TAG = "virtualtreasure";

    Facebook facebook = new Facebook("561901693827304");
	AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);

	private SharedPreferences mPrefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button lButton = (Button) findViewById(R.id.buttonLogin);
		Button lfbButton = (Button) findViewById(R.id.buttonFBLogin);
		Button cButton = (Button) findViewById(R.id.buttonAbout);
		OnClickListener sClick = new OnClickListener() {
			public void onClick(View v) {
				onCreateDialog().show();

			}
		};
		OnClickListener aClick = new OnClickListener() {
			public void onClick(View v) {
				startCameraActivity();

			}
		};
		lButton.setOnClickListener(sClick);
		cButton.setOnClickListener(aClick);
	}

	public Dialog onCreateDialog() {

	

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();
		final View v = inflater.inflate(R.layout.login_dialog, null);
		final EditText usernameEditText = (EditText) v
				.findViewById(R.id.username);
		final EditText passwordEditText = (EditText) v
				.findViewById(R.id.password);
		builder.setView(v)
				.setPositiveButton("Enter",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								if (usernameEditText.getText().toString()
										.equals("")) {
									Toast toast = Toast.makeText(
											MainActivity.this,
											"UserName cannot be blank",
											Toast.LENGTH_LONG);
									toast.show();
								} else if (passwordEditText.getText()
										.toString().equals("")) {
									Toast toast = Toast.makeText(
											MainActivity.this,
											"Passowrd cannot be blank",
											Toast.LENGTH_LONG);
									toast.show();
								} else
									startThingsToDoActivity();
							}
						})
				.setNegativeButton("Back",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								onCreateDialog().cancel();
							}
						});

		return builder.create();
	}

	// Facebook method
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		facebook.authorizeCallback(requestCode, resultCode, data);
	}

	public void facebookLoginButtonClicked(View v) {
		Log.i(TAG, "facebook button clicked");
		mPrefs = getPreferences(MODE_PRIVATE);
		String access_token = mPrefs.getString("access_token", null);
		long expires = mPrefs.getLong("access_expires", 0);
		if (access_token != null) {
			facebook.setAccessToken(access_token);
		}
		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}

		/*
		 * Only call authorize if the access_token has expired.
		 */
		if (!facebook.isSessionValid()) {
			// session is invalid create another token
			facebook.authorize(this, new String[] {}, new DialogListener() {
				@Override
				public void onComplete(Bundle values) {
					SharedPreferences.Editor editor = mPrefs.edit();
					editor.putString("access_token", facebook.getAccessToken());
					editor.putLong("access_expires",
							facebook.getAccessExpires());
					editor.commit();
					startThingsToDoActivity();

				}

				@Override
				public void onFacebookError(FacebookError error) {
				}

				@Override
				public void onError(DialogError e) {
				}

				@Override
				public void onCancel() {
				}
			});
		} else {
			// session is  valid
			startThingsToDoActivity();
		}
	}

	public void startThingsToDoActivity() {
		// if the login is successful then go to another activity
		Intent i = new Intent(MainActivity.this, ThingsToDoActivity.class);
		startActivity(i);
	}
	
	public void startCameraActivity() {
		// if the login is successful then go to another activity
		Intent i = new Intent(MainActivity.this, ActivityMaps.class);
		startActivity(i);
	}
}
