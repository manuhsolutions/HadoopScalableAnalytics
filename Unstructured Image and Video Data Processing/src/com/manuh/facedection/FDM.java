package com.manuh.facedection;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.CV_FOURCC;
import static com.googlecode.javacv.cpp.opencv_highgui.cvConvertImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FrameRecorder;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.OpenCVFrameRecorder;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;


public class FDM {
	private static final String OUTPUT_FILE_NAME = "output.avi";
	private static final int FOURCC = CV_FOURCC('X', 'V', 'I', 'D');
	
	public static void main(String[] args) throws Exception {
		// Grab the video file
		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber("test.avi");
		//OpenCVFrameGrabber grabber = new OpenCVFrameGrabber("Walk.mpg");
		//OpenCVFrameGrabber grabber = new OpenCVFrameGrabber("obama.mp4");
		grabber.start();
		CvHaarClassifierCascade classifier = new CvHaarClassifierCascade(
				cvLoad("/home/hadoop/hadoop-computer-vision-read-only/hadoop-computer-vision/classifiers/haarcascade_upperbody.xml"));
		if (classifier.isNull()) {
			System.err.println("Error loading classifier file");
		}
		
		IplImage frame = grabber.grab();
		IplImage image = null;
		CvSeq faces = null;
		CanvasFrame canvasFrame = new CanvasFrame("Test");
		canvasFrame.setCanvasSize(frame.width(), frame.height());
		System.out.println("Video processing .........started");
		CvMemStorage storage = CvMemStorage.create();
		CvSize frameSize = new CvSize(grabber.getImageWidth(), grabber.getImageHeight());
		/*OpenCVFrameRecorder recorder = new OpenCVFrameRecorder(OUTPUT_FILE_NAME, frameSize.width(), frameSize.height());
		recorder.setVideoCodec(FOURCC);
		recorder.setFrameRate(grabber.getFrameRate());
		recorder.start();*/
		
		while (canvasFrame.isVisible() && (frame = grabber.grab()) != null) {
			//while ((frame = grabber.grab()) != null) {
			cvClearMemStorage(storage);
			image = cvCreateImage(frameSize, IPL_DEPTH_8U, 3);
			cvConvertImage(frame,image,0);
			faces = cvHaarDetectObjects(image, classifier, storage, 1.1, 3,CV_HAAR_DO_CANNY_PRUNING);
			int total = faces.total();
			for (int i = 0; i < total; i++) {
				CvRect r = new CvRect(cvGetSeqElem(faces, i));
					int x = r.x(), y = r.y(), w = r.width(), h = r.height();
					cvRectangle(image, cvPoint(x, y),cvPoint(x + w, y + h),CvScalar.RED, 3, CV_AA, 0);
			}
		
			canvasFrame.showImage(image);
			
			//recorder.record(image);
			//Thread.sleep(20);
			
		}
		grabber.stop();
		//recorder.stop();
		canvasFrame.dispose();
		System.out.println("...............Done");
	}
}
