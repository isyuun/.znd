package kr.keumyoung.mukin.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RSRuntimeException;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

public class RenderScriptBlur {
    public static Bitmap blur(Context context, Bitmap targetBitmap, int radius) throws RSRuntimeException {
        RenderScript renderScript = null;
        try {
            renderScript = RenderScript.create(context);
            Allocation input = Allocation.createFromBitmap(renderScript, targetBitmap,
                    Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
            Allocation output = Allocation.createTyped(renderScript, input.getType());
            ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));

            blur.setInput(input);
            blur.setRadius((radius > 25 ? 25 : radius));
            blur.forEach(output);
            output.copyTo(targetBitmap);

        } finally {
            if (renderScript != null) {
                renderScript.destroy();
            }
        }
        return targetBitmap;
    }
}