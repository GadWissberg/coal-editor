package view.dialogs;

public class InvalidValueException extends Throwable {
    public InvalidValueException(String invalidMessage) {
        super(invalidMessage);
    }
}
