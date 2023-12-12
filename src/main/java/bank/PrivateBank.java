package bank;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import bank.exceptions.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;


/**
 * Class that implements the interface Bank
 */
public class PrivateBank implements Bank{
    /**
     * Name of the bank
     */
    private String name;

    /**
     * Interest on a deposit, must be a positive number
     */
    private double incomingInterest;

    /**
     * Interest on a withdrawal, must be positive number
     */
    private double outgoingInterest;

    /**
     * Name of the directory where the accounts are saved as JSON files
     */
    private String directoryName;
    /**
     * This attribute is intended to associate accounts with transactions so that each stored account
     * can be associated with 0 to n transactions.
     * The key represents the name of the account.
     */
    private Map<String, List<Transaction>> accountsToTransactions = new HashMap<>();

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

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
        if (_outgoingInterest < 0 || _outgoingInterest > 1)
            throw new TransactionAttributeException("Outgoing interest must be a number between 0 and 1.");
        else
            outgoingInterest = _outgoingInterest;
    }

    public String getDirectoryName() { return directoryName; }
    public void setDirectoryName(String _directoryName) { directoryName = _directoryName; }

    public Map<String, List<Transaction>> getAccountsToTransactions() { return accountsToTransactions; }

    /**
     * Constructor
     *
     * @param _name Name of the bank
     * @param _incomingInterest Interest on a deposit, must be a positive number
     * @param _outgoingInterest Interest on a withdrawal, must be positive number
     */
    public PrivateBank(String _name, double _incomingInterest, double _outgoingInterest, String _directoryName) throws TransactionAttributeException, TransactionAlreadyExistException, AccountAlreadyExistsException, IOException {
        name = _name;
        setIncomingInterest(_incomingInterest);
        setOutgoingInterest(_outgoingInterest);
        directoryName = _directoryName; // "src/main/java/Accounts/"

        readAccounts();
    }

    /**
     * Copy constructor
     * @param other The private bank to be copied
     */
    public PrivateBank(PrivateBank other) throws TransactionAttributeException {
        this.name = other.name;
        this.setIncomingInterest(other.incomingInterest);
        this.setOutgoingInterest(other.outgoingInterest);
        this.accountsToTransactions = new HashMap<>(other.accountsToTransactions);
        this.directoryName = other.getDirectoryName();
    }

    /**
     * Tests for equality of a PrivateBank object and another object
     * @param obj the object to test equality with a PrivateBank object
     * @return true if the two objects' attributes are the same
     */
    @Override
    public boolean equals(Object obj) {
        // same reference => equal
        if (this == obj) { return true;}

        // other is null or not the same class => not equal
        if (obj == null || getClass() != obj.getClass()) {return false;}

        // cast other to Transaction type
        PrivateBank other = (PrivateBank) obj;

        // compare the attributes for equality
        return Objects.equals(this.name, other.name)
                && this.incomingInterest == other.incomingInterest
                && this.outgoingInterest == other.outgoingInterest
                && Objects.equals(this.accountsToTransactions, other.accountsToTransactions);
                //&& Objects.equals(this.directoryName, other.directoryName);
    }

    /**
     * Stringifies a PrivateBank
     * @return a string that contains information of a PrivateBank
     */
    @Override
    public String toString() {
        return "Name: " + name
                + "\nIncoming interest: " + incomingInterest
                + "\nOutgoing interest: " + outgoingInterest
                + "\nAccounts list: " + accountsToTransactions;
    }

    /**
     * Adds an account to the bank.
     *
     * @param account the account to be added
     * @throws AccountAlreadyExistsException if the account already exists
     */
    @Override
    public void createAccount(String account) throws AccountAlreadyExistsException, IOException {
        if(accountsToTransactions.containsKey(account)){
            throw new AccountAlreadyExistsException("The account '" + account + "' already exists!");
        }
        else {
            this.accountsToTransactions.put(account, new ArrayList<>());
            writeAccount(account);
        }
    }

    /**
     * Adds an account (with specified transactions) to the bank.
     * Important: duplicate transactions must not be added to the account!
     *
     * @param account      the account to be added
     * @param transactions a list of already existing transactions which should be added to the newly created account
     * @throws AccountAlreadyExistsException    if the account already exists
     * @throws TransactionAlreadyExistException if the transaction already exists
     * @throws TransactionAttributeException    if the validation check for certain attributes fail
     */
    @Override
    public void createAccount(String account, List<Transaction> transactions) throws AccountAlreadyExistsException, TransactionAlreadyExistException, TransactionAttributeException, IOException {

        if(accountsToTransactions.containsKey(account)){
            throw new AccountAlreadyExistsException("The account '" + account + "' already exists!");
        } else {
            List<Transaction> temp = new ArrayList<>();
            for (Transaction t : transactions) {
                if (!temp.contains(t)) {
                    temp.add(t);
                } else {
                    throw new TransactionAlreadyExistException("The list of transactions contains duplicate transactions!");
                }
            }
            accountsToTransactions.put(account, temp);
            writeAccount(account);
        }
    }

    /**
     * Adds a transaction to an already existing account.
     *
     * @param account     the account to which the transaction is added
     * @param transaction the transaction which should be added to the specified account
     * @throws TransactionAlreadyExistException if the transaction already exists
     * @throws AccountDoesNotExistException     if the specified account does not exist
     * @throws TransactionAttributeException    if the validation check for certain attributes fail
     */
    @Override
    public void addTransaction(String account, Transaction transaction) throws TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException, IOException {

        if(!accountsToTransactions.containsKey(account)) {
            throw new AccountDoesNotExistException("Cannot add a transaction to a non existing account!");
        } else {
            if (!containsTransaction(account, transaction)) {
                if(transaction instanceof Payment) {
                    ((Payment) transaction).setIncomingInterest(this.incomingInterest);
                    ((Payment) transaction).setOutgoingInterest(this.outgoingInterest);
                }
                accountsToTransactions.get(account).add(transaction);
                writeAccount(account);
            } else {
                throw new TransactionAlreadyExistException("Transaction already exists!");
            }

        }
    }

    /**
     * Removes a transaction from an account. If the transaction does not exist, an exception is
     * thrown.
     *
     * @param account     the account from which the transaction is removed
     * @param transaction the transaction which is removed from the specified account
     * @throws AccountDoesNotExistException     if the specified account does not exist
     * @throws TransactionDoesNotExistException if the transaction cannot be found
     */
    @Override
    public void removeTransaction(String account, Transaction transaction) throws AccountDoesNotExistException, TransactionDoesNotExistException, IOException {

        if(!accountsToTransactions.containsKey(account)) {
            throw new AccountDoesNotExistException("Account does not exist!");
        } else {
            if(!containsTransaction(account, transaction)) {
                throw new TransactionDoesNotExistException("Transaction does not exist!");
            } else {
                accountsToTransactions.get(account).remove(transaction);
                writeAccount(account);
            }
        }
    }

    /**
     * Checks whether the specified transaction for a given account exists.
     *
     * @param account     the account from which the transaction is checked
     * @param transaction the transaction to search/look for
     * @return true if the transaction already exists
     */
    @Override
    public boolean containsTransaction(String account, Transaction transaction) {
        return accountsToTransactions.containsKey(account) && accountsToTransactions.get(account).contains(transaction);
    }

    /**
     * Calculates and returns the current account balance.
     *
     * @param account the selected account
     * @return the current account balance
     */
    @Override
    public double getAccountBalance(String account) {
        double balance = 0.0;

        if (accountsToTransactions.containsKey(account)) {
            for (Transaction t : accountsToTransactions.get(account)) {
                balance += t.calculate();
            }
        }

        return balance;
    }

    /**
     * Returns a list of transactions for an account.
     *
     * @param account the selected account
     * @return the list of all transactions for the specified account
     */
    @Override
    public List<Transaction> getTransactions(String account) {
        if(accountsToTransactions.isEmpty())
            return null;
        return new ArrayList<Transaction>(this.accountsToTransactions.get(account));
    }

    /**
     * Returns a sorted list (-> calculated amounts) of transactions for a specific account. Sorts the list either in ascending or descending order
     * (or empty).
     *
     * @param account the selected account
     * @param asc     selects if the transaction list is sorted in ascending or descending order
     * @return the sorted list of all transactions for the specified account
     */
    @Override
    public List<Transaction> getTransactionsSorted(String account, boolean asc) {
        if(accountsToTransactions.isEmpty())
            return null;

        if(this.accountsToTransactions.containsKey(account)) {
            List<Transaction> transactionsSorted = getTransactions(account);
            if (transactionsSorted.isEmpty())
                return null;

            if (asc) {
                transactionsSorted.sort(Comparator.comparingDouble(Transaction::calculate));
            } else {
                transactionsSorted.sort(Comparator.comparingDouble(Transaction::calculate).reversed());
            }
            return transactionsSorted;
        }

        return null;
    }

    /**
     * Returns a list of either positive or negative transactions (-> calculated amounts).
     *
     * @param account  the selected account
     * @param positive selects if positive or negative transactions are listed
     * @return the list of all transactions by type
     */
    @Override
    public List<Transaction> getTransactionsByType(String account, boolean positive) {
        if(accountsToTransactions.isEmpty())
            return null;

        if(accountsToTransactions.containsKey(account)) {
            List<Transaction> transactionsFiltered = new ArrayList<>();

            if(positive) {
                for(Transaction t : accountsToTransactions.get(account)) {
                    if (t.calculate() > 0)
                        transactionsFiltered.add(t);
                }
            } else {
                for(Transaction t : accountsToTransactions.get(account)) {
                    if (t.calculate() < 0)
                        transactionsFiltered.add(t);
                }
            }
            return transactionsFiltered;
        }
        return null;
    }

    /**
     * Reads all existing accounts from the file system and make them available in the 'AccountsToTransactions' attribute
     */
    private void readAccounts() throws IOException, TransactionAlreadyExistException, AccountAlreadyExistsException, TransactionAttributeException {

        // GsonBuilder allows the configuration of the gson parser's behaviour during the serialization and deserialization processes
        GsonBuilder gsonBuilder = new GsonBuilder();

        // This method is used to register a custom deserializer for a specific type,
        // The first parameter is the type of the object that requires a custom serialization
        // The second parameter is an implementation of the JsonDeserializer interface
        // in this case we are telling gson to use our customized deserializer on 'Transaction' objects
        gsonBuilder.registerTypeAdapter(Transaction.class, new TransactionDeserializer());

        File file = new File(directoryName); // the directory where the serialized accounts are

        if(file.listFiles() != null) { // Directory is not empty
            for (File f : file.listFiles()) {
                // read all content
                String json = new String(Files.readAllBytes(Paths.get(directoryName + f.getName())));

                // extract the type
                Type type = new TypeToken<List<Transaction>>() {}.getType();

                // extract the data from json string and put it in a list
                List<Transaction> transactionList = gsonBuilder.create().fromJson(json, type);

                createAccount(f.getName().replaceAll(".json", ""), transactionList);
            }
        }
    }

    /**
     * Persists (serializes and then saves) the specified account in the file system
     */
    private void writeAccount(String account) throws IOException {

        // The custom transaction serializer returns a json object with 2 json objects CLASSNAME and INSTANCE
        TransactionSerializer transactionSerializer = new TransactionSerializer();

        // We put all the transactions (in json notation) in a list
        List<JsonElement> jsonElements = new ArrayList<>();
        for(Transaction t :  accountsToTransactions.get(account))
            jsonElements.add(transactionSerializer.serialize(t, null, null));

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        FileWriter fileWriter = new FileWriter(directoryName + account + ".json");
        fileWriter.write(gson.toJson(jsonElements));
        fileWriter.close();
    }

    /**
     * Removes an account from the bank
     * @param account the account to delete
     * @throws AccountDoesNotExistException if the specified account does not exist
     * @throws IOException if a problem occurs while reading a file
     */
    public void deleteAccount(String account) throws AccountDoesNotExistException, IOException {
        if(!accountsToTransactions.containsKey(account)) {
            throw new AccountDoesNotExistException("Account '" + account + "' does not exist!");
        } else {

            File file = new File("accounts/" + account + ".json");
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("File deleted successfully.");
                } else {
                    throw new IOException("Unable to delete the file.");

                }
            } else {
                throw new IOException("File not found.");
            }

            accountsToTransactions.remove(account);
        }
    }

    /**
     * Returns a list with all existing account names
     * @return the list with all account names
     */
    public List<String> getAllAccounts() {
        Set<String> setKey = accountsToTransactions.keySet();
        return new ArrayList<>(setKey);
    }

}