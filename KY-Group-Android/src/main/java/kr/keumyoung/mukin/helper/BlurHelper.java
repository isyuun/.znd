package kr.keumyoung.mukin.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

import kr.keumyoung.mukin.MainApplication;

import javax.inject.Inject;

/**
 *  on 31/01/18.
 * Project: KyGroup
 */

public class BlurHelper {

    private RenderScript renderScript ;

    @Inject
    Context context;

    @Inject
    public BlurHelper() {
        MainApplication.getInstance().getMainComponent().inject(this);
        renderScript = RenderScript.create(context);
    }

    // this method should work in only background thread
    Bitmap blurBitmap(Bitmap input) {
        Bitmap outBitmap = Bitmap.createBitmap(input.getWidth(), input.getHeight(), input.getConfig());

        final Allocation inputAllocation = Allocation.createFromBitmap(renderScript, input);
        final Allocation output = Allocation.createFromBitmap(renderScript, outBitmap);

        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));

        script.setRadius(8f);
        script.setInput(inputAllocation);
        script.forEach(output);
        output.copyTo(input);

        input.recycle();

        return outBitmap;
    }
}
