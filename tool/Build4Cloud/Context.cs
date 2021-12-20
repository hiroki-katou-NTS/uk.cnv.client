using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Build4Cloud
{
    class Context
    {
        public string RootPath { get; set; }

        public IEnumerable<string> Projects { get; set; }
    }
}
