package ogorek.wojciech.persistence.validator.impl;

import ogorek.wojciech.persistence.model.Customer;
import ogorek.wojciech.persistence.model.Order;
import ogorek.wojciech.persistence.model.Product;
import ogorek.wojciech.persistence.validator.generic.AbstractValidator;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

public class OrderValidator extends AbstractValidator<Order> {
    CustomerValidator customerValidator = new CustomerValidator();
    ProductValidator productValidator = new ProductValidator();

    @Override
    public Map<String, String> validate(Order order) {
        errors.clear();

        if(order == null){
            errors.put("Order object is invalid", "It is null");
            return errors;
        }
        if(!isOrderCustomerValid(order.getCustomer())){
            errors.put("Order customer is invalid", "It has errors: " + order.getCustomer());
        }
        if(!isOrderProductValid(order.getProduct())){
            errors.put("Order product is invalid", "It has errors: " + order.getProduct());
        }
        if(!isOrderQuantityValid(order.getQuantity())){
            errors.put("Order quantity is invalid", "It must be equal or greater than 0: " + order.getQuantity());
        }
        if(!isOrderDateValid(order.getOrderDate())){
            errors.put("Order date is invalid", "It is null: " + order.getOrderDate());
        }

        return errors;
    }


    private boolean isOrderCustomerValid(Customer customer){ return customer != null && !customerValidator.hasErrors();}

    private boolean isOrderProductValid(Product product){ return product != null && !productValidator.hasErrors();}

    private boolean isOrderQuantityValid(int quantity){ return quantity >= 0;}

    private boolean isOrderDateValid(LocalDate localDate){
        return Objects.isNull(localDate);
    }

}
