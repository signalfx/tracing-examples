using AspNetCoreExample.Models;
using MongoDB.Driver;
using System.Collections.Generic;
using System.Linq;
using OpenTracing;
using OpenTracing.Util;

namespace AspNetCoreExample.Services
{
    public class ItemService
    {
        private readonly IMongoCollection<Item> _Items;

        private static ITracer tracer = GlobalTracer.Instance;

        public ItemService(ItemsDatabaseSettings settings)
        {
            using (var scope = tracer.BuildSpan("establish.ItemService").IgnoreActiveSpan().StartActive(finishSpanOnDispose: true))
            {
                var client = new MongoClient(settings.ConnectionString);
                var database = client.GetDatabase(settings.DatabaseName);
                _Items = database.GetCollection<Item>(settings.ItemsCollectionName);
            }
        }

        public List<Item> Get()
        {
            using (var scope = tracer.BuildSpan("GetItems").StartActive(finishSpanOnDispose: true))
            {
                return _Items.Find(Item => true).ToList();
            }
        }

        public Item Get(string id)
        {
            using (var scope = tracer.BuildSpan("GetItem").StartActive(finishSpanOnDispose: true))
            {
                scope.Span.SetTag("item.id", id);
                return _Items.Find<Item>(Item => Item.Id == id).FirstOrDefault();
            }
        }

        public Item Create(Item Item)
        {
            using (var scope = tracer.BuildSpan("CreateItem").StartActive(finishSpanOnDispose: true))
            {
                _Items.InsertOne(Item);
                scope.Span.SetTag("item.id", Item.Id);
                return Item;
            }
        }

        public void Update(string id, Item ItemIn)
        {
            using (var scope = tracer.BuildSpan("UpdateItem").StartActive(finishSpanOnDispose: true))
            {
                scope.Span.SetTag("item.id", id);
                _Items.ReplaceOne(Item => Item.Id == id, ItemIn);
            }

        }

        public void Remove(Item ItemIn)
        {
            using (var scope = tracer.BuildSpan("RemoveItem").StartActive(finishSpanOnDispose: true))
            {
                scope.Span.SetTag("item.id", ItemIn.Id);
                _Items.DeleteOne(Item => Item.Id == ItemIn.Id);
            }
        }

        public void Remove(string id)
        {
            using (var scope = tracer.BuildSpan("RemoveItem").StartActive(finishSpanOnDispose: true))
            {
                scope.Span.SetTag("item.id", id);
                _Items.DeleteOne(Item => Item.Id == id);
            }
        }
    }
}
