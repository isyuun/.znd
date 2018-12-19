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
 * project	:	Karaoke.FileExplorer
 * filename	:	FileAdapter.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.widget
 *    |_ FileAdapter.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.kymedia.karaoke.view.CheckableSelectableRelativeLayout;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.isyoon.ArrayAdapter;

import com.lamerman.isyoon.R;
import com.lamerman.isyoon.SelectionItem;

/**
 *
 * TODO<br>
 * 
 * <pre></pre>
 *
 * @author isyoon
 * @since 2014. 9. 4.
 * @version 1.0
 */
public class FileAdapter extends ArrayAdapter<HashMap<String, Object>> {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	@Override
	public String toString() {

		// return super.toString();
		return getClass().getSimpleName() + '@' + Integer.toHexString(hashCode());
	}

	protected static String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	private final int mResource;
	private int mDropDownResource;
	private final LayoutInflater mInflater;

	private List<HashMap<String, Object>> mData;
	private final String[] mFrom;
	private final int[] mTo;

	private final List<HashMap<String, Object>> mOriginalValues; // Original Values

	public FileAdapter(Context context, ArrayList<HashMap<String, Object>> data, int resource,
			String[] from, int[] to) {
		super(context, resource, data);

		mResource = mDropDownResource = resource;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mData = data;
		mFrom = from;
		mTo = to;

		mOriginalValues = new ArrayList<HashMap<String, Object>>(mData); // saves the original data in mOriginalValues

	}

	private View createViewFromResource(int position, View convertView, ViewGroup parent, int resource) {

		if (convertView == null) {
			convertView = mInflater.inflate(resource, parent, false);
		} else {
		}

		bindView(position, convertView);

		return convertView;
	}

	/**
	 * <p>
	 * Sets the layout resource to create the drop down views.
	 * </p>
	 *
	 * @param resource
	 *          the layout resource defining the drop down views
	 * @see #getDropDownView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public void setDropDownViewResource(int resource) {
		this.mDropDownResource = resource;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return createViewFromResource(position, convertView, parent, mDropDownResource);
	}

	private void bindView(int position, View view) {
		// _Log.e(toString(), getMethodName() + position + "-" + view);
		if (position > mData.size() - 1) {
			return;
		}

		final Map<String, ?> dataSet = mData.get(position);
		if (dataSet == null) {
			return;
		}

		final String[] from = mFrom;
		final int[] to = mTo;
		final int count = to.length;

		for (int i = 0; i < count; i++) {
			final View v = view.findViewById(to[i]);
			if (v != null) {
				final Object data = dataSet.get(from[i]);
				String text = data == null ? "" : data.toString();
				if (text == null) {
					text = "";
				}

				boolean bound = false;

				// if (binder != null) {
				// bound = binder.setViewValue(v, data, text);
				// }

				if (!bound) {
					if (v instanceof Checkable) {
						if (data instanceof Boolean) {
							if (v instanceof CheckBox) {
								// _Log.e(toString(), getMethodName() + position + "-" + "CHECK" + data + ":" + v);
								((CheckBox) v).setChecked((Boolean) data);
							} else {
								((Checkable) v).setChecked((Boolean) data);
							}
						} else if (v instanceof TextView) {
							// _Log.e(toString(), getMethodName() + position + "-" + "TEXT" + data + ":" + v);
							// Note: keep the instanceof TextView check at the bottom of these
							// ifs since a lot of views are TextViews (e.g. CheckBoxes).
							setViewText((TextView) v, text);
						} else {
							throw new IllegalStateException(v.getClass().getName()
									+ " should be bound to a Boolean, not a "
									+ (data == null ? "<unknown type>" : data.getClass()));
						}
					} else if (v instanceof TextView) {
						// Note: keep the instanceof TextView check at the bottom of these
						// ifs since a lot of views are TextViews (e.g. CheckBoxes).
						setViewText((TextView) v, text);
					} else if (v instanceof ImageView) {
						if (data instanceof Integer) {
							setViewImage((ImageView) v, (Integer) data);
						} else {
							setViewImage((ImageView) v, text);
						}
					} else {
						throw new IllegalStateException(v.getClass().getName() + " is not a "
								+ " view that can be bounds by this SimpleAdapter");
					}
				}
			}
		}
	}

	/**
	 * Called by bindView() to set the image for an ImageView but only if
	 * there is no existing ViewBinder or if the existing ViewBinder cannot
	 * handle binding to an ImageView.
	 *
	 * This method is called instead of {@link #setViewImage(ImageView, String)} if the supplied data is an int or Integer.
	 *
	 * @param v
	 *          ImageView to receive an image
	 * @param value
	 *          the value retrieved from the data set
	 *
	 * @see #setViewImage(ImageView, String)
	 */
	public void setViewImage(ImageView v, int value) {
		v.setImageResource(value);
	}

	/**
	 * Called by bindView() to set the image for an ImageView but only if
	 * there is no existing ViewBinder or if the existing ViewBinder cannot
	 * handle binding to an ImageView.
	 *
	 * By default, the value will be treated as an image resource. If the
	 * value cannot be used as an image resource, the value is used as an
	 * image Uri.
	 *
	 * This method is called instead of {@link #setViewImage(ImageView, int)} if the supplied data is not an int or Integer.
	 *
	 * @param v
	 *          ImageView to receive an image
	 * @param value
	 *          the value retrieved from the data set
	 *
	 * @see #setViewImage(ImageView, int)
	 */
	public void setViewImage(ImageView v, String value) {
		try {
			v.setImageResource(Integer.parseInt(value));
		} catch (NumberFormatException nfe) {
			v.setImageURI(Uri.parse(value));
		}
	}

	/**
	 * Called by bindView() to set the text for a TextView but only if
	 * there is no existing ViewBinder or if the existing ViewBinder cannot
	 * handle binding to a TextView.
	 *
	 * @param v
	 *          TextView to receive text
	 * @param text
	 *          the text to be set for the TextView
	 */
	public void setViewText(TextView v, String text) {
		v.setText(text);
	}

	@Override
	public int getCount() {

		// return super.getCount();
		return mData.size();
	}

	@Override
	public HashMap<String, Object> getItem(int position) {

		// return super.getItem(position);
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {

		return super.getItemId(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// if (position == 0) {
		// _Log.w(toString(), getMethodName() + position + "-" + isSelectable + ":" + convertView);
		// } else {
		// _Log.i(toString(), getMethodName() + position + "-" + isSelectable + ":" + convertView);
		// }


		View v = createViewFromResource(position, convertView, parent, mResource);

		if (position > getCount() - 1) {
			return v;
		}

		if (v != null && v instanceof CheckableSelectableRelativeLayout) {
			HashMap<String, Object> item = getItem(position);
			// _Log.i(toString(), getMethodName() + position + "-" + isSelectable + ":" + item);
			Integer res = (Integer) item.get(SelectionItem.ITEM_IMAGE);
			if (res == R.drawable.file) {
				((CheckableSelectableRelativeLayout) v).setSelectable(isSelectable);
			} else {
				((CheckableSelectableRelativeLayout) v).setSelectable(false);
			}
			Boolean check = (Boolean) item.get(SelectionItem.ITEM_CHECK);
			((CheckableSelectableRelativeLayout) v).setChecked(check);
			// _Log.w(toString(), getMethodName() + position + "-" + check);
		}

		return v;
	}

	boolean isSelectable = false;

	public boolean isSelectable() {
		return isSelectable;
	}

	public void setSelectable(boolean isSelectable) {
		this.isSelectable = isSelectable;
	}

	public void setChecked(int position, boolean check) {
		HashMap<String, Object> item = getItem(position);
		Integer res = (Integer) item.get(SelectionItem.ITEM_IMAGE);
		if (res == R.drawable.file) {
			item.put(SelectionItem.ITEM_CHECK, check);
		}
		// _Log.e(toString(), getMethodName() + position + "-" + check + item.get(SelectionItem.ITEM_CHECK));
	}

	@Override
	public Filter getFilter() {

		// return super.getFilter();
		return new Filter() {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {

				// return null;

				FilterResults results = new FilterResults();

				// constraint is the result from text you want to filter against.
				// objects is your data set you will filter from
				if (constraint == null || constraint.length() == 0) {
					// set the Original result to return
					results.values = mOriginalValues;
					results.count = mOriginalValues.size();
				} else {
					ArrayList<HashMap<String, Object>> tempList = new ArrayList<HashMap<String, Object>>();
					int length = mOriginalValues.size();
					int i = 0;
					while (i < length) {
						HashMap<String, Object> item = mOriginalValues.get(i);
						// do whatever you wanna do here
						// adding result set output array
						String src = ((String) item.get(SelectionItem.ITEM_KEY)).toLowerCase();
						String tgt = constraint.toString().toLowerCase();

						if (src != null && src.contains(tgt)) {
							tempList.add(item);
						}

						i++;
					}
					// following two lines is very important
					// as publish result can only take FilterResults objects
					results.values = tempList;
					results.count = tempList.size();
				}
				return results;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {

				mData = (ArrayList<HashMap<String, Object>>) results.values;
				notifyDataSetChanged();
			}

		};
	}

	@Override
	public void notifyDataSetChanged() {

		super.notifyDataSetChanged();
	}

}
