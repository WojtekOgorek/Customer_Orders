package ogorek.wojciech.service.utils;

import ogorek.wojciech.persistence.converter.OrderConverter;
import ogorek.wojciech.persistence.exception.AppException;
import ogorek.wojciech.persistence.model.Customer;
import ogorek.wojciech.persistence.model.Order;
import ogorek.wojciech.persistence.model.Product;
import ogorek.wojciech.persistence.validator.impl.CustomerValidator;
import ogorek.wojciech.persistence.validator.impl.OrderValidator;
import ogorek.wojciech.persistence.validator.impl.ProductValidator;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class DataGenerator {

    private final CustomerValidator customerValidator = new CustomerValidator();
    private final ProductValidator productValidator = new ProductValidator();
    private final OrderValidator orderValidator = new OrderValidator();
    private final RandomDataGenerator randomDataGenerator = new RandomDataGenerator();


    public Set<Order> generateUserOrders() {

        var orders = new HashSet<Order>();
        boolean isTrue;

        do {
            var order = Order.builder()
                    .customer(generateUserCustomer())
                    .product(generateUserProduct())
                    .quantity(UserDataService.getInt("Type quantity"))
                    .orderDate(UserDataService.getLocalDate("Type order date"))
                    .build();

            orders.add(order);

            isTrue = UserDataService.getBoolean("Wanna add another order ?  y/n ?");

        } while (isTrue);

        toJsonFile(orders);
        return checkValidation(orders);
    }

    public Set<Order> generateRandomOrders(String filename) {
        var orders = new HashSet<Order>();
        boolean isTrue;
        do {
            var order = Order.builder()
                    .customer(generateRandomCustomer(filename))
                    .product(generateRandomProduct(filename))
                    .quantity(randomDataGenerator.getRandomIntegerValue(999))
                    .orderDate(randomDataGenerator.getRandomLocalDateValue())
                    .build();
            orders.add(order);

            isTrue = UserDataService.getBoolean("Wanna add another random order ?  y/n ?");
        } while (isTrue);

        toJsonFile(orders);
        return checkValidation(orders);

    }

    private void toJsonFile(Set<Order> order){
        OrderConverter orders = new OrderConverter(
                "<filepath_where_u_store_created_orders>\\userOrders.json");
        orders.toJson(checkValidation(order));
    }

    private Set<Order> checkValidation(Set<Order> orders) {
        var counter = new AtomicInteger(1);
        return orders
                .stream()
                .filter(order -> {
                    var errors = orderValidator.validate(order);
                    if (orderValidator.hasErrors()) {
                        System.out.println("validation issue for order nr." + counter.get());
                        errors.forEach((k, v) -> System.out.println(k + ":" + v));
                        System.out.println("\n\n");
                    }
                    counter.getAndIncrement();
                    return !orderValidator.hasErrors();
                })
                .collect(Collectors.toSet());
    }

    private Customer generateRandomCustomer(String filename) {

        var customer = Customer.builder()
                .name(randomDataGenerator.getRandomCustomerFromCollection(filename).getName())
                .surname(randomDataGenerator.getRandomCustomerFromCollection(filename).getSurname())
                .age(randomDataGenerator.getRandomIntegerValue(99))
                .email(randomDataGenerator.getRandomCustomerFromCollection(filename).getEmail())
                .build();

        customerValidator.validate(customer);
        if (customerValidator.hasErrors()) {
            throw new AppException("random generated customer is invalid");
        }
        return customer;
    }

    private Product generateRandomProduct(String filename) {

        var product = Product.builder()
                .name(randomDataGenerator.getRandomProductFromCollection(filename).getName())
                .category(randomDataGenerator.getRandomCategoryValue())
                .price(randomDataGenerator.getRandomBigDecimalValue())
                .build();

        productValidator.validate(product);
        if (productValidator.hasErrors()) {
            throw new AppException("random generated product is invalid");
        }

        return product;

    }


    private Customer generateUserCustomer() {
        System.out.println("Generating customer");

        var customer = Customer.builder()
                .name(UserDataService.getString("Type name"))
                .surname(UserDataService.getString("Type surname"))
                .age(UserDataService.getInt("Type age"))
                .email(UserDataService.getString("Type email"))
                .build();

        customerValidator.validate(customer);
        if (customerValidator.hasErrors()) {
            throw new AppException("generated customer is invalid");
        }
        return customer;
    }

    private Product generateUserProduct() {
        System.out.println("Generating product");

        var product = Product.builder()
                .name(UserDataService.getString("Type name"))
                .category(UserDataService.getCategory("Pick category"))
                .price(UserDataService.getBigDecimal("Type price"))
                .build();

        productValidator.validate(product);
        if (productValidator.hasErrors()) {
            throw new AppException("generated product is invalid");
        }

        return product;
    }


}
