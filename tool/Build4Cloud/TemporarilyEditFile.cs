using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Build4Cloud
{
    abstract class TemporarilyEditFile
    {
        protected readonly string path;

        public TemporarilyEditFile(string path)
        {
            this.path = path;
        }

        public void CreateCloudEdition(int datasourcesCount)
        {
            StashOriginalFile();
            EditFile(datasourcesCount);
        }

        protected abstract void EditFile(int datasourcesCount);

        private void StashOriginalFile()
        {
            // 何かの理由で残っていたら削除
            File.Delete(PathToStashed);

            File.Move(path, PathToStashed);
        }

        public void RestoreOriginalFile()
        {
            File.Delete(path);
            File.Move(PathToStashed, path);
        }

        protected string PathToStashed
        {
            get { return path + ".orig"; }
        }
    }
}
