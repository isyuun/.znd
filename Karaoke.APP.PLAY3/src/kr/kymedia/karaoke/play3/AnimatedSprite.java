package kr.kymedia.karaoke.play3;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class AnimatedSprite {
	private Bitmap animation;
	private int xPos;
	private int yPos;
	private Rect sRectangle;
	private int fps;
	private int numFrames;
	private int currentFrame;
	private long frameTimer;
	private int spriteHeight;
	private int spriteWidth;
	private boolean loop;
	public boolean dispose;

	public AnimatedSprite() {
		sRectangle = new Rect(0, 0, 0, 0);
		frameTimer = 0;
		currentFrame = 0;
		xPos = 0;
		yPos = 0;
		dispose = false;
	}

	public void Initialize(Bitmap bitmap, int width, int height, float fps, int frameCount,
			boolean loop) {
		this.animation = bitmap;
		this.spriteHeight = height;
		this.spriteWidth = width;
		this.sRectangle.top = 0;
		this.sRectangle.bottom = spriteHeight;
		this.sRectangle.left = 0;
		this.sRectangle.right = spriteWidth;
		this.fps = (int) (1000 / fps);
		this.numFrames = frameCount;
		this.loop = loop;
	}

	public void initialize(Bitmap bitmap, int x, int y, int width, int height, float fps, int frameCount, boolean loop) {
		this.animation = bitmap;
		this.spriteHeight = height;
		this.spriteWidth = width;
		this.sRectangle.top = 0;
		this.sRectangle.bottom = spriteHeight;
		this.sRectangle.left = 0;
		this.sRectangle.right = spriteWidth;
		this.fps = (int) (1000 / fps);
		this.numFrames = frameCount;
		this.loop = loop;
		this.xPos = x;
		this.yPos = y;
	}

	public int getXPos() {
		return xPos;
	}

	public int getYPos() {
		return yPos;
	}

	public void setXPos(int value) {
		xPos = value;
	}

	public void setYPos(int value) {
		yPos = value;
	}

	public void update(long gameTime) {
		if (loop == true) {
			if (gameTime > frameTimer + fps) {
				frameTimer = gameTime;
				currentFrame += 1;

				if (currentFrame >= numFrames) {
					currentFrame = 0;
				}

				sRectangle.left = currentFrame * spriteWidth;
				sRectangle.right = sRectangle.left + spriteWidth;
			}
		}
	}

	public void draw(Canvas canvas) {
		Rect dest = new Rect(getXPos(), getYPos(), getXPos() + spriteWidth, getYPos() + spriteHeight);
		canvas.drawBitmap(animation, sRectangle, dest, null);
	}
}
