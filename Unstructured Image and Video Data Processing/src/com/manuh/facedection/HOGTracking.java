package com.manuh.facedection;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;

public class HOGTracking {
    private static OpenCVFrameGrabber grabber;
    private static IplImage currentFrame;

    private static boolean queryFrame() throws Exception {
        IplImage frame = grabber.grab();
        

        if (frame != null) {
            cvConvertImage(frame, currentFrame, 0);
            return true;
        } else {
            return false;
        }
    }

    public static void main(String arg[]) throws Exception {

        grabber = new OpenCVFrameGrabber("test.avi");
        grabber.start();
        CanvasFrame canvasFrame = new CanvasFrame("HOG-Test");
 canvasFrame.setCanvasSize(grabber.getImageWidth(),grabber.getImageHeight());

        CvSize frameSize = new CvSize(grabber.getImageWidth(),grabber.getImageHeight());
        currentFrame = cvCreateImage(frameSize, IPL_DEPTH_8U, 3);

      HOGDescriptor hog = new HOGDescriptor();
 hog.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());
      CvMemStorage storage = CvMemStorage.create();
        while(queryFrame()) {
            cvClearMemStorage(storage);
           CvRect found=new CvRect();
           hog.detectMultiScale(currentFrame, found, 0, cvSize(10, 10), cvSize(32, 32), 1.05, 0);
            for(int i=0;i<found.sizeof();i++) {
               int x = found.position(i).x(), y = found.position(i).y(), w = found.position(i).width(), h = found.position(i).height();
                cvRectangle(currentFrame, cvPoint(x, y),cvPoint(x + w, y + h), CvScalar.GREEN, 2, CV_AA, 0);
           }
            canvasFrame.showImage(currentFrame);
        }
        grabber.stop();
        canvasFrame.dispose();
      }

}
