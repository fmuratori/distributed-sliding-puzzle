package part3.test;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ProductInfoService extends Remote {
    /**
     * The name used in the RMI registry.
     */
    static final String SERVICE_NAME = "ProductInfoService";

    /**
     * Get product info by the given Id.
     *
     * @param id the product id
     * @return a ProductInfo instance
     * @throws RemoteException
     */
    ProductInfo getProductInfoById(int id) throws RemoteException;
}