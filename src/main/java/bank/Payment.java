package bank;

import bank.exceptions.TransactionAttributeException;

/**
 * Payment is a subclass of Transaction and implements the interface CalculateBill
 */
public class Payment extends Transaction implements CalculateBill{
    /**
     * Interest on a deposit, must be a positive number
     */
    private double incomingInterest;

    /**
     * Interest on a withdrawal, must be positive number
     */
    private double outgoingInterest;

    /**
     * Constructor
     *
     * @param _date Time of the payment (deposit or withdrawal) in format DD.MM.YYYY
     * @param _amount Amount of the payment
     * @param _description Additional description of the payment
     * @param _incomingInterest Interest on a deposit, must be a positive number
     * @param _outgoingInterest Interest on a payout, must be positive number
     */
    public Payment(String _date, double _amount, String _description, double _incomingInterest, double _outgoingInterest) throws TransactionAttributeException {
        super(_date, _amount, _description);

        setIncomingInterest(_incomingInterest);
        setOutgoingInterest(_outgoingInterest);
    }

    /**
     *  Copy constructor
     *
     * @param other The payment to copy
     */
    public Payment(Payment other) throws TransactionAttributeException {
        super(other.getDate(), other.amount, other.getDescription());
        setIncomingInterest(other.incomingInterest);
        setOutgoingInterest(other.outgoingInterest);
    }

    // Setters and getters
    public double getIncomingInterest() { return incomingInterest; }
    public void setIncomingInterest(double _incomingInterest) throws TransactionAttributeException {
         if (_incomingInterest < 0 || _incomingInterest > 1){
             throw new TransactionAttributeException("Incoming interest must be a number between 0 and 1.");
        } else {
             incomingInterest = _incomingInterest;
        }
    }

    public double getOutgoingInterest() { return outgoingInterest; }
    public void setOutgoingInterest(double _outgoingInterest) throws TransactionAttributeException {
        if (_outgoingInterest < 0 || _outgoingInterest > 1){
            throw new TransactionAttributeException("Outgoing interest must be a number between 0 and 1.");
        } else {
            outgoingInterest = _outgoingInterest;
        }
    }

    @Override
    public void setAmount(double _amount) {
        this.amount = _amount;
    }

    /**
     * Implementation of the interface "calculateBill"
     * Calculates the amount after interest. If the amount is positive and therefore represents a deposit, the value of
     * "incomingInterest" should be deducted as a percentage from the amount, otherwise it's a payout and the value of
     * "outgoingInterest" should be added as a percentage to the amount
     *
     * @return The amount after interest.
     */
    @Override
    public double calculate() {
        double amount = getAmount();
        if(amount > 0) {
            return amount - amount * incomingInterest;
        }
        return  amount + amount * outgoingInterest;
    }

    /**
     * Stringifies a payment
     *
     * @return a string that contains information of a payment
     */
    @Override
    public String toString(){
        return super.toString()
                + "\nIncoming interest: " + incomingInterest
                + "\nOutgoing interest: " + outgoingInterest
                + "\nAmount after interest: " + calculate();
    }

    /**
     * Tests for equality of a Payment object and another object
     *
     * @param obj the object to test equality with a Payment object
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
        Payment otherPayment = (Payment) obj;

        // compare the attributes for equality
        return this.incomingInterest == otherPayment.incomingInterest
                && this.outgoingInterest == otherPayment.outgoingInterest;
    }
}
