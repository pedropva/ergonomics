package com.projecttango.examples.java.pointtopoint.Classification;

import org.rajawali3d.math.vector.Vector3;

/**
 * Created by pedropva on 13/06/2018.
 */

public class Joint {
    public double x;
    public double y;
    public double z;
    public Joint(){}
    public Joint(double x,double y,double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public boolean isKnow(){
        if(this.x != 0f && this.y != 0f && this.z != 0f){
            return true;
        }else{
            return false;
        }
    }
    public Vector3 toVector3(){
        return new Vector3(this.x,this.y,this.z);
    }
}
