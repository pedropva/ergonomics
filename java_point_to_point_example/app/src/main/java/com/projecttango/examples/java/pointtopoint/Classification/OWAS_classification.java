
package com.projecttango.examples.java.pointtopoint.Classification;

import android.util.Log;

import org.rajawali3d.math.vector.Vector3;

import java.util.ArrayList;
import java.util.List;

public class OWAS_classification {
    private Skeleton _Skeleton;
    //private Tuple<float, float, float, float> FloorClipPlane;

    public OWAS_classification(Skeleton skeleton)// Tuple<float, float, float, float> _floor
    {
        _Skeleton = skeleton;
        //FloorClipPlane = _floor;
    }

    public String getNameBracosPositions(BracosPosicao arms) {

        if(arms == BracosPosicao.BothArmsareDown){
            return "Ambos abaixados.";
        }else if(arms == BracosPosicao.OneHandUp){
            return "Um braço levantado.";
        }else if(arms == BracosPosicao.TwoHandUp){
            return "Dois braços levantados.";
        }
        return "Posição desconhecida.";
    }

    public String getNamePernasPositions(PernasPosicao legs) {
        if(legs == PernasPosicao.twoLegUp){
            return "Duas pernas retas.";
        }else if(legs == PernasPosicao.OneLegUp){
            return "Uma perna reta.";
        }else if(legs == PernasPosicao.twoLegFlex){
            return "Duas pernas flexionadas.";
        }else if(legs == PernasPosicao.OneLegFlex){
            return "Uma Perna Flexionada.";
        }else if(legs == PernasPosicao.OneLegknee){
            return "Ajoelhado.";
        }
        return "Posição desconhecida.";
    }
    public static String getNameCostaPositions(CostaPosicao back) {
        if(back == CostaPosicao.Ereta){
            return "Ereta.";
        }else if(back == CostaPosicao.EretaETorcida){
            return "Ereta e Torcida.";
        }else if(back == CostaPosicao.Inclinada){
            return "Inclinada.";
        }else if(back == CostaPosicao.InclinadaETorcida){
            return "Inclinada e Torcida.";
        }
        return "Posição desconhecida.";
    }



    public BracosPosicao getBracoPosition() {
        BracosPosicao posicao = BracosPosicao.Desconhecida;
        if (this._Skeleton != null) {
            posicao = this.GetArmsClassification();
        }
        return posicao;
    }

    public CostaPosicao getBackPosition() {
        CostaPosicao posicao = CostaPosicao.Desconhecida;
        if (this._Skeleton != null) {
            posicao = this.getCostaPosicaoBySkeleton();
        }
        return posicao;
    }


    public PernasPosicao getPernasPosition() {
        PernasPosicao posicao = PernasPosicao.Desconhecida;
        if (this._Skeleton != null) {
            posicao = this.GetLegsClassification();//FloorClipPlane
        }
        return posicao;
    }

    public PernasPosicao GetLegsClassification()
    {
        Skeleton skeleton = this._Skeleton;
        PernasPosicao posicaoPernas = PernasPosicao.Desconhecida;

        if(skeleton.LAnkle.isKnow() && skeleton.RAnkle.isKnow() && //if all joints we use are known
                skeleton.LKnee.isKnow() && skeleton.RKnee.isKnow() &&
                skeleton.LHip.isKnow() && skeleton.RHip.isKnow())
        {
            posicaoPernas = LegOWASClassification(skeleton.RAnkle,skeleton.RKnee,
                    skeleton.LAnkle, skeleton.LKnee,
                    skeleton.LHip, skeleton.RHip);
        }
        return posicaoPernas;
    }
    private BracosPosicao GetArmsClassification() {
        Skeleton skeleton = this._Skeleton;
        BracosPosicao posicaoBracos = BracosPosicao.Desconhecida;
        //Arms Position
        if (skeleton.LElbow.isKnow() && skeleton.RElbow.isKnow() &&     //if all joints we use are known
                skeleton.LShoulder.isKnow() && skeleton.RShoulder.isKnow() &&
                skeleton.LWrist.isKnow() && skeleton.RWrist.isKnow())
        {
            posicaoBracos = ArmsOWASClassification(skeleton.LWrist, skeleton.LShoulder, skeleton.LElbow,
                    skeleton.RWrist, skeleton.RShoulder, skeleton.RElbow);
        }
        return posicaoBracos;
    }

