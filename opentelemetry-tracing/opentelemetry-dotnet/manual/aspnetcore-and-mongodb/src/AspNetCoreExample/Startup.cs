using AspNetCoreExample.Models;
using AspNetCoreExample.Services;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Options;

using OpenTelemetry.Trace;

namespace AspNetCoreExample
{
    public class Startup
    {
        private const string DefaultActivitySourceName = "AspNetCoreExample.*";
        private const string MongoDBDriverActivitySourceName = "MongoDB.Driver.Core.Extensions.DiagnosticSources";

        public Startup(IConfiguration configuration)
        {
            Configuration = configuration;
        }

        public IConfiguration Configuration { get; }

        public void ConfigureServices(IServiceCollection services)
        {
            services.Configure<ItemsDatabaseSettings>(
                Configuration.GetSection(nameof(ItemsDatabaseSettings)));

            services.AddSingleton<ItemsDatabaseSettings>(sp =>
                sp.GetRequiredService<IOptions<ItemsDatabaseSettings>>().Value);

            services.AddSingleton<ItemService>();

            services.AddControllers()
                .AddNewtonsoftJson(options => options.UseMemberCasing());

            // Configure OpenTelemetry tracing
            services.AddOpenTelemetry().WithTracing(builder =>
            {
                builder
                    .AddOtlpExporter()
                    .AddAspNetCoreInstrumentation()
                    .AddSource(MongoDBDriverActivitySourceName)
                    .AddSource(DefaultActivitySourceName);
            });
        }

        public void Configure(IApplicationBuilder app, IWebHostEnvironment env)
        {
            app.UseDeveloperExceptionPage();
            app.UseRouting();
            app.UseEndpoints(endpoints =>
            {
                endpoints.MapControllers();
            });
        }
    }
}
