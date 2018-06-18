package com.projecttango.examples.java.pointtopoint.Classification;

/**
 * Created by pedropva on 08/06/2018.
 */
//---------------------------------------------------------------------------------------------------
//OWAS Classification of SPINE
//1)	Reto
//2)	Inclinado
//3)	Reto e Torcido
//4)    Inclinado e Torcido
//---------------------------------------------------------------------------------------------------
public enum CostaPosicao
{
    Desconhecida ,
    Ereta ,
    Inclinada ,
    EretaETorcida ,
    InclinadaETorcida
};
