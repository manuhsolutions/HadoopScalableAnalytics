package com.manuh.output;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.manuh.vidproc.VideoObject;


public class VideoOutputFormat extends FileOutputFormat<Text, VideoObject>{

	@Override
	public org.apache.hadoop.mapreduce.RecordWriter<Text, VideoObject> getRecordWriter(
			TaskAttemptContext job) throws IOException, InterruptedException {
		Configuration conf = job.getConfiguration();
		Path outputPath = getOutputPath(job);
		FileSystem fs = outputPath.getFileSystem(conf);
		return new VideoRecordWriter(outputPath,fs);
	}

}
