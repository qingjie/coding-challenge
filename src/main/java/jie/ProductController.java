
package jie;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class ProductController {

    private ProductService productService;
    private Map<String, Comparator<Product>> comparatorMap = null;
    private final List<String> SORTABLE_FIELDS = Collections
            .unmodifiableList(Arrays.asList(
                    "name",
                    "image",
                    "brandId",
                    "brandName",
                    "brandUrl",
                    "description",
                    "quantitySold"
                    ));
    private ConcurrentMap<String, String> cache = new ConcurrentHashMap<String, String>();


    public ProductController() throws Exception{
        Map<String,Comparator<Product>> sortComparatorMap = new HashMap<String, Comparator<Product>>();
        for(String fieldName : SORTABLE_FIELDS){
            sortComparatorMap.put(fieldName, new ReflectionComparator(fieldName));
        }
        this.comparatorMap = Collections.unmodifiableMap(sortComparatorMap);
    }

    @RequestMapping("/")
    public String proudctIndex(
            @RequestParam(value = "sort", required = false, defaultValue = "quantitySold") String sort,
            Model model) throws Exception {
        List<Product> productIndex = productService.findAllProducts();

        productIndex = filterDiscontinuedAndJoinWithBrand(productIndex);

        if(!this.comparatorMap.containsKey(sort)){
            sort = "quantitySold";
        }
        
        model.addAttribute("sort", sort);
        
        // Sort
        Collections.sort(productIndex, this.comparatorMap.get(sort));

        model.addAttribute("products", productIndex);

        return "index";
    }

    /**
     * @param productIndex
     * @throws Exception
     */
    private List<Product> filterDiscontinuedAndJoinWithBrand(List<Product> productIndex) throws Exception {
        List<Product> result = new LinkedList<Product>();
        for (Product product : productIndex) {
            if(product.isDiscontinued()){
               continue; 
            }
            else{
                result.add(product);
            }
            if (StringUtils.isNotBlank(product.getBrandId())) {
                
                if (!cache.containsKey(product.getBrandId())) {
                    Brand brand = productService.getBrand(product.getBrandId());
                    if (brand == null) {
                        cache.put(product.getBrandId(), null);
                    }
                    else {
                        cache.put(product.getBrandId(), brand.getBrandUrl());
                    }
                }
                product.setBrandUrl(cache.get(product.getBrandId()));
            }
        }
        return result;
    }

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    static class ReflectionComparator implements Comparator<Product> {
        
        private final Method getter;

        public ReflectionComparator(String fieldName)
                throws Exception {
            String getterMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            getter = Product.class.getMethod(getterMethodName);
        }

        @SuppressWarnings("unchecked")
        //@Override
        public int compare(Product o1, Product o2) {
            
            try {
                return ((Comparable<Object>) getter.invoke(o2)).compareTo(getter.invoke(o1));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

    }
}
