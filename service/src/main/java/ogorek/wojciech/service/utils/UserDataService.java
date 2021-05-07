package ogorek.wojciech.service.utils;

import lombok.experimental.UtilityClass;
import ogorek.wojciech.persistence.exception.AppException;
import ogorek.wojciech.persistence.model.enums.Category;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

@UtilityClass
public class UserDataService {

    private Scanner scanner = new Scanner(System.in);

    public boolean getBoolean(String message) {
        System.out.println(message);

        return scanner.nextLine().toLowerCase().equals("y");
    }
    public String getString(String message) {
        System.out.println(message);

        return scanner.nextLine();
    }

    public int getInt(String message) {
        System.out.println(message);

        String value = scanner.nextLine();
        if (!value.matches("\\d+")) {
            throw new AppException("getInt value must be integer");
        }
        return Integer.parseInt(value);
    }

    public BigDecimal getBigDecimal(String message) {
        System.out.println(message);

        String value = scanner.nextLine();
        if (!value.matches("(\\d+\\.)?\\d+")) {
            throw new AppException("getBigDecimal value must be BigDecimal");
        }
        return new BigDecimal(value);
    }

    public Category getCategory(String message) {
        System.out.println(message);

        var counter = new AtomicInteger(1);

        Arrays
                .stream(Category.values())
                .forEach(category -> System.out.println(counter.getAndIncrement() + ". " + category));
        var option = getInt("Pick one option");
        if(option < 1 || option > Category.values().length ){
            throw new AppException("getCategory invalid category option");
        }

        return Category.values()[option - 1];
    }
    public LocalDate getLocalDate(String message) {
        System.out.println(message);
        System.out.println("Correct date format: yyyy-MM-dd");

        String value = scanner.nextLine();

        var date = LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if(!value.equals(String.valueOf(date))){
            throw new AppException("get local date invalid format");
        }
        return date;
    }

    public void close(){
        if(scanner != null){
            scanner.close();
            scanner = null;
        }
    }


}
