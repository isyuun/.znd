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
 * 2015 All rights (c)KYGroup Co.,Ltd. reserved.
 * 
 * This software is the confidential and proprietary information
 *  of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * 
 * project	:	Karaoke
 * filename	:	Application.java
 * author	:	isyoon
 *
 * <pre>
 * android.app
 *    |_ Application.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.app;

import android.content.Context;
import androidx.multidex.MultiDex;

/**
 * <pre>
 * 지랄개좆같은안드로이드
 * 언제나캐삽질시키는구만
 * 씹지랄앤디루빈이개새끼
 * <a href="https://groups.google.com/forum/#!topic/android-developers/87pvh7W5Isw">Dex Limit workarounds- ADT or Android Studio only?</a>
 * <a href="http://android-developers.blogspot.kr/2011/07/custom-class-loading-in-dalvik.html">Custom Class Loading in Dalvik</a>
 * <a href="http://android-developers.blogspot.kr/2014/12/google-play-services-and-dex-method.html">Google Play services and DEX method limits</a>
 * <a href="https://medium.com/@rotxed/dex-skys-the-limit-no-65k-methods-is-28e6cb40cf71">[DEX] Sky’s the limit? No, 65K methods is</a>
 * <a href="https://github.com/mihaip/dex-method-counts">mihaip/dex-method-counts</a>
 * </pre>
 * 
 * @see <a href="https://developer.android.com/tools/building/multidex.html">Building Apps with Over 65K Methods</a>
 * @see <a href="https://source.android.com/devices/tech/dalvik/index.html">ART and Dalvik</a>
 * @see <a href="https://developer.android.com/tools/help/proguard.html">ProGuard</a>
 * @see <a href="https://developer.android.com/tools/support-library/features.html#multidex">Multidex Support Library</a>
 * @see <a href="http://stackoverflow.com/questions/26609734/how-to-enable-multidexing-with-the-new-android-multidex-support-library">How to enable multidexing with the new Android Multidex support library</a>
 * @see <a href="https://developer.android.com/reference/android/support/multidex/MultiDex.html">MultiDex</a>
 * @see <a href="https://developer.android.com/reference/android/support/multidex/MultiDexApplication.html">MultiDexApplication</a>
 *
 * @author isyoon
 * @since 2015. 7. 2.
 * @version 1.0
 */
public class MultiDexApplication extends androidx.multidex.MultiDexApplication {
	/**
	 * 
	 * <pre>
	 * 진짜이거한줄이다냐?씨팔~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * </pre>
	 * 
	 * @see <a href="http://stackoverflow.com/questions/26609734/how-to-enable-multidexing-with-the-new-android-multidex-support-library">How to enable multidexing with the new Android Multidex support library</a>
	 */
	@Override
	protected void attachBaseContext(Context base) {

		super.attachBaseContext(base);
		MultiDex.install(this);
	}
}
