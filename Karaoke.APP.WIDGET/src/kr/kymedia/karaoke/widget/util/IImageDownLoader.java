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
 * 2014 All rights (c)KYGroup Co.,Ltd. reserved.
 * 
 * This software is the confidential and proprietary information
 *  of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * 
 * project	:	Karaoke.APP
 * filename	:	ImageDownLoader.java
 * author	:	isyoon
 *
 * <pre>
 * com.example.android.displayingbitmaps.util
 *    |_ ImageDownLoader.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.widget.util;

import android.content.Context;
import android.widget.ImageView;

/**
 * 
 *
 * <pre></pre>
 * 
 * @author isyoon
 * @since 2014. 4. 24.
 * @version 1.0
 */
public interface IImageDownLoader {

	void putURLImage(final Context context, final ImageView v, final String url,
			final boolean resize, final int imageRes);

	void onResume();

	void onPause();

	void clear();

	void release();
}
