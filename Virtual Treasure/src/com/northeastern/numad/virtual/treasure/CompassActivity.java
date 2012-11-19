package com.northeastern.numad.virtual.treasure;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
 
public class CompassActivity extends Activity implements SensorEventListener {
 
  Float azimut;  // View to draw a compass
 
  public class CustomDrawableView extends View {
    Paint paint = new Paint();
    public CustomDrawableView(Context context) {
      super(context);
      paint.setColor(0xff00ff00);
      paint.setStyle(Style.STROKE);
      paint.setStrokeWidth(2);
      paint.setAntiAlias(true);
    };
 
    @SuppressLint("DrawAllocation")
	protected void onDraw(Canvas canvas) {
      int width = getWidth();
      int height = getHeight();
      int centerx = width/2;
      int centery = height/2;
      Bitmap mBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.compass); 
      
      // Rotate the canvas with the azimut      
      if (azimut != null)
        canvas.rotate(-azimut*360/(2*3.14159f), centerx, centery);
      canvas.drawBitmap(mBitmap, centerx - 185, (float) (centery - 208.5), null);
      canvas.drawColor(android.R.color.white);
    }
  }
 
  CustomDrawableView mCustomDrawableView;
  private SensorManager mSensorManager;
  Sensor accelerometer;
  Sensor magnetometer;
 
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mCustomDrawableView = new CustomDrawableView(this);
    setContentView(mCustomDrawableView);    // Register the sensor listeners
    mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
      accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
  }
 
  protected void onResume() {
    super.onResume();
    mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
    mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
  }
 
  protected void onPause() {
    super.onPause();
    mSensorManager.unregisterListener(this);
  }
 
  public void onAccuracyChanged(Sensor sensor, int accuracy) {  }
 
  float[] mGravity;
  float[] mGeomagnetic;
  public void onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
      mGravity = event.values;
    if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
      mGeomagnetic = event.values;
    if (mGravity != null && mGeomagnetic != null) {
      float R[] = new float[9];
      float I[] = new float[9];
      boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
      if (success) {
        float orientation[] = new float[3];
        SensorManager.getOrientation(R, orientation);
        azimut = orientation[0]; // orientation contains: azimut, pitch and roll
      }
    }
    mCustomDrawableView.invalidate();
  }
}