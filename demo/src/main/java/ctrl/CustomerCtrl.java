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
    public static void updateCustomerInfo(Customer customer, String fullName, String phoneNumber, String email, java.util.List<Customer> customers) {
        if (fullName != null && !fullName.isBlank()) customer.setFullName(fullName);
        if (phoneNumber != null && !phoneNumber.isBlank()) customer.setPhoneNumber(phoneNumber);
        if (email != null && !email.isBlank()) customer.setEmail(email);
        CustomerRepository.saveCustomersToFile(customers);
    }
}
