package com.northeastern.numad.virtual.treasure;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class CheckMessageMapActivity extends MapActivity implements
		LocationListener {

	LocationManager locationManager;
	Geocoder geocoder;
	MapView map;
	MapController mapController;
	Location location;
	CustomOverlays itemizedoverlay = null;

	private static final String TAG = "CheckMessageMapActivity";

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.check_message_map_activity);

		map = (MapView) this.findViewById(R.id.mapview);
		map.setBuiltInZoomControls(true);
		mapController = map.getController();
		mapController.setZoom(16);

		locationManager = (LocationManager) this
				.getSystemService(LOCATION_SERVICE);

		// listen for location updates using the gps provider, at a distance of
		// 1 meter and
		// after a time interval of 5 seconds
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				5000, 1, this);

		geocoder = new Geocoder(this);
		
		getUserLocation();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLocationChanged(Location loc) {
		location = loc;
		Log.i(TAG, "onLocationChanged with location " + location.toString());
		int latitude = (int) (location.getLatitude() * 1000000);
		int longitude = (int) (location.getLongitude() * 1000000);
		GeoPoint point = new GeoPoint(latitude, longitude);
		mapController.animateTo(point);

		// draw the current user location
		drawCurrentUserLocation();
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	// getUserLocation
	// tries to get the user location based on GPS provider
	// or the network provider
	public void getUserLocation() {
		location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (location != null) {
			Log.d(TAG, location.toString());
			this.onLocationChanged(location);
		} else {
			// the network provider
			location = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (location != null) {
				Log.d(TAG, location.toString());
				this.onLocationChanged(location);
			} else {
				Toast.makeText(
						this,
						"Cannot find your location, please try restarting your GPS",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	// draw the overlays at the points of interest on the given map
	public void drawCurrentUserLocation() {

		map.getOverlays().clear();
		Drawable drawable = this.getResources().getDrawable(R.drawable.orange);
		itemizedoverlay = new CustomOverlays(drawable);
		GeoPoint gp = null;

		// draw user current location
		gp = new GeoPoint((int) (location.getLatitude() * 1E6),
				(int) (location.getLongitude() * 1E6));
		OverlayItem overlayitem = new OverlayItem(gp, null, null);
		overlayitem.setMarker(drawable);
		itemizedoverlay.addOverlay(overlayitem);
		Log.i("adding central location ", gp.toString());
		map.getOverlays().add(itemizedoverlay);
		mapController.animateTo(gp);
	}

	private class CustomOverlays extends ItemizedOverlay<OverlayItem> {

		private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

		public CustomOverlays(Drawable defaultMarker) {
			super(boundCenterBottom(defaultMarker));
		}

		public void addOverlay(OverlayItem overlay) {
			mOverlays.add(overlay);
			populate();
		}

		@Override
		protected OverlayItem createItem(int i) {
			return mOverlays.get(i);

		}

		@Override
		public int size() {
			return mOverlays.size();

		}

	}
}
