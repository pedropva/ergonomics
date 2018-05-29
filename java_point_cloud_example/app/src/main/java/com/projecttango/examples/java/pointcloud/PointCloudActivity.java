/*
 * Copyright 2014 Google Inc. All Rights Reserved.
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

package com.projecttango.examples.java.pointcloud;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoCameraIntrinsics;
import com.google.atap.tangoservice.TangoConfig;
import com.google.atap.tangoservice.TangoCoordinateFramePair;
import com.google.atap.tangoservice.TangoErrorException;
import com.google.atap.tangoservice.TangoEvent;
import com.google.atap.tangoservice.TangoInvalidException;
import com.google.atap.tangoservice.TangoOutOfDateException;
import com.google.atap.tangoservice.TangoPointCloudData;
import com.google.atap.tangoservice.TangoPoseData;
import com.google.atap.tangoservice.experimental.TangoImageBuffer;
import com.google.tango.depthinterpolation.TangoDepthInterpolation;
import com.google.tango.support.TangoPointCloudManager;
import com.google.tango.support.TangoSupport;
import com.google.tango.ux.TangoUx;
import com.google.tango.ux.UxExceptionEvent;
import com.google.tango.ux.UxExceptionEventListener;

import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.scene.ASceneFrameCallback;
import org.rajawali3d.surface.RajawaliSurfaceView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Main Activity class for the Point Cloud Sample. Handles the connection to the {@link Tango}
 * service and propagation of Tango point cloud data to OpenGL and Layout views. OpenGL rendering
 * logic is delegated to the {@link PointCloudRajawaliRenderer} class.
 */
public class PointCloudActivity extends Activity {
    private static final String TAG = PointCloudActivity.class.getSimpleName();

    private static final String UX_EXCEPTION_EVENT_DETECTED = "Exception Detected: ";
    private static final String UX_EXCEPTION_EVENT_RESOLVED = "Exception Resolved: ";

    private static final int SECS_TO_MILLISECS = 1000;
    private static final DecimalFormat FORMAT_THREE_DECIMAL = new DecimalFormat("0.000");
    private static final double UPDATE_INTERVAL_MS = 100.0;

    private Tango mTango;
    private TangoConfig mConfig;
    private TangoUx mTangoUx;

    private TangoPointCloudManager mPointCloudManager;
    private PointCloudRajawaliRenderer mRenderer;
    private RajawaliSurfaceView mSurfaceView;
    private TextView mPointCountTextView;

    private TextView mAverageZTextView;
    private TextView mSizeBufferTextView;
    TextView response;
    private double mPointCloudPreviousTimeStamp;
    String ServerAddress = "192.168.200.71";
    String ServerPort = "30000";
    Button sendDataBt;
    float[] firstTransform=null;
    private boolean firstPointCLoud = true;
    private boolean mIsConnected = false;
    private boolean pointCloudIsSelected = false;
    final String dir = "/sdcard/picFolder/";


    private double mPointCloudTimeToNextUpdate = UPDATE_INTERVAL_MS;

    Integer fileCount = 0;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CAMERA = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private int mDisplayRotation = 0;

