package bank;

import bank.exceptions.TransactionAttributeException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Base class for Payment and Transfer
 */
 public abstract class Transaction implements CalculateBill{
    /**
     * Time of the transfer (deposit or withdrawal) in format DD.MM.YYYY
     */
    private String date;

    /**
     * Amount of the transfer
     */
    protected double amount;

    /**
     * Additional description of the transfer
     */
    private String description;

    /**
     * Constructor
     *
     * @param _date Time of the transfer (deposit or withdrawal) in format DD.MM.YYYY
     * @param _amount Amount of the transfer
     * @param _description Additional description of the transfer
     */
    public Transaction(String _date, double _amount, String _description) throws TransactionAttributeException {
        setDate(_date);
        setAmount(_amount);
        description = _description;
    }

    /**
     * Default constructor
     */
    public Transaction() {
        date = "02-10-2023";
        amount = 0;
        description = "";
    }

    /**
     * Copy constructor
     *
     * @param other The transaction to copy
     */
    public Transaction(Transaction other) throws TransactionAttributeException {
        this.setDate(other.date);
        this.setAmount(other.amount);
        this.description = other.description;
    }

    // Setters and getters
    public String getDate() { return date; }
    public void setDate(String _date) throws TransactionAttributeException {
        String dateFormat = "dd-MM-yyyy";
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
            LocalDate date = LocalDate.parse(_date, formatter);
        } catch (Exception e) {
            throw new TransactionAttributeException("Please enter the date in the format dd-MM-yyyy. Example: 31-12-2023. Try again.");
        }
        this.date = _date;
    }

    public double getAmount() { return amount; }
    abstract public void setAmount(double amount) throws TransactionAttributeException;

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    /**
     * Tests for equality of a Transaction object and another object
     *
     * @param obj the object to test equality with a Transaction object
     * @return true if the two objects' attributes are the same
     */
    @Override
    public boolean equals(Object obj) {
        // same reference => equal
        if (this == obj) { return true;}

        // other is null or not the same class => not equal
        if (obj == null || getClass() != obj.getClass()) {return false;}

        // cast other to Transaction type
        Transaction otherTransaction = (Transaction) obj;

        // compare the attributes for equality
        return Objects.equals(this.date, otherTransaction.date)
                && this.amount == otherTransaction.amount
                && Objects.equals(this.description, otherTransaction.description);
    }

    /**
     * Stringifies a transaction
     *
     * @return a string that contains information of a Transaction
     */
    @Override
    public String toString(){
        return "Date: " + date
                + "\nAmount: " + amount
                + "\nDescription: " + description;
    }
}
