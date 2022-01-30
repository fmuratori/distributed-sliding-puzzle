package part3.test;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

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
            Registry registry = LocateRegistry.getRegistry("192.168.0.100", 1099);
            ProductInfoService productInfoService = (ProductInfoService) registry.lookup(ProductInfoService.SERVICE_NAME);

//            ProductInfoService productInfoService = (ProductInfoService) Naming.lookup(ProductInfoService.SERVICE_NAME);
            // invoke the remote method via the stub
            ProductInfo productInfo = productInfoService.getProductInfoById(123);
            System.out.println("ProductInfoClient> production info received: " + productInfo.toString());
        } catch (Exception e) {
            System.err.println("ProductInfoClient> RemoteDate exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
