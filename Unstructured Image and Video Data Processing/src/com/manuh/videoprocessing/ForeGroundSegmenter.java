package com.manuh.videoprocessing;

import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.*;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.cvConvertImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

public class ForeGroundSegmenter {

	private static OpenCVFrameGrabber grabber;
	private static String srcvid = "video/group2.avi";
	private static IplImage currentFrame;
	
	private static boolean queryFrame() throws Exception {
		try {
			IplImage frame = grabber.grab();

			if (frame != null) {
				cvConvertImage(frame, currentFrame, 0);
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println("Last frame reached");
			System.exit(1);
			return false;
		}
	}
	public static void main(String[] args) throws Exception {
			grabber = new OpenCVFrameGrabber(srcvid);
			grabber.start();
			CanvasFrame canvasFrame = new CanvasFrame("Foreground Segmenter");
			canvasFrame.setCanvasSize(grabber.getImageWidth(),grabber.getImageHeight());
			canvasFrame.setDefaultCloseOperation(canvasFrame.EXIT_ON_CLOSE );
			IplImage background = null;
			long delay = Math.round(1000d / grabber.getFrameRate());
			CvSize frameSize = new CvSize(grabber.getImageWidth(),grabber.getImageHeight());
			currentFrame = cvCreateImage(frameSize, IPL_DEPTH_8U, 3);
			IplImage output = cvCreateImage(cvGetSize(currentFrame), IPL_DEPTH_8U,1);
			IplImage backImage = cvCreateImage(cvGetSize(currentFrame),IPL_DEPTH_8U, 1);
			IplImage foreground = cvCreateImage(cvGetSize(currentFrame),IPL_DEPTH_8U, 1);
			IplImage gray = cvCreateImage(cvGetSize(currentFrame), IPL_DEPTH_8U, 1);
			
			while (queryFrame()) {
				cvCvtColor(currentFrame, gray, CV_BGR2GRAY);
				if (background == null) {
					background = cvCreateImage(cvGetSize(currentFrame), IPL_DEPTH_32F,1);
					cvConvert(gray, background);
				}
				cvConvert(background, backImage);
				cvAbsDiff(backImage, gray, foreground);
				cvThreshold(foreground, output, 32, 255, CV_THRESH_BINARY_INV);
				cvRunningAvg(gray, background, 0.01, output);
				canvasFrame.showImage(output);
				Thread.sleep(delay);
			}
			grabber.stop();
			canvasFrame.dispose();
	}
}
