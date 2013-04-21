package com.manuh.vidproc;

        
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import static com.googlecode.javacv.cpp.opencv_core.*;

import com.manuh.input.VideoInputFormat;
import com.manuh.output.VideoOutputFormat;
        
public class DirectVideoProcessor {
	private static final Log LOG = LogFactory.getLog(DirectVideoProcessor.class);   
   
	public static void main(String[] args) throws Exception {
		long t = cvGetTickCount();
		Configuration conf = new Configuration();
	    long milliSeconds = 1800000; 
	    conf.setLong("mapred.task.timeout", milliSeconds);
	    Job job = new Job(conf, "VideoProcessing");
	    job.setJarByClass(DirectVideoProcessor.class);
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(VideoObject.class);
	    job.setMapperClass(VidMapper.class);
	    job.setReducerClass(VidReducer.class);
	    job.setInputFormatClass(VideoInputFormat.class);
	    job.setOutputFormatClass(VideoOutputFormat.class);
	    FileInputFormat.addInputPath(job, new Path(args[0]));
	    /*Path outputPath = new Path(job.getWorkingDirectory().toString()+"/"+args[1]);
	    FileSystem fs = outputPath.getFileSystem(conf);
	    if(fs.exists(outputPath)){
	    	fs.delete(outputPath, true);
	    }*/
	    FileOutputFormat.setOutputPath(job, new Path(args[1]));
	    job.waitForCompletion(true);
	    t = cvGetTickCount() - t;
		System.out.println("Process Time: " + (t/cvGetTickFrequency()/1000000) + "sec");
	 }
        
}