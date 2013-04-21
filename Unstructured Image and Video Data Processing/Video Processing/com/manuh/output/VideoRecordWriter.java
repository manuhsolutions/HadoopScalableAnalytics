package com.manuh.output;

import java.io.IOException;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;


import com.manuh.vidproc.VideoObject;

public class VideoRecordWriter extends RecordWriter<Text, VideoObject>{

	private Path outputPath = null;
	private FileSystem fs = null;
	
	VideoRecordWriter(Path outputPath, FileSystem fs){
		this.outputPath = outputPath;
		this.fs = fs;
	}
	
	@Override
	public void close(TaskAttemptContext context) throws IOException,
			InterruptedException {
		
	}
	
	@Override
	public void write(Text key, VideoObject value) throws IOException,
			InterruptedException {
		
		String fileName = key.toString();
		Path filePath = new Path(outputPath, fileName);
		FSDataOutputStream out = fs.create(filePath, false);
		out.write(value.getVideoByteArray());
		out.close();
	}
}
