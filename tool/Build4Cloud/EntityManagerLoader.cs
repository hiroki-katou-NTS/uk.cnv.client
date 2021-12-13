using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Build4Cloud
{
    class EntityManagerLoader : TemporarilyEditFile
    {
        private readonly string rootPath;

        public EntityManagerLoader(string rootPath)
            : base(Path.Combine(rootPath, @"uk.com\shr\nts.uk.shr.com\src\main\java\nts\uk\shr\infra\data\CloudEntityManagerLoader.java"))
        {
            this.rootPath = rootPath;
        }

        protected override void EditFile(int datasourcesCount)
        {
            var lines = File.ReadAllLines(PathToStashed);

            using (var writer = File.CreateText(path))
            {
                bool appended = false;

                foreach (string line in lines)
                {
                    writer.WriteLine(line);

                    if (appended)
                    {
                        continue;
                    }

                    if (line.Contains("private EntityManager entityManager;"))
                    {
                        AppendDataSources(writer, datasourcesCount);
                        appended = true;
                    }
                }
            }

            // com.web以外のビルドのためにupverが必要
            Util.Gradle("upver", Path.Combine(rootPath, @"uk.com\shr\nts.uk.shr.com"));
        }

        private void AppendDataSources(StreamWriter writer, int datasourcesCount)
        {
            for (int i = 1; i <= datasourcesCount; i++)
            {
                writer.WriteLine($"@PersistenceContext(unitName = \"UK{i}\")");
                writer.WriteLine($"private EntityManager entityManager{i};");
            }
        }
    }
}
