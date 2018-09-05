package kr.kymedia.karaoke.play3;

import java.io.InputStream;

import android.graphics.Bitmap;import kr.kymedia.karaoke.data.SongData;

public interface SongView
{
	public int READY_MARGIN = 30;

	public void setOnPlayViewListener(PlayViewListener listener);

	public boolean open(String mp3, String lyc);

	public boolean open(String mp3, InputStream is);

	public void destroy();

	public boolean stop();

	public int getMp3Position();

	public int getHeaderTotalTime();

	public void setRuby(boolean b);

	public boolean getRuby();

	public void setLang(int v);

	public int getLang();

	public void setSystem(boolean b);

	public boolean getSystem();

	public void setViewType(int v);

	public int getViewType();

	public void setRedraw(boolean redraw);

	public void setStatus(int status);

	public int getStatus();

	public void setSyncTime(int msec);

	public int getSyncTime();

	public boolean jump();

	public boolean seek(int time);

	public void redraw();

	public void setShow(boolean show);

	public void setBackgroundImage(Bitmap b, int x, int y, int width, int height, int count);

	public void setAnimatedSpriteImage(Bitmap b, int x, int y, int width, int height, float fps, int count, boolean loop);

	public void setStatusImage(Bitmap b, int x, int y, int width, int height, int count);

	public void setReadyImage(Bitmap b, int x, int y, int width, int height, int count);

	public void setBackgroundColor(int r, int g, int b);

	public void setBackgroundColor(int a, int r, int g, int b);

	public void setBackgroundTransparent(boolean type);

	public void setFont(String path);

	public void setLyrics(int posHeight, int fontHeight, int fontBackColor, int fontPaintColor, int alpha);

	public void setReady(boolean view, int posHeight, int fontHeight, int fontBackColor, int alpha);

	public void setTitle(boolean view, int posHeight, int fontHeight, int fontBackColor, int alpha);

	public void setSinger(boolean view, int posWidth, int posHeight, int fontHeight, int fontBackColor, int alpha);

	public void setVision(int type);

	public void updateTime(int t);

	public void setLyricAlign(int align);

	public void setLyricMargin(int margin);

	public void setLyricDelay(int delay);

	public SongData getSongData();
}
