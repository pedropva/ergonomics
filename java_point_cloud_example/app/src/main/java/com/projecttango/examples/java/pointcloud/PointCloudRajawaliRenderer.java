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

import com.google.atap.tangoservice.TangoPointCloudData;
import com.google.atap.tangoservice.TangoPoseData;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;

import org.rajawali3d.Object3D;
import org.rajawali3d.materials.Material;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Line3D;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.renderer.RajawaliRenderer;

import com.projecttango.examples.java.pointcloud.rajawali.FrustumAxes;
import com.projecttango.examples.java.pointcloud.rajawali.Grid;
import com.projecttango.examples.java.pointcloud.rajawali.PointCloud;

import java.util.Stack;

/**
 * Renderer for Point Cloud data.
 */
public class PointCloudRajawaliRenderer extends RajawaliRenderer {

    private static final float CAMERA_NEAR = 0.01f;
    private static final float CAMERA_FAR = 200f;
    private static final int MAX_NUMBER_OF_POINTS = 60000;

    private Stack<Object3D> skeletonLines;
    private Stack<Object3D> skeletonSpheres;



    private TouchViewHandler mTouchViewHandler;

    // Objects rendered in the scene.
    private PointCloud mPointCloud;
    private FrustumAxes mFrustumAxes;
    private Grid mGrid;

    public PointCloudRajawaliRenderer(Context context) {
        super(context);
        mTouchViewHandler = new TouchViewHandler(mContext, getCurrentCamera());
    }

    @Override
    protected void initScene() {
        mGrid = new Grid(100, 1, 1, 0xFFCCCCCC);
        mGrid.setPosition(0, -1.3f, 0);
        getCurrentScene().addChild(mGrid);

        mFrustumAxes = new FrustumAxes(3);
        getCurrentScene().addChild(mFrustumAxes);

        // Indicate four floats per point since the point cloud data comes
        // in XYZC format.
        mPointCloud = new PointCloud(MAX_NUMBER_OF_POINTS, 4);
        skeletonLines = new Stack();
        skeletonSpheres = new Stack();
        getCurrentScene().addChild(mPointCloud);
        getCurrentScene().setBackgroundColor(Color.WHITE);
        getCurrentCamera().setNearPlane(CAMERA_NEAR);
        getCurrentCamera().setFarPlane(CAMERA_FAR);
        getCurrentCamera().setFieldOfView(37.5);

    }
    

    /**
     * Updates the rendered point cloud. For this, we need the point cloud data and the device pose
     * at the time the cloud data was acquired.
     * NOTE: This needs to be called from the OpenGL rendering thread.
     */
    public void updatePointCloud(TangoPointCloudData pointCloudData, float[] openGlTdepth) {
        mPointCloud.updateCloud(pointCloudData.numPoints, pointCloudData.points);
        Matrix4 openGlTdepthMatrix = new Matrix4(openGlTdepth);
        mPointCloud.setPosition(openGlTdepthMatrix.getTranslation());
        // Conjugating the Quaternion is needed because Rajawali uses left-handed convention.
        mPointCloud.setOrientation(new Quaternion().fromMatrix(openGlTdepthMatrix).conjugate());

    }

    public void clearSkeleton(){
        while(!skeletonLines.empty()) {
            getCurrentScene().removeChild(skeletonLines.pop());
        }
        while(!skeletonSpheres.empty()) {
            getCurrentScene().removeChild(skeletonSpheres.pop());
        }
    }
    public void updateSkeleton(Stack<Vector3> skeletonPoints){
        Log.i("skeleton renderer", "updating Skeleton");

        /*
        Stack points = new Stack();
        points.add(new Vector3(0.2, 0.4, 0));
        points.add(new Vector3(0.4, 0.7, 0.1));
        points.add(new Vector3(0.2, 0.2, 0.3));
        points.add(new Vector3(0.2, 0.4, 0.6));
        Line3D line = new Line3D(points, 10, 0xfffff000);
        Material material = new Material();
        material.setColor(Color.RED);
        line.setMaterial(material);
        getCurrentScene().addChild(line);
        */
        while(!skeletonLines.empty()) {//remove the previous skeleton
            getCurrentScene().removeChild(skeletonLines.pop());
        }
        while(!skeletonSpheres.empty()) {
            getCurrentScene().removeChild(skeletonSpheres.pop());
        }
        if (skeletonPoints != null) {
            for(int i=0;i<skeletonPoints.size()-1;i++){
                Sphere joint;
                joint = new Sphere(0.05f, 24, 24);
                Material m = new Material();
                m.setColor(Color.RED);
                joint.setMaterial(m);
                joint.setPosition(skeletonPoints.get(i));
                skeletonSpheres.add(joint);
                getCurrentScene().addChild(skeletonSpheres.get(skeletonSpheres.size()-1));

                /*currently not drawing bones
                Stack<Vector3> bone = new Stack();
                bone.add(skeletonPoints.get(i));
                bone.add(skeletonPoints.get(i+1));
                skeletonLines.add(new Line3D(bone, 50, Color.RED));
                skeletonLines.get(i).setMaterial(m);
                getCurrentScene().addChild(skeletonLines.get(skeletonLines.size()-1));
                */
            }
        } else {
            skeletonLines = null;
            skeletonSpheres = null;
        }
    }

    /**
     * Updates our information about the current device pose.
     * NOTE: This needs to be called from the OpenGL rendering thread.
     */
    public void updateCameraPose(TangoPoseData cameraPose) {
        float[] rotation = cameraPose.getRotationAsFloats();
        float[] translation = cameraPose.getTranslationAsFloats();
        Quaternion quaternion = new Quaternion(rotation[3], rotation[0], rotation[1], rotation[2]);
        mFrustumAxes.setPosition(translation[0], translation[1], translation[2]);
        // Conjugating the Quaternion is needed because Rajawali uses left-handed convention for
        // quaternions.
        mFrustumAxes.setOrientation(quaternion.conjugate());
        mTouchViewHandler.updateCamera(new Vector3(translation[0], translation[1], translation[2]),
                quaternion);
    }

    @Override
    public void onOffsetsChanged(float v, float v1, float v2, float v3, int i, int i1) {
    }

    @Override
    public void onTouchEvent(MotionEvent motionEvent) {
        mTouchViewHandler.onTouchEvent(motionEvent);
    }

    public void setFirstPersonView() {
        mTouchViewHandler.setFirstPersonView();
    }

    public void setTopDownView() {
        mTouchViewHandler.setTopDownView();
    }

    public void setThirdPersonView() {
        mTouchViewHandler.setThirdPersonView();
    }
}
