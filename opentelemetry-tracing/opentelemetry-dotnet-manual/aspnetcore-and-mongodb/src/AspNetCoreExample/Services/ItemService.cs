using AspNetCoreExample.Models;
using MongoDB.Driver;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using MongoDB.Driver.Core.Extensions.DiagnosticSources;

namespace AspNetCoreExample.Services
{
    public class ItemService
    {
        private readonly IMongoCollection<Item> _Items;

        private static ActivitySource activitySource = new ActivitySource("AspNetCoreExample.Services.ItemService", "1.0.0");

        public ItemService(ItemsDatabaseSettings settings)
        {
            using (activitySource.StartActivity("establish.ItemService", ActivityKind.Internal, parentContext: default))
            {
                // Subscribe to the Mongo activities for OpenTelemetry.
                var clientSettings = MongoClientSettings.FromConnectionString(settings.ConnectionString);
                clientSettings.ClusterConfigurator = cb => cb.Subscribe(new DiagnosticsActivityEventSubscriber());

                var client = new MongoClient(clientSettings);
                var database = client.GetDatabase(settings.DatabaseName);
                _Items = database.GetCollection<Item>(settings.ItemsCollectionName);
            }
        }

        public List<Item> Get()
        {
            using (activitySource.StartActivity("GetItems"))
            {
                return _Items.Find(Item => true).ToList();
            }
        }

        public Item Get(string id)
        {
            using (var activity = activitySource.StartActivity("GetItem"))
            {
                activity?.SetTag("item.id", id);
                return _Items.Find<Item>(Item => Item.Id == id).FirstOrDefault();
            }
        }

        public Item Create(Item Item)
        {
            using (var activity = activitySource.StartActivity("CreateItem"))
            {
                _Items.InsertOne(Item);
                activity?.SetTag("item.id", Item.Id);
                return Item;
            }
        }

        public void Update(string id, Item ItemIn)
        {
            using (var activity = activitySource.StartActivity("UpdateItem"))
            {
                activity?.SetTag("item.id", id);
                _Items.ReplaceOne(Item => Item.Id == id, ItemIn);
            }
        }

        public void Remove(Item ItemIn)
        {
            using (var activity = activitySource.StartActivity("RemoveItem"))
            {
                activity?.SetTag("item.id", ItemIn.Id);
                _Items.DeleteOne(Item => Item.Id == ItemIn.Id);
            }
        }

        public void Remove(string id)
        {
            using (var activity = activitySource.StartActivity("RemoveItem"))
            {
                activity?.SetTag("item.id", id);
                _Items.DeleteOne(Item => Item.Id == id);
            }
        }
    }
}
