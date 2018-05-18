using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace OWAS
{
    public enum CostaPosicao : int
    {
        Desconhecida = 0,
        Ereta = 1,
        Inclinada = 2,
        EretaETorcida = 3,
        InclinadaETorcida = 4
    };

    public enum BracosPosicao : int
    {
        Desconhecida = 0,
        BothArmsareDown = 1,
        OneHandUp = 2,
        TwoHandUp = 3

    };

    //1)	Duas pernas retas: As duas pernas esta no chão (classificação 2 = True) e as duas penas estão retas (classificação 1 = True para as duas pernas) . 
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

    public enum Results
    {
        Nivel1 = 1,//Não são necessárias medidas corretivas
        Nivel2 = 2,// São necessárias correções em um futuro próximo
        Nivel3 = 3,// São necessárias correções tão logo quanto possível
        Nivel4 = 4 // São necessárias correções imediatas
    };

    public enum Weigth
    {
        Nivel1 = 1, // Até 10kg
        Nivel2 = 2, //entre 10kg-20kg
        Nivel3 = 3  // maior que 20kg
    }

    public class Owas
    {
        public CostaPosicao BackPosition { get; set; }
        public BracosPosicao ArmsPosition { get; set; }
        public PernasPosicao LegsPosition { get; set; }
        public Weigth _Weight { get; set; }

        public Owas()
        {
            BackPosition = CostaPosicao.Desconhecida;
            ArmsPosition = BracosPosicao.Desconhecida;
            LegsPosition = PernasPosicao.Desconhecida;
            _Weight = Weigth.Nivel3;
        }

        public Owas(CostaPosicao bp, BracosPosicao ap, PernasPosicao lp, Weigth weight)
        {
            this.BackPosition = bp;
            this.ArmsPosition = ap;
            this.LegsPosition = lp;
            this._Weight = weight;
        }

        /*===================================================================================*/
        //Calculo do risco de cada membro por esforço exercido durante a atividade
        /*===================================================================================*/


        public Results evaluate()
        {

            int results = this.ClassOWAS((int)ArmsPosition,(int)BackPosition,(int) LegsPosition, (int) _Weight );
            
            switch(results){
                case 1: return  Results.Nivel1; 
                case 2: return Results.Nivel2;
                case 3: return Results.Nivel3;
                case 4: return Results.Nivel4;
            }

            return Results.Nivel1;
        }

        private int ClassOWAS(int Braco, int Dorso, int Perna, int Peso)
        {

            // Para Dorso = 1 

            if (Dorso == 1)
            {
                if (Braco == 1 | Braco == 2)
                {
                    if (Perna == 1 | Perna == 2 | Perna == 3)
                    {
                        return 1;
                    }
                    if (Perna == 4 | Perna == 5)
                    {
                        return 2;
                    }
                }

                if (Braco == 3)
                {
                    if (Perna == 1 | Perna == 2 | Perna == 3)
                    {
                        return 1;
                    }
                    if (Perna == 4 | Perna == 5)
                    {
                        if (Peso == 3)
                        {
                            return 3;
                        }
                        else
                        {
                            return 2;
                        }

                    }
                }
            }



            // Para Dorso = 2 

            if (Dorso == 2)
            {

                if (Braco == 1)
                {
                    if (Perna == 1 | Perna == 2 | Perna == 3)
                    {
                        if (Peso == 3)
                        {
                            return 3;
                        }
                        else
                        {
                            return 2;
                        }
                    }
                    if (Perna == 4 | Perna == 5)
                    {
                        return 3;
                    }
                }


                if (Braco == 2)
                {
                    if (Perna == 1 | Perna == 2)
                    {
                        if (Peso == 3 | Peso == 2)
                        {
                            return 3;
                        }
                        else
                        {
                            return 2;
                        }
                    }

                    if (Perna == 3)
                    {
                        if (Peso == 3)
                        {
                            return 3;
                        }
                        else
                        {
                            return 2;
                        }
                    }

                    if (Perna == 4 | Perna == 5)
                    {
                        if (Peso == 2 | Peso == 3)
                        {
                            return 4;
                        }
                        else
                        {
                            return 3;
                        }
                    }
                }




                if (Braco == 3)
                {
                    if (Perna == 1)
                    {
                        if (Peso == 1 | Peso == 2)
                        {
                            return 3;
                        }
                        else
                        {
                            return 4;
                        }
                    }

                    if (Perna == 2)
                    {
                        if (Peso == 1 | Peso == 2)
                        {
                            return 2;
                        }
                        else
                        {
                            return 3;
                        }
                    }

                    if (Perna == 3)
                    {
                        return 3;
                    }


                    if (Perna == 4)
                    {
                        if (Peso == 1)
                        {
                            return 3;
                        }
                        else
                        {
                            return 4;
                        }

                    }


                    if (Perna == 5)
                    {
                        return 4;
                    }

                }
            }


            //Para Dorso = 3 
            if (Dorso == 3)
            {
                if (Braco == 1)
                {
                    if (Perna == 1 | Perna == 2)
                    {
                        return 1;
                    }
                    if (Perna == 3)
                    {
                        if (Peso == 1 | Peso == 2)
                        {
                            return 1;
                        }
                        else
                        {
                            return 2;
                        }
                    }
                    if (Perna == 4)
                    {
                        return 3;
                    }
                    if (Perna == 5)
                    {
                        return 4;
                    }

                }


                if (Braco == 2)
                {
                    if (Perna == 1)
                    {
                        if (Peso == 1 | Peso == 2)
                        {
                            return 2;
                        }
                        else
                        {
                            return 3;
                        }
                    }

                    if (Perna == 2)
                    {
                        return 1;
                    }


                    if (Perna == 3)
                    {
                        if (Peso == 1 | Peso == 2)
                        {
                            return 1;
                        }
                        else
                        {
                            return 2;
                        }
                    }

                    if (Perna == 4 | Perna == 5)
                    {
                        return 4;
                    }

                }



                if (Braco == 3)
                {
                    if (Perna == 1)
                    {
                        if (Peso == 1 | Peso == 2)
                        {
                            return 2;
                        }
                        else
                        {
                            return 3;
                        }
                    }

                    if (Perna == 2)
                    {
                        return 1;
                    }


                    if (Perna == 3)
                    {
                        if (Peso == 1)
                        {
                            return 2;
                        }
                        else
                        {
                            return 3;
                        }
                    }

                    if (Perna == 4 | Perna == 5)
                    {
                        return 4;
                    }

                }

            }

            //Para Dorso = 4

            if (Dorso == 4)
            {
                if (Braco == 1)
                {
                    if (Perna == 1)
                    {
                        if (Peso == 1)
                        {
                            return 2;
                        }
                        else
                        {
                            return 3;
                        }
                    }

                    if (Perna == 2)
                    {
                        if (Peso == 3)
                        {
                            return 3;
                        }
                        else
                        {
                            return 2;
                        }
                    }

                    if (Perna == 3)
                    {
                        if (Peso == 1)
                        {
                            return 3;
                        }
                        else
                        {
                            return 2;
                        }
                    }


                    if (Perna == 4 | Perna == 5)
                    {
                        return 4;
                    }

                }


                if (Braco == 2)
                {
                    if (Perna == 1)
                    {
                        if (Peso == 3)
                        {
                            return 4;
                        }
                        else
                        {
                            return 3;
                        }
                    }


                    if (Perna == 2)
                    {
                        if (Peso == 1)
                        {
                            return 2;
                        }
                        else
                        {
                            if (Peso == 2)
                            {
                                return 3;
                            }
                            else
                            {
                                return 4;
                            }
                        }

                        if (Perna == 3)
                        {
                            if (Peso == 3)
                            {
                                return 4;
                            }
                            else
                            {
                                return 3;
                            }
                        }

                        if (Perna == 4 | Perna == 5)
                        {
                            return 4;
                        }

                    }



                    if (Braco == 3)
                    {
                        if (Perna == 1)
                        {
                            return 4;
                        }
                    }

                    if (Perna == 2)
                    {
                        if (Peso == 1)
                        {
                            return 2;
                        }
                        else
                        {
                            if (Peso == 2)
                            {
                                return 3;
                            }
                            else
                            {
                                return 4;
                            }
                        }


                        if (Perna == 3)
                        {
                            if (Peso == 3)
                            {
                                return 4;
                            }
                            else
                            {
                                return 3;
                            }
                        }

                        if (Perna == 4 | Perna == 5)
                        {
                            return 4;
                        }

                    }

                }
            }
            return 0;
        }


        /*===================================================================================*/
        //Calculo do risco de cada membro por duração do tempo da atividade
        /*===================================================================================*/

        public static Results riskSpine(CostaPosicao cp, double percent)
        {
            if (cp == CostaPosicao.Ereta)
            {
                return Results.Nivel1;
            }
            else if (cp == CostaPosicao.Inclinada)
            {
                if (percent < 30)
                {
                    return Results.Nivel1;
                }
                else if (percent >= 30 && percent < 80)
                {
                    return Results.Nivel2;
                }
                else
                {
                    return Results.Nivel3;
                }
            }
            else if (cp == CostaPosicao.EretaETorcida)
            {
                if (percent < 20)
                {
                    return Results.Nivel1;
                }
                else if (percent >= 20 && percent < 50)
                {
                    return Results.Nivel2;
                }
                else
                {
                    return Results.Nivel3;
                }
            }
            else if (cp == CostaPosicao.InclinadaETorcida)
            {
                if (percent < 10)
                {
                    return Results.Nivel1;
                }
                else if (percent >= 10 && percent < 30)
                {
                    return Results.Nivel2;
                }
                else if (percent >= 30 && percent < 50)
                {
                    return Results.Nivel3;
                }
                else
                {
                    return Results.Nivel4;
                }
            }

            return Results.Nivel1;
        }

        public static Results riskArms(BracosPosicao bp, double percent)
        {
            //Braços abaixo do ombro
            if (bp == BracosPosicao.BothArmsareDown)
            {
                return Results.Nivel1;
            }
            //Um dos bracos abaixados    
            else if (bp == BracosPosicao.OneHandUp)
            {
                if (percent < 30)
                {
                    return Results.Nivel1;
                }
                else if (percent >= 30 && percent < 80)
                {
                    return Results.Nivel2;
                }
                else
                {
                    return Results.Nivel3;
                }

            }
            //Ambos os braços levantados
            else if (bp == BracosPosicao.TwoHandUp)
            {
                if (percent < 20)
                {
                    return Results.Nivel1;
                }
                else if (percent >= 20 && percent < 80)
                {
                    return Results.Nivel2;
                }
                else
                {
                    return Results.Nivel3;
                }
            }
            return Results.Nivel1;
        }

        public static Results riskLegs(PernasPosicao pp, double percent)
        {
            //Duas pernas retas
            if (pp == PernasPosicao.twoLegUp)
            {
                if (percent < 90)
                {
                    return Results.Nivel1;
                }
                else
                {
                    return Results.Nivel2;
                }
            }
            //Uma perna reta
            else if (pp == PernasPosicao.OneLegUp)
            {
                if (percent < 80)
                {
                    return Results.Nivel1;
                }
                else
                {
                    return Results.Nivel2;
                }
            }
            //Duas pernas flexionadas
            else if (pp == PernasPosicao.twoLegFlex)
            {
                if (percent < 30)
                {
                    return Results.Nivel1;
                }
                else if (percent >= 30 && percent < 80)
                {
                    return Results.Nivel2;
                }
                else
                {
                    return Results.Nivel3;
                }
            }
            //Uma perna flexionada
            else if (pp == PernasPosicao.OneLegFlex)
            {
                if (percent < 10)
                {
                    return Results.Nivel1;
                }
                else if (percent >= 10 && percent < 30)
                {
                    return Results.Nivel2;
                }
                else if (percent >= 30 && percent < 80)
                {
                    return Results.Nivel3;
                }
                else
                {
                    return Results.Nivel4;
                }
            }
            //Perna ajoelhada   
            else if (pp == PernasPosicao.OneLegknee)
            {
                if (percent < 10)
                {
                    return Results.Nivel1;
                }
                else if (percent >= 10 && percent < 30)
                {
                    return Results.Nivel2;
                }
                else if (percent >= 30 && percent < 80)
                {
                    return Results.Nivel3;
                }
                else
                {
                    return Results.Nivel4;
                }
            }
            return Results.Nivel1;
        }

        /*===================================================================================*/

    }
}
