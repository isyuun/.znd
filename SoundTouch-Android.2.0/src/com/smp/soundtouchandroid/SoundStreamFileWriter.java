package com.smp.soundtouchandroid;

import java.io.IOException;

import kr.kymedia.karaoke.play.soundtouch.SoundStreamRunnable;

public class SoundStreamFileWriter extends SoundStreamRunnable implements OnProgressChangedListener
{
	private long start, end;
	private AACFileAudioSink file;
	private String fileNameOut;
	private FileWritingListener fileListener;
	
	public interface FileWritingListener extends OnProgressChangedListener
	{	
		public void onFinishedWriting(boolean success);
	}
	@Override
	public void onAudioFormatChanged(int samplingRate, int channels) throws IOException
	{
		boolean changed;
		changed = samplingRate != this.samplingRate || channels != this.channels;
		super.onAudioFormatChanged(samplingRate, channels);
		if (changed)
		{
			file.initFileOutputStream(fileNameOut);
		}
		
	}
	public SoundStreamFileWriter(int id, String fileNameIn, String fileNameOut,
			float tempo, float pitchSemi) throws IOException
	{
		super(id, fileNameIn, tempo, pitchSemi);
		this.fileNameOut = fileNameOut;
		file.initFileOutputStream(fileNameOut);
		setOnProgressChangedListener(this);
	}
	
	public SoundStreamFileWriter(int id, String fileNameIn, String fileNameOut,
			float tempo, float pitchSemi, float rate) throws IOException
	{
		super(id, fileNameIn, tempo, pitchSemi);
		setRate(rate);
		this.fileNameOut = fileNameOut;
		file.initFileOutputStream(fileNameOut);
		setOnProgressChangedListener(this);
	}
	
	public void setFileWritingListener(FileWritingListener listener)
	{
		this.fileListener = listener;
	}
	@Override
	protected AudioSink initAudioSink() throws IOException
	{
		file = new AACFileAudioSink(fileNameOut, getSamplingRate(), getChannels());
		return file;
	}

	@Override
	protected void onStart()
	{
		start = System.nanoTime();
	}

	@Override
	protected void onPause()
	{
	}

	@Override
	protected void onStop()
	{
		try
		{
			file.finishWriting();
			handler.post(new Runnable() {

				@Override
				public void run()
				{
					fileListener.onFinishedWriting(true);
				}
			});
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
			handler.post(new Runnable() {

				@Override
				public void run()
				{
					fileListener.onFinishedWriting(true);
				}
			});
		}
		end = System.nanoTime();
		long elapsedTime = end - start;
		double seconds = (double) elapsedTime / 1000000000.0;
		//Log.i("ENCODE", "SECONDS: " + String.valueOf(seconds));
	}

	@Override
	public void onProgressChanged(int track, double currentPercentage,
			long position)
	{
		fileListener.onProgressChanged(track, currentPercentage, position);
	}

	@Override
	public void onTrackEnd(int track)
	{
		finished = true;
		//Don't want to call it on the main thread - the most likely place we are.
		new Thread(new Runnable() {

			@Override
			public void run()
			{
				stop();
				
			}
		}).start();
	}

	

	@Override
	public void onExceptionThrown(String string)
	{
		fileListener.onExceptionThrown(string);
	}
}
