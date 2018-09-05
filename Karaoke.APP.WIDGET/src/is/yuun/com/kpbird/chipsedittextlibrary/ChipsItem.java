package is.yuun.com.kpbird.chipsedittextlibrary;

import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

public class ChipsItem {

	private String id;
	private String name;
	private Drawable image;
	private ImageSpan span;

	public ChipsItem() {

	}

	public ChipsItem(String id, String name) {
		this.id = id;
		this.name = name;
		image = null;
	}

	public ChipsItem(String id, String name, Drawable image) {
		this.id = id;
		this.name = name;
		this.setImage(image);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Drawable getImage() {
		return image;
	}

	public void setImage(Drawable image) {
		this.image = image;
	}

	public ImageSpan getSpan() {
		return span;
	}

	public void setSpan(ImageSpan span) {
		this.span = span;
	}

	@Override
	public String toString() {
		return getName();
	}

}
