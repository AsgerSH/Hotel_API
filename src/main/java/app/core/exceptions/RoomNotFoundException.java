package app.core.exceptions;

public class RoomNotFoundException extends ApiException {
    public RoomNotFoundException(String message) {
        super(404, message);
    }
}
