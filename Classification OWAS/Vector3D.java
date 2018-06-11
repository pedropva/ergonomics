/*
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;


public class Vector3D
{
    //the 3d vector class
    // data members - X, Y and Z values
    public double X{get;set;}
    public double Y{get;set;}
    public double Z{get;set;}
    
     // constructor
    public Vector3D(){}

    // parametrised constructor
    public Vector3D(double x,double y,double z){
        this.X = x;
        this.Y = y;
        this.Z = z;
    }

    // copy constructor
    public Vector3D(Vector3D Vec){
        this.X = Vec.X;
        this.Y = Vec.Y;
        this.Z = Vec.Z;
    }
    
     //vector definido a partir de dois pontos
    // set vector from two points
    public Vector3D( Point3D StartPt,  Point3D EndPt ){

        this.X = EndPt.X - StartPt.X;
        this.Y = EndPt.Y - StartPt.Y;
        this.Z = EndPt.Z - StartPt.Z;
        
    }



    // dot product (or scale produt) of this vector and the parameter vector the dot product is a scale
    //produto escalar (ou escala produt) deste vetor e o vetor de parâmetros, O produto escalar é uma escala

    public double DotProduct(  Vector3D Vec){
        return (this.X * Vec.X) + (this.Y * Vec.Y) + (this.Z * Vec.Z);

    }

    //produto escalar (ou escala produt) deste vetor e o vetor de parâmetros, O produto escalar é uma escala
    public double DotProduct(  Vector3D Vec0,  Vector3D Vec){
        return (Vec0.X * Vec.X) + (Vec0.Y * Vec.Y) + (Vec0.Z * Vec.Z);
    }



    // length of this vector ou norma do vetor 
    //comprimento desse vetor
    public  double Length(){
        return System.Math.Sqrt(X * X + Y * Y + Z * Z);
    }
    

     //ind the angle between this vector and the parameter vector
    public double AngleTo(  Vector3D Vec){
       //organizando os vetores antes de calcular o produto escalar 
        Vector3D VectorA;
        Vector3D VectorB;

        VectorA = UnitVector();
        VectorB = Vec.UnitVector();


        double AdotB = DotProduct(VectorA,VectorB);  //produto escalar 
        double ALstarBL  = VectorA.Length() * VectorB.Length();  //produto das normas

        //Normalizando o produto escalar 

        if(ALstarBL == 0){
            return 0.0;
        }
        return System.Math.Acos(AdotB / ALstarBL);

        //double angle = Math.Acos(dot_pro);          angle = angle * 180 / Math.PI;         angle = 180 - angle;  

    }


        //Ângulo Para Graus
    public double AngleToGraus(  Vector3D Vec){
        //Normalizando os vetores antes de calcular o produto escalar 
        Vector3D VectorA; 
        Vector3D VectorB;
        double AngRad=0;

        // do wpf
        VectorA = this;
        VectorB = Vec;

        double AdotB= DotProduct(VectorA,VectorB);  //produto escalar 
        double ALstarBL = VectorA.Length() * VectorB.Length() ; //produto das normas

        //Normalizando o produto escalar 
        if (ALstarBL == 0){
            return 0.0;
        }

        //Return System.Math.Acos(AdotB / ALstarBL)

        if(System.Math.Acos(AdotB / ALstarBL) >= 0 && System.Math.Acos(AdotB / ALstarBL) <= 1.57 ){
            //Se a medida angular esta no intervalo [0,Pi/2]  então angulo é entre os vetores A e B
            AngRad = System.Math.Acos(AdotB / ALstarBL);
        }else{
           if( System.Math.Acos(AdotB / ALstarBL) > 1.57 && System.Math.Acos(AdotB / ALstarBL) <= 3.14){
            //Se a medida angular esta no intervalo [0,Pi/2]  então angulo é entre os vetores A e B
            AngRad = 3.14 - System.Math.Acos(AdotB / ALstarBL);
        }
    }

        //double angle = Math.Acos(dot_pro);          angle = angle * 180 / Math.PI;         angle = 180 - angle;  
        //Returnig the value in graus
    return((AngRad * 180) / 3.14);

}


    //find the unit vector and return it
    //encontrar o vetor unitário e devolvê-lo
public Vector3D UnitVector(){ 
    Vector3D Vec = new Vector3D();
        double len = Length() ;  //Norma do Vetor
        if(len == 0.0) {
            Vec.X = 0.0;
            Vec.Y = 0.0;
            Vec.Z = 0.0;
            return Vec;
        }
        Vec.X = X / len;
        Vec.Y = Y / len;
        Vec.Z = Z / len;
        return Vec;
    }
    
    // checks whether this vector is codirectional to the parameter vector
    //verifica se este vector está codirecional para o vector de parâmetros
    public Boolean IsCodirectionalTo(  Vector3D Vec){
        Vector3D Vec1  = UnitVector();
        Vector3D Vec2  = Vec.UnitVector();
        if (Vec1.X == Vec2.X && Vec1.Y == Vec2.Y && Vec1.Z == Vec2.Z){
            return true;
        }
        return false;
    }

    // checks whether this vector is equal to the parameter vector
    //verifica se este vector é igual ao vector de parâmetros
    public Boolean IsEqualTo(  Vector3D Vec ){
        if( X == Vec.X && Y == Vec.Y && Z == Vec.Z ){
            return true;
        }
        return false;
    }

    //   checks whether this vector is parallel to the parameter vector
    //verifica se este vector é paralelo ao vector de parâmetros
    public Boolean IsParallelTo(  Vector3D Vec){
        Vector3D Vec1 = UnitVector();
        Vector3D Vec2  = Vec.UnitVector();
        if((Vec1.X == Vec2.X && Vec1.Y == Vec2.Y && Vec1.Z == Vec2.Z) ||
            (Vec1.X == -Vec2.X  && Vec1.Y == -Vec2.Y && Vec1.Z == Vec2.Z)){
            return true;
        }
        return false;
    }

    //checks whether this vector is perpendicular to the parameter vector
    //verifica se este vector é perpendicular ao vector de parâmetros
    public Boolean IsPerpendicularTo(  Vector3D Vec){
        double Ang = 0.0;
        Ang = AngleTo(  Vec);
        if( Ang == (90 * System.Math.PI / 180.0)){
            return true;
        }
        return false;
    }

    // checks whether this vector is X axis
    //verifica se esse vetor é eixo X
    public Boolean IsXAxis(){
        if(X != 0.0 && Y == 0.0 && Z == 0.0){
            return true;
        }
        return false;
    }
    
    // checks whether this vector is Y axis
    //verifica se esse vetor é eixo Y
    public Boolean IsYAxis(){
        if(X == 0.0 && Y != 0.0 && Z == 0.0){
            return true;
        }
        return false;
    }
    
   // checks whether this vector is Z axis
   // verifica se esse vetor é eixo Z
    public Boolean IsZAxis(){
        if(X == 0.0 && Y == 0.0 && Z != 0.0){
            return true;
        }
        return false;
    }
    
    // negate this vector
    //negar este vector
    public void Negate(){
        this.X = this.X * -1.0;
        this.Y = this.Y * -1.0;
        this.Z = this.Z * -1.0;
    }
/*
    ' transform this vector with given matrix
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

    // add this vector with the parameter vector and return the result
    //adicionar este vector com o vetor de parâmetros e retornar o resultado
    public Vector3D Add(  Vector3D Vec){
        Vector3D NewVec = new Vector3D();
        NewVec.X = this.X + Vec.X;
        NewVec.Y = this.Y + Vec.Y;
        NewVec.Z = this.Z + Vec.Z;
        return NewVec;
    }

    // subtract this vector with the parameter vector and return the result
    //subtrair este vector com o vetor de parâmetros e retornar o resultado
    public Vector3D Subtract(  Vector3D Vec){
        Vector3D NewVec = new Vector3D();
        NewVec.X = this.X - Vec.X;
        NewVec.Y = this.Y - Vec.Y;
        NewVec.Z = this.Z - Vec.Z;
        return NewVec;
    }

}
    
*/