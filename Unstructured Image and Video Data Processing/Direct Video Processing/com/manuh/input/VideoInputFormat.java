package com.manuh.input;


import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

import com.manuh.vidproc.VideoObject;

public class VideoInputFormat extends FileInputFormat<Text, VideoObject>{

	@Override
	public RecordReader<Text, VideoObject> createRecordReader(InputSplit arg0,
			TaskAttemptContext arg1) throws IOException, InterruptedException {
		return new VideoRecordReader();
	}
	@Override
	protected boolean isSplitable(JobContext context, Path file) {
		return false;
	}
}
