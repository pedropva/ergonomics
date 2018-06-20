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

import com.google.atap.tangoservice.TangoPoseData;
import com.google.tango.support.TangoSupport;
import com.projecttango.examples.java.pointtopoint.Classification.Skeleton;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;

import org.rajawali3d.Object3D;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.StreamingTexture;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Line3D;
import org.rajawali3d.primitives.ScreenQuad;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.renderer.Renderer;

import java.util.Stack;

import javax.microedition.khronos.opengles.GL10;

/**
 * Very simple example point-to-point renderer which displays a line fixed in place.
 * When the user clicks the screen, the line is re-rendered with an endpoint
 * placed at the point corresponding to the depth at the point of the click.
 */
public class PointToPointRenderer extends Renderer {
    private static final String TAG = PointToPointRenderer.class.getSimpleName();

    private float[] textureCoords0 = new float[]{0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F};

    private Stack<Sphere> mSkeletonSpheres;
    private Stack<Line3D> mSkeletonLines;
    private Stack<Vector3> mPoints;
    Skeleton skeleton;
    Material m;
    Line3D line;
    Stack<Vector3> pointLine;
    private boolean mSkeletonUpdated = false;

    // Augmented reality related fields.
    private ATexture mTangoCameraTexture;
    private boolean mSceneCameraConfigured;
    private ScreenQuad mBackgroundQuad;

    public PointToPointRenderer(Context context) {
        super(context);
    }

    @Override
    protected void initScene() {
        mSkeletonSpheres = new Stack<Sphere>();
        mSkeletonLines = new Stack<Line3D>();
        mPoints = new Stack<Vector3>();
        pointLine = new Stack<Vector3>();
        skeleton = new Skeleton();
        m = new Material();
        m.setColor(Color.RED);
        // Create a quad covering the whole background and assign a texture to it where the
        // Tango color camera contents will be rendered.
        if (mBackgroundQuad == null) {
            mBackgroundQuad = new ScreenQuad();
            mBackgroundQuad.getGeometry().setTextureCoords(textureCoords0);
        }
        Material tangoCameraMaterial = new Material();
        tangoCameraMaterial.setColorInfluence(0);
        // We need to use Rajawali's {@code StreamingTexture} since it sets up the texture
        // for GL_TEXTURE_EXTERNAL_OES rendering.
        mTangoCameraTexture =
                new StreamingTexture("camera", (StreamingTexture.ISurfaceListener) null);
        try {
            tangoCameraMaterial.addTexture(mTangoCameraTexture);
            mBackgroundQuad.setMaterial(tangoCameraMaterial);
        } catch (ATexture.TextureException e) {
            Log.e(TAG, "Exception creating texture for RGB camera contents", e);
        }
        getCurrentScene().addChildAt(mBackgroundQuad, 0);

        // Add a directional light in an arbitrary direction.
        DirectionalLight light = new DirectionalLight(1, 0.2, -1);
        light.setColor(1, 1, 1);
        light.setPower(0.8f);
        light.setPosition(3, 2, 4);
        getCurrentScene().addLight(light);
    }

    /**
     * Update background texture's UV coordinates when device orientation is changed (i.e., change
     * between landscape and portrait mode).
     * This must be run in the OpenGL thread.
     */
    public void updateColorCameraTextureUvGlThread(int rotation) {
        if (mBackgroundQuad == null) {
            mBackgroundQuad = new ScreenQuad();
        }

        float[] textureCoords =
                TangoSupport.getVideoOverlayUVBasedOnDisplayRotation(textureCoords0, rotation);
        mBackgroundQuad.getGeometry().setTextureCoords(textureCoords, true);
        mBackgroundQuad.getGeometry().reload();
    }

    public void clearSkeleton(){
        while(!mSkeletonSpheres.empty()) {
            getCurrentScene().removeChild(mSkeletonSpheres.pop());
        }
        while(!mSkeletonLines.empty()) {
            getCurrentScene().removeChild(mSkeletonLines.pop());
        }
    }

