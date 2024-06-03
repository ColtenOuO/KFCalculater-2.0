package com.example.demo;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/DataBase")
public class KFC_DataBase {

    private DataBase db;
    @GetMapping("/images")
        public ResponseEntity<Object> getImages(@RequestParam(required = false) String mealId) {
        MongoClient mongoClient = MongoClients.create("mongodb://127.0.0.1:27017/");
        MongoDatabase database = mongoClient.getDatabase("KFC-Data");
        MongoCollection<Document> collection = database.getCollection("Meals");
        List<String> imageUrls = new ArrayList<>();
        FindIterable<Document> documents;
        if (mealId != null && !mealId.isEmpty()) {
            documents = collection.find(new Document("meal_id", mealId));
        } else {
            documents = collection.find();
        }

        for (Document document : documents) {
            String url = document.getString("meal_url");
            if (url != null) {
                imageUrls.add(url);
            }
        }

        mongoClient.close();
        return ResponseEntity.ok(imageUrls);
    }
    

    @GetMapping("/get")
    public String UpdateDataBase() throws Exception {
        List<String> meal_urls_and_imgs = CouponMealLinkConstructor.setup_meal_url();
        List<CouponMeal> meals = new ArrayList<>();
        for (String meal_url_img : meal_urls_and_imgs) {
            // produce all coupon Meal instances
            CouponMeal meal = new CouponMeal();
            String[] str = meal_url_img.split(",");
            meal.setupMeal(str[0], str[1]);
            meals.add(meal);
        }

        db = new DataBase("mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+2.2.6", "KFC-Data", "Meals");
        ArrayList<String> contents = new ArrayList<>();
        for (CouponMeal meal : meals) {
            meal.price = meal.price.replaceAll(",","");
            Meal_Data test = new Meal_Data(meal.code, contents, meal.food_img_url, Integer.parseInt(meal.price) );
            db.InsertMeals(test);
            System.out.println(meal);
        }

        return "crawler";
    }
    

    class CouponMeal {
        private String price;
        private String code;
        private String food_img_url;

        public CouponMeal() {
            price = "";
            code = "";
            food_img_url = "";
        }

        public void setupMeal(String url, String img_url) throws Exception {
            org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
            org.jsoup.nodes.Element priceElement = doc.selectFirst("span.small-price");
            if (priceElement != null) {
                this.price = priceElement.text().substring(1);
            }

            org.jsoup.nodes.Element codeElement = doc.selectFirst("h1.combo-flow__header-title.mealsTitle");
            if (codeElement != null) {
                this.code = codeElement.text().replaceAll("[^0-9]", "");
            }

            this.food_img_url = img_url;
        }

        public String toString() {
            return String.format("[code: %s, price: %s, image url: %s]\n", code, price, food_img_url);
        }
    }

    class CouponMealLinkConstructor {
        public static final String KFC_COUPON_URL = "https://www.kfcclub.com.tw/Coupon";
        public static final String KFC_PARTIAL_COUPON_IMG_URL = "https://kfcoosfs.kfcclub.com.tw/";
        public static final String KFC_PARTIAL_MEAL_URL = "https://www.kfcclub.com.tw/meal/";

        public static List<String> setup_meal_url() throws Exception {
            org.jsoup.nodes.Document doc = Jsoup.connect(KFC_COUPON_URL).timeout(10000000).get();
            Elements scripts = doc.select("script");
            List<String> meals = new ArrayList<>();

            for (org.jsoup.nodes.Element script : scripts) {
                String content = script.html().trim();
                int start_idx = content.indexOf("coupondata: [");
                if (start_idx != -1) {
                    int end_idx = content.indexOf("]", start_idx) + 1;
                    String jsonPart = content.substring(start_idx + "coupondata: ".length(), end_idx);
                    Pattern p = Pattern.compile("\"ImgNameNew\"\\s*:\\s*\"([^\"]+)\",\\s*\"Fcode\"\\s*:\\s*\"([^\"]+)\"");
                    Matcher m = p.matcher(jsonPart);

                    while (m.find()) {
                        String link = KFC_PARTIAL_MEAL_URL + m.group(2);
                        String img_url = KFC_PARTIAL_COUPON_IMG_URL + m.group(1);
                        meals.add(link + "," + img_url);
                    }
                }
            }
            return meals;
        }
    }

    public static class Meal_Data {
        private String meal_id;
        private ArrayList<String> meal_content;
        private String meal_url;
        private int price;

        Meal_Data(String meal_id, ArrayList<String> meal_content, String meal_url, int price) {
            this.meal_id = meal_id;
            this.meal_content = meal_content;
            this.meal_url = meal_url;
            this.price = price;
        }

        public String getID() { return this.meal_id; }
        public ArrayList<String> getContent() { return this.meal_content; }
        public String getURL() { return this.meal_url; }
        public int getPrice() { return this.price; }
    }

    public static class DataBase {
        private MongoClient mongoClient;
        private MongoDatabase database;
        private MongoCollection<Document> collection;

        DataBase(String uri, String dbName, String collectionName) {
            this.mongoClient = MongoClients.create(uri);
            this.database = mongoClient.getDatabase(dbName);
            this.collection = database.getCollection(collectionName);
        }

        public void InsertMeals(Meal_Data target) {
            Document doc = new Document("meal_id", target.getID())
                               .append("content", target.getContent())
                               .append("meal_url", target.getURL());
            this.collection.insertOne(doc);
        }
    }
}
