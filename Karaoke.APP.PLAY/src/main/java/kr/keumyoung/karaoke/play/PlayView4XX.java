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
 * filename	:	PlayView4XX.java
 * author	:	isyoon
 * <p/>
 * <pre>
 * kr.kymedia.kykaraoke.tv.play
 *    |_ PlayView4XX.java
 * </pre>
 */
package kr.keumyoung.karaoke.play;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import kr.kymedia.karaoke.play.impl.ISongPlay;
import kr.keumyoung.karaoke.play.R;

/**
 * <pre>
 * 음정/템포 UI처리
 * </pre>
 *
 * @author isyoon
 * @since 2015-11-04
 * @version 1.0
 */
class PlayView4XX extends PlayViewTempo {
	public PlayView4XX(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public PlayView4XX(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public PlayView4XX(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PlayView4XX(Context context) {
		super(context);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

}
