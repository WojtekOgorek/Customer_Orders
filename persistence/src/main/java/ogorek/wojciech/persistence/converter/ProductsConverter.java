package ogorek.wojciech.persistence.converter;

import ogorek.wojciech.persistence.model.Product;

import java.util.List;

public class ProductsConverter extends JsonConverter<List<Product>> {
    public ProductsConverter(String jsonFilename) {
        super(jsonFilename);
    }
}
