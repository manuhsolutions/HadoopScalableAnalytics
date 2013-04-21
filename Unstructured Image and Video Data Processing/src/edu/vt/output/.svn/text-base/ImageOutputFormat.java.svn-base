package edu.vt.output;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import edu.vt.io.Image;

public class ImageOutputFormat extends FileOutputFormat<Text, Image> {

	@Override
	public RecordWriter<Text, Image> getRecordWriter(TaskAttemptContext job)
			throws IOException, InterruptedException {

		Configuration conf = job.getConfiguration();
		Path outputPath = getOutputPath(job);
		FileSystem fs = outputPath.getFileSystem(conf);

		return new ImageRecordWriter(outputPath, fs);
	}
}
