package com.projecttango.examples.java.pointtopoint.Classification;

public class Point3D {
         public  double x;
         public double y;
         public double z;
    
    // constructor
     public Point3D(){ }
    
    //parametrised constructor
    public Point3D(  double x,   double y,  double z){
         this.x = x;
        this.y = y;
        this.z = z;
    
    }
    
    // copy constructor
    public Point3D(  Point3D Point){
        x = Point.x;
        y = Point.y;
        z = Point.z;
      }
    
      
    //Saber a dist?ncia entre este ponto e ponto par?metro
    // find the distance between this point and the parameter point
     public double DistanceTo(  Point3D  Point ){
          double xval = x - Point.x;
          double yval  = y - Point.y;
          double zval = z - Point.z;
        return java.lang.Math.sqrt(xval * xval + yval * yval + zval * zval);

    }
    
       
    // checks whether this point is equal to the parameter point
        //verifica se esse ponto ? igual ao ponto de par?metro
    public Boolean IsEqualTo(  Point3D Point){
        if( (x == Point.x) && (y == Point.y) && (z == Point.z) ){
            return true;
        }
        return false;
    }
    
    
        // translate this point by the parameter vector
        //traduzir este ponto pelo vetor de par?metros
    public void TranslateBy(  Vector3D Vec){
        if(Vec.Length() > 1.0) {
            x = x + Vec.X;
            y = y + Vec.Y;
            z = z + Vec.Z;
          
        }
    
    }
    /*
    ' transform this point by the parameter matrix
    'Public Sub TransformBy(ByVal Mat As Matrix3D)
    '    Dim xx As Double = 0, yy As Double = 0, zz As Double = 0
    '    xx = (x * Mat.matrix.GetValue(0, 0)) + (y * Mat.matrix.GetValue(1, 0)) + _
    '         (z * Mat.matrix.GetValue(2, 0)) + (Mat.matrix.GetValue(0, 3))
    
    '    yy = (x * Mat.matrix.GetValue(0, 1)) + (y * Mat.matrix.GetValue(1, 1)) + _
    '         (z * Mat.matrix.GetValue(2, 1)) + (Mat.matrix.GetValue(1, 3))
    
    '    zz = (x * Mat.matrix.GetValue(0, 2)) + (y * Mat.matrix.GetValue(1, 2)) + _
    '         (z * Mat.matrix.GetValue(2, 2)) + (Mat.matrix.GetValue(2, 3))
    
    '    x = xx
    '    y = yy
    '    z = zz
    'End Sub
        */
    
    // add this point with the parameter point
    // and return the result
        //Adicionar este ponto com o ponto de par?metro E retornar o resultado
    public Point3D Add(  Point3D Point){
       Point3D NewPoint= new Point3D();
        NewPoint.x = x + Point.x;
        NewPoint.y = y + Point.y;
        NewPoint.z = z + Point.z;
        return NewPoint;
    }
        
    // subtract this point with the parameter point and return the result
    // subtrair este Ponto Com o Ponto de parametro e retornar o resultados
    public Point3D Subtract(  Point3D Point){ 
        Point3D NewPoint = new Point3D();
        NewPoint.x = x - Point.x;
        NewPoint.y = y - Point.y;
        NewPoint.y = z - Point.z;
        return NewPoint;
    }
}

