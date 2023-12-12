package bank.exceptions;

public class TransactionAlreadyExistException extends Exception{
    public TransactionAlreadyExistException(String err) {
        super(err);
    }
}
