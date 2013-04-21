package edu.vt.example;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import edu.vt.io.Image;
import edu.vt.input.ImageInputFormat;
import edu.vt.output.ImageOutputFormat;

public class CannyTest extends Configured implements Tool {
	public static class Map extends
			Mapper<Text, Image, Text, Image> {

		@Override
		public void map(Text key, Image value, Context context)
				throws IOException, InterruptedException {

			int N = 7;
			int aperature_size = N;
			double lowThresh = 100;
			double highThresh = 400;

			IplImage img = value.getImage();
			
			cvCanny( img, img, lowThresh*N*N, highThresh*N*N, aperature_size );
			
			context.write(key, value);
		}
	}

	public static class Reduce extends
			Reducer<Text, Image, Text, Image> {

		@Override
		public void reduce(Text key, Iterable<Image> values,
				Context context) throws IOException, InterruptedException {

			// Sum the parts
			Iterator<Image> it = values.iterator();
			Image img = null;
			Image part = null;
			while (it.hasNext()) {
				part = (Image) it.next();
				if(img == null){
					int height = part.getHeight();
					int width = part.getWidth();
					if(part.getWindow().isParentInfoValid()){
						height = part.getWindow().getParentHeight();
						width = part.getWindow().getParentWidth();
					}
					int depth = part.getDepth();
					int nChannel = part.getNumChannel();
					img = new Image(height, width, depth, nChannel);
				}
				img.insertImage(part);
			}

			context.write(key, img);
		}
	}

	public int run(String[] args) throws Exception {
		// Set various configuration settings
		Configuration conf = getConf();
		conf.setInt("mapreduce.imagerecordreader.windowsizepercent", 100);
		conf.setInt("mapreduce.imagerecordreader.borderPixel", 0);
		conf.setInt("mapreduce.imagerecordreader.iscolor", 0);
		
		// Create job
		Job job = new Job(conf);
		
		// Specify various job-specific parameters
		job.setJarByClass(CannyTest.class);
		job.setJobName("CannyTest");

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Image.class);

		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		job.setInputFormatClass(ImageInputFormat.class);
		job.setOutputFormatClass(ImageOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		return job.waitForCompletion(true) ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new CannyTest(), args);
		System.exit(res);
	}
}