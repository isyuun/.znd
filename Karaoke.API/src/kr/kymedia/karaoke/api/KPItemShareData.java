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
 * filename	:	KPnnnnShareData.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.kpop.data
 *    |_ KPnnnnShareData.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.api;

import kr.kymedia.karaoke._IKaraoke.LOGIN;
import kr.kymedia.karaoke.util.TextUtil;
import android.webkit.URLUtil;

/**
 * 
 * TODO NOTE:<br>
 * SNS공유등 주요조회정보를 처리한다.
 * 
 * @author isyoon
 * @since 2012. 5. 17.
 * @version 1.0
 *
 */

public class KPItemShareData {
	public String uid = "";
	public String song_id = "";
	public String record_id = "";
	public String title = "";
	public String artist = "";
	public String name = "";
	public String url = "";

	/**
	 * SNS공유등 주요조회정보를 처리한다.
	 * 
	 * @param info
	 * @param list
	 */
	public KPItemShareData(KPItem info, KPItem list) {
		// info에서 정보를 받는다.
		if (info != null) {
			uid = !TextUtil.isEmpty(info.getValue("uid")) ? info.getValue("uid").trim() : uid;
			song_id = !TextUtil.isEmpty(info.getValue("song_id")) ? info.getValue("song_id").trim() : song_id;
			record_id = !TextUtil.isEmpty(info.getValue("record_id")) ? info.getValue("record_id").trim() : record_id;
			title = !TextUtil.isEmpty(info.getValue("title")) ? info.getValue("title").trim() : title;
			artist = !TextUtil.isEmpty(info.getValue("artist")) ? info.getValue("artist").trim() : artist;
			name = !TextUtil.isEmpty(info.getValue(LOGIN.KEY_NICKNAME)) ? info.getValue(LOGIN.KEY_NICKNAME).trim() : name;
			// 반주곡 재생화면에서 업로드 녹음곡 공유시
			url = URLUtil.isNetworkUrl(info.getValue("url_record")) ? info.getValue("url_record")
					.trim() : url;
			// 녹음곡 재생화면에서 녹음된 녹음곡 공유시
			url = URLUtil.isNetworkUrl(info.getValue("url_share")) ? info.getValue("url_share").trim()
					: url;
		}

		// list에서 정보를 받는다.
		if (list != null) {
			uid = !TextUtil.isEmpty(list.getValue("uid")) ? list.getValue("uid").trim() : uid;
			song_id = !TextUtil.isEmpty(list.getValue("song_id")) ? list.getValue("song_id").trim() : song_id;
			record_id = !TextUtil.isEmpty(list.getValue("record_id")) ? list.getValue("record_id").trim() : record_id;
			title = !TextUtil.isEmpty(list.getValue("title")) ? list.getValue("title").trim() : title;
			artist = !TextUtil.isEmpty(list.getValue("artist")) ? list.getValue("artist").trim() : artist;
			name = !TextUtil.isEmpty(list.getValue(LOGIN.KEY_NICKNAME)) ? list.getValue(LOGIN.KEY_NICKNAME).trim() : name;
			// 반주곡 재생화면에서 업로드 녹음곡 공유시
			url = URLUtil.isNetworkUrl(list.getValue("url_record")) ? list.getValue("url_record")
					.trim() : url;
			// 녹음곡 재생화면에서 녹음된 녹음곡 공유시
			url = URLUtil.isNetworkUrl(list.getValue("url_share")) ? list.getValue("url_share").trim()
					: url;
		}
	}
}
