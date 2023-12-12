import bank.IncomingTransfer;
import bank.OutgoingTransfer;
import bank.Transfer;

import bank.exceptions.TransactionAttributeException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

public class TransferTest {

    Transfer transfer;
    Transfer transferCopy;

    Transfer incomingTransfer;
    Transfer outgoingTransfer;


    @BeforeEach
    public void initialize() throws TransactionAttributeException {
        transfer = new Transfer("04-12-2023", 200, "Transfer", "Eeee", "Hhhh");
        transferCopy = new Transfer(transfer);

        incomingTransfer = new IncomingTransfer(transfer);
        outgoingTransfer = new OutgoingTransfer(transfer);
    }

    @Test
    public void testConstructor() {
        assertNotNull(transfer);

        assertEquals("04-12-2023", transfer.getDate());
        assertEquals(200, transfer.getAmount());
        assertEquals("Transfer", transfer.getDescription());
        assertEquals("Eeee", transfer.getSender());
        assertEquals("Hhhh", transfer.getRecipient());

        assertThrows(TransactionAttributeException.class, () -> new Transfer("04-12-2023", -200, "Transfer", "Eeee", "Hhhh"));
    }

    @Test
    public void testCopyConstructor() {
        assertEquals(transfer.getDate(), transferCopy.getDate());
        assertEquals(transfer.getAmount(), transferCopy.getAmount());
        assertEquals(transfer.getDescription(), transferCopy.getDescription());
        assertEquals(transfer.getSender(), transferCopy.getSender());
        assertEquals(transfer.getRecipient(), transferCopy.getRecipient());
    }

    @Test
    public void testTransferCalculate() {
        assertEquals(transfer.getAmount(), transfer.calculate());

        assertEquals(transfer.calculate(), incomingTransfer.calculate());
        assertEquals(transfer.calculate(), -outgoingTransfer.calculate());
    }


    @Test
    public void testEquals() {
        assumeTrue(transfer.equals(transfer));

        assumeFalse(transfer.equals(null));

        String s = "...";
        assumeFalse(transfer.equals(s));

        assumeTrue(transfer.equals(transferCopy));

        assumeFalse(transfer.equals(incomingTransfer));

        assumeFalse(incomingTransfer.equals(outgoingTransfer));
    }

    @Test
    public void testToString() {
        String expected = "Date: " + transfer.getDate()
                + "\nAmount: " + transfer.getAmount()
                + "\nDescription: " + transfer.getDescription()
                + "\nSender: " + transfer.getSender()
                + "\nRecipient: " + transfer.getRecipient();

        assertEquals(expected, transfer.toString());
    }
}
