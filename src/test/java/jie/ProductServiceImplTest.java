/**
 * 
 */
package jie;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

public class ProductServiceImplTest {

	private ProductServiceImpl productServiceImpl = new ProductServiceImpl();

	@Before
	public void setup() throws Exception {
		RestTemplate restTemplate = new RestTemplate();

		productServiceImpl.setRestTemplate(restTemplate);
	}

	/**
	 * Test method for {@link jie.ProductServiceImpl#findAllProducts()}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFindAllProducts() throws Exception {
		List<Product> actual = productServiceImpl.findAllProducts();

		assertNotNull(actual);
		assertFalse(actual.isEmpty());
	}

	@Test
	public void testGetBrand() throws Exception {
		Brand actual = productServiceImpl.getBrand("BRAND_PRO_TAN");

		assertNotNull(actual);
		assertEquals("BRAND_PRO_TAN", actual.getId());
		assertEquals("/pt/pt.htm", actual.getBrandUrl());
	}
}
