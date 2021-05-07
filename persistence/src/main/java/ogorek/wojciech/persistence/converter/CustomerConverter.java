package ogorek.wojciech.persistence.converter;

import ogorek.wojciech.persistence.model.Customer;

import java.util.List;

public class CustomerConverter extends JsonConverter<List<Customer>> {
    public CustomerConverter(String jsonFilename) {
        super(jsonFilename);
    }
}
