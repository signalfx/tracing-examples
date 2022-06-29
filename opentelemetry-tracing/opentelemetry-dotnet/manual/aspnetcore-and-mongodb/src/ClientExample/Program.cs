using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;
using OpenTelemetry;
using OpenTelemetry.Trace;

namespace ClientExample 
{
    public class Program
    {
        private static string exampleUrl = "http://aspnetcore:5000/api/items";
        private static string clientActivitySourceName = "ClientExample";

        private static ActivitySource activitySource = new ActivitySource(clientActivitySourceName, "1.0.0");

        private static Dictionary<string, Item> itemStore = new Dictionary<string, Item>();

        private static List<Item> itemsToUse = new List<Item>()
            { 
                new Item()
                    {
                        Name = "Heatron 10000",
                        Description = "Ceramic space heater.",
                        Maker = "DL",
                        Price = 100.00m,
                        Url = "http://example.com/heatron/10000"
                    },
                new Item()
                    {
                        Name = "Wool Blanket",
                        Description = "Navajo inspired.",
                        Maker = "PT",
                        Price = 300.00m,
                        Url = "http://example.com/wool/blanket"
                    },
                new Item()
                    {
                        Name = "Slippers",
                        Description = "Handmade from wool and leather.",
                        Maker = "FJ",
                        Price = 20.00m,
                        Url = "http://example.com/slippers"
                    },
                new Item()
                    {
                        Name = "Clay Mug",
                        Description = "Large drinking vessel.",
                        Maker = "FM",
                        Price = 7.00m,
                        Url = "http://example.com/clay/mug"
                    },
            };

        public static async Task Main(string[] args)
        {
            using var tracerProvider = Sdk.CreateTracerProviderBuilder()
                .AddOtlpExporter()
                .AddHttpClientInstrumentation()
                .AddSource(clientActivitySourceName)
                .Build();

            await CreateItems(itemsToUse);
            await FetchItems();
            await UpdateItems();
            await DeleteItems();
        }

        public static async Task CreateItems(List<Item> items)
        {
            using (activitySource.StartActivity("CreateItems", ActivityKind.Client))
            {
                foreach (var item in items)
                {
                    var created = await CreateItem(item);
                    itemStore[created.Id] = created;
                }
            }
        }

        public static async Task FetchItems()
        {
            using (activitySource.StartActivity("FetchItems", ActivityKind.Client))
            {
                foreach (var item in itemStore)
                {
                    await FetchItem(item.Value);
                }
            }
        }

        public static async Task UpdateItems()
        {
            using (var activity = activitySource.StartActivity("UpdateItems", ActivityKind.Client))
            {
                foreach (var item in itemStore)
                {
                   var updated = await UpdateItem(item.Value);
                   if (activity != null)
                   {
                       activity.AddEvent(new ActivityEvent(updated ? $"Updated {item.Key}." : $"Failed to update {item.Key}."));
                   }
                }
            }
        }

        public static async Task DeleteItems()
        {
            using (var activity = activitySource.StartActivity("DeleteItems", ActivityKind.Client))
            {
                foreach (var item in itemStore)
                {
                   var deleted = await DeleteItem(item.Value);
                   if (deleted)
                   {
                       itemStore.Remove(item.Key);
                   }

                    if (activity != null)
                    {
                        activity.AddEvent(new ActivityEvent(deleted ? $"Deleted {item.Key}." : $"Failed to delete {item.Key}."));
                    }
                }
            }
        }

        public static async Task<Item> CreateItem(Item item)
        {
            Console.WriteLine($"Creating {item.Name}: {item.Maker} - {item.Description}");
            using (var activity = activitySource.StartActivity("CreateItems", ActivityKind.Client))
            {
                if (activity != null)
                {
                    activity.SetTag("item.name", item.Name);
                    activity.SetTag("item.description", item.Description);
                    activity.SetTag("item.maker", item.Maker);
                    activity.SetTag("item.price", $"${item.Price}");
                    activity.SetTag("item.url", item.Url);
                }

                var jsonObject = JsonConvert.SerializeObject(item);

                using (var client = new HttpClient())
                {
                    var content = new StringContent(jsonObject.ToString(), Encoding.UTF8, "application/json");

                    using (var responseMessage = await client.PostAsync($"{exampleUrl}", content))
                    {
                        var responseContent = await responseMessage.Content.ReadAsStringAsync();
                        var responseStatus = responseMessage.StatusCode;
                        Console.WriteLine($"CreateItem response - {responseStatus}: {responseContent}");
                        return JsonConvert.DeserializeObject<Item>(responseContent);
                    }
                }
            }
        }

        public static async Task<Item> FetchItem(Item item)
        {
            Console.WriteLine($"Fetching {item.Id}");
            Item fetchedItem = null;
            using (var activity = activitySource.StartActivity("FetchItem", ActivityKind.Client))
            {
                using (var client = new HttpClient())
                {
                    using (var responseMessage = await client.GetAsync($"{exampleUrl}/{item.Id}"))
                    {
                        var responseContent = await responseMessage.Content.ReadAsStringAsync();
                        var responseStatus = responseMessage.StatusCode;
                        Console.WriteLine($"FetchItem response - {responseStatus}: {responseContent}");
                        fetchedItem = JsonConvert.DeserializeObject<Item>(responseContent);
                    }
                }

                if (activity != null)
                {
                    activity.SetTag("item.name", fetchedItem.Name);
                    activity.SetTag("item.description", fetchedItem.Description);
                    activity.SetTag("item.maker", fetchedItem.Maker);
                    activity.SetTag("item.price", $"${fetchedItem.Price}");
                    activity.SetTag("item.url", fetchedItem.Url);
                }

                return fetchedItem;
            }
        }

        public static async Task<bool> UpdateItem(Item item)
        {
            Console.WriteLine($"Updating {item.Name}: {item.Maker} - {item.Description}");
            using (var activity = activitySource.StartActivity("UpdateItem", ActivityKind.Client))
            {
                var updatedDescription = $"updated: {item.Description}";
                item.Description = updatedDescription;
                if (activity != null)
                {
                    activity?.AddEvent(new ActivityEvent($"Updating item.description to {updatedDescription}"));
                }

                var jsonObject = JsonConvert.SerializeObject(item);

                using (var client = new HttpClient())
                {
                    var content = new StringContent(jsonObject.ToString(), Encoding.UTF8, "application/json");

                    using (var responseMessage = await client.PutAsync($"{exampleUrl}/{item.Id}", content))
                    {
                        var responseContent = await responseMessage.Content.ReadAsStringAsync();
                        var responseStatus = responseMessage.StatusCode;
                        Console.WriteLine($"UpdateItem response - {responseStatus}: {responseContent}");
                        return responseStatus == HttpStatusCode.NoContent;
                    }
                }
            }
        }

        public static async Task<bool> DeleteItem(Item item)
        {
            Console.WriteLine($"Deleting {item.Name}: {item.Maker} - {item.Description}");
            using (var activity = activitySource.StartActivity("DeleteItem", ActivityKind.Client))
            {
                activity?.SetTag("item.name", item.Name);

                using (var client = new HttpClient())
                {
                    using (var responseMessage = await client.DeleteAsync($"{exampleUrl}/{item.Id}"))
                    {
                        var responseContent = await responseMessage.Content.ReadAsStringAsync();
                        var responseStatus = responseMessage.StatusCode;
                        Console.WriteLine($"DeleteItem response - {responseStatus}: {responseContent}");
                        return responseStatus == HttpStatusCode.NoContent;
                    }
                }
            }
        }
    }

    public class Item
    {
        public string Id { get; set; }
        public string Name { get; set; }
        public string Description { get; set; }
        public string Maker { get; set; }
        public decimal Price { get; set; }
        public string Url { get; set; }
    }
}
