package com.example.demo;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

class CouponMeal {
    private String price;
    private String code;
    private String food_img_url;

    public void CouponMeal() {
        price = "";
        code = "";
        food_img_url = "";
    }

    public void setupMeal(String url, String img_url) throws Exception {
        Document doc = Jsoup.connect(url).get();

        Element price = doc.selectFirst("span.small-price");
        this.price = price.text().substring(1);

        Element code = doc.selectFirst("h1.combo-flow__header-title.mealsTitle");
        this.code = code.text().replaceAll("[^0-9]", "");

        this.food_img_url = img_url;
    }

    public String toString() {
        String str = "[code: " + code + ", price: " + price + ", image url: " + food_img_url + "]\n";
        return str;
    }
}
class CouponMealLinkConstructor {
    // all coupons here
    public static final String KFC_COUPON_URL = "https://www.kfcclub.com.tw/Coupon";

    // to get images with their descriptions
    public static final String KFC_PARTIAL_COUPON_IMG_URL = "https://kfcoosfs.kfcclub.com.tw/";

    // it need fCode to direct to a specific meal
    public static final String KFC_PARTIAL_MEAL_URL = "https://www.kfcclub.com.tw/meal/";

    public static List<String> setup_meal_url() throws Exception {
        Document doc = Jsoup.connect(KFC_COUPON_URL).get();
        Elements scripts = doc.select("script");
        for (Element script : scripts) {
            String content = script.html().trim();

            int start_idx = content.indexOf("coupondata: [");
            if (start_idx != -1) {
                int end_idx = content.indexOf("]", start_idx) + 1;

                String jsonPart = content.substring(start_idx + "coupondata: ".length(), end_idx);

                Pattern p = Pattern
                        .compile("\"ImgNameNew\"\s*:\s*\"([^\"]+)\",[\\s\\n\\r]*\"Fcode\"\s*:\s*\"([^\"]+)\"");
                Matcher m = p.matcher(jsonPart);

                List<String> meals = new ArrayList<>();
                while (m.find()) {
                    String link = KFC_PARTIAL_MEAL_URL + m.group(2);
                    String img_url = KFC_PARTIAL_COUPON_IMG_URL + m.group(1);
                    meals.add(link + "," + img_url);
                }
                return meals;
            }
        }
        return null;
    }
}


public class Crawler_KFC {
    
}
