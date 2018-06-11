/*
 * Copyright 2016 Google Inc. All Rights Reserved.
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

package com.projecttango.examples.java.pointtopoint;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.Tango.OnTangoUpdateListener;
import com.google.atap.tangoservice.TangoCameraIntrinsics;
import com.google.atap.tangoservice.TangoConfig;
import com.google.atap.tangoservice.TangoCoordinateFramePair;
import com.google.atap.tangoservice.TangoErrorException;
import com.google.atap.tangoservice.TangoEvent;
import com.google.atap.tangoservice.TangoException;
import com.google.atap.tangoservice.TangoInvalidException;
import com.google.atap.tangoservice.TangoOutOfDateException;
import com.google.atap.tangoservice.TangoPointCloudData;
import com.google.atap.tangoservice.TangoPoseData;
import com.google.atap.tangoservice.TangoXyzIjData;
import com.google.atap.tangoservice.experimental.TangoImageBuffer;
import com.google.tango.depthinterpolation.TangoDepthInterpolation;
import com.google.tango.support.TangoPointCloudManager;
import com.google.tango.support.TangoSupport;
import com.google.tango.transformhelpers.TangoTransformHelper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.scene.ASceneFrameCallback;
import org.rajawali3d.view.SurfaceView;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An example showing how to build a very simple point-to-point measurement app
 * in Java. It uses the Tango Support Library to do depth calculations using
 * the point cloud data. Whenever the user clicks on the camera display, a point
 * is recorded from the point cloud data closest to the point of the touch;
 * consecutive touches are used as the two points for a distance measurement.
 * <p/>
 * Note that it is important to include the KEY_BOOLEAN_LOWLATENCYIMUINTEGRATION
 * configuration parameter in order to achieve best results synchronizing the
 * Rajawali virtual world with the RGB camera.
 * <p/>
 * For more details on the augmented reality effects, including color camera texture rendering,
 * see java_augmented_reality_example or java_hello_video_example.
 */
public class PointToPointActivity extends Activity implements View.OnTouchListener {
    private class MeasuredPoint {
        public double mTimestamp;
        public float[] coords;

        public MeasuredPoint(double timestamp, float[] coordinates) {
            mTimestamp = timestamp;
            this.coords = coordinates;
        }
    }

    //here are the variables we save when the user clicks the send data button to use later when the response is given(getDethpAtPoint)
    private volatile TangoImageBuffer savedImageBuffer = null;
    TangoPointCloudData savedPointCloud = null;
    TangoPoseData savedDepthlTcolorPose = null;
    double savedRgbTimestamp;

    private boolean takePhoto = false;
    private boolean updateSkeleton = false;
    Stack<MeasuredPoint> skeletonPoints = new Stack<MeasuredPoint>();

    private static final String TAG = PointToPointActivity.class.getSimpleName();

    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private static final int CAMERA_PERMISSION_CODE = 0;

    // The interval at which we'll update our UI debug text in milliseconds.
    // This is the rate at which we query for distance data.
    private static final int UPDATE_UI_INTERVAL_MS = 100;

    private static final int INVALID_TEXTURE_ID = 0;

    private SurfaceView mSurfaceView;
    private PointToPointRenderer mRenderer;
    private TangoPointCloudManager mPointCloudManager;
    private Tango mTango;
    private TangoConfig mConfig;
    private boolean mIsConnected = false;
    private double mCameraPoseTimestamp = 0;
    private TextView mDistanceTextview;
    private CheckBox mBilateralBox;
    private CheckBox mDummieSkeleton;
    TextView response;
    String ServerAddress = "192.168.200.91";
    String ServerPort = "30000";
    Button sendDataBt;
    private volatile TangoImageBuffer mCurrentImageBuffer;

    // Texture rendering related fields.
    // NOTE: Naming indicates which thread is in charge of updating this variable.
    private int mConnectedTextureIdGlThread = INVALID_TEXTURE_ID;
    private AtomicBoolean mIsFrameAvailableTangoThread = new AtomicBoolean(false);
    private double mRgbTimestampGlThread;

