package ctrl;
import java.util.List;

import expection.DuplicateException;
import model.Customer;
import repository.CustomerRepository;

public class CustomerCtrl {
    List<Customer> customers = CustomerRepository.loadCustomersFromFile();
    // Tạo khách hàng mới
    public void createCustomer(String ID, String fullName, String dobString, String phoneNumber, String email) throws DuplicateException{
        for (Customer customer: customers){
            if ((customer.getCustomerID()).equals(ID)){
                throw new DuplicateException("Đã có khách hàng có ID này rồi");
            }
        }
        Customer newCustomer = new Customer(ID, fullName, dobString, phoneNumber, email);
        // Khi email không được cung cấp
        if (email == null || email.isEmpty()) {
            newCustomer = new Customer(ID, fullName, dobString, phoneNumber);
        }
        customers.add(newCustomer);
        CustomerRepository.saveCustomersToFile(customers);
    }
    // Cập nhật thông tin khách hàng khi đã biết khách hàng đó
    public void updateCustomerInfo(String customerID, String fullName, String phoneNumber, String email) {
        for (Customer customer : customers) {
            if (fullName != null && !fullName.isBlank()) customer.setFullName(fullName);
            if (phoneNumber != null && !phoneNumber.isBlank()) customer.setPhoneNumber(phoneNumber);
            if (email != null && !email.isBlank()) customer.setEmail(email);
            CustomerRepository.saveCustomersToFile(customers);
        }
    }
}
