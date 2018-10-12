package kr.keumyoung.mukin.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.DisplayMetrics;

import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.exif.ExifInterface;

import java.io.IOException;

public class ImageCompressionUtility {

    @SuppressWarnings("deprecation")
    public static Bitmap compressImage(Context activity, String imageUri) {

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) activity).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int deviceWidth = metrics.widthPixels;
        int deviceheight = metrics.heightPixels;

        Bitmap scaledBitmap = null;
        Bitmap bmp = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bmp = BitmapFactory.decodeFile(imageUri, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        float maxHeight = deviceheight;
        float maxWidth = deviceWidth;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(imageUri, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {

            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
            try {
                System.gc();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {

                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError e1) {
                e1.printStackTrace();
                try {
                    System.gc();
                    System.gc();
                    System.gc();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        if (scaledBitmap != null) {
            try {
                Canvas canvas = new Canvas(scaledBitmap);
                canvas.setMatrix(scaleMatrix);
                canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
            } catch (OutOfMemoryError e) {
                try {
                    System.gc();
                } catch (Exception e2) {
                    e.printStackTrace();
                }

                Canvas canvas = new Canvas(scaledBitmap);
                canvas.setMatrix(scaleMatrix);
                canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        } else {
            // scaledBitmap = Bitmap.createBitmap(bmp);
        }

        ExifInterface exif;
        Matrix matrix = null;
        try {
            exif = new ExifInterface();
            exif.readExif(imageUri);
            int imageOrientation = 0;
            Integer exifOrientation = exif.getTagIntValue(ExifInterface.TAG_ORIENTATION);
            if (exifOrientation != null) {
                if (exifOrientation == 6) {
                    imageOrientation = 90;
                } else if (exifOrientation == 8) {
                    imageOrientation = 270;
                } else if (exifOrientation == 3) {
                    imageOrientation = 180;
                } else if (exifOrientation == 1) {
                    imageOrientation = 0;
                }
            }
            if (imageOrientation != 0) {
                matrix = rotate((matrix == null ? new Matrix() : matrix), imageOrientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (NullPointerException e) {

        }

        try {
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return scaledBitmap;

    }

    @SuppressWarnings("deprecation")
    public static Bitmap compressImage(Activity activity, byte[] data) {
        try {
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int deviceWidth = metrics.widthPixels;
            int deviceheight = metrics.heightPixels;


            Bitmap scaledBitmap = null;
            Bitmap bmp = null;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);//File(imageUri, options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;
            // float maxHeight = 816.0f;
            // float maxWidth = 612.0f;
            float maxHeight = deviceheight;
            float maxWidth = deviceWidth;
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
                bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);//decodeFile(imageUri, options);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();

            }
            try {

                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
                try {
                    System.gc();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {

                    scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
                } catch (OutOfMemoryError e1) {
                    e1.printStackTrace();
                    try {
                        System.gc();
                        System.gc();
                        System.gc();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
            if (scaledBitmap != null) {
                try {
                    Canvas canvas = new Canvas(scaledBitmap);
                    canvas.setMatrix(scaleMatrix);
                    canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
                } catch (OutOfMemoryError e) {
                    try {
                        System.gc();
                    } catch (Exception e2) {
                        e.printStackTrace();
                    }

                    Canvas canvas = new Canvas(scaledBitmap);
                    canvas.setMatrix(scaleMatrix);
                    canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            } else {
                // scaledBitmap = Bitmap.createBitmap(bmp);
            }

            ExifInterface exif;
            Matrix matrix = null;
            try {
                exif = new ExifInterface();
                exif.readExif(data);
                int imageOrientation = 0;
                Integer exifOrientation = exif.getTagIntValue(ExifInterface.TAG_ORIENTATION);
                if (exifOrientation != null) {
                    if (exifOrientation == 6) {
                        imageOrientation = 90;
                    } else if (exifOrientation == 8) {
                        imageOrientation = 270;
                    } else if (exifOrientation == 3) {
                        imageOrientation = 180;
                    } else if (exifOrientation == 1) {
                        imageOrientation = 0;
                    }
                }
                if (imageOrientation != 0) {
                    matrix = rotate((matrix == null ? new Matrix() : matrix), imageOrientation);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                System.gc();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return scaledBitmap;

        } catch (NullPointerException e) {
            e.printStackTrace();
            return BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher);
        }

    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    private static Matrix rotate(Matrix input, int degree) {
        input.setRotate(degree);

        return (input);
    }
}
