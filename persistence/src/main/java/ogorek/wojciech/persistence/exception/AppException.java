package ogorek.wojciech.persistence.exception;

public class AppException extends RuntimeException {

    private final String message;

    public AppException(String message){
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
