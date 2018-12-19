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
 * project	:	Karaoke.PLAY4.APP
 * filename	:	PlayViewValance2.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.play4.app.view
 *    |_ PlayViewValance2.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play.app.view;

import kr.kymedia.karaoke.util._Log;

import android.content.Context;
import android.util.AttributeSet;

/**
 * <pre>
 * 좌우음조절 
 * 	기존:10단계
 * 	변경:1단계
 * </pre>
 *
 * @author isyoon
 * @since 2015. 5. 19.
 * @version 1.0
 */
public class PlayViewValance2 extends PlayViewValance {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	@Override
	public String toString() {

		// return super.toString();
		// return getClass().getSimpleName() + '@' + Integer.toHexString(hashCode()) + ":" + song;
		return (new Exception()).getStackTrace()[0].getFileName() + '@' + Integer.toHexString(hashCode()) + ":" + song;
	}

	@Override
	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);

		return name + "() ";
	}

	public PlayViewValance2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public PlayViewValance2(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PlayViewValance2(Context context) {
		super(context);
	}

	@Override
	protected void init(boolean init) {

		_Log.w(__CLASSNAME__, getMethodName() + init);
		super.init(init);

		try {
			// 좌우초기화
			if (init) {
				/**
				 * 절대좌우(씪빠)
				 */
				final float absVALANCE = 1f;
				/**
				 * 단위좌우(씪빠)
				 */
				final float untVALANCE = 1f;
				/**
				 * 절대좌우(표시)
				 */
				final float dabsVALANCE = 1.0f;
				/**
				 * 단위좌우(표시)
				 */
				final float duntVALANCE = 1.0f;

				seekValance.init(absVALANCE, untVALANCE, dabsVALANCE, duntVALANCE);
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	@Override
	public void setValance(float LR) {

		_Log.w(__CLASSNAME__, getMethodName() + LR);
		super.setValance(LR);
	}

	@Override
	public void setValanceText(float LR) {

		_Log.w(__CLASSNAME__, getMethodName() + LR);
		super.setValanceText(LR);
	}

}
