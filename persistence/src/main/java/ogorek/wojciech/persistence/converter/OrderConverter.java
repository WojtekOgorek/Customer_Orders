package ogorek.wojciech.persistence.converter;

import ogorek.wojciech.persistence.model.Order;

import java.util.Set;

public class OrderConverter extends JsonConverter<Set<Order>>{
    public OrderConverter(String jsonFilename) {
        super(jsonFilename);
    }
}
