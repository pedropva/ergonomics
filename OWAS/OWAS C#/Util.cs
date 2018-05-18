using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using System.Xml;


namespace OWAS.OWAS
{
    class Util
    {
        private XmlWriter writer;
        public String FileName {private set; get; }

        public Util(String fileName)
        {
            this.FileName = fileName;
            this.writer = XmlWriter.Create(FileName);
            this.writer.WriteStartDocument();
            this.writer.WriteStartElement("OWAS");
        }

        public Util()
        {
            this.FileName = "owas_classification.xml";
            this.writer = XmlWriter.Create(FileName);
            this.writer.WriteStartDocument();
            this.writer.WriteStartElement("OWAS");
        }

        public void addPosition(long timestamp, CostaPosicao costaPosicao, BracosPosicao bracoPosicao, PernasPosicao pernasPosicao)
        {
    
            writer.WriteStartElement("Frame");
            writer.WriteAttributeString(null, "timestamp", null, timestamp+"");

            writer.WriteAttributeString(null, "spine", null, costaPosicao + "");
            writer.WriteAttributeString(null, "arms", null, bracoPosicao + "");
            writer.WriteAttributeString(null, "legs", null, pernasPosicao + "");

            // writer.WriteElementString("timestamp", timestamp + "");
            
            //writer.WriteElementString("timestamp", TimeSpan.FromMilliseconds(timestamp).TotalSeconds+"");
            //writer.WriteElementString("Spine", (int)((CostaPosicao)Enum.Parse(typeof(CostaPosicao), costaPosicao + ""))+"");
            //writer.WriteElementString("Arms", (int)((BracosPosicao)Enum.Parse(typeof(BracosPosicao), bracoPosicao + "")) + "");
            //writer.WriteElementString("Legs", (int)((PernasPosicao)Enum.Parse(typeof(PernasPosicao), pernasPosicao + "")) + "");

            /*writer.WriteElementString("Spine", costaPosicao + "");
            writer.WriteElementString("Arms", bracoPosicao + "");
            writer.WriteElementString("Legs",  pernasPosicao + "");*/

            writer.WriteEndElement();
        }

        public void addPosition(long timestamp, Owas owas) {
            if (owas != null) {
                writer.WriteStartElement("Frame");
                writer.WriteAttributeString(null, "timestamp", null, timestamp + "");
                
                writer.WriteElementString("Spine", (int)((CostaPosicao)Enum.Parse(typeof(CostaPosicao), owas.BackPosition+"")) + "");
                writer.WriteElementString("Arms", (int)((BracosPosicao)Enum.Parse(typeof(BracosPosicao),owas.ArmsPosition + ""))+"");
                writer.WriteElementString("Legs", (int)((PernasPosicao)Enum.Parse(typeof(PernasPosicao),owas.LegsPosition + ""))+"");
                
                writer.WriteElementString("Weight", (int)((Weigth)Enum.Parse(typeof(Weigth), owas._Weight + "")) + "");
                
                writer.WriteEndElement();
            }
        }

        public bool save()
        {
            writer.WriteEndElement();
            writer.WriteEndDocument();
            writer.Flush();
            writer.Close();
            return false;
        }
    }
}
