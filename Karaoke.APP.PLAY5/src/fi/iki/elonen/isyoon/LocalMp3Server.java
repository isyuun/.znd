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
 * project	:	Karaoke.PLAY4.APP
 * filename	:	LocalMp3Server.java
 * author	:	isyoon
 *
 * <pre>
 * fi.iki.elonen.isyoon
 *    |_ LocalMp3Server.java
 * </pre>
 * 
 */

package fi.iki.elonen.isyoon;

import java.io.FileInputStream;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

/**
 *
 * TODO<br>
 * 
 * <pre></pre>
 *
 * @author isyoon
 * @since 2014. 9. 29.
 * @version 1.0
 */
public class LocalMp3Server extends NanoHTTPD {

	String path;

	public LocalMp3Server(String path) {
		super(8089);
		this.path = path;
	}

	@Override
	@Deprecated
	public Response serve(String uri, Method method, Map<String, String> headers,
			Map<String, String> parms, Map<String, String> files) {

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new NanoHTTPD.Response(Status.OK, "audio/mpeg", fis);
	}

	public void setPath(String path) {

		this.path = path;
	}

}
