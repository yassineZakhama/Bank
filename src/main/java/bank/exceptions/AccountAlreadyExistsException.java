package bank.exceptions;

public class AccountAlreadyExistsException extends Exception{
    public AccountAlreadyExistsException(String err) {
        super(err);
    }
}
