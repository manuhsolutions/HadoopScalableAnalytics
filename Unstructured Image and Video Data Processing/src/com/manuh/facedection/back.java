package com.manuh.facedection;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

import java.io.File;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import javax.imageio.ImageIO;
import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.CvHistogram;

import static com.googlecode.javacv.cpp.opencv_core.*;

public class back {

//private static String[] testFiles = new String[] {"my.jpg","my1.jpg"};
// private static String testFilespath = "/home/student/Desktop/images";
private static String testFilespath ="images";
public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> 
{
 private Text image = new Text();
 private final static IntWritable one = new IntWritable(1);
 private final static IntWritable zero = new IntWritable(0);
 public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException
   {  
      String line = value.toString();
   // for (String identifier : testFiles)
   //  {
  //  FileInputStream fis=new FileInputStream(testFilespath+"/"+"my.jpg");
//Document doc = builder.createDocument(fis, identifier);
  //FileInputStream imageStream = new FileInputStream(testFilespath + "/" + identifier);
      //BufferedImage bimg = ImageIO.read(fis);
      String f=testFilespath+"/"+line;
      IplImage src_img = cvLoadImage (f, CV_LOAD_IMAGE_COLOR);
      f=testFilespath+"/"+"my1.jpg";
      IplImage tmp_img = cvLoadImage (f, CV_LOAD_IMAGE_COLOR);
      IplImage src_planes[]={null,null,null};
      IplImage src_hsv,tmp_hsv,dst_img;
      CvHistogram hist;
      int i, hist_size[] = {90};
    float h_ranges[] = { 0, 180 };
    float ranges[][] = { h_ranges };
    CvSize dst_size;
    CvPoint min_loc=new CvPoint();
    CvPoint max_loc=new CvPoint();
    //0,src_planes1,src_planes2;
      IplImage tmp_planes[]={null,null,null};
      for (i = 0; i < 3; i++) {
          src_planes[i]=cvCreateImage(cvGetSize (src_img), IPL_DEPTH_8U, 1);
          tmp_planes[i] = cvCreateImage (cvGetSize (tmp_img), IPL_DEPTH_8U, 1);
       //   src_planes1=cvCreateImage(cvGetSize (src_img), IPL_DEPTH_8U, 1);
        //  tmp_planes1 = cvCreateImage (cvGetSize (tmp_img), IPL_DEPTH_8U, 1);
         // src_planes2=cvCreateImage(cvGetSize (src_img), IPL_DEPTH_8U, 1);
        //  tmp_planes2 = cvCreateImage (cvGetSize (tmp_img), IPL_DEPTH_8U, 1);
        //cout<<"I"; 
          }
      src_hsv= cvCreateImage (cvGetSize (src_img), IPL_DEPTH_8U, 3);
    tmp_hsv = cvCreateImage (cvGetSize (tmp_img), IPL_DEPTH_8U, 3);
  //            CanvasFrame canvas1 = new CanvasFrame("sreeeee");
  //  canvas1.showImage(src_hsv);
    cvCvtColor (src_img, src_hsv, CV_BGR2HSV);
    cvCvtColor (tmp_img, tmp_hsv, CV_BGR2HSV);
  //  CanvasFrame canvas2 = new CanvasFrame("sczx");
  //  canvas2.showImage(src_hsv);
    cvSplit(src_hsv, src_planes[0], src_planes[1], src_planes[2], null);
    cvSplit(tmp_hsv, tmp_planes[0], tmp_planes[1], tmp_planes[2], null);
    //   f=testFilespath+"/"+"my1.jpg";
    //  IplImage tmp_img = cvLoadImage (f, CV_LOAD_IMAGE_COLOR);
//     CanvasFrame canvas2 = new CanvasFrame("pix");
//   canvas2.showImage(src_hsv);
hist = cvCreateHist (1, hist_size, CV_HIST_ARRAY, ranges, 1);

// IplImage.PointerByReference planesPointer = new IplImage.PointerByReference(tmp_planes);
cvCalcHist(tmp_planes, hist, 0, null);
// (5)探索画像全体に対して，テンプレートのヒストグラムとの距離（手法に依存）を計算します． 
dst_size =
  cvSize (src_img.width() - tmp_img.width() + 1,
          src_img.height() - tmp_img.height() + 1);
int n=src_img.width() - tmp_img.width() + 1;
int g=src_img.height() - tmp_img.height() + 1;
//System.out.println("dfsd"+n+"dfgdfgdf"+g);
//- tmp_img.width() + 1);

image.set(line);
int flag=0;
if(n>0 && g>0)
{dst_img = cvCreateImage (dst_size, IPL_DEPTH_32F, 1);
//CanvasFrame canvasr = new CanvasFrame("klkhj");
//canvasr.showImage(dst_img);

cvCalcBackProjectPatch (src_planes, dst_img, cvGetSize (tmp_img), hist,
                        CV_COMP_CORREL, 1.0);
// CanvasFrame canvas3 = new CanvasFrame("pixkjhkjh");
// canvas3.showImage(src_hsv);
double[] min_val={0};
double[] max_val={0};
//min_loc.x()=10;

cvMinMaxLoc(dst_img, min_val, max_val, min_loc, max_loc, null);
//    System.out.println("vxcvxc "+min_val[0]+"   "+max_val[0]);
//    System.out.println("vxcvxc "+max_loc.x()+"   "+max_loc.y());
//    System.out.println("vxcvxc "+min_loc.x()+"   "+min_loc.y());
//***strong text***//   System.out.println("vxcvxc "+tmp_img.width()+"   "+tmp_img.height());
//(6)テンプレートに対応する位置に矩形を描画します． 

if(max_loc.x() !=0 && max_loc.y() !=0)
{ cvRectangle (src_img, max_loc,
             cvPoint (max_loc.x() + tmp_img.width(),
                      max_loc.y() + tmp_img.height()), CV_RGB (255, 0, 0), 3, 8, 0);
  flag=1;
}
// cvNamedWindow ("src", CV_WINDOW_AUTOSIZE);
// cvNamedWindow ("dst", CV_WINDOW_AUTOSIZE);
 //     CanvasFrame canvas4 = new CanvasFrame("sad");
//canvas4.showImage(src_img);
//CanvasFrame canvas5 = new CanvasFrame("lkj");
//canvas5.showImage(dst_img);
//CanvasFrame canvas6 = new CanvasFrame("jkl");
//canvas6.showImage(src_hsv);
//cvWaitKey(0);

//cvDestroyWindow("Image");

cvReleaseImage(dst_img);
cvReleaseImage(tmp_img);
cvReleaseImage(src_hsv);
cvReleaseImage(tmp_hsv);
for(i=0; i<3; i++) {
  cvReleaseImage(src_planes[i]);
  cvReleaseImage(tmp_planes[i]);
//  cvReleaseImage(src_planes1);
//  cvReleaseImage(tmp_planes1);
//  cvReleaseImage(src_planes2);
//  cvReleaseImage(tmp_planes2);
}


}
if(flag==1)
 {
  output.collect(image,one);
  line = "/user/hduser/output/"+line;
  cvSaveImage(line, src_img);
  cvReleaseImage(src_img);
 }
else
 output.collect(image,zero);

//System.out.println("key value pair   "+line);
    // }
   }
}

public static class Reduce extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
   public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
     int sum = 0;
     while (values.hasNext()) {
       sum += values.next().get();
      // System.out.println("jkgj"+key+"nbnbmnbmnb"+sum+"jghgjhgjhg");
     }
     output.collect(key, new IntWritable(sum));
   }
 }

 public static void main(String[] args) throws Exception {
   JobConf conf = new JobConf(back.class);
   conf.setJobName("back");

   conf.setOutputKeyClass(Text.class);
   conf.setOutputValueClass(IntWritable.class);

   conf.setMapperClass(Map.class);
   conf.setCombinerClass(Reduce.class);
   conf.setReducerClass(Reduce.class);

   conf.setInputFormat(TextInputFormat.class);
   conf.setOutputFormat(TextOutputFormat.class);

   FileInputFormat.setInputPaths(conf, new Path(args[0]));
   FileOutputFormat.setOutputPath(conf, new Path(args[1]));

   JobClient.runJob(conf);
 }


}