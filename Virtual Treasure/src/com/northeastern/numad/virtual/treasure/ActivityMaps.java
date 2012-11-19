
/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.northeastern.numad.virtual.treasure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class ActivityMaps extends MapActivity implements LocationListener, OnTreasureShowListener{

	private ImageButton ibCompass;
	private ImageButton ibCurrentLocation;
	private MapView mMapView;
	private MapsItemizedOverlay mCurrentLocationOverlay;
	private Location mCurrentLocation;
	private Location location;
	private static final int DISTANCE = 2;
	private static final double DCONSTANT = 300;
	
	public static ArrayList<TreasureData> mTreasureDataList;
	
	private LocationManager mLocationManager;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_maps);
	    
	    mMapView = (MapView) findViewById(R.id.mapview);
	    ibCompass = (ImageButton) findViewById(R.id.ibCompass);
	    ibCurrentLocation = (ImageButton) findViewById(R.id.ibCurrentLocation);
	    
	    mMapView.setBuiltInZoomControls(true);
	    
	    List<Overlay> mapOverlays = mMapView.getOverlays();
	    Drawable drawable = this.getResources().getDrawable(R.drawable.img_happy);
	    drawable.setBounds(0, 0, 50, 50);
	    MapsItemizedOverlay itemizedoverlay = new MapsItemizedOverlay(drawable, this);
	    
	    // Populate fake data
	    mTreasureDataList = new ArrayList<TreasureData>();
	    Location loc = new Location ("");
	      
	    double lat = 0;
		double lon = 0;
		double tempLon;
		double tempLat;
		String locLat;
		String locLon;
		Random r = new Random();
		for (int i = 0; i < 4; i++) {
			switch (i) {
			case 0:
				// north
				
				int tempInt = r.nextInt(DISTANCE) + 1;
				tempLon = lon - (tempInt / DCONSTANT);
				tempLat = lat;
				loc = new Location("");
				loc.setLatitude(tempLat);
				String g = "lat="+ loc.getLatitude() +"&lon="+ loc.getLongitude() +"&http://www.facebook.com";
				//locLat = loc.getLatitude();
				loc.setLongitude(tempLon);
				mTreasureDataList.add(new TreasureData(g, ActivityMaps.this));
				break;
			case 1:
				// east
				tempInt = r.nextInt(DISTANCE) + 1;
				tempLon = lon;
				tempLat = lat + (tempInt / DCONSTANT);
				loc = new Location("");
				loc.setLatitude(tempLat);
				loc.setLongitude(tempLon);
				mTreasureDataList.add(new TreasureData("lat="+ loc.getLatitude() +"&lon="+ loc.getLongitude() +"&http://www.facebook.com", this));
				break;
			case 2:
				// south
				tempInt = r.nextInt(DISTANCE) + 1;
				tempLon = lon + (tempInt / DCONSTANT);
				tempLat = lat;
				loc = new Location("");
				loc.setLatitude(tempLat);
				loc.setLongitude(tempLon);
				mTreasureDataList.add(new TreasureData("lat="+ loc.getLatitude() +"&lon="+ loc.getLongitude() +"&http://www.facebook.com", this));
				break;
			case 3:
				// west
				tempInt = r.nextInt(DISTANCE) + 1;
				tempLon = lon;
				tempLat = lat - (tempInt / DCONSTANT);
				loc = new Location("");
				loc.setLatitude(tempLat);
				loc.setLongitude(tempLon);
				mTreasureDataList.add(new TreasureData("lat="+ loc.getLatitude() +"&lon="+ loc.getLongitude() +"&http://www.facebook.com", this));
				break;
			}
		}
	    
		
		//mTreasureDataList.add(new TreasureData(g, ActivityMaps.this));
	    //generateRandomPoints();
	    
	    
	    
	    // Boston Locations
//        mTreasureDataList.add(new TreasureData("lat=42.345192&lon=-71.08597&facebook//aman124@gmail.com", this));
//        mTreasureDataList.add(new TreasureData("lat=42.341195&lon=-71.084425&twitter//@aman124", this));
//        mTreasureDataList.add(new TreasureData("lat=42.343225&lon=-71.09489&twitter//@random", this));
//        mTreasureDataList.add(new TreasureData("lat=42.338467&lon=-71.102099&http://www.apple.com", this));
//        mTreasureDataList.add(new TreasureData("lat=42.327332&lon=-71.090298&http://www.google.com", this));
//        mTreasureDataList.add(new TreasureData("lat=42.330948&lon=-71.104374&facebook//aman124@gmail.com", this));
//        mTreasureDataList.add(new TreasureData("lat=42.338721&lon=-71.084161&twitter//@aman124", this));
//        mTreasureDataList.add(new TreasureData("lat=42.335453&lon=-71.094546&http://www.android.com", this));
//        mTreasureDataList.add(new TreasureData("lat=42.328601&lon=-71.099396&facebook//random@gmail.com", this));
//        mTreasureDataList.add(new TreasureData("lat=42.333423&lon=-71.097686&http://www.facebook.com", this));
//        
//        // Seattle Locations
//        mTreasureDataList.add(new TreasureData("lat=47.620657&lon=-122.18946&http://www.microsoft.com", this));
//        mTreasureDataList.add(new TreasureData("lat=47.620758&lon=-122.184589&http://www.microsoft.com", this));
//        mTreasureDataList.add(new TreasureData("lat=47.618922&lon=-122.182562&http://www.microsoft.com", this));
//        mTreasureDataList.add(new TreasureData("lat=47.62274&lon=-122.193054&http://www.microsoft.com", this));
        
        
	    
        for (TreasureData treasureData : mTreasureDataList) {
        	
        	GeoPoint point = new GeoPoint((int) (treasureData.getLatitude() * 1000000), (int) (treasureData.getLongitude() * 1000000));
        	OverlayItem overlayitem = new OverlayItem(point, treasureData.getMessage(), treasureData.getMessage());
        	
        	Drawable icon = getResources().getDrawable(treasureData.GetDrawableId());
        	icon.setBounds(0, 0, 50, 50);
        	overlayitem.setMarker(icon);
        	
        	itemizedoverlay.AddOverlay(overlayitem);
		} 
        
	    mapOverlays.add(itemizedoverlay);
	    
	    ibCompass.setOnClickListener(new OnClickListener() {
			
			
			public void onClick(View v) {
				Intent myIntent = new Intent(ActivityMaps.this, ActivityCamera.class);
				ActivityMaps.this.startActivity(myIntent);
			}
		});
	    
	    ibCurrentLocation.setOnClickListener(new OnClickListener() {
			
			
			public void onClick(View v) {
				ZoomToLocation(mCurrentLocation);
			}
		});
	    
	    // Get current location
	    mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
	}
	
	@Override
    protected void onResume() {
        super.onResume();

        // Register the listener with the Location Manager to receive location updates
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        
        // Get LKG location
        LocationManager locationManager = (LocationManager) this
				.getSystemService(LOCATION_SERVICE);
        Location lkgLocation = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (lkgLocation != null) {
			Log.d("GPS Location", lkgLocation.toString());
			this.onLocationChanged(lkgLocation);
		} 
		else {
			// Check for the network provider
			lkgLocation = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (lkgLocation != null) {
				Log.d("Network Location", lkgLocation.toString());
				this.onLocationChanged(lkgLocation);
			} 
			else {
				// Do nothing
			}
		}
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister GPS sensor
        mLocationManager.removeUpdates(this);
    }
    
    private void ZoomToLocation(Location location)
    {
    	// Remove previous location overlay
		this.mMapView.getOverlays().remove(mCurrentLocationOverlay);
		
		// Create current location overlay
	    Drawable drawableCurrentLocation = getResources().getDrawable(R.drawable.ic_maps_indicator_current_position);
		mCurrentLocationOverlay = new MapsItemizedOverlay(drawableCurrentLocation, this);
	    
		GeoPoint currentLocation = new GeoPoint((int )(location.getLatitude() * 1000000), (int )(location.getLongitude() * 1000000));
		OverlayItem currentLocationOverlay = new OverlayItem(currentLocation, "", "");
		mCurrentLocationOverlay.AddOverlay(currentLocationOverlay);
		
    	this.mMapView.getController().animateTo(currentLocation);
        this.mMapView.getController().setZoom(14);
        this.mMapView.getOverlays().add(mCurrentLocationOverlay);
    }
	
    
	public void onLocationChanged(Location location) {
    	
    	if (location != null)
    	{
    		mCurrentLocation = location;
    		ZoomToLocation(location);
    	}
	}
	
	public void generateRandomPoints() {
		//Log.i(TAG, "generate random points called");
		// generate 4 random points near the user
		if (location != null) {
			double lat = location.getLatitude();
			double lon = location.getLongitude();
			double tempLon;
			double tempLat;
			String locLat;
			String locLon;
			Random r = new Random();
			for (int i = 0; i < 4; i++) {
				switch (i) {
				case 0:
					// north
					
					int tempInt = r.nextInt(DISTANCE) + 1;
					tempLon = lon - (tempInt / DCONSTANT);
					tempLat = lat;
					Location loc = new Location("");
					loc.setLatitude(tempLat);
					String g = "lat="+ loc.getLatitude() +"&lon="+ loc.getLongitude() +"&http://www.facebook.com";
					//locLat = loc.getLatitude();
					loc.setLongitude(tempLon);
					mTreasureDataList.add(new TreasureData(g, ActivityMaps.this));
					break;
				case 1:
					// east
					tempInt = r.nextInt(DISTANCE) + 1;
					tempLon = lon;
					tempLat = lat + (tempInt / DCONSTANT);
					loc = new Location("");
					loc.setLatitude(tempLat);
					loc.setLongitude(tempLon);
					mTreasureDataList.add(new TreasureData("lat="+ loc.getLatitude() +"&lon="+ loc.getLongitude() +"&http://www.facebook.com", this));
					break;
				case 2:
					// south
					tempInt = r.nextInt(DISTANCE) + 1;
					tempLon = lon + (tempInt / DCONSTANT);
					tempLat = lat;
					loc = new Location("");
					loc.setLatitude(tempLat);
					loc.setLongitude(tempLon);
					mTreasureDataList.add(new TreasureData("lat="+ loc.getLatitude() +"&lon="+ loc.getLongitude() +"&http://www.facebook.com", this));
					break;
				case 3:
					// west
					tempInt = r.nextInt(DISTANCE) + 1;
					tempLon = lon;
					tempLat = lat - (tempInt / DCONSTANT);
					loc = new Location("");
					loc.setLatitude(tempLat);
					loc.setLongitude(tempLon);
					mTreasureDataList.add(new TreasureData("lat="+ loc.getLatitude() +"&lon="+ loc.getLongitude() +"&http://www.facebook.com", this));
					break;
				}
			}

			

		}
	}
    
   
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}
    
	
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void OnTreasureShow(TreasureData data, double relativePosition) {
		// Do nothing
	}

	
	public void OnTreasureHide(TreasureData data) {
		// Do nothing
	}	
}