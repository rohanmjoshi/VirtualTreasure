package com.northeastern.numad.virtual.treasure;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class GameActivity extends MapActivity implements LocationListener {

	private static final String TAG = "LocationActivity";

	LocationManager locationManager;
	Geocoder geocoder;
	MapView map;
	MapController mapController;
	Location location;
	String[] latlongs;
	ArrayList<Location> latLongList;
	CustomOverlays itemizedoverlay = null;
	private static final int DISTANCE = 2;
	private static final double DCONSTANT = 300;
	private Boolean areRandomPointsGenerated = false;
	private long startTime;
	private long timeSpent = 0;

	Facebook mFacebook = new Facebook("561901693827304");

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

		// listen for location updates using the gps provider, at a distance of
		// 1 meter and
		// after a time interval of 5 seconds
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				5000, 1, this);

		// get the current user time
		startTime = new Date().getTime();

		geocoder = new Geocoder(this);
		latLongList = new ArrayList<Location>();
		getUserLocation();

		shareOnFinishDialog("You finished the treasure hunt in "
				+ "\nWould you like to share your score on Facebook ?");
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
	public void onLocationChanged(Location loc) {
		location = loc;
		Log.i(TAG, "onLocationChanged with location " + location.toString());
		int latitude = (int) (location.getLatitude() * 1000000);
		int longitude = (int) (location.getLongitude() * 1000000);
		GeoPoint point = new GeoPoint(latitude, longitude);
		mapController.animateTo(point);

		if (!areRandomPointsGenerated) {
			generateRandomPoints();
			areRandomPointsGenerated = true;
		} else {
			// check for matching overlays.
			checkIfReachedOverlays();
			drawOverlays();
		}
	}

	public void checkIfReachedOverlays() {
		String locationLat = String.format("%.4f", location.getLatitude());
		String locationLong = String.format("%.4f", location.getLongitude());
		if (!latLongList.isEmpty()) {
			for (int i = 0; i < latLongList.size(); i++) {
				Double treasureLat = latLongList.get(i).getLatitude();
				Double treasureLong = latLongList.get(i).getLongitude();
				String sTreasureLat = String.format("%.4f", treasureLat);
				String sTreasureLong = String.format("%.4f", treasureLong);
				if (locationLat.equals(sTreasureLat)
						&& locationLong.equals(sTreasureLong)) {
					// user has found treasure
					// remove the treasure point
					latLongList.remove(i);
					Log.i(TAG, "removing the treasure" + latLongList);

					if (!latLongList.isEmpty()) {
						Toast.makeText(this, "good keep going!",
								Toast.LENGTH_LONG).show();

						// calculate the time;
						long currentTime = new Date().getTime();
						timeSpent += (currentTime - startTime);
						startTime = currentTime;
					} else {
						// user has found all the treasure points

						// convert milliseconds to seconds
						timeSpent /= 1000;

						// convert timeSpent to hour:minute:second format
						String timeToShow = String.format("%02d:%02d:%02d",
								timeSpent / 3600, (timeSpent % 3600) / 60,
								(timeSpent % 60));
						Log.i(TAG, timeToShow);
						// Toast.makeText(
						// this,
						// "You finished the treasure hunt in "
						// + timeToShow, Toast.LENGTH_LONG).show();

						shareOnFinishDialog("You finished the treasure hunt in "
								+ timeToShow
								+ "\nWould you like to share your score on Facebook ?");
					}
				}
			}
		}
	}

	public void shareOnFinishDialog(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
		// Add the buttons
		builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				showFacebookDialog("");
			}
		});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});

		builder.setMessage(message);
		// Create the AlertDialog
		AlertDialog dialog = builder.create();
		dialog.show();
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
	public void drawCurrentUserLocation() {
		// List<Overlay> mapOverlays = map.getOverlays();
		// mapOverlays.clear();
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

	// draw the overlays at the points of interest on the given map
	public void drawOverlays() {

		// draw other overlays
		// List<Overlay> mapOverlays = map.getOverlays();

		// clear all the previous overlays.
		map.getOverlays().clear();
		Drawable drawable = this.getResources().getDrawable(R.drawable.green);
		GeoPoint gp = null;
		itemizedoverlay = new CustomOverlays(drawable);
		for (int i = 0; i < latLongList.size(); i++) {
			gp = new GeoPoint((int) (latLongList.get(i).getLatitude() * 1E6),
					(int) (latLongList.get(i).getLongitude() * 1E6));
			OverlayItem overlayitem = new OverlayItem(gp, null, null);
			overlayitem.setMarker(drawable);
			itemizedoverlay.addOverlay(overlayitem);
			Log.i("adding overlayitem ", gp.toString());
		}

		map.getOverlays().add(itemizedoverlay);
		drawCurrentUserLocation();
		// map.postInvalidate();
	}

	public void generateRandomPoints() {
		Log.i(TAG, "generate random points called");
		// generate 4 random points near the user
		if (location != null) {
			double lat = location.getLatitude();
			double lon = location.getLongitude();
			double tempLon;
			double tempLat;
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
					loc.setLongitude(tempLon);
					latLongList.add(loc);
					break;
				case 1:
					// east
					tempInt = r.nextInt(DISTANCE) + 1;
					tempLon = lon;
					tempLat = lat + (tempInt / DCONSTANT);
					loc = new Location("");
					loc.setLatitude(tempLat);
					loc.setLongitude(tempLon);
					latLongList.add(loc);
					break;
				case 2:
					// south
					tempInt = r.nextInt(DISTANCE) + 1;
					tempLon = lon + (tempInt / DCONSTANT);
					tempLat = lat;
					loc = new Location("");
					loc.setLatitude(tempLat);
					loc.setLongitude(tempLon);
					latLongList.add(loc);
					break;
				case 3:
					// west
					tempInt = r.nextInt(DISTANCE) + 1;
					tempLon = lon;
					tempLat = lat - (tempInt / DCONSTANT);
					loc = new Location("");
					loc.setLatitude(tempLat);
					loc.setLongitude(tempLon);
					latLongList.add(loc);
					break;
				}
			}

			// draw the random points as well
			drawOverlays();

		}
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

	public void showFacebookDialog(String message) {
		// post on user's wall.
		// mFacebook.authorize(this, new PostDialogListener());

		Bundle parameters = new Bundle();
		//parameters.putString("message", "Some message");// the message to post
		parameters.putString("app_id", "561901693827304");
		parameters.putString("caption", "some caption");
		parameters.putString("description", "some description");
														// to the wall
		mFacebook.dialog(GameActivity.this, "feed", parameters,
				new PostDialogListener());// "stream.publish" is an API call
		// mFacebook.dialog(this, "feed", new PostDialogListener());
	}

	private class PostDialogListener implements DialogListener {

		@Override
		public void onComplete(Bundle values) {

		}

		@Override
		public void onFacebookError(FacebookError e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onError(DialogError e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub

		}

	}
}
