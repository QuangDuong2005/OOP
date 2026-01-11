package expection;
// Ngoại lệ cho ngày không hợp lệ
public class InvalidDateException extends Exception {
    public InvalidDateException(String message) {
        super(message);
    }   
}
