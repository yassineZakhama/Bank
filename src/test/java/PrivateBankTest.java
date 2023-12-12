import bank.*;
import bank.exceptions.*;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

public class PrivateBankTest {

    PrivateBank bank;
    PrivateBank bankCopy;

    @BeforeEach
    public void initialize() throws TransactionAlreadyExistException, AccountAlreadyExistsException, TransactionAttributeException, IOException{
        bank = new PrivateBank("Bank", 0.1, 0.1, "src/test/testAccounts/");

        Payment p1 = new Payment("04-12-2023", 100, "description 1", 0.1, 0.1);
        Payment p2 = new Payment("04-12-2023", 100, "description 2", 0.1, 0.1);
        Payment p3 = new Payment("04-12-2023", 100, "description 3", 0.1, 0.1);
        IncomingTransfer it = new IncomingTransfer("04-12-2023", 200, "des", "Eeeee", "Hhhhh");
        OutgoingTransfer ot = new OutgoingTransfer("04-12-2023", 200, "desc", "Eeeee", "Hhhhh");

        List<Transaction> list = new ArrayList<>();
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(it);
        list.add(ot);

        bank.createAccount("test", list);

        bankCopy = new PrivateBank(bank);
    }

    @AfterEach
    public void cleanTestAccountsDirectory() {
        try {
            Path directory = Paths.get("src/test/testAccounts/");
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
                for (Path file : directoryStream) {
                    Files.delete(file);
                }
            }
            System.out.println("All files in the folder have been deleted.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testConstructor() {
        assertNotNull(bank);

        assertEquals("Bank", bank.getName());
        assertEquals(0.1, bank.getIncomingInterest());
        assertEquals(0.1, bank.getOutgoingInterest());
        assertEquals("src/test/testAccounts/", bank.getDirectoryName());

        assertThrows(TransactionAttributeException.class, () -> new PrivateBank("Test", 1.5, 0.1, "src/test/testAccounts/"));
    }

    @Test
    public void testCopyConstructor() {
        assertEquals(bank.getName(), bankCopy.getName());
        assertEquals(bank.getIncomingInterest(), bankCopy.getIncomingInterest());
        assertEquals(bank.getOutgoingInterest(), bankCopy.getOutgoingInterest());
        assertEquals(bank.getDirectoryName(), bankCopy.getDirectoryName());
    }

    @Test
    public void testEquals() {

        assumeTrue(bank.equals(bank));

        assumeFalse(bank.equals(null));

        String s = "...";
        assumeFalse(bank.equals(s));

        assumeTrue(bank.equals(bankCopy));
    }

    @Test
    public void testToString() {
        String expected = "Name: " + bank.getName()
                + "\nIncoming interest: " + bank.getIncomingInterest()
                + "\nOutgoing interest: " + bank.getOutgoingInterest()
                + "\nAccounts list: " + bank.getAccountsToTransactions();

        assertEquals(expected, bank.toString());
    }

    @Test
    public void testCreateDuplicateAccount() {
        assertThrows(AccountAlreadyExistsException.class, () -> bank.createAccount("test"));

        assertDoesNotThrow(() -> bank.createAccount("test2"));
    }

    @Test
    public void testCreateAccountWithDuplicateTransactions() throws TransactionAlreadyExistException, AccountAlreadyExistsException, TransactionAttributeException, IOException {

        Payment payment1 = new Payment("04-12-2023", 100, "description 1", 0.1, 0.1);
        Payment payment2 = new Payment("04-12-2023", 100, "description 1", 0.1, 0.1);

        List<Transaction> l = new ArrayList<>();
        l.add(payment1);
        l.add(payment2);

        assertThrows(TransactionAlreadyExistException.class, () -> bank.createAccount("test3", l));
    }

    @Test
    public void testAddDuplicateTransaction() {
        assertThrows(TransactionAlreadyExistException.class, () -> {
            bank.addTransaction("test", new Payment("04-12-2023", 100, "description 1", 0.1, 0.1));
        });
    }

    @Test
    public void testAddNonValidTransaction() {
        assertThrows(TransactionAttributeException.class, () -> {
            bank.addTransaction("test", new Payment("04-12-2023", 100, "description 1", 1.5, 0.1));
        });
    }

    @Test
    public void testAddTransactionToNonExistingAccount() {
        assertThrows(AccountDoesNotExistException.class, () -> {
            bank.addTransaction("test69", new Payment("04-12-2023", 100, "description 1", 0.1, 0.1));
        });
    }

    @Test
    public void testRemoveTransaction() throws TransactionAttributeException, AccountDoesNotExistException, TransactionDoesNotExistException, IOException {
        assumeTrue(bank.containsTransaction("test" ,new Payment("04-12-2023", 100, "description 1", 0.1, 0.1)));

        bank.removeTransaction("test", new Payment("04-12-2023", 100, "description 1", 0.1, 0.1));

        assumeFalse(bank.containsTransaction("test" ,new Payment("04-12-2023", 100, "description 1", 0.1, 0.1)));
    }

    @Test
    public void testRemoveTransactionFromNonExistingAccount() {
        assertThrows(AccountDoesNotExistException.class, () -> {
            bank.removeTransaction("test69", new Payment("04-12-2023", 100, "description 1", 0.1, 0.1));
        });
    }

    @Test
    public void testRemoveNonExistingTransaction() {
        assertThrows(TransactionDoesNotExistException.class, () -> {
            bank.removeTransaction("test", new Payment("04-12-2023", 10000, "description 1", 0.1, 0.1));
        });
    }

    @Test
    public void testContainsTransaction() throws TransactionAttributeException {
        assumeTrue(bank.containsTransaction("test" ,new Payment("04-12-2023", 100, "description 1", 0.1, 0.1)));
        assumeFalse(bank.containsTransaction("test" ,new Payment("04-12-2023", 10000, "description 1", 0.1, 0.1)));
    }

    @Test
    public void testGetAccountBalance() {
        double expected = (100 - 100 * 0.1) * 3 + 200 - 200;
        assertEquals(expected, bank.getAccountBalance("test"));
    }

    @Test
    public void testGetTransactions() throws TransactionAttributeException {
        Payment p1 = new Payment("04-12-2023", 100, "description 1", 0.1, 0.1);
        Payment p2 = new Payment("04-12-2023", 100, "description 2", 0.1, 0.1);
        Payment p3 = new Payment("04-12-2023", 100, "description 3", 0.1, 0.1);
        IncomingTransfer it = new IncomingTransfer("04-12-2023", 200, "des", "Eeeee", "Hhhhh");
        OutgoingTransfer ot = new OutgoingTransfer("04-12-2023", 200, "desc", "Eeeee", "Hhhhh");

        List<Transaction> list = new ArrayList<>();
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(it);
        list.add(ot);

        assertEquals(list, bank.getTransactions("test"));
    }

    @Test
    public void testGetTransactionsSortedAsc() throws TransactionAttributeException {
        Payment p1 = new Payment("04-12-2023", 100, "description 1", 0.1, 0.1);
        Payment p2 = new Payment("04-12-2023", 100, "description 2", 0.1, 0.1);
        Payment p3 = new Payment("04-12-2023", 100, "description 3", 0.1, 0.1);
        IncomingTransfer it = new IncomingTransfer("04-12-2023", 200, "des", "Eeeee", "Hhhhh");
        OutgoingTransfer ot = new OutgoingTransfer("04-12-2023", 200, "desc", "Eeeee", "Hhhhh");

        List<Transaction> list = new ArrayList<>();
        list.add(ot);
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(it);

        assertEquals(list, bank.getTransactionsSorted("test", true));
    }

    @Test
    public void testGetTransactionsSortedDesc() throws TransactionAttributeException {
        Payment p1 = new Payment("04-12-2023", 100, "description 1", 0.1, 0.1);
        Payment p2 = new Payment("04-12-2023", 100, "description 2", 0.1, 0.1);
        Payment p3 = new Payment("04-12-2023", 100, "description 3", 0.1, 0.1);
        IncomingTransfer it = new IncomingTransfer("04-12-2023", 200, "des", "Eeeee", "Hhhhh");
        OutgoingTransfer ot = new OutgoingTransfer("04-12-2023", 200, "desc", "Eeeee", "Hhhhh");

        List<Transaction> list = new ArrayList<>();
        list.add(it);
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(ot);

        assertEquals(list, bank.getTransactionsSorted("test", false));
    }

    @Test
    public void testGetTransactionsByTypePositive() throws TransactionAttributeException {
        Payment p1 = new Payment("04-12-2023", 100, "description 1", 0.1, 0.1);
        Payment p2 = new Payment("04-12-2023", 100, "description 2", 0.1, 0.1);
        Payment p3 = new Payment("04-12-2023", 100, "description 3", 0.1, 0.1);
        IncomingTransfer it = new IncomingTransfer("04-12-2023", 200, "des", "Eeeee", "Hhhhh");

        List<Transaction> list = new ArrayList<>();
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(it);

        assertEquals(list, bank.getTransactionsByType("test", true));
    }

    @Test
    public void testGetTransactionsByTypeNegative() throws TransactionAttributeException {
        OutgoingTransfer ot = new OutgoingTransfer("04-12-2023", 200, "desc", "Eeeee", "Hhhhh");

        List<Transaction> list = new ArrayList<>();
        list.add(ot);

        assertEquals(list, bank.getTransactionsByType("test", false));
    }

    @Test
    public void testSerialization() throws TransactionAlreadyExistException, AccountAlreadyExistsException, TransactionAttributeException, IOException, AccountDoesNotExistException {

        Path directory = Paths.get("src/test/testSerialization/");
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
            for (Path file : directoryStream) {
                Files.delete(file);
            }
        }

        PrivateBank b = new PrivateBank("Bank", 0.1, 0.1, "src/test/testSerialization/");
        b.createAccount("TEST");
        b.addTransaction("TEST", new Payment("04-12-2023", 200, "test", 0.1, 0.1));

        String content = new String(Files.readAllBytes(Paths.get(directory + "/TEST.json")));
        assertNotNull(content);

        String s = "[\n" +
                "  {\n" +
                "    \"CLASSNAME\": \"Payment\",\n"
                +"    \"INSTANCE\": {\n" +
                "      \"incomingInterest\": 0.1,\n" +
                "      \"outgoingInterest\": 0.1,\n" +
                "      \"date\": \"04-12-2023\",\n" +
                "      \"amount\": 200.0,\n" +
                "      \"description\": \"test\"\n"
                +"    }\n" +
                "  }\n" +
                "]";

        assertEquals(content, s);
    }

    @Test
    public void testDeserialization() throws TransactionAlreadyExistException, AccountAlreadyExistsException, TransactionAttributeException, IOException {
        PrivateBank b = new PrivateBank("Bank", 0.1, 0.1, "src/test/testSerialization/");
        assertTrue(b.containsTransaction("TEST", new Payment("04-12-2023", 200, "test", 0.1, 0.1)));
    }
}
