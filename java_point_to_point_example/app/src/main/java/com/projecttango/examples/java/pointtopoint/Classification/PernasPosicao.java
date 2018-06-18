package com.projecttango.examples.java.pointtopoint.Classification;

/**
 * Created by pedropva on 08/06/2018.
 */

//---------------------------------------------------------------------------------------------------
//Classification of Leg
//1)	Duas pernas retas: As duas pernas esta no chão (classificação 2 = True) e as duas penas estão retas (classificação 1 = True para as duas pernas) .
//2)	Uma perna reta :  As duas pernas não estão no chão (classificação 2 = False) e uma das pernas  está reta ( classificação 1 = False)
//3)	Duas pernas flexionadas: As duas pernas esta no chão ( classificação 2 = True) e as duas penas não estão retas (classificação 1 =False  para as duas pernas) .
//4)	Uma Perna Flexionada: As duas pernas não estão no chão (classificação 2 = False) e a perna que está no chão não está reta ( classificação 1 = False)
//5)	Uma perna Ajoelhada: As duas pernas esta no chão ( classificação 2 = True) e as duas pernas estão flexionada (classificação 1 =False  para as duas pernas ) e pelo menos uma perna está ajoelhada (classificação 1 = False)
//---------------------------------------------------------------------------------------------------
public enum PernasPosicao
{
    Desconhecida ,
    twoLegUp ,
    OneLegUp ,
    twoLegFlex ,
    OneLegFlex ,
    OneLegknee
};