using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using System.Xml;
using System.Xml.Linq;

namespace OWAS.OWAS
{
    class UtilReader
    {
        public String FileName { get; private set; }
        
        public UtilReader() {
            this.FileName = "owas_classification.xml";
        }

        public UtilReader(String fileName) {
            this.FileName = fileName;
        }

        public  String find(String timestamp) {
            String xml="";
            XElement root = XElement.Load(this.FileName);
            IEnumerable<XElement> frame =
                from el in root.Elements("Frame")
                where (string)el.Attribute("timestamp") == timestamp
                select el;
            foreach (XElement el in frame)
            {
                Console.WriteLine(el);
                xml += el.ToString();
            }
            return xml;
        }

        public  Owas findOwasPosition(String timestamp)
        {
            Owas owas = new Owas();
            XElement root = XElement.Load(this.FileName);
            IEnumerable<XElement> frame =
                from el in root.Elements("Frame")
                where (string)el.Attribute("timestamp") == timestamp
                select el;
            foreach (XElement el in frame)
            {
                owas.BackPosition = (CostaPosicao)Enum.Parse(typeof(CostaPosicao), el.Attribute("spine").Value.ToString());
                owas.ArmsPosition = (BracosPosicao)Enum.Parse(typeof(BracosPosicao), el.Attribute("arms").Value.ToString());
                owas.LegsPosition = (PernasPosicao)Enum.Parse(typeof(PernasPosicao), el.Attribute("legs").Value.ToString());
            }
            return owas;
        }

        public String updatePositions(String timestamp,CostaPosicao bp,BracosPosicao ap, PernasPosicao lp)
        {
            String fileName = this.FileName;
            String xml = "";
            
            XElement root = XElement.Load(fileName);
            IEnumerable<XElement> frame =
                from el in root.Elements("Frame")
                where (string)el.Attribute("timestamp") == timestamp
                select el;
            foreach (XElement el in frame)
            {
                 el.Attribute("spine").Value = bp.ToString();
                 el.Attribute("arms").Value = ap.ToString();
                 el.Attribute("legs").Value = lp.ToString();
                 xml += el.ToString();
            }
            root.Save(fileName);
            return xml;
        }

        public  String updateBackPosition(String timestamp, CostaPosicao bp)
        {
            String fileName = this.FileName;
            String xml = "";

            XElement root = XElement.Load(fileName);
            IEnumerable<XElement> frame =
                from el in root.Elements("Frame")
                where (string)el.Attribute("timestamp") == timestamp
                select el;
            foreach (XElement el in frame)
            {
                el.Attribute("spine").Value = bp.ToString();
                xml += el.ToString();
            }
            root.Save(fileName);
            return xml;
        }
        public String updateBackPosition(long start,long end, CostaPosicao bp)
        {
            if (start > end)
                return "";
            String fileName = this.FileName;
            String xml = "";

            XElement root = XElement.Load(fileName);
            IEnumerable<XElement> frame =
                from el in root.Elements("Frame")
                where (long) el.Attribute("timestamp") >= start
                select el;
            foreach (XElement el in frame)
            {
                if ((long)(Double.Parse(el.Attribute("timestamp").Value)) < end)
                {
                    el.Attribute("spine").Value = bp.ToString();
                    xml += el.ToString();
                }
                else {
                    break;
                }
                
            }
            root.Save(fileName);
            return xml;
        }

        public String updateArmsPosition(String timestamp,  BracosPosicao ap)
        {
            String fileName = this.FileName;
            String xml = "";

            XElement root = XElement.Load(fileName);
            IEnumerable<XElement> frame =
                from el in root.Elements("Frame")
                where (string)el.Attribute("timestamp") == timestamp
                select el;
            foreach (XElement el in frame)
            {   
                el.Attribute("arms").Value = ap.ToString();
                xml += el.ToString();
            }
            root.Save(fileName);
            return xml;
        }
        public String updateArmsPosition(long start, long end, BracosPosicao ap)
        {
            if (start > end)
                return "";
            String fileName = this.FileName;
            String xml = "";

            XElement root = XElement.Load(fileName);
            IEnumerable<XElement> frame =
                from el in root.Elements("Frame")
                where (long)el.Attribute("timestamp") >= start
                select el;
            foreach (XElement el in frame)
            {
                if ((long)(Double.Parse(el.Attribute("timestamp").Value)) < end)
                {
                    el.Attribute("arms").Value = ap.ToString();
                    xml += el.ToString();
                }
                else
                {
                    break;
                }

            }
            root.Save(fileName);
            return xml;
        }
        
        public String updateLegsPosition(String timestamp,PernasPosicao lp)
        {
            String fileName =this.FileName;
            String xml = "";

            XElement root = XElement.Load(fileName);
            IEnumerable<XElement> frame =
                from el in root.Elements("Frame")
                where (string)el.Attribute("timestamp") == timestamp
                select el;
            foreach (XElement el in frame)
            {
                el.Attribute("legs").Value = lp.ToString();
                xml += el.ToString();
            }
            root.Save(fileName);
            return xml;
        }
        public String updateLegsPosition(long start, long end, PernasPosicao lp) {
            if (start > end)
                return "";
            String fileName = this.FileName;
            String xml = "";

            XElement root = XElement.Load(fileName);
            IEnumerable<XElement> frame =
                from el in root.Elements("Frame")
                where (long)el.Attribute("timestamp") >= start
                select el;
            foreach (XElement el in frame)
            {
                if ((long)(Double.Parse(el.Attribute("timestamp").Value)) < end)
                {
                    el.Attribute("legs").Value = lp.ToString();
                    xml += el.ToString();
                }
                else
                {
                    break;
                }

            }
            root.Save(fileName);
            return xml;
        }

        public void Relatorio(String relatorioName)
        {
            String fileName = this.FileName;
      
            XElement root = XElement.Load(fileName);
            IEnumerable<XElement> frame =
                from el in root.Elements("Frame")
                select el;
            
            int[] arms= new int[4];
            int[] spine = new int[5];
            int[] legs = new int[6];
            for (int i = 0; i < arms.Length; i++)
            {
                arms[i] = 0;
                spine[i] = 0;
                legs[i] = 0;
            }
            int index,kindex,jindex;
            int totalFrames = 0;
           // DDebug db = new DDebug(); db.Show();
            foreach (XElement el in frame)
            {
                index =(int) ((BracosPosicao)Enum.Parse(typeof(BracosPosicao), el.Attribute("arms").Value.ToString()));
                kindex = (int)((CostaPosicao)Enum.Parse(typeof( CostaPosicao), el.Attribute("spine").Value.ToString()));
                jindex = (int)((PernasPosicao)Enum.Parse(typeof(PernasPosicao), el.Attribute("legs").Value.ToString()));

                arms[index] += 1;
                spine[kindex] += 1;
                legs[jindex] += 1;
                totalFrames++;
            }

            XmlWriter writer = XmlWriter.Create(relatorioName);
           writer.WriteStartDocument();
           writer.WriteStartElement("classOwas");
           double percent =0.0;
           CostaPosicao costaP =CostaPosicao.Desconhecida;
           
            for (int i = 1; i < spine.Length; i++)
            {
                percent = ((double)spine[i] / (double)totalFrames * 100) ;
                writer.WriteStartElement("member");
                writer.WriteAttributeString(null, "name", null, "spine");
                switch (i) {
                    case 1: costaP = CostaPosicao.Ereta; break;
                    case 2: costaP = CostaPosicao.Inclinada; break;
                    case 3: costaP = CostaPosicao.EretaETorcida; break;
                    case 4: costaP = CostaPosicao.InclinadaETorcida; break;
                }
                writer.WriteAttributeString(null, "position", null, costaP + "");
                writer.WriteAttributeString(null, "percent", null, percent + "");
                writer.WriteAttributeString(null, "risk", null, Owas.riskSpine(costaP,percent)+"");
                writer.WriteEndElement();

            }
            
            BracosPosicao bracosP = BracosPosicao.Desconhecida;

            for (int i = 1; i < arms.Length; i++)
            {
                switch(i){
                    case 1: bracosP = BracosPosicao.BothArmsareDown;break;
                    case 2: bracosP = BracosPosicao.OneHandUp; break;
                    case 3: bracosP = BracosPosicao.TwoHandUp; break;    
                }
                percent = ((double)arms[i] / (double)totalFrames * 100);
                writer.WriteStartElement("member");
                writer.WriteAttributeString(null, "name", null, "arms");

                writer.WriteAttributeString(null, "position", null, bracosP + "");
                writer.WriteAttributeString(null, "percent", null, percent + "");
                writer.WriteAttributeString(null, "risk", null, Owas.riskArms(bracosP, percent) + "");
                writer.WriteEndElement();
            }

            PernasPosicao pernasP = PernasPosicao.Desconhecida;

            for (int i = 0; i < legs.Length; i++)
            {
                switch (i)
                {
                    case 1: pernasP = PernasPosicao.twoLegUp; break;
                    case 2: pernasP = PernasPosicao.OneLegUp; break;
                    case 3: pernasP = PernasPosicao.twoLegFlex; break;
                    case 4: pernasP = PernasPosicao.OneLegFlex; break;
                    case 5: pernasP = PernasPosicao.OneLegknee; break;
                   
                }
                percent = ((double)legs[i] / (double)totalFrames * 100);
                writer.WriteStartElement("member");
                writer.WriteAttributeString(null, "name", null, "legs");

                writer.WriteAttributeString(null, "position", null, pernasP + "");
                writer.WriteAttributeString(null, "percent", null, percent + "");
                writer.WriteAttributeString(null, "risk", null, Owas.riskLegs(pernasP,percent)+"");
                writer.WriteEndElement();

            }
            writer.WriteEndElement();
            writer.WriteEndDocument();
            writer.Flush();
            writer.Close();
        }
        