    @Override
    protected void onRender(long elapsedRealTime, double deltaTime) {
        // Update the AR object if necessary.
        // Synchronize against concurrent access with the setter below.
        synchronized (this) {
            if (mSkeletonUpdated) {
                //clearSkeleton();
                if (mPoints != null) {
                    //Log.w(TAG, Integer.toString(mPoints.size()));
                    for(int i=0;i<mPoints.size();i++){
                        if(!mSkeletonSpheres.empty() && mSkeletonSpheres.size() == mPoints.size() && !mPoints.get(i).isZero()) {//if the points already exist, just move them
                            mSkeletonSpheres.get(i).setPosition(mPoints.get(i));
                        }else if(!mPoints.get(i).isZero()){
                            Sphere joint;
                            joint = new Sphere(0.03f, 6, 6);
                            joint.setMaterial(m);
                            mSkeletonSpheres.add(joint);
                            getCurrentScene().addChild(mSkeletonSpheres.get(mSkeletonSpheres.size()-1));
                        }
                    }
                    if(mPoints.size() == 18 && mSkeletonLines.empty()) {
                        skeleton.setJoints(mPoints);
                        if (skeleton.Nose.isKnow() && skeleton.Neck.isKnow()) {
                            //Neck
                            pointLine.clear();
                            pointLine.push(skeleton.Nose.toVector3());
                            pointLine.push(skeleton.Neck.toVector3());
                            line = new Line3D(pointLine, 50, Color.RED);
                            line.setMaterial(m);
                            mSkeletonLines.add(line);
                            getCurrentScene().addChild(mSkeletonLines.get(mSkeletonLines.size() - 1));
                        }if (skeleton.Neck.isKnow() && skeleton.LShoulder.isKnow()) {
                            //Left Shoulder
                            pointLine.clear();
                            pointLine.push(skeleton.Neck.toVector3());
                            pointLine.push(skeleton.LShoulder.toVector3());
                            line = new Line3D(pointLine, 50, Color.RED);
                            line.setMaterial(m);
                            mSkeletonLines.add(line);
                            getCurrentScene().addChild(mSkeletonLines.get(mSkeletonLines.size() - 1));
                        }if (skeleton.Neck.isKnow() && skeleton.RShoulder.isKnow()) {
                            //Right Shoulder
                            pointLine.clear();
                            pointLine.push(skeleton.Neck.toVector3());
                            pointLine.push(skeleton.RShoulder.toVector3());
                            line = new Line3D(pointLine, 50, Color.RED);
                            line.setMaterial(m);
                            mSkeletonLines.add(line);
                            getCurrentScene().addChild(mSkeletonLines.get(mSkeletonLines.size() - 1));
                        }if (skeleton.LShoulder.isKnow() && skeleton.LElbow.isKnow()) {
                            //Left UpperArm
                            pointLine.clear();
                            pointLine.push(skeleton.LShoulder.toVector3());
                            pointLine.push(skeleton.LElbow.toVector3());
                            line = new Line3D(pointLine, 50, Color.RED);
                            line.setMaterial(m);
                            mSkeletonLines.add(line);
                            getCurrentScene().addChild(mSkeletonLines.get(mSkeletonLines.size() - 1));
                        }if (skeleton.RShoulder.isKnow() && skeleton.RElbow.isKnow()) {
                            //Right UpperArm
                            pointLine.clear();
                            pointLine.push(skeleton.RShoulder.toVector3());
                            pointLine.push(skeleton.RElbow.toVector3());
                            line = new Line3D(pointLine, 50, Color.RED);
                            line.setMaterial(m);
                            mSkeletonLines.add(line);
                            getCurrentScene().addChild(mSkeletonLines.get(mSkeletonLines.size() - 1));
                        }if (skeleton.LElbow.isKnow() && skeleton.LWrist.isKnow()) {
                            //Left LowerArm
                            pointLine.clear();
                            pointLine.push(skeleton.LElbow.toVector3());
                            pointLine.push(skeleton.LWrist.toVector3());
                            line = new Line3D(pointLine, 50, Color.RED);
                            line.setMaterial(m);
                            mSkeletonLines.add(line);
                            getCurrentScene().addChild(mSkeletonLines.get(mSkeletonLines.size() - 1));
                        }if (skeleton.RElbow.isKnow() && skeleton.RWrist.isKnow()) {
                            //Right LowerArm
                            pointLine.clear();
                            pointLine.push(skeleton.RElbow.toVector3());
                            pointLine.push(skeleton.RWrist.toVector3());
                            line = new Line3D(pointLine, 50, Color.RED);
                            line.setMaterial(m);
                            mSkeletonLines.add(line);
                            getCurrentScene().addChild(mSkeletonLines.get(mSkeletonLines.size() - 1));
                        }if (skeleton.Neck.isKnow() && skeleton.LHip.isKnow()) {
                            //Spine Left
                            pointLine.clear();
                            pointLine.push(skeleton.Neck.toVector3());
                            pointLine.push(skeleton.LHip.toVector3());
                            line = new Line3D(pointLine, 50, Color.RED);
                            line.setMaterial(m);
                            mSkeletonLines.add(line);
                            getCurrentScene().addChild(mSkeletonLines.get(mSkeletonLines.size() - 1));
                        }if (skeleton.Neck.isKnow() && skeleton.RHip.isKnow()) {
                            //Spine Right
                            pointLine.clear();
                            pointLine.push(skeleton.Neck.toVector3());
                            pointLine.push(skeleton.RHip.toVector3());
                            line = new Line3D(pointLine, 50, Color.RED);
                            line.setMaterial(m);
                            mSkeletonLines.add(line);
                            getCurrentScene().addChild(mSkeletonLines.get(mSkeletonLines.size() - 1));
                        }if (skeleton.LHip.isKnow() && skeleton.LKnee.isKnow()) {
                            //Left Thigh
                            pointLine.clear();
                            pointLine.push(skeleton.LHip.toVector3());
                            pointLine.push(skeleton.LKnee.toVector3());
                            line = new Line3D(pointLine, 50, Color.RED);
                            line.setMaterial(m);
                            mSkeletonLines.add(line);
                            getCurrentScene().addChild(mSkeletonLines.get(mSkeletonLines.size() - 1));
                        }if (skeleton.RHip.isKnow() && skeleton.RKnee.isKnow()) {
                            //Right Thigh
                            pointLine.clear();
                            pointLine.push(skeleton.RHip.toVector3());
                            pointLine.push(skeleton.RKnee.toVector3());
                            line = new Line3D(pointLine, 50, Color.RED);
                            line.setMaterial(m);
                            mSkeletonLines.add(line);
                            getCurrentScene().addChild(mSkeletonLines.get(mSkeletonLines.size() - 1));
                        }if (skeleton.LKnee.isKnow() && skeleton.LAnkle.isKnow()) {
                            //Left Calf
                            pointLine.clear();
                            pointLine.push(skeleton.LKnee.toVector3());
                            pointLine.push(skeleton.LAnkle.toVector3());
                            line = new Line3D(pointLine, 50, Color.RED);
                            line.setMaterial(m);
                            mSkeletonLines.add(line);
                            getCurrentScene().addChild(mSkeletonLines.get(mSkeletonLines.size() - 1));
                        }if (skeleton.RKnee.isKnow() && skeleton.RAnkle.isKnow()) {
                            //Right Calf
                            pointLine.clear();
                            pointLine.push(skeleton.RKnee.toVector3());
                            pointLine.push(skeleton.RAnkle.toVector3());
                            line = new Line3D(pointLine, 50, Color.RED);
                            line.setMaterial(m);
                            mSkeletonLines.add(line);
                            getCurrentScene().addChild(mSkeletonLines.get(mSkeletonLines.size() - 1));
                        }
                    }
                }
                mSkeletonUpdated = false;
            }

        }

        super.onRender(elapsedRealTime, deltaTime);
    }

