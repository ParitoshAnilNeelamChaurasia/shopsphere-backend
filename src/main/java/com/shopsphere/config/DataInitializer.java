package com.shopsphere.config;

import com.shopsphere.product.Product;
import com.shopsphere.product.ProductRepository;
import com.shopsphere.user.User;
import com.shopsphere.user.UserRepository;
import com.shopsphere.user.UserRole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seed(UserRepository users, ProductRepository products, PasswordEncoder encoder) {
        return args -> {
            users.findByEmailIgnoreCase("admin@shopsphere.com").orElseGet(() -> users.save(new User("ShopSphere Admin","admin@shopsphere.com",encoder.encode("admin123"), UserRole.ADMIN)));
            users.findByEmailIgnoreCase("demo@shopsphere.com").orElseGet(() -> users.save(new User("Demo Customer","demo@shopsphere.com",encoder.encode("demo123"), UserRole.CUSTOMER)));
            if (products.count() == 0) seedProducts(products);
        };
    }

    private void seedProducts(ProductRepository r) {
        r.save(product(101,"AeroBook Pro 14","Aero","Electronics",74999,82999,4.8,1248,18,"https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=900&q=80","A premium 14-inch laptop built for developers, creators and everyday productivity with a crisp display and all-day battery life.","Best Seller",Map.of("Display","14-inch 2.8K OLED","Processor","Intel Core Ultra 7","Memory","16 GB","Storage","1 TB SSD","Weight","1.35 kg")));
        r.save(product(102,"Pulse X1 Smartwatch","Pulse","Electronics",4999,6999,4.5,892,34,"https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=900&q=80","A sleek smartwatch with AMOLED display, activity tracking, notifications and long battery life.","20% OFF",Map.of("Display","1.78-inch AMOLED","Battery","Up to 10 days","WaterResistance","5 ATM","Connectivity","Bluetooth 5.3")));
        r.save(product(103,"SoundMax Air Pro","SoundMax","Electronics",6999,8999,4.7,1564,27,"https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=900&q=80","Wireless over-ear headphones with adaptive noise cancellation and rich spatial sound.","Trending",Map.of("Driver","40 mm","Battery","45 hours","Connectivity","Bluetooth 5.4","Charging","USB-C fast charge")));
        r.save(product(104,"CoreFit Training Shoes","CoreFit","Fashion",3199,4499,4.4,513,41,"https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=900&q=80","Lightweight training shoes with a stable heel and responsive cushioning for daily workouts.","Popular",Map.of("Upper","Breathable mesh","Sole","EVA + rubber","Closure","Lace-up","Use","Training / Running")));
        r.save(product(105,"Urban Overshirt","Northline","Fashion",1899,2599,4.3,312,16,"https://images.unsplash.com/photo-1529139574466-a303027c1d8b?auto=format&fit=crop&w=900&q=80","A structured overshirt designed for layered everyday outfits with a modern relaxed fit.","New",Map.of("Material","Cotton blend","Fit","Relaxed","Pattern","Solid","Care","Machine wash")));
        r.save(product(106,"Aura Table Lamp","Luma","Home",2299,2999,4.6,228,22,"https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=900&q=80","Warm ambient lighting with a clean silhouette that works beautifully on desks and bedside tables.",null,Map.of("Material","Aluminium + glass","Power","12 W LED","ColorTemperature","2700 K","Warranty","1 year")));
        r.save(product(107,"Nova Gaming Chair","Nova","Gaming",10499,13999,4.7,674,11,"https://images.unsplash.com/photo-1593305841991-05c297ba4575?auto=format&fit=crop&w=900&q=80","Ergonomic gaming chair with adjustable lumbar support and a high-back reclining frame.","Staff Pick",Map.of("Recline","90–135°","Frame","Steel","Cushion","High-density foam","WeightCapacity","120 kg")));
        r.save(product(108,"KeyForge Mechanical Keyboard","KeyForge","Gaming",5799,6499,4.8,941,29,"https://images.unsplash.com/photo-1587829741301-dc798b83add3?auto=format&fit=crop&w=900&q=80","Hot-swappable mechanical keyboard with tactile switches, compact layout and customizable backlight.","Hot",Map.of("Layout","75%","Switches","Tactile","Connectivity","USB-C / Wireless","Keycaps","Double-shot PBT")));
        r.save(product(109,"PureGlow Skincare Set","PureGlow","Beauty",1499,1899,4.5,388,48,"https://images.unsplash.com/photo-1556228578-8c89e6adf883?auto=format&fit=crop&w=900&q=80","A simple daily skincare kit featuring cleanser, moisturizer and SPF essentials.",null,Map.of("Pieces","3","SkinType","Combination / Normal","Fragrance","Light","Vegan","Yes")));
        r.save(product(110,"The Systems Design Handbook","TechPress","Books",999,1299,4.9,1221,63,"https://images.unsplash.com/photo-1532012197267-da84d127e765?auto=format&fit=crop&w=900&q=80","An approachable guide to designing reliable services, APIs, databases and distributed systems.","Top Rated",Map.of("Pages","412","Language","English","Format","Paperback","Edition","2nd")));
        r.save(product(111,"PixelView 4K Monitor","PixelView","Electronics",29999,34999,4.6,407,9,"https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&w=900&q=80","4K UHD monitor with accurate colors and a clean minimal bezel for work and entertainment.",null,Map.of("Resolution","3840 × 2160","RefreshRate","144 Hz","Panel","IPS","Ports","HDMI / DisplayPort / USB-C")));
        r.save(product(112,"BrewMate Coffee Maker","BrewMate","Home",4299,5499,4.4,249,14,"https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?auto=format&fit=crop&w=900&q=80","Compact coffee maker for consistent morning brews with a simple touch control interface.","Deal",Map.of("Capacity","1.2 L","Power","950 W","Controls","Touch buttons","Warranty","2 years")));
    }

    private Product product(long id, String name, String brand, String category, double price, double originalPrice, double rating, int reviews, int stock, String image, String desc, String badge, Map<String,String> specs) {
        Product p=new Product();
//        p.setId(id);
        p.setName(name);
        p.setSlug(name.toLowerCase().replaceAll("[^a-z0-9]+","-").replaceAll("^-|-$",""));
        p.setBrand(brand);
        p.setCategory(category);
        p.setPrice(BigDecimal.valueOf(price));
        p.setOriginalPrice(BigDecimal.valueOf(originalPrice));
        p.setRating(rating);
        p.setReviews(reviews);
        p.setStock(stock);
        p.setImage(image);
        p.setImages(List.of(image));
        p.setDescription(desc);
        p.setBadge(badge);
        p.setSpecifications(specs);
        return p;
    }
}
