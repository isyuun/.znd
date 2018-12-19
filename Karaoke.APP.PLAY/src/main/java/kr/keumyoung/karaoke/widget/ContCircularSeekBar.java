package kr.keumyoung.karaoke.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.devadvance.circularseekbar._CircularSeekBar;

import kr.keumyoung.karaoke.play.BuildConfig;
import kr.keumyoung.karaoke.play.R;

public class ContCircularSeekBar extends RelativeLayout {

    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    protected String getMethodName() {
        String name = Thread.currentThread().getStackTrace()[3].getMethodName();
        // int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
        // name = String.format("line:%d - %s() ", line, name);
        name += "() ";
        return name;
    }


    public ContCircularSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ContCircularSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContCircularSeekBar(Context context) {
        super(context);
    }

    private _CircularSeekBar seek;

    @Override
    protected void onAttachedToWindow() {
        Log.wtf(__CLASSNAME__, getMethodName());
        super.onAttachedToWindow();
        seek = findViewById(R.id.seek_pitch_tempo);
        seek.setIsTouchEnabled(false);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(__CLASSNAME__, "onClick()" + view);
            }
        });
    }

    public void setMax(int max) {
        if (seek == null) seek = findViewById(R.id.seek_pitch_tempo);
        seek.setMax(max);
    }

    public void setProgress(int progress) {
        if (seek == null) seek = findViewById(R.id.seek_pitch_tempo);
        seek.setProgress(progress);
    }

    public interface OnCrossClickListener {
        void onUpClick(View v);
        void onDownClick(View v);
        void onLeftClick(View v);
        void onRightClick(View v);
        void onCenterClick(View v);
    }

    OnCrossClickListener listener;
    public void setOnCrossClickListener(OnCrossClickListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(__CLASSNAME__, getMethodName() + event);

        if (event.getAction() != MotionEvent.ACTION_DOWN) return super.onTouchEvent(event);

        float x = event.getX();
        float y = event.getY();
        int w = getWidth();
        int h = getHeight();

        //up
        if ((x > w * 1/3) && (x < w * 2/3) && y < h * 1/3) {
            //_Log.e(__CLASSNAME__, getMethodName() + "[UP]");
            if (this.listener != null) listener.onUpClick(this);
        } else if ((x > w * 1/3) && (x < w * 2/3) && y > h * 2/3) {
            //_Log.e(__CLASSNAME__, getMethodName() + "[DOWN]");
            if (this.listener != null) listener.onDownClick(this);
        } else if ((y > h * 1/3) && (y < h * 2/3) && x < w * 1/3) {
            //_Log.e(__CLASSNAME__, getMethodName() + "[LEFT]");
            if (this.listener != null) listener.onLeftClick(this);
        } else if ((y > h * 1/3) && (y < h * 2/3) && x > w * 2/3) {
            //_Log.e(__CLASSNAME__, getMethodName() + "[RIGHT]");
            if (this.listener != null) listener.onRightClick(this);
        } else if ((x > w * 1/3) && (x < w * 2/3) && (y > h * 1/3) && (y < h * 2/3)) {
            //_Log.e(__CLASSNAME__, getMethodName() + "[CENTER]");
            if (this.listener != null) listener.onCenterClick(this);
        }

        return super.onTouchEvent(event);
    }



    boolean show = true;
    public void show(boolean show) {
        this.show = show;
        if (show) {
            setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.INVISIBLE);
        }
        bringToFront();
        for (int i = 0 ; i < getChildCount() ; i++) bringChildToFront(getChildAt(i));
    }

    public boolean show() {
        return show;
    }

    public void toggle() {
        show(!this.show);
    }

    public boolean isLoading() {
        return seek.isLoading();
    }

    public void startLoading() {
        if (seek == null) seek = findViewById(R.id.seek_pitch_tempo);
        show(true);
        seek.startLoading();
    }

    public void stopLoading() {
        if (seek == null) seek = findViewById(R.id.seek_pitch_tempo);
        seek.stopLoading();
        show(false);
    }
}
