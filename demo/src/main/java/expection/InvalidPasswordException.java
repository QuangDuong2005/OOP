package expection;
// Ngoại lệ cho mật khẩu không hợp lệ
public class InvalidPasswordException extends Exception {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
