package jie;

import java.util.List;

public interface ProductService {
	List<Product> findAllProducts() throws Exception;

	Brand getBrand(String brandId) throws Exception;
}
