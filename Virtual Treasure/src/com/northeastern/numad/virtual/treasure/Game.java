package com.northeastern.numad.virtual.treasure;

import java.util.ArrayList;
import java.util.List;

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
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class Game extends MapActivity implements LocationListener {

	private static final String TAG = "LocationActivity";

	LocationManager locationManager;
	Geocoder geocoder;
	MapView map;
	MapController mapController;
	Location location;
	String[] latlongs;
	ArrayList<String> latLongList;

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);
		map = (MapView) this.findViewById(R.id.mapview);
		map.setBuiltInZoomControls(true);
		mapController = map.getController();
		mapController.setZoom(16);

		locationManager = (LocationManager) this
				.getSystemService(LOCATION_SERVICE);

		geocoder = new Geocoder(this);

		getUserLocation();

		latLongList = new ArrayList<String>();

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
			addLocation();
		} else {
			// the network provider
			location = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (location != null) {
				Log.d(TAG, location.toString());
				this.onLocationChanged(location);
				addLocation();
			} else {
				Toast.makeText(
						this,
						"Cannot find your location, please try restarting your GPS",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public void addLocation() {
	}

	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1000, 10, this);
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d(TAG, "onLocationChanged with location " + location.toString());
		String text = String.format(
				"Lat:\t %f\nLong:\t %f\nAlt:\t %f\nBearing:\t %f",
				location.getLatitude(), location.getLongitude(),
				location.getAltitude(), location.getBearing());

		int latitude = (int) (location.getLatitude() * 1000000);
		int longitude = (int) (location.getLongitude() * 1000000);
		GeoPoint point = new GeoPoint(latitude, longitude);
		mapController.animateTo(point);

		drawOverlays();
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

	// draw the overlays at the points of interest on the given map
	public void drawOverlays() {
		List<Overlay> mapOverlays = map.getOverlays();
		mapOverlays.clear();
		Drawable drawable = this.getResources().getDrawable(
				android.R.drawable.btn_radio);
		CustomOverlays itemizedoverlay = new CustomOverlays(drawable);
		GeoPoint gp = null;
		/*
		 * for(int i =0;i<latlongs.length;i=i+2){ float lat =
		 * Float.parseFloat(latlongs[i]); float lng =
		 * Float.parseFloat(latlongs[i+1]); gp = new
		 * GeoPoint((int)(lat*1E6),(int)(lng*1E6)); OverlayItem overlayitem =
		 * new OverlayItem(gp, null, null); overlayitem.setMarker(drawable);
		 * itemizedoverlay.addOverlay(overlayitem);
		 * Log.i("adding overlayitem ",gp.toString()); }
		 */

		gp = new GeoPoint((int) (location.getLatitude() * 1E6),
				(int) (location.getLongitude() * 1E6));
		OverlayItem overlayitem = new OverlayItem(gp, null, null);
		overlayitem.setMarker(drawable);
		itemizedoverlay.addOverlay(overlayitem);
		Log.i("adding overlayitem ", gp.toString());
		mapOverlays.add(itemizedoverlay);
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
