package ogorek.wojciech.persistence.converter;

import ogorek.wojciech.persistence.model.Order;

import java.util.List;

public class JsonOrdersConverter extends JsonConverter<List<Order>> {
    public JsonOrdersConverter(String jsonFilename) {
        super(jsonFilename);
    }
}
