using AspNetCoreExample.Models;
using AspNetCoreExample.Services;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.HttpsPolicy;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Options;

using OpenTelemetry.Trace;

namespace AspNetCoreExample
{
    public class Startup
    {
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
            services.AddOpenTelemetryTracing(builder =>
            {
                builder
                    .AddOtlpExporter()
                    .AddAspNetCoreInstrumentation()
                    .AddMongoDBInstrumentation();
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
