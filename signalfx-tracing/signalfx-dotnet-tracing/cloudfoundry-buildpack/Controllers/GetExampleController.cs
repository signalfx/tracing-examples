using System;
using System.Net;
using System.Net.Http;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;

namespace CloudFoundryBuildpackExample.Controllers
{
    public class GetExampleController : Controller
    {
        private readonly ILogger<GetExampleController> _logger;

        public GetExampleController(ILogger<GetExampleController> logger)
        {
            _logger = logger;
        }

        [HttpGet("get/example")]
        public async Task<ContentResult> GetExample() {
            var stopwatch = Stopwatch.StartNew();

            using (var client = new HttpClient())
            {
                var response = await client.GetByteArrayAsync("http://www.example.com");
                Console.WriteLine("Response size: " + response.Length);
            }

            return Content("Done in " + stopwatch.ElapsedMilliseconds + "ms");
        }
    }
}
