package is.yuun.com.kpbird.chipsedittextlibrary;

import java.util.ArrayList;

import kr.kymedia.karaoke.app.widget.R;
import kr.kymedia.karaoke.util.TextUtil;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ChipsMultiAutoCompleteTextview extends MultiAutoCompleteTextView implements
		OnItemClickListener, OnKeyListener, OnEditorActionListener {
	final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();
	final String __TOKENIZER__ = "";

	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		String text = String.format("%s()", name);
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// text = String.format("line:%d - %s() ", line, name);
		return text;
	}

	String mRecipients = "";
	String mMessage = "";

	SpannableStringBuilder mRecipientSSB = new SpannableStringBuilder("");
	ArrayList<ChipsItem> mRecipientChipsItems = new ArrayList<ChipsItem>();

	public ArrayList<ChipsItem> getRecipientChipsItems() {
		return mRecipientChipsItems;
	}

	// public void setRecipientChipsItems(ArrayList<ChipsItem> mRecipientChipsItems) {
	// this.mRecipientChipsItems = mRecipientChipsItems;
	// }

	/* Constructor */
	public ChipsMultiAutoCompleteTextview(Context context) {
		super(context);
		init(context);
	}

	/* Constructor */
	public ChipsMultiAutoCompleteTextview(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/* Constructor */
	public ChipsMultiAutoCompleteTextview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	InputFilter[] mOrgFilter;

	/* set listeners for item click and text change */
	public void init(Context context) {
		// setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
		// setOnItemClickListener(this);
		addTextChangedListener(textWather);
		setOnEditorActionListener(this);
		mOrgFilter = getFilters();
	}

	int getRecipientsSpanLength() {
		int len = 0;

		if (mRecipientChipsItems != null) {
			for (ChipsItem chipsItem : mRecipientChipsItems) {
				String name = chipsItem.getName();
				if (!TextUtil.isEmpty(name)) {
					len += name.length();
				}
			}
		}

		// Log2.e(__CLASSNAME__, getMethodName() + len);

		return len;
	}

	int getRecipientsSpanIndex(int start) {
		int idx = -1;

		int len = 0;
		for (ChipsItem chipsItem : mRecipientChipsItems) {
			idx++;
			if (start == len) {
				break;
			}
			String name = chipsItem.getName();
			if (!TextUtil.isEmpty(name)) {
				len += name.length();
			}
		}

		// Log2.e(__CLASSNAME__, getMethodName() + start + " - " + idx);

		return idx;
	}

	@Override
	protected void onSelectionChanged(int selStart, int selEnd) {
		// Log2.e(__CLASSNAME__, getMethodName() + "selStart:" + selStart + ", selEnd:" + selEnd + ", len:" + getRecipientsSpanLength());

		super.onSelectionChanged(selStart, selEnd);

		if (selStart < getRecipientsSpanLength()) {
			setEditable(false);
		} else {
			setEditable(true);
		}
	}

	public void setEditable(boolean editable) {
		// Log2.e(__CLASSNAME__, getMethodName() + getSelectionStart() + "," + getSelectionEnd() + "," + editable);
		if (mOrgFilter != null) {
			if (editable) {
				setFilters(mOrgFilter);
			} else {
				setFilters(new InputFilter[] { new InputFilter.LengthFilter(length()) });
			}
		}
	}

	boolean KEYCODE_DEL = false;

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// Log2.i(__CLASSNAME__, getMethodName() + v + "keycode:" + keyCode + ", event" + event);

		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Log2.e(__CLASSNAME__, getMethodName() + "keycode:" + keyCode + ", event" + event);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_DEL:
			case KeyEvent.KEYCODE_FORWARD_DEL:
				KEYCODE_DEL = true;
				break;

			default:
				KEYCODE_DEL = false;
				// if (event.getAction() == KeyEvent.ACTION_DOWN) {
				// int position = getSelectionStart();
				// if (position < getRecipientsSpanLength()) {
				// setSelection(getRecipientsSpanLength());
				// }
				// }
				break;
			}
		} else {
			switch (keyCode) {
			case KeyEvent.KEYCODE_DEL:
				KEYCODE_DEL = true;
				break;

			default:
				KEYCODE_DEL = false;
				// if (event.getAction() == KeyEvent.ACTION_DOWN) {
				// int position = getSelectionStart();
				// if (position < getRecipientsSpanLength()) {
				// setSelection(getRecipientsSpanLength());
				// }
				// }
				break;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// Log2.i(__CLASSNAME__, getMethodName() + "keycode:" + keyCode + ", event" + event);

		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// Log2.e(__CLASSNAME__, getMethodName() + v + "actionId:" + actionId + ", event" + event);

		return false;
	}

	/*
	 * TextWatcher, If user type any country name and press comma then following code will regenerate
	 * chips
	 */
	boolean block = false;
	private final TextWatcher textWather = new TextWatcher() {

		int start = 0;

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// Log2.e(__CLASSNAME__, getMethodName() + s + " - start:" + start + ", after:" + after + ", count:" + count);
			this.start = start;
			if (!block) {
				mMessage = getMessageText();
			}
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// Log2.e(__CLASSNAME__, getMethodName() + s + " - start:" + start + ", before:" + before + ", count:" + count);
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (!block && KEYCODE_DEL) {
				ImageSpan[] spans = s.getSpans(0, s.length(), ImageSpan.class);

				// Log2.e(__CLASSNAME__, getMethodName() + s + "spans:" + spans.length + ", chips:" + mRecipientChipsItems.size());

				if (spans.length < mRecipientChipsItems.size()) {
					// 수신자삭제처리
					int idx = getRecipientsSpanIndex(start);
					if (idx > -1) {
						ChipsItem chipsItem = mRecipientChipsItems.remove(idx);
						mRecipientSSB.removeSpan(chipsItem.getSpan());
						// Log2.i(__CLASSNAME__, getMethodName() + mRecipientChipsItems.size() + " - " + mRecipientSSB + " - deleted:" + chipsItem);
						setRecipients();
					}
				}
			}

			KEYCODE_DEL = false;

		}
	};

	public void clearRecipientChipsItems() {
		// Log2.i(__CLASSNAME__, getMethodName());

		mRecipientChipsItems.clear();
		mRecipients = "";
	}

	public void addRecipientChipsItem(ChipsItem chipItem) {
		// Log2.i(__CLASSNAME__, getMethodName());

		mRecipientChipsItems.remove(chipItem);
		mRecipientChipsItems.add(chipItem);

		makeRecipients();
	}

	public void makeRecipients() {
		mRecipients = "";

		int idx = 0;
		for (ChipsItem chipsItem : mRecipientChipsItems) {
			String name = chipsItem.getName();

			// if (idx == mRecipientChipsItems.size() - 1) {
			// mRecipients += name;
			// } else {
			// mRecipients += name + ",";
			// }
			mRecipients += name + __TOKENIZER__;

			// _Log.i(__CLASSNAME__, getMethodName() + "ChipsItem -> " + uid + "," + name);
			idx++;
		}

		// Log2.i(__CLASSNAME__, getMethodName() + idx + " - " + mRecipients);
	}

	/**
	 * <pre>
	 * 	capture bitmapt of genreated chipview
	 * </pre>
	 */
	BitmapDrawable createChipsBubble(View chipView) {
		// Log2.i(__CLASSNAME__, getMethodName());

		int spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

		chipView.measure(spec, spec);
		chipView.layout(0, 0, chipView.getMeasuredWidth(), chipView.getMeasuredHeight());
		Bitmap b = Bitmap.createBitmap(chipView.getWidth(), chipView.getHeight(),
				Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(b);
		canvas.translate(-chipView.getScrollX(), -chipView.getScrollY());
		chipView.draw(canvas);

		chipView.setDrawingCacheEnabled(true);

		Bitmap cacheBmp = chipView.getDrawingCache();
		Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);

		chipView.destroyDrawingCache(); // destory drawable

		// create bitmap drawable for imagespan
		BitmapDrawable bmpDrawable = new BitmapDrawable(viewBmp);
		bmpDrawable.setBounds(0, 0, bmpDrawable.getIntrinsicWidth(), bmpDrawable.getIntrinsicHeight());

		return bmpDrawable;
	}

	public String getMessageText() {

		String message = getText().toString();

		// Log2.e(__CLASSNAME__, getMethodName() + message);

		// for (ChipsItem chipsItem : mRecipientChipsItems) {
		// String name = chipsItem.getName();
		// message = message.replace(name, "");
		// }
		// 수신자길이확인
		if (mRecipientChipsItems.size() > 0) {
			int start = getRecipientsSpanLength();
			if (start < message.length()) {
				message = message.substring(start);
			} else {
				message = "";
			}
		}

		// Log2.e(__CLASSNAME__, getMethodName() + message);

		return message;
	}

	public void setRecipients() {

		// Log2.i(__CLASSNAME__, getMethodName());

		block = true;

		makeRecipients();

		// Log2.e(__CLASSNAME__, getMethodName() + mRecipients + " - " + mMessage);

		mRecipientSSB = new SpannableStringBuilder(mRecipients + mMessage);

		if (mRecipientChipsItems.size() > 0) {

			int x = 0;

			// loop will generate ImageSpan for every country name separated by comma
			for (ChipsItem chipsItem : mRecipientChipsItems) {

				String uid = chipsItem.getId();
				String name = chipsItem.getName();

				// inflate chips_edittext layout
				LayoutInflater lf = (LayoutInflater) getContext().getSystemService(
						Activity.LAYOUT_INFLATER_SERVICE);

				ViewGroup chipView = (ViewGroup) lf.inflate(R.layout.chips_edittext, null);

				TextView textView = (TextView) chipView.findViewById(R.id.edtTxt1);
				textView.setText(name); // set text

				ImageView imageView = (ImageView) chipView.findViewById(R.id.imageView1);
				imageView.setImageDrawable(chipsItem.getImage());

				// LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)chipView.getLayoutParams();
				// params.rightMargin = 10;
				// chipView.setLayoutParams(params);

				// capture bitmapt of genreated chipview
				// create and set imagespan
				ImageSpan span = new ImageSpan(createChipsBubble(chipView));
				mRecipientSSB.setSpan(span, x, x + name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				chipsItem.setSpan(span);

				// Log2.i(__CLASSNAME__, getMethodName() + "ChipsItem -> " + uid + "," + name);

				// increase position
				x = x + name.length() + __TOKENIZER__.length();

			}

		}

		// set chips span
		setText(mRecipientSSB);

		// take messge part message = text - recipients
		mMessage = getMessageText();

		// move cursor to last
		setSelection(getText().length());

		if (getSelectionStart() < getRecipientsSpanLength()) {
			setEditable(false);
		} else {
			setEditable(true);
		}

		block = false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ChipsItem chipItem = (ChipsItem) getAdapter().getItem(position);

		addRecipientChipsItem(chipItem);
		setRecipients(); // call generate chips when user select any item from auto complete
	}

}