        public List<KeyValuePair<string, double>> RelatorioList()
        {
            String fileName = this.FileName;

            XElement root = XElement.Load(fileName);
            IEnumerable<XElement> frame =
                from el in root.Elements("Frame")
                select el;

            int[] arms = new int[4];
            int[] spine = new int[5];
            int[] legs = new int[6];

            List<KeyValuePair<string, double>> posicoes = new List<KeyValuePair<string, double>>();
            
            //

            for (int i = 0; i < arms.Length; i++)
            {
                arms[i] = 0;
                spine[i] = 0;
                legs[i] = 0;
            }
            int index, kindex, jindex;
            int totalFrames = 0;
        
            foreach (XElement el in frame)
            {
                index = (int)((BracosPosicao)Enum.Parse(typeof(BracosPosicao), el.Attribute("arms").Value.ToString()));
                kindex = (int)((CostaPosicao)Enum.Parse(typeof(CostaPosicao), el.Attribute("spine").Value.ToString()));
                jindex = (int)((PernasPosicao)Enum.Parse(typeof(PernasPosicao), el.Attribute("legs").Value.ToString()));

                arms[index] += 1;
                spine[kindex] += 1;
                legs[jindex] += 1;
                totalFrames++;
            }

            
            double percent = 0.0;
            CostaPosicao costaP = CostaPosicao.Desconhecida;
            String nomePosicao = "Desconhecida";
            for (int i = 1; i < spine.Length; i++)
            {
                percent = ((double)spine[i] / (double)totalFrames * 100);
                switch (i)
                {
                    case 1: nomePosicao = "Ereta";
                            costaP = CostaPosicao.Ereta; 
                            break;
                    case 2: nomePosicao = "Inclinada";
                            costaP = CostaPosicao.Inclinada;
                            break;
                    case 3: nomePosicao = "Ereta e Torcida";
                            costaP = CostaPosicao.EretaETorcida; 
                            break;
                    case 4: nomePosicao = "Inclinada e Torcida";
                            costaP = CostaPosicao.InclinadaETorcida; 
                        break;
                }
                posicoes.Add(new KeyValuePair<string, double>(nomePosicao, percent)); 
            }
            nomePosicao = "Desconhecida";
            BracosPosicao bracosP = BracosPosicao.Desconhecida;
            
            for (int i = 1; i < arms.Length; i++)
            {
                
                switch (i)
                {
                    case 1:
                        nomePosicao = "Braços para baixo";
                        bracosP = BracosPosicao.BothArmsareDown; 
                        break;
                    case 2:
                        nomePosicao = "Um braço para cima";
                        bracosP = BracosPosicao.OneHandUp; 
                       break;
                    case 3:
                       nomePosicao = "Braços para cima";
                        bracosP = BracosPosicao.TwoHandUp; 
                        break;
                }
                percent = ((double)arms[i] / (double)totalFrames * 100);
                posicoes.Add(new KeyValuePair<string, double>(nomePosicao, percent)); 
            }

            nomePosicao = "Desconhecida";
            PernasPosicao pernasP = PernasPosicao.Desconhecida;

            for (int i = 1; i < legs.Length; i++)
            {
                switch (i)
                {
                    case 1:
                        nomePosicao = "Pernas estão retas";
                        pernasP = PernasPosicao.twoLegUp; 
                        break;
                    case 2:
                        nomePosicao = "Uma perna está reta";
                        pernasP = PernasPosicao.OneLegUp; 
                        break;
                    case 3:
                        nomePosicao = "Duas pernas flexionadas";
                        pernasP = PernasPosicao.twoLegFlex;
                        break;
                    case 4:
                        nomePosicao = "Uma perna flexionada";
                        pernasP = PernasPosicao.OneLegFlex; 
                        break;
                    case 5:
                        nomePosicao = "Ajoelhado";
                        pernasP = PernasPosicao.OneLegknee; 
                        break;
                }
                
                percent = ((double)legs[i] / (double)totalFrames * 100);
                posicoes.Add(new KeyValuePair<string, double>(nomePosicao, percent));
            }
            return posicoes;
        }

