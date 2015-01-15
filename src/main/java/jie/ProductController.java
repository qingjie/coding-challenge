/**
 * 
 */
package jie;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ProductController {

	private ProductService productService;

	@RequestMapping("/")
	public String proudctIndex(Model model) throws Exception {
		List<Product> productIndex = productService.findAllProducts();

		joinWithBrand(productIndex);

		// Sort
		Collections.sort(productIndex, new Comparator<Product>() {

			// @Override
			public int compare(Product o1, Product o2) {
				return o2.getQuantitySold() - o1.getQuantitySold();
			}
		});

		model.addAttribute("products", productIndex);

		return "index";
	}

	/**
	 * @param productIndex
	 * @throws Exception
	 */
	private void joinWithBrand(List<Product> productIndex) throws Exception {
		Map<String, String> cache = new HashMap<String, String>();
		for (Product product : productIndex) {
			if (StringUtils.isNotBlank(product.getBrandId())) {
				if (!cache.containsKey(product.getBrandId())) {
					Brand brand = productService.getBrand(product.getBrandId());
					if (brand == null) {
						cache.put(product.getBrandId(), null);
					} else {
						cache.put(product.getBrandId(), brand.getBrandUrl());
					}
				}
				product.setBrandUrl(cache.get(product.getBrandId()));
			}
		}
	}

	@Autowired
	public void setProductService(ProductService productService) {
		this.productService = productService;
	}
}
