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
using NLog.Web;

var builder = WebApplication.CreateBuilder(args);

// NLog is used as a logging provider.
// Logging is done using ILogger interface.
// By default, logs emitted by the application
// will be enriched with trace context and
// exported to local instance of OpenTelemetry collector.
// In order to inject trace context into existing log
// destination, i.e Nlog target, NLog extension
// that provides additional renderer is added, and output template adjusted.
// Logging is configured with xml in nlog.config

builder.Host.UseNLog(); // add NLog as a logging provider
builder.Services.AddHealthChecks();

var app = builder.Build();

app.MapHealthChecks("/healthz");

app.MapGet("/", (ILogger<Program> logger) =>
{
    logger.LogInformation("Request received.");
    return "Hello World!";
});

app.Run();