    private CostaPosicao getCostaPosicaoBySkeleton() {

        Skeleton skeleton = this._Skeleton;
        CostaPosicao posicaoCostas = CostaPosicao.Desconhecida;

        if(skeleton.Neck.isKnow() && skeleton.RShoulder.isKnow() &&     //checking if all points used are known
           skeleton.LShoulder.isKnow() && skeleton.LHip.isKnow() &&
           skeleton.RHip.isKnow())
        {
            double midX = (skeleton.LHip.x + skeleton.RHip.x) / 2;
            double midY = (skeleton.LHip.y + skeleton.RHip.y) / 2;
            double midZ = (skeleton.LHip.z + skeleton.RHip.z) / 2;
            Joint SkeletonPointHipCenter = new Joint(midX, midY, midZ);

            boolean isReto;
            boolean isTorcido;

            //Verifica se está torcido
            if (Math.Point3Y0GetAngleBetween(skeleton.RShoulder, skeleton.LShoulder, skeleton.RHip, skeleton.LHip) >= 5) {
                isTorcido = true;
            } else {
                isTorcido = false;
            }

            //Verifica se está reta
            if (Math.Point3Z0GetAngleBetween(SkeletonPointHipCenter,skeleton.Neck) >= 80) {
                isReto = true;
            } else {
                isReto = false;
            }
            posicaoCostas = OWASClassificationSpine(isReto, isTorcido);

        }
        return posicaoCostas;
    }


    //Classificação da espinha
    private CostaPosicao OWASClassificationSpine(boolean IsReto, boolean IsTorcido) {
        //---------------------------------------------------------------------------------------------------
        //OWAS Classification of SPINE
        //1)	Reto
        //2)	Inclinado
        //3)	Reto e Torcido
        //4)    Inclinado e Torcido
        //---------------------------------------------------------------------------------------------------

        //Inicialize the value
        CostaPosicao posicao = CostaPosicao.Desconhecida;

        if (IsReto == true & IsTorcido == false) {
            posicao = CostaPosicao.Ereta;
            //Console.Write("Ereta");
        }

        if (IsReto == false & IsTorcido == false) {
            posicao = CostaPosicao.Inclinada;
            //Console.Write("Inclinada");
        }

        if (IsReto == true & IsTorcido == true) {
            posicao = CostaPosicao.EretaETorcida;
            //Console.Write("Torcida");
        }
        if (IsReto == false & IsTorcido == true) {
            posicao = CostaPosicao.InclinadaETorcida;
            //Console.Write("Inclinada e torcida");
        }
        return posicao;
    }


    //BRACOS


    //se esta para cima
    private boolean IsAbove(Joint FirstPoint, Joint SecondPoint) {

        if (FirstPoint.y > SecondPoint.y) {
            return true;
        } else {
            return false;
        }

    }

    //classificacao dos bracos
    private BracosPosicao ArmsOWASClassification(Joint SkeletonPointHandLeft, Joint SkeletonPointShouderLeft, Joint SkeletonPointElbowLeft, Joint SkeletonPointHandRight, Joint SkeletonPointShouderRight, Joint SkeletonPointElbowRight) {
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


        /*LOGS FOR DEBUGGING
        if(IsAbove(SkeletonPointHandLeft, SkeletonPointShouderLeft)){
            Log.i("ArmsClassification","Mão esquerda acima do ombro");
            Log.i("Mão esquerda e ombro:", SkeletonPointHandLeft.y+"," + SkeletonPointShouderLeft.y);
        }
        if(IsAbove(SkeletonPointElbowLeft, SkeletonPointShouderLeft)){
            Log.i("ArmsClassification","Cotovelo esquerda acima do ombro");
            Log.i("coto esquerdo e ombro:", SkeletonPointElbowLeft.y+"," + SkeletonPointShouderLeft.y);
        }
        if(IsAbove(SkeletonPointHandRight, SkeletonPointShouderRight)){
            Log.i("ArmsClassification","Mão direita acima do ombro");
            Log.i("Mão direita e ombro:", SkeletonPointHandRight.y+"," + SkeletonPointShouderRight.y);
        }
        if(IsAbove(SkeletonPointElbowRight, SkeletonPointShouderRight)){
            Log.i("ArmsClassification","Cotovelo direito acima do ombro");
            Log.i("coto direito e ombro:", SkeletonPointElbowRight.y+"," + SkeletonPointShouderRight.y);
        }
        */


        if ((IsAbove(SkeletonPointHandLeft, SkeletonPointShouderLeft) & IsAbove(SkeletonPointElbowLeft, SkeletonPointShouderLeft)) & (IsAbove(SkeletonPointHandRight, SkeletonPointShouderRight) & IsAbove(SkeletonPointElbowRight, SkeletonPointShouderRight))) {
            posicaoBraco = BracosPosicao.TwoHandUp;

        } else if ((IsAbove(SkeletonPointHandLeft, SkeletonPointShouderLeft) & IsAbove(SkeletonPointElbowLeft, SkeletonPointShouderLeft)) || (IsAbove(SkeletonPointHandRight, SkeletonPointShouderRight) & IsAbove(SkeletonPointElbowRight, SkeletonPointShouderRight))) {
            posicaoBraco = BracosPosicao.OneHandUp;

        } else {
            posicaoBraco = BracosPosicao.BothArmsareDown;
        }
        return posicaoBraco;
    }


