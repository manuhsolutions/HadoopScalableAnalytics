package edu.vt.input;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;

import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;


public class VideoReader {
	private static final Log LOG = LogFactory.getLog(VideoReader.class);
	
	private static final int DEFAULT_BUFFER_SIZE = 64 * 1024;
	private InputStream in;
	private int bufferSize = DEFAULT_BUFFER_SIZE;
	private byte[] buffer;
	private byte[] videoByteArray;
	private OpenCVFrameGrabber grabber;
	private static IplImage currentFrame;
	
    public VideoReader(InputStream in, int bufferSize) {
		    this.in = in;
		    this.bufferSize = bufferSize;
		    this.buffer = new byte[this.bufferSize];
    }
	
	public VideoReader(InputStream in, Configuration conf) throws IOException{
		
		this(in, conf.getInt("io.file.buffer.size", DEFAULT_BUFFER_SIZE));
	}
	
	public void close() throws IOException {
		in.close();
    }
	
	public byte[] readVideoFile() throws IOException{
		//FFmpegFrameGrabber g = new FFmpegFrameGrabber(in);
		// Get the size of the file
		long length = in.available();
		try {
			grabber = new OpenCVFrameGrabber(in);
			grabber.start();
			int len = grabber.getLengthInFrames();
			for( int i=0;i<len;i++) {
				IplImage frame =grabber.grab();
			}
		} catch(Exception e) {
			
		}
		// Create the byte array to hold the data
		videoByteArray = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < videoByteArray.length
			   && (numRead = in.read(videoByteArray, offset, videoByteArray.length - offset)) >= 0) {
					offset += numRead;
		}
		return videoByteArray;
	}
	
	public String getByteArrayLength(){
		Integer byteArrayLength = new Integer(videoByteArray.length);
		return byteArrayLength.toString();
	}
	
	public InputStream getInputStream(){
		return in;
	}
	

}
