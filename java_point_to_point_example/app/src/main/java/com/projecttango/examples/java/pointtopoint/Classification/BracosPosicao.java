package com.projecttango.examples.java.pointtopoint.Classification;

/**
 * Created by pedropva on 08/06/2018.
 */
//---------------------------------------------------------------------------------------------------
//Classification of Arms
//1 - Both Arms are Down:  Right and Left Hand below shouder and  Right and Left Elbow  below shouder
//2 - One Hand up : At least one hand above shouder
//3 - Two Hand Up: Right and Left Hand above
// 1 Ambos os braços para baixo: Direita e Esquerda Mão abaixo do ombro e Direita e Esquerda cotovelo abaixo do ombro
// 2-uma mao para cima:Pelo menos uma das mãos acima ombro
// 3-duas maos para cima:mão direita e esquerda acima;
//---------------------------------------------------------------------------------------------------
public enum BracosPosicao
{
    Desconhecida ,
    BothArmsareDown ,
    OneHandUp ,
    TwoHandUp;
};