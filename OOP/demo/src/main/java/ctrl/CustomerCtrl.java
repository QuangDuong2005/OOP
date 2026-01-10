package ctrl;
import model.Customer;
import repository.CustomerRepository;

public class CustomerCtrl {
    // Tạo khách hàng mới
    public static void createCustomer(String fullName, String dobString, String phoneNumber, String email) {
        Customer newCustomer = new Customer(fullName, dobString, phoneNumber, email);
        // Khi email không được cung cấp
        if (email == null || email.isEmpty()) {
            newCustomer = new Customer(fullName, dobString, phoneNumber);
        }
        CustomerRepository.appendCustomerToFile(newCustomer);
    }
    // Cập nhật thông tin khách hàng khi đã biết khách hàng đó
    public static void updateCustomerInfo(Customer customer, String fullName, String phoneNumber, String email) {
        customer.setFullName(fullName);
        customer.setPhoneNumber(phoneNumber);
        customer.setEmail(email);
        CustomerRepository.saveCustomersToFile(CustomerRepository.loadCustomersFromFile());
    }
}
