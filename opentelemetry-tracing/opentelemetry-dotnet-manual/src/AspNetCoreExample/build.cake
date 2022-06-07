var target = Argument("target", "Default");
var tag = Argument("tag", "cake");
var runtime = Argument("runtime", "linux-x64");

Task("Restore")
  .Does(() =>
{
    DotNetCoreRestore(".");
});

Task("Build")
  .Does(() =>
{
    DotNetCoreBuild(".");
});

Task("Publish")
  .Does(() =>
{
    var settings = new DotNetCorePublishSettings
    {
        Configuration = "Release",
        OutputDirectory = "./publish",
        Runtime = runtime,
        VersionSuffix = tag
    };
                
    DotNetCorePublish(".", settings);
});

Task("Clean")
    .Does(() => {
        void RemoveDirectory(string d) 
        {
            if (DirectoryExists(d))
            {
                Information($"Cleaning {d}");
                CleanDirectory(d);
            }
        }

        RemoveDirectory("publish/");
        var directories = GetDirectories("**/obj").Concat(GetDirectories("**/bin"));
        foreach(var dir in directories)
        {
            RemoveDirectory(dir.ToString());
        }
    });

Task("Default")
    .IsDependentOn("Restore")
    .IsDependentOn("Build")
    .IsDependentOn("Publish");

 Task("Rebuild")
    .IsDependentOn("Restore")
    .IsDependentOn("Build");

RunTarget(target);
