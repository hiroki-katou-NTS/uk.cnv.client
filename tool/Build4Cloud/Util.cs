using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Build4Cloud
{
    class Util
    {
        public static string FindRootPathFromCurrent()
        {
            for (string dir = Environment.CurrentDirectory; Directory.Exists(dir); dir = Path.Combine(dir, ".."))
            {
                string root = Path.Combine(dir, "nts.uk");
                if (Directory.Exists(root))
                {
                    return root;
                }
            }

            throw new Exception("root not found: " + Environment.CurrentDirectory);
        }

        public static void Gradle(string task, string currentDirectory)
        {
            var processStartInfo = new ProcessStartInfo("gradle", task);
            processStartInfo.WorkingDirectory = currentDirectory;

            var process = Process.Start(processStartInfo);
            process.WaitForExit();
            process.Close();
        }
    }
}
