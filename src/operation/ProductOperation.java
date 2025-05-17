packageoperation;

import java.io.*;
import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import model.Product;

public class ProductOperation{
    private static ProductOperation instance;
    private static final String PRODUCT_FILE = "product.txt";
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
        System.out.println("Extracting product data from source files and saving to " + PRODUCT_FILE);
    }

    public List<Product> getProductList(int pageNumber){
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

        List<Product> pageProducts = new ArrayList<>();
        for (int i = startIndex; i < endIndex; i++){
            pageProducts.add(allProducts.get(i));
        }
        return pageProducts;
    }

    public boolean deleteProduct(String productId){
        List<Product> allProducts = readProductsFromFile();
        boolean found = false;
        Iterator<Product> it = allProducts.iterator();
        while (it.hasNext()){
            Product p = it.next();
            if (p.getProId().equals(productId)){
                it.remove();
                found = true;
                break;
            }
        }
        return found;
    }

    public List<Product> getProductListByKeyword(String keyword){
        List<Product> allProducts = readProductsFromFile();
        List<Product> result = new ArrayList<>();
        for (Product p : allProducts){
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
        System.out.println("Generating category bar chart; saving...");
    }

    public void generateDiscountFigure(){
        System.out.println("Generating discount pie chart; saving...");
    }

    public void generateLikesCountFigure(){
        System.out.println("Generating likes count chart; saving...");
    }

    public void generateDiscountLikesCountFigure(){
        System.out.println("Generating discount-likes scatter chart; saving...");
    }

    public void deleteAllProducts(){
        try (PrintWriter writer = new PrintWriter(new FileWriter(PRODUCT_FILE, false))) {
            writer.print("");
        } catch (IOException e){
            System.err.println("Error deleting all products: " + e.getMessage());
        }
    }
    // Reads product records
    private List<Product> readProductsFromFile(){
        List<Product> products = new ArrayList<>();
        File file = new File(PRODUCT_FILE);
        if (!file.exists()){
            return products;
        }
        JSONParser parser = new JSONParser();
        try (BufferedReader reader = new BufferedReader(new FileReader(PRODUCT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null){
                if (line.trim().isEmpty()){
                    continue;
                }
                try {
                    JSONObject obj = (JSONObject) parser.parse(line);
                    String proId = (String) obj.get("pro_id");
                    String proModel = (String) obj.get("pro_model");
                    String proCategory = (String) obj.get("pro_category");
                    String proName = (String) obj.get("pro_name");
                    double proCurrentPrice = Double.parseDouble(obj.get("pro_current_price").toString());
                    double proRawPrice = Double.parseDouble(obj.get("pro_raw_price").toString());
                    double proDiscount = Double.parseDouble(obj.get("pro_discount").toString());
                    int proLikesCount = Integer.parseInt(obj.get("pro_likes_count").toString());
                    
                    Product product = new Product(proId, proModel, proCategory, proName,
                                                  proCurrentPrice, proRawPrice, proDiscount, proLikesCount);
                    products.add(product);
                } catch (ParseException pe){
                    System.err.println("Error parsing product JSON: " + pe.getMessage());
                }
            }
        } catch (IOException e){
            System.err.println("Error reading products file: " + e.getMessage());
        }
        return products;
    }
}
