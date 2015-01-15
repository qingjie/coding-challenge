package jie;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Service
public class ProductServiceImpl implements ProductService {

	private RestTemplate restTemplate;
	private ObjectMapper objectMapper = new ObjectMapper();

	// @Override
	public List<Product> findAllProducts() throws Exception {
		return objectMapper.readValue(
				get("http://api.bodybuilding.com/api-proxy/commerce/products"),
				ProductResponse.class).getData();
	}

	// @Override
	public Brand getBrand(String brandId) throws Exception {
		List<Brand> result = objectMapper.readValue(
				get("http://api.bodybuilding.com/api-proxy/commerce/brand/"
						+ URLEncoder.encode(brandId, "UTF-8")),
				BrandResponse.class).getData();

		if (result.isEmpty()) {
			return null;
		} else {
			return result.get(0);
		}
	}

	private String get(String url) throws UnsupportedEncodingException {
		ResponseEntity<String> response = restTemplate.getForEntity(url,
				String.class);

		if (response.getStatusCode() != HttpStatus.OK) {
			throw new RuntimeException();
		}
		return response.getBody();
	}

	static class BrandResponse extends Response {
		@JsonDeserialize(as = ArrayList.class, contentAs = Brand.class)
		private List<Brand> data;

		public List<Brand> getData() {
			return data;
		}

		public void setData(List<Brand> data) {
			this.data = data;
		}
	}

	static abstract class Response {

		@JsonProperty("err_msg")
		private String errMsag;
		@JsonProperty("ret_code")
		private String retCode;

		public String getErrMsag() {
			return errMsag;
		}

		public void setErrMsag(String errMsag) {
			this.errMsag = errMsag;
		}

		public String getRetCode() {
			return retCode;
		}

		public void setRetCode(String retCode) {
			this.retCode = retCode;
		}

	}

	static class ProductResponse extends Response {
		@JsonDeserialize(as = ArrayList.class, contentAs = Product.class)
		private List<Product> data;

		public List<Product> getData() {
			return data;
		}

		public void setData(List<Product> data) {
			this.data = data;
		}
	}

	@Autowired
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
}
