package com.manuh.detector.tracking;

import java.util.Scanner;

import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;
import static com.googlecode.javacv.cpp.opencv_video.*;



public class FaceTracking {
	private static final String OUTPUT_DIR = "";
	private static final String OUTPUT_FILE_NAME ="" ;
	private static final String OUTPUT_FILE_TYPE =".avi" ;
	private static final int FOURCC = CV_FOURCC('X', 'V', 'I', 'D');
	private static final int MAX_CORNERS = 500;
	private static final int WINDOW_SIZE = 10;
	private static OpenCVFrameGrabber grabber;
	private static IplImage currentFrame;
	private static IplImage[] frameBuffer;
	private static boolean tracking = false;
	private static int cl;
	private static String srcvid = "video/Test1.mpg";
	private static String[] classifierName = {
			"/home/hadoop/hadoop-computer-vision-read-only/hadoop-computer-vision/classifiers/haarcascade_lowerbody.xml",
			"/home/hadoop/hadoop-computer-vision-read-only/hadoop-computer-vision/classifiers/haarcascade_upperbody.xml",
			"/home/hadoop/hadoop-computer-vision-read-only/hadoop-computer-vision/classifiers/haarcascade_fullbody.xml"};

	private static boolean queryFrame() throws Exception {
	 try {	
		IplImage frame = grabber.grab();

		if (frame != null) {
			cvConvertImage(frame, currentFrame, 0);

			IplImage temp = frameBuffer[0];
			frameBuffer[0] = frameBuffer[1];
			frameBuffer[1] = temp;
			cvConvertImage(frame, frameBuffer[0], 0);

			return true;
		} else {
			return false;
		}
	 } catch(Exception e) {
		e.printStackTrace();
		 return false;
	 }
	}
 private static String outfilename(String classifier,String vidIn) {
	 String filename = "";
	 String prefix = vidIn.substring(vidIn.lastIndexOf("/")+1,vidIn.lastIndexOf("."))+"_";
	 if(classifier.contains("lower")) {
		 filename="lowerbody";
	 }
	 else if(classifier.contains("upper")) {
		 filename="upperbody";
	 }
	 else if(classifier.contains("full")) {
		 filename="fullbody";
	 }
	 return prefix+filename+OUTPUT_FILE_TYPE;
 }
	public static void main(String[] args) throws Exception {
		Scanner in = new Scanner(System.in); 
		System.out.println("1.Haarcascade Lowerbody");
		System.out.println("2.Haarcascade Upperbody");
		System.out.println("3.Haarcascade Fullbody");
		System.out.println("9.Above all");
		System.out.println("0.Exit");
		System.out.println("Choose the classifier :");
		cl=in.nextInt();
		System.out.println("Do you want to enable Tracker [y/n] :");
		tracking =in.next().equalsIgnoreCase("y") ? true : false;
		if(cl==0) {
			System.exit(1);
		}
		else if(cl==9) {
		for(int k=1;k<=classifierName.length;k++)  {
			   process(k);
			}
		}
		else if(cl>0 && cl < 4) {
			process(cl);
		}
		else {
			System.out.println("Wrong classifier..Try again");
		}
	}
	
