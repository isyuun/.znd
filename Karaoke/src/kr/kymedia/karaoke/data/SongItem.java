package kr.kymedia.karaoke.data;

import android.os.Parcel;
import android.os.Parcelable;

public class SongItem implements Parcelable {
	final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	public int down;
	public String order;
	public String number;
	public String title;
	public String artist;
	public String info;
	public String url;
	public String dst;

	public SongItem() {
		this.down = 1;
		this.order = "";
		this.number = "";
		this.title = "";
		this.artist = "";
		this.info = "";
		this.url = "";
		this.dst = "";
	}

	/**
	 * 
	 * 
	 * @param in
	 */
	private SongItem(Parcel in) {
		readFromParcel(in);
	}

	/**
	 * 
	 */
	public static final Parcelable.Creator<SongItem> CREATOR = new Parcelable.Creator<SongItem>() {
		public SongItem createFromParcel(Parcel in) {
			return new SongItem(in);
		}

		public SongItem[] newArray(int size) {
			return new SongItem[size];
		}
	};

	/**
	 * 
	 */
	@Override
	public int describeContents() {

		return 0;
	}

	/**
	 * 
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(order);
		dest.writeString(number);
		dest.writeString(title);
		dest.writeString(artist);
		dest.writeString(info);
		dest.writeInt(down);
	}

	/**
	 * 
	 * @param in
	 */
	private void readFromParcel(Parcel in) {
		order = in.readString();
		number = in.readString();
		title = in.readString();
		artist = in.readString();
		info = in.readString();
		down = in.readInt();
	}
}
