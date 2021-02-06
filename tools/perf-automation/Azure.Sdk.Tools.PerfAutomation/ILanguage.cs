﻿using Azure.Sdk.Tools.PerfAutomation.Models;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Azure.Sdk.Tools.PerfAutomation
{
    public interface ILanguage
    {
        Task<(string output, string error, string context)> SetupAsync(string project, IDictionary<string, string> packageVersions);
        Task<IterationResult> RunAsync(string project, string testName, string arguments, string context);
        Task CleanupAsync(string project);
    }
}
