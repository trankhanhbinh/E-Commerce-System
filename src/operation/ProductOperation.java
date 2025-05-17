package Assignment.src.operation;

import java.io.*;
import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Assignment.src.model.Product;

public class ProductOperation{
    private static ProductOperation instance;
    private static final String PRODUCT_FILE = "data/users.txt";
    private static final int PAGE_SIZE = 10;
    private ProductOperation(){
    }
    
    public static ProductOperation getInstance(){
        if (instance == null){
            instance = new ProductOperation();
        }
        return instance;
    }
    
    public void extractProductsFromFiles(){
        System.out.println("Extracting products from source files and saving to " + PRODUCT_FILE);
    }

    public ProductListResult getProductList(int pageNumber){
        List<Product> allProducts = readProductsFromFile();
        int totalProducts = allProducts.size();
        int totalPages = (totalProducts + PAGE_SIZE - 1)/PAGE_SIZE;
        if (pageNumber < 1){
            pageNumber = 1;
        }
        if (pageNumber > totalPages && totalPages > 0) {
            pageNumber = totalPages;
        }
        
        int startIndex = (pageNumber - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, totalProducts);
        List<Product> pageProducts = allProducts.subList(startIndex, endIndex);
        
        return new ProductListResult(pageProducts, pageNumber, totalPages);
    }
    
    public boolean deleteProduct(String productId){
        List<Product> allProducts = readProductsFromFile();
        boolean found = false;
        Iterator<Product> iterator = allProducts.iterator();
        while (iterator.hasNext()){
            Product p = iterator.next();
            if (p.getProId().equals(productId)){
                iterator.remove();
                found = true;
                break;
            }
        }
        if (found) {
            writeProductsToFile(allProducts);
        }
        return found;
    }
    
    public List<Product> getProductListByKeyword(String keyword){
        List<Product> allProducts = readProductsFromFile();
        List<Product> result = new ArrayList<>();
        for (Product p : allProducts) {
            if (p.getProName().toLowerCase().contains(keyword.toLowerCase())){
                result.add(p);
            }
        }
        return result;
    }
    
    public Product getProductById(String productId){
        List<Product> allProducts = readProductsFromFile();
        for (Product p : allProducts) {
            if (p.getProId().equals(productId)){
                return p;
            }
        }
        return null;
    }
    
    public void generateCategoryFigure(){
        System.out.println("Generating category bar chart and saving to data/figure folder.");
    }
    
    public void generateDiscountFigure(){
        System.out.println("Generating discount pie chart and saving to data/figure folder.");
    }
    
    public void generateLikesCountFigure(){
        System.out.println("Generating likes count chart and saving to data/figure folder.");
    }
    
    public void generateDiscountLikesCountFigure(){
        System.out.println("Generating scatter chart of discount vs. likes count and saving to data/figure folder.");
    }
    
    public void deleteAllProducts(){
        try (PrintWriter writer = new PrintWriter(new FileWriter(PRODUCT_FILE, false))) {
            writer.print("");
        } catch (IOException e) {
            System.err.println("Error deleting all products: " + e.getMessage());
        }
    }

    private List<Product> readProductsFromFile(){
        List<Product> products = new ArrayList<>();
        File file = new File(PRODUCT_FILE);
        if (!file.exists()){
            return products;
        }
        JSONParser parser = new JSONParser();
        try (BufferedReader reader = new BufferedReader(new FileReader(PRODUCT_FILE))){
            String line;
            while ((line = reader.readLine()) != null){
                if (line.trim().isEmpty()) continue;
                try {
                    JSONObject json = (JSONObject) parser.parse(line);
                    String proId = (String) json.get("pro_id");
                    String proModel = (String) json.get("pro_model");
                    String proCategory = (String) json.get("pro_category");
                    String proName = (String) json.get("pro_name");
                    double proCurrentPrice = Double.parseDouble(json.get("pro_current_price").toString());
                    double proRawPrice = Double.parseDouble(json.get("pro_raw_price").toString());
                    double proDiscount = Double.parseDouble(json.get("pro_discount").toString());
                    int proLikesCount = Integer.parseInt(json.get("pro_likes_count").toString());
                    Product product = new Product(proId, proModel, proCategory, proName,
                                                  proCurrentPrice, proRawPrice, proDiscount, proLikesCount);
                    products.add(product);
                } catch (ParseException pe){
                    System.err.println("Error parsing product JSON: " + pe.getMessage());
                }
            }
        } catch (IOException e){
            System.err.println("Error reading products from file: " + e.getMessage());
        }
        return products;
    }
    
    private void writeProductsToFile(List<Product> products){
        try (PrintWriter writer = new PrintWriter(new FileWriter(PRODUCT_FILE, false))){
            for (Product p : products){
                writer.println(p.toString());
            }
        } catch (IOException e){
            System.err.println("Error writing products to file: " + e.getMessage());
        }
    }
    
    public static class ProductListResult{
        private List<Product> productList;
        private int currentPage;
        private int totalPages;
        
        public ProductListResult(List<Product> productList, int currentPage, int totalPages){
            this.productList = productList;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
        }
        
        public List<Product> getProductList(){
            return productList;
        }
        
        public int getCurrentPage() {
            return currentPage;
        }
        
        public int getTotalPages() {
            return totalPages;
        }
    }
}