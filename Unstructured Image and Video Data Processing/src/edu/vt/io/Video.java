package edu.vt.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class Video implements Writable {
	
	private IplImage [] images = null;
	private WindowInfo window = null;
	
	public Video() {
	
	}

	// Create Image from IplImage
	public Video(IplImage[] images) {
		this.images = images;
		this.window = new WindowInfo();
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		for(int i=0;i<images.length;i++) {
			new Image().readFields(in);
		}
	}
	@Override
	public void write(DataOutput out) throws IOException {
		
	}
}
