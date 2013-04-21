package com.manuh.facedection;


import static com.googlecode.javacv.cpp.opencv_core.*;

import static com.googlecode.javacv.cpp.opencv_highgui.*;

import static com.googlecode.javacv.cpp.opencv_objdetect.*;

import com.googlecode.javacpp.FloatPointer;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.CvSize;

public class HogImageNew {
    public static void main(String arg[]) throws Exception {
    CanvasFrame frame;
    IplImage originalImage,monoImg;
    HOGDescriptor hog;
    FloatPointer f;
    CvRect found;
    OpenCVFrameGrabber grabber;

    grabber = new OpenCVFrameGrabber("test.avi");
    grabber.start();
    
    
    CvSize frameSize = new CvSize(grabber.getImageWidth(), grabber.getImageHeight());
	originalImage = cvCreateImage(frameSize, IPL_DEPTH_8U, 3);
    
    
        // originalImage = cvLoadImage("Grp1.jpg", 1);
         monoImg = cvCreateImage(cvGetSize(originalImage), IPL_DEPTH_8U, 1);
         frame = new CanvasFrame("frame");
         frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
         frame.showImage(originalImage);
         //cvShowImage("Original",originalImage);
         f = HOGDescriptor.getDefaultPeopleDetector();
    	 System.out.println("........Processing started ........");
         hog=new HOGDescriptor(); 
         CvMat defaultSVM = CvMat.createHeader(f.capacity(), 1, 6);
         defaultSVM.data_fl(f);
         hog.setSVMDetector(defaultSVM);
         found = new CvRect(null);        
         hog.detectMultiScale(originalImage, found, 0, cvSize(1, 1), cvSize(4, 7), 1.05, 30);
         System.out.println(found);
         
        // While(found.get()!=null)
         
         int x = found.x(), y = found.y(), w = found.width(), h = found.height(); 
         cvRectangle(originalImage, cvPoint(x, y),cvPoint(x + w, y + h), CvScalar.RED, 1, CV_AA, 0);
         frame.showImage(originalImage);
         //cvShowImage("Result",originalImage);
        // System.out.println(originalImage);        
        
        System.out.println("..........done");
         }
    
}