        public List<KeyValuePair<string, double>> RelatorioListBack()
        {
            String fileName = this.FileName;

            XElement root = XElement.Load(fileName);
            IEnumerable<XElement> frame =
                from el in root.Elements("Frame")
                select el;

            int[] arms = new int[4];
            int[] spine = new int[5];
            int[] legs = new int[6];

            List<KeyValuePair<string, double>> posicoes = new List<KeyValuePair<string, double>>();

            int totalFrames = 0;

            for (int i = 0; i < arms.Length; i++)
            {
                arms[i] = 0;
                spine[i] = 0;
                legs[i] = 0;
            }
            
            int index, kindex, jindex;
            
            foreach (XElement el in frame)
            {
                index = (int)((BracosPosicao)Enum.Parse(typeof(BracosPosicao), el.Attribute("arms").Value.ToString()));
                kindex = (int)((CostaPosicao)Enum.Parse(typeof(CostaPosicao), el.Attribute("spine").Value.ToString()));
                jindex = (int)((PernasPosicao)Enum.Parse(typeof(PernasPosicao), el.Attribute("legs").Value.ToString()));

                arms[index] += 1;
                spine[kindex] += 1;
                legs[jindex] += 1;
                totalFrames++;
            }

            double percent = 0.0;

            CostaPosicao costaP = CostaPosicao.Desconhecida;
            String nomePosicao = "Desconhecida";
            for (int i = 1; i < spine.Length; i++)
            {
                percent = ((double)spine[i] / (double)totalFrames * 100);
                switch (i)
                {
                    case 1: nomePosicao = "Ereta";
                        costaP = CostaPosicao.Ereta;
                        break;
                    case 2: nomePosicao = "Inclinada";
                        costaP = CostaPosicao.Inclinada;
                        break;
                    case 3: nomePosicao = "Ereta e Torcida";
                        costaP = CostaPosicao.EretaETorcida;
                        break;
                    case 4: nomePosicao = "Inclinada e Torcida";
                        costaP = CostaPosicao.InclinadaETorcida;
                        break;
                }
                posicoes.Add(new KeyValuePair<string, double>(nomePosicao, percent));
            }

            return posicoes;
           }

        public List<KeyValuePair<string, double>> RelatorioListArms()
        {
            String fileName = this.FileName;

            XElement root = XElement.Load(fileName);
            IEnumerable<XElement> frame =
                from el in root.Elements("Frame")
                select el;

            int[] arms = new int[4];
            int[] spine = new int[5];
            int[] legs = new int[6];

            List<KeyValuePair<string, double>> posicoes = new List<KeyValuePair<string, double>>();

            //

            for (int i = 0; i < arms.Length; i++)
            {
                arms[i] = 0;
                spine[i] = 0;
                legs[i] = 0;
            }
            int index, kindex, jindex;
            int totalFrames = 0;

            foreach (XElement el in frame)
            {
                index = (int)((BracosPosicao)Enum.Parse(typeof(BracosPosicao), el.Attribute("arms").Value.ToString()));
                kindex = (int)((CostaPosicao)Enum.Parse(typeof(CostaPosicao), el.Attribute("spine").Value.ToString()));
                jindex = (int)((PernasPosicao)Enum.Parse(typeof(PernasPosicao), el.Attribute("legs").Value.ToString()));

                arms[index] += 1;
                spine[kindex] += 1;
                legs[jindex] += 1;
                totalFrames++;
            }

            double percent = 0.0;
            String nomePosicao = "Desconhecida";
            
            nomePosicao = "Desconhecida";
            BracosPosicao bracosP = BracosPosicao.Desconhecida;

            for (int i = 1; i < arms.Length; i++)
            {

                switch (i)
                {
                    case 1:
                        nomePosicao = "Braços para baixo";
                        bracosP = BracosPosicao.BothArmsareDown;
                        break;
                    case 2:
                        nomePosicao = "Um braço para cima";
                        bracosP = BracosPosicao.OneHandUp;
                        break;
                    case 3:
                        nomePosicao = "Braços para cima";
                        bracosP = BracosPosicao.TwoHandUp;
                        break;
                }
                percent = ((double)arms[i] / (double)totalFrames * 100);
                posicoes.Add(new KeyValuePair<string, double>(nomePosicao, percent));
            }

            return posicoes;
        }

        public List<KeyValuePair<string, double>> RelatorioListLegs()
        {
            String fileName = this.FileName;

            XElement root = XElement.Load(fileName);
            IEnumerable<XElement> frame =
                from el in root.Elements("Frame")
                select el;

            int[] arms = new int[4];
            int[] spine = new int[5];
            int[] legs = new int[6];

            List<KeyValuePair<string, double>> posicoes = new List<KeyValuePair<string, double>>();

            //

            for (int i = 0; i < arms.Length; i++)
            {
                arms[i] = 0;
                spine[i] = 0;
                legs[i] = 0;
            }
            int index, kindex, jindex;
            int totalFrames = 0;

            foreach (XElement el in frame)
            {
                index = (int)((BracosPosicao)Enum.Parse(typeof(BracosPosicao), el.Attribute("arms").Value.ToString()));
                kindex = (int)((CostaPosicao)Enum.Parse(typeof(CostaPosicao), el.Attribute("spine").Value.ToString()));
                jindex = (int)((PernasPosicao)Enum.Parse(typeof(PernasPosicao), el.Attribute("legs").Value.ToString()));

                arms[index] += 1;
                spine[kindex] += 1;
                legs[jindex] += 1;
                totalFrames++;
            }


            double percent = 0.0;
            String nomePosicao = "Desconhecida";

            PernasPosicao pernasP = PernasPosicao.Desconhecida;

            for (int i = 1; i < legs.Length; i++)
            {
                switch (i)
                {
                    case 1:
                        nomePosicao = "Pernas estão retas";
                        pernasP = PernasPosicao.twoLegUp;
                        break;
                    case 2:
                        nomePosicao = "Uma perna está reta";
                        pernasP = PernasPosicao.OneLegUp;
                        break;
                    case 3:
                        nomePosicao = "Duas pernas flexionadas";
                        pernasP = PernasPosicao.twoLegFlex;
                        break;
                    case 4:
                        nomePosicao = "Uma perna flexionada";
                        pernasP = PernasPosicao.OneLegFlex;
                        break;
                    case 5:
                        nomePosicao = "Ajoelhado";
                        pernasP = PernasPosicao.OneLegknee;
                        break;
                }

                percent = ((double)legs[i] / (double)totalFrames * 100);
                posicoes.Add(new KeyValuePair<string, double>(nomePosicao, percent));
            }
            return posicoes;
        }
    }
}
