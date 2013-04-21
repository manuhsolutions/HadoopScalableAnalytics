package edu.vt.input;

import java.io.File;
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
import edu.vt.io.VideoObject;

public class VideoRecordReader extends RecordReader<Text, VideoObject>{
	
	private static final Log LOG = LogFactory.getLog(VideoRecordReader.class);
	private VideoReader videoReader;
	private long start;	
	private long pos;
	private String filename;
	private long end;
	
	private FSDataInputStream fileIn;
	private Text key = null;
	private VideoObject value = null;

	@Override
	public synchronized void close() throws IOException {
	    if (videoReader != null) {
	    	videoReader.close(); 
	    }
	  }

	@Override
	public Text getCurrentKey() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return key;
	}

	@Override
	public VideoObject getCurrentValue() throws IOException,
			InterruptedException {
		// TODO Auto-generated method stub
		return value;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		if (start == end) {
		    return 0.0f;
		} 
		else {
		    return Math.min(1.0f, (pos - start) / (float)(end - start));
		}

	}

	@Override
	public void initialize(InputSplit genericSplit, TaskAttemptContext context)
			throws IOException, InterruptedException {
	    FileSplit split = (FileSplit) genericSplit;
	    Configuration job = context.getConfiguration();
	    final Path file = split.getPath();
	    FileSystem fs = file.getFileSystem(job);
	    fileIn = fs.open(split.getPath());
	    filename = split.getPath().getName().substring(0,split.getPath().getName().indexOf('.'));
	    videoReader = new VideoReader(fileIn,job);
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
	    if (start < end) {
	        key = new Text();	
	        Long temp = new Long(start);
	        key.set(filename+temp.toString());
	        value = this.value;
	        LOG.info("Log__videoRecordReader ByteArrayLength: " + videoReader.getByteArrayLength());
	        start++;
	        return true;
	    }
	    else{
	    	return false;
	    }
	    
	}
}
