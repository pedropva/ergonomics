
package com.projecttango.examples.java.pointtopoint.Classification;

import org.rajawali3d.math.vector.Vector3;

import java.util.ArrayList;
import java.util.List;

public class OWAS_classification
{
    private Skeleton _Skeleton;
    //private Tuple<float, float, float, float> FloorClipPlane;

    public OWAS_classification(Skeleton skeleton)// Tuple<float, float, float, float> _floor
    {
        _Skeleton = skeleton;
        //FloorClipPlane = _floor;
    }




    //COSTAS

    //Pega a posição das costas
    public static List<String> getNameCostaPositions() {
        List<String> posicaoCostas= new ArrayList<String>();

        posicaoCostas.add("Desconhecido");
        posicaoCostas.add("Ereta");
        posicaoCostas.add("Inclinada");
        posicaoCostas.add("Ereta e Torcida");
        posicaoCostas.add("Inclinada e Torcida");

        return posicaoCostas;
    }

    public CostaPosicao getBackPosition()
    {
        CostaPosicao posicao = CostaPosicao.Desconhecida;
        if (this._Skeleton != null)
        {
            posicao = this.getCostaPosicaoBySkeleton();
        }
        return posicao;
    }


    //Pega a posição das costas
    public static List<String> getNameBracosPositions()
    {
        List<String> posicao = new ArrayList<String>();

        posicao.add("Desconhecido");
        posicao.add("Braços abaixados");
        posicao.add("Uma mão levantada");
        posicao.add("Duas mão levantadas");

        return posicao;
    }

    public BracosPosicao getBracoPosition()
    {
        BracosPosicao posicao = BracosPosicao.Desconhecida;
        if (this._Skeleton != null)
        {
            posicao = this.GetArmsClassification();
        }
        return posicao;
    }


    //Pega a posição das pernas
    public static List<String> getNamePernasPositions()
    {
        List<String> posicao = new ArrayList<String>();

        posicao.add("Desconhecida");
        posicao.add("Pernas estão retas");
        posicao.add("Uma perna reta");
        posicao.add("Pernas flexionadas");
        posicao.add("Uma perna flexionada");
        posicao.add("Uma perna ajoelhada");

        return posicao;
    }


    public PernasPosicao getPernasPosition()
    {
        PernasPosicao posicao = PernasPosicao.Desconhecida;
        if (this._Skeleton != null)
        {
            posicao = this.GetLegsClassification();//FloorClipPlane
        }
        return posicao;
    }




    private CostaPosicao getCostaPosicaoBySkeleton()
    {

        Skeleton skeleton = this._Skeleton;
        int HipCenter =0 ;
        //Declaração de variáveis
        Joint SkeletonPointShoulderCenter = new Joint();
        Joint SkeletonPointShoulderRight = new Joint();
        Joint SkeletonPointShoulderLeft = new Joint();

        Joint SkeletonPointHipCenter = new Joint();
        Joint SkeletonPointHipLeft = new Joint();
        Joint SkeletonPointHipRight = new Joint();
        Joint SkeletonPointSpine = new Joint();

        boolean isReto;
        boolean isTorcido;

        CostaPosicao posicaoCostas;

        //Pega a posição das costas pelo esqueleto
        if (skeleton == null)
            return CostaPosicao.Ereta;

        //Calcula posicação das costas

        //Verifica se está torcido
        if (Math.Point3Y0GetAngleBetween( SkeletonPointShoulderRight,  SkeletonPointShoulderLeft,  SkeletonPointHipRight,  SkeletonPointHipLeft) >= 5)
        {
            isTorcido = true;
        }
        else
        {
            isTorcido = false;
        }

        //Verifica se está reta
        if (Math.Point3Z0GetAngleBetween( SkeletonPointHipCenter,  SkeletonPointShoulderCenter) >= 80)
        {
            isReto = true;
        }
        else
        {
            isReto = false;
        }

        posicaoCostas = OWASClassificationSpine(isReto, isTorcido);

        return posicaoCostas;
    }



    //Classificação da espinha
    private CostaPosicao OWASClassificationSpine(boolean IsReto, boolean IsTorcido)
    {
        //---------------------------------------------------------------------------------------------------
        //OWAS Classification of SPINE
        //1)	Reto
        //2)	Inclinado
        //3)	Reto e Torcido
        //4)    Inclinado e Torcido
        //---------------------------------------------------------------------------------------------------

        //Inicialize the value
        CostaPosicao posicao = CostaPosicao.Desconhecida;

        if (IsReto == true & IsTorcido == false)
        {
            posicao = CostaPosicao.Ereta;
            //Console.Write("Ereta");
        }

        if (IsReto == false & IsTorcido == false)
        {
            posicao = CostaPosicao.Inclinada;
            //Console.Write("Inclinada");
        }

        if (IsReto == true & IsTorcido == true)
        {
            posicao = CostaPosicao.EretaETorcida;
            //Console.Write("Torcida");
        }
        if (IsReto == false & IsTorcido == true)
        {
            posicao = CostaPosicao.InclinadaETorcida;
            //Console.Write("Inclinada e torcida");
        }

        return posicao;
    }



    //BRACOS


    //se esta para cima
    private boolean IsAbove(Joint FirstPoint, Joint SecondPoint)
    {

        if (FirstPoint.y > SecondPoint.y)
        {
            return true;
        }
        else
        {
            return false;
        }

    }



    private boolean ArmsAboveShouder(Joint CenterShouderPoint, Joint HandPoint)
    {

        if (HandPoint.y > CenterShouderPoint.y)
        {
            return true;
        }
        else
        {
            return false;
        }


    }

    //classificacao dos bracos
    private BracosPosicao ArmsOWASClassification(Joint SkeletonPointHandLeft, Joint SkeletonPointShouderLeft, Joint SkeletonPointElbowLeft, Joint SkeletonPointHandRight, Joint SkeletonPointShouderRight, Joint SkeletonPointElbowRight)
    {
        //---------------------------------------------------------------------------------------------------
        //Classification of Arms
        //1 - Both Arms are Down:  Right and Left Hand below shouder and  Right and Left Elbow  below shouder
        //2 - One Hand up : At least one hand above shouder
        //3 - Two Hand Up: Right and Left Hand above
        // 1 Ambos os braços para baixo: Direita e Esquerda Mão abaixo do ombro e Direita e Esquerda cotovelo abaixo do ombro
        // 2-uma mao para cima:Pelo menos uma das mãos acima ombro
        // 3-duas maos para cima:mão direita e esquerda acima;
        //---------------------------------------------------------------------------------------------------
        //Inicialize the value
        BracosPosicao posicaoBraco = BracosPosicao.Desconhecida;
        if ((IsAbove(SkeletonPointHandLeft, SkeletonPointShouderLeft) == true & IsAbove(SkeletonPointElbowLeft, SkeletonPointShouderLeft) == true) & (IsAbove(SkeletonPointHandRight, SkeletonPointShouderRight) == true & IsAbove(SkeletonPointElbowRight, SkeletonPointShouderRight) == true))
        {
            posicaoBraco = BracosPosicao.TwoHandUp;

        }
        else if ((IsAbove(SkeletonPointHandLeft, SkeletonPointShouderLeft) == true & IsAbove(SkeletonPointElbowLeft, SkeletonPointShouderLeft) == true) | (IsAbove(SkeletonPointHandRight, SkeletonPointShouderRight) == true & IsAbove(SkeletonPointElbowRight, SkeletonPointShouderRight) == true))
        {
            posicaoBraco = BracosPosicao.OneHandUp;

        }
        else
        {

            posicaoBraco = BracosPosicao.BothArmsareDown;
        }
        return posicaoBraco;
    }


    private BracosPosicao GetArmsClassification()
    {
        Skeleton skeleton = this._Skeleton;
        //Arms Position
        Joint SkeletonPointHandLeft = new Joint();
        Joint SkeletonPointHandRight = new Joint();
        Joint SkeletonPointShouderLeft = new Joint();
        Joint SkeletonPointShouderRight = new Joint();
        Joint SkeletonPointElbowLeft = new Joint();
        Joint SkeletonPointElbowRight = new Joint();
        BracosPosicao posicaoBracos = BracosPosicao.Desconhecida;





        //Loop  into all skeleton joints
        //      For i As Integer = 0 To skeleton.Joints.Count - 1
        //  Rotacao = skeleton.BoneOrientations(Spine).HierarchicalRotation
        //Rotacao.Matrix.M11.ToString()
        //Rotacao.Matrix.M21.ToString()
        //Rotacao.Matrix.M31.ToString()
        //Rotacao.Matrix.M41.ToString()
        //Matrix3D hipCenterMatrix = skeleton.GetRelativeJointMatrix("hipcenter");


        posicaoBracos = ArmsOWASClassification(SkeletonPointHandLeft, SkeletonPointShouderLeft, SkeletonPointElbowLeft, SkeletonPointHandRight, SkeletonPointShouderRight, SkeletonPointElbowRight);



        return posicaoBracos;
    }




    /*
    //PERNAS
    //Este teste de função se o pé está no mesmo nível do Piso
    private boolean IsFloorPlane(Joint SkeletonPointAnkle, Tuple<float, float, float, float> FloorPlane, double MaxGroudLevelValue)
    {
        //This function test if the Foot is in the same level as Floor
        //Plane Equation    Ax +By + Cz + D = 0
        if ((FloorPlane.Item1 * SkeletonPointAnkle.x) + (FloorPlane.Item2 * SkeletonPointAnkle.x) + (FloorPlane.Item3 * SkeletonPointAnkle.z) + FloorPlane.Item4 > MaxGroudLevelValue)
        {
            return false;
        }
        else
        {
            return true;
        }

    }
    */
        /*//TODO RODAR A CLASSIFICAÇÃO DAS PERNAS
        public PernasPosicao LegOWASClassification(Joint SkeletonPointAnkleRight, Joint SkeletonPointKneeRight,
                                                    Joint SkeletonPointAnkleLeft, Joint SkeletonPointKneeLeft,
                                                 Joint SkeletonPointHipLeft, Joint SkeletonPointHipRight,
                                                    Tuple<float, float, float, float> FloorPlane)
        {


            PernasPosicao posicaoPernas = PernasPosicao.Desconhecida;
            //---------------------------------------------------------------------------------------------------
            //Classification of Leg
            //1)	Duas pernas retas: As duas pernas esta no chão (classificação 2 = True) e as duas penas estão retas (classificação 1 = True para as duas pernas) .
            //2)	Uma perna reta :  As duas pernas não estão no chão (classificação 2 = False) e uma das pernas  está reta ( classificação 1 = False)
            //3)	Duas pernas flexionadas: As duas pernas esta no chão ( classificação 2 = True) e as duas penas não estão retas (classificação 1 =False  para as duas pernas) .
            //4)	Uma Perna Flexionada: As duas pernas não estão no chão (classificação 2 = False) e a perna que está no chão não está reta ( classificação 1 = False)
            //5)	Uma perna Ajoelhada: As duas pernas esta no chão ( classificação 2 = True) e as duas pernas estão flexionada (classificação 1 =False  para as duas pernas ) e pelo menos uma perna está ajoelhada (classificação 1 = False)
            //---------------------------------------------------------------------------------------------------
            //Inicialize the value

            Point3D PointHipLeft = new Point3D();
            Point3D PointKneeLeft = new Point3D();
            Point3D PointHipRight = new Point3D();
            Point3D PointKneeRight = new Point3D();

            //to do: Mudar parametros da função
            //IsStraightByDotProduct(SkeletonPointKneeRight, SkeletonPointKneeRight, SkeletonPointHipRight, 2.8)

            //Perna Reta: Point3Z0GetAngleBetween(XMLSkeletonPointKneeRight, XMLSkeletonPointKneeRight) > 80
            //Pé no Chão: IsFloorPlane(SkeletonPointKneeRight, FloorPlane, 0.1)


            PointHipLeft.x = SkeletonPointHipLeft.x;
            PointHipLeft.y = SkeletonPointHipLeft.y;
            PointHipLeft.z = SkeletonPointHipLeft.z;


            PointKneeLeft.x = SkeletonPointKneeLeft.x;
            PointKneeLeft.y = SkeletonPointKneeLeft.y;
            PointKneeLeft.z = SkeletonPointKneeLeft.z;



            PointHipRight.x = SkeletonPointHipRight.x;
            PointHipRight.y = SkeletonPointHipRight.y;
            PointHipRight.z = SkeletonPointHipRight.z;


            PointKneeRight.x = SkeletonPointKneeRight.x;
            PointKneeRight.y = SkeletonPointKneeRight.y;
            PointKneeRight.z = SkeletonPointKneeRight.z;

            if ((Math.Point3Z0GetAngleBetween( SkeletonPointKneeRight,  SkeletonPointAnkleRight) > 80 & IsFloorPlane(SkeletonPointAnkleRight, FloorPlane, 0.1)) & (Math.Point3Z0GetAngleBetween( SkeletonPointKneeLeft,  SkeletonPointAnkleLeft) > 80 & IsFloorPlane(SkeletonPointAnkleLeft, FloorPlane, 0.1)))
            {
                if ((java.lang.Math.round(PointHipLeft.DistanceTo(PointKneeLeft)) <= 0.35 || java.lang.Math.round(PointHipRight.DistanceTo(PointKneeRight)) <= 0.35))
                {

                    posicaoPernas = PernasPosicao.OneLegknee;
                }
                else
                {
                    posicaoPernas = PernasPosicao.twoLegUp;
                }
            }

            if ((Math.Point3Z0GetAngleBetween( SkeletonPointKneeRight,  SkeletonPointAnkleRight) > 80 & IsFloorPlane(SkeletonPointAnkleRight, FloorPlane, 0.1)) | (Math.Point3Z0GetAngleBetween( SkeletonPointKneeLeft,  SkeletonPointAnkleLeft) > 80 & IsFloorPlane(SkeletonPointAnkleLeft, FloorPlane, 0.1)))
            {

                posicaoPernas = PernasPosicao.OneLegUp;
            }

            if ((Math.Point3Z0GetAngleBetween( SkeletonPointKneeRight,  SkeletonPointAnkleRight) <= 80 & IsFloorPlane(SkeletonPointAnkleRight, FloorPlane, 0.1)) & (Math.Point3Z0GetAngleBetween( SkeletonPointKneeLeft,  SkeletonPointAnkleLeft) <= 80 & IsFloorPlane(SkeletonPointAnkleLeft, FloorPlane, 0.1)))
            {
                if ((java.lang.Math.round(PointHipLeft.DistanceTo(PointKneeLeft)) <= 0.3 | java.lang.Math.round(PointHipRight.DistanceTo(PointKneeRight)) <= 0.3))
                {
                    posicaoPernas = PernasPosicao.OneLegknee;

                }
                else
                {
                    posicaoPernas = PernasPosicao.twoLegFlex;
                }
            }



            if ((Math.Point3Z0GetAngleBetween( SkeletonPointKneeRight,  SkeletonPointAnkleRight) <= 80 & Math.Point3Z0GetAngleBetween( SkeletonPointKneeLeft,  SkeletonPointAnkleLeft) <= 80) & (IsFloorPlane(SkeletonPointAnkleRight, FloorPlane, 0.1) | IsFloorPlane(SkeletonPointAnkleLeft, FloorPlane, 0.1)))
            {
                if ((java.lang.Math.round(PointHipLeft.DistanceTo(PointKneeLeft)) <= 0.3 | java.lang.Math.round(PointHipRight.DistanceTo(PointKneeRight)) <= 0.3))
                {
                    posicaoPernas = PernasPosicao.OneLegknee;
                }
                else
                {
                    posicaoPernas = PernasPosicao.OneLegFlex;
                }
            }
            return posicaoPernas;

        }

        */
    public PernasPosicao GetLegsClassification()//Tuple<float, float, float, float> FloorPlane
    {
        Skeleton skeleton = this._Skeleton;
        //Legs Position
        Joint SkeletonPointAnkleLeft = new Joint();
        Joint SkeletonPointAnkleRight = new Joint();
        Joint SkeletonPointKneeLeft = new Joint();
        Joint SkeletonPointKneeRight = new Joint();

        Joint SkeletonPointHipLeft = new Joint();
        Joint SkeletonPointHipRight = new Joint();


        PernasPosicao posicaoPernas = PernasPosicao.Desconhecida;


        //+(13)	Position:{Microsoft.Kinect.SkeletonPoint} JointType:KneeLeft {13} TrackingState:Inferred {1}	Microsoft.Kinect.Joint
        //+(14)	Position:{Microsoft.Kinect.SkeletonPoint} JointType:AnkleLeft {14} TrackingState:Inferred {1}	Microsoft.Kinect.Joint
        //+(15)	Position:{Microsoft.Kinect.SkeletonPoint} JointType:FootLeft {15} TrackingState:Inferred {1}	Microsoft.Kinect.Joint
        //+(17)	Position:{Microsoft.Kinect.SkeletonPoint} JointType:KneeRight {17} TrackingState:Inferred {1}	Microsoft.Kinect.Joint
        //+(18)	Position:{Microsoft.Kinect.SkeletonPoint} JointType:AnkleRight {18} TrackingState:Inferred {1}	Microsoft.Kinect.Joint
        //+(12)	Position:{Microsoft.Kinect.SkeletonPoint} JointType:HipLeft {12} TrackingState:Tracked {2}	Microsoft.Kinect.Joint
        //+(16)	Position:{Microsoft.Kinect.SkeletonPoint} JointType:HipRight {16} TrackingState:Tracked {2}	Microsoft.Kinect.Joint
        //+(19)	Position:{Microsoft.Kinect.SkeletonPoint} JointType:FootRight {19} TrackingState:Inferred {1}	Microsoft.Kinect.Joint



        posicaoPernas = PernasPosicao.twoLegUp;
                    /*
            posicaoPernas = LegOWASClassification(SkeletonPointAnkleRight, SkeletonPointKneeRight,
                                                  SkeletonPointAnkleLeft, SkeletonPointKneeLeft,
                                                  SkeletonPointHipLeft, SkeletonPointHipRight, FloorPlane);
                    */




        return posicaoPernas;
    }
}