using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Build4Cloud
{
    class PersistenceXml : TemporarilyEditFile
    {
        public PersistenceXml(string rootPath, string pathToWeb)
            : base(Path.Combine(pathToWeb, "src", "main", "resources", "META-INF", "persistence.xml"))
        {
        }

        protected override void EditFile(int datasourcesCount)
        {
            Template template = Template.Load(PathToStashed);

            using (var cloud = File.CreateText(path))
            {
                template.Header.ForEach(l => cloud.WriteLine(l));
                template.Body.ForEach(l => cloud.WriteLine(l));

                for (int i = 1; i <= datasourcesCount; i++)
                {
                    cloud.WriteLine("");

                    foreach (string l in template.ReplaceBody("UK" + i))
                    {
                        cloud.WriteLine(l);
                    }
                }
                
                template.Footer.ForEach(l => cloud.WriteLine(l));
            }
        }
    }

    class Template
    {
        public List<string> Header { get; } = new List<string>();

        public List<string> Body { get; } = new List<string>();

        public List<string> Footer { get; } = new List<string>();

        public static Template Load(string path)
        {
            var origLines = File.ReadAllLines(path);
            var template = new Template();

            bool isHeader = true;
            bool isBody = false;
            bool isFooter = false;

            foreach (string line in origLines)
            {
                if (isHeader)
                {
                    if (line.Contains("<persistence-unit"))
                    {
                        isHeader = false;
                        isBody = true;
                    }
                    else
                    {
                        template.Header.Add(line);
                        continue;
                    }
                }

                if (isBody)
                {
                    template.Body.Add(line);

                    if (line.Contains("</persistence-unit>"))
                    {
                        isBody = false;
                        isFooter = true;
                    }

                    continue;
                }

                if (isFooter)
                {
                    template.Footer.Add(line);
                }
            }

            return template;
        }

        public IEnumerable<string> ReplaceBody(string unitName)
        {
            foreach (string line in Body)
            {
                if (line.Contains("<persistence-unit") || line.Contains("<jta-data-source>"))
                {
                    yield return line.Replace("UK", unitName);
                }
                else
                {
                    yield return line;
                }
            }
        }
    }
}
