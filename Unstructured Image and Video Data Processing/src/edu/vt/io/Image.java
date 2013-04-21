package edu.vt.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableUtils;

import com.googlecode.javacpp.BytePointer;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

public class Image implements Writable {

	private static final Log LOG = LogFactory.getLog(Image.class);

	// IPL image
	private IplImage image = null;
	private WindowInfo window = null;

	public Image() {
	}

	// Create Image from IplImage
	public Image(IplImage image) {
		this.image = image;
		this.window = new WindowInfo();
	}
	
	// Create empty Image
	public Image(int height, int width, int depth, int nChannels){
		this.image = cvCreateImage(cvSize(width, height), depth, nChannels);
		this.window = new WindowInfo();
	}

	// Create Image from IplImage and IplROI
	public Image(IplImage image, WindowInfo window) {
		this.image = image;
		this.window = window;
	}

	public IplImage getImage() {
		return image;
	}

	// get window where image came from
	public WindowInfo getWindow() {
		return window;
	}

	// Pixel depth in bits
	// PL_DEPTH_8U - Unsigned 8-bit integer
	// IPL_DEPTH_8S - Signed 8-bit integer
	// IPL_DEPTH_16U - Unsigned 16-bit integer
	// IPL_DEPTH_16S - Signed 16-bit integer
	// IPL_DEPTH_32S - Signed 32-bit integer
	// IPL_DEPTH_32F - Single-precision floating point
	// IPL_DEPTH_64F - Double-precision floating point
	public int getDepth() {
		return image.depth();
	}

	// Number of channels.
	public int getNumChannel() {
		return image.nChannels();
	}

	// Image height in pixels
	public int getHeight() {
		return image.height();
	}

	// Image width in pixels
	public int getWidth() {
		return image.width();
	}

	// The size of an aligned image row, in bytes
	public int getWidthStep() {
		return image.widthStep();
	}

	// Image data size in bytes.
	public int getImageSize() {
		return image.imageSize();
	}

	// Copies one image into current image using 
	// information contained in WindowInfo struct
	public void insertImage(Image sourceImage){
		IplImage img1 = this.image;
		IplImage img2 = sourceImage.getImage();
		WindowInfo win = sourceImage.getWindow();
		
		// set the ROI on destination image
		if(win.isParentInfoValid()){
			cvSetImageROI(img1, cvRect(win.getParentXOffset(), win.getParentYOffset(),win.getWidth(), win.getHeight()));
		}
		// set the ROI on source image
		if(win.isBorderValid()){
			cvSetImageROI(img2, cvRect(win.getBorderLeft(), win.getBorderTop(), win.getWidth(), win.getHeight()));
		}
		
		// copy sub-image
		cvCopy(img2, img1, null);
		 
		// reset the ROI
		cvResetImageROI(img1);
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		// Read image information
		int height = WritableUtils.readVInt(in);
		int width = WritableUtils.readVInt(in);
		int depth = WritableUtils.readVInt(in);
		int nChannels = WritableUtils.readVInt(in);
		int imageSize = WritableUtils.readVInt(in);
		// Read window information
		int windowXOffest = WritableUtils.readVInt(in);
		int windowYOffest = WritableUtils.readVInt(in);
		int windowHeight = WritableUtils.readVInt(in);
		int windowWidth = WritableUtils.readVInt(in);
		
		int top = WritableUtils.readVInt(in);
		int bottom = WritableUtils.readVInt(in);
		int left = WritableUtils.readVInt(in);
		int right = WritableUtils.readVInt(in);
		
		int h = WritableUtils.readVInt(in);
		int w = WritableUtils.readVInt(in);
		
		window = new WindowInfo();
		window.setParentInfo(windowXOffest, windowYOffest, windowHeight,windowWidth);
		window.setBorder(top, bottom, left, right);
		window.setWindowSize(h, w);

		// Read image bytes
		byte[] bytes = new byte[imageSize];
		in.readFully(bytes, 0, imageSize);

		image = cvCreateImage(cvSize(width, height), depth, nChannels);
		image.imageData(new BytePointer(bytes));
	}

	@Override
	public void write(DataOutput out) throws IOException {
		// Write image information
		WritableUtils.writeVInt(out, image.height());
		WritableUtils.writeVInt(out, image.width());
		WritableUtils.writeVInt(out, image.depth());
		WritableUtils.writeVInt(out, image.nChannels());
		WritableUtils.writeVInt(out, image.imageSize());

		// Write window information
		WritableUtils.writeVInt(out, window.getParentXOffset());
		WritableUtils.writeVInt(out, window.getParentYOffset());
		WritableUtils.writeVInt(out, window.getParentHeight());
		WritableUtils.writeVInt(out, window.getParentWidth());
		
		WritableUtils.writeVInt(out, window.getBorderTop());
		WritableUtils.writeVInt(out, window.getBorderBottom());
		WritableUtils.writeVInt(out, window.getBorderLeft());
		WritableUtils.writeVInt(out, window.getBorderRight());
		
		WritableUtils.writeVInt(out, window.getHeight());
		WritableUtils.writeVInt(out, window.getWidth());

		// Write image bytes
		ByteBuffer buffer = image.getByteBuffer();
		while (buffer.hasRemaining()) {
			out.writeByte(buffer.get());
		}
	}

}