    private volatile TangoImageBuffer mImageBuffer = null;
    TangoPointCloudData pointCloud = null;
    private boolean takePhoto = false;
    private boolean updateSkeleton = false;
    Stack<Vector3> skeletonPoints;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_cloud);

        mPointCountTextView = (TextView) findViewById(R.id.point_count_textview);
        mAverageZTextView = (TextView) findViewById(R.id.average_z_textview);
        mSizeBufferTextView = (TextView) findViewById(R.id.size_buffer_textview);
        mSurfaceView = (RajawaliSurfaceView) findViewById(R.id.gl_surface_view);
        Stack<Vector3> skeletonPoints = new Stack();

        response = (TextView) findViewById(R.id.responseTextView);
        sendDataBt =(Button) findViewById(R.id.send_data_button);
        mPointCloudManager = new TangoPointCloudManager();
        mTangoUx = setupTangoUxAndLayout();
        mRenderer = new PointCloudRajawaliRenderer(this);
        setupRenderer();

        //verifying permissions
        verifyStoragePermissions(this);
        verifyCameraPermissions(this);

        //creating the images folder
        try{
            File mediaStorageDir = new File(dir);
            if (!mediaStorageDir.exists()) {
                Log.d("CameraDemo", "Dir doesnt exist");
                if (!mediaStorageDir.mkdirs()) {
                    Log.e("App", "failed to create directory");
                }
                fileCount = (mediaStorageDir.listFiles().length-1);
            }else{
                Log.d("CameraDemo", "Dir exists");
                fileCount = (mediaStorageDir.listFiles().length-1);
            }
        } catch(Exception e) {

            // if any error occurs
            e.printStackTrace();
        }


        DisplayManager displayManager = (DisplayManager) getSystemService(DISPLAY_SERVICE);
        if (displayManager != null) {
            displayManager.registerDisplayListener(new DisplayManager.DisplayListener() {
                @Override
                public void onDisplayAdded(int displayId) {
                }

                @Override
                public void onDisplayChanged(int displayId) {
                    synchronized (this) {
                        setDisplayRotation();
                    }
                }

                @Override
                public void onDisplayRemoved(int displayId) {
                }
            }, null);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mTangoUx.start();
        bindTangoService();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Synchronize against disconnecting while the service is being used in the OpenGL
        // thread or in the UI thread.
        // NOTE: DO NOT lock against this same object in the Tango callback thread.
        // Tango.disconnect will block here until all Tango callback calls are finished.
        // If you lock against this object in a Tango callback thread it will cause a deadlock.
        synchronized (this) {
            try {
                mTangoUx.stop();
                mTango.disconnect();
                mIsConnected = false;
            } catch (TangoErrorException e) {
                Log.e(TAG, getString(R.string.exception_tango_error), e);
            }
        }
    }

    /**
     * Initialize Tango Service as a normal Android Service.
     */
    private void bindTangoService() {
        // Initialize Tango Service as a normal Android Service. Since we call mTango.disconnect()
        // in onPause, this will unbind Tango Service, so every time onResume gets called we
        // should create a new Tango object.
        mTango = new Tango(PointCloudActivity.this, new Runnable() {
            // Pass in a Runnable to be called from UI thread when Tango is ready; this Runnable
            // will be running on a new thread.
            // When Tango is ready, we can call Tango functions safely here only when there are no
            // UI thread changes involved.
            @Override
            public void run() {
                // Synchronize against disconnecting while the service is being used in the OpenGL
                // thread or in the UI thread.
                synchronized (PointCloudActivity.this) {
                    try {
                        mConfig = setupTangoConfig(mTango);
                        mTango.connect(mConfig);
                        startupTango();
                        TangoSupport.initialize(mTango);
                        mIsConnected = true;
                        setDisplayRotation();
                    } catch (TangoOutOfDateException e) {
                        Log.e(TAG, getString(R.string.exception_out_of_date), e);
                    } catch (TangoErrorException e) {
                        Log.e(TAG, getString(R.string.exception_tango_error), e);
                        showsToastAndFinishOnUiThread(R.string.exception_tango_error);
                    } catch (TangoInvalidException e) {
                        Log.e(TAG, getString(R.string.exception_tango_invalid), e);
                        showsToastAndFinishOnUiThread(R.string.exception_tango_invalid);
                    }
                }
            }
        });
    }

    /**
     * Sets up the Tango configuration object. Make sure mTango object is initialized before
     * making this call.
     */
    private TangoConfig setupTangoConfig(Tango tango) {
        // Use the default configuration plus add depth sensing.
        TangoConfig config = tango.getConfig(TangoConfig.CONFIG_TYPE_DEFAULT);
        config.putBoolean(TangoConfig.KEY_BOOLEAN_DEPTH, true);
        config.putBoolean(TangoConfig.KEY_BOOLEAN_COLORCAMERA, true);
        config.putInt(TangoConfig.KEY_INT_DEPTH_MODE, TangoConfig.TANGO_DEPTH_MODE_POINT_CLOUD);
        return config;
    }

    /**
     * Set up the callback listeners for the Tango Service and obtain other parameters required
     * after Tango connection.
     * Listen to updates from the Point Cloud and Tango Events and Pose.
     */
    private void startupTango() {
        ArrayList<TangoCoordinateFramePair> framePairs = new ArrayList<TangoCoordinateFramePair>();

        framePairs.add(new TangoCoordinateFramePair(TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE,
                TangoPoseData.COORDINATE_FRAME_DEVICE));

        mTango.connectListener(framePairs, new Tango.TangoUpdateCallback() {
            @Override
            public void onPoseAvailable(TangoPoseData pose) {
                // Passing in the pose data to UX library produce exceptions.
                if (mTangoUx != null) {
                    mTangoUx.updatePoseStatus(pose.statusCode);
                }
            }

            @Override
            public void onPointCloudAvailable(TangoPointCloudData pointCloud) {
                if (mTangoUx != null) {
                    mTangoUx.updatePointCloud(pointCloud);
                }
                mPointCloudManager.updatePointCloud(pointCloud);

                final double currentTimeStamp = pointCloud.timestamp;
                final double pointCloudFrameDelta =
                        (currentTimeStamp - mPointCloudPreviousTimeStamp) * SECS_TO_MILLISECS;
                mPointCloudPreviousTimeStamp = currentTimeStamp;
                final double averageDepth = getAveragedDepth(pointCloud.points,
                        pointCloud.numPoints);

                mPointCloudTimeToNextUpdate -= pointCloudFrameDelta;

                if (mPointCloudTimeToNextUpdate < 0.0) {
                    mPointCloudTimeToNextUpdate = UPDATE_INTERVAL_MS;
                    final String pointCountString = Integer.toString(pointCloud.numPoints);
                    final String sizeBufferString = Integer.toString(pointCloud.points.capacity());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPointCountTextView.setText(pointCountString);
                            mAverageZTextView.setText(FORMAT_THREE_DECIMAL.format(averageDepth));
                            mSizeBufferTextView.setText(sizeBufferString);
                        }
                    });
                }
            }

            @Override
            public void onTangoEvent(TangoEvent event) {
                if (mTangoUx != null) {
                    mTangoUx.updateTangoEvent(event);
                }
            }
            /*
            @Override
            public void onFrameAvailable(int i) {
                Log.d("CAMERA FRAME", "Tango frame called");
            }
            */
        });
        mTango.experimentalConnectOnFrameListener(TangoCameraIntrinsics.TANGO_CAMERA_COLOR, new
                Tango.OnFrameAvailableListener() {
                    @Override
                    public void onFrameAvailable(TangoImageBuffer tangoImageBuffer, int id) {
                        if(!
                                takePhoto) {
                            return;
                        }
                            if (id != TangoCameraIntrinsics.TANGO_CAMERA_COLOR || tangoImageBuffer == null) {
                            return;
                        }
                        Log.d("CAMERA FRAME", "Tango frame called");
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
                        sendImage(mImageBuffer);
                        takePhoto = false;
                    }
                });
    }

    /**
     * Sets Rajawali surface view and its renderer. This is ideally called only once in onCreate.
     */
    public void setupRenderer() {
        mSurfaceView.setEGLContextClientVersion(2);
        mRenderer.getCurrentScene().registerFrameCallback(new ASceneFrameCallback() {
            @Override
            public void onPreFrame(long sceneTime, double deltaTime) {
                // NOTE: This will be executed on each cycle before rendering; called from the
                // OpenGL rendering thread.

                // Prevent concurrent access from a service disconnect through the onPause event.
                synchronized (PointCloudActivity.this) {
                    // Don't execute any Tango API actions if we're not connected to the service.
                    if (!mIsConnected) {
                        return;
                    }
                    // Update point cloud data.
                    TangoPointCloudData pointCloud = mPointCloudManager.getLatestPointCloud();
                    if(!pointCloudIsSelected) {
                        if (pointCloud != null) {
                            // Calculate the depth camera pose at the last point cloud update.
                            TangoSupport.MatrixTransformData transform =
                                    TangoSupport.getMatrixTransformAtTime(pointCloud.timestamp,
                                            TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE,
                                            TangoPoseData.COORDINATE_FRAME_CAMERA_DEPTH,
                                            TangoSupport.ENGINE_OPENGL,
                                            TangoSupport.ENGINE_TANGO,
                                            TangoSupport.ROTATION_IGNORED);
                            if (transform.statusCode == TangoPoseData.POSE_VALID) {
                                mRenderer.updatePointCloud(pointCloud, transform.matrix);
                                if(firstPointCLoud){
                                    firstTransform = transform.matrix;
                                    firstPointCLoud = false;
                                }
                            }
                        }
                    }
                    // Update current camera pose.
                    try {
                        // Calculate the device pose. This transform is used to display
                        // frustum in third and top down view, and used to render camera pose in
                        // first person view.
                        TangoPoseData lastFramePose = TangoSupport.getPoseAtTime(0,
                                TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE,
                                TangoPoseData.COORDINATE_FRAME_DEVICE,
                                TangoSupport.ENGINE_OPENGL,
                                TangoSupport.ENGINE_OPENGL,
                                mDisplayRotation);
                        if (lastFramePose.statusCode == TangoPoseData.POSE_VALID) {
                            mRenderer.updateCameraPose(lastFramePose);
                        }
                    } catch (TangoErrorException e) {
                        Log.e(TAG, "Could not get valid transform");
                    }
                    // Update current skeleton pose.
                    try {
                       if(updateSkeleton){
                           mRenderer.updateSkeleton(skeletonPoints);
                       }
                    } catch (TangoErrorException e) {
                        Log.e(TAG, "Could not get valid skeleton");
                    }
                }
            }

            @Override
            public boolean callPreFrame() {
                return true;
            }

            @Override
            public void onPreDraw(long sceneTime, double deltaTime) {

            }

            @Override
            public void onPostFrame(long sceneTime, double deltaTime) {

            }
        });
        mSurfaceView.setSurfaceRenderer(mRenderer);
    }

    /**
     * Sets up TangoUX and sets its listener.
     */
    private TangoUx setupTangoUxAndLayout() {
        TangoUx tangoUx = new TangoUx(this);
        tangoUx.setUxExceptionEventListener(mUxExceptionListener);
        return tangoUx;
    }

    /*
    * Set a UxExceptionEventListener to be notified of any UX exceptions.
    * In this example we are just logging all the exceptions to logcat, but in a real app,
    * developers should use these exceptions to contextually notify the user and help direct the
    * user in using the device in a way Tango Service expects it.
    * <p>
    * A UxExceptionEvent can have two statuses: DETECTED and RESOLVED.
    * An event is considered DETECTED when the exception conditions are observed, and RESOLVED when
    * the root causes have been addressed.
    * Both statuses will trigger a separate event.
    */
    private UxExceptionEventListener mUxExceptionListener = new UxExceptionEventListener() {
        @Override
        public void onUxExceptionEvent(UxExceptionEvent uxExceptionEvent) {
            String status = uxExceptionEvent.getStatus() == UxExceptionEvent.STATUS_DETECTED ?
                    UX_EXCEPTION_EVENT_DETECTED : UX_EXCEPTION_EVENT_RESOLVED;

            if (uxExceptionEvent.getType() == UxExceptionEvent.TYPE_LYING_ON_SURFACE) {
                Log.i(TAG, status + "Device lying on surface");
            }
            if (uxExceptionEvent.getType() == UxExceptionEvent.TYPE_FEW_DEPTH_POINTS) {
                Log.i(TAG, status + "Too few depth points");
            }
            if (uxExceptionEvent.getType() == UxExceptionEvent.TYPE_FEW_FEATURES) {
                Log.i(TAG, status + "Too few features");
            }
            if (uxExceptionEvent.getType() == UxExceptionEvent.TYPE_MOTION_TRACK_INVALID) {
                Log.i(TAG, status + "Invalid poses in MotionTracking");
            }
            if (uxExceptionEvent.getType() == UxExceptionEvent.TYPE_MOVING_TOO_FAST) {
                Log.i(TAG, status + "Moving too fast");
            }
            if (uxExceptionEvent.getType() == UxExceptionEvent.TYPE_FISHEYE_CAMERA_OVER_EXPOSED) {
                Log.i(TAG, status + "Fisheye Camera Over Exposed");
            }
            if (uxExceptionEvent.getType() == UxExceptionEvent.TYPE_FISHEYE_CAMERA_UNDER_EXPOSED) {
                Log.i(TAG, status + "Fisheye Camera Under Exposed");
            }
        }
    };

    /**
     * First Person button onClick callback.
     */
    public void onFirstPersonClicked(View v) {
        mRenderer.setFirstPersonView();
    }

    /**
     * Third Person button onClick callback.
     */
    public void onThirdPersonClicked(View v) {
        mRenderer.setThirdPersonView();
    }

    /**
     * Top-down button onClick callback.
     */
    public void onTopDownClicked(View v) {
        mRenderer.setTopDownView();
    }

    /**
     * onSendData button onClick callback.
     */
    public void onSendDataClicked(View v) {
        pointCloud = mPointCloudManager.getLatestPointCloud();
        pointCloudIsSelected = true;
        if (pointCloud != null && firstTransform != null) {
            takePhoto = true;
        }else{
            response.setText("Error while extracting the image/point cloud.");
        }
    }

    /**
     * Clear button onClick callback.
     */
    public void onClearClicked(View v) {
        response.setText("");
        pointCloudIsSelected = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mRenderer.onTouchEvent(event);
        return true;
    }

    /**
     * Calculates the average depth from a point cloud buffer.
     *
     * @param pointCloudBuffer
     * @param numPoints
     * @return Average depth.
     */
    private float getAveragedDepth(FloatBuffer pointCloudBuffer, int numPoints) {
        float totalZ = 0;
        float averageZ = 0;
        if (numPoints != 0) {
            int numFloats = 4 * numPoints;
            for (int i = 2; i < numFloats; i = i + 4) {
                totalZ = totalZ + pointCloudBuffer.get(i);
            }
            averageZ = totalZ / numPoints;
        }
        return averageZ;
    }
    private String print5LastPoints(float[] pointCloudBuffer, int numPoints) {
        String pointsString = "";
        if (numPoints != 0) {
            int numFloats = 4 * numPoints;
            for (int i = numFloats-20; i < numFloats; i++) {
                pointsString +=" " + pointCloudBuffer[i];
            }
        }else{
            pointsString="Não deu pra recuperar a nuvem de pontos :(";
        }
        return pointsString;
    }
    private String printNFirstPoints(float[] pointCloudBuffer, int numPoints) {
        String pointsString = "";
        if (numPoints != 0) {
            int numFloats = 4 * numPoints;
            for (int i = 0; i < numFloats; i = i + 1) {
                pointsString +=" " + pointCloudBuffer[i];
            }
        }else{
            pointsString="Não deu pra recuperar a nuvem de pontos :(";
        }
        return pointsString;
    }
    private void fillTestCloud(FloatBuffer pointCloudBuffer) {
        float aux=0.0f;
        for (int i = 0; i < pointCloudBuffer.capacity(); i = i + 1) {
            pointCloudBuffer.put(aux);
            aux += 1.0f;
        }

    }
    private float[] fixPointCloud(FloatBuffer pointCloudBuffer, float[] fix) {
        float[] fixed = new float[pointCloudBuffer.capacity()];


        for(int i=0;i < pointCloudBuffer.capacity()/4;i++) {//pra cada ponto(que vamos considerar uma matrix 1x4)
            for (int j =0 ; j< 4;j++){//pra cada coluna do ponto 1x4
                for (int k =0 ; k< 4;k++){//pra cada coluna da matrix 4x4
                    if(k==3){// se for o quarto elemento do ponto entao a gente multiplica por 1.0f pq o quarto é a confiabilidade
                        fixed[(4*i)+j]+=1.0f*fix[(k*4)+j];//multiplico cada elemento da primeira linha 0 por seu respectivo elemento da coluna 0 e somo o resultado
                    }
                    fixed[(4*i)+j]+=pointCloudBuffer.get((i*4)+k)*fix[(k*4)+j];//multiplico cada elemento da primeira linha 0 por seu respectivo elemento da coluna 0 e somo o resultado
                }
            }
        }
        /*
        for(int i=0;i < pointCloudBuffer.capacity();i++) {
            fixed[i] = pointCloudBuffer.get(i);
        }
        */
        //AINDA PRECISA TORANR A CONFIABILIDADE DE TODOS 1

        //pointCloudBuffer.get(fixed);
        return fixed;
    }
    public static final byte[] float2Byte(float[] inData) {
        int j = 0;
        int length = inData.length;
        int dataLength = 3000+inData.length-inData.length%3000;
        byte[] outData = new byte[dataLength * 4];
        for (int i = 0; i < length; i++) {
            int d = Float.floatToIntBits(inData[i]);
            outData[j++] = (byte) (d >>> 24);
            outData[j++] = (byte) (d >>> 16);
            outData[j++] = (byte) (d >>> 8);
            outData[j++] = (byte) (d >>> 0);
        }
        return outData;
    }
    public static final byte[] float2Byte(FloatBuffer inData) {
        int j = 0;
        int length = inData.capacity();
        int dataLength = 3000+inData.capacity()-inData.capacity()%3000;
        byte[] outData = new byte[dataLength * 4];
        for (int i = 0; i < length; i++) {
            int d = Float.floatToIntBits(inData.get(i));
            outData[j++] = (byte) (d >>> 24);
            outData[j++] = (byte) (d >>> 16);
            outData[j++] = (byte) (d >>> 8);
            outData[j++] = (byte) (d >>> 0);
        }
        return outData;
    }

    /**
     * Query the display's rotation.
     */
    @SuppressLint("WrongConstant")
    private void setDisplayRotation() {
        Display display = getWindowManager().getDefaultDisplay();
        mDisplayRotation = display.getRotation();
    }

    /**
     * Display toast on UI thread.
     *
     * @param resId The resource id of the string resource to use. Can be formatted text.
     */
    private void showsToastAndFinishOnUiThread(final int resId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(PointCloudActivity.this,
                        getString(resId), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    public static void verifyCameraPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_CAMERA
            );
        }
    }
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }
    public void sendImage(TangoImageBuffer buffer){

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        YuvImage yuvImage = new YuvImage(buffer.data.array(), ImageFormat.NV21,buffer.width, buffer.height, null);
        yuvImage.compressToJpeg(new Rect(0, 0, buffer.width, buffer.height), 70, out);
        byte[] imgbyte = out.toByteArray();
        Bitmap storedBitmap = BitmapFactory.decodeByteArray(imgbyte, 0, imgbyte.length, null);

        Matrix mat = new Matrix();
        mat.postRotate(90);  // angle is the desired angle you wish to rotate
        storedBitmap = Bitmap.createBitmap(storedBitmap, 0, 0, storedBitmap.getWidth(), storedBitmap.getHeight(), mat, true);
        imgbyte = getBytesFromBitmap(storedBitmap);
        /*

            String filepath =  "/sdcard/picFolder/"+fileCount+".jpg";
            Log.d("CameraDemo", filepath);
            File imagefile = new File(filepath);
            if (imagefile.exists()){
                Log.d("CameraDemo", "File exists");
            }else {
                Log.d("CameraDemo", "File doesnt exist");
            }
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(imagefile);
            } catch (FileNotFoundException e) {
                Log.e("Error",e.toString());
            }

            try {
                imgbyte = new byte[fis.available()];
                fis.read(imgbyte);
            } catch (IOException e) {
                Log.e("Error",e.toString());
            }
            Bitmap bm = BitmapFactory.decodeStream(fis);
            imgbyte = getBytesFromBitmap(bm);
            */

        Log.d("CameraDemo", "Tamanho do buffer enviado:" + Integer.toString(imgbyte.length));
        if(imgbyte.length>0){
            Client myClient = new Client(ServerAddress
                    , Integer.parseInt(ServerPort)
                    , imgbyte
                    ,this);
            myClient.execute();
        }

    }
    /**
     * Use the Tango Support Library with point cloud data to calculate the depth
     * of the point closest to where the user touches the screen. It returns a
     * Vector3 in OpenGL world space.
     */
    private float[] getDepthAt2DPosition(float u, float v,TangoImageBuffer imageBuffer,TangoPointCloudData pointCloud ) {//
        if (pointCloud == null) {
            return null;
        }

        double rgbTimestamp;
        rgbTimestamp = imageBuffer.timestamp; // CPU.

        TangoPoseData depthlTcolorPose = TangoSupport.getPoseAtTime(
                rgbTimestamp,
                TangoPoseData.COORDINATE_FRAME_CAMERA_DEPTH,
                TangoPoseData.COORDINATE_FRAME_CAMERA_COLOR,
                TangoSupport.ENGINE_TANGO,
                TangoSupport.ENGINE_TANGO,
                TangoSupport.ROTATION_IGNORED);
        if (depthlTcolorPose.statusCode != TangoPoseData.POSE_VALID) {
            Log.w(TAG, "Could not get color camera transform at time "
                    + rgbTimestamp);
            return null;
        }

        float[] depthPoint;
        /*
        depthPoint = TangoDepthInterpolation.getDepthAtPointBilateral(
                pointCloud,
                new double[] {0.0, 0.0, 0.0},
                new double[] {0.0, 0.0, 0.0, 1.0},
                imageBuffer,
                u, v,
                mDisplayRotation,
                depthlTcolorPose.translation,
                depthlTcolorPose.rotation);
        */
        depthPoint = TangoDepthInterpolation.getDepthAtPointNearestNeighbor(
                pointCloud,
                new double[] {0.0, 0.0, 0.0},
                new double[] {0.0, 0.0, 0.0, 1.0},
                u, v,
                mDisplayRotation,
                depthlTcolorPose.translation,
                depthlTcolorPose.rotation);


        if (depthPoint == null) {
            return null;
        }

        return depthPoint;
    }

    public void DrawPoints(String response){
        //dividir os pontos aqui
        float[] curDepthPoint = null;
        Log.i("Skeleton",response);
        String[] people = response.split(" \"pose_keypoints_2d\":\\[");
        for(int j=0; j < people.length-1;j++){
            String[] keypointsString = people[j+1].split("]");
            String[] keypoints = keypointsString[0].split(",");
            for(int i = 0 ; i< 17; i++) {
                Log.i("SkeletonKeypoints", keypoints[i]);
            }
            /*
            for(int i = 0 ; i< 17; i++){
                //colocar os pontos na point cloud aqui
                curDepthPoint = getDepthAt2DPosition(Float.parseFloat(keypoints[i]),Float.parseFloat(keypoints[i]),mImageBuffer,pointCloud);
                if(curDepthPoint != null && curDepthPoint.length == 3){
                    //põe os skeleton points em 3D no stack e ativa a função de atualizar esqueletos na thread do openGL
                    skeletonPoints.add(new Vector3(curDepthPoint[0],curDepthPoint[1],curDepthPoint[2]));
                }
            }
            updateSkeleton = true;
            */
        }
    }


}
