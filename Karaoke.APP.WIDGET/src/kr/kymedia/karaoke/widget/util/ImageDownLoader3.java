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
 * project	:	Karaoke.KPOP.APP
 * filename	:	ImageLoader.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.kpop.widget
 *    |_ ImageLoader.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.widget.util;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Handler;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

/**
 *
 * TODO<br>
 * NOTE:<br>
 * Android-universal-image-loader-1.9.2
 *
 * @author isyoon
 * @since 2013. 9. 27.
 * @version 1.0
 * @see
 */
public class ImageDownLoader3 implements ImageLoadingListener, IImageDownLoader {
	// private String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();
	private String __CLASSNAME__ = "[UIL]";

	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// text = String.format("line:%d - %s() ", line, name);
		String text = String.format("%s()", name);
		return text;
	}

	private final android.os.Handler handler = new android.os.Handler();

	protected void removeCallbacks(Runnable r) {
		if (handler != null) {
			handler.removeCallbacks(r);
		}
	}

	protected void post(Runnable r) {
		removeCallbacks(r);
		if (handler != null) {
			handler.post(r);
		}
	}

	protected void postDelayed(Runnable r, long delayMillis) {
		removeCallbacks(r);
		if (handler != null) {
			handler.postDelayed(r, delayMillis);
		}
	}

	Context context;

	// Android-universal-image-loader-1.8.6
	private ImageLoader imageLoader;

	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	// BitmapFactory.Options bitmapOptions;
	int imageRes;
	private ImageLoadingListener mImageLoadingListener;

	List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

	public ImageDownLoader3() {
		super();
	}

	public void init(ImageLoaderConfiguration config) {
		if (imageLoader != null) {
			imageLoader.init(config);
		}
	}

	@Override
	public void clear() {

		if (imageLoader != null) {
			imageLoader.clearMemoryCache();
		}
	}

	@Override
	public void release() {

		try {
			if (imageLoader != null) {
				imageLoader.clearMemoryCache();
				imageLoader.stop();
			}
			if (displayedImages != null) {
				displayedImages.clear();
			}
			displayedImages = null;
			if (imageLoader != null) {
				imageLoader.destroy();
			}
			imageLoader = null;
			mImageLoadingListener = null;
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	@Override
	protected void finalize() throws Throwable {

		super.finalize();

		release();

	}

	@Override
	public void onLoadingStarted(String imageUri, View view) {

		if (mImageLoadingListener != null) {
			mImageLoadingListener.onLoadingStarted(imageUri, view);
		}
	}

	@Override
	public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

		if (mImageLoadingListener != null) {
			mImageLoadingListener.onLoadingFailed(imageUri, view, failReason);
		}
	}

	boolean isAnimation = true;

	public void setIsAnimation(boolean isAnimation) {
		this.isAnimation = isAnimation;
	}


	@Override
	public void onLoadingComplete(String imageUri, final View view, Bitmap loadedImage) {

		if (mImageLoadingListener != null) {
			mImageLoadingListener.onLoadingComplete(imageUri, view, loadedImage);
		}
		if (loadedImage != null && view != null) {
			view.setVisibility(View.VISIBLE);
			if (isAnimation) {
				boolean firstDisplay = false;
				if (displayedImages != null) {
					firstDisplay = !displayedImages.contains(imageUri);
				}
				if (firstDisplay) {
					post(new Runnable() {
						@Override
						public void run() {
							FadeInBitmapDisplayer.animate(view, 100);
						}
					});
					displayedImages.add(imageUri);
				}
			}
		} else {
			// android.util.Log.e(__CLASSNAME__, "[ERROR:" + view + "]" + loadedImage);
		}
	}

	@Override
	public void onLoadingCancelled(String imageUri, View view) {

		if (mImageLoadingListener != null) {
			mImageLoadingListener.onLoadingCancelled(imageUri, view);
		}
	}

	public ImageLoadingListener getImageLoadingListener() {
		return mImageLoadingListener;
	}

	public void setImageLoadingListener(ImageLoadingListener listener) {
		this.mImageLoadingListener = listener;
	}

	public ImageDownLoader3(Context context) {
		super();
		this.context = context;
		initImageLoader(context);
	}

	private void initImageLoader(Context context) {
		try {
			imageLoader = ImageLoader.getInstance();
			File cacheDir = StorageUtils.getCacheDirectory(context);
			if (cacheDir == null) {
				cacheDir = StorageUtils.getIndividualCacheDirectory(context);
			}

			// Log.e(__CLASSNAME__, "[CACHE:" + cacheDir.exists() + "]" + cacheDir);

			// DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
			// .bitmapConfig(Bitmap.Config.RGB_565)
			// .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
			// .build();
			//
			// ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
			// .defaultDisplayImageOptions(defaultOptions)
			// .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
			// // .discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75)
			// .taskExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
			// .taskExecutorForCachedImages(AsyncTask.THREAD_POOL_EXECUTOR)
			// .threadPoolSize(3) // default
			// .threadPriority(Thread.NORM_PRIORITY - 1) // default
			// .tasksProcessingOrder(QueueProcessingType.FIFO) // default
			// .denyCacheImageMultipleSizesInMemory()
			// .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // default
			// .memoryCacheSize(2 * 1024 * 1024)
			// .discCache(new UnlimitedDiscCache(cacheDir)) // default
			// .discCacheSize(50 * 1024 * 1024)
			// .discCacheFileCount(100)
			// .discCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
			// .imageDownloader(new BaseImageDownloader(context)) // default
			// .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
			// // .enableLogging()
			// .build();
			//
			// imageLoader.init(config);

			// Create default options which will be used for every
			// displayImage(...) call if no options will be passed to this method
			DisplayImageOptions option = new DisplayImageOptions.Builder()
					.cacheInMemory(true)
					.cacheOnDisk(true)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
					.build();

			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
					.defaultDisplayImageOptions(option)
					//.threadPriority(Thread.MIN_PRIORITY)
					.build();

			imageLoader.init(config); // Do it on Application start

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	// @Deprecated
	// private ImageDownLoader2 imageDownLoader2;
	public void putURLImage(final Context context, final ImageView v, final String url,
			final boolean resize, final int imageRes) {

		if (v == null) {
			return;
		}

		try {
			// BitmapUtils2사용
			// BitmapUtils2.putURLImage(context, v, url, false, WidgetUtils.getDrawable(context, "ic_menu_01"));

			// com.nostra13.universalimageloader.core.ImageLoader사용
			// if (imageLoader == null) {
			// return;
			// }
			//
			// if (!imageLoader.isInited()) {
			// return;
			// }

			this.imageRes = imageRes;
			// v.setImageResource(imageRes);
			imageLoader.displayImage(url, v, this);
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	@Override
	public void onResume() {


	}

	@Override
	public void onPause() {


	}

}
