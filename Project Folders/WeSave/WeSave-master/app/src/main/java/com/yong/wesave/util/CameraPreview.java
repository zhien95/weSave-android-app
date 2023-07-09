package com.yong.wesave.util;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {

            Camera.CameraInfo info = new Camera.CameraInfo();
            int rotate = (info.orientation + 90) % 360;
            Camera.Parameters params = mCamera.getParameters();
            params.setRotation(rotate);
            //params.setPreviewSize(getScreenWidth(), getScreenWidth());
            mCamera.setParameters(params);
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();

        } catch (IOException e) {
            Log.d("", "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }


    public int determineDisplayOrientation() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        Camera.getCameraInfo(cameraId, cameraInfo);

        int rotation = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getRotation();
        int screenDegrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                screenDegrees = 0;
                break;

            case Surface.ROTATION_90:
                screenDegrees = 90;
                break;

            case Surface.ROTATION_180:
                screenDegrees = 180;
                break;

            case Surface.ROTATION_270:
                screenDegrees = 270;
                break;
        }

        return screenDegrees;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            Camera.Parameters params = mCamera.getParameters();
            //params.setPreviewSize(getScreenWidth(), getScreenWidth());
            mCamera.setParameters(params);
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();

        } catch (Exception e) {
            Log.d("", "Error starting camera preview: " + e.getMessage());
        }
    }
}