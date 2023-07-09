package com.yong.wesave.asyntask;

/**
 * Created by Yong0156 on 28/2/2017.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import androidx.collection.LruCache;
import android.widget.ImageView;

import org.apache.http.protocol.HTTP;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncImageLoader implements Runnable {

    private static ExecutorService executor = Executors.newFixedThreadPool(3);
    private static LruCache<String, Bitmap> mMemoryCache;
    // private static Handler handler = new Handler();
    private ImageView imageView;
    private String imageUrl;
    private int maxWidth;
    private Activity activity;

    public AsyncImageLoader(Activity activity, ImageView imageView,
                            int photoDimension, String imageUrl) {
        this.imageView = imageView;
        this.activity = activity;
        this.maxWidth = photoDimension;
        this.imageUrl = imageUrl;
        imageView.setTag(imageUrl);
        initLruCache();
    }

    public void execute(String param) {
        executor.execute(this);
    }

    @Override
    public void run() {
        doInBackground();
    }

    private void doInBackground() {
        try {
            Bitmap existingBitmap = getBitmapFromMemCache(imageUrl);
            if (existingBitmap != null) {
                activity.runOnUiThread(new ImageDisplayer(existingBitmap, imageView,
                        imageUrl));
//				handler.post(new ImageDisplayer(existingBitmap, imageView,
//						imageUrl));
            }

            String pathToImage = checkImageCacheExist(imageUrl);

            if (pathToImage == null || pathToImage.equals("")) {
                pathToImage = downloadAndSaveImage(imageUrl);
            }

            if (maxWidth > 0) {
                BitmapFactory.Options bounds = new BitmapFactory.Options();
                bounds.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(pathToImage, bounds);

                int imgHeight = bounds.outHeight;
                int imgWidth = bounds.outWidth;
                int inSampleSize = 1;

                while (imgWidth / inSampleSize / 2 >= maxWidth
                        && imgHeight / inSampleSize / 2 >= maxWidth) {
                    inSampleSize *= 2;
                }

                BitmapFactory.Options resample = new BitmapFactory.Options();
                resample.inSampleSize = inSampleSize;
                Bitmap outputImage = BitmapFactory.decodeFile(pathToImage,
                        resample);

                if (outputImage == null) {
                    File imgFile = new File(pathToImage);
                    imgFile.delete();
                } else {
                    addBitmapToMemoryCache(imageUrl, outputImage);
                }

                activity.runOnUiThread(new ImageDisplayer(outputImage, imageView,
                        imageUrl));
            } else {
                Bitmap outputImage = BitmapFactory.decodeFile(pathToImage);
                activity.runOnUiThread(new ImageDisplayer(outputImage, imageView,
                        imageUrl));
            }

        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    private class ImageDisplayer implements Runnable {
        private Bitmap bm;
        private ImageView iv;
        private String url;

        public ImageDisplayer(Bitmap bm, ImageView iv, String url) {
            this.bm = bm;
            this.iv = iv;
            this.url = url;
        }

        @Override
        public void run() {
            String taggedUrl = (String) iv.getTag();
            if (!taggedUrl.equals(url)) {
                return;
            }

            iv.setImageBitmap(bm);
        }
    }

    public static void initLruCache() {
        if (mMemoryCache != null) {
            return;
        }

        int maxMemory = (int) (Runtime.getRuntime().maxMemory());
        int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
    }

    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        mMemoryCache.put(key, bitmap);
    }

    public static Bitmap getBitmapFromMemCache(String key) {
        if (mMemoryCache == null) {
            return null;
        }
        return mMemoryCache.get(key);
    }

    public static void clearCache() {
        if (mMemoryCache == null) {
            return;
        }
        mMemoryCache.evictAll();
        mMemoryCache = null;
    }

    public static String checkImageCacheExist(String imgUrl) {
        File downloadedFile = null;
        try {
            String rootFolder = Environment.getExternalStorageDirectory()
                    .toString();
            File dir = new File(rootFolder + "/WeComplaint/Cache");
            String fileName = URLEncoder.encode(imgUrl, HTTP.UTF_8) + "-cache";

            downloadedFile = new File(dir, fileName);

            if (downloadedFile.exists()) {
                return downloadedFile.getAbsolutePath();
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }

        return "";
    }

    public static String downloadAndSaveImage(String imgUrl) {
        File downloadedFile = null;
        try {
            String rootFolder = Environment.getExternalStorageDirectory()
                    .toString();
            File dir = new File(rootFolder + "/WeComplaint/Cache");
            dir.mkdirs();

            String fileName = URLEncoder.encode(imgUrl, HTTP.UTF_8) + "-cache";

            downloadedFile = new File(dir, fileName);

            if (downloadedFile.exists()) {
                return downloadedFile.getAbsolutePath();
            } else {
                downloadedFile.createNewFile();
            }

            URL url = new URL(imgUrl);
            URLConnection connection = url.openConnection();
            connection.connect();
            // this will be useful so that you can show a typical 0-100%
            // progress bar
            // int fileLength = connection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(downloadedFile);

            byte data[] = new byte[10240]; // 10kb
            // long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // total += count;
                // this part of code will be useful in future if we need a
                // progress bar
                // publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }

        if (downloadedFile == null) {
            return null;
        } else {
            return downloadedFile.getAbsolutePath();
        }
    }


}
