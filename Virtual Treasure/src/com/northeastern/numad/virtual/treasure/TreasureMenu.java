package com.northeastern.numad.virtual.treasure;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class TreasureMenu extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_treasure_menu, menu);
        return true;
    }
}
