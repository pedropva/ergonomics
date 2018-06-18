    package com.projecttango.examples.java.pointtopoint.Classification;


    public class OWAS_evaluation
    {
        public CostaPosicao BackPosition;
        public BracosPosicao ArmsPosition;
        public PernasPosicao LegsPosition;
        public Weigth _Weight;

        public  OWAS_evaluation()
        {
            BackPosition = CostaPosicao.Desconhecida;
            ArmsPosition = BracosPosicao.Desconhecida;
            LegsPosition = PernasPosicao.Desconhecida;
            _Weight = Weigth.Nivel3;
        }
        public String getNameResults(Results results) {
            if (results == Results.Nivel1)
                return "Nivel 1: Não são necessárias medidas corretivas.";
            else if (results == Results.Nivel2)
                return "Nivel 2: São necessárias correções em um futuro próximo.";
            else if (results == Results.Nivel3)
                return "Nivel 3: São necessárias correções tão logo quanto possível.";
            else if (results == Results.Nivel4)
                return "Nivel 4: São necessárias correções imediatas!";
            return "Nivel 1: Não são necessárias medidas corretivas.";
        }

        public OWAS_evaluation(CostaPosicao bp, BracosPosicao ap, PernasPosicao lp, Weigth weight)
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

            int results = this.ClassOWAS(ArmsPosition,BackPosition, LegsPosition,_Weight );

            switch(results){
                case 0: return  Results.Nivel1;
                case 1: return Results.Nivel2;
                case 2: return Results.Nivel3;
                case 3: return Results.Nivel4;
            }

            return Results.Nivel1;
        }

        private int ClassOWAS(BracosPosicao Braco, CostaPosicao Dorso, PernasPosicao Perna, Weigth Peso) {

            // Para Dorso = 1

            if (Dorso == CostaPosicao.Ereta) {
                if (Braco == BracosPosicao.BothArmsareDown | Braco == BracosPosicao.OneHandUp) {
                    if (Perna == PernasPosicao.twoLegUp | Perna == PernasPosicao.OneLegUp | Perna == PernasPosicao.twoLegFlex) {
                        return 1;
                    }
                    if (Perna == PernasPosicao.OneLegFlex | Perna == PernasPosicao.OneLegknee) {
                        return 2;
                    }
                }

                if (Braco == BracosPosicao.TwoHandUp) {
                    if (Perna == PernasPosicao.twoLegUp | Perna == PernasPosicao.OneLegUp | Perna == PernasPosicao.twoLegFlex) {
                        return 1;
                    }
                    if (Perna == PernasPosicao.OneLegFlex | Perna == PernasPosicao.OneLegknee) {
                        if (Peso == Weigth.Nivel3) {
                            return 3;
                        } else {
                            return 2;
                        }

                    }
                }
            }


            // Para Dorso = 2

            if (Dorso == CostaPosicao.Inclinada) {

                if (Braco == BracosPosicao.BothArmsareDown) {
                    if (Perna == PernasPosicao.twoLegUp | Perna == PernasPosicao.OneLegUp | Perna == PernasPosicao.twoLegFlex) {
                        if (Peso == Weigth.Nivel3) {
                            return 3;
                        } else {
                            return 2;
                        }
                    }
                    if (Perna == PernasPosicao.OneLegFlex | Perna == PernasPosicao.OneLegknee) {
                        return 3;
                    }
                }


                if (Braco == BracosPosicao.OneHandUp) {
                    if (Perna == PernasPosicao.twoLegUp | Perna == PernasPosicao.OneLegUp) {
                        if (Peso == Weigth.Nivel3 | Peso == Weigth.Nivel2) {
                            return 3;
                        } else {
                            return 2;
                        }
                    }

                    if (Perna == PernasPosicao.twoLegFlex) {
                        if (Peso == Weigth.Nivel3) {
                            return 3;
                        } else {
                            return 2;
                        }
                    }

                    if (Perna == PernasPosicao.OneLegFlex | Perna == PernasPosicao.OneLegknee) {
                        if (Peso == Weigth.Nivel2 | Peso == Weigth.Nivel3) {
                            return 4;
                        } else {
                            return 3;
                        }
                    }
                }


                if (Braco == BracosPosicao.TwoHandUp) {
                    if (Perna == PernasPosicao.twoLegUp) {
                        if (Peso == Weigth.Nivel1 | Peso == Weigth.Nivel2) {
                            return 3;
                        } else {
                            return 4;
                        }
                    }

                    if (Perna == PernasPosicao.OneLegUp) {
                        if (Peso == Weigth.Nivel1 | Peso == Weigth.Nivel2) {
                            return 2;
                        } else {
                            return 3;
                        }
                    }

                    if (Perna == PernasPosicao.twoLegFlex) {
                        return 3;
                    }


                    if (Perna == PernasPosicao.OneLegFlex) {
                        if (Peso == Weigth.Nivel1) {
                            return 3;
                        } else {
                            return 4;
                        }

                    }


                    if (Perna == PernasPosicao.OneLegknee) {
                        return 4;
                    }

                }
            }


            //Para Dorso = 3
            if (Dorso == CostaPosicao.EretaETorcida) {
                if (Braco == BracosPosicao.BothArmsareDown) {
                    if (Perna == PernasPosicao.twoLegUp | Perna == PernasPosicao.OneLegUp) {
                        return 1;
                    }
                    if (Perna == PernasPosicao.twoLegFlex) {
                        if (Peso == Weigth.Nivel1 | Peso == Weigth.Nivel2) {
                            return 1;
                        } else {
                            return 2;
                        }
                    }
                    if (Perna == PernasPosicao.OneLegFlex) {
                        return 3;
                    }
                    if (Perna == PernasPosicao.OneLegknee) {
                        return 4;
                    }

                }


                if (Braco == BracosPosicao.OneHandUp) {
                    if (Perna == PernasPosicao.twoLegUp) {
                        if (Peso == Weigth.Nivel1 | Peso == Weigth.Nivel2) {
                            return 2;
                        } else {
                            return 3;
                        }
                    }

                    if (Perna == PernasPosicao.OneLegUp) {
                        return 1;
                    }


                    if (Perna == PernasPosicao.twoLegFlex) {
                        if (Peso == Weigth.Nivel1 | Peso == Weigth.Nivel2) {
                            return 1;
                        } else {
                            return 2;
                        }
                    }

                    if (Perna == PernasPosicao.OneLegFlex | Perna == PernasPosicao.OneLegknee) {
                        return 4;
                    }

                }


                if (Braco == BracosPosicao.TwoHandUp) {
                    if (Perna == PernasPosicao.twoLegUp) {
                        if (Peso == Weigth.Nivel1 | Peso == Weigth.Nivel2) {
                            return 2;
                        } else {
                            return 3;
                        }
                    }

                    if (Perna == PernasPosicao.OneLegUp) {
                        return 1;
                    }


                    if (Perna == PernasPosicao.twoLegFlex) {
                        if (Peso == Weigth.Nivel1) {
                            return 2;
                        } else {
                            return 3;
                        }
                    }

                    if (Perna == PernasPosicao.OneLegFlex | Perna == PernasPosicao.OneLegknee) {
                        return 4;
                    }

                }

            }

            //Para Dorso = 4

            if (Dorso == CostaPosicao.InclinadaETorcida) {
                if (Braco == BracosPosicao.BothArmsareDown) {
                    if (Perna == PernasPosicao.twoLegUp) {
                        if (Peso == Weigth.Nivel1) {
                            return 2;
                        } else {
                            return 3;
                        }
                    }

                    if (Perna == PernasPosicao.OneLegUp) {
                        if (Peso == Weigth.Nivel3) {
                            return 3;
                        } else {
                            return 2;
                        }
                    }

                    if (Perna == PernasPosicao.twoLegFlex) {
                        if (Peso == Weigth.Nivel1) {
                            return 3;
                        } else {
                            return 2;
                        }
                    }


                    if (Perna == PernasPosicao.OneLegFlex | Perna == PernasPosicao.OneLegknee) {
                        return 4;
                    }

                }


                if (Braco == BracosPosicao.OneHandUp) {
                    if (Perna == PernasPosicao.twoLegUp) {
                        if (Peso == Weigth.Nivel3) {
                            return 4;
                        } else {
                            return 3;
                        }
                    }


                    if (Perna == PernasPosicao.OneLegUp) {

                        if (Peso == Weigth.Nivel1) {
                            return 2;
                        } else {
                            if (Peso == Weigth.Nivel2) {
                                return 3;
                            } else {
                                return 4;
                            }
                        }

                    }
                    if (Perna == PernasPosicao.twoLegFlex) {
                        if (Peso == Weigth.Nivel3)
                        {
                            return 4;
                        }
                        else
                        {
                            return 3;
                        }
                    }

                    if (Perna == PernasPosicao.OneLegFlex | Perna == PernasPosicao.OneLegknee) {
                        return 4;
                    }


                    if (Braco == BracosPosicao.TwoHandUp) {
                        if (Perna == PernasPosicao.twoLegUp) {
                            return 4;
                        }
                    }

                    if (Perna == PernasPosicao.OneLegUp) {
                        if (Peso == Weigth.Nivel1) {
                            return 2;
                        } else {
                            if (Peso == Weigth.Nivel2) {
                                return 3;
                            } else {
                                return 4;
                            }
                        }
                    }
                    if (Perna == PernasPosicao.twoLegFlex) {
                        if (Peso == Weigth.Nivel3) {
                            return 4;
                        } else {
                            return 3;
                        }
                    }

                    if (Perna == PernasPosicao.OneLegFlex | Perna == PernasPosicao.OneLegknee) {
                        return 4;
                    }
                }
                return 0;
            }
            return 0;//TODO VER PQ CHEGARIA AQUI
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

