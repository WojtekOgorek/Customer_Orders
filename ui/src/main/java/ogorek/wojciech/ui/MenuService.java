package ogorek.wojciech.ui;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import ogorek.wojciech.persistence.exception.AppException;
import ogorek.wojciech.service.services.OrdersService;
import ogorek.wojciech.service.utils.DataGenerator;
import ogorek.wojciech.service.utils.UserDataService;


@RequiredArgsConstructor
public class MenuService {

    private final OrdersService ordersService;
    private final DataGenerator dataGenerator;

    public void mainMenu() {
        while (true) {
            try {
                int option = chooseOptionForMainMenu();
                switch (option) {
                    case 1 -> option1();
                    case 2 -> option2();
                    case 3 -> option3();
                    case 4 -> option4();
                    case 5 -> option5();
                    case 6 -> option6();
                    case 7 -> option7();
                    case 8 -> option8();
                    case 9 -> option9();
                    case 10 -> option10();
                    case 11 -> option11();
                    case 12 -> option12();
                    case 13 -> option13();
                    case 14 -> {
                        UserDataService.close();
                        System.out.println("Have a nice day!");
                        return;
                    }
                    default -> System.out.println("\nWrong option, try again\n");
                }
            } catch (Exception e) {
                throw new AppException("menu exception " + e.getMessage());
            }
        }
    }

    private int chooseOptionForMainMenu() {

        System.out.println("1.Get all orders.");
        System.out.println("2.Get average products price between dates.");
        System.out.println("3.Get highest priced products in specific category.");
        System.out.println("4.Send to user email with his order.");
        System.out.println("5.Get date with the least and the most ordered products.");
        System.out.println("6.Get Client who paid the most for his orders.");
        System.out.println("7.Get orders including discounts.");
        System.out.println("8.Get customers that ordered specified amount of a product.");
        System.out.println("9.Get category that products was bought the most.");
        System.out.println("10.Get category that products was bought the most in certain month");
        System.out.println("11.Get sheet with amount of products that was ordered in certain month.");
        System.out.println("12.Generate user order.");
        System.out.println("13.Generate random order.");

        System.out.println("14.Exit");

        return UserDataService.getInt("Choose option");
    }

    private void option1() {
        var orders = ordersService.orders();
        System.out.println(orders);
    }

    private void option2() {
        var dateFrom = UserDataService.getLocalDate("Type date from");
        var dateTo = UserDataService.getLocalDate("Type date to");

        var averagePriceBetweenDates = ordersService.averagePriceBetweenDates(dateFrom, dateTo);
        System.out.print("average price = ");
        System.out.println(toJson(averagePriceBetweenDates));
    }

    private void option3() {
        var highestPricedProductInCategory = ordersService.highestPricedProductFromSpecificCategory();
        System.out.println(toJson(highestPricedProductInCategory));
    }

    private void option4() {
        var email = UserDataService.getString("Type email");
        if(!ordersService.isThereEmail(email)){
            mainMenu();
        }
        ordersService.orderToMail(email);
    }

    private void option5() {
        var min = ordersService.minQuantityLocalDate();
        var max = ordersService.maxQuantityLocalDate();
        System.out.print("min = ");
        System.out.println(toJson(min));
        System.out.print("max = ");
        System.out.println(toJson(max));
    }

    private void option6() {
        var customerWhoPaidTheMost = ordersService.customerWhoPaidTheMost();
        System.out.println(toJson(customerWhoPaidTheMost));
    }

    private void option7() {
        var orderWithDiscount = ordersService.ordersPriceIncludingDiscounts();
        System.out.println(toJson(orderWithDiscount));
    }

    private void option8() {
        var numberOfOrderedProducts = UserDataService.getInt("Type number of products");
        var customersToJson = ordersService.customersThatOrderCertainNumberOfProducts(numberOfOrderedProducts);
        System.out.println(toJson(customersToJson.stream().count()));
    }
    private void option9() {
        var categoryWithMaxProductSell = ordersService.categoryThatBoughtTheMost();
        System.out.println(toJson(categoryWithMaxProductSell));
    }
    private void option10() {
        var categoryWithMaxProductSellPerMonth = ordersService.mostPickedCategoryInAMonth();
        System.out.println(toJson(categoryWithMaxProductSellPerMonth));
    }
    private void option11() {
        var mapOfMonthAndAmountOfProductSell = ordersService.quantityProductsInAMonth();
        System.out.println(toJson(mapOfMonthAndAmountOfProductSell));
    }
    private void option12(){
        var userOrders = dataGenerator.generateUserOrders();
        System.out.println(toJson(userOrders));
    }
    private void option13(){
        var filename = "<filepath_where_u_get_random_data>/randomOrders.json";
        var randomOrders = dataGenerator.generateRandomOrders(filename);
        System.out.println(toJson(randomOrders));
    }

    private static <T> String toJson(T t) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(t);
    }
}

