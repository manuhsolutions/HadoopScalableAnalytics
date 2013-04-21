package com.manuh.facedection;


import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

public class MotionDetector {
    public static void main(String[] args) throws Exception {
    	// Grab the video file 
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber("obama.mp4");
        grabber.start();

        IplImage frame = grabber.grab();
        IplImage image = null;
        IplImage prevImage = null;
        IplImage diff = null;

        CanvasFrame canvasFrame = new CanvasFrame("Test");
        canvasFrame.setCanvasSize(frame.width(), frame.height());

        CvMemStorage storage = CvMemStorage.create();

        while (canvasFrame.isVisible() && (frame = grabber.grab()) != null) {
            //cvSmooth(frame, frame, CV_GAUSSIAN, 9, 9, 2, 2);
        	// validation for the first frame image second image onwards it goes to else part and first image converts to previous image
        	// and so on.
            if (image == null) {   
                image = IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1);
                cvCvtColor(frame, image, CV_BGR2GRAY);
            } else {
                prevImage = IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1);
                prevImage = image;
                image = IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1);
                cvCvtColor(frame, image, CV_BGR2GRAY);
            }

            if (diff == null) {
                diff = IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1);
            }

            if (prevImage != null) {
                // perform ABS difference
                cvAbsDiff(image, prevImage, diff);
                // do some threshold for wipe away useless details
                cvThreshold(diff, diff, 64, 255, CV_THRESH_BINARY);

                canvasFrame.showImage(diff);

                // recognize contours
                CvSeq contour = new CvSeq(null);
                cvFindContours(diff, storage, contour, Loader.sizeof(CvContour.class), CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);

                while (contour != null && !contour.isNull()) {
                    if (contour.elem_size() > 0) {
                        CvBox2D box = cvMinAreaRect2(contour, storage);
                        // test intersection
                        if (box != null) {
                            CvPoint2D32f center = box.center();
                            CvSize2D32f size = box.size();
                        }
                    }
                    contour = contour.h_next();
                    //Thread.sleep(10); // delay between each frame display
                }
            }
        }
        grabber.stop();
        canvasFrame.dispose();
    }
}
