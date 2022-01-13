using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Build4Cloud
{
    class Program
    {
        static void Main(string[] args)
        {
            int dataSourcesCount = 1;
            IEnumerable<string> targetProjects = new[] { "cloud" };
            string mode = "build";

            foreach (string arg in args)
            {
                string[] parts = arg.Split('=');
                string name = parts[0];
                string value = parts[1];

                switch (name)
                {
                    case "-d":
                        dataSourcesCount = int.Parse(value);
                        break;
                    case "-p":
                        targetProjects = value.Split(',');
                        break;
                    case "-m":
                        mode = value.ToLower();
                        break;
                }
            }

            var context = new Context
            {
                RootPath = Util.FindRootPathFromCurrent(),
                Projects = targetProjects,
            };

            var loader = new EntityManagerLoader(context.RootPath);
            loader.CreateCloudEdition(dataSourcesCount);

            foreach (var project in context.Projects)
            {
                string pathToWeb = Path.Combine(context.RootPath, $"uk.{project}", $"{project}.web", $"nts.uk.{project}.web");

                var xml = new PersistenceXml(context.RootPath, pathToWeb);
                xml.CreateCloudEdition(dataSourcesCount);

                if (mode == "build")
                {
                    Util.Gradle("build", pathToWeb);

                    xml.RestoreOriginalFile();
                }
            }

            if (mode == "build")
            {
                loader.RestoreOriginalFile();
            }
        }
    }

}

