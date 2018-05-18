using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OWAS
{
    class Point3D
    {
         public  double X {get; set;}
         public double Y{get; set;}
         public double Z{get; set;}

    // constructor
     public Point3D(){ }

    //parametrised constructor
    public Point3D(  double x,   double y,  double z){
         this.X = x;
        this.Y = y;
        this.Z = z;
  
    }

    // copy constructor
    public Point3D(  Point3D Point){
        X = Point.X;
        Y = Point.Y;
        Z = Point.Z;
      }
    
      
    //Saber a distância entre este ponto e ponto parâmetro
    // find the distance between this point and the parameter point
     public double DistanceTo(  Point3D  Point ){
          double xval = X - Point.X;
          double yval  = Y - Point.Y;
          double zval = Z - Point.Z;
        return System.Math.Sqrt(xval * xval + yval * yval + zval * zval);
   }

       
    // checks whether this point is equal to the parameter point
        //verifica se esse ponto é igual ao ponto de parâmetro
    public Boolean IsEqualTo(  Point3D Point){
        if( (X == Point.X) && (Y == Point.Y) && (Z == Point.Z) ){
            return true;
        }
        return false;
    }
    
    
        // translate this point by the parameter vector
        //traduzir este ponto pelo vetor de parâmetros
    public void TranslateBy(  Vector3D Vec){
        if(Vec.Length() > 1.0) {
            X = X + Vec.X;
            Y = Y + Vec.Y;
            Z = Z + Vec.Z;
          
        }
    
    }
/*
    ' transform this point by the parameter matrix
    'Public Sub TransformBy(ByVal Mat As Matrix3D)
    '    Dim xx As Double = 0, yy As Double = 0, zz As Double = 0
    '    xx = (X * Mat.matrix.GetValue(0, 0)) + (Y * Mat.matrix.GetValue(1, 0)) + _
    '         (Z * Mat.matrix.GetValue(2, 0)) + (Mat.matrix.GetValue(0, 3))

    '    yy = (X * Mat.matrix.GetValue(0, 1)) + (Y * Mat.matrix.GetValue(1, 1)) + _
    '         (Z * Mat.matrix.GetValue(2, 1)) + (Mat.matrix.GetValue(1, 3))

    '    zz = (X * Mat.matrix.GetValue(0, 2)) + (Y * Mat.matrix.GetValue(1, 2)) + _
    '         (Z * Mat.matrix.GetValue(2, 2)) + (Mat.matrix.GetValue(2, 3))

    '    X = xx
    '    Y = yy
    '    Z = zz
    'End Sub
        */

    // add this point with the parameter point
    // and return the result
        //Adicionar este ponto com o ponto de parâmetro E retornar o resultado
    public Point3D Add(  Point3D Point){
       Point3D NewPoint= new Point3D();
        NewPoint.X = X + Point.X;
        NewPoint.Y = Y + Point.Y;
        NewPoint.Z = Z + Point.Z;
        return NewPoint;
    }
        
    // subtract this point with the parameter point and return the result
    // subtrair este Ponto Com o Ponto de parametro e retornar o resultados
    public Point3D Subtract(  Point3D Point){ 
        Point3D NewPoint = new Point3D();
        NewPoint.X = X - Point.X;
        NewPoint.Y = Y - Point.Y;
        NewPoint.Y = Z - Point.Z;
        return NewPoint;
    }

    }
}