    private boolean mPointSwitch = true;

    // Two measured points in OpenGL space, we used a stack to hold the data is because rajawalli
    // LineRenderer expects a stack of points to be passed in. This is render ready data format from
    // Rajawalli's perspective.
    private Stack<Vector3> skeletonPointsInOpenGLSpace = new Stack<Vector3>();
    private float mMeasuredDistance = 0.0f;

    // Handles the debug text UI update loop.
    private Handler mHandler = new Handler();

    private int mDisplayRotation = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSurfaceView = (SurfaceView) findViewById(R.id.ar_view);
        mRenderer = new PointToPointRenderer(this);
        mSurfaceView.setSurfaceRenderer(mRenderer);
        mSurfaceView.setOnTouchListener(this);
        mPointCloudManager = new TangoPointCloudManager();
        mDistanceTextview = (TextView) findViewById(R.id.distanceTextView);
        mBilateralBox = (CheckBox) findViewById(R.id.check_bilateral);
        mDummieSkeleton = (CheckBox) findViewById(R.id.check_dummie);

        sendDataBt =(Button) findViewById(R.id.send_data_button);
        response = (TextView) findViewById(R.id.responseTextView);



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

        // Check and request camera permission at run time.
        if (checkAndRequestPermissions()) {
            bindTangoService();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRenderer.clearSkeleton();
        // Synchronize against disconnecting while the service is being used in the OpenGL thread or
        // in the UI thread.
        // NOTE: DO NOT lock against this same object in the Tango callback thread. Tango.disconnect
        // will block here until all Tango callback calls are finished. If you lock against this
        // object in a Tango callback thread it will cause a deadlock.
        synchronized (this) {
            try {
                mRenderer.getCurrentScene().clearFrameCallbacks();
                if (mTango != null) {
                    mTango.disconnectCamera(TangoCameraIntrinsics.TANGO_CAMERA_COLOR);
                    mTango.disconnect();
                }
                // We need to invalidate the connected texture ID so that we cause a
                // re-connection in the OpenGL thread after resume.
                mConnectedTextureIdGlThread = INVALID_TEXTURE_ID;
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
        mTango = new Tango(PointToPointActivity.this, new Runnable() {
            // Pass in a Runnable to be called from UI thread when Tango is ready; this Runnable
            // will be running on a new thread.
            // When Tango is ready, we can call Tango functions safely here only when there are no
            // UI thread changes involved.
            @Override
            public void run() {
                // Synchronize against disconnecting while the service is being used in the OpenGL
                // thread or in the UI thread.
                synchronized (PointToPointActivity.this) {
                    try {
                        mConfig = setupTangoConfig(mTango);
                        mTango.connect(mConfig);
                        startupTango();
                        TangoSupport.initialize(mTango);
                        connectRenderer();
                        mIsConnected = true;
                        setDisplayRotation();
                    } catch (TangoOutOfDateException e) {
                        Log.e(TAG, getString(R.string.exception_out_of_date), e);
                        showsToastAndFinishOnUiThread(R.string.exception_out_of_date);
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
        mHandler.post(mUpdateUiLoopRunnable);
    }

    /**
     * Sets up the Tango configuration object. Make sure mTango object is initialized before
     * making this call.
     */
    private TangoConfig setupTangoConfig(Tango tango) {
        // Use default configuration for Tango Service (motion tracking), plus low latency
        // IMU integration, color camera, depth and drift correction.
        TangoConfig config = tango.getConfig(TangoConfig.CONFIG_TYPE_DEFAULT);
        // NOTE: Low latency integration is necessary to achieve a
        // precise alignment of virtual objects with the RBG image and
        // produce a good AR effect.
        config.putBoolean(TangoConfig.KEY_BOOLEAN_LOWLATENCYIMUINTEGRATION, true);
        config.putBoolean(TangoConfig.KEY_BOOLEAN_COLORCAMERA, true);
        config.putBoolean(TangoConfig.KEY_BOOLEAN_DEPTH, true);
        config.putInt(TangoConfig.KEY_INT_DEPTH_MODE, TangoConfig.TANGO_DEPTH_MODE_POINT_CLOUD);
        // Drift correction allows motion tracking to recover after it loses tracking.
        config.putBoolean(TangoConfig.KEY_BOOLEAN_DRIFT_CORRECTION, true);

        return config;
    }

    /**
     * Set up the callback listeners for the Tango Service and obtain other parameters required
     * after Tango connection.
     * Listen to updates from the RGB camera and point cloud.
     */
    private void startupTango() {
        // No need to add any coordinate frame pairs since we are not
        // using pose data. So just initialize.
        ArrayList<TangoCoordinateFramePair> framePairs =
                new ArrayList<TangoCoordinateFramePair>();
        mTango.connectListener(framePairs, new OnTangoUpdateListener() {
            @Override
            public void onPoseAvailable(TangoPoseData pose) {
                // We are not using OnPoseAvailable for this app.
            }

            @Override
            public void onFrameAvailable(int cameraId) {
                // Check if the frame available is for the camera we want and update its frame
                // on the view.
                if (cameraId == TangoCameraIntrinsics.TANGO_CAMERA_COLOR) {
                    // Mark a camera frame as available for rendering in the OpenGL thread.
                    mIsFrameAvailableTangoThread.set(true);
                    mSurfaceView.requestRender();
                }
            }

            @Override
            public void onXyzIjAvailable(TangoXyzIjData xyzIj) {
                // We are not using onXyzIjAvailable for this app.
            }

            @Override
            public void onPointCloudAvailable(TangoPointCloudData pointCloud) {
                // Save the cloud and point data for later use.
                mPointCloudManager.updatePointCloud(pointCloud);
            }

            @Override
            public void onTangoEvent(TangoEvent event) {
                // We are not using OnPoseAvailable for this app.
            }
        });
        mTango.experimentalConnectOnFrameListener(TangoCameraIntrinsics.TANGO_CAMERA_COLOR,
                new Tango.OnFrameAvailableListener() {
                    @Override
                    public void onFrameAvailable(TangoImageBuffer tangoImageBuffer, int i) {
                        mCurrentImageBuffer = copyImageBuffer(tangoImageBuffer);
                        if(!takePhoto) {
                            return;
                        }
                        if (i != TangoCameraIntrinsics.TANGO_CAMERA_COLOR || tangoImageBuffer == null) {
                            return;
                        }

                        sendImage(mCurrentImageBuffer);
                        takePhoto = false;
                    }

                    TangoImageBuffer copyImageBuffer(TangoImageBuffer imageBuffer) {
                        ByteBuffer clone = ByteBuffer.allocateDirect(imageBuffer.data.capacity());
                        imageBuffer.data.rewind();
                        clone.put(imageBuffer.data);
                        imageBuffer.data.rewind();
                        clone.flip();
                        return new TangoImageBuffer(imageBuffer.width, imageBuffer.height,
                                imageBuffer.stride, imageBuffer.frameNumber,
                                imageBuffer.timestamp, imageBuffer.format, clone,
                                imageBuffer.exposureDurationNs);
                    }
                });
    }

    /**
     * Connects the view and renderer to the color camara and callbacks.
     */
    private void connectRenderer() {
        // Register a Rajawali Scene Frame Callback to update the scene camera pose whenever a new
        // RGB frame is rendered.
        // (@see https://github.com/Rajawali/Rajawali/wiki/Scene-Frame-Callbacks)
        mRenderer.getCurrentScene().registerFrameCallback(new ASceneFrameCallback() {
            @Override
            public void onPreFrame(long sceneTime, double deltaTime) {
                // NOTE: This is called from the OpenGL render thread, after all the renderer
                // onRender callbacks have a chance to run and before scene objects are rendered
                // into the scene.

                try {
                    // Prevent concurrent access to {@code mIsFrameAvailableTangoThread} from the
                    // Tango callback thread and service disconnection from an onPause event.
                    synchronized (PointToPointActivity.this) {
                        // Don't execute any tango API actions if we're not connected to the
                        // service.
                        if (!mIsConnected) {
                            return;
                        }

                        // Set up scene camera projection to match RGB camera intrinsics.
                        if (!mRenderer.isSceneCameraConfigured()) {
                            TangoCameraIntrinsics intrinsics =
                            TangoSupport.getCameraIntrinsicsBasedOnDisplayRotation(
                                    TangoCameraIntrinsics.TANGO_CAMERA_COLOR,
                                    mDisplayRotation);
                            mRenderer.setProjectionMatrix(
                                    projectionMatrixFromCameraIntrinsics(intrinsics));
                        }

                        // Connect the camera texture to the OpenGL Texture if necessary
                        // NOTE: When the OpenGL context is recycled, Rajawali may regenerate the
                        // texture with a different ID.
                        if (mConnectedTextureIdGlThread != mRenderer.getTextureId()) {
                            mTango.connectTextureId(TangoCameraIntrinsics.TANGO_CAMERA_COLOR,
                                    mRenderer.getTextureId());
                            mConnectedTextureIdGlThread = mRenderer.getTextureId();
                            Log.d(TAG, "connected to texture id: " + mRenderer.getTextureId());
                        }

                        // If there is a new RGB camera frame available, update the texture with
                        // it.
                        if (mIsFrameAvailableTangoThread.compareAndSet(true, false)) {
                            mRgbTimestampGlThread =
                                    mTango.updateTexture(TangoCameraIntrinsics.TANGO_CAMERA_COLOR);
                        }

                        // If a new RGB frame has been rendered, update the camera pose to match.
                        if (mRgbTimestampGlThread > mCameraPoseTimestamp) {
                            // Calculate the camera color pose at the camera frame update time in
                            // OpenGL engine.
                            //
                            // When drift correction mode is enabled in config file, we need
                            // to query the device with respect to Area Description pose in
                            // order to use the drift-corrected pose.
                            //
                            // Note that if you don't want to use the drift corrected pose, the
                            // normal device with respect to start of service pose is still
                            // available.
                            //
                            // Also, we used mColorCameraToDipslayRotation to rotate the
                            // transformation to align with the display frame. The reason we use
                            // color camera instead depth camera frame is because the
                            // getDepthAtPointNearestNeighbor transformed depth point to camera
                            // frame.
                            TangoPoseData lastFramePose = TangoSupport.getPoseAtTime(
                                    mRgbTimestampGlThread,
                                    TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE,
                                    TangoPoseData.COORDINATE_FRAME_CAMERA_COLOR,
                                    TangoSupport.ENGINE_OPENGL,
                                    TangoSupport.ENGINE_OPENGL,
                                    mDisplayRotation);
                            if (lastFramePose.statusCode == TangoPoseData.POSE_VALID) {
                                // Update the camera pose from the renderer.
                                mRenderer.updateRenderCameraPose(lastFramePose);
                                mCameraPoseTimestamp = lastFramePose.timestamp;
                            } else {
                                // When the pose status is not valid, it indicates the tracking has
                                // been lost. In this case, we simply stop rendering.
                                //
                                // This is also the place to display UI to suggest the user walk
                                // to recover tracking.
                                Log.w(TAG, "Can't get device pose at time: " +
                                        mRgbTimestampGlThread);
                            }

                            // If points have been measured, we transform the points to OpenGL
                            // space, and send it to mRenderer to render.

                            if (!skeletonPoints.empty()) {
                                skeletonPointsInOpenGLSpace.clear();
                                // To make sure drift correct pose is also applied to virtual
                                // object (measured points).
                                // We need to re-query the Start of Service to Depth camera
                                // pose every frame. Note that you will need to use the timestamp
                                // at the time when the points were measured to query the pose.
                                TangoSupport.MatrixTransformData openglTDepthArr0 =
                                        TangoSupport.getMatrixTransformAtTime(
                                                skeletonPoints.get(0).mTimestamp,//the timestamp of the first point is supposed to be the same for all other points
                                                TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE,
                                                TangoPoseData.COORDINATE_FRAME_CAMERA_DEPTH,
                                                TangoSupport.ENGINE_OPENGL,
                                                TangoSupport.ENGINE_TANGO,
                                                TangoSupport.ROTATION_IGNORED);

                                if (openglTDepthArr0.statusCode == TangoPoseData.POSE_VALID) {
                                    for (int l = 0; l < skeletonPoints.size(); l++) {//here we recalculate the position of the points
                                        float[] p0 = TangoTransformHelper.transformPoint(openglTDepthArr0.matrix,skeletonPoints.get(l).coords);//multiply(openglTDepthArr0.matrix,skeletonPoints.get(l).coords);
                                        skeletonPointsInOpenGLSpace.push(new Vector3(p0[0], p0[1], p0[2]));
                                    }
                                }

                                mRenderer.setSkeleton(skeletonPointsInOpenGLSpace);// here the send the points in opengl coordinates to the render to be rendered
                            }else{
                                mRenderer.clearSkeleton();
                            }

                        }
                    }
                    // Avoid crashing the application due to unhandled exceptions.
                } catch (TangoErrorException e) {
                    Log.e(TAG, "Tango API call error within the OpenGL render thread", e);
                } catch (Throwable t) {
                    Log.e(TAG, "Exception on the OpenGL thread", t);
                }
            }

            @Override
            public void onPreDraw(long sceneTime, double deltaTime) {

            }

            @Override
            public void onPostFrame(long sceneTime, double deltaTime) {

            }

            @Override
            public boolean callPreFrame() {
                return true;
            }
        });
    }

    /**
     * Use Tango camera intrinsics to calculate the projection Matrix for the Rajawali scene.
     */
    private static float[] projectionMatrixFromCameraIntrinsics(TangoCameraIntrinsics intrinsics) {
        // Uses frustumM to create a projection matrix taking into account calibrated camera
        // intrinsic parameter.
        // Reference: http://ksimek.github.io/2013/06/03/calibrated_cameras_in_opengl/
        float near = 0.1f;
        float far = 100;

        double cx = intrinsics.cx;
        double cy = intrinsics.cy;
        double width = intrinsics.width;
        double height = intrinsics.height;
        double fx = intrinsics.fx;
        double fy = intrinsics.fy;

        double xscale = near / fx;
        double yscale = near / fy;

        double xoffset = (cx - (width / 2.0)) * xscale;
        // Color camera's coordinates has y pointing downwards so we negate this term.
        double yoffset = -(cy - (height / 2.0)) * yscale;

        float m[] = new float[16];
        android.opengl.Matrix.frustumM(m, 0,
                (float) (xscale * -width / 2.0 - xoffset),
                (float) (xscale * width / 2.0 - xoffset),
                (float) (yscale * -height / 2.0 - yoffset),
                (float) (yscale * height / 2.0 - yoffset), near, far);
        return m;
    }
    /**
     * onSendData button onClick callback.
     */
    public void onSendDataClicked(View v) {
        takePhoto = true;
        sendDataBt.setEnabled(false);
    }

    /**
     * Clear button onClick callback.
     */
    public void onClearClicked(View v) {
        response.setText("");
        //pointCloudIsSelected = false;
        updateSkeleton = false;
        skeletonPoints.clear();
        sendDataBt.setEnabled(true);
        mRenderer.clearSkeleton();
    }
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            // Calculate click location in u,v (0;1) coordinates.
            float u = motionEvent.getX() / view.getWidth();
            float v = motionEvent.getY() / view.getHeight();

            try {
                // Place point near the clicked point using the latest point cloud data.
                // Synchronize against concurrent access to the RGB timestamp in the OpenGL thread
                // and a possible service disconnection due to an onPause event.
                MeasuredPoint newMeasuredPoint;
                synchronized (this) {
                    newMeasuredPoint = getDepthAtPosition(u, v);
                }
                if (newMeasuredPoint != null) {
                    // Update a line endpoint to the touch location.
                    // This update is made thread-safe by the renderer.
                    //updateLine(newMeasuredPoint);
                    mMeasuredDistance = newMeasuredPoint.coords[0];//todo ver se esse é mesmo o numero da profundidade
                } else {
                    Log.w(TAG, "Point was null.");
                }

            } catch (TangoException t) {
                Toast.makeText(getApplicationContext(),
                        R.string.failed_measurement,
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, getString(R.string.failed_measurement), t);
            } catch (SecurityException t) {
                Toast.makeText(getApplicationContext(),
                        R.string.failed_permissions,
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, getString(R.string.failed_permissions), t);
            }
        }
        return true;
    }

    /**
     * Use the Tango Support Library with point cloud data to calculate the depth
     * of the point closest to where the user touches the screen. It returns a
     * Vector3 in OpenGL world space.
     */
    private MeasuredPoint getDepthAtPosition(float u, float v) {
        if (savedPointCloud == null) {
            return null;
        }

        float[] depthPoint;
        if (mBilateralBox.isChecked()) {
            depthPoint = TangoDepthInterpolation.getDepthAtPointBilateral(
                    savedPointCloud,
                    new double[] {0.0, 0.0, 0.0},
                    new double[] {0.0, 0.0, 0.0, 1.0},
                    savedImageBuffer,
                    u, v,
                    mDisplayRotation,
                    savedDepthlTcolorPose.translation,
                    savedDepthlTcolorPose.rotation);
        } else {
            depthPoint = TangoDepthInterpolation.getDepthAtPointNearestNeighbor(
                    savedPointCloud,
                    new double[] {0.0, 0.0, 0.0},
                    new double[] {0.0, 0.0, 0.0, 1.0},
                    u, v,
                    mDisplayRotation,
                    savedDepthlTcolorPose.translation,
                    savedDepthlTcolorPose.rotation);
        }

        if (depthPoint == null) {
            return null;
        }

        return new MeasuredPoint(savedRgbTimestamp,depthPoint);
    }


    // Debug text UI update loop, updating at 10Hz.
    private Runnable mUpdateUiLoopRunnable = new Runnable() {
        public void run() {
            try {
                mDistanceTextview.setText(String.format("%.2f", mMeasuredDistance) + " meters");
            } catch (Exception e) {
                e.printStackTrace();
            }
            mHandler.postDelayed(this, UPDATE_UI_INTERVAL_MS);
        }
    };

    /**
     * Set the color camera background texture rotation and save the display rotation.
     */
    @SuppressLint("WrongConstant")
    private void setDisplayRotation() {
        Display display = getWindowManager().getDefaultDisplay();
        mDisplayRotation = display.getRotation();

        // We also need to update the camera texture UV coordinates. This must be run in the OpenGL
        // thread.
        mSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mIsConnected) {
                    mRenderer.updateColorCameraTextureUvGlThread(mDisplayRotation);
                }
            }
        });
    }

    /**
     * Check to see if we have the necessary permissions for this app; ask for them if we don't.
     *
     * @return True if we have the necessary permissions, false if we don't.
     */
    private boolean checkAndRequestPermissions() {
        if (!hasCameraPermission()) {
            requestCameraPermission();
            return false;
        }
        return true;
    }

    /**
     * Check to see if we have the necessary permissions for this app.
     */
    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION) ==
                PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request the necessary permissions for this app.
     */
    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, CAMERA_PERMISSION)) {
            showRequestPermissionRationale();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{CAMERA_PERMISSION},
                    CAMERA_PERMISSION_CODE);
        }
    }

    /**
     * If the user has declined the permission before, we have to explain that the app needs this
     * permission.
     */
    private void showRequestPermissionRationale() {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("Java Point to point Example requires camera permission")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(PointToPointActivity.this,
                                new String[]{CAMERA_PERMISSION}, CAMERA_PERMISSION_CODE);
                    }
                })
                .create();
        dialog.show();
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
                Toast.makeText(PointToPointActivity.this,
                        getString(resId), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    /**
     * Result for requesting camera permission.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (hasCameraPermission()) {
            bindTangoService();
        } else {
            Toast.makeText(this, "Java Point to point Example requires camera permission",
                    Toast.LENGTH_LONG).show();
        }
    }
    public void DrawPoints(String response){
        //dividir os pontos aqui
        MeasuredPoint curDepthPoint = null;
        //{"version":1.2,"people":[{"pose_keypoints_2d":[0.452,0.343,0.867505,0.502,0.774,0.848145,0.847,0.271,0.821732,0.388,0.606,0.840489,0.522,0.903,0.811624,0.438,0.455,0.807057,0.873,0.84,0.886421,0.819,0.061,0.84181,0.614,0.706,0.671921,0.865,0.96,0.832892,0.324,0.11,0.826998,0.624,0.92,0.670989,0.443,0.32,0.820865,0.447,0.63,0.809832,0.799,0.605,0.846628,0.378,0.717,0.896872,0.455,0.456,0.980416,0.907,0.776,0.916141],"face_keypoints_2d":[],"hand_left_keypoints_2d":[],"hand_right_keypoints_2d":[],"pose_keypoints_3d":[],"face_keypoints_3d":[],"hand_left_keypoints_3d":[],"hand_right_keypoints_3d":[]}]}
        String[] people = response.split("pose_keypoints_2d\\\":\\[");
        for(int j=1; j < people.length;j++){
            String[] keypointsString = people[j].split("]");
            Log.i("SkeletonKeypointsString", keypointsString[0]);
            String[] keypoints = keypointsString[0].split(",");
            Log.i("SkeletonKeypointsSize",Integer.toString(keypoints.length));//must be 54, its 18 points, composed by x,y, and confidence
            for(int i = 0 ; i< keypoints.length; i = i+3) {
                //Log.i("SkeletonKeypointsXY, "x:"+keypoints[i] + " y:"+keypoints[i+1]);

                //colocar os pontos na point cloud aqui
                try {
                    // Place point near the clicked point using the latest point cloud data.
                    // Synchronize against concurrent access to the RGB timestamp in the OpenGL thread
                    // and a possible service disconnection due to an onPause event.
                    synchronized (this) {
                        curDepthPoint = getDepthAtPosition(Float.parseFloat(keypoints[i]), Float.parseFloat(keypoints[i+1]));
                    }
                    if (curDepthPoint != null) {//if the new point isnt null and it has x,y, and z
                        Log.i("Point3D", "x:"+curDepthPoint.coords[2] + " y:"+curDepthPoint.coords[1] + " z:"+ curDepthPoint.coords[0]);
                        //põe os skeleton points em 3D no stack e ativa a função de atualizar esqueletos na thread do openGL
                        skeletonPoints.add(curDepthPoint);
                    }else {
                        Log.w(TAG, "Point was null.");
                    }

                } catch (TangoException t) {
                    Toast.makeText(getApplicationContext(),
                            R.string.failed_measurement,
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, getString(R.string.failed_measurement), t);
                } catch (SecurityException t) {
                    Toast.makeText(getApplicationContext(),
                            R.string.failed_permissions,
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, getString(R.string.failed_permissions), t);
                }

            }
        }
        updateSkeleton = true;
    }

    public void sendImage(TangoImageBuffer buffer){
        //before sending the image save the point cloud and the pose
        savedPointCloud = mPointCloudManager.getLatestPointCloud();
        savedImageBuffer = buffer;
        if (mBilateralBox.isChecked()) {
            savedRgbTimestamp = savedImageBuffer.timestamp; // CPU.
        } else {
            savedRgbTimestamp = mRgbTimestampGlThread; // GPU.
        }

        Log.w(TAG, "Timestamp of point:  " + savedRgbTimestamp);

        savedDepthlTcolorPose = TangoSupport.getPoseAtTime(
                savedRgbTimestamp,
                TangoPoseData.COORDINATE_FRAME_CAMERA_DEPTH,
                TangoPoseData.COORDINATE_FRAME_CAMERA_COLOR,
                TangoSupport.ENGINE_TANGO,
                TangoSupport.ENGINE_TANGO,
                TangoSupport.ROTATION_IGNORED);
        if (savedDepthlTcolorPose.statusCode != TangoPoseData.POSE_VALID) {
            Log.w(TAG, "Could not get color camera transform at time "
                    + savedRgbTimestamp);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        YuvImage yuvImage = new YuvImage(buffer.data.array(), ImageFormat.NV21,buffer.width, buffer.height, null);
        yuvImage.compressToJpeg(new Rect(0, 0, buffer.width, buffer.height), 70, out);
        byte[] imgbyte = out.toByteArray();
        Bitmap storedBitmap = BitmapFactory.decodeByteArray(imgbyte, 0, imgbyte.length, null);

        android.graphics.Matrix mat = new android.graphics.Matrix();
        mat.postRotate(90);  // angle is the desired angle you wish to rotate
        storedBitmap = Bitmap.createBitmap(storedBitmap, 0, 0, storedBitmap.getWidth(), storedBitmap.getHeight(), mat, true);
        imgbyte = getBytesFromBitmap(storedBitmap);

        if(mDummieSkeleton.isChecked()){
            DrawPoints("{\"version\":1.2,\"people\":[{\"pose_keypoints_2d\":[0.330691,0.164091,0.934238,0.330702,0.28955,0.85941,0.175143,0.27871,0.857185,0.0878041,0.404177,0.846443,0.0124852,0.532487,0.426256,0.486037,0.292354,0.851642,0.55875,0.420536,0.846238,0.636182,0.554357,0.906915,0.218955,0.56257,0.662926,0.209405,0.731734,0.843959,0.214184,0.862767,0.824996,0.403335,0.576102,0.718475,0.379163,0.728967,0.775835,0.36949,0.865598,0.810001,0.291818,0.147609,0.94949,0.369184,0.144912,0.966644,0.247821,0.169541,0.874044,0.413042,0.16677,0.902624],\"face_keypoints_2d\":[],\"hand_left_keypoints_2d\":[],\"hand_right_keypoints_2d\":[],\"pose_keypoints_3d\":[],\"face_keypoints_3d\":[],\"hand_left_keypoints_3d\":[],\"hand_right_keypoints_3d\":[]}]}");
        }else{
            Log.d("CameraDemo", "Tamanho do buffer enviado:" + Integer.toString(imgbyte.length));
            if(imgbyte.length>0){
                Client myClient = new Client(ServerAddress
                        , Integer.parseInt(ServerPort)
                        , imgbyte
                        ,this);
                myClient.execute();
            }
        }
    }
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }
    // matrix-vector multiplication (y = Matrix a * 3Dcoords x)
    public static float[] multiply(float[] a, float[] x) {
        int m = 3;//number of lines
        int n = 3;//number of collums
        if (x.length != n) throw new RuntimeException("Illegal matrix dimensions.");
        float[] y = new float[m];

        for (int j = 0; j < n; j++) //point* matrix
            for (int i = 0; i < m; i++)
                y[j] += a[i*n + j] * x[i];

        /*
        for (int i = 0; i < m; i++) //matrix * point
            for (int j = 0; j < n; j++)
                y[i] += a[i*n+j] * x[j];
        */
        return y;
    }

}
