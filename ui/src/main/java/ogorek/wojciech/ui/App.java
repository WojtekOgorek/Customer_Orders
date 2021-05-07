package ogorek.wojciech.ui;

import ogorek.wojciech.service.services.OrdersService;
import ogorek.wojciech.service.utils.DataGenerator;

import java.util.Set;


public class App {


    public static void main(String[] args) {
//        EmailService.send("ogorkao@gmail.com", "Test Message", "<h1>Hello World</h1>");

        String file = "<filepath_to_main_orders_json>orders.json";
        String file2 = "<filepath_to_main_orders2_json>\\orders2.json";

        Set<String> orders = Set.of(file, file2);

        OrdersService ordersService = new OrdersService(orders);
        DataGenerator dataGenerator = new DataGenerator();
        MenuService menuService = new MenuService(ordersService, dataGenerator);
        menuService.mainMenu();


    }
}
