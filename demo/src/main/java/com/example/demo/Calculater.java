package com.example.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.KFC_DataBase.DataBase;
import com.mongodb.client.FindIterable;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Projections.*;

@Controller
@RequestMapping("/calculater")
public class Calculater {
    public class MealData {
        private int danta;
        private int chicken;
        private int price;
        MealData(int danta, int chicken, int price) {
            this.danta = danta;
            this.chicken = chicken;
            this.price = price;
        }
    }
    public class IndexData {
        private int first;
        private int second;
        IndexData(int first,int second) {
            this.first = first;
            this.second = second;
        }
    }
    public class KFCalculater extends DataBase  {
        List<String> imageUrls = new ArrayList<>();
        FindIterable<Document> documents = collection.find();
        private String req_meal;
        private int req_money;
        KFCalculater(String req_meal,int req_money) { 
            this.req_meal = req_meal;
            this.req_money = req_money; 
        }
        public List<String> getReqMealList() {
            List<String> target_url = new ArrayList<>();
            for (Document document : documents) {
                String meal_content = document.getString("content");
                System.out.println(req_meal);
                Boolean ok = true;
                for(int i=0;i<req_meal.length();i++) {
                    if( meal_content.contains(Character.toString(req_meal.charAt(i))) == false ) {
                        ok = false;
                        break;
                    }
                }

                if( ok == true ) { target_url.add(document.getString("meal_url")); }
            }

            return target_url;
        }
        public List<String> getReqMoneyList() {
            List<String> target_url = new ArrayList<>();
            for (Document document : documents) {
                int meal_price = document.getInteger("price");
                if( meal_price <= this.req_money ) { target_url.add(document.getString("meal_url")); }
            }
            return target_url;
        }
        public List<String> getReqDantaList() {
            List<String> target_url = new ArrayList<>();
            List<MealData> all_item = new ArrayList<>();
            List<String> all_url = new ArrayList<>();
            for (Document document : documents ) {
                String content = document.getString("content");
                int price = document.getInteger("price");
                for(int i=0;i<content.length();i++) {
                    if( content.charAt(i) == 'E' ) {
                        int idx = i + 1, cnt = 0;
                        while( idx < content.length() ) {
                            if( content.charAt(idx) >= '0' && content.charAt(idx) <= '9' ) {
                                cnt *= 10;
                                cnt += ( content.charAt(idx) - '0' );
                            }
                            else break;
                            idx++;
                        }
                        
                        all_item.add(new MealData(cnt, 0, price));
                        all_url.add(document.getString("meal_url"));
                        break;
                    }
                }
            }
            // dp[i][j] 考慮前 i 個優惠代碼且得到 j 個蛋塔的最小花費
            int[][] dp = new int[201][501];
            for(int i=0;i<=200;i++) for(int j=0;j<=500;j++) dp[i][j] = (int)1e9;
            for(int i=0;i<=200;i++) dp[i][0] = 0;
            IndexData[][] last = new IndexData[201][501];
            for(int i=1;i<=all_item.size();i++) {
                int item_price = all_item.get(i-1).price;
                int item_danta = all_item.get(i-1).danta;
                for(int j=0;j<=500;j++) {
                    if( j < item_danta ) {
                        dp[i][j] = dp[i-1][j];
                        last[i][j] = new IndexData(i-1, j);
                        continue;
                    }
                    if( dp[i][j] > dp[i-1][j-item_danta] + item_price ) {
                        dp[i][j] = dp[i-1][j-item_danta] + item_price;
                        last[i][j] = new IndexData(i-1,j-item_danta); 
                        if( j == 2 ) System.out.println(dp[i][j]);
                    }
                    if( dp[i][j] > dp[i-1][j] ) {
                        dp[i][j] = dp[i-1][j];
                        last[i][j] = new IndexData(i-1,j);
                        if( j == 2 ) System.out.println(dp[i][j]);
                    }
                }
            }
            
            int price_cnt = 0;
            int danta_total = 0;
            for(int i=500;i>=0;i--) {
                if( dp[all_item.size()][i] <= req_money ) {
                    price_cnt = dp[all_item.size()][i];
                    danta_total = i;
                    break;
                }
               // System.out.println(dp[all_item.size()][i]);
            }
            System.out.println(danta_total);
            if( danta_total == 0 ) return null;
            int idx1 = all_item.size(), idx2 = danta_total, now_price = price_cnt;
            while( idx1 != 0 && idx2 != 0 ) {
                int item_cnt = all_item.get(idx1-1).danta;
                int item_price = all_item.get(idx1-1).price;
                if( idx2 - item_cnt >= 0 && dp[idx1-1][idx2-item_cnt] + item_price == now_price ) {
                    target_url.add(all_url.get(idx1-1));
                    now_price -= item_price;
                    idx2 = last[idx1][idx2].second;
                }
                idx1--;
            }

            return target_url;
        }
        public List<String> getReqChickenList() {
            List<String> target_url = new ArrayList<>();
            List<MealData> all_item = new ArrayList<>();
            List<String> all_url = new ArrayList<>();
            for (Document document : documents ) {
                String content = document.getString("content");
                int price = document.getInteger("price");
                for(int i=0;i<content.length();i++) {
                    if( content.charAt(i) == 'F' ) {
                        int idx = i + 1, cnt = 0;
                        while( idx < content.length() ) {
                            if( content.charAt(idx) >= '0' && content.charAt(idx) <= '9' ) {
                                cnt *= 10;
                                cnt += ( content.charAt(idx) - '0' );
                            }
                            else break;
                            idx++;
                        }
                        
                        all_item.add(new MealData(0, cnt, price));
                        all_url.add(document.getString("meal_url"));
                        break;
                    }
                }
            }

            int[][] dp = new int[201][501];
            for(int i=0;i<=200;i++) for(int j=0;j<=500;j++) dp[i][j] = (int)1e9;
            dp[0][0] = 0;
            IndexData[][] last = new IndexData[201][501];
            for(int i=1;i<=all_item.size();i++) {
                int item_price = all_item.get(i-1).price;
                int item_chicken = all_item.get(i-1).chicken;
                for(int j=0;j<=500;j++) {
                    if( j - item_chicken < 0 ) {
                        dp[i][j] = dp[i-1][j];
                        last[i][j] = new IndexData(i-1, j);
                        continue;
                    }
                    if( dp[i][j] > dp[i-1][j-item_chicken] + item_price ) {
                        dp[i][j] = dp[i-1][j-item_chicken] + item_price;
                        last[i][j] = new IndexData(i-1,j-item_chicken); 
                    }
                    if( dp[i][j] > dp[i-1][j] ) {
                        dp[i][j] = dp[i-1][j];
                        last[i][j] = new IndexData(i-1,j);
                    }
                }
            }

            int price_cnt = 0;
            int chicken_total = 0;
            for(int i=500;i>=0;i--) {
                if( dp[all_item.size()][i] <= req_money ) {
                    price_cnt = dp[all_item.size()][i];
                    chicken_total = i;
                    break;
                }
            }
            if( chicken_total == 0 ) return null;
            int idx1 = all_item.size(), idx2 = chicken_total, now_price = price_cnt;
            while( idx1 != 0 && idx2 != 0 ) {
                int item_cnt = all_item.get(idx1-1).chicken;
                int item_price = all_item.get(idx1-1).price;
                if( idx2 - item_cnt >= 0 && dp[idx1-1][idx2-item_cnt] + item_price == now_price ) {
                    target_url.add(all_url.get(idx1-1));
                    now_price -= item_price;
                    idx2 = last[idx1][idx2].second;
                }
                idx1--;
            }

            return target_url;
        }
        public List<String> getReqDantaList_nolimit() {
            List<String> target_url = new ArrayList<>();
            List<MealData> all_item = new ArrayList<>();
            List<String> all_url = new ArrayList<>();
            for (Document document : documents ) {
                String content = document.getString("content");
                int price = document.getInteger("price");
                for(int i=0;i<content.length();i++) {
                    if( content.charAt(i) == 'E' ) {
                        int idx = i + 1, cnt = 0;
                        while( idx < content.length() ) {
                            if( content.charAt(idx) >= '0' && content.charAt(idx) <= '9' ) {
                                cnt *= 10;
                                cnt += ( content.charAt(idx) - '0' );
                            }
                            else break;
                            idx++;
                        }
                        
                        all_item.add(new MealData(cnt, 0, price));
                        all_url.add(document.getString("meal_url"));
                        break;
                    }
                }
            }
            // dp[i][j] 考慮前 i 個優惠代碼且得到 j 個蛋塔的最小花費
            int[][] dp = new int[201][501];
            for(int i=0;i<=200;i++) for(int j=0;j<=500;j++) dp[i][j] = (int)1e9;
            dp[0][0] = 0;
            IndexData[][] last = new IndexData[201][501];
            for(int i=1;i<=all_item.size();i++) {
                int item_price = all_item.get(i-1).price;
                int item_danta = all_item.get(i-1).danta;
                for(int j=0;j<=500;j++) {
                    if( j - item_danta < 0 ) {
                        dp[i][j] = dp[i-1][j];
                        last[i][j] = new IndexData(i-1, j);
                        continue;
                    }
                    if( dp[i][j] > dp[i-1][j-item_danta] + item_price ) {
                        dp[i][j] = dp[i-1][j-item_danta] + item_price;
                        last[i][j] = new IndexData(i-1,j-item_danta); 
                    }
                    if( dp[i][j] > dp[i-1][j] ) {
                        dp[i][j] = dp[i-1][j];
                        last[i][j] = new IndexData(i-1,j);
                    }
                    if( dp[i][j] > dp[i][j-item_danta] + item_price ) {
                        dp[i][j] = dp[i][j-item_danta] + item_price;
                        last[i][j] = new IndexData(i,j-item_danta);
                    }
                }
            }
    
            int danta_total = 0;
            for(int i=500;i>=0;i--) {
                if( dp[all_item.size()][i] <= req_money ) {
                    danta_total = i;
                    break;
                }
            }
            System.out.println(danta_total);
            if( danta_total == 0 ) return null;
            int idx1 = all_item.size(), idx2 = danta_total;
            while( idx1 != 0 && idx2 != 0 ) { 
                int item_price = all_item.get(idx1-1).price;             
                int new_idx1 = last[idx1][idx2].first, new_idx2 = last[idx1][idx2].second;
                if( dp[idx1][idx2] == dp[new_idx1][new_idx2] + item_price ) {
                    target_url.add(all_url.get(idx1-1));
                }

                idx1 = new_idx1;
                idx2 = new_idx2;
            }

            System.out.println(danta_total);
    
            return target_url;
        }
        public List<String> getReqChickenList_nolimit() {
            List<String> target_url = new ArrayList<>();
            List<MealData> all_item = new ArrayList<>();
            List<String> all_url = new ArrayList<>();
            for (Document document : documents ) {
                String content = document.getString("content");
                int price = document.getInteger("price");
                for(int i=0;i<content.length();i++) {
                    if( content.charAt(i) == 'F' ) {
                        int idx = i + 1, cnt = 0;
                        while( idx < content.length() ) {
                            if( content.charAt(idx) >= '0' && content.charAt(idx) <= '9' ) {
                                cnt *= 10;
                                cnt += ( content.charAt(idx) - '0' );
                            }
                            else break;
                            idx++;
                        }
                        
                        all_item.add(new MealData(0, cnt, price));
                        all_url.add(document.getString("meal_url"));
                        break;
                    }
                }
            }

            int[][] dp = new int[201][501];
            for(int i=0;i<=200;i++) for(int j=0;j<=500;j++) dp[i][j] = (int)1e9;
            dp[0][0] = 0;
            IndexData[][] last = new IndexData[201][501];
            for(int i=1;i<=all_item.size();i++) {
                int item_price = all_item.get(i-1).price;
                int item_chicken = all_item.get(i-1).chicken;
                for(int j=0;j<=500;j++) {
                    if( j - item_chicken < 0 ) {
                        dp[i][j] = dp[i-1][j];
                        last[i][j] = new IndexData(i-1, j);
                        continue;
                    }
                    if( dp[i][j] > dp[i-1][j-item_chicken] + item_price ) {
                        dp[i][j] = dp[i-1][j-item_chicken] + item_price;
                        last[i][j] = new IndexData(i-1,j-item_chicken); 
                    }
                    if( dp[i][j] > dp[i-1][j] ) {
                        dp[i][j] = dp[i-1][j];
                        last[i][j] = new IndexData(i-1,j);
                    }
                    if( dp[i][j] > dp[i][j-item_chicken] + item_price ) {
                        dp[i][j] = dp[i][j-item_chicken] + item_price;
                        last[i][j] = new IndexData(i, j-item_chicken);
                    }
                }
            }

            int chicken_total = 0;
            for(int i=500;i>=0;i--) {
                if( dp[all_item.size()][i] <= req_money ) {
                    chicken_total = i;
                    break;
                }
            }
            if( chicken_total == 0 ) return null;
            int idx1 = all_item.size(), idx2 = chicken_total;
            while( idx1 != 0 && idx2 != 0 ) {
                int item_price = all_item.get(idx1-1).price;
                int new_idx1 = last[idx1][idx2].first, new_idx2 = last[idx1][idx2].second;
                if( new_idx2 == dp[new_idx1][new_idx2] + item_price ) {
                    target_url.add(all_url.get(idx1-1));
                }
                idx1--;
            }

            return target_url;
        }
        public void delete_db() {
            List<Document> duplicates = collection.aggregate(
                Arrays.asList(
                    group("$meal_id", sum("count", 1), addToSet("ids", "$_id")),
                    match(gt("count", 1)),
                    project(fields(include("ids")))
                )
            ).into(new java.util.ArrayList<>());
    
            duplicates.forEach(doc -> {
                List<Object> ids = doc.getList("ids", Object.class);
                if (ids.size() > 1) {
                    ids.remove(0);  // 保留第一個 ID，移除其餘的 ID
                    collection.deleteMany(in("_id", ids));  // 刪除這些重複的文檔
                }
            });
        }
    }

