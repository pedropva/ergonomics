package com.projecttango.examples.java.pointcloud;

/**
 * Created by pedropva on 25/05/2018.
 */


import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoCameraIntrinsics;
import com.google.atap.tangoservice.TangoEvent;
import com.google.atap.tangoservice.TangoInvalidException;
import com.google.atap.tangoservice.TangoPointCloudData;
import com.google.atap.tangoservice.TangoPoseData;
import com.google.atap.tangoservice.TangoXyzIjData;
import com.google.atap.tangoservice.experimental.TangoImageBuffer;
import com.google.tango.support.TangoPointCloudManager;
import com.google.tango.support.TangoSupport;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.util.HashMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 *
 */
public class CameraThing extends Tango.OnTangoUpdateListener implements Tango.OnFrameAvailableListener{
    private volatile TangoImageBuffer mImageBuffer = null;

    @Override
    public void onFrameAvailable(TangoImageBuffer tangoImageBuffer, int cameraId) {

            Log.d("CAMERA FRAME", "Tango frame called");
            if (cameraId != TangoCameraIntrinsics.TANGO_CAMERA_COLOR) {
            return;
            }

            if (tangoImageBuffer == null) {
            return;
            }

            TangoImageBuffer image = new TangoImageBuffer();
            image.frameNumber = tangoImageBuffer.frameNumber;
            image.timestamp = tangoImageBuffer.timestamp;
            image.stride = tangoImageBuffer.stride;
            image.format = tangoImageBuffer.format;
            image.width = tangoImageBuffer.width;
            image.height = tangoImageBuffer.height;
            image.data = ByteBuffer.allocateDirect(tangoImageBuffer.data.capacity());
            image.data.put(tangoImageBuffer.data);
            image.data.position(0);

            mImageBuffer = image;
    }
}

