/*
 * Copyright 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 2012 All rights (c)KYGroup Co.,Ltd. reserved.
 * 
 * This software is the confidential and proprietary information
 *  of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * 
 * project	:	Karaoke.KPOP
 * filename	:	ImageLoader2.java
 * author	:	isyoon
 *
 * <pre>
 * com.fedorvlasov.lazylist
 *    |_ ImageLoader2.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.widget.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kr.kymedia.karaoke.util.FileCache2;
import kr.kymedia.karaoke.util.MemoryCache2;
import kr.kymedia.karaoke.util.Util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

/**
 * 
 * TODO NOTE:<br>
 * 리스트고속이미지처리용(리사징기능추가)
 * 
 * @author isyoon
 * @since 2012. 7. 18.
 * @version 1.0
 */
@Deprecated
public class ImageDownLoader2 {
	final private String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		String text = String.format("%s()", name);
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// text = String.format("line:%d - %s() ", line, name);
		return text;
	}

	Context context = null;
	int res = 0;
	Drawable drawable = null;
	boolean resize = false;

	MemoryCache2 memoryCache = new MemoryCache2();
	FileCache2 fileCache;
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService executorService;

	public ImageDownLoader2(Context context) {
		this.context = context;
		fileCache = new FileCache2(context);
		executorService = Executors.newFixedThreadPool(5);
	}

	@Deprecated
	public void DisplayImage(String url, ImageView v) {
		DisplayImage(url, v, resize, null);
	}

	/**
	 * 기본리소스이미지처리
	 * 
	 * @param url
	 * @param v
	 * @param res
	 */
	@Deprecated
	public void DisplayImage(String url, ImageView v, boolean resize, int res) {

		this.resize = resize;
		this.res = res;

		v.setImageResource(res);

		imageViews.put(v, url);
		Bitmap bm = memoryCache.get(url);
		if (bm != null) {
			v.setImageBitmap(bm);
			// 리사이즈기능추가
			if (resize) {
				resizeImageView2Bitmap(v, bm);
			}
		} else {
			queuePhoto(url, v);
		}
	}

	public void DisplayImage(String url, ImageView v, boolean resize, Drawable drawable) {

		this.resize = resize;
		this.drawable = drawable;

		if (drawable != null) {
			v.setImageDrawable(drawable);
		}

		imageViews.put(v, url);
		Bitmap bm = memoryCache.get(url);
		if (bm != null) {
			v.setImageBitmap(bm);
			// 리사이즈기능추가
			if (resize) {
				resizeImageView2Bitmap(v, bm);
			}
		} else {
			queuePhoto(url, v);
		}
	}

	private void queuePhoto(String url, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(String url) {
		// File f = fileCache.getFile(url);
		File f = fileCache.getFile(this.context, url);

		// from SD cache
		Bitmap b = decodeFile(f);
		if (b != null)
			return b;

		// from web
		try {
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			Util.CopyStream(is, os);
			os.close();
			bitmap = decodeFile(f);
			return bitmap;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	// decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 70;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			Bitmap bmp = getBitmap(photoToLoad.url);
			memoryCache.put(photoToLoad.url, bmp);
			if (imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bm;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bm = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			ImageView v = photoToLoad.imageView;
			if (bm != null) {
				v.setImageBitmap(bm);
				// 리사이즈기능추가
				if (resize) {
					resizeImageView2Bitmap(v, bm);
				}
			} else {
				// photoToLoad.imageView.setImageResource(res);
			}
		}
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}

	static public void resizeImageView2Bitmap(ImageView v, Bitmap bm) {
		float h1, w1, h2, w2;
		h1 = bm.getHeight();
		w1 = bm.getWidth();
		h2 = v.getHeight();
		w2 = v.getWidth();
		// Log.e("RESIZE", "[BF]->" + w1 + ":" + h1 + " = " + w2 + ":" + h2);
		if (h1 > w1) {
			h2 = (h1 / w1) * w2;
		} else if (h1 < w1) {
			h2 = w2 / (w1 / h1);
		} else {
			h2 = w2;
		}
		LayoutParams params = (LayoutParams) v.getLayoutParams();
		params.height = (int) (h2 > 0 ? h2 : params.height);
		v.setLayoutParams(params);
		// Log.e("RESIZE", "[AF]->" + w1 + ":" + h1 + " = " + w2 + ":" + h2);
	}
}
