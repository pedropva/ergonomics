using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace OWAS.OWAS
{
    class Relatorio
    {
        protected UtilReader OwasReader;
        
        public Relatorio(String FileName)
        {
            OwasReader = new UtilReader(FileName);
        }


    }
}
