package ogorek.wojciech.persistence.validator.impl;

import ogorek.wojciech.persistence.model.Product;
import ogorek.wojciech.persistence.validator.generic.AbstractValidator;

import java.math.BigDecimal;
import java.util.Map;

public class ProductValidator extends AbstractValidator<Product> {

    @Override
    public Map<String, String> validate(Product product) {

        errors.clear();

        if(product == null){
            errors.put("Product object is invalid", "It is null");
            return errors;
        }
        if(!isProductNameValid(product.getName())){
            errors.put("Product name is invalid", "It must contain upper letters, lower and white spaces only: " + product.getName());
        }
        if(!isProductPriceValid(product.getPrice())){
            errors.put("Product price is invalid", "It cannot be null and must be greater than 0: " + product.getPrice());
        }
        return errors;
    }

    private boolean isProductNameValid(String productName){
        return (productName != null && productName.matches("[A-Za-z\\s]+"));
    }
    private boolean isProductPriceValid(BigDecimal productPrice){
        return (productPrice != null && productPrice.compareTo(BigDecimal.ZERO) > 0);
    }

}
