package edu.vt.io;

import static com.googlecode.javacv.cpp.opencv_core.cvRect;

import com.googlecode.javacv.cpp.opencv_core.CvRect;

public class WindowInfo {
	
	// Size of parent image
	private int parentWidth = -1;
	private int parentHeight = -1;
	
	// Location of window in parent image
	private int parentXOffset = -1;
	private int parentYOffset = -1;
	
	private int width = -1;
	private int height = -1;
	
	// Amount of border
	private int borderTop = -1;
	private int borderBottom = -1;
	private int borderLeft = -1;
	private int borderRight = -1;
	
	public WindowInfo(){}
	
	public void setParentInfo(int parentXOffset, int parentYOffset, int parentHeight, int parentWidth){
		this.parentWidth = parentWidth;
		this.parentHeight = parentHeight;
		this.parentXOffset = parentXOffset;
		this.parentYOffset = parentYOffset;
	}
	
	public boolean isParentInfoValid(){
		if (parentWidth < 0 || parentHeight < 0 || parentXOffset < 0 || parentYOffset < 0){
			return false;
		}
		
		return true;
	}
	
	public void setBorder(int borderTop, int borderBottom, int borderLeft, int borderRight){
		this.borderTop = borderTop;
		this.borderBottom = borderBottom;
		this.borderLeft = borderLeft;
		this.borderRight = borderRight;
	}
	
	public boolean isBorderValid(){
		if (borderTop < 0 || borderBottom < 0 || borderLeft < 0 || borderRight < 0){
			return false;
		}
		
		return true;
	}
	
	public void setWindowSize(int height, int width){
		this.height = height;
		this.width = width;
	}
	
	public boolean isWindowSizeValid(){
		if (height < 0 || width < 0 ){
			return false;
		}
		
		return true;
	}
	
	public CvRect computeROI(){
		int newX = parentXOffset - borderLeft;
		int newY = parentYOffset - borderTop;
		int newWidth = width + borderLeft + borderRight;
		int newHeight = height + borderTop + borderBottom;
		return cvRect(newX, newY, newWidth, newHeight);
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getParentWidth() {
		return parentWidth;
	}
	
	public int getParentHeight() {
		return parentHeight;
	}
	
	public int getParentXOffset() {
		return parentXOffset;
	}
	
	public int getParentYOffset() {
		return parentYOffset;
	}
	public int getBorderTop() {
		return borderTop;
	}

	public int getBorderBottom() {
		return borderBottom;
	}

	public int getBorderLeft() {
		return borderLeft;
	}

	public int getBorderRight() {
		return borderRight;
	}
}
