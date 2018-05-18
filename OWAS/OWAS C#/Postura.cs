using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Microsoft.Kinect;

namespace OWAS
{
    public enum CostaPosicao
    {
        Desconhecida = 0,
        Ereta = 1,
        Inclinada = 2,
        EretaETorcida = 3,
        InclinadaETorcida = 4
    };

    public enum BracosPosicao
    {
        Desconhecida = 0,
        BothArmsareDown = 1,
        OneHandUp = 2,
        TwoHandUp = 3

    };
    // 1)	Duas pernas retas: As duas pernas esta no chão (classificação 2 = True) e as duas penas estão retas (classificação 1 = True para as duas pernas) . 
    //2)	Uma perna reta :  As duas pernas não estão no chão (classificação 2 = False) e uma das pernas  está reta ( classificação 1 = False)
    //3)	Duas pernas flexionadas: As duas pernas esta no chão ( classificação 2 = True) e as duas penas não estão retas (classificação 1 =False  para as duas pernas) .
    //4)	Uma Perna Flexionada: As duas pernas não estão no chão (classificação 2 = False) e a perna que está no chão não está reta ( classificação 1 = False)
    //5)	Uma perna Ajoelhada: 
    public enum PernaPosicao
    {
        twoLegUp = 1,
        OneLegUp = 2,
        twoLegFlex = 3,
        OneLegFlex = 4,

    };

    public class OWASMethod
    {
        private Skeleton _Skeleton { get; set; }

        public OWASMethod(Skeleton skeleton)
        {
            _Skeleton = skeleton;
        }

        //costas

        //Pega a posição das costas
        public CostaPosicao getBackPosition()
        {
            CostaPosicao posicao = CostaPosicao.Desconhecida;
            if (this._Skeleton != null)
            {
                posicao = this.getCostaPosicaoBySkeleton();
            }
            return posicao;
        }

        private CostaPosicao getCostaPosicaoBySkeleton()
        {

            Skeleton skeleton = this._Skeleton;

            //Declaração de variáveis
            SkeletonPoint SkeletonPointShoulderCenter = new SkeletonPoint();
            SkeletonPoint SkeletonPointShoulderRight = new SkeletonPoint();
            SkeletonPoint SkeletonPointShoulderLeft = new SkeletonPoint();

            SkeletonPoint SkeletonPointHipCenter = new SkeletonPoint();
            SkeletonPoint SkeletonPointHipLeft = new SkeletonPoint();
            SkeletonPoint SkeletonPointHipRight = new SkeletonPoint();
            SkeletonPoint SkeletonPointSpine = new SkeletonPoint();

            bool isReto;
            bool isTorcido;

            CostaPosicao posicaoCostas;

            //Pega a posição das costas pelo esqueleto
            if (skeleton == null)
                return CostaPosicao.Ereta;

            if (skeleton.Joints[JointType.HipCenter].TrackingState == JointTrackingState.Tracked)
            {
                SkeletonPointHipCenter.X = skeleton.Joints[JointType.HipCenter].Position.X;
                SkeletonPointHipCenter.Y = skeleton.Joints[JointType.HipCenter].Position.Y;
                SkeletonPointHipCenter.Z = skeleton.Joints[JointType.HipCenter].Position.Z;
            }

            if (skeleton.Joints[JointType.HipRight].TrackingState == JointTrackingState.Tracked)
            {
                SkeletonPointHipRight.X = skeleton.Joints[JointType.HipRight].Position.X;
                SkeletonPointHipRight.Y = skeleton.Joints[JointType.HipRight].Position.Y;
                SkeletonPointHipRight.Z = skeleton.Joints[JointType.HipRight].Position.Z;
            }

            if (skeleton.Joints[JointType.HipLeft].TrackingState == JointTrackingState.Tracked)
            {
                SkeletonPointHipLeft.X = skeleton.Joints[JointType.HipLeft].Position.X;
                SkeletonPointHipLeft.Y = skeleton.Joints[JointType.HipLeft].Position.Y;
                SkeletonPointHipLeft.Z = skeleton.Joints[JointType.HipLeft].Position.Z;
            }

            if (skeleton.Joints[JointType.Spine].TrackingState == JointTrackingState.Tracked)
            {
                SkeletonPointSpine.X = skeleton.Joints[JointType.Spine].Position.X;
                SkeletonPointSpine.Y = skeleton.Joints[JointType.Spine].Position.Y;
                SkeletonPointSpine.Z = skeleton.Joints[JointType.Spine].Position.Z;
            }

            if (skeleton.Joints[JointType.ShoulderCenter].TrackingState == JointTrackingState.Tracked)
            {
                SkeletonPointShoulderCenter.X = skeleton.Joints[JointType.ShoulderCenter].Position.X;
                SkeletonPointShoulderCenter.Y = skeleton.Joints[JointType.ShoulderCenter].Position.Y;
                SkeletonPointShoulderCenter.Z = skeleton.Joints[JointType.ShoulderCenter].Position.Z;
            }


            if (skeleton.Joints[JointType.ShoulderRight].TrackingState == JointTrackingState.Tracked)
            {
                SkeletonPointShoulderRight.X = skeleton.Joints[JointType.ShoulderRight].Position.X;
                SkeletonPointShoulderRight.Y = skeleton.Joints[JointType.ShoulderRight].Position.Y;
                SkeletonPointShoulderRight.Z = skeleton.Joints[JointType.ShoulderRight].Position.Z;
            }


            if (skeleton.Joints[JointType.ShoulderLeft].TrackingState == JointTrackingState.Tracked)
            {
                SkeletonPointShoulderLeft.X = skeleton.Joints[JointType.ShoulderLeft].Position.X;
                SkeletonPointShoulderLeft.Y = skeleton.Joints[JointType.ShoulderLeft].Position.Y;
                SkeletonPointShoulderLeft.Z = skeleton.Joints[JointType.ShoulderLeft].Position.Z;
            }


            //Calcula posicação das costas

            //Verifica se está torcido
            if (OWAS.Math.Point3Y0GetAngleBetween(ref SkeletonPointShoulderRight, ref SkeletonPointShoulderLeft, ref SkeletonPointHipRight, ref SkeletonPointHipLeft) >= 5)
            {
                isTorcido = true;
            }
            else
            {
                isTorcido = false;
            }

            //Verifica se está reta
            if (OWAS.Math.Point3Z0GetAngleBetween(ref SkeletonPointHipCenter, ref SkeletonPointShoulderCenter) >= 80)
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
        private CostaPosicao OWASClassificationSpine(bool IsReto, bool IsTorcido)
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
                Console.Write("Ereta");
            }

            if (IsReto == false & IsTorcido == false)
            {
                posicao = CostaPosicao.Inclinada;
                Console.Write("Inclinada");
            }

            if (IsReto == true & IsTorcido == true)
            {
                posicao = CostaPosicao.EretaETorcida;
                Console.Write("Torcida");
            }
            if (IsReto == false & IsTorcido == true)
            {
                posicao = CostaPosicao.InclinadaETorcida;
                Console.Write("Inclinada e torcida");
            }

            return posicao;
        }



        //bracos


        // Essa função compara a coordenada Y de duas joint diferente.
        // É usado para verificar se um conjunto está acima ou abaixo outra articulação

        //se esta para cima
        private bool IsAbove(SkeletonPoint FirstPoint, SkeletonPoint SecondPoint)
        {

            if (FirstPoint.Y > SecondPoint.Y)
            {
                return true;
            }
            else
            {
                return false;
            }

        }

        //classificacao dos bracos
        public BracosPosicao ArmsOWASClassification(SkeletonPoint SkeletonPointHandLeft, SkeletonPoint SkeletonPointShouderLeft, SkeletonPoint SkeletonPointElbowLeft, SkeletonPoint SkeletonPointHandRight, SkeletonPoint SkeletonPointShouderRight, SkeletonPoint SkeletonPointElbowRight)
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


        //pernas
        public int LegOWASClassification(SkeletonPoint SkeletonPointAnkleRight, SkeletonPoint SkeletonPointKneeRight, SkeletonPoint SkeletonPointAnkleLeft, SkeletonPoint SkeletonPointKneeLeft, SkeletonPoint SkeletonPointHipLeft, SkeletonPoint SkeletonPointHipRight, Tuple<float, float, float, float> FloorPlane)
        {
            int functionReturnValue = 0;
            //---------------------------------------------------------------------------------------------------
            //Classification of Leg
            //1)	Duas pernas retas: As duas pernas esta no chão (classificação 2 = True) e as duas penas estão retas (classificação 1 = True para as duas pernas) . 
            //2)	Uma perna reta :  As duas pernas não estão no chão (classificação 2 = False) e uma das pernas  está reta ( classificação 1 = False)
            //3)	Duas pernas flexionadas: As duas pernas esta no chão ( classificação 2 = True) e as duas penas não estão retas (classificação 1 =False  para as duas pernas) .
            //4)	Uma Perna Flexionada: As duas pernas não estão no chão (classificação 2 = False) e a perna que está no chão não está reta ( classificação 1 = False)
            //5)	Uma perna Ajoelhada: As duas pernas esta no chão ( classificação 2 = True) e as duas pernas estão flexionada (classificação 1 =False  para as duas pernas ) e pelo menos uma perna está ajoelhada (classificação 1 = False) 
            //---------------------------------------------------------------------------------------------------
            //Inicialize the value
            functionReturnValue = 0;
            Point3D PointHipLeft = new Point3D();
            Point3D PointKneeLeft = new Point3D();
            Point3D PointHipRight = new Point3D();
            Point3D PointKneeRight = new Point3D();

            //to do: Mudar parametros da função
            //IsStraightByDotProduct(SkeletonPointKneeRight, SkeletonPointKneeRight, SkeletonPointHipRight, 2.8)

            //Perna Reta: Point3Z0GetAngleBetween(XMLSkeletonPointKneeRight, XMLSkeletonPointKneeRight) > 80 
            //Pé no Chão: IsFloorPlane(SkeletonPointKneeRight, FloorPlane, 0.1)


            PointHipLeft.X = SkeletonPointHipLeft.X;
            PointHipLeft.Y = SkeletonPointHipLeft.Y;
            PointHipLeft.Z = SkeletonPointHipLeft.Z;


            PointKneeLeft.X = SkeletonPointKneeLeft.X;
            PointKneeLeft.Y = SkeletonPointKneeLeft.Y;
            PointKneeLeft.Z = SkeletonPointKneeLeft.Z;



            PointHipRight.X = SkeletonPointHipRight.X;
            PointHipRight.Y = SkeletonPointHipRight.Y;
            PointHipRight.Z = SkeletonPointHipRight.Z;


            PointKneeRight.X = SkeletonPointKneeRight.X;
            PointKneeRight.Y = SkeletonPointKneeRight.Y;
            PointKneeRight.Z = SkeletonPointKneeRight.Z;


            if ((Point3Z0GetAngleBetween(SkeletonPointKneeRight, SkeletonPointAnkleRight) > 80 & IsFloorPlane(SkeletonPointAnkleRight, FloorPlane, 0.1)) & (Point3Z0GetAngleBetween(SkeletonPointKneeLeft, SkeletonPointAnkleLeft) > 80 & IsFloorPlane(SkeletonPointAnkleLeft, FloorPlane, 0.1)))
            {
                if ((Math.Round(PointHipLeft.DistanceTo(PointKneeLeft), 2) <= 0.35 | Math.Round(PointHipRight.DistanceTo(PointKneeRight), 2) <= 0.35))
                {
                    return 5;
                }
                else
                {
                    return 1;
                }
            }

            if ((Point3Z0GetAngleBetween(SkeletonPointKneeRight, SkeletonPointAnkleRight) > 80 & IsFloorPlane(SkeletonPointAnkleRight, FloorPlane, 0.1)) | (Point3Z0GetAngleBetween(SkeletonPointKneeLeft, SkeletonPointAnkleLeft) > 80 & IsFloorPlane(SkeletonPointAnkleLeft, FloorPlane, 0.1)))
            {
                return 2;
            }

            if ((Point3Z0GetAngleBetween(SkeletonPointKneeRight, SkeletonPointAnkleRight) <= 80 & IsFloorPlane(SkeletonPointAnkleRight, FloorPlane, 0.1)) & (Point3Z0GetAngleBetween(SkeletonPointKneeLeft, SkeletonPointAnkleLeft) <= 80 & IsFloorPlane(SkeletonPointAnkleLeft, FloorPlane, 0.1)))
            {
                if ((Math.Round(PointHipLeft.DistanceTo(PointKneeLeft), 2) <= 0.3 | Math.Round(PointHipRight.DistanceTo(PointKneeRight), 2) <= 0.3))
                {
                    return 5;

                }
                else
                {
                    return 3;
                }
            }



            if ((Point3Z0GetAngleBetween(SkeletonPointKneeRight, SkeletonPointAnkleRight) <= 80 & Point3Z0GetAngleBetween(SkeletonPointKneeLeft, SkeletonPointAnkleLeft) <= 80) & (IsFloorPlane(SkeletonPointAnkleRight, FloorPlane, 0.1) | IsFloorPlane(SkeletonPointAnkleLeft, FloorPlane, 0.1)))
            {
                if ((Math.Round(PointHipLeft.DistanceTo(PointKneeLeft), 2) <= 0.3 | Math.Round(PointHipRight.DistanceTo(PointKneeRight), 2) <= 0.3))
                {
                    return 5;

                }
                else
                {
                    return 4;
                }
            }
            return 0;
            return functionReturnValue;
        }


    }
}
