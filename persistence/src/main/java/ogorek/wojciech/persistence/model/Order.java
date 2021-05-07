package ogorek.wojciech.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Month;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    private Customer customer;
    private Product product;
    private int quantity;
    private LocalDate orderDate;

public Month getOrderMonth(){
    return orderDate.getMonth();
}
}
