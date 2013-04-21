package com.manuh.vidproc;

import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvClearSeq;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvLoad;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_core.cvRectangle;
import static com.googlecode.javacv.cpp.opencv_highgui.CV_FOURCC;
import static com.googlecode.javacv.cpp.opencv_highgui.cvConvertImage;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.cpp.opencv_objdetect.*;
import com.googlecode.javacv.OpenCVFrameRecorder;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class VidMapper extends Mapper<Text, VideoObject, Text, VideoObject> {
 
	private static final Log LOG = LogFactory.getLog(VidMapper.class); 
	private static OpenCVFrameGrabber grabber;
	private static IplImage currentFrame;
	private static final int FOURCC = CV_FOURCC('X', 'V', 'I', 'D');
    public void map(Text key, VideoObject value, Context context) throws IOException, InterruptedException {
    	ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(value.getVideoByteArray());
	 	LOG.info("Log__VideoConverter__byteArray: "+ byteArrayInputStream.available());
	 	
	 	String fileName = key.toString();
	 	int id = value.getId();
	 	LocalFileSystem fs = FileSystem.getLocal(context.getConfiguration());
		Path filePath = new Path("/tmp", fileName);
		Path resFile = new Path("/tmp", "res_"+fileName);
		System.out.println("File to Process :"+filePath.toString());
		FSDataOutputStream out = fs.create(filePath, true);
		out.write(value.getVideoByteArray());
		out.close();
		try {
			CvHaarClassifierCascade classifier = new CvHaarClassifierCascade(
					cvLoad("/usr/local/share/OpenCV/haarcascades/haarcascade_fullbody.xml"));
			if (classifier.isNull()) {
				System.err.println("Error loading classifier file");
			}
			grabber = new OpenCVFrameGrabber(filePath.toString());
			grabber.start();
			CvMemStorage storage = CvMemStorage.create();
			CvSize frameSize = new CvSize(grabber.getImageWidth(), grabber.getImageHeight());
			currentFrame = cvCreateImage(frameSize, IPL_DEPTH_8U, 3);
			CvSeq faces = null;
			OpenCVFrameRecorder recorder = new OpenCVFrameRecorder(resFile.toString()+".avi", frameSize.width(), frameSize.height());
			recorder.setVideoCodec(FOURCC);
			recorder.setFrameRate(grabber.getFrameRate());
			recorder.start();
			System.out.println("Video processing .........started");
			while(queryFrame()) {
				cvClearMemStorage(storage);
				faces = cvHaarDetectObjects(currentFrame, classifier, storage, 1.1, 3,CV_HAAR_DO_CANNY_PRUNING);
				int total = faces.total();
				for (int j = 0; j < total; j++) {
					CvRect r = new CvRect(cvGetSeqElem(faces, j));
						int x = r.x(), y = r.y(), w = r.width(), h = r.height();
						cvRectangle(currentFrame, cvPoint(x, y),cvPoint(x + w, y + h),CvScalar.RED, 1, CV_AA, 0);
				}
				cvClearSeq(faces);
				recorder.record(currentFrame);
			}
			grabber.stop();
			recorder.stop();
			System.out.println("Video processing .........Completed");
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		FSDataInputStream fin = fs.open(new Path(resFile.toString()+".avi"));
		byte [] b = new byte[fin.available()];
		fin.readFully(b);
		fin.close();
		VideoObject vres = new VideoObject(b);
		vres.setId(id);
    	context.write(key, vres);
    	fs.delete(new Path(resFile.toString()+".avi"),false);
		fs.delete(filePath,false);
    }
    
    private static boolean queryFrame() throws Exception {
   	 try {	
   		IplImage frame = grabber.grab();

   		if (frame != null) {
   			cvConvertImage(frame, currentFrame, 0);
   			return true;
   		} else {
   			return false;
   		}
   	 }catch(com.googlecode.javacv.FrameGrabber.Exception fge)  {
   		 return false;
   	 }
   	 catch(Exception e) {
   		 return false;
   	 }
   	}
} 