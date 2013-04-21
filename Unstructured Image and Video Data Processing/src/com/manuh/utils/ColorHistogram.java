package com.manuh.utils;

import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

public class ColorHistogram {
	public CvHistogram getHistogram(IplImage image){
	   if(image==null || image.nChannels()<3) new Exception("Error!");
	   IplImage hsvImage= cvCreateImage(image.cvSize(), image.depth(), 3);
	   cvCvtColor(image, hsvImage, CV_BGR2HSV);
	   // Split the 3 channels into 3 images
	   IplImageArray hsvChannels = splitChannels(hsvImage);
	   //bins and value-range
	   int numberOfBins=255;
	   float minRange= 0f;
	   float maxRange= 180f;
	   // Allocate histogram object
	   int dims = 1;
	   int[]sizes = new int[]{numberOfBins};
	   int histType = CV_HIST_ARRAY;
	   float[] minMax = new  float[]{minRange, maxRange};
	   float[][] ranges = new float[][]{minMax};
	   int uniform = 1;
	   CvHistogram hist = cvCreateHist(dims, sizes, histType, ranges, uniform);
	   // Compute histogram
	   int accumulate = 1;
	   IplImage l = null;
	   cvCalcHist(hsvChannels.position(0),hist, accumulate, null);
	   return hist;
	}
	
	private IplImageArray splitChannels(IplImage hsvImage) {
	    CvSize size = hsvImage.cvSize();
	    int depth=hsvImage.depth();
	    IplImage channel0 = cvCreateImage(size, depth, 1);
	    IplImage channel1 = cvCreateImage(size, depth, 1);
	    IplImage channel2 = cvCreateImage(size, depth, 1);
	    cvSplit(hsvImage, channel0, channel1, channel2, null);
	    return new IplImageArray(channel0, channel1, channel2);
	}
	
	public CvHistogram getHistogram(IplImage image,double minSaturation){
		
		if(image==null || image.nChannels()<3) new Exception("Error!");

	        // Convert RGB to HSV color space
		IplImage hsvImage= cvCreateImage(image.cvSize(), image.depth(), 3);
		 cvCvtColor(image, hsvImage, CV_BGR2HSV);

	        // Split the 3 channels into 3 images
		 IplImageArray hsvChannels = splitChannels(hsvImage);
		 IplImage saturationMask = null;
		 if (minSaturation > 0) {
			 saturationMask = cvCreateImage(cvGetSize(hsvImage), IPL_DEPTH_8U, 1);
			 //cvThreshold(hsvChannels.position(1), saturationMask, minSaturation, 255.00, CV_THRESH_BINARY);
		 }
	     //getHistogram(hsvChannels.position(0), saturationMask);
	    }
}
