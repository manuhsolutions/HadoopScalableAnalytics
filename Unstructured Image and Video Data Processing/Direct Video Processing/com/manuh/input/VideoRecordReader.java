package com.manuh.input;

import java.io.IOException;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.commons.logging.Log;


import com.manuh.vidproc.VideoObject;


public class VideoRecordReader extends RecordReader<Text, VideoObject>{
	
	private static final Log LOG = LogFactory.getLog(VideoRecordReader.class);
	private int start =0;	
	private String filename;
	private int end;
	
	private FSDataInputStream fileIn;
	private Text key = null;
	private VideoObject value = null;
	private VideoObject video = null;
	//private VideoDivider videoDivider;

	@Override
	public synchronized void close() throws IOException {
	  }

	@Override
	public Text getCurrentKey() throws IOException, InterruptedException {
		return key;
	}

	@Override
	public VideoObject getCurrentValue() throws IOException,
			InterruptedException {
		return value;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		if (start == end) {
			//return 0.0f;
		    return 1.0f;
		} 
		else {
			return (float) (end - start) / (end );
		    //return Math.min(1.0f, (pos - start) / (float)(end - start));
		}

	}

	@Override
	public void initialize(InputSplit genericSplit, TaskAttemptContext context)
			throws IOException, InterruptedException {
	  
		FileSplit split = (FileSplit) genericSplit;
	    Configuration job = context.getConfiguration();
	   
	    start = 0;
	    end = 1; 
	    
	    final Path file = split.getPath();
	    FileSystem fs = file.getFileSystem(job);
	    fileIn = fs.open(split.getPath());
	    filename = split.getPath().getName();
	    byte [] b = new byte[fileIn.available()];
		fileIn.readFully(b);
		video = new VideoObject(b);
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
	    if (start < end) {
	        key = new Text();	
	        key.set(filename);
	        value = video;
	        LOG.info("Log videoRecordReader ByteArrayLength: " + value.getVideoByteArray().length);
	        start++;
	        return true;
	    }
	    else{
	    	return false;
	    }
	    
	}
}
