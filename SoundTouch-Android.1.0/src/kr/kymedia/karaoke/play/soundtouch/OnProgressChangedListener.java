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
 * <p/>
 * This software is the confidential and proprietary information
 * of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * <p/>
 * project	:	.prj
 * filename	:	OnProgressChangedListener.java
 * author	:	isyoon
 * <p/>
 * <pre>
 * com.smp.soundtouchandroid.isyoon
 *    |_ OnProgressChangedListener.java
 * </pre>
 */
package kr.kymedia.karaoke.play.soundtouch;

/**
 * <pre>
 *
 * </pre>
 *
 * @author isyoon
 * @since 2015-10-13
 * @version 1.0
 */
public interface OnProgressChangedListener extends SoundTouchPlayable.OnProgressChangedListener{
	void onExceptionThrown(String string);
}
