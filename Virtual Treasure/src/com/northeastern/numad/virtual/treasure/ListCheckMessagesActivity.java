package com.northeastern.numad.virtual.treasure;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

import edu.neu.mobileclass.apis.KeyValueAPI;

public class ListCheckMessagesActivity extends ListActivity {
	final static String MESSAGE_DELIMITER = "-+-";
	final static String KEY_DELIMITER = "--";
	String userName;
	String userId;
	String[] treasureList;

	Facebook mFacebook = new Facebook("561901693827304");
	AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(mFacebook);
	private SharedPreferences mPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub\
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_check_messages);

		checkFacebookAuthentication();
	}

	private void checkFacebookAuthentication() {
		mPrefs = getPreferences(MODE_PRIVATE);
		String access_token = mPrefs.getString("access_token", null);
		long expires = mPrefs.getLong("access_expires", 0);
		if (access_token != null) {
			mFacebook.setAccessToken(access_token);
		}
		if (expires != 0) {
			mFacebook.setAccessExpires(expires);
		}

		/*
		 * Only call authorize if the access_token has expired.
		 */
		if (!mFacebook.isSessionValid()) {
			// session is invalid create another token
			mFacebook.authorize(this, new String[] {}, new DialogListener() {
				@Override
				public void onComplete(Bundle values) {
					SharedPreferences.Editor editor = mPrefs.edit();
					editor.putString("access_token", mFacebook.getAccessToken());
					editor.putLong("access_expires",
							mFacebook.getAccessExpires());
					editor.commit();
					Log.i("virtualtreasure", "calling facebook request");
					asyncRunner.request("me", new FriendsRequestListener());

				}

				@Override
				public void onFacebookError(FacebookError error) {
					Log.i("virtualtreasure", error.toString());
				}

				@Override
				public void onError(DialogError e) {
					Log.i("virtualtreasure", e.toString());
				}

				@Override
				public void onCancel() {
				}
			});
		} else {
			// session is valid
			Log.i("virtualtreasure", "calling facebook request - valid token");
			asyncRunner.request("me", new FriendsRequestListener());
		}

	}

	// Facebook method
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		mFacebook.authorizeCallback(requestCode, resultCode, data);
	}

	public class FriendsRequestListener implements RequestListener {

		/**
		 * Called when the request to get friends has been completed. Retrieve
		 * and parse and display the JSON stream.
		 */
		public void onComplete(String response, Object state) {
			try {
				// process the response here: executed in background thread
				Log.d("Facebook-Example-Friends Request", "response.length(): "
						+ response.length());
				Log.d("Facebook-Example-Friends Request", "Response: "
						+ response);

				final JSONObject json = new JSONObject(response);
				userId = json.getString("id");
				userName = json.getString("name");

				Log.i("virtualtreasure", userId + "--" + userName);

				String treasureListString = KeyValueAPI.get("teamAR",
						"hello123", userId + KEY_DELIMITER + userName);

				treasureList = treasureListString.split(MESSAGE_DELIMITER);

				Log.i("virtualtreasure", Arrays.toString(treasureList));
				// Only the original owner thread can touch its views

				ListCheckMessagesActivity.this.runOnUiThread(new Runnable() {
					public void run() { // FriendsArrayAdapter
						ArrayAdapter aa = new ArrayAdapter<String>(
								getBaseContext(),
								android.R.layout.simple_dropdown_item_1line,
								treasureList);
						setListAdapter(aa);
						aa.notifyDataSetChanged();
					}
				});

			} catch (JSONException e) {
				Log.w("Facebook-Example", e.toString());
			}
		}

		@Override
		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			Log.i("leavemessageactivity", e.toString());

		}
	}

	private void fetchDataFromServer() {
		/*
		 * String oldMessage = KeyValueAPI.get("teamAR", "hello123", friendId +
		 * KEY_DELIMITER + );
		 */

	}
}