
//package com.projecttango.examples.java.pointtopoint;
/*
import org.rajawali3d.math.vector.Vector3;

import java.util.ArrayList;
import java.util.List;

public class OWAS_classification
    {
        private Skeleton _Skeleton;
        private Tuple<float, float, float, float> FloorClipPlane ;
            
        public OWAS_classification(Skeleton skeleton)
        {
            _Skeleton = skeleton;
            FloorClipPlane = null;
        }
        public OWAS_classification(Skeleton skeleton, Tuple<float, float, float, float> _floor)
        {
            _Skeleton = skeleton;
            FloorClipPlane = _floor;
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
                posicao = this.GetLegsClassification(FloorClipPlane);
            }
            return posicao;
        }

      


        private CostaPosicao getCostaPosicaoBySkeleton()
        {

            Skeleton skeleton = this._Skeleton;
            int HipCenter =0 ;
            //Declaração de variáveis
            Vector3 SkeletonPointShoulderCenter = new Vector3();
            Vector3 SkeletonPointShoulderRight = new Vector3();
            Vector3 SkeletonPointShoulderLeft = new Vector3();

            Vector3 SkeletonPointHipCenter = new Vector3();
            Vector3 SkeletonPointHipLeft = new Vector3();
            Vector3 SkeletonPointHipRight = new Vector3();
            Vector3 SkeletonPointSpine = new Vector3();

            boolean isReto;
            boolean isTorcido;

            CostaPosicao posicaoCostas;

            //Pega a posição das costas pelo esqueleto
            if (skeleton == null)
                return CostaPosicao.Ereta;

            if (skeleton.Joints.get(HipCenter) != null)
            {
                SkeletonPointHipCenter.x = skeleton.Joints.get(HipCenter)[0];
                SkeletonPointHipCenter.Y = skeleton.Joints.get(HipCenter].Position.Y;
                SkeletonPointHipCenter.Z = skeleton.Joints.get(HipCenter].Position.Z;
            }

            if (skeleton.Joints.get(HipRight] != null)
            {
                SkeletonPointHipRight.X = skeleton.Joints.get(HipRight][0];
                SkeletonPointHipRight.Y = skeleton.Joints.get(HipRight].Position.Y;
                SkeletonPointHipRight.Z = skeleton.Joints.get(HipRight].Position.Z;
            }

            if (skeleton.Joints.get(HipLeft] != null)
            {
                SkeletonPointHipLeft.X = skeleton.Joints.get(HipLeft][0];
                SkeletonPointHipLeft.Y = skeleton.Joints.get(HipLeft].Position.Y;
                SkeletonPointHipLeft.Z = skeleton.Joints.get(HipLeft].Position.Z;
            }

            if (skeleton.Joints.get(Spine] != null)
            {
                SkeletonPointSpine.X = skeleton.Joints.get(Spine][0];
                SkeletonPointSpine.Y = skeleton.Joints.get(Spine].Position.Y;
                SkeletonPointSpine.Z = skeleton.Joints.get(Spine].Position.Z;
            }

            if (skeleton.Joints.get(ShoulderCenter] != null)
            {
                SkeletonPointShoulderCenter.X = skeleton.Joints.get(ShoulderCenter][0];
                SkeletonPointShoulderCenter.Y = skeleton.Joints.get(ShoulderCenter].Position.Y;
                SkeletonPointShoulderCenter.Z = skeleton.Joints.get(ShoulderCenter].Position.Z;
            }


            if (skeleton.Joints.get(ShoulderRight] != null)
            {
                SkeletonPointShoulderRight.X = skeleton.Joints.get(ShoulderRight][0];
                SkeletonPointShoulderRight.Y = skeleton.Joints.get(ShoulderRight].Position.Y;
                SkeletonPointShoulderRight.Z = skeleton.Joints.get(ShoulderRight].Position.Z;
            }


            if (skeleton.Joints.get(ShoulderLeft] != null)
            {
                SkeletonPointShoulderLeft.X = skeleton.Joints.get(ShoulderLeft][0];
                SkeletonPointShoulderLeft.Y = skeleton.Joints.get(ShoulderLeft].Position.Y;
                SkeletonPointShoulderLeft.Z = skeleton.Joints.get(ShoulderLeft].Position.Z;
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
        private boolean IsAbove(SkeletonPoint FirstPoint, SkeletonPoint SecondPoint)
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



        private boolean ArmsAboveShouder(SkeletonPoint CenterShouderPoint, SkeletonPoint HandPoint)
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
        private BracosPosicao ArmsOWASClassification(Vector3 SkeletonPointHandLeft, Vector3 SkeletonPointShouderLeft, Vector3 SkeletonPointElbowLeft, Vector3 SkeletonPointHandRight, Vector3 SkeletonPointShouderRight, Vector3 SkeletonPointElbowRight)
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
            Vector3 SkeletonPointHandLeft = new Vector3();
            Vector3 SkeletonPointHandRight = new Vector3();
            Vector3 SkeletonPointShouderLeft = new Vector3();
            Vector3 SkeletonPointShouderRight = new Vector3();
            Vector3 SkeletonPointElbowLeft = new Vector3();
            Vector3 SkeletonPointElbowRight = new Vector3();
            BracosPosicao posicaoBracos = BracosPosicao.Desconhecida;





            //Loop  into all skeleton joints 
            //      For i As Integer = 0 To skeleton.Joints.Count - 1
            //  Rotacao = skeleton.BoneOrientations(Spine).HierarchicalRotation
            //Rotacao.Matrix.M11.ToString()
            //Rotacao.Matrix.M21.ToString()
            //Rotacao.Matrix.M31.ToString()
            //Rotacao.Matrix.M41.ToString()
            //Matrix3D hipCenterMatrix = skeleton.GetRelativeJointMatrix("hipcenter");

            if (skeleton.Joints.get(ShoulderRight] != null)
            {
                SkeletonPointShouderRight.X = skeleton.Joints.get(ShoulderRight][0];
                SkeletonPointShouderRight.Y = skeleton.Joints.get(ShoulderRight].Position.Y;
                SkeletonPointShouderRight.Z = skeleton.Joints.get(ShoulderRight].Position.Z;
            }

            if (skeleton.Joints.get(ShoulderLeft] != null)
            {
                SkeletonPointShouderLeft.X = skeleton.Joints.get(ShoulderLeft][0];
                SkeletonPointShouderLeft.Y = skeleton.Joints.get(ShoulderLeft].Position.Y;
                SkeletonPointShouderLeft.Z = skeleton.Joints.get(ShoulderLeft].Position.Z;
            }

            if (skeleton.Joints.get(JointType.HandRight] != null)
            {
                SkeletonPointHandRight.X = skeleton.Joints.get(JointType.HandRight][0];
                SkeletonPointHandRight.Y = skeleton.Joints.get(JointType.HandRight].Position.Y;
                SkeletonPointHandRight.Z = skeleton.Joints.get(JointType.HandRight].Position.Z;
            }

            if (skeleton.Joints.get(JointType.HandLeft] != null)
            {
                SkeletonPointHandLeft.X = skeleton.Joints.get(JointType.HandLeft][0];
                SkeletonPointHandLeft.Y = skeleton.Joints.get(JointType.HandLeft].Position.Y;
                SkeletonPointHandLeft.Z = skeleton.Joints.get(JointType.HandLeft].Position.Z;
            }

            if (skeleton.Joints.get(JointType.ElbowRight] != null)
            {
                SkeletonPointElbowRight.X = skeleton.Joints.get(JointType.ElbowRight][0];
                SkeletonPointElbowRight.Y = skeleton.Joints.get(JointType.ElbowRight].Position.Y;
                SkeletonPointElbowRight.Z = skeleton.Joints.get(JointType.ElbowRight].Position.Z;
            }

            if (skeleton.Joints.get(JointType.ElbowLeft] != null)
            {
                SkeletonPointElbowLeft.X = skeleton.Joints.get(JointType.ElbowLeft][0];
                SkeletonPointElbowLeft.Y = skeleton.Joints.get(JointType.ElbowLeft].Position.Y;
                SkeletonPointElbowLeft.Z = skeleton.Joints.get(JointType.ElbowLeft].Position.Z;
            }


            posicaoBracos = ArmsOWASClassification(SkeletonPointHandLeft, SkeletonPointShouderLeft, SkeletonPointElbowLeft, SkeletonPointHandRight, SkeletonPointShouderRight, SkeletonPointElbowRight);



            return posicaoBracos;
        }





        //PERNAS
        //Este teste de função se o pé está no mesmo nível do Piso
        private boolean IsFloorPlane(Vector3 SkeletonPointAnkle, Tuple<float, float, float, float> FloorPlane, double MaxGroudLevelValue)
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

        public PernasPosicao LegOWASClassification(Vector3 SkeletonPointAnkleRight, Vector3 SkeletonPointKneeRight,
                                                    Vector3 SkeletonPointAnkleLeft, Vector3 SkeletonPointKneeLeft,
                                                 Vector3 SkeletonPointHipLeft, Vector3 SkeletonPointHipRight,
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
            Vector3 SkeletonPointAnkleLeft = new Vector3();
            Vector3 SkeletonPointAnkleRight = new Vector3();
            Vector3 SkeletonPointKneeLeft = new Vector3();
            Vector3 SkeletonPointKneeRight = new Vector3();

            Vector3 SkeletonPointHipLeft = new Vector3();
            Vector3 SkeletonPointHipRight = new Vector3();


            PernasPosicao posicaoPernas = PernasPosicao.Desconhecida;


            //+(13)	Position:{Microsoft.Kinect.SkeletonPoint} JointType:KneeLeft {13} TrackingState:Inferred {1}	Microsoft.Kinect.Joint
            //+(14)	Position:{Microsoft.Kinect.SkeletonPoint} JointType:AnkleLeft {14} TrackingState:Inferred {1}	Microsoft.Kinect.Joint
            //+(15)	Position:{Microsoft.Kinect.SkeletonPoint} JointType:FootLeft {15} TrackingState:Inferred {1}	Microsoft.Kinect.Joint
            //+(17)	Position:{Microsoft.Kinect.SkeletonPoint} JointType:KneeRight {17} TrackingState:Inferred {1}	Microsoft.Kinect.Joint
            //+(18)	Position:{Microsoft.Kinect.SkeletonPoint} JointType:AnkleRight {18} TrackingState:Inferred {1}	Microsoft.Kinect.Joint
            //+(12)	Position:{Microsoft.Kinect.SkeletonPoint} JointType:HipLeft {12} TrackingState:Tracked {2}	Microsoft.Kinect.Joint
            //+(16)	Position:{Microsoft.Kinect.SkeletonPoint} JointType:HipRight {16} TrackingState:Tracked {2}	Microsoft.Kinect.Joint
            //+(19)	Position:{Microsoft.Kinect.SkeletonPoint} JointType:FootRight {19} TrackingState:Inferred {1}	Microsoft.Kinect.Joint

            if (skeleton.Joints.get(JointType.KneeRight] != null)
            {
                SkeletonPointKneeRight.X = skeleton.Joints.get(JointType.KneeRight][0];
                SkeletonPointKneeRight.Y = skeleton.Joints.get(JointType.KneeRight].Position.Y;
                SkeletonPointKneeRight.Z = skeleton.Joints.get(JointType.KneeRight].Position.Z;
            }

            if (skeleton.Joints.get(JointType.KneeLeft] != null)
            {
                SkeletonPointKneeLeft.X = skeleton.Joints.get(JointType.KneeLeft][0];
                SkeletonPointKneeLeft.Y = skeleton.Joints.get(JointType.KneeLeft].Position.Y;
                SkeletonPointKneeLeft.Z = skeleton.Joints.get(JointType.KneeLeft].Position.Z;
            }

            if (skeleton.Joints.get(JointType.FootRight] != null)
            {
                SkeletonPointAnkleRight.X = skeleton.Joints.get(JointType.FootRight][0];
                SkeletonPointAnkleRight.Y = skeleton.Joints.get(JointType.FootRight].Position.Y;
                SkeletonPointAnkleRight.Z = skeleton.Joints.get(JointType.FootRight].Position.Z;
            }


            if (skeleton.Joints.get(JointType.FootLeft] != null)
            {
                SkeletonPointAnkleLeft.X = skeleton.Joints.get(JointType.FootLeft][0];
                SkeletonPointAnkleLeft.Y = skeleton.Joints.get(JointType.FootLeft].Position.Y;
                SkeletonPointAnkleLeft.Z = skeleton.Joints.get(JointType.FootLeft].Position.Z;
            }

            if (skeleton.Joints.get(HipRight] != null)
            {
                SkeletonPointHipRight.X = skeleton.Joints.get(HipRight][0];
                SkeletonPointHipRight.Y = skeleton.Joints.get(HipRight].Position.Y;
                SkeletonPointHipRight.Z = skeleton.Joints.get(HipRight].Position.Z;
            }

            if (skeleton.Joints.get(HipLeft] != null)
            {
                SkeletonPointHipLeft.X = skeleton.Joints.get(HipLeft][0];
                SkeletonPointHipLeft.Y = skeleton.Joints.get(HipLeft].Position.Y;
                SkeletonPointHipLeft.Z = skeleton.Joints.get(HipLeft].Position.Z;
            }


            posicaoPernas = LegOWASClassification(SkeletonPointAnkleRight, SkeletonPointKneeRight,
                                                  SkeletonPointAnkleLeft, SkeletonPointKneeLeft,
                                                  SkeletonPointHipLeft, SkeletonPointHipRight, FloorPlane);





            return posicaoPernas;
        }




    }
*/