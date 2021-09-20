import Foundation

struct Product {
    var id: String;
    var name: String;
    var price: Double;
    var image: URL;
    var description: String;
}

extension Product {
    static var testData = [
        Product(id: "OLJCESPC7Z", name: "Vintage Typewriter", price: 67.98, image: URL(string: "http://ssidhu.o11ystore.com/static/img/products/typewriter.jpg")!, description: "This typewriter looks good in your living room."),
        Product(id: "66VCHSJNUP", name: "Vintage Camera Lens", price: 12.48, image: URL(string: "http://ssidhu.o11ystore.com/static/img/products/camera-lens.jpg")!, description: "You won't have a camera to use it and it probably doesn't work anyway."),
        Product(id: "1YMWWN1N4O", name: "Home Barista Kit", price: 123.99, image: URL(string: "http://ssidhu.o11ystore.com/static/img/products/barista-kit.jpg")!, description: "Always wanted to brew coffee with Chemex and Aeropress at home?"),
        Product(id: "L9ECAV7KIM", name: "Terrarium", price: 36.44, image: URL(string: "http://ssidhu.o11ystore.com/static/img/products/terrarium.jpg")!, description: "This terrarium will looks great in your white painted living room."),
        Product(id: "2ZYFJ3GM2N", name: "Film Camera", price: 2244.99, image: URL(string: "http://ssidhu.o11ystore.com/static/img/products/film-camera.jpg")!, description: "This camera looks like it's a film camera, but it's actually digital."),
        Product(id: "0PUK6V6EV0", name: "Vintage Record Player", price: 65.50, image: URL(string: "http://ssidhu.o11ystore.com/static/img/products/record-player.jpg")!, description: "It still works."),
        Product(id: "LS4PSXUNUM", name: "Metal Camping Mug", price: 24.33, image: URL(string: "http://ssidhu.o11ystore.com/static/img/products/camp-mug.jpg")!, description: "You probably don't go camping that often but this is better than plastic cups."),
        Product(id: "9SIQT8TOJO", name: "City Bike", price: 789.50, image: URL(string: "http://ssidhu.o11ystore.com/static/img/products/city-bike.jpg")!, description: "This single gear bike probably cannot climb the hills of San Francisco."),
        Product(id: "6E92ZMYYFZ", name: "Air Plant", price: 12.29, image: URL(string: "http://ssidhu.o11ystore.com/static/img/products/air-plant.jpg")!, description: "Have you ever wondered whether air plants need water? Buy one and figure out.")
    ]
}
