package edu.vt.input;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import com.googlecode.javacpp.BytePointer;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import edu.vt.io.Image;
import edu.vt.io.WindowInfo;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

public class VidReader extends RecordReader<Text, Image> {

	private static final Log LOG = LogFactory.getLog(VidReader.class);
	
	// Image information
	private String fileName = null;
	private Image image = null;
	
	// Key/Value pair
	private Text key = null;
	private Image value = null;
	
	// Configuration parameters
	// By default use percentage for splitting
	boolean byPixel = false;
	int sizePercent = 0;
	int sizePixel = 0;
	int borderPixel = 0;
	int iscolor = -1;
	  
	// splits based on configuration parameters
	int totalXSplits = 0;
	int totalYSplits = 0;
	int xSplitPixels = 0;
	int ySplitPixels = 0;
	
	// Current split
	int currentSplit = 0;
	
	@Override
	public void close() throws IOException {

	}

	@Override
	public Text getCurrentKey() throws IOException, InterruptedException {

		return key;
	}

	@Override
	public Image getCurrentValue() throws IOException, InterruptedException {

		return value;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {

		return (float)(totalXSplits * totalYSplits) / (float)currentSplit;
	}

	@Override
	public void initialize(InputSplit genericSplit, TaskAttemptContext context)
			throws IOException, InterruptedException {
		// Get file split
		FileSplit split = (FileSplit) genericSplit;
		Configuration conf = context.getConfiguration();
		IplImage currentFrame;
		// Read configuration parameters
		getConfig(conf);
		
		// Open the file
		Path file = split.getPath();
		//FileSystem fs = file.getFileSystem(conf);
		//FSDataInputStream fileIn = fs.open(split.getPath());
		
		// Read file and decode image
		//byte [] b = new byte[fileIn.available()];
		//fileIn.readFully(b);
		CvMemStorage storage = CvMemStorage.create();
		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(file.toUri().getRawPath());
		try {
		grabber.start();
		CvSize frameSize = new CvSize(grabber.getImageWidth(),grabber.getImageHeight());
		currentFrame = cvCreateImage(frameSize, IPL_DEPTH_8U, 3);
		
		int len = grabber.getLengthInFrames();
		for(int i=0;i<len;i++) {
			cvClearMemStorage(storage);
			currentFrame = grabber.grab();
		}
		
		//image = new Image(cvDecodeImage(cvMat(1, b.length, CV_8UC1, new BytePointer(b)),iscolor)); 
		
		// Get filename to use as key
		fileName = split.getPath().getName().toString();
		
		// Calculate the number of splits
		//calculateSplitInfo();
		currentSplit = 0;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		if (currentSplit < (totalXSplits * totalYSplits) && fileName != null) {

			key = new Text(fileName);
			
			if(totalXSplits * totalYSplits == 1){
				value = image;
			}else{
				value = getSubWindow();
			}

			currentSplit += 1;
			return true;
		}

		return false;
	}
	
	private Image getSubWindow(){
		WindowInfo window = createWindow();
		CvRect roi = window.computeROI();
		 
		// sets the ROI
		IplImage img1 = image.getImage();
		cvSetImageROI(img1, roi);
		 
		// create destination image 
		IplImage img2 = cvCreateImage(cvSize(roi.width(), roi.height()), img1.depth(), img1.nChannels());
		 
		// copy sub-image
		cvCopy(img1, img2, null);
		 
		// reset the ROI
		cvResetImageROI(img1);
		
		return new Image(img2, window);
	}
	
	private void getConfig(Configuration conf){
		// Ensure that value is not negative
		borderPixel = conf.getInt("mapreduce.imagerecordreader.borderPixel", 0);
		if(borderPixel < 0){
			borderPixel = 0;
		}
		
		// Ensure that percentage is between 0 and 100
		sizePercent = conf.getInt("mapreduce.imagerecordreader.windowsizepercent", 100);
		if(sizePercent < 0 || sizePercent > 100){
			sizePercent = 100;
		}
		
		// Ensure that value is not negative
		sizePixel = conf.getInt("mapreduce.imagerecordreader.windowsizepixel", Integer.MAX_VALUE);
		if(sizePixel < 0){
			sizePixel = 0;
		}
		
		iscolor = conf.getInt("mapreduce.imagerecordreader.iscolor", -1);
		
		byPixel = conf.getBoolean("mapreduce.imagerecordreader.windowbypixel", false);
	}
	
	private WindowInfo createWindow(){
		WindowInfo window = new WindowInfo();
		
		// Get current window
		int x = currentSplit % totalXSplits;
		int y = currentSplit / totalYSplits;
		
		int width = xSplitPixels;
		int height = ySplitPixels;
		
		// Deal with partial windows
		if(x * xSplitPixels + width > image.getWidth()){
			width = image.getWidth() - x * xSplitPixels; 
		}
		if(y * ySplitPixels + height > image.getHeight()){
			height = image.getHeight() - y * ySplitPixels;
		}
		
		window.setParentInfo(x * xSplitPixels, y * ySplitPixels, image.getHeight(), image.getWidth());
		window.setWindowSize(height, width);
		
		// Calculate borders
		int top = 0;
		int bottom = 0;
		int left = 0;
		int right = 0;
		
		if(window.getParentXOffset() > borderPixel){
			left = borderPixel;
		}
		if(window.getParentYOffset() > borderPixel){
			top = borderPixel;
		}
		if(window.getParentXOffset() + borderPixel + window.getWidth() < window.getParentWidth()){
			right = borderPixel;
		}
		if(window.getParentYOffset() + borderPixel + window.getHeight() < window.getParentHeight()){
			bottom = borderPixel;
		}
		
		window.setBorder(top, bottom, left, right);
		return window;
	}
	
	private void calculateSplitInfo(){
		if(byPixel){
			xSplitPixels = sizePixel;
			ySplitPixels = sizePixel;
			totalXSplits = (int)Math.ceil(image.getWidth() / Math.min(xSplitPixels, image.getWidth()));
			totalYSplits = (int)Math.ceil(image.getHeight() / Math.min(ySplitPixels, image.getHeight()));
		}else{
			xSplitPixels = (int)(image.getWidth() * (sizePercent / 100.0));
			ySplitPixels = (int)(image.getHeight() * (sizePercent / 100.0));
			totalXSplits = (int)Math.ceil(image.getWidth() / (double)Math.min(xSplitPixels, image.getWidth()));
			totalYSplits = (int)Math.ceil(image.getHeight() / (double)Math.min(ySplitPixels, image.getHeight()));
		}
	}
}
