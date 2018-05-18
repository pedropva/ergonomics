using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Microsoft.Kinect;
using System.Diagnostics;

namespace OWAS
{
    public enum CostaPosicao :int
    {
        Desconhecida = 0,
        Ereta = 1,
        Inclinada = 2,
        EretaETorcida = 3,
        InclinadaETorcida = 4
    };

    public enum BracosPosicao :int
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
    public enum PernasPosicao
    {
        Desconhecida = 0,
        twoLegUp = 1,
        OneLegUp = 2,
        twoLegFlex = 3,
        OneLegFlex = 4,
        OneLegknee = 5

    };

    public class OWASMethod
    {
        private Skeleton _Skeleton { get; set; }
        private Tuple<float, float, float, float> FloorClipPlane { get; set; }
            
        public OWASMethod(Skeleton skeleton)
        {
            _Skeleton = skeleton;
            FloorClipPlane = null;
        }
        public OWASMethod(Skeleton skeleton, Tuple<float, float, float, float> _floor)
        {
            _Skeleton = skeleton;
            FloorClipPlane = _floor;
        }


        

        //COSTAS

        //Pega a posição das costas
        public static List<String> getNameCostaPositions() {
            List<String> posicaoCostas= new List<String>();
            
            posicaoCostas.Add("Desconhecido");
            posicaoCostas.Add("Ereta");
            posicaoCostas.Add("Inclinada");
            posicaoCostas.Add("Ereta e Torcida");
            posicaoCostas.Add("Inclinada e Torcida");
            
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
            List<String> posicao = new List<String>();

            posicao.Add("Desconhecido");
            posicao.Add("Braços abaixados");
            posicao.Add("Uma mão levantada");
            posicao.Add("Duas mão levantadas");

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
            List<String> posicao = new List<String>();

            posicao.Add("Desconhecida");
            posicao.Add("Pernas estão retas");
            posicao.Add("Uma perna reta");
            posicao.Add("Pernas flexionadas");
            posicao.Add("Uma perna flexionada");
            posicao.Add("Uma perna ajoelhada");

            return posicao;
        }


        public PernasPosicao getPernasPosition()
        {
            PernasPosicao posicao = PernasPosicao.Desconhecida;
            if (this._Skeleton != null)
            {
                posicao = this.GetLegsClassification(FloorClipPlane);
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



        private bool ArmsAboveShouder(SkeletonPoint CenterShouderPoint, SkeletonPoint HandPoint)
        {

            if (HandPoint.Y > CenterShouderPoint.Y)
            {
                return true;
            }
            else
            {
                return false;
            }


        }

        //classificacao dos bracos
        private BracosPosicao ArmsOWASClassification(SkeletonPoint SkeletonPointHandLeft, SkeletonPoint SkeletonPointShouderLeft, SkeletonPoint SkeletonPointElbowLeft, SkeletonPoint SkeletonPointHandRight, SkeletonPoint SkeletonPointShouderRight, SkeletonPoint SkeletonPointElbowRight)
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
            SkeletonPoint SkeletonPointHandLeft = new SkeletonPoint();
            SkeletonPoint SkeletonPointHandRight = new SkeletonPoint();
            SkeletonPoint SkeletonPointShouderLeft = new SkeletonPoint();
            SkeletonPoint SkeletonPointShouderRight = new SkeletonPoint();
            SkeletonPoint SkeletonPointElbowLeft = new SkeletonPoint();
            SkeletonPoint SkeletonPointElbowRight = new SkeletonPoint();
            BracosPosicao posicaoBracos = BracosPosicao.Desconhecida;





            //Loop  into all skeleton joints 
            //      For i As Integer = 0 To skeleton.Joints.Count - 1
            //  Rotacao = skeleton.BoneOrientations(JointType.Spine).HierarchicalRotation
            //Rotacao.Matrix.M11.ToString()
            //Rotacao.Matrix.M21.ToString()
            //Rotacao.Matrix.M31.ToString()
            //Rotacao.Matrix.M41.ToString()
            //Matrix3D hipCenterMatrix = skeleton.GetRelativeJointMatrix("hipcenter");

            if (skeleton.Joints[JointType.ShoulderRight].TrackingState == JointTrackingState.Tracked)
            {
                SkeletonPointShouderRight.X = skeleton.Joints[JointType.ShoulderRight].Position.X;
                SkeletonPointShouderRight.Y = skeleton.Joints[JointType.ShoulderRight].Position.Y;
                SkeletonPointShouderRight.Z = skeleton.Joints[JointType.ShoulderRight].Position.Z;
            }

            if (skeleton.Joints[JointType.ShoulderLeft].TrackingState == JointTrackingState.Tracked)
            {
                SkeletonPointShouderLeft.X = skeleton.Joints[JointType.ShoulderLeft].Position.X;
                SkeletonPointShouderLeft.Y = skeleton.Joints[JointType.ShoulderLeft].Position.Y;
                SkeletonPointShouderLeft.Z = skeleton.Joints[JointType.ShoulderLeft].Position.Z;
            }

            if (skeleton.Joints[JointType.HandRight].TrackingState == JointTrackingState.Tracked)
            {
                SkeletonPointHandRight.X = skeleton.Joints[JointType.HandRight].Position.X;
                SkeletonPointHandRight.Y = skeleton.Joints[JointType.HandRight].Position.Y;
                SkeletonPointHandRight.Z = skeleton.Joints[JointType.HandRight].Position.Z;
            }

            if (skeleton.Joints[JointType.HandLeft].TrackingState == JointTrackingState.Tracked)
            {
                SkeletonPointHandLeft.X = skeleton.Joints[JointType.HandLeft].Position.X;
                SkeletonPointHandLeft.Y = skeleton.Joints[JointType.HandLeft].Position.Y;
                SkeletonPointHandLeft.Z = skeleton.Joints[JointType.HandLeft].Position.Z;
            }

            if (skeleton.Joints[JointType.ElbowRight].TrackingState == JointTrackingState.Tracked)
            {
                SkeletonPointElbowRight.X = skeleton.Joints[JointType.ElbowRight].Position.X;
                SkeletonPointElbowRight.Y = skeleton.Joints[JointType.ElbowRight].Position.Y;
                SkeletonPointElbowRight.Z = skeleton.Joints[JointType.ElbowRight].Position.Z;
            }

            if (skeleton.Joints[JointType.ElbowLeft].TrackingState == JointTrackingState.Tracked)
            {
                SkeletonPointElbowLeft.X = skeleton.Joints[JointType.ElbowLeft].Position.X;
                SkeletonPointElbowLeft.Y = skeleton.Joints[JointType.ElbowLeft].Position.Y;
                SkeletonPointElbowLeft.Z = skeleton.Joints[JointType.ElbowLeft].Position.Z;
            }


            posicaoBracos = ArmsOWASClassification(SkeletonPointHandLeft, SkeletonPointShouderLeft, SkeletonPointElbowLeft, SkeletonPointHandRight, SkeletonPointShouderRight, SkeletonPointElbowRight);



            return posicaoBracos;
        }





        //PERNAS
        //Este teste de função se o pé está no mesmo nível do Piso
        private bool IsFloorPlane(SkeletonPoint SkeletonPointAnkle, Tuple<float, float, float, float> FloorPlane, double MaxGroudLevelValue)
        {
            //This function test if the Foot is in the same level as Floor
            //Plane Equation    Ax +By + Cz + D = 0
            if ((FloorPlane.Item1 * SkeletonPointAnkle.X) + (FloorPlane.Item2 * SkeletonPointAnkle.Y) + (FloorPlane.Item3 * SkeletonPointAnkle.Z) + FloorPlane.Item4 > MaxGroudLevelValue)
            {
                return false;
            }
            else
            {
                return true;
            }

        }

        public PernasPosicao LegOWASClassification(SkeletonPoint SkeletonPointAnkleRight, SkeletonPoint SkeletonPointKneeRight,
                                                    SkeletonPoint SkeletonPointAnkleLeft, SkeletonPoint SkeletonPointKneeLeft,
                                                 SkeletonPoint SkeletonPointHipLeft, SkeletonPoint SkeletonPointHipRight,
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


            if ((OWAS.Math.Point3Z0GetAngleBetween(ref SkeletonPointKneeRight, ref SkeletonPointAnkleRight) > 80 & IsFloorPlane(SkeletonPointAnkleRight, FloorPlane, 0.1)) & (OWAS.Math.Point3Z0GetAngleBetween(ref SkeletonPointKneeLeft, ref SkeletonPointAnkleLeft) > 80 & IsFloorPlane(SkeletonPointAnkleLeft, FloorPlane, 0.1)))
            {
                if ((Math.Round(PointHipLeft.DistanceTo(PointKneeLeft), 2) <= 0.35 | Math.Round(PointHipRight.DistanceTo(PointKneeRight), 2) <= 0.35))
                {

                    posicaoPernas = PernasPosicao.OneLegknee;
                }
                else
                {
                    posicaoPernas = PernasPosicao.twoLegUp;
                }
            }

            if ((OWAS.Math.Point3Z0GetAngleBetween(ref SkeletonPointKneeRight, ref SkeletonPointAnkleRight) > 80 & IsFloorPlane(SkeletonPointAnkleRight, FloorPlane, 0.1)) | (OWAS.Math.Point3Z0GetAngleBetween(ref SkeletonPointKneeLeft, ref SkeletonPointAnkleLeft) > 80 & IsFloorPlane(SkeletonPointAnkleLeft, FloorPlane, 0.1)))
            {

                posicaoPernas = PernasPosicao.OneLegUp;
            }

            if ((OWAS.Math.Point3Z0GetAngleBetween(ref SkeletonPointKneeRight, ref SkeletonPointAnkleRight) <= 80 & IsFloorPlane(SkeletonPointAnkleRight, FloorPlane, 0.1)) & (OWAS.Math.Point3Z0GetAngleBetween(ref SkeletonPointKneeLeft, ref SkeletonPointAnkleLeft) <= 80 & IsFloorPlane(SkeletonPointAnkleLeft, FloorPlane, 0.1)))
            {
                if ((Math.Round(PointHipLeft.DistanceTo(PointKneeLeft), 2) <= 0.3 | Math.Round(PointHipRight.DistanceTo(PointKneeRight), 2) <= 0.3))
                {
                    posicaoPernas = PernasPosicao.OneLegknee;

                }
                else
                {
                    posicaoPernas = PernasPosicao.twoLegFlex;
                }
            }



            if ((OWAS.Math.Point3Z0GetAngleBetween(ref SkeletonPointKneeRight, ref SkeletonPointAnkleRight) <= 80 & OWAS.Math.Point3Z0GetAngleBetween(ref SkeletonPointKneeLeft, ref SkeletonPointAnkleLeft) <= 80) & (IsFloorPlane(SkeletonPointAnkleRight, FloorPlane, 0.1) | IsFloorPlane(SkeletonPointAnkleLeft, FloorPlane, 0.1)))
            {
                if ((Math.Round(PointHipLeft.DistanceTo(PointKneeLeft), 2) <= 0.3 | Math.Round(PointHipRight.DistanceTo(PointKneeRight), 2) <= 0.3))
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


        public PernasPosicao GetLegsClassification(Tuple<float, float, float, float> FloorPlane)
        {
            Skeleton skeleton = this._Skeleton;
            //Legs Position 
            SkeletonPoint SkeletonPointAnkleLeft = new SkeletonPoint();
            SkeletonPoint SkeletonPointAnkleRight = new SkeletonPoint();
            SkeletonPoint SkeletonPointKneeLeft = new SkeletonPoint();
            SkeletonPoint SkeletonPointKneeRight = new SkeletonPoint();

            SkeletonPoint SkeletonPointHipLeft = new SkeletonPoint();
            SkeletonPoint SkeletonPointHipRight = new SkeletonPoint();


            PernasPosicao posicaoPernas = PernasPosicao.Desconhecida;


            //+(13)	Position:{Microsoft.Kinect.SkeletonPoint} JointType:KneeLeft {13} TrackingState:Inferred {1}	Microsoft.Kinect.Joint
            //+(14)	Position:{Microsoft.Kinect.SkeletonPoint} JointType:AnkleLeft {14} TrackingState:Inferred {1}	Microsoft.Kinect.Joint
            //+(15)	Position:{Microsoft.Kinect.SkeletonPoint} JointType:FootLeft {15} TrackingState:Inferred {1}	Microsoft.Kinect.Joint
            //+(17)	Position:{Microsoft.Kinect.SkeletonPoint} JointType:KneeRight {17} TrackingState:Inferred {1}	Microsoft.Kinect.Joint
            //+(18)	Position:{Microsoft.Kinect.SkeletonPoint} JointType:AnkleRight {18} TrackingState:Inferred {1}	Microsoft.Kinect.Joint
            //+(12)	Position:{Microsoft.Kinect.SkeletonPoint} JointType:HipLeft {12} TrackingState:Tracked {2}	Microsoft.Kinect.Joint
            //+(16)	Position:{Microsoft.Kinect.SkeletonPoint} JointType:HipRight {16} TrackingState:Tracked {2}	Microsoft.Kinect.Joint
            //+(19)	Position:{Microsoft.Kinect.SkeletonPoint} JointType:FootRight {19} TrackingState:Inferred {1}	Microsoft.Kinect.Joint

            if (skeleton.Joints[JointType.KneeRight].TrackingState == JointTrackingState.Tracked)
            {
                SkeletonPointKneeRight.X = skeleton.Joints[JointType.KneeRight].Position.X;
                SkeletonPointKneeRight.Y = skeleton.Joints[JointType.KneeRight].Position.Y;
                SkeletonPointKneeRight.Z = skeleton.Joints[JointType.KneeRight].Position.Z;
            }

            if (skeleton.Joints[JointType.KneeLeft].TrackingState == JointTrackingState.Tracked)
            {
                SkeletonPointKneeLeft.X = skeleton.Joints[JointType.KneeLeft].Position.X;
                SkeletonPointKneeLeft.Y = skeleton.Joints[JointType.KneeLeft].Position.Y;
                SkeletonPointKneeLeft.Z = skeleton.Joints[JointType.KneeLeft].Position.Z;
            }

            if (skeleton.Joints[JointType.FootRight].TrackingState == JointTrackingState.Tracked)
            {
                SkeletonPointAnkleRight.X = skeleton.Joints[JointType.FootRight].Position.X;
                SkeletonPointAnkleRight.Y = skeleton.Joints[JointType.FootRight].Position.Y;
                SkeletonPointAnkleRight.Z = skeleton.Joints[JointType.FootRight].Position.Z;
            }


            if (skeleton.Joints[JointType.FootLeft].TrackingState == JointTrackingState.Tracked)
            {
                SkeletonPointAnkleLeft.X = skeleton.Joints[JointType.FootLeft].Position.X;
                SkeletonPointAnkleLeft.Y = skeleton.Joints[JointType.FootLeft].Position.Y;
                SkeletonPointAnkleLeft.Z = skeleton.Joints[JointType.FootLeft].Position.Z;
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


            posicaoPernas = LegOWASClassification(SkeletonPointAnkleRight, SkeletonPointKneeRight,
                                                  SkeletonPointAnkleLeft, SkeletonPointKneeLeft,
                                                  SkeletonPointHipLeft, SkeletonPointHipRight, FloorPlane);





            return posicaoPernas;
        }




    }
}
