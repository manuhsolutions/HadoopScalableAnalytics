package com.manuh.vidproc;

        
import static com.googlecode.javacv.cpp.opencv_core.cvGetTickCount;
import static com.googlecode.javacv.cpp.opencv_core.cvGetTickFrequency;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


import com.manuh.input.VideoInputFormat;
import com.manuh.output.VideoOutputFormat;
        
public class VideoProcessor {
	private static final Log LOG = LogFactory.getLog(VideoProcessor.class);   
   
	public static void main(String[] args) throws Exception {
		long t = cvGetTickCount();
	    Configuration conf = new Configuration();
	    conf.setInt("mapreduce.videosplit.number", Integer.parseInt(args[2]));
	    long milliSeconds = 1800000; 
	    conf.setLong("mapred.task.timeout", milliSeconds);
	    Path input = new Path(args[0]);
	    FileSystem f = input.getFileSystem(conf);
	    conf.set("mapred.map.tasks", (f.listStatus(input).length * Integer.parseInt(args[2]))+"");
	    //System.out.println(f.listStatus(input).length);
	    Job job = new Job(conf, "VideoProcessing");
	    job.setJarByClass(VideoProcessor.class);
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(VideoObject.class);
	    job.setMapperClass(VidMapper.class);
	    job.setReducerClass(VidReducer.class);
	    job.setInputFormatClass(VideoInputFormat.class);
	    job.setOutputFormatClass(VideoOutputFormat.class);
	    FileInputFormat.addInputPath(job, input);
	    Path outputPath = new Path(job.getWorkingDirectory().toString()+"/"+args[1]);
	   /* FileSystem fs = outputPath.getFileSystem(conf);
	    if(fs.exists(outputPath)){
	    	fs.delete(outputPath, true);
	    }*/
	    FileOutputFormat.setOutputPath(job, new Path(args[1]));
	    job.waitForCompletion(true);
	    t = cvGetTickCount() - t;
		System.out.println("Process Time: " + (t/cvGetTickFrequency()/1000000) + "sec");
	 }
        
}