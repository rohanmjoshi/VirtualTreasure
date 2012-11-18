package com.northeastern.numad.virtual.treasure;

public interface OnTreasureShowListener {
	public void OnTreasureShow(TreasureData data, double relativePosition);
	public void OnTreasureHide(TreasureData data);
}
