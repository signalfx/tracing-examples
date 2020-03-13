using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;
using OpenTracing;
using OpenTracing.Util;

namespace ClientExample 
{
    public class Program
    {
        private static string exampleUrl = "http://aspnetcore:5000/api/items";

        private static ITracer tracer = GlobalTracer.Instance;

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
            await CreateItems(itemsToUse);
            await FetchItems();
            await UpdateItems();
            await DeleteItems();
        }

        public static async Task CreateItems(List<Item> items)
        {
            using (var scope = tracer.BuildSpan("CreateItems").StartActive(finishSpanOnDispose: true))
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
            using (var scope = tracer.BuildSpan("FetchItems").StartActive(finishSpanOnDispose: true))
            {
                foreach (var item in itemStore)
                {
                    await FetchItem(item.Value);
                }
            }
        }

        public static async Task UpdateItems()
        {
            using (var scope = tracer.BuildSpan("UpdateItems").StartActive(finishSpanOnDispose: true))
            {
                foreach (var item in itemStore)
                {
                   var updated = await UpdateItem(item.Value);
                   scope.Span.Log(updated ? $"Updated {item.Key}." : $"Failed to update {item.Key}.");
                }
            }
        }

        public static async Task DeleteItems()
        {
            using (var scope = tracer.BuildSpan("DeleteItems").StartActive(finishSpanOnDispose: true))
            {
                foreach (var item in itemStore)
                {
                   var deleted = await DeleteItem(item.Value);
                   if (deleted)
                   {
                       itemStore.Remove(item.Key);
                   }

                   scope.Span.Log(deleted ? $"Deleted {item.Key}." : $"Failed to delete {item.Key}.");
                }
            }
        }

        public static async Task<Item> CreateItem(Item item)
        {
            Console.WriteLine($"Creating {item.Name}: {item.Maker} - {item.Description}");
            using (var scope = tracer.BuildSpan("CreateItem").StartActive(finishSpanOnDispose: true))
            {
                scope.Span.SetTag("item.name", item.Name);
                scope.Span.SetTag("item.description", item.Description);
                scope.Span.SetTag("item.maker", item.Maker);
                scope.Span.SetTag("item.price", $"${item.Price}");
                scope.Span.SetTag("item.url", item.Url);

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
            using (var scope = tracer.BuildSpan("FetchItem").StartActive(finishSpanOnDispose: true))
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
                scope.Span.SetTag("item.name", fetchedItem.Name);
                scope.Span.SetTag("item.description", fetchedItem.Description);
                scope.Span.SetTag("item.maker", fetchedItem.Maker);
                scope.Span.SetTag("item.price", $"${fetchedItem.Price}");
                scope.Span.SetTag("item.url", fetchedItem.Url);
                return fetchedItem;
            }
        }

        public static async Task<bool> UpdateItem(Item item)
        {
            Console.WriteLine($"Updating {item.Name}: {item.Maker} - {item.Description}");
            using (var scope = tracer.BuildSpan("UpdateItem").StartActive(finishSpanOnDispose: true))
            {
                var updatedDescription = $"updated: {item.Description}";
                item.Description = updatedDescription;
                scope.Span.Log($"Updating item.description to {updatedDescription}");

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
            using (var scope = tracer.BuildSpan("DeleteItem").StartActive(finishSpanOnDispose: true))
            {
                scope.Span.SetTag("item.name", item.Name);

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
