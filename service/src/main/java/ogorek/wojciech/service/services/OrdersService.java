package ogorek.wojciech.service.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ogorek.wojciech.persistence.converter.CustomerConverter;
import ogorek.wojciech.persistence.converter.JsonOrdersConverter;
import ogorek.wojciech.persistence.exception.AppException;
import ogorek.wojciech.persistence.model.Customer;
import ogorek.wojciech.persistence.model.Order;
import ogorek.wojciech.persistence.model.Product;
import ogorek.wojciech.persistence.model.enums.Category;
import ogorek.wojciech.persistence.validator.impl.OrderValidator;
import org.eclipse.collections.impl.collector.Collectors2;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static j2html.TagCreator.body;
import static j2html.TagCreator.h1;

public class OrdersService {

    private Set<Order> customerOrders;

    public OrdersService(Set<String> jsonFilenames) {
        customerOrders = readOrdersFromJsonFile(jsonFilenames);
    }

    private Set<Order> readOrdersFromJsonFile(Set<String> jsonFilenames) {

        var orderValidator = new OrderValidator();
        var counter = new AtomicInteger(1);

        return jsonFilenames
                .stream()
                .flatMap(jsonFilename -> new JsonOrdersConverter(jsonFilename)
                        .fromJson()
                        .orElseThrow(() -> new AppException("orders service cannot read file from json file"))
                        .stream()
                        .filter(order -> {
                                    var errors = orderValidator.validate(order);
                                    if (orderValidator.hasErrors()) {
                                        System.out.println("---------------validation error for order nr." + counter.get() + "in file " + jsonFilename + "---------------");
                                        errors.forEach((k, v) -> System.out.println(k + " " + v));
                                        System.out.println("/n/n");
                                    }
                                    counter.getAndIncrement();
                                    return !orderValidator.hasErrors();
                                }
                        )
                )
                .collect(Collectors.toSet());
    }

    /*
     *   method 1
     *   get all orders
     *
     */
    public String orders() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return customerOrders
                .stream()
                .map(gson::toJson)
                .collect(Collectors.joining(","));
    }

    /*
     *   method 2
     *   Get average products price between dates
     *
     */
    public BigDecimal averagePriceBetweenDates(LocalDate d1, LocalDate d2) {
        if (Objects.isNull(d1) || Objects.isNull(d2)) {
            throw new AppException("LocalDate object cannot be null");
        }

        return customerOrders
                .stream()
                .filter(date -> date.getOrderDate().isAfter(d1) && date.getOrderDate().isBefore(d2))
                .map(Order::getProduct)
                .collect(Collectors.collectingAndThen(Collectors.toList(), this::averageProductPrice));

    }

    private BigDecimal averageProductPrice(List<Product> products) {

        var stats = products
                .stream()
                .collect(Collectors2.summarizingBigDecimal(Product::getPrice));

        return stats.getAverage();
    }

    /*
     *   method 3
     *   Get highest priced products in specific category
     *
     */
    public Map<Category, Product> highestPricedProductFromSpecificCategory() {

        return customerOrders
                .stream()
                .map(Order::getProduct)
                .collect(Collectors.groupingBy(Product::getCategory))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, this::highestPricedProduct));

    }

    private Product highestPricedProduct(Map.Entry<Category, List<Product>> entry) {

        return entry
                .getValue()
                .stream()
                .max(Comparator.comparing(Product::getPrice))
                .orElseThrow();
    }

    /*
     *   method 4
     *   Send to user email with his order
     *
     */

    public void orderToMail(String email) {
        if (Objects.isNull(email)) {
            throw new AppException("Email object is invalid");
        }

        String customerProducts = clientsOrder()
                .entrySet()
                .stream()
                .filter(address -> address.getKey().getEmail().equals(email))
                .map(Map.Entry::getValue)
                .collect(Collectors.joining(","));


        EmailService.send(email, "Order", htmlProducts(customerProducts));
    }

    public boolean isThereEmail(String email) {
        if (customerOrders.stream().noneMatch(mail -> mail.getCustomer().getEmail().equals(email))) {
            System.out.println("no email in db");
            return false;
        }
        return true;
    }

    private String htmlProducts(String products) {
        return body(h1(products)).render();
    }

    private Map<Customer, String> clientsOrder() {
        return customerOrders
                .stream()
                .collect(Collectors.groupingBy(Order::getCustomer))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, this::clientsProducts));
    }

    private String clientsProducts(Map.Entry<Customer, List<Order>> entry) {
        return entry
                .getValue()
                .stream()
                .map(p -> p.getProduct().getName())
                .collect(Collectors.joining(", "));
    }

    /*
     *   method 5
     *   Get date with the least and the most ordered products.
     *
     */


    public Map<LocalDate, Integer> maxQuantityLocalDate() {

        return datesWithOrders()
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<LocalDate, Integer> minQuantityLocalDate() {

        return datesWithOrders()
                .entrySet()
                .stream()
                .min(Map.Entry.comparingByValue())
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<LocalDate, Integer> datesWithOrders() {
        return customerOrders
                .stream()
                .collect(Collectors.groupingBy(Order::getOrderDate))
                .entrySet()
                .stream()
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.collectingAndThen(
                                Collectors.mapping(value -> value.getValue().stream().count(), Collectors.toList()),
                                items -> items
                                        .stream()
                                        .max(Long::compareTo)
                                        .map(Long::intValue)
                                        .orElseThrow()))
                );
    }

    /*
     *   method 6
     *   Get Client who paid the most for his orders.
     *
     */
    public Customer customerWhoPaidTheMost() {

        Map<Customer, BigDecimal> customerBill = customerOrders
                .stream()
                .collect(Collectors.groupingBy(Order::getCustomer))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        this::priceOfAllProducts

                ));
        return customerBill
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow()
                .getKey();
    }

    private BigDecimal priceOfAllProducts(Map.Entry<Customer, List<Order>> entry) {

        return entry
                .getValue()
                .stream()
                .flatMap(product -> Collections.nCopies(product.getQuantity(), product.getProduct()).stream())
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /*
     *   method 7
     *   Get orders including discounts.
     *   If customer is under 25 years old - 3%.
     *   If order was made maximum of 2 days before actual date - 2%.
     *   Discounts don't sum up.
     *
     */

    public BigDecimal ordersPriceIncludingDiscounts() {
        return customerOrders
                .stream()
                .map(order -> {
                    var price = order.getProduct().getPrice().multiply(BigDecimal.valueOf(order.getQuantity()));
                    var discount = BigDecimal.ONE;
                    if (order.getCustomer().getAge() < 25) {
                        discount = discount.subtract(new BigDecimal("0.03"));
                    } else if (order.getOrderDate().plusDays(2).compareTo(LocalDate.now()) <= 0) {
                        discount = discount.subtract(new BigDecimal("0.02"));
                    }
                    return price.multiply(discount);
                }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    /*
     *   method 8
     *   Get customers that ordered equal or more amount of a product.
     *   Save those customers to json file.
     *
     */

    public List<Customer> customersThatOrderCertainNumberOfProducts(int number) {
        var customersToJson =
                new ArrayList<>(customerOrders
                        .stream()
                        .filter(quantity -> quantity.getQuantity() >= number)
                        .collect(Collectors.groupingBy(Order::getCustomer, Collectors.counting()))
                        .keySet());

        CustomerConverter converter = new CustomerConverter(
                "<filepath_where_u_wanna_store_this_return>/customersWithProducts.json");
        converter.toJson(customersToJson);

        return customersToJson;
    }


    public Category categoryThatBoughtTheMost() {

        return customerOrders
                .stream()
                .flatMap(customerOrder -> Collections.nCopies(customerOrder.getQuantity(), customerOrder.getProduct().getCategory()).stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow()
                .getKey();

    }

    /*
     *   method 10
     *   Get category that products was bought the most in certain month
     */
    public Map<Month, Category> mostPickedCategoryInAMonth() {

        return customerOrders
                .stream()
                .collect(Collectors.groupingBy(Order::getOrderMonth, Collectors.collectingAndThen(
                        Collectors.mapping(c -> c, Collectors.toList()),
                        category -> category
                                .stream()
                                .flatMap(customerOrder -> Collections.nCopies(customerOrder.getQuantity(), customerOrder.getProduct().getCategory()).stream())
                                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                                .entrySet()
                                .stream()
                                .max(Map.Entry.comparingByValue())
                                .orElseThrow()
                                .getKey()
                )));
    }

    /*
     *   method 11
     *   Get sheet with amount of products that was ordered in certain month.
     *   Sorting is descending.
     *
     */

    public Map<Month, Integer> quantityProductsInAMonth() {

        return customerOrders
                .stream()
                .collect(Collectors.groupingBy(Order::getOrderMonth,
                        Collectors.collectingAndThen(
                                Collectors.mapping(Order::getQuantity, Collectors.toList()),
                                items -> items
                                        .stream()
                                        .reduce(0, Integer::sum)
                        )
                ));
    }


}