    @GetMapping("/req_meal")
    public ResponseEntity<Object> CalculaterMeal(@RequestParam("meal_char") String meal_char) {
        KFCalculater calculater = new KFCalculater(meal_char, 0);
        List<String> mealList = calculater.getReqMealList();
        calculater.mongoClient.close();
        System.out.println(mealList);
        return ResponseEntity.ok(mealList);
    }
    @GetMapping("/req_money")
    public ResponseEntity<Object> CalculaterMoney(@RequestParam("money") int money) {
        KFCalculater calculater = new KFCalculater("", money);
        List<String> mealList = calculater.getReqMoneyList();
        calculater.mongoClient.close();
        return ResponseEntity.ok(mealList);
    }
    @GetMapping("/req_danta")
    public ResponseEntity<Object> CalculaterDanta(@RequestParam("money") int money) {
        KFCalculater calculater = new KFCalculater("", money);
        List<String> mealList = calculater.getReqDantaList();
        calculater.mongoClient.close();
        return ResponseEntity.ok(mealList);
    }
    @GetMapping("/req_chicken")
    public ResponseEntity<Object> CalculaterChicken(@RequestParam("money") int money) {
        KFCalculater calculater = new KFCalculater("", money);
        List<String> mealList = calculater.getReqChickenList();
        calculater.mongoClient.close();
        return ResponseEntity.ok(mealList);
    }
    @GetMapping("/req_danta_nolimit")
    public ResponseEntity<Object> CalculaterDantaNoLimit(@RequestParam("money") int money) {
        KFCalculater calculater = new KFCalculater("", money);
        List<String> mealList = calculater.getReqDantaList_nolimit();
        calculater.mongoClient.close();
        return ResponseEntity.ok(mealList);
    }
    @GetMapping("/req_chicken_nolimit")
    public ResponseEntity<Object> CalculaterChickenNoLimit(@RequestParam("money") int money) {
        KFCalculater calculater = new KFCalculater("", money);
        List<String> mealList = calculater.getReqChickenList_nolimit();
        calculater.mongoClient.close();
        return ResponseEntity.ok(mealList);
    }
    @GetMapping("/delete")
    public void delete() {
        List<String> mealList = new ArrayList<>();
        KFCalculater calculater = new KFCalculater("", 0);
        calculater.delete_db();
        calculater.mongoClient.close();
        return;
    }
    
}
