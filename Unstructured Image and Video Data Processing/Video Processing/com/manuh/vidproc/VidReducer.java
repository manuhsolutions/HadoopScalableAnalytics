package com.manuh.vidproc;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import com.manuh.output.Combiner;

public class VidReducer extends Reducer<Text, VideoObject, Text, VideoObject> {
	
    public void reduce(Text key, Iterable<VideoObject> values, Context context) 
      throws IOException, InterruptedException {
    	Iterator<VideoObject> it = values.iterator();
    	VideoObject result = new Combiner().merge(values, key.toString());
        context.write(key, result);
    }	 
 }
