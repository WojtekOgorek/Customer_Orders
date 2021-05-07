package ogorek.wojciech.persistence.validator.impl;

import ogorek.wojciech.persistence.model.Order;
import ogorek.wojciech.persistence.model.Orders;
import ogorek.wojciech.persistence.validator.generic.AbstractValidator;

import java.util.Map;
import java.util.Set;

public class OrdersValidator extends AbstractValidator<Orders> {
    OrderValidator orderValidator = new OrderValidator();
    @Override
    public Map<String, String> validate(Orders orders) {

        errors.clear();

        if(orders == null){
            errors.put("orders value is invalid","It is null");
            return errors;
        }

        if(!isOrdersValid(orders.getOrders())){
            errors.put("Orders value", "is invalid" + orders.getOrders());
        }

        return errors;
    }

    private boolean isOrdersValid(Set<Order> orders){
        return orders != null && orders.stream().anyMatch(p -> orderValidator.hasErrors());

    }
}