        public PernasPosicao LegOWASClassification(Joint SkeletonPointAnkleRight, Joint SkeletonPointKneeRight,
                                                    Joint SkeletonPointAnkleLeft, Joint SkeletonPointKneeLeft,
                                                 Joint SkeletonPointHipLeft, Joint SkeletonPointHipRight) {


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
            Point3D PointAnkleLeft = new Point3D();
            Point3D PointAnkleRight = new Point3D();

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


            PointAnkleLeft.x = SkeletonPointAnkleLeft.x;
            PointAnkleLeft.y = SkeletonPointAnkleLeft.y;
            PointAnkleLeft.z = SkeletonPointAnkleLeft.z;

            PointHipRight.x = SkeletonPointHipRight.x;
            PointHipRight.y = SkeletonPointHipRight.y;
            PointHipRight.z = SkeletonPointHipRight.z;


            PointKneeRight.x = SkeletonPointKneeRight.x;
            PointKneeRight.y = SkeletonPointKneeRight.y;
            PointKneeRight.z = SkeletonPointKneeRight.z;

            PointAnkleRight.x = SkeletonPointAnkleRight.x;
            PointAnkleRight.y = SkeletonPointAnkleRight.y;
            PointAnkleRight.z = SkeletonPointAnkleRight.z;

            //TODO FINALIZAR A CLASSIFICAÇÃO DAS PERNAS

            if ((Math.Point3Z0GetAngleBetween(SkeletonPointKneeRight, SkeletonPointAnkleRight) > 80) || (Math.Point3Z0GetAngleBetween(SkeletonPointKneeLeft, SkeletonPointAnkleLeft) > 80))//test if one leg is straight
            {
                if ((Math.Point3Z0GetAngleBetween(SkeletonPointKneeRight, SkeletonPointAnkleRight) > 80) && (Math.Point3Z0GetAngleBetween(SkeletonPointKneeLeft, SkeletonPointAnkleLeft) > 80))//test if both legs are straight
                {
                    if ((java.lang.Math.round(PointHipLeft.DistanceTo(PointAnkleLeft)) > java.lang.Math.round(PointHipLeft.DistanceTo(PointKneeLeft))) || (java.lang.Math.round(PointHipRight.DistanceTo(PointAnkleRight)) > java.lang.Math.round(PointHipRight.DistanceTo(PointKneeRight))))//if both  knees are not flexed and are too far to be kneeled
                    {
                        posicaoPernas = PernasPosicao.twoLegUp;
                    }
                } else {
                    posicaoPernas = PernasPosicao.OneLegUp;
                }
            }
            if ((Math.Point3Z0GetAngleBetween(SkeletonPointKneeRight, SkeletonPointAnkleRight) <= 80) || (Math.Point3Z0GetAngleBetween(SkeletonPointKneeLeft, SkeletonPointAnkleLeft) <= 80))//test if one leg is bent
            {
                if ((Math.Point3Z0GetAngleBetween(SkeletonPointKneeRight, SkeletonPointAnkleRight) <= 80) & (Math.Point3Z0GetAngleBetween(SkeletonPointKneeLeft, SkeletonPointAnkleLeft) <= 80)) {//testing if the two of them ate bent
                    if ((java.lang.Math.round(PointHipLeft.DistanceTo(PointAnkleLeft)) <= java.lang.Math.round(PointHipLeft.DistanceTo(PointKneeLeft))) || (java.lang.Math.round(PointHipRight.DistanceTo(PointAnkleRight)) <= java.lang.Math.round(PointHipRight.DistanceTo(PointKneeRight))))//if both  knees are flexed and are too close to not be kneeled
                    {
                        posicaoPernas = PernasPosicao.OneLegknee;
                    } else {
                        posicaoPernas = PernasPosicao.twoLegFlex;
                    }
                }else{
                    posicaoPernas = PernasPosicao.OneLegFlex;
                }
            }
            return posicaoPernas;

        }
}