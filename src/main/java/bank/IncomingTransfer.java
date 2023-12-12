package bank;

import bank.exceptions.TransactionAttributeException;

/**
 * Specialization of a Transfer
 */
public class IncomingTransfer extends Transfer{
    /**
     * Constructor
     *
     * @param _date        Time of the transfer in format DD.MM.YYYY
     * @param _amount      Amount of the transfer, must be positive
     * @param _description Additional description of the transfer
     * @param _sender      Sender of the amount
     * @param _recipient   Receiver of the amount
     */
    public IncomingTransfer(String _date, double _amount, String _description, String _sender, String _recipient) throws TransactionAttributeException {
        super(_date, _amount, _description, _sender, _recipient);
    }

    /**
     * Copy constructor
     *
     * @param other The transfer to copy
     */
    public IncomingTransfer(Transfer other) throws TransactionAttributeException {
        super(other);
    }

    @Override
    public double calculate() {
        return super.calculate();
    }
}
