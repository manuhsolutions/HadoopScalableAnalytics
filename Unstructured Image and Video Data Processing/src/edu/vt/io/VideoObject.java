package edu.vt.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.io.Writable;

public class VideoObject implements Writable{
	private byte[] videoByteArray= null;
	private InputStream in = null;
	
	public VideoObject(){}
	
	public VideoObject(byte[] video){
		videoByteArray = video;		
	}
		
	public void write(DataOutput out) throws IOException{
		out.write(videoByteArray);
	}
	
	public void readFields(DataInput in) throws IOException{
		in.readByte();
//		in.readFully(videoByteArray);
	}
	
	public byte[] getVideoByteArray(){
		return this.videoByteArray;
	}
	
	public InputStream getVideoStream(){
		return in;
	}
 }
