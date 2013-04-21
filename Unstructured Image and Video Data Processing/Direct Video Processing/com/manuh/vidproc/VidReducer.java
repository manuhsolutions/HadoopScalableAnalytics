package com.manuh.vidproc;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class VidReducer extends Reducer<Text, VideoObject, Text, VideoObject> {
	
    public void reduce(Text key, Iterable<VideoObject> values, Context context) 
      throws IOException, InterruptedException {
    	Iterator<VideoObject> it = values.iterator();
    	while(it.hasNext()) {
        context.write(key, it.next());
    	}
    }	 
 }
