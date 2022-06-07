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

using OpenTelemetry.Context.Propagation;
using OpenTelemetry.Shims.OpenTracing;
using OpenTelemetry.Trace;
using OpenTracing;

namespace AspNetCoreExample
{
    public class Startup
    {
        private const string defaultActivitySourceName = "AspNetCoreExample";

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
                    .AddMongoDBInstrumentation()
                    .AddSource(defaultActivitySourceName);
            });

            // Setup the OpenTracing shim. This must happen after the tracer is built, so perform the 
            // shim setup as a singleton service so it happens after the OpenTelemetry tracer is built
            // by the AddOpenTelemetryTracing call above.
            services.AddSingleton(s =>
            {
                // Instantiate the OpenTracing shim. The underlying OpenTelemetry tracer will create
                // spans using the defaultActivitySourceName source.
                var openTracingTracer = new TracerShim(
                    TracerProvider.Default.GetTracer(defaultActivitySourceName),
                    Propagators.DefaultTextMapPropagator);

                // The registration of a global tracer in OpenTracing must happen before any use of
                // GlobalTracer.Instance. Otherwise, a no-op tracer is going to be register for the
                // respective load context. OpenTracing only allow the registration of a single global
                // tracer per load context.
                OpenTracing.Util.GlobalTracer.Register(openTracingTracer);

                return s;
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
