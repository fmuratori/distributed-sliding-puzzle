package part3.test;

import java.rmi.Naming;

/**
 * @author Kevin Yang
 *
 */
public class ProductInfoClient {
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            System.out.println("ProductInfoClient> get product info with id '123'...");
            // looks up the registry by service name and returns a stub
            ProductInfoService productInfoService = (ProductInfoService) Naming.lookup(ProductInfoService.SERVICE_NAME);
            // invoke the remote method via the stub
            ProductInfo productInfo = productInfoService.getProductInfoById(123);
            System.out.println("ProductInfoClient> production info received: " + productInfo.toString());
        } catch (Exception e) {
            System.err.println("ProductInfoClient> RemoteDate exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
