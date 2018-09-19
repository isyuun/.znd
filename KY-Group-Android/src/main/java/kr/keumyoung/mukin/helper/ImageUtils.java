package kr.keumyoung.mukin.helper;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import kr.keumyoung.mukin.activity.BaseActivity;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


/**
 *  on 16/05/16.
 */
public class ImageUtils {
    public static final int REQUEST_CAPTURE = 1;
    public static final int REQUEST_GALLERY = 2;
    public static final int REQUEST_GALLERY_VIDEO = 4;
    public static final String BASE_PATH = Environment.getExternalStorageDirectory().toString() + "/KyGroup/";
    private String fileName;

    BaseActivity context;

    public ImageUtils(BaseActivity context) {
        this.context = context;
        checkForSdCardFolder();
    }

    private void copyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private File getFile(String tag, InputStream is) {
        File cacheDir = null;
        cacheDir = new File(BASE_PATH);
        if (!cacheDir.exists())
            cacheDir.mkdirs();

        File f = new File(cacheDir, tag);

        try {
            OutputStream os = new FileOutputStream(f);
            copyStream(is, os);
            os.close();
            return f;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public File retrieve(int requestCode, int resultCode, Intent data) {
        Bitmap bm = null;
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAPTURE) {
                File f = new File(BASE_PATH + "temp.jpg");
                fileName = f.toString();
                bm = ImageCompressionUtility.compressImage(context, f.getAbsolutePath());
                OutputStream fOut = null;
                try {
                    File file = new File(BASE_PATH, getNewFileName());
                    file.createNewFile();

                    //Convert bitmap to byte array
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    byte[] bitmapdata = bos.toByteArray();

                    //write the bytes in file
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();
                    return file;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_GALLERY || requestCode == REQUEST_GALLERY_VIDEO) {
                try {
                    final Uri imageUri = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = context.getContentResolver().query(imageUri, filePath, null, null, null);
                    c.moveToFirst();
                    String picturePath = c.getString(c.getColumnIndex(filePath[0]));
                    c.close();
                    if (picturePath.endsWith(".png") || picturePath.endsWith(".jpg") || picturePath.endsWith(".jpeg")) {
                        final InputStream imageStream = context.getContentResolver().openInputStream(imageUri);
                        File image = getFile(getNewFileName(), imageStream);
                        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                        bitmapOptions.inSampleSize = 4;
                        bm = ImageCompressionUtility.compressImage(context, image.getAbsolutePath());
                        File file = new File(BASE_PATH, getNewFileName());
                        file.createNewFile();

                        //Convert bitmap to byte array
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                        byte[] bitmapdata = bos.toByteArray();

                        //write the bytes in file
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(bitmapdata);
                        fos.flush();
                        fos.close();
                        return file;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public String getNewFileName() {
        String fileName = "";
        DateTime dateTime = new DateTime(DateTimeZone.UTC);
        fileName = Long.toString(dateTime.getMillis());
        return fileName + ".jpg";
    }

    public void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String folderPath = BASE_PATH + "/temp.jpg";
        File file = new File(folderPath);
        Uri imageUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        ((Activity) context).startActivityForResult(intent, REQUEST_CAPTURE);
    }

    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ((Activity) context).startActivityForResult(intent, REQUEST_GALLERY);
    }

    public void checkForSdCardFolder() {
        File file = new File(BASE_PATH);
        if (!file.exists()) file.mkdirs();
    }

    public static File getBaseFolder() {
        return new File(BASE_PATH);
    }
}
