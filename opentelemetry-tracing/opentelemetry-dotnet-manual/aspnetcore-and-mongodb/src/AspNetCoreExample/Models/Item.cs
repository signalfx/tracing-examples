using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;
using Newtonsoft.Json;

namespace AspNetCoreExample.Models
{
    public class Item
    {
        [BsonId]
        [BsonRepresentation(BsonType.ObjectId)]
        public string Id { get; set; }

        public string Name { get; set; }

        public string Description { get; set; }

        [BsonElement("Maker")]
        [JsonProperty("Maker")]
        public string Manufacturer { get; set; }

        public decimal Price { get; set; }

        public string Url { get; set; }
    }
}
