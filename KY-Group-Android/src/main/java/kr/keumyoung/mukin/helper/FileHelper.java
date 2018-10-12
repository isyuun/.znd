package kr.keumyoung.mukin.helper;

import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.util.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.inject.Inject;

/**
 *  on 06/03/18.
 * Project: KyGroup
 */
// unused
public class FileHelper {

    @Inject
    public FileHelper() {
        MainApplication.getInstance().getMainComponent().inject(this);
    }

    public String getStringFromFile(String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        fin.close();
        return ret;
    }

    public ArrayList<String> getStringArray(String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        ArrayList<String> ret = convertStreamToStringArray(fin);
        fin.close();
        return ret;
    }

    private ArrayList<String> convertStreamToStringArray(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Constants.ENCODING_EUC_KR));
        StringBuilder sb = new StringBuilder();
        ArrayList<String> strings = new ArrayList<>();
        String line = null;
        int count = 0;
        while ((line = reader.readLine()) != null) {
            count += 1;
            if (count <= 6) continue;

            line = line.replace("\r", "");
            line = line.replace("\n", "");
            strings.add(line);
        }
        reader.close();
        return strings;
    }

    public String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) sb.append(line);
        reader.close();
        return sb.toString();
    }
}