 public static void process(int clfr)  throws Exception {
	 System.out.println("Video rendering started...");
		CvHaarClassifierCascade classifier = new CvHaarClassifierCascade(cvLoad(classifierName[clfr-1]));
		if (classifier.isNull()) {
			System.err.println("Error loading classifier file :"+classifierName[clfr-1]);
		}

		// Grab the video file
		grabber = new OpenCVFrameGrabber(srcvid);
		grabber.start();
		CvSize frameSize = new CvSize(grabber.getImageWidth(),grabber.getImageHeight());
		currentFrame = cvCreateImage(frameSize, IPL_DEPTH_8U, 3);
		
		CanvasFrame canvasFrame = new CanvasFrame("Test");
		canvasFrame.setCanvasSize(grabber.getImageWidth(),grabber.getImageHeight());
		
		/*OpenCVFrameRecorder recorder = new OpenCVFrameRecorder(OUTPUT_DIR+outfilename(classifierName[clfr-1],srcvid), frameSize.width(),
		frameSize.height()); 
		recorder.setVideoCodec(FOURCC);
		recorder.setFrameRate(grabber.getFrameRate()); 
		recorder.start();*/
		 
		 CvSeq faces = null;
		 CvMemStorage storage = CvMemStorage.create();
		 IplImage[] pyramidImages = new IplImage[2];
			frameBuffer = new IplImage[2];

			for (int i = 0; i < 2; i++) {
				frameBuffer[i] = cvCreateImage(frameSize, IPL_DEPTH_8U, 1);
				pyramidImages[i] = cvCreateImage(frameSize, IPL_DEPTH_32F, 1);
			}
			
		while (queryFrame()) {
			cvClearMemStorage(storage);
			faces = cvHaarDetectObjects(currentFrame, classifier, storage, 1.1,	3, CV_HAAR_DO_CANNY_PRUNING);
			int total = faces.total();
			IplImage mask = cvCreateImage(cvSize(currentFrame.width(), currentFrame.height()),IPL_DEPTH_8U, 1);
			for (int j = 0; j < total; j++) {
				CvRect r = new CvRect(cvGetSeqElem(faces, j));
				int x = r.x(), y = r.y(), w = r.width(), h = r.height();
				cvRectangle(currentFrame, cvPoint(x, y), cvPoint(x + w, y + h),	CvScalar.RED, 1, CV_AA, 0);
				cvRectangle(mask, cvPoint(x, y), cvPoint(x + w, y + h),CvScalar.WHITE, CV_FILLED, 8, 0);
			}
			if(tracking) {
				int[] cornerCount = { MAX_CORNERS };
				byte[] featuresFound = new byte[MAX_CORNERS];
				float[] featureErrors = new float[MAX_CORNERS];
				IplImage eigenImage = cvCreateImage(frameSize, IPL_DEPTH_32F, 1);
				IplImage tempImage = cvCreateImage(frameSize, IPL_DEPTH_32F, 1);

			CvPoint2D32f corners1 = new CvPoint2D32f(MAX_CORNERS);
			CvPoint2D32f corners2 = new CvPoint2D32f(MAX_CORNERS);
			// Corner finding with Shi and Thomasi
			cvGoodFeaturesToTrack(frameBuffer[0], eigenImage, tempImage,
					corners1, cornerCount, 0.2 // change the value to observe diff
					, 5.0, mask,3, 0, 0.4);

			cvFindCornerSubPix(frameBuffer[0], corners1, cornerCount[0],
					cvSize(WINDOW_SIZE, WINDOW_SIZE), cvSize(-1, -1),
					cvTermCriteria(CV_TERMCRIT_ITER | CV_TERMCRIT_EPS, 20, 0.3));

			// Pyramid Lucas-Kanade
			cvCalcOpticalFlowPyrLK(
					frameBuffer[0],
					frameBuffer[1],
					pyramidImages[0],
					pyramidImages[1],
					corners1,
					corners2,
					cornerCount[0],
					cvSize(WINDOW_SIZE, WINDOW_SIZE),
					5,  // change the value to observe diff
					featuresFound,
					featureErrors,
					cvTermCriteria(CV_TERMCRIT_ITER | CV_TERMCRIT_EPS, 20, 0.3),0);

			// Draw optical flow vectors
			for (int i = 0; i < cornerCount[0]; i++) {
				if (featuresFound[i] == 0 || featureErrors[i] > 550) {
					continue;
				}

				corners1.position(i);
				corners2.position(i);
				CvPoint center = cvPoint((Math.round(corners1.x())+Math.round(corners2.x()))/2,(Math.round(corners1.y())+Math.round(corners2.y()))/2);
				cvCircle(currentFrame, center, 8, CvScalar.GREEN, 1, 8, 0);
			 }
			} // if (tracking) close
			canvasFrame.showImage(currentFrame);
			//recorder.record(currentFrame);
		} // while close
		grabber.stop();
		//recorder.stop();
		canvasFrame.dispose();
		System.out.println("..................Done");
 }
}