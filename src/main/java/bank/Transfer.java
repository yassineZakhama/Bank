package bank;

import bank.exceptions.TransactionAttributeException;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Transfer is a subclass of Transaction and implements the interface CalculateBill
 */
public class Transfer extends Transaction implements CalculateBill{
    /**
     * Sender of the amount
     */
    private String sender;

    /**
     * Receiver of the amount
     */
    private String recipient;

    /**
     * Constructor
     * @param _date Time of the transfer in format DD.MM.YYYY
     * @param _amount Amount of the transfer, must be positive
     * @param _description Additional description of the transfer
     * @param _sender Sender of the amount
     * @param _recipient Receiver of the amount
     */
    public Transfer(String _date, double _amount, String _description, String _sender, String _recipient) throws TransactionAttributeException {
        super(_date, _amount, _description);
        setSender(_sender);
        setRecipient(_recipient);
    }

    /**
     * Copy constructor
     * @param other The transfer to copy
     */
    public Transfer(Transfer other) throws TransactionAttributeException {
        super(other);
        setSender(other.sender);
        setRecipient(other.recipient);
    }

    // Getters and setters
    public String getSender() { return sender; }
    public void setSender(String _sender) throws TransactionAttributeException {
        if(isValidName(_sender))
            throw new TransactionAttributeException("The sender's name is not a valid name, please enter again.");
        this.sender = _sender;
    }

    public String getRecipient() { return recipient; }
    public void setRecipient(String _recipient) throws TransactionAttributeException {
        if(isValidName(_recipient))
            throw new TransactionAttributeException("The recipient's name is not a valid name, please enter again.");
        this.recipient = _recipient;
    }

    @Override
    public void setAmount(double _amount) throws TransactionAttributeException {
        if(_amount < 0)
            throw new TransactionAttributeException("The amount in a payment must be positive, please try again.");
        this.amount = _amount;
    }

    /**
     * @return The amount unchanged
     */
    @Override
    public double calculate() {
        return getAmount();
    }

    /**
     * Stringifies a payment
     * @return a string that contains information of a payment
     */
    @Override
    public String toString(){
        return super.toString()
                + "\nSender: " + sender
                + "\nRecipient: " + recipient;
    }

    /**
     * Tests for equality of a Transfer object and another object
     * @param obj the object to test equality with a Transfer object
     * @return true if the two objects' attributes are the same
     */
    @Override
    public boolean equals(Object obj) {
        // same reference => equal
        if (this == obj) { return true;}

        // other is null or not the same class => not equal
        if (obj == null || getClass() != obj.getClass()) {return false;}

        // super class equals
        if (!super.equals(obj)) { return false; }

        // cast other to Transaction type
        Transfer otherTransfer = (Transfer) obj;

        // compare the attributes for equality
        return Objects.equals(this.sender, otherTransfer.sender)
                && Objects.equals(this.recipient, otherTransfer.recipient);
    }

    private boolean isValidName(String name) {
        String regex = "^[A-Za-z]\\w$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }
}
