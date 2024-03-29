

package com.northeastern.numad.virtual.treasure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


// ----------------------------------------------------------------------

public class ActivityCamera extends Activity implements SensorEventListener, LocationListener, OnTreasureShowListener{
    private Preview mPreview;
    private Camera mCamera;
    private int numberOfCameras;
    private double mRequiredAngle = 0;

    // Checks if any location coordinates are available or not
	private boolean mIsLKGKnown;
	
	private LocationManager mLocationManager;
	private SensorManager mSensorManager;
	private Sensor mOrientation;
    
	private ArrayList<TreasureData> mTreasureDataList;
    
    private TextView tv;
    private RelativeLayout rlTreasureData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
        // Enable if you want to switch to full-screen
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);
        
        tv = (TextView) findViewById(R.id.debugText);
        rlTreasureData = (RelativeLayout) findViewById(R.id.rlTreasure);
        // FrameLayout flMain = (FrameLayout) findViewById(R.id.flMain);
        FrameLayout flCamera = (FrameLayout) findViewById(R.id.flCamera);
        
        // Create a RelativeLayout container that will hold a SurfaceView,
        // and set it as the content of our activity.
        mPreview = new Preview(this);
        flCamera.addView(mPreview);
        //setContentView(mPreview);

        // Find the total number of cameras available
        numberOfCameras = Camera.getNumberOfCameras();

        // Find the ID of the default camera
        CameraInfo cameraInfo = new CameraInfo();
        
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
            }
        }
       
        // Enable location services
        // Acquire a reference to the system Location Manager
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        
        // Populate data
        mTreasureDataList = ActivityMaps.mTreasureDataList;   
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Assume the location to be unknown
        mIsLKGKnown = false;
        
        // Open the default i.e. the first rear facing camera.
        mCamera = Camera.open();
        mPreview.setCamera(mCamera);
        
    	double cameraHAngle = mCamera.getParameters().getHorizontalViewAngle();
        // mCameraHAngleBy2 = 45;
    	Toast.makeText(this, "Camera Angle : " + cameraHAngle, Toast.LENGTH_SHORT).show();
    	// Update camera angle
        TreasureData.UpdateCameraAngle(cameraHAngle);
        
        // Register the listener with the Location Manager to receive location updates
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        
        // Now request compass updates
        mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_NORMAL);
        
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
		
		// Add listener to listen to data show event
		for (TreasureData data: mTreasureDataList)
        {
        	data.addOnTreasureShowListener(this);
        }
        
		Log.d("Virtual Treasure", "Required Angle : " + mRequiredAngle);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Because the Camera object is a shared resource, it's very
        // important to release it when the activity is paused.
        if (mCamera != null) {
            mPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
        
        // Unregister GPS sensor
        mLocationManager.removeUpdates(this);
        
        // Unregister Orientation sensor
        mSensorManager.unregisterListener(this);
        
        // Unregister listeners for data show event
        for (TreasureData data: mTreasureDataList)
        {
        	data.removeOnTreasureShowListener(this);
        }
    }

	
	public void onLocationChanged(Location location) {

		// Update that some location is available
		mIsLKGKnown = true;
		
		// Send updated current location to each item
		// so that they can update their angle
		for (TreasureData treasureData : this.mTreasureDataList) {
			treasureData.UpdateCurrentLocation(location);
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

	
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	
	public void onSensorChanged(SensorEvent event) {
		
		double currentAngle = event.values[0];
		
		if (mIsLKGKnown)
		{
			currentAngle -=250;
			
			if (currentAngle < 0)
			{
				currentAngle += 360;
			}
			
			// Send current direction angle to each item
			// so that they can decide to show up or not. 
			// They would fire a callback if they decide to show up 
			// as camera overlay
			for (TreasureData treasureData : this.mTreasureDataList) {
				treasureData.UpdateCurrentViewAngle(currentAngle);
			}
		}		
	}

	
	public void OnTreasureShow(final TreasureData data, double relativePosition) {
		tv.setText("ID : " + data.getId());
		ImageView ivData;
		
		// Get ImageView
		ivData = data.getImageView();
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
		params.leftMargin = (int) ((rlTreasureData.getWidth() / 2) - (rlTreasureData.getWidth() / 2) * relativePosition);
		
		// Calculate vertical position
		// For all object with distance greater than 1000 kilometers,
		// show them on top of screen
		if (data.getDistance() > 1000.0f)
		{
			params.topMargin = 10;
		}
		else
		{
			params.topMargin = (int) (((rlTreasureData.getHeight() - 10) * (1 - (data.getDistance() / 1000.0f)) ) + 10);	
		}
		
		if (ivData == null)
		{
			// Create a new ImageView for this object
			ivData = new ImageView(getApplicationContext());
			
			ivData.setBackgroundDrawable(getResources().getDrawable(data.GetDrawableId()));

			ivData.setOnClickListener(new OnClickListener() {
				
				
				public void onClick(View arg0) {
					Toast.makeText(getApplicationContext(), data.getMessage(), Toast.LENGTH_SHORT).show();
				}
			});
			
			rlTreasureData.addView(ivData, params);	
		}
		else
		{
			// Remove from RelativeLayout and place on a new position
			rlTreasureData.removeView(ivData);
			rlTreasureData.addView(ivData, params);
		}
		
		data.setImageView(ivData);
	}
	
	
	public void OnTreasureHide(TreasureData data)
	{
		rlTreasureData.removeView(data.getImageView());
	}
}
    
/**
 * A simple wrapper around a Camera and a SurfaceView that renders a centered preview of the Camera
 * to the surface. We need to center the SurfaceView because not all devices have cameras that
 * support preview sizes at the same aspect ratio as the device's display.
 */
class Preview extends ViewGroup implements SurfaceHolder.Callback {
    private final String TAG = "Preview";

    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    Size mPreviewSize;
    List<Size> mSupportedPreviewSizes;
    Camera mCamera;

    Preview(Context context) {
        super(context);

        mSurfaceView = new SurfaceView(context);
        addView(mSurfaceView);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
        if (mCamera != null) {
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            requestLayout();
        }
    }

    public void switchCamera(Camera camera) {
       setCamera(camera);
       try {
           camera.setPreviewDisplay(mHolder);
       } catch (IOException exception) {
           Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
       }
       Camera.Parameters parameters = camera.getParameters();
       parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
       requestLayout();

       camera.setParameters(parameters);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);

            final int width = r - l;
            final int height = b - t;

            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.width;
                previewHeight = mPreviewSize.height;
            }

            // Center the child SurfaceView within the parent.
            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2,
                        width, (height + scaledChildHeight) / 2);
            }
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
            }
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }


    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        requestLayout();

        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }

}


/*package com.northeastern.numad.virtualtreasure;

import android.os.Bundle;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class ActivityCamera extends FragmentActivity {

	private MyAdapter mAdapter;
	private ViewPager mPager;
	 
    /** Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mAdapter = new MyAdapter(getSupportFragmentManager(), this.getApplicationContext());
 
        mPager = (ViewPager) findViewById(R.id.viewpager_main);
        mPager.setAdapter(mAdapter);
    }
 
    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    
    public static class MyAdapter extends FragmentPagerAdapter {
    	private static Context mContext = null;
        public MyAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
        }
 
        @Override
        public int getCount() {
            return 2;
        }
 
        @Override
        public Fragment getItem(int position) {
            switch (position) {
            case 0:
                return new FragmentCamera(mContext);
            case 1:
            	return new FragmentMaps();
            default:
                return null;
            }
        }
    }
	    
	/*
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_activity_maps, menu);
        return true;
    }
}
*/