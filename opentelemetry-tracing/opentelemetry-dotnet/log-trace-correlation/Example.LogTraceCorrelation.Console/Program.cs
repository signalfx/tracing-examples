// <copyright file="Program.cs" company="Splunk Inc.">
// Copyright Splunk Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// </copyright>

using System.Diagnostics;
using Microsoft.Extensions.Logging;
using Serilog;
using Serilog.Enrichers.Span;

namespace Example.LogTraceCorrelation.Console;

internal class Program
{
    private static readonly ActivitySource ActivitySource = new("Example.LogTraceCorrelation.Console");

    public static async Task<int> Main(string[] args)
    {
        if (args.Length != 1)
        {
            return 1;
        }

        // Serilog is used as a logging provider.
        // Logging is done using ILogger interface.
        // By default, logs emitted by the application
        // will be enriched with trace context and
        // exported to local instance of OpenTelemetry collector.
        // In order to inject trace context into existing log
        // destination, i.e file Serilog writes into, additional
        // enricher is added, and output template adjusted.
        Log.Logger = new LoggerConfiguration()
            // enrich logs with span context
            .Enrich.WithSpan(new SpanOptions { IncludeTraceFlags = true })
            .WriteTo.File(
                "logs/client.log",
                outputTemplate: "[{Timestamp:HH:mm:ss} {Level:u3}] {Message:lj}{Exception}|TraceId={TraceId}|SpanId={SpanId}|TraceFlags={TraceFlags}{NewLine}")
            .CreateLogger();

        using var loggerFactory = LoggerFactory.Create(
            builder => builder.AddSerilog()); // add serilog as a logging provider

        var logger = loggerFactory.CreateLogger<Program>();

        using var httpClient = new HttpClient();
        var serverAddress = args[0];

        while (true)
        {
            using (var activity = ActivitySource.StartActivity("LogWrappingActivity"))
            {
                try
                {
                    var content = await httpClient.GetStringAsync(serverAddress);
                    logger.LogInformation("Request finished.");
                }
                catch (Exception e)
                {
                    logger.LogError(e, "Request failed.");
                }
            }

            await Task.Delay(TimeSpan.FromSeconds(5));
        }
    }
}
