package com.manuh.facedection;

import static com.googlecode.javacv.cpp.opencv_core.CV_AA;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvInRangeS;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_core.cvRectangle;
import static com.googlecode.javacv.cpp.opencv_core.cvReleaseImage;
import static com.googlecode.javacv.cpp.opencv_core.cvScalar;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;
import static com.googlecode.javacv.cpp.opencv_highgui.cvConvertImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2HSV;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_MEDIAN;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvSmooth;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvGetSpatialMoment;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvGetCentralMoment;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvMoments;
import java.awt.Dimension;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.CvMoments;
import com.googlecode.javacv.cpp.opencv_objdetect.HOGDescriptor;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
public class ObjectMovingDetection {
    //public static IplImage  = null;
	static int hueLowerR = 160;
    static int hueUpperR = 180;
  
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
    	 CvMemStorage storage = CvMemStorage.create();
    	 grabber.start();
         while(queryFrame()) {
               cvClearMemStorage(storage);
   	
    	
        CanvasFrame canvasFrame = new CanvasFrame("Object-Detection");
 canvasFrame.setCanvasSize(grabber.getImageWidth(),grabber.getImageHeight());
 IplImage frame = grabber.grab();
        CvSize frameSize = new CvSize(grabber.getImageWidth(),grabber.getImageHeight());
        currentFrame = cvCreateImage(frameSize, IPL_DEPTH_8U, 3);
        IplImage detectThrs = getThresholdImage(frame);
      //  canvasFrame.showImage(detectThrs);
        int posX = 0;
        int posY = 0;
        CvMoments moments = new CvMoments();
        cvMoments(detectThrs, moments, 1);
        double mom10 = cvGetSpatialMoment(moments, 1, 0);
        double mom01 = cvGetSpatialMoment(moments, 0, 1);
        double area = cvGetCentralMoment(moments, 0, 0);
        posX = (int) (mom10 / area);
        posY = (int) (mom01 / area);
        JPanel jp = new JPanel();
        canvasFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        canvasFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        canvasFrame.setContentPane(jp);
        Graphics g = jp.getGraphics();
        canvasFrame.setSize(detectThrs.width(), detectThrs.height());
        // g.clearRect(0, 0, img.width(), img.height());
        g.setColor(Color.RED);
        // g.fillOval(posX, posY, 20, 20);
        g.drawOval(posX, posY, 20, 20);
        System.out.println(posX + " , " + posY);
        
        grabber.stop();
        canvasFrame.dispose();
       }
    }
      private static IplImage getThresholdImage(IplImage orgImg) throws Exception {
    	 
        
        
        IplImage imgHSV = cvCreateImage(cvGetSize(orgImg), 8, 3);
        System.out.println(cvGetSize(orgImg));
        cvCvtColor(orgImg, imgHSV, CV_BGR2HSV);
        // 8-bit 1- color = monochrome
         IplImage imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1);
        // cvScalar : ( H , S , V, A)
        cvInRangeS(imgHSV, cvScalar(160, 100, 100, 0), cvScalar(180, 255, 255, 0), imgThreshold);
        cvReleaseImage(imgHSV);
        cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 13);
       // cvSaveImage( "dsmthreshold.jpg", imgThreshold);
        return imgThreshold;
        
      }
   
       
      }      

      
	 
        
    
    
    
    
    
    /* 
    private static OpenCVFrameGrabber grabber;
    private static IplImage currentFrame;

    

    public static void main(String[] args) throws Exception 
    {
    	
    	IplImage frame;
    	
    	 grabber = new OpenCVFrameGrabber("test.avi");
         grabber.start();
         frame = grabber.grab();
         CanvasFrame canvasFrame = new CanvasFrame("HOG-Test");
  canvasFrame.setCanvasSize(grabber.getImageWidth(),grabber.getImageHeight());

         CvSize frameSize = new CvSize(grabber.getImageWidth(),grabber.getImageHeight());
         currentFrame = cvCreateImage(frameSize, IPL_DEPTH_8U, 3);
         
         
         System.out.println(frameSize);
         cvCvtColor(frame, currentFrame, CV_BGR2HSV);
         // 8-bit 1- color = monochrome
         IplImage imgThreshold = cvCreateImage(cvGetSize(frame), 8, 3);
         CvMemStorage storage = CvMemStorage.create();
         if(frame == null)
         {
        	 currentFrame = cvCreateImage(frameSize, IPL_DEPTH_8U, 3); 
         }
         else{
         while ((frame = grabber.grab()) != null) {
         
        // IplImage frame = grabber.grab();
        // IplImage imgHSV = cvCreateImage(cvGetSize(orgImg), 8, 3);
        System.out.println("Processing");
         // cvScalar : ( H , S , V, A)
         //CvMemStorage storage = CvMemStorage.create();
       
            cvClearMemStorage(storage);
             canvasFrame.showImage(currentFrame);
         cvInRangeS(currentFrame, cvScalar(hueLowerR, 100, 100, 0), cvScalar(hueUpperR, 255, 255, 0), imgThreshold);
        // cvReleaseImage(currentFrame);
         cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 13);
         
        // canvasFrame.showImage(currentFrame);
         //cvClearMemStorage(storage);
         }
         }
         grabber.stop();
         canvasFrame.dispose(); }
}
       
*/
 
    	/*
    	
       // IplImage orgImg = cvLoadImage("rainbow.jpg");
        IplImage thresholdImage = hsvThreshold(frame);
        cvSaveImage("hsvthreshold.jpg", thresholdImage);
        Dimension position = getCoordinates(thresholdImage);
        System.out.println("Dimension of original Image : " + thresholdImage.width() + " , " + thresholdImage.height());
        System.out.println("Position of red spot    : x : " + position.width + " , y : " + position.height);
    }

    static Dimension getCoordinates(IplImage thresholdImage) {
        int posX = 0;
        int posY = 0;
        CvMoments moments = new CvMoments();
        cvMoments(thresholdImage, moments, 1);
        // cv Spatial moment : Mji=sumx,y(I(x,y)•xj•yi)
        // where I(x,y) is the intensity of the pixel (x, y).
        double momX10 = cvGetSpatialMoment(moments, 1, 0); // (x,y)
        double momY01 = cvGetSpatialMoment(moments, 0, 1);// (x,y)
        double area = cvGetCentralMoment(moments, 0, 0);
        posX = (int) (momX10 / area);
        posY = (int) (momY01 / area);
        return new Dimension(posX, posY);
    }

    static IplImage hsvThreshold(IplImage orgImg) {
        // 8-bit, 3- color =(RGB)
        IplImage imgHSV = cvCreateImage(cvGetSize(orgImg), 8, 3);
        System.out.println(cvGetSize(orgImg));
        cvCvtColor(orgImg, imgHSV, CV_BGR2HSV);
        // 8-bit 1- color = monochrome
        IplImage imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1);
        // cvScalar : ( H , S , V, A)
        cvInRangeS(imgHSV, cvScalar(20, 100, 100, 0), cvScalar(30, 255, 255, 0), imgThreshold);
        cvReleaseImage(imgHSV);
        cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 13);
        // save
        return imgThreshold;
    }
}

*/
