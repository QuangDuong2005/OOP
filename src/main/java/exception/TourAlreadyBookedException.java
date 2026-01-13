package exception;
// lỗi khi cố sửa tour đã có ng book
public class TourAlreadyBookedException extends Exception {
    public TourAlreadyBookedException(String mess){
        super(mess);
    }
}
