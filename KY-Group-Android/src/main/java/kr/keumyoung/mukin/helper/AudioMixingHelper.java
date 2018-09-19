package kr.keumyoung.mukin.helper;

import android.content.Context;
import android.util.Log;

import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.activity.PlayerActivity;
import kr.keumyoung.mukin.data.model.Song;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 *  on 23/03/18.
 * Project: KyGroup
 */
// unused
public class AudioMixingHelper {

    @Inject
    Context context;

    PlayerActivity instance;
    Song song;

    String recordedFilePath;

    final int BUFFER = 2048;

    public enum MusicFileDescriptor {
        RECORDING, MUSIC, ZIP
    }

    @Inject
    public AudioMixingHelper() {
        MainApplication.getInstance().getMainComponent().inject(this);
    }

    FileOutputStream os;

    public void initialize(PlayerActivity activity, Song song) {
        this.instance = activity;
        this.song = song;

        prepareAudioRecorder();
    }

    private void prepareAudioRecorder() {
        recordedFilePath = ImageUtils.BASE_PATH + song.getIdentifier() + ".pcm";

        try {
            File file = new File(recordedFilePath);
            if (!file.exists()) System.out.println(file.createNewFile());

            os = new FileOutputStream(recordedFilePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeFile(short[] data) {
        try {
            byte bData[] = short2byte(data);
            os.write(bData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];

        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;
    }

    private void closeFile() {
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Observable<String> saveFile() {
        return Observable.create(subscriber -> {
            closeFile();
            subscriber.onComplete();
        });
    }

    private File compressFile(Song song, File musicFile, File recordedFile) {
        try {
            String toLocation = ImageUtils.BASE_PATH + song.getIdentifier() + ".zip";

            String[] files = new String[2];
            files[0] = musicFile.getAbsolutePath();
            files[1] = recordedFile.getAbsolutePath();

            zip(files, toLocation);

            return new File(toLocation);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void zip(String[] _files, String zipFileName) {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            byte data[] = new byte[BUFFER];

            for (String _file : _files) {
                Log.v("Compress", "Adding: " + _file);
                FileInputStream fi = new FileInputStream(_file);
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(_file.substring(_file.lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
