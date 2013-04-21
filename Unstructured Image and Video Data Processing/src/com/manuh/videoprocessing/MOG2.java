package com.manuh.videoprocessing;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;
import static com.googlecode.javacv.cpp.opencv_highgui.cvConvertImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_THRESH_BINARY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvThreshold;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.cpp.opencv_video.BackgroundSubtractorMOG2;

public class MOG2 {

	private static IplImage currentFrame;
	private static OpenCVFrameGrabber grabber;
	private static String srcvid = "test.avi";

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
		CvSize frameSize = new CvSize(grabber.getImageWidth(),
				grabber.getImageHeight());
		currentFrame = cvCreateImage(frameSize, IPL_DEPTH_8U, 3);

		CanvasFrame canvasFrame = new CanvasFrame("Extracted Foreground");
		canvasFrame.setCanvasSize(grabber.getImageWidth(),	grabber.getImageHeight());

		long delay = Math.round(1000d / grabber.getFrameRate());
		BackgroundSubtractorMOG2 mog = new BackgroundSubtractorMOG2(1,32,false);
		while (queryFrame()) {
			IplImage foreground = cvCreateImage(cvSize(currentFrame.width(), currentFrame.height()),IPL_DEPTH_8U, 1);
			mog.apply(currentFrame, foreground, 0.01);
			cvThreshold(foreground, foreground, 128, 255, CV_THRESH_BINARY);
			canvasFrame.showImage(foreground);
			Thread.sleep(delay);
		}
		grabber.stop();
	    canvasFrame.dispose();
	}
}
