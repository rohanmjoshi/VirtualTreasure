package com.northeastern.numad.virtual.treasure;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class LeaveMessageActivity extends ListActivity {

	Facebook mFacebook = new Facebook("561901693827304");
	AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(mFacebook);
	List<Friend> friends = null;
	private SharedPreferences mPrefs;
	EditText searchEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.leave_message_activity);
		friends = new ArrayList<Friend>();

		// make the request for getting the friend list

		checkAcitveTokenForFacebook();

		final TextWatcher tw = new TextWatcher() {
			public void afterTextChanged(Editable s) {

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Log.i("virtualtreasure", "sending string " + s.toString());
				updateAdapter(s.toString());
			}
		};

		searchEditText = (EditText) findViewById(R.id.searchEdittText);
		searchEditText.addTextChangedListener(tw);
	}

	protected void updateAdapter(String keyWord) {
		// update the list view but in the background
		new filterFriends().execute(keyWord);
	}

	private void checkAcitveTokenForFacebook() {
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
					asyncRunner.request("me/friends",
							new FriendsRequestListener());

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
			asyncRunner.request("me/friends", new FriendsRequestListener());
		}

	}

	public void showFacebookFriendsClicked(View v) {

	}

	/**
	 * FriendsRequestListener implements a request lister/callback for
	 * "get friends" requests
	 */
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
				JSONArray d = json.getJSONArray("data");
				int l = (d != null ? d.length() : 0);
				Log.d("Facebook-Example-Friends Request", "d.length(): " + l);

				for (int i = 0; i < l; i++) {
					JSONObject o = d.getJSONObject(i);
					String n = o.getString("name");
					String id = o.getString("id");
					Friend f = new Friend();
					f.id = id;
					f.name = n;

					friends.add(f);
				}

				// Only the original owner thread can touch its views
				LeaveMessageActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						// FriendsArrayAdapter friendsArrayAdapter = new
						// FriendsArrayAdapter(
						// LeaveMessageActivity.this.getBaseContext(),
						// android.R.layout.simple_list_item_1, friends);
						ArrayAdapter aa = new ArrayAdapter<String>(
								getBaseContext(),
								android.R.layout.simple_list_item_1, // /
																		// Changed
																		// this
																		// now
																		// it
																		// shows
																		// fine
								getStringList(friends));
						setListAdapter(aa);
						aa.notifyDataSetChanged();
					}

					public List<String> getStringList(List<Friend> friends) {
						List<String> result = new ArrayList<String>();
						for (Friend i : friends) {
							result.add(i.name);
						}
						return result;
					}
				});
			} catch (JSONException e) {
				Log.w("Facebook-Example", "JSON Error in response");
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

	// Facebook method
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		mFacebook.authorizeCallback(requestCode, resultCode, data);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Friend f = friends.get(position);
		Intent i = new Intent(LeaveMessageActivity.this,
				SendGiftToFriendActivity.class);
		i.putExtra("friend-name", f.name);
		i.putExtra("friend-id", f.id);
		startActivity(i);
	}

	private class filterFriends extends AsyncTask<String, Void, Void> {

		List<String> resultList;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// initializations
			resultList = new ArrayList<String>();
		}

		@Override
		protected Void doInBackground(String... params) {

			if (params[0].equals("")) {
				resultList = getStringList(friends);
				Log.i("virtualtreasure", "empty list executed");
			} else {
				for (String friendList : getStringList(friends)) {
					// convert both to lower case to compare
					if (friendList.toLowerCase().contains(
							params[0].toLowerCase()))
						resultList.add(friendList);
				}
			}
			return null;
		}

		public List<String> getStringList(List<Friend> friends) {
			List<String> result = new ArrayList<String>();
			for (Friend i : friends) {
				result.add(i.name);
			}
			return result;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			ArrayAdapter aa = new ArrayAdapter<String>(getBaseContext(),
					android.R.layout.simple_list_item_1, // /
															// Changed
															// this
															// now
															// it
															// shows
															// fine
					resultList);
			setListAdapter(aa);
			aa.notifyDataSetChanged();
		}
	}

}
