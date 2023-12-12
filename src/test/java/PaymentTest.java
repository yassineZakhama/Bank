import bank.Payment;
import bank.exceptions.TransactionAttributeException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

public class PaymentTest {

    Payment deposit;
    Payment depositCopy;
    Payment payout;
    @BeforeEach
    public void initialize() throws TransactionAttributeException {
        deposit = new Payment("04-12-2023", 200, "Deposit", 0.1, 0.1);
        depositCopy = new Payment(deposit);
        payout = new Payment("04-12-2023", -200, "Payout", 0.1, 0.1);
    }

    @Test
    public void testConstructor() {
        assertNotNull(deposit);

        assertEquals("04-12-2023", deposit.getDate());
        assertEquals(200, deposit.getAmount());
        assertEquals("Deposit", deposit.getDescription());
        assertEquals(0.1, deposit.getIncomingInterest());
        assertEquals(0.1, deposit.getOutgoingInterest());

        assertThrows(TransactionAttributeException.class, () -> new Payment("04-12-2023", 200, "Deposit", 1.5, 0.1));
    }

    @Test
    public void testCopyConstructor() {
        assertEquals(deposit.getDate(), depositCopy.getDate());
        assertEquals(deposit.getAmount(), depositCopy.getAmount());
        assertEquals(deposit.getDescription(), depositCopy.getDescription());
        assertEquals(deposit.getIncomingInterest(), depositCopy.getIncomingInterest());
        assertEquals(deposit.getOutgoingInterest(), depositCopy.getOutgoingInterest());
    }

    @Test
    public void testDepositCalculate() {
        double amount = deposit.getAmount();
        double expected = amount - amount * deposit.getIncomingInterest();

        assertEquals(expected, deposit.calculate());
    }

    @Test
    public void testPayoutCalculate() {
        double amount = payout.getAmount();
        double expected = amount + amount * payout.getIncomingInterest();

        assertEquals(expected, payout.calculate());
    }

    @Test
    public void testEquals() {
        assumeTrue(deposit.equals(deposit));

        assumeFalse(deposit.equals(null));

        String s = "...";
        assumeFalse(deposit.equals(s));

        assumeTrue(deposit.equals(depositCopy));

        assumeFalse(deposit.equals(payout));
    }

    @Test
    public void testToString() {
        String expected = "Date: " + deposit.getDate()
                + "\nAmount: " + deposit.getAmount()
                + "\nDescription: " + deposit.getDescription()
                + "\nIncoming interest: " + deposit.getIncomingInterest()
                + "\nOutgoing interest: " + deposit.getOutgoingInterest()
                + "\nAmount after interest: " + deposit.calculate();

        assertEquals(expected, deposit.toString());
    }
}
