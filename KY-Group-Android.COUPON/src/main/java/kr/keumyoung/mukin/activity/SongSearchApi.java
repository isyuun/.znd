package kr.keumyoung.mukin.activity;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;

import kr.keumyoung.mukin.util.Constants;
import kr.keumyoung.mukin.util.SSLUtil;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SongSearchApi {

    private static SongApiService getApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_API_URL)
                .client(SSLUtil.getUnsafeOkHttpClient().build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        SongApiService apiService = retrofit.create(SongApiService.class);
        return apiService;
    }

    /**
     * 파일 다운로드
     *
     * @param downUrl
     * @param savePath
     * @param saveName
     * @param handler
     * @param what
     */
    public static void downloadFile(String downUrl, final String savePath, final String saveName, final Handler handler, final int what) {
        SongApiService apiService = getApiService();
        String apiVersion = "v1";

        final File path = new File(savePath);
        File dir = new File(path.getPath());

        if (!dir.exists()) {
            dir.mkdir();
        }
        File downFile = new File(path + File.separator + saveName);

        Call<ResponseBody> response = apiService.downloadSoundLib(downUrl);
        response.enqueue(new Callback<ResponseBody>() {
            //성공시
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    System.out.println("server contacted and has file");

                    //     final String strpath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    //     System.out.println("!!!---- strpath+File.separator+ savePath : "+strpath+File.separator+ savePath);
                    //     final File path = new File(strpath+File.separator+ savePath);

                    //new AsyncTask<Void, Void, Boolean>() {
                    //    @Override
                    //    protected Boolean doInBackground(Void... voids) {
                    //        boolean writtenToDisk = false;
                    //        try {
                    //            BufferedSource bufferedSource = response.body().source();
                    //            BufferedSink bufferedSink = Okio.buffer(Okio.sink(downFile));
                    //            bufferedSink.writeAll(bufferedSource);
                    //            bufferedSink.close();
                    //            writtenToDisk = true;
                    //        } catch (Exception e) {
                    //            writtenToDisk = false;
                    //        }
                    //
                    //        System.out.println("file download was a success? " + writtenToDisk);
                    //        return writtenToDisk;
                    //    }
                    //
                    //    @Override
                    //    protected void onPostExecute(Boolean result) {
                    //        super.onPostExecute(result);
                    //        Message msg = new Message();
                    //        msg.what = what;
                    //        handler.sendMessage(msg);
                    //    }
                    //}.execute();
                    try {
                        final BufferedSource bufferedSource = response.body().source();
                        final BufferedSink bufferedSink = Okio.buffer(Okio.sink(downFile));
                        //new AsyncTask<Void, Void, Boolean>() {
                        //    @Override
                        //    protected Boolean doInBackground(Void... voids) {
                        //        boolean writtenToDisk = false;
                        //        try {
                        //            bufferedSink.writeAll(bufferedSource);
                        //            bufferedSink.close();
                        //            writtenToDisk = true;
                        //        } catch (Exception e) {
                        //            writtenToDisk = false;
                        //        }
                        //
                        //        System.out.println("file download was a success? " + writtenToDisk);
                        //        return writtenToDisk;
                        //    }
                        //
                        //    @Override
                        //    protected void onPostExecute(Boolean result) {
                        //        super.onPostExecute(result);
                        //        Message msg = new Message();
                        //        msg.what = what;
                        //        handler.sendMessage(msg);
                        //    }
                        //}.execute();
                        new DownloadFileTask(handler, what, bufferedSink, bufferedSource).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    handler.sendEmptyMessage(Constants.API_ERROR_CODE);
                    System.out.println("server contact failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("!!-- downloadFile : " + t.getMessage());
                handler.sendEmptyMessage(Constants.API_ERROR_CODE);
            }
        });
    }

    private static class DownloadFileTask extends AsyncTask<Void, Void, Boolean> {
        final int what;
        final private WeakReference<Handler> handler;
        final private WeakReference<BufferedSink> bufferedSink;
        final private WeakReference<BufferedSource> bufferedSource;

        public DownloadFileTask(final Handler handler, final int what, final BufferedSink bufferedSink, final BufferedSource bufferedSource) {
            super();
            this.what = what;
            this.handler = new WeakReference<>(handler);
            this.bufferedSink = new WeakReference<>(bufferedSink);
            this.bufferedSource = new WeakReference<>(bufferedSource);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean writtenToDisk = false;
            try {
                final BufferedSink bufferedSink = this.bufferedSink.get();
                final BufferedSource bufferedSource = this.bufferedSource.get();
                bufferedSink.writeAll(bufferedSource);
                bufferedSink.close();
                writtenToDisk = true;
            } catch (Exception e) {
                writtenToDisk = false;
            }

            System.out.println("file download was a success? " + writtenToDisk);
            return writtenToDisk;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            final Handler handler = this.handler.get();
            Message msg = new Message();
            msg.what = what;
            handler.sendMessage(msg);
        }
    }
}
