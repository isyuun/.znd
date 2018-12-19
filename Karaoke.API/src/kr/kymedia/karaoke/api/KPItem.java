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
 * project	:	Karaoke
 * filename	:	BaseItem.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.kpop.data
 *    |_ BaseItem.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.api;

import java.util.Iterator;

import kr.kymedia.karaoke.api.KPItem;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * TODO NOTE:<br>
 * 
 * @author isyoon
 * @since 2012. 3. 7.
 * @version 1.0
 * @see KPItem.java
 */

public class KPItem implements Parcelable {

	String data = "";
	JSONObject json = null;

	public KPItem() {
		json = new JSONObject();
	}

	public KPItem(String json) throws Exception {
		if (json != null) {
			this.data = json;
			this.json = new JSONObject(json);
		}
	}

	public KPItem(JSONObject json) throws Exception {
		if (json != null) {
			this.data = json.toString();
			this.json = json;
		}
	}

	@Override
	protected void finalize() throws Throwable {

		super.finalize();
		this.data = null;
		this.json = null;
	}

	public String getValue(String name) {
		String value = null;
		if (json == null) {
			return null;
		}
		try {
			value = json.getString(name);
			if (("null").equalsIgnoreCase(value)) {
				return null;
			}
			// escape문자클리어
			return value;
			// return value.replace("\\", "");
		} catch (Exception e) {

			// _Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
			return null;
		}
	}

	public JSONObject putValue(String name, String value) {
		return put(name, value);
	}

	private JSONObject put(String name, String value) {
		JSONObject json = null;
		try {
			json = this.json.put(name, value);
			data = this.json.toString();
		} catch (Exception e) {

			// _Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
		}
		return json;
	}

	@SuppressWarnings("unused")
	private Object get(String name) {
		Object object = null;
		try {
			object = this.json.get(name);
			data = this.json.toString();
		} catch (Exception e) {

			// _Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
		}
		return object;
	}

	public String toString() {
		if (json == null) {
			return "";
		}
		return json.toString();
	}

	public String toString(int indentSpaces) {
		if (json == null) {
			return "";
		}
		try {
			return json.toString(indentSpaces);
		} catch (Exception e) {

			// _Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
			return "";
		}
	}

	public void merge(KPItem item) {
		try {
			// _Log.d("KPnnnnItem.merge():BF", getJSON().toString(2));

			if (item == null) {
				return;
			}

			JSONObject json = item.getJSON();
			Iterator<?> keys = json.keys();

			while (keys.hasNext()) {
				String key = (String) keys.next();
				getJSON().put(key, json.get(key));
			}

			// _Log.d("KPnnnnItem::merge():AF", getJSON().toString(2));
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * @return the json
	 */
	private JSONObject getJSON() {
		return json;
	}

	/**
	 * @param json
	 *          the json to set
	 */
	@SuppressWarnings("unused")
	private void setJSON(JSONObject json) {
		this.json = json;
	}

	// ------------------------------------------
	// * Parcelable start
	// ------------------------------------------
	public KPItem(Parcel in) {
		// json = (JSONObject) in.readValue(json.getClass().getClassLoader());
		data = in.readString();
		try {
			json = new JSONObject(data);
		} catch (Exception e) {

			// _Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int i) {
		// out.writeValue(json);
		out.writeString(data);
		try {
			json = new JSONObject(data);
		} catch (Exception e) {

			// _Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
		}
	}

	public static final Parcelable.Creator<KPItem> CREATOR = new Parcelable.Creator<KPItem>() {
		public KPItem createFromParcel(Parcel source) {
			return new KPItem(source);
		}

		public KPItem[] newArray(int size) {
			return new KPItem[size];
		}
	};
	// ------------------------------------------
	// * Parcelable end
	// ------------------------------------------

}
