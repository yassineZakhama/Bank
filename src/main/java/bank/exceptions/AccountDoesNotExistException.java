package bank.exceptions;

public class AccountDoesNotExistException extends Exception{
    public AccountDoesNotExistException(String err) {
        super(err);
    }
}
