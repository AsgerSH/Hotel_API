package app.core.exceptions;

public class HotelNotFoundException extends ApiException {
    public HotelNotFoundException(String message) {
        super(404, message);
    }
}
