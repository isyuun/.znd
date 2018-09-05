package kr.kymedia.karaoke.play3;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Sprite {
	private Bitmap animation;
	private int xPos;
	private int yPos;
	private int fps;
	private Rect sRectangle;
	private int numFrames;
	private int currentFrame;
	private long frameTimer;
	private int spriteHeight;
	private int spriteWidth;

	public Sprite() {
		animation = null;
		sRectangle = new Rect(0, 0, 0, 0);
		frameTimer = 0;
		currentFrame = 0;
		xPos = 0;
		yPos = 0;
	}

	public void initialize(Bitmap bitmap, int x, int y, int width, int height, int frameCount) {
		this.animation = bitmap;
		this.spriteHeight = height;
		this.spriteWidth = width;
		this.sRectangle.top = 0;
		this.sRectangle.bottom = spriteHeight;
		this.sRectangle.left = 0;
		this.sRectangle.right = spriteWidth;
		this.numFrames = frameCount;
		this.xPos = x;
		this.yPos = y;
	}

	public void initialize(Bitmap bitmap, int x, int y, int width, int height, float fps, int frameCount) {
		this.animation = bitmap;
		this.spriteHeight = height;
		this.spriteWidth = width;
		this.sRectangle.top = 0;
		this.sRectangle.bottom = spriteHeight;
		this.sRectangle.left = 0;
		this.sRectangle.right = spriteWidth;
		this.fps = (int) (1000 / fps);
		this.numFrames = frameCount;
		this.xPos = x;
		this.yPos = y;
	}

	public int getXPos() {
		return xPos;
	}

	public int getYPos() {
		return yPos;
	}

	public int getWidth() {
		return spriteWidth;
	}

	public int getHeight() {
		return spriteHeight;
	}

	public void setXPos(int value) {
		xPos = value;
	}

	public void setYPos(int value) {
		yPos = value;
	}

	public void update(int frame) {
		currentFrame = frame;

		if (currentFrame >= numFrames) {
			currentFrame = 0;
		}

		sRectangle.left = currentFrame * spriteWidth;
		sRectangle.right = sRectangle.left + spriteWidth;
	}

	public boolean update(long gameTime) {
		if (gameTime > frameTimer + fps) {
			frameTimer = gameTime;

			return true;
		}

		return false;
	}

	public void draw(Canvas canvas) {
		Rect dest = new Rect(getXPos(), getYPos(), getXPos() + spriteWidth, getYPos() + spriteHeight);
		canvas.drawBitmap(animation, sRectangle, dest, null);
	}

	public void draw(Canvas canvas, int x, int y) {
		Rect dest = new Rect(x, y, x + spriteWidth, y + spriteHeight);
		canvas.drawBitmap(animation, sRectangle, dest, null);
	}
}
