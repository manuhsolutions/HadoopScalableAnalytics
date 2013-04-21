package com.manuh.vidproc;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableUtils;

public class VideoObject implements Writable{
	private byte[] videoByteArray= null;
	private InputStream in = null;
	private int id;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public VideoObject(){}
	
	public VideoObject(byte[] video){
		videoByteArray = video;	
	}
		
	public void write(DataOutput out) throws IOException{
		WritableUtils.writeVInt(out, videoByteArray.length);
		out.write(videoByteArray);
	}
	
	public void readFields(DataInput in) throws IOException{
		int len = WritableUtils.readVInt(in);
		videoByteArray = new byte[len];
		in.readFully(videoByteArray, 0, len);
	}
	
	public byte[] getVideoByteArray(){
		return this.videoByteArray;
	}
	
	public InputStream getVideoStream(){
		return in;
	}
 }
