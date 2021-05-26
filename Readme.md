# Customer Orders

Customer Orders is a simple multi-module java application to show how you can use more advanced java streams, sending
emails to customers, generating random products, customers and whole orders from or to json file.

## Installation

Use maven -> [link](https://maven.apache.org/download.cgi) <- to install customer orders.
You need to add file paths to json paths to the filename variables. 

```bash
#main folder
mvn clean install
#go to ui folder 
cd ui
#go to target folder
cd target
#start app
java -jar --enable-preview ui.jar
```

## Usage

```java

/*
 *
 *    ----------  MENU SERVICE ----------
 *
 */
public class MenuService {
    
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
}
```
