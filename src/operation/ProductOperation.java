package operation

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

import model.Product;

public class ProductOperation{
    private static ProductOperation instance;
    private static final String PRODUCT_FILE = "data/products.txt";
    private static final int PAGE_SIZE = 10;
    
    static{
        Platform.startup(() -> {});
    }
    
    private ProductOperation(){
    }
    
    public static ProductOperation getInstance(){
        if (instance == null){
            instance = new ProductOperation();
        }
        return instance;
    }
    
    public void extractProductsFromFiles(){
        File sourceFile = new File("data/products.txt");
        File targetFile = new File(PRODUCT_FILE);
        if (!sourceFile.exists()){
            System.err.println("Source file not found: " + sourceFile.getAbsolutePath());
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(sourceFile));
             PrintWriter pw = new PrintWriter(new FileWriter(targetFile, false))){
            String line;
            while ((line = br.readLine()) != null){
                if (!line.trim().isEmpty()) {
                    pw.println(line);
                }
            }
            pw.flush();
        } catch (IOException e){
            System.err.println("Error extracting products: " + e.getMessage());
        }
    }
    
    public ProductListResult getProductList(int pageNumber){
        List<Product> allProducts = readProductsFromFile();
        int totalProducts = allProducts.size();
        int totalPages = (totalProducts + PAGE_SIZE - 1)/PAGE_SIZE;
        if (pageNumber < 1){
            pageNumber = 1;
        }
        if (pageNumber > totalPages && totalPages > 0){
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
            if (p.getProId().equals(productId)) {
                iterator.remove();
                found = true;
                break;
            }
        }
        if (found){
            writeProductsToFile(allProducts);
        }
        return found;
    }
    
    public List<Product> getProductListByKeyword(String keyword){
        List<Product> allProducts = readProductsFromFile();
        List<Product> result = new ArrayList<>();
        for (Product p : allProducts) {
            if (p.getProName().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(p);
            }
        }
        return result;
    }
    
    public Product getProductById(String productId){
        List<Product> allProducts = readProductsFromFile();
        for (Product p : allProducts){
            if (p.getProId().equals(productId)){
                return p;
            }
        }
        return null;
    }
    
    public void generateCategoryFigure(){
        Platform.runLater(() -> {
            List<Product> products = readProductsFromFile();
            java.util.Map<String, Integer> categoryCount = new java.util.HashMap<>();
            for (Product p : products){
                String cat = p.getProCategory();
                categoryCount.put(cat, categoryCount.getOrDefault(cat, 0) + 1);
            }
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Category");
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Count");
            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Products by Category");
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            for (java.util.Map.Entry<String, Integer> entry : categoryCount.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
            barChart.getData().add(series);
            Scene scene = new Scene(barChart, 800, 600);
            WritableImage image = scene.snapshot(null);
            File outputDir = new File("data/figure");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            File file = new File(outputDir, "category_chart.png");
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException e){
                e.printStackTrace();
            }
        });
    }
    
    public void generateDiscountFigure(){
        Platform.runLater(() -> {
            List<Product> products = readProductsFromFile();
            int lessThan30 = 0, between30And60 = 0, greaterThan60 = 0;
            for (Product p : products) {
                double discount = p.getProDiscount();
                if (discount < 30) {
                    lessThan30++;
                } else if (discount <= 60) {
                    between30And60++;
                } else {
                    greaterThan60++;
                }
            }
            PieChart pieChart = new PieChart();
            pieChart.setTitle("Discount Distribution");
            pieChart.getData().add(new PieChart.Data("Less than 30", lessThan30));
            pieChart.getData().add(new PieChart.Data("30 to 60", between30And60));
            pieChart.getData().add(new PieChart.Data("Greater than 60", greaterThan60));
            Scene scene = new Scene(pieChart, 800, 600);
            WritableImage image = scene.snapshot(null);
            File outputDir = new File("data/figure");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            File file = new File(outputDir, "discount_chart.png");
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException e){
                e.printStackTrace();
            }
        });
    }
    
    public void generateLikesCountFigure(){
        Platform.runLater(() -> {
            List<Product> products = readProductsFromFile();
            java.util.Map<String, Integer> likesByCategory = new java.util.HashMap<>();
            for (Product p : products) {
                String cat = p.getProCategory();
                likesByCategory.put(cat, likesByCategory.getOrDefault(cat, 0) + p.getProLikesCount());
            }
            List<java.util.Map.Entry<String, Integer>> entryList = new ArrayList<>(likesByCategory.entrySet());
            entryList.sort(java.util.Map.Entry.comparingByValue());
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Category");
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Total Likes");
            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Total Likes by Category (Ascending)");
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            for (java.util.Map.Entry<String, Integer> entry : entryList) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
            barChart.getData().add(series);
            Scene scene = new Scene(barChart, 800, 600);
            WritableImage image = scene.snapshot(null);
            File outputDir = new File("data/figure");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            File file = new File(outputDir, "likes_count_chart.png");
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    
    public void generateDiscountLikesCountFigure(){
        Platform.runLater(() -> {
            List<Product> products = readProductsFromFile();
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Discount (%)");
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Likes Count");
            ScatterChart<String, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
            scatterChart.setTitle("Likes Count vs Discount");
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            for (Product p : products) {
                series.getData().add(new XYChart.Data<>(Double.toString(p.getProDiscount()), p.getProLikesCount()));
            }
            scatterChart.getData().add(series);
            Scene scene = new Scene(scatterChart, 800, 600);
            WritableImage image = scene.snapshot(null);
            File outputDir = new File("data/figure");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            File file = new File(outputDir, "discount_likes_scatter.png");
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    
    public void deleteAllProducts(){
        try (PrintWriter writer = new PrintWriter(new FileWriter(PRODUCT_FILE, false))) {
            writer.print("");
        } catch (IOException e){
            System.err.println("Error deleting all products: " + e.getMessage());
        }
    }
    
    private List<Product> readProductsFromFile(){
        List<Product> products = new ArrayList<>();
        File file = new File(PRODUCT_FILE);
        if (!file.exists()) {
            return products;
        }
        JSONParser parser = new JSONParser();
        try (BufferedReader reader = new BufferedReader(new FileReader(PRODUCT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null){
                if (line.trim().isEmpty())
                    continue;
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
                    Product product = new Product(proId, proModel, proCategory, proName, proCurrentPrice, proRawPrice, proDiscount, proLikesCount);
                    products.add(product);
                } catch (ParseException pe){
                    System.err.println("Error parsing product JSON: " + pe.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading products from file: " + e.getMessage());
        }
        return products;
    }
    
    private void writeProductsToFile(List<Product> products){
        try (PrintWriter writer = new PrintWriter(new FileWriter(PRODUCT_FILE, false))) {
            for (Product p : products) {
                writer.println(p.toString());
            }
        } catch (IOException e) {
            System.err.println("Error writing products to file: " + e.getMessage());
        }
    }
    
    public static class ProductListResult{
        private List<Product> productList;
        private int currentPage;
        private int totalPages;
        
        public ProductListResult(List<Product> productList, int currentPage, int totalPages) {
            this.productList = productList;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
        }
        
        public List<Product> getProductList(){
            return productList;
        }
        
        public int getCurrentPage(){
            return currentPage;
        }
        
        public int getTotalPages(){
            return totalPages;
        }
    }
}
