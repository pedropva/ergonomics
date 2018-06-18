package com.projecttango.examples.java.pointtopoint.Classification;

import android.util.Log;

import org.rajawali3d.math.vector.Vector3;

import java.util.List;
import java.util.Stack;

/**
 * Created by pedropva on 11/06/2018.
 */

public class Skeleton {
    public Joint Nose;
    public Joint Neck;
    public Joint RShoulder;
    public Joint RElbow;
    public Joint RWrist;
    public Joint LShoulder;
    public Joint LElbow;
    public Joint LWrist;
    public Joint RHip;
    public Joint RKnee;
    public Joint RAnkle;
    public Joint LHip;
    public Joint LKnee;
    public Joint LAnkle;
    public Joint REye;
    public Joint LEye;
    public Joint REar;
    public Joint LEar;

    public Skeleton(){}
    public Skeleton(Stack<Vector3> points){
        for(int i = 0 ; i< points.size(); i++) {
            if(i == 0){
                Nose = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }else if(i == 1){
                Neck = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }else if(i == 2){
                RShoulder = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }
            else if(i == 3){
                RElbow = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }
            else if(i == 4){
                RWrist = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }
            else if(i == 5){
                LShoulder = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }
            else if(i == 6){
                LElbow = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }
            else if(i == 7){
                LWrist = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }
            else if(i == 8){
                RHip = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }
            else if(i == 9){
                RKnee = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }
            else if(i == 10){
                RAnkle = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }
            else if(i == 11){
                LHip = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }
            else if(i == 12){
                LKnee = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }else if(i == 13){
                LAnkle = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }
            else if(i == 14){
                REye = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }
            else if(i == 15){
                LEye = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }
            else if(i == 16){
                REar = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }
            else if(i == 17){
                LEar = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }
        }
    }
    public void setJoints(Stack<Vector3> points){
        for(int i = 0 ; i< points.size(); i++) {
            if(i == 0){
                Nose = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }else if(i == 1){
                Neck = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }else if(i == 2){
                RShoulder = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }
            else if(i == 3){
                RElbow = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }
            else if(i == 4){
                RWrist = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }
            else if(i == 5){
                LShoulder = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }
            else if(i == 6){
                LElbow = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }
            else if(i == 7){
                LWrist = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }
            else if(i == 8){
                RHip = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }
            else if(i == 9){
                RKnee = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }
            else if(i == 10){
                RAnkle = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }
            else if(i == 11){
                LHip = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }
            else if(i == 12){
                LKnee = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }else if(i == 13){
                LAnkle = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }
            else if(i == 14){
                REye = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }
            else if(i == 15){
                LEye = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }
            else if(i == 16){
                REar = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }
            else if(i == 17){
                LEar = new Joint(points.get(i).x,points.get(i).y,points.get(i).z);
            }
        }
    }
    public boolean isComplete(){
        if(LEar != null){
            return true;
        }else{
            return false;
        }
    }
}
