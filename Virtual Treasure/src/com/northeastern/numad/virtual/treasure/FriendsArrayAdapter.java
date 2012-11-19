package com.northeastern.numad.virtual.treasure;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class FriendsArrayAdapter extends ArrayAdapter<Friend> {
	
	public FriendsArrayAdapter(Context context, int textViewResourceId,
			List<Friend> objects) {
		super(context, textViewResourceId, objects);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return super.getCount();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return super.getView(position, convertView, parent);
	}

	
}
