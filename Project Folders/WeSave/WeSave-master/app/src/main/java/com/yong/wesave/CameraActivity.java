package com.yong.wesave;

/**
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.yong.wesave.util.CameraPreview;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    private Camera mCamera;
    private CameraPreview mPreview;
    private Button mCapture;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);

        // Create an instance of Camera
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(this, mCamera);


        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        // Create our Preview view and set it as the content of our activity.
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);

        mCapture = (Button) findViewById(R.id.button_capture);
        preview.addView(mPreview);
        mCapture.setOnClickListener(this);

    }

    public static Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open(); // attempt to get a Camera instance
            Camera.Parameters parameters = camera.getParameters();


            List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
            //parameters.set("jpeg-quality", 70);
            Camera.Size size = sizes.get(0);
            parameters.setPictureFormat(PixelFormat.JPEG);
            parameters.setPictureSize(size.width, size.height);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            camera.setParameters(parameters);

        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return camera; // returns null if camera is unavailable
    }

    public static int getScreenWidth() {
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        return width;
    }

    @Override
    public void onClick(View v) {
        if (v == mCapture) {
            mCamera.takePicture(null, null, myPictureCallback_JPG);

        }
    }

    private Camera.PictureCallback myPictureCallback_JPG = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera cam) {
            String path = SaveImageByteArray(data);
            data = null;
            endActivity(path);
        }
    };

    public static String SaveImageByteArray(byte[] data) {
        String rootFolder = Environment.getExternalStorageDirectory()
                .toString();
        File dir = new File(rootFolder + "/WeSave");
        dir.mkdirs();

        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmssSSS");
        String date = s.format(new Date());

        String fileName = "TEMP_" + date + ".jpg";

        File file = new File(dir, fileName);

        try {
            FileOutputStream out = new FileOutputStream(file);

            out.write(data);
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        //resize image
        String path = file.getAbsolutePath();
        //String newPath = "C:/Users/NTU Student/Desktop/FYP-Code/WeSaveRestAPI/WeSaveRestAPI/node-postgres-promises/public/uploads/item_images/item_" + item_id + ".jpg";
        //*** Resize Images
        try {
            ResizeImages(path, path);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return file.getAbsolutePath();
    }

    private void endActivity(String path) {
        Intent resultIntent = new Intent(this, CreateItemActivity.class);
        resultIntent.putExtra("imagePath", path);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    public static void ResizeImages(String sPath, String sTo) throws IOException {
        Double width;
        Double height;
        Matrix matrix = new Matrix();

        matrix.postRotate(90);

        Bitmap photo = BitmapFactory.decodeFile(sPath);
        width = photo.getWidth() * 0.3;
        height = photo.getHeight() * 0.3;
        int crop = (int) ((width - height) / 2);
        photo = Bitmap.createScaledBitmap(photo, width.intValue(), height.intValue(), false);
        Bitmap rotatedBitmap = Bitmap.createBitmap(photo, crop, 0, height.intValue(), height.intValue(), matrix, true);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        File file = new File(sPath);
        file.delete();

        File f = new File(sTo);
        f.createNewFile();
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());
        fo.close();

    }

}

