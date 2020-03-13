using AspNetCoreExample.Models;
using AspNetCoreExample.Services;
using Microsoft.AspNetCore.Mvc;
using System.Collections.Generic;

namespace AspNetCoreExample.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ItemsController : ControllerBase
    {
        private readonly ItemService itemService;

        public ItemsController(ItemService ItemService)
        {
            itemService = ItemService;
        }

        [HttpGet]
        public ActionResult<List<Item>> Get()
        {
            return itemService.Get();
        }

        [HttpGet("{id:length(24)}", Name = "GetItem")]
        public ActionResult<Item> Get(string id)
        {
            var Item = itemService.Get(id);
            if (Item == null)
            {
                return NotFound();
            }

            return Item;
        }

        [HttpPost]
        public ActionResult<Item> Create(Item Item)
        {
            itemService.Create(Item);
            return CreatedAtRoute("GetItem", new { id = Item.Id.ToString() }, Item);
        }

        [HttpPut("{id:length(24)}")]
        public IActionResult Update(string id, Item ItemIn)
        {
            var Item = itemService.Get(id);
            if (Item == null)
            {
                return NotFound();
            }

            itemService.Update(id, ItemIn);
            return NoContent();
        }

        [HttpDelete("{id:length(24)}")]
        public IActionResult Delete(string id)
        {
            var Item = itemService.Get(id);
            if (Item == null)
            {
                return NotFound();
            }

            itemService.Remove(Item.Id);
            return NoContent();
        }
    }
}
