using AspNetCoreExample.Models;
using AspNetCoreExample.Services;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Options;
using OpenTelemetry.Trace;

var builder = WebApplication.CreateBuilder(args);

builder.WebHost.UseUrls("http://+:5000");

builder.Services.Configure<ItemsDatabaseSettings>(
    builder.Configuration.GetSection(nameof(ItemsDatabaseSettings)));

builder.Services.AddSingleton<ItemsDatabaseSettings>(sp =>
    sp.GetRequiredService<IOptions<ItemsDatabaseSettings>>().Value);

builder.Services.AddSingleton<ItemService>();

builder.Services.AddControllers()
    .AddNewtonsoftJson(options => options.UseMemberCasing());

const string DefaultActivitySourceName = "AspNetCoreExample.*";
const string MongoDBDriverActivitySourceName = "MongoDB.Driver";

builder.Services.AddOpenTelemetry().WithTracing(tracing =>
{
    tracing
        .AddOtlpExporter()
        .AddAspNetCoreInstrumentation()
        .AddSource(MongoDBDriverActivitySourceName)
        .AddSource(DefaultActivitySourceName);
});

var app = builder.Build();

app.UseDeveloperExceptionPage();
app.MapControllers();

app.Run();
