package com.manuh.input;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IContainerFormat;
import com.xuggle.xuggler.IStream;
import com.manuh.vidproc.VideoObject;

public class VideoDevider
{
	private static byte[] videoByteArray;
	private static long numPackets;
	private static ArrayList<IStream> streams = new ArrayList<IStream>();
	private static String format ;
	private static int numOfClips=1;
	private static IContainer container = IContainer.make();
	private static String tmpDir = "/home/ubuntu/tmp/"; 
	private static String filename;
	private static String tmpFile ;
	private static HashMap<String, Integer> info = new HashMap<String, Integer>();
	
	public VideoDevider(byte[] file,String filename,int numOfClips) {
		this.videoByteArray = file;
		this.filename = filename;
		this.numOfClips=numOfClips;
		this.tmpFile = tmpDir+filename;
		this.format = filename.substring(filename.indexOf(".")+1);
		try {
			File f = new File(tmpFile);
			FileOutputStream out = new FileOutputStream(f);
			out.write(videoByteArray);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public VideoObject[] seperateVideo()
	{
	VideoObject video[] = new VideoObject[numOfClips];
    ByteArrayInputStream bis = new ByteArrayInputStream(videoByteArray);		
	IContainerFormat containerFormat = IContainerFormat.make();
	containerFormat.setInputFormat(format);
	container.setInputBufferLength(bis.available());   	
	container.open(bis,containerFormat);
	int streamNumber = container.getNumStreams();    	
    for (int i = 0 ; i < streamNumber; i++){       	
    	streams.add(container.getStream(i));        	
    }
    numPackets = getVideoStream().getNumFrames();
    long durationSec = numPackets / getVideoStream().getTimeBase().getDenominator();
    container.close();
    try {
    		String frames[] = getTimeFrames(durationSec,numOfClips);
    		for(int i=0;i<frames.length-1; i++) {
    		String splitName = tmpDir+i+"_"+filename;	
    		String command = "ffmpeg -i "+ tmpFile+" -ss "+ frames[i]+" -t "+frames[i+1] +" -vcodec copy -acodec copy "+splitName;
    		System.out.println(command);
    		Process ps = Runtime.getRuntime().exec(command);
    		ps.waitFor();
    		if(ps.exitValue()==0) {
	    		File sf = new File(splitName);
	    		FileInputStream file_input = new FileInputStream (sf);
	     	   	byte[] temp = new byte[file_input.available()];
	     	   	file_input.read(temp);
	     	   	file_input.close();
	     	    video[i] = new VideoObject(temp);
	     	    video[i].setId(i);
	     	    if(sf.exists()) {
	     	    	sf.delete();
	     	    }
    		}
    	}
      File f = new File(tmpFile);
      if(f.exists()){
    	  f.delete();
      }
	} catch (Exception e) {
		e.printStackTrace();
	}
    return video;
  }
  
  private  String[] getTimeFrames(long durationSec, int numofclips) {

		String frames[] = new String[numofclips+1];
		frames[0]="00:00:00";
		long framelen = durationSec / numofclips ;
		long rem = durationSec % numofclips;
		long temp = 0;
		long min;
		long sec;
		for (int i = 1; i < frames.length ; i++) {
			temp = temp + framelen;
			if (i == numofclips - 1) {
				temp = temp + rem;
			}
			long hr = temp / 3600;
			if (hr > 0) {
				min = (temp - 3600) / 60;
				sec = (temp - 3600) % 60;
			} else {
				min = temp / 60;
				sec = temp % 60;
			}
			frames[i] = hr + ":" + min + ":" + sec;
		}
		return frames;
	}
  private IStream getVideoStream(){
		IStream videoStream = null;
		for (IStream is : streams){
			if(is.getStreamCoder().getCodec().getType().equals(ICodec.Type.CODEC_TYPE_VIDEO)){
				videoStream = is;
				break;
			}
		}
		return videoStream;
 }
}	
