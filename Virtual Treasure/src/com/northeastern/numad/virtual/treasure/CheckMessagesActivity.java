package com.northeastern.numad.virtual.treasure;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class CheckMessagesActivity extends TabActivity {


	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.check_messages);

		TabHost tabHost = getTabHost();

		// Tab for Photos
		TabSpec listSpec = tabHost.newTabSpec("List");
		// setting Title and Icon for the Tab
		listSpec.setIndicator("List");
		Intent photosIntent = new Intent(this, ListCheckMessagesActivity.class);
		listSpec.setContent(photosIntent);

		// Tab for Map
		TabSpec mapSpec = tabHost.newTabSpec("Map");
		mapSpec.setIndicator("Map");
		Intent mapIntent = new Intent(this, CheckMessageMapActivity.class);
		mapSpec.setContent(mapIntent);

		tabHost.addTab(listSpec);
		tabHost.addTab(mapSpec);
	}

}
