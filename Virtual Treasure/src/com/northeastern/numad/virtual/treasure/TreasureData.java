package com.northeastern.numad.virtual.treasure;

import java.util.ArrayList;

import android.location.Location;
import android.util.Log;
import android.widget.ImageView;

public class TreasureData{
	
	public enum DataType
	{
		Facebook,
		Twitter,
		URL,
		Photo
	}
	
	private static double CAMERA_ANGLE_BY_2;
	private static int ID_COUNTER = 0;
	
	// Data Id
	private int mId = 0;
	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}

	// Latitude
	private double mLatitude;
	public double getLatitude() {
		return mLatitude;
	}

	public void setLatitude(double mLatitude) {
		this.mLatitude = mLatitude;
	}

	// Longitude
	private double mLongitude;
	public double getLongitude() {
		return mLongitude;
	}

	public void setLongitude(double mLongitude) {
		this.mLongitude = mLongitude;
	}

	// Data Type
	private DataType mType;
	public DataType getType() {
		return mType;
	}

	public void setType(DataType mType) {
		this.mType = mType;
	}
	
	// Message
	private String mMessage;
	public String getMessage() {
		return mMessage;
	}

	public void setMessage(String mMessage) {
		this.mMessage = mMessage;
	}
	
	// Distance from current location
	private double mDistance;

	public double getDistance() {
		return mDistance;
	}

	public void setDistance(double mDistance) {
		this.mDistance = mDistance;
	}

	private double mRequiredAngle;
	private double mOldDisplacementAngle = 0;
	
	private ArrayList<OnTreasureShowListener> mListenerList;
	
	private ImageView ivData;
	
	public ImageView getImageView() {
		return ivData;
	}

	public void setImageView(ImageView ivData) {
		this.ivData = ivData;
	}

	public static void UpdateCameraAngle(double angle)
	{
		CAMERA_ANGLE_BY_2 = angle / 2.0f;
	}
	
	private TreasureData()
	{
		ID_COUNTER++;
		mId = ID_COUNTER;
		
		mListenerList = new ArrayList<OnTreasureShowListener>();
	}
	
	public TreasureData(String data, OnTreasureShowListener listener)
	{
		// Call private constructor to assign id
		this();
		
		if (listener == null)
		{
			throw new IllegalArgumentException("TreasureShowlistener cannot be null");
		}

		String[] splitData = data.split("&");
		
		for (int counter = 0; counter < splitData.length; counter++)
		{
			if (splitData[counter].startsWith("lat"))
			{
				this.mLatitude = Double.parseDouble(splitData[counter].split("=")[1]);
			}
			else if (splitData[counter].startsWith("lon"))
			{
				this.mLongitude = Double.parseDouble(splitData[counter].split("=")[1]);
			}
			else
			{
				String[] dataMessage = splitData[counter].split("//");
				
				if (dataMessage.length >= 2)
				{
					if (dataMessage[0].startsWith("facebook"))
					{
						this.mType = DataType.Facebook;
					}
					else if (dataMessage[0].startsWith("twitter"))
					{
						this.mType = DataType.Twitter;
					} 
					else if (dataMessage[0].startsWith("http"))
					{
						this.mType = DataType.URL;
					}
					else if (dataMessage[0].startsWith("photo"))
					{
						this.mType = DataType.Photo;
					}
					
					this.mMessage = dataMessage[1];
				}
			}
		}
		
		mRequiredAngle = 0.0f;
		
		// Update listener
		mListenerList.add(listener);
	}
	
	public void UpdateCurrentLocation(Location fromLocation)
	{
		Location toLocation = new Location(fromLocation);
		toLocation.setLatitude(mLatitude);
		toLocation.setLongitude(mLongitude);
		
		mRequiredAngle = fromLocation.bearingTo(toLocation);
		mDistance = fromLocation.distanceTo(toLocation);
		
		// If angle is less than 0,
		// add 360 degrees to it to make it positive
		// e.g. -90 would be converted to 270, which is same
		if (mRequiredAngle < 0)
		{
			mRequiredAngle += 360;
		}
	}
	
	public void UpdateCurrentViewAngle(double currentAngle)
	{
		double displacementAngle;
		
		displacementAngle = (currentAngle - mRequiredAngle) / CAMERA_ANGLE_BY_2;
		
		if ( (displacementAngle <  1.0f) && (displacementAngle > (-1.0f)) )
		{
			// Log.d("Virtual Treasure","Show Object with id # " + mId);
			
			// Check if displacement angle is calculated first time
			if (mOldDisplacementAngle == 0)
			{
				mOldDisplacementAngle = displacementAngle;
				for(OnTreasureShowListener listener: mListenerList)
				{
					listener.OnTreasureShow(this, displacementAngle);
				}
			}
			else
			{
				// Else compare new displacement angle with old one
				// Update if new angle is NOT near
				if (Math.abs(mOldDisplacementAngle - displacementAngle) > 0.02f)
				{
					// Update if big change
					mOldDisplacementAngle = displacementAngle;
					for(OnTreasureShowListener listener: mListenerList)
					{
						listener.OnTreasureShow(this, displacementAngle);
					}
				}
				else
				{
					Log.d("Virtual Treasure","No Change");		
				}
			}
		}
		else
		{
			// Log.d("Virtual Treasure","Hide Object with id # " + mId);
			for(OnTreasureShowListener listener: mListenerList)
			{
				listener.OnTreasureHide(this);
			}
			
			mOldDisplacementAngle = 0;
		}
	} 
	
	public int GetDrawableId()
	{
		if (this.mType== DataType.Facebook)
		{
			return R.drawable.img_facebook;
		}
		else if (this.mType == DataType.Twitter)
		{
			return R.drawable.img_twitter;
		} 
		else if (this.mType == DataType.URL)
		{
			return R.drawable.img_http;
		}
		
		return R.drawable.img_happy;
	}
	
	public void addOnTreasureShowListener(OnTreasureShowListener listener)
	{
		this.mListenerList.add(listener);
	}
	
	public void removeOnTreasureShowListener(OnTreasureShowListener listener)
	{ 
		this.mListenerList.remove(listener);
	}
}