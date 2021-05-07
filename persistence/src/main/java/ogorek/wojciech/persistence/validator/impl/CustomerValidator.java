package ogorek.wojciech.persistence.validator.impl;

import ogorek.wojciech.persistence.model.Customer;
import ogorek.wojciech.persistence.validator.generic.AbstractValidator;

import java.util.Map;

public class CustomerValidator extends AbstractValidator<Customer> {


    @Override
    public Map<String, String> validate(Customer customer) {
        errors.clear();

        if(customer == null){
            errors.put("customer object is invalid", "It is null");
            return errors;
        }
        if(!isCustomerNameValid(customer.getName())){
            errors.put("Customer name is invalid", "Name must contain upper letters, lower and white spaces: " + customer.getName());
        }
        if(!isCustomerSurnameValid(customer.getSurname())){
            errors.put("Customer surname is invalid", "Surname must contain only upper letters, lower and white spaces: " + customer.getSurname());
        }
        if(!isCustomerAgeValid(customer.getAge())){
            errors.put("Customer age is invalid", "Age must equal or be greater than 18: " + customer.getAge());
        }
        if(!isCustomerEmailValid(customer.getEmail())){
            errors.put("Customer email is invalid", "Email must contain valid characters: " + customer.getEmail());
        }
        return errors;
    }

    private boolean isCustomerNameValid(String customerName){
        return customerName != null && customerName.matches("[A-Za-z\\s]+");
    }
    private boolean isCustomerSurnameValid(String customerSurname){
        return customerSurname != null && customerSurname.matches("[A-Za-z\\s]+");
    }
    private boolean isCustomerAgeValid(int customerAge){
        return customerAge >= 18;
    }
    private boolean isCustomerEmailValid(String customerEmail){
        return customerEmail != null && customerEmail.matches("[a-z1-9.]+@[a-z1-9]+\\.[a-z]{2,3}");
    }

}
