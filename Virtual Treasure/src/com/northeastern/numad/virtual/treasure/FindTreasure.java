package com.northeastern.numad.virtual.treasure;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class FindTreasure extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_treasure);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_find_treasure, menu);
        return true;
    }
}
