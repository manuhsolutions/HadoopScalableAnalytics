package edu.vt.example;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import edu.vt.input.ImageInputFormat;
import edu.vt.io.Image;

public class FaceDetect2  extends Configured implements Tool {
	public static class Map extends Mapper<Text, Image, Text, Text> {

		// Create memory for calculations
		CvMemStorage storage = null;

		// Create a new Haar classifier
		CvHaarClassifierCascade classifier = null;

		// List of classifiers
		String[] classifierName = {
				"classifiers/haarcascade_frontalface_alt.xml",
				"classifiers/haarcascade_frontalface_alt2.xml",
				"classifiers/haarcascade_profileface.xml" };

		@Override
		protected void setup(Context context) {
			// Allocate the memory storage
			storage = CvMemStorage.create();
			
			// Load the HaarClassifierCascade
			classifier = new CvHaarClassifierCascade(cvLoad(classifierName[0]));
			
			// Make sure the cascade is loaded
			if (classifier.isNull()) {
				System.err.println("Error loading classifier file");
			}
		}

		@Override
		public void map(Text key, Image value, Context context)
				throws IOException, InterruptedException {

			// Clear the memory storage which was used before
			cvClearMemStorage(storage);

			if(!classifier.isNull()){
				// Detect the objects and store them in the sequence
				CvSeq faces = cvHaarDetectObjects(value.getImage(), classifier,
						storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
				
				// Loop the number of faces found.  Draw red box around face.
				int total = faces.total();
				for (int i = 0; i < total; i++) {
					CvRect r = new CvRect(cvGetSeqElem(faces, i));
					int x = r.x(), y = r.y(), w = r.width(), h = r.height();
					String strRect = String.format("CvRect(%d,%d,%d,%d)", x, y, w, h);
					context.write(key, new Text(strRect));
				}
			}
		}
	}

	public static class Reduce extends Reducer<Text, Text, Text, Text> {

		@Override
		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			String str = "";
			Iterator<Text> it = values.iterator();
			while (it.hasNext()) {
				str += it.next().toString() + ";";
			}
			
			context.write(key, new Text(str));
		}
	}

	public int run(String[] args) throws Exception {
		// Set various configuration settings
		Configuration conf = getConf();
		conf.setInt("mapreduce.imagerecordreader.windowsizepercent", 100);
		conf.setInt("mapreduce.imagerecordreader.borderPixel", 0);
		conf.setInt("mapreduce.imagerecordreader.iscolor", 1);

		// Create job
		Job job = new Job(conf);

		// Specify various job-specific parameters
		job.setJarByClass(FaceDetect2.class);
		job.setJobName("FaceDetect2");

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		job.setInputFormatClass(ImageInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		return job.waitForCompletion(true) ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		long t = cvGetTickCount();
		int res = ToolRunner.run(new Configuration(), new FaceDetect2(), args);
		t = cvGetTickCount() - t;
		System.out.println("Time: " + (t/cvGetTickFrequency()/1000000) + "sec");
		System.exit(res);
	}
}