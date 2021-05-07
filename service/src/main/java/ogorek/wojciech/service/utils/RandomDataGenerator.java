package ogorek.wojciech.service.utils;

import ogorek.wojciech.persistence.converter.OrderConverter;
import ogorek.wojciech.persistence.exception.AppException;
import ogorek.wojciech.persistence.model.Customer;
import ogorek.wojciech.persistence.model.Order;
import ogorek.wojciech.persistence.model.Product;
import ogorek.wojciech.persistence.model.enums.Category;
import ogorek.wojciech.persistence.validator.impl.CustomerValidator;
import ogorek.wojciech.persistence.validator.impl.ProductValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class RandomDataGenerator {

    private final Random rnd = new Random();

    public LocalDate getRandomLocalDateValue() {
        LocalDate startDate = LocalDate.of(2000, 12, 1);
        long start = startDate.toEpochDay();
        LocalDate endDate = LocalDate.now();
        long end = endDate.toEpochDay();

        return LocalDate.ofEpochDay(ThreadLocalRandom.current().longs(start, end).findAny().orElseThrow());
    }

    public BigDecimal getRandomBigDecimalValue() {
        return BigDecimal.valueOf(Math.random());
    }

    public Category getRandomCategoryValue() {
        var category = Category.values();
        return category[rnd.nextInt(category.length)];
    }

    public int getRandomIntegerValue(int max) {
        return rnd.nextInt(max + 1);
    }


    public Customer getRandomCustomerFromCollection(String filename) {

        return getCustomersFromJsonFile(filename)
                .stream()
                .collect(Collectors.collectingAndThen
                        (Collectors.toList(),
                                collected -> {
                                    Collections.shuffle(collected);
                                    return collected
                                            .stream()
                                            .findFirst()
                                            .orElseThrow();
                                }));
    }

    public Product getRandomProductFromCollection(String filename) {

        return getProductsFromJsonFile(filename)
                .stream()
                .collect(Collectors.collectingAndThen
                        (Collectors.toList(),
                                collected -> {
                                    Collections.shuffle(collected);
                                    return collected
                                            .stream()
                                            .findFirst()
                                            .orElseThrow();
                                }));
    }


    private Set<Product> getProductsFromJsonFile(String jsonFile) {

        var productValidator = new ProductValidator();
        var counter = new AtomicInteger(1);

        return new OrderConverter(jsonFile)
                .fromJson()
                .orElseThrow(() -> new AppException("Random product data service - cannot read data from json file "))
                .stream()
                .map(Order::getProduct)
                .filter(product -> {
                    var errors = productValidator.validate(product);
                    if (productValidator.hasErrors()) {
                        System.out.println("validation issue with product nr. " + counter.get());
                        errors.forEach((k, v) -> System.out.println(k + ": " + v));
                        System.out.println("\n\n");
                    }
                    counter.incrementAndGet();
                    return !productValidator.hasErrors();
                })
                .collect(Collectors.toSet());


    }

    private Set<Customer> getCustomersFromJsonFile(String jsonFile) {

        var customerValidator = new CustomerValidator();
        var counter = new AtomicInteger(1);

        return new OrderConverter(jsonFile)
                .fromJson()
                .orElseThrow(() -> new AppException("Random customer data service - cannot read data from json file"))
                .stream()
                .map(Order::getCustomer)
                .filter(customer -> {
                    var errors = customerValidator.validate(customer);
                    if (customerValidator.hasErrors()) {
                        System.out.println("validation issue with customer nr." + counter.get());
                        errors.forEach((k, v) -> System.out.println(k + ": " + v));
                        System.out.println("\n\n");
                    }
                    counter.incrementAndGet();
                    return !customerValidator.hasErrors();

                })
                .collect(Collectors.toSet());
    }

}