    public synchronized void setSkeleton(Stack<Vector3> points) {
        mPoints = points;
        mSkeletonUpdated = true;
    }

    /**
     * Update the scene camera based on the provided pose in Tango start of service frame.
     * The camera pose should match the pose of the camera color at the time of the last rendered
     * RGB frame, which can be retrieved with this.getTimestamp();
     * <p/>
     * NOTE: This must be called from the OpenGL render thread; it is not thread safe.
     */
    public void updateRenderCameraPose(TangoPoseData cameraPose) {
        float[] rotation = cameraPose.getRotationAsFloats();
        float[] translation = cameraPose.getTranslationAsFloats();
        Quaternion quaternion = new Quaternion(rotation[3], rotation[0], rotation[1], rotation[2]);
        // Conjugating the Quaternion is needed because Rajawali uses left-handed convention for
        // quaternions.
        getCurrentCamera().setRotation(quaternion.conjugate());
        getCurrentCamera().setPosition(translation[0], translation[1], translation[2]);
    }

    /**
     * It returns the ID currently assigned to the texture where the Tango color camera contents
     * should be rendered.
     * NOTE: This must be called from the OpenGL render thread; it is not thread safe.
     */
    public int getTextureId() {
        return mTangoCameraTexture == null ? -1 : mTangoCameraTexture.getTextureId();
    }

    /**
     * We need to override this method to mark the camera for re-configuration (set proper
     * projection matrix) since it will be reset by Rajawali on surface changes.
     */
    @Override
    public void onRenderSurfaceSizeChanged(GL10 gl, int width, int height) {
        super.onRenderSurfaceSizeChanged(gl, width, height);
        mSceneCameraConfigured = false;
    }

    public boolean isSceneCameraConfigured() {
        return mSceneCameraConfigured;
    }

    /**
     * Sets the projection matrix for the scene camera to match the parameters of the color camera,
     * provided by the {@code TangoCameraIntrinsics}.
     */
    public void setProjectionMatrix(float[] matrixFloats) {
        getCurrentCamera().setProjectionMatrix(new Matrix4(matrixFloats));
    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset,
                                 float xOffsetStep, float yOffsetStep,
                                 int xPixelOffset, int yPixelOffset) {
    }

    @Override
    public void onTouchEvent(MotionEvent event) {

    }
}
