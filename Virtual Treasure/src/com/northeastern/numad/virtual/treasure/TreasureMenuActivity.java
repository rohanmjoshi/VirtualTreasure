package com.northeastern.numad.virtual.treasure;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class TreasureMenuActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_menu);
       
    }

    public void checkMessageButtonClicked(View v) {
		Intent i = new Intent(this, CheckMessagesActivity.class);
		startActivity(i);
	}

	public void leaveMessageButtonClicked(View v) {
		Intent i = new Intent(this, LeaveMessageActivity.class);
		startActivity(i);
	}
	
	public void treasureGameClicked(View v) {
		Intent i = new Intent(this, ThingsToDoActivity.class);
		startActivity(i);
	}
	
}
