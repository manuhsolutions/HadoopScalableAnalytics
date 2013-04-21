package com.manuh.output;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import com.manuh.vidproc.VideoObject;


public class Combiner
{
    private String tmpDir = "/home/ubuntu/tmp/";
    public Combiner() {  }
    
    public VideoObject merge(Iterable<VideoObject> values,String resultfile) {
		VideoObject result = null ;
		Iterator<VideoObject> it = values.iterator();
		int count =0;
		while(it.hasNext()) {
			VideoObject val = it.next();
			try {
				File f = new File(tmpDir+count+".avi");
				FileOutputStream out = new FileOutputStream(f);
				out.write(val.getVideoByteArray());
				System.out.println("Length :"+val.getVideoByteArray().length);
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			count++;
		}
		String command = "avconv -i concat:";
		String files = "";
		String resFile = tmpDir+resultfile; 
		for(int j=0;j<count;j++) {
			if(j==count-1)
				files=files+tmpDir+j+".avi";
			else
				files=files+tmpDir+j+".avi"+"|";
		}
		command=command+files+" -c copy "+resFile;
		System.out.println(command);
		try {
			Process ps = Runtime.getRuntime().exec(command);
			ps.waitFor();
			System.out.println("ps.exitValue() :"+ps.exitValue());
			if(ps.exitValue()==0) {
				File sf = new File(resFile);
				FileInputStream file_input = new FileInputStream (sf);
			   	byte[] temp = new byte[file_input.available()];
			   	System.out.println("Result Length :"+temp.length);
			   	file_input.read(temp);
			   	file_input.close();
			   	result = new VideoObject(temp);
			    if(sf.exists()) {
			    	sf.delete();
			    }
			    for(int j=0;j<count;j++) {
			    	File f = new File(tmpDir+j+".avi");
			    	if(f.exists())
			    		f.delete();
			    }
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}
}
