package part3.test;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ProductInfoServiceImpl extends UnicastRemoteObject implements ProductInfoService {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs the service.
     *
     * @throws RemoteException
     */
    protected ProductInfoServiceImpl() throws RemoteException {
        super();
    }

    /**
     * Get the product info by the given id.
     *
     * @param id the product id
     * @return a ProductInfo instance
     */
    public ProductInfo getProductInfoById(int id) throws RemoteException {
        return new ProductInfo(id, "Sample Product");
    }
}