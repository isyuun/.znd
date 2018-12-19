/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.kymedia.karaoke.widget.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.util.EntityUtils;
import kr.kymedia.karaoke.util.EnvironmentUtils;
import kr.kymedia.karaoke.util.HttpSync;
import kr.kymedia.karaoke.util.TextUtil;

/**
 * Helper class for fetching and disk-caching images from the web.
 */
public class BitmapUtils2 {
    final private static String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    protected String getMethodName() {
        String name = Thread.currentThread().getStackTrace()[3].getMethodName();
        String text = String.format("%s()", name);
        // int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
        // text = String.format("line:%d - %s() ", line, name);
        return text;
    }

    private static final String LOG_TAG = "BitmapUtils";

    // TODO: for concurrent connections, DefaultHttpClient isn't great, consider
    // other options
    // that still allow for sharing resources across bitmap fetches.

    public static interface OnFetchCompleteListener {
        public void onFetchComplete(Object cookie, Bitmap result);
    }

    /**
     * Only call this method from the main (UI) thread. The {@link OnFetchCompleteListener} callback
     * be invoked on the UI thread, but image fetching will be done in an {@link AsyncTask}.
     */
    public static void fetchImage(final Context context, final String url,
                                  final OnFetchCompleteListener callback) {
        fetchImage(context, url, null, null, callback);
    }

    private static Bitmap getBitmapFromCacheFile(final Context context, final String url) {
        Bitmap cachedBitmap = null;
        BitmapFactory.Options decodeOptions = null;
        // First compute the cache key and cache file path for this URL
        File cacheFile = null;
        try {
            MessageDigest mDigest = MessageDigest.getInstance("SHA-1");
            mDigest.update(url.getBytes());
            final String cacheKey = bytesToHexString(mDigest.digest());
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                // cacheFile = new File(Environment.getExternalStorageDirectory() + File.separator + "Android"
                // + File.separator + "data" + File.separator + context.getPackageName() + File.separator
                // + "cache" + File.separator + "bitmap_" + cacheKey + ".tmp");
                cacheFile = new File(EnvironmentUtils.getCachePath(context) + "bitmap_" + cacheKey + ".tmp");
            }
            if (cacheFile != null && cacheFile.exists()) {
                cachedBitmap = BitmapFactory.decodeFile(cacheFile.toString(), decodeOptions);
            }
        } catch (Exception e) {
            // Oh well, SHA-1 not available (weird), don't cache bitmaps.
            Log.e(__CLASSNAME__, Log.getStackTraceString(e));
        }

        return cachedBitmap;
    }

    // @Deprecated
    // private static Bitmap getBitmapFromCacheFile(final Context context, final String url, int w,
    // int h, BitmapFactory.Options decodeOptions) {
    // Bitmap cachedBitmap = null;
    // //BitmapFactory.Options decodeOptions = null;
    // // First compute the cache key and cache file path for this URL
    // File cacheFile = null;
    // try {
    // MessageDigest mDigest = MessageDigest.getInstance("SHA-1");
    // mDigest.update(url.getBytes());
    // final String cacheKey = bytesToHexString(mDigest.digest());
    // if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
    // // cacheFile = new File(Environment.getExternalStorageDirectory() + File.separator + "Android"
    // // + File.separator + "data" + File.separator + context.getPackageName() + File.separator
    // // + "cache" + File.separator + "bitmap_" + cacheKey + ".tmp");
    // cacheFile = new File(EnvironmentUtils.getCachePath(context) + "bitmap_" + cacheKey + ".tmp");
    // }
    // if (cacheFile != null && cacheFile.exists()) {
    // //cachedBitmap = BitmapFactory.decodeFile(cacheFile.toString(), decodeOptions);
    // cachedBitmap = SafeDecodeBitmapFile(cacheFile.toString(), w, h, true);
    // }
    // } catch (Exception e) {
    // // Oh well, SHA-1 not available (weird), don't cache bitmaps.
    // _Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
    // }
    //
    // return cachedBitmap;
    // }

    private static void putBitmapFromCacheFile(final Context context, final String url,
                                               final byte[] respBytes) {
        File cacheFile = null;
        try {
            MessageDigest mDigest = MessageDigest.getInstance("SHA-1");
            mDigest.update(url.getBytes());
            final String cacheKey = bytesToHexString(mDigest.digest());
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                // cacheFile = new File(Environment.getExternalStorageDirectory() + File.separator + "Android"
                // + File.separator + "data" + File.separator + context.getPackageName() + File.separator
                // + "cache" + File.separator + "bitmap_" + cacheKey + ".tmp");
                cacheFile = new File(EnvironmentUtils.getCachePath(context) + "bitmap_" + cacheKey + ".tmp");
            }
        } catch (Exception e) {
            // Oh well, SHA-1 not available (weird), don't cache bitmaps.
            Log.e(__CLASSNAME__, Log.getStackTraceString(e));
        }

        // Write response bytes to cache.
        if (cacheFile != null) {
            try {
                cacheFile.getParentFile().mkdirs();
                cacheFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(cacheFile);
                fos.write(respBytes);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.e(LOG_TAG, "Error writing to bitmap cache: " + cacheFile.toString(), e);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error writing to bitmap cache: " + cacheFile.toString(), e);
            }
        }
    }

    /**
     * Only call this method from the main (UI) thread. The {@link OnFetchCompleteListener} callback
     * be invoked on the UI thread, but image fetching will be done in an {@link AsyncTask}.
     *
     * @param cookie An arbitrary object that will be passed to the callback.
     */
    public static void fetchImage(final Context context, final String url,
                                  final BitmapFactory.Options decodeOptions, final Object cookie,
                                  final OnFetchCompleteListener callback) {
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                final String url = params[0];
                if (TextUtil.isEmpty(url)) {
                    return null;
                }

                // First compute the cache key and cache file path for this URL
                // File cacheFile = null;
                // try {
                // MessageDigest mDigest = MessageDigest.getInstance("SHA-1");
                // mDigest.update(url.getBytes());
                // final String cacheKey = bytesToHexString(mDigest.digest());
                // if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                // cacheFile = new File(Environment.getExternalStorageDirectory() + File.separator
                // + "Android" + File.separator + "data" + File.separator + context.getPackageName()
                // + File.separator + "cache" + File.separator + "bitmap_" + cacheKey + ".tmp");
                // }
                // } catch (NoSuchAlgorithmException e) {
                // // Oh well, SHA-1 not available (weird), don't cache bitmaps.
                // }
                //
                // if (cacheFile != null && cacheFile.exists()) {
                // Bitmap cachedBitmap = BitmapFactory.decodeFile(cacheFile.toString(), decodeOptions);
                // if (cachedBitmap != null) {
                // return cachedBitmap;
                // }
                // }
                Bitmap cachedBitmap = getBitmapFromCacheFile(context, url);
                if (cachedBitmap != null) {
                    return cachedBitmap;
                }

                try {
                    // TODO: check for HTTP caching headers
                    final HttpClient httpClient = HttpSync.getHttpClient(context.getApplicationContext());
                    final HttpResponse resp = httpClient.execute(new HttpGet(url));
                    final HttpEntity entity = resp.getEntity();

                    final int statusCode = resp.getStatusLine().getStatusCode();
                    if (statusCode != HttpStatus.SC_OK || entity == null) {
                        return null;
                    }

                    final byte[] respBytes = EntityUtils.toByteArray(entity);

                    // Write response bytes to cache.
                    // if (cacheFile != null) {
                    // try {
                    // cacheFile.getParentFile().mkdirs();
                    // cacheFile.createNewFile();
                    // FileOutputStream fos = new FileOutputStream(cacheFile);
                    // fos.write(respBytes);
                    // fos.close();
                    // } catch (FileNotFoundException e) {
                    // _Log.w(TAG, "Error writing to bitmap cache: " + cacheFile.toString(), e);
                    // } catch (IOException e) {
                    // _Log.w(TAG, "Error writing to bitmap cache: " + cacheFile.toString(), e);
                    // }
                    // }
                    putBitmapFromCacheFile(context, url, respBytes);

                    // Decode the bytes and return the bitmap.
                    return BitmapFactory.decodeByteArray(respBytes, 0, respBytes.length, decodeOptions);
                } catch (Exception e) {
                    Log.w(LOG_TAG, "Problem while loading image: " + e.toString(), e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                callback.onFetchComplete(cookie, result);
            }
        }.execute(url);
    }

    private static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * @param v
     * @param url
     * @return
     */
    @Deprecated
    public static ImageView putURLImage(final Context context, ImageView v, String url,
                                        boolean resize, int res) {

        if (v == null) {
            return null;
        }

        if (TextUtil.isEmpty(url)) {
            return null;
        }

        try {
            if (!TextUtil.isEmpty(url)) {
                putURLImage(context, v, url, resize);
            } else {
                v.setImageResource(res);
            }
        } catch (Exception e) {

            // _Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
            v.setImageResource(res);
        }
        return v;
    }

    public static ImageView putURLImage(final Context context, ImageView v, String url,
                                        boolean resize, Drawable drawable) {
        if (v == null) {
            return null;
        }

        if (TextUtil.isEmpty(url)) {
            return null;
        }

        try {
            if (!TextUtil.isEmpty(url)) {
                putURLImage(context, v, url, resize);
            } else {
                v.setImageDrawable(drawable);
            }
        } catch (Exception e) {

            // _Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
            v.setImageDrawable(drawable);
        }
        return v;
    }

    public static ImageView putURLImage(final Context context, final ImageView v, String url,
                                        final boolean resize) {

        if (v == null) {
            return null;
        }

        if (TextUtil.isEmpty(url)) {
            return null;
        }

        try {
            if (URLUtil.isNetworkUrl(url)) {
                fetchImage(context, url, null, null, new OnFetchCompleteListener() {
                    public void onFetchComplete(Object cookie, Bitmap result) {
                        if (result != null) {
                            // Bitmap bmp = SafeDecodeBitmapFile(context, result);
                            Bitmap bm = result;
                            v.setImageBitmap(bm);

                            if (resize) {
                                resizeImageView2Bitmap(v, bm);
                            }

                        }
                    }
                });
            } else if (((File) new File(url)).exists()) {
                Uri uri = null;
                if (url.contains("file://")) {
                    uri = Uri.parse(url);
                } else {
                    uri = Uri.parse("file://" + url);
                }
                v.setImageURI(uri);
            }
        } catch (Exception e) {

            // _Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
        }

        return v;
    }

    // static OnFetchCompleteListener mFetchCompleteListener = new OnFetchCompleteListener() {
    // public void onFetchComplete(Object cookie, Bitmap result) {
    // if (result != null) {
    // //v.setImageBitmap(result);
    // d = new BitmapDrawable(result);
    // }
    // }
    // };

    /**
     * @author isyoon
     */
    public static enum Position {
        left,
        top,
        right,
        bottom,
    }

    public static TextView putURLCompoundDrawable(final Context context, final TextView v,
                                                  final String url, final Position pos) {

        try {
            // if (!TextUtil.isEmpty(url)) {
            if (URLUtil.isNetworkUrl(url)) {
                fetchImage(context, url, null, null, new OnFetchCompleteListener() {
                    public void onFetchComplete(Object cookie, Bitmap result) {
                        if (result != null) {
                            // Drawable d = new BitmapDrawable(result);
                            Drawable d = new BitmapDrawable(context.getResources(), result);
                            if (d != null) {
                                d.setBounds(new Rect(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight()));
                                Drawable ds[] = v.getCompoundDrawables();
                                if (pos == Position.left) {
                                    ds[0] = d;
                                } else if (pos == Position.top) {
                                    ds[1] = d;
                                } else if (pos == Position.right) {
                                    ds[2] = d;
                                } else if (pos == Position.bottom) {
                                    ds[3] = d;
                                }
                                v.setCompoundDrawables(ds[0], ds[1], ds[2], ds[3]);
                            }
                        }
                    }
                });
            } else if (((File) new File(url)).exists()) {
                Drawable d = Drawable.createFromPath(url);
                if (d != null) {
                    d.setBounds(new Rect(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight()));
                    Drawable ds[] = v.getCompoundDrawables();
                    if (pos == Position.left) {
                        ds[0] = d;
                    } else if (pos == Position.top) {
                        ds[1] = d;
                    } else if (pos == Position.right) {
                        ds[2] = d;
                    } else if (pos == Position.bottom) {
                        ds[3] = d;
                    }
                    v.setCompoundDrawables(ds[0], ds[1], ds[2], ds[3]);
                }
            }
        } catch (Exception e) {

            // _Log.e(__CLASSNAME__, _Log.getStackTraceString(e));
        }

        return v;
    }

    static public void resizeImageView2Bitmap(ImageView v, Bitmap bm) {
        float h1, w1, h2, w2;
        h1 = bm.getHeight();
        w1 = bm.getWidth();
        h2 = v.getHeight();
        w2 = v.getWidth();
        // _Log.e("RESIZE", "[BF]->" + w1 + ":" + h1 + " = " + w2 + ":" + h2);
        if (h1 > w1) {
            h2 = (h1 / w1) * w2;
        } else if (h1 < w1) {
            h2 = w2 / (w1 / h1);
        } else {
            h2 = w2;
        }
        LayoutParams params = (LayoutParams) v.getLayoutParams();
        params.height = (int) (h2 > 0 ? h2 : params.height);
        v.setLayoutParams(params);
        // _Log.e("RESIZE", "[AF]->" + w1 + ":" + h1 + " = " + w2 + ":" + h2);
    }

    public static Bitmap putSafeDecodeBitmap(Context context, ImageView im, Uri uri) {
        Log.i(__CLASSNAME__, "putSafeDecodeBitmap(...)");

        Bitmap bm = SafeDecodeBitmapFile(context, uri);

        im.setImageBitmap(bm);

        return bm;
    }

    public static Bitmap putSafeDecodeBitmap(Context context, ImageView v, Uri uri, boolean resize) {
        Log.i(__CLASSNAME__, "putSafeDecodeBitmap(...)");

        Bitmap bm = SafeDecodeBitmapFile(context, uri);

        v.setImageBitmap(bm);

        if (resize) {
            resizeImageView2Bitmap(v, bm);
        }

        return bm;
    }

    public static Bitmap putSafeDecodeBitmap(Context context, ImageView v, Uri uri, boolean resize,
                                             String path, CompressFormat format, int quality) {
        Log.i(__CLASSNAME__, "putSafeDecodeBitmap(...)");

        Bitmap bm = SafeDecodeBitmapFile(context, uri);

        v.setImageBitmap(bm);

        if (resize) {
            resizeImageView2Bitmap(v, bm);
        }

        if (!TextUtil.isEmpty(path)) {
            putSaveDecodeBitmap(bm, path, format, quality);
        }

        return bm;
    }

    final static int QUALITY_DEFAULT = 50;

    public static Uri putSaveDecodeBitmap(Bitmap bm, String path, CompressFormat format, int quality) {

        Uri uri = null;

        if (bm != null && !TextUtil.isEmpty(path)) {
            FileOutputStream fo = null;
            try {

                if (quality > 100) {
                    quality = QUALITY_DEFAULT;
                } else if (quality < 0) {
                    quality = QUALITY_DEFAULT;
                }

                // uri = Uri.parse(path);
                if (path.contains("file://")) {
                    uri = Uri.parse(path);
                } else {
                    uri = Uri.parse("file://" + path);
                }
                fo = new FileOutputStream(new File(uri.getPath()));
                bm.compress(format, quality, fo);
                fo.flush();
                fo.close();
            } catch (Exception e) {
                Log.e(__CLASSNAME__, Log.getStackTraceString(e));
            }
        }

        return uri;
    }

    public static void putSafeDecodeBitmap(ImageView v, String filename, boolean resize) {

        int w = v.getWidth();
        int h = v.getHeight();

        Bitmap bm = SafeDecodeBitmapFile(filename, w, h, true);

        v.setImageBitmap(bm);

        if (resize) {
            resizeImageView2Bitmap(v, bm);
        }

        if (bm != null) {
            bm.recycle();
            bm = null;
        }
    }

    // SafeDecodeBitmapFile
    // File 에서 이미지를 불러올 때 안전하게 불러오기 위해서 만든 함수
    // bitmap size exceeds VM budget 오류 방지용
    public static Bitmap SafeDecodeBitmapFile(String filename, int width, int height, boolean exact) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);
        if (options.outHeight > 0 && options.outWidth > 0) {
            options.inJustDecodeBounds = false;
            options.inSampleSize = 2;
            while (options.outWidth / options.inSampleSize > width
                    && options.outHeight / options.inSampleSize > height) {
                options.inSampleSize++;
            }
            options.inSampleSize--;

            bitmap = BitmapFactory.decodeFile(filename, options);
            if (bitmap != null && exact) {
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
            }
        }
        return bitmap;
    }

    // SafeDecodeBitmapFile
    // File 에서 이미지를 불러올 때 안전하게 불러오기 위해서 만든 함수
    // bitmap size exceeds VM budget 오류 방지용
    public static Bitmap SafeDecodeBitmapFile(Context context, Bitmap org) {

        ByteArrayInputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            // in = mContentResolver.openInputStream(uri);
            // in = context.getContentResolver().openInputStream(bmp);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            org.compress(CompressFormat.PNG, 0 /* ignored for PNG */, bos);
            byte[] bitmapdata = bos.toByteArray();
            in = new ByteArrayInputStream(bitmapdata);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d(__CLASSNAME__, "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: "
                    + o.outHeight);

            Bitmap bm = null;
            in = new ByteArrayInputStream(bitmapdata);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                bm = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = bm.getHeight();
                int width = bm.getWidth();
                Log.d(__CLASSNAME__, "1th scale operation dimenions - width: " + width + ", height: "
                        + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bm, (int) x, (int) y, true);
                if (bm != null) {
                    bm.recycle();
                    bm = null;
                }
                bm = scaledBitmap;

                System.gc();
            } else {
                bm = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d(__CLASSNAME__, "bitmap size - width: " + bm.getWidth() + ", height: " + bm.getHeight());
            return bm;
        } catch (Exception e) {
            Log.e(__CLASSNAME__, Log.getStackTraceString(e), e);
            return null;
        }
    }

    // SafeDecodeBitmapFile
    // File 에서 이미지를 불러올 때 안전하게 불러오기 위해서 만든 함수
    // bitmap size exceeds VM budget 오류 방지용
    public static Bitmap SafeDecodeBitmapFile(Context context, Uri uri) {

        // String path = TextUtil.getRealPathFromURI(context.getContentResolver(), uri);
        // File file = new File(path);
        // if (file == null || !file.exists()) {
        // return null;
        // }

        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            // in = mContentResolver.openInputStream(uri);
            in = context.getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d(__CLASSNAME__, "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: "
                    + o.outHeight);

            Bitmap bm = null;
            in = context.getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                bm = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = bm.getHeight();
                int width = bm.getWidth();
                Log.d(__CLASSNAME__, "1th scale operation dimenions - width: " + width + ", height: "
                        + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bm, (int) x, (int) y, true);
                if (bm != null) {
                    bm.recycle();
                    bm = null;
                }
                bm = scaledBitmap;

                System.gc();
            } else {
                bm = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d(__CLASSNAME__, "bitmap size - width: " + bm.getWidth() + ", height: " + bm.getHeight());
            return bm;
        } catch (Exception e) {
            Log.e(__CLASSNAME__, Log.getStackTraceString(e), e);
            return null;
        }
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 12;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Bitmap getRoundedCornerBitmap(Context context, Bitmap input, int pixels, int w,
                                                int h, boolean squareTL, boolean squareTR, boolean squareBL, boolean squareBR) {

        Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);

        // make sure that our rounded corner is scaled appropriately
        final float roundPx = pixels * densityMultiplier;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        // draw rectangles over the corners we want to be square
        if (squareTL) {
            canvas.drawRect(0, 0, w / 2, h / 2, paint);
        }
        if (squareTR) {
            canvas.drawRect(w / 2, 0, w, h / 2, paint);
        }
        if (squareBL) {
            canvas.drawRect(0, h / 2, w / 2, h, paint);
        }
        if (squareBR) {
            canvas.drawRect(w / 2, h / 2, w, h, paint);
        }

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(input, 0, 0, paint);

        return output;
    }

    public static Bitmap getRoundedCornerBitmap(Context context, Bitmap input, int pixels, int w,
                                                int h, boolean squareTL, boolean squareTR, boolean squareBL, boolean squareBR, int m) {

        Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0 + m, 0 + m, w - m, h - m);
        final RectF rectF = new RectF(rect);

        // make sure that our rounded corner is scaled appropriately
        final float roundPx = pixels * densityMultiplier;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        // draw rectangles over the corners we want to be square
        if (squareTL) {
            canvas.drawRect(0, 0, w / 2, h / 2, paint);
        }
        if (squareTR) {
            canvas.drawRect(w / 2, 0, w, h / 2, paint);
        }
        if (squareBL) {
            canvas.drawRect(0, h / 2, w / 2, h, paint);
        }
        if (squareBR) {
            canvas.drawRect(w / 2, h / 2, w, h, paint);
        }

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(input, 0, 0, paint);

        return output;
    }

    // Props to George Walters II above, I just took his answer and extended it a bit to support rounding individual corners differently. This could be optimized a bit further (some of the target rects overlap), but not a whole lot.
    //
    // I know this thread is a bit old, but its one of the top results for queries on Google for how to round corners of ImageViews on Android.
    // /**
    // * Use this method to scale a bitmap and give it specific rounded corners.
    // *
    // * @param context
    // * Context object used to ascertain display density.
    // * @param bitmap
    // * The original bitmap that will be scaled and have rounded corners applied to it.
    // * @param upperLeft
    // * Corner radius for upper left.
    // * @param upperRight
    // * Corner radius for upper right.
    // * @param lowerRight
    // * Corner radius for lower right.
    // * @param lowerLeft
    // * Corner radius for lower left.
    // * @param endWidth
    // * Width to which to scale original bitmap.
    // * @param endHeight
    // * Height to which to scale original bitmap.
    // * @return Scaled bitmap with rounded corners.
    // */
    // @Deprecated
    // public static Bitmap getRoundedCornerBitmap(Context context, Bitmap bitmap, float upperLeft,
    // float upperRight, float lowerRight, float lowerLeft, int endWidth, int endHeight) {
    // float densityMultiplier = context.getResources().getDisplayMetrics().density;
    //
    // // scale incoming bitmap to appropriate px size given arguments and display dpi
    // bitmap = Bitmap.createScaledBitmap(bitmap, Math.round(endWidth * densityMultiplier),
    // Math.round(endHeight * densityMultiplier), true);
    //
    // // create empty bitmap for drawing
    // Bitmap output = Bitmap.createBitmap(Math.round(endWidth * densityMultiplier),
    // Math.round(endHeight * densityMultiplier), Config.ARGB_8888);
    //
    // // get canvas for empty bitmap
    // Canvas canvas = new Canvas(output);
    // int width = canvas.getWidth();
    // int height = canvas.getHeight();
    //
    // // scale the rounded corners appropriately given dpi
    // upperLeft *= densityMultiplier;
    // upperRight *= densityMultiplier;
    // lowerRight *= densityMultiplier;
    // lowerLeft *= densityMultiplier;
    //
    // Paint paint = new Paint();
    // paint.setAntiAlias(true);
    // paint.setColor(Color.WHITE);
    //
    // // fill the canvas with transparency
    // canvas.drawARGB(0, 0, 0, 0);
    //
    // // draw the rounded corners around the image rect. clockwise, starting in upper left.
    // canvas.drawCircle(upperLeft, upperLeft, upperLeft, paint);
    // canvas.drawCircle(width - upperRight, upperRight, upperRight, paint);
    // canvas.drawCircle(width - lowerRight, height - lowerRight, lowerRight, paint);
    // canvas.drawCircle(lowerLeft, height - lowerLeft, lowerLeft, paint);
    //
    // // fill in all the gaps between circles. clockwise, starting at top.
    // RectF rectT = new RectF(upperLeft, 0, width - upperRight, height / 2);
    // RectF rectR = new RectF(width / 2, upperRight, width, height - lowerRight);
    // RectF rectB = new RectF(lowerLeft, height / 2, width - lowerRight, height);
    // RectF rectL = new RectF(0, upperLeft, width / 2, height - lowerLeft);
    //
    // canvas.drawRect(rectT, paint);
    // canvas.drawRect(rectR, paint);
    // canvas.drawRect(rectB, paint);
    // canvas.drawRect(rectL, paint);
    //
    // // set up the rect for the image
    // Rect imageRect = new Rect(0, 0, width, height);
    //
    // // set up paint object such that it only paints on Color.WHITE
    // paint.setXfermode(new AvoidXfermode(Color.WHITE, 255, AvoidXfermode.Mode.TARGET));
    //
    // // draw resized bitmap onto imageRect in canvas, using paint as configured above
    // canvas.drawBitmap(bitmap, imageRect, imageRect, paint);
    //
    // return output;
    // }
}
