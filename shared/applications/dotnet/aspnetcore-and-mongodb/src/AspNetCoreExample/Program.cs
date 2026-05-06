using AspNetCoreExample.Models;
using AspNetCoreExample.Services;
using Microsoft.Extensions.Options;

var builder = WebApplication.CreateBuilder(args);

builder.WebHost.UseUrls("http://+:5000");

builder.Services.Configure<ItemsDatabaseSettings>(
    builder.Configuration.GetSection(nameof(ItemsDatabaseSettings)));

builder.Services.AddSingleton<ItemsDatabaseSettings>(sp =>
    sp.GetRequiredService<IOptions<ItemsDatabaseSettings>>().Value);

builder.Services.AddSingleton<ItemService>();

builder.Services.AddControllers()
    .AddNewtonsoftJson(options => options.UseMemberCasing());

var app = builder.Build();

app.UseDeveloperExceptionPage();
app.MapControllers();

app.Run();
