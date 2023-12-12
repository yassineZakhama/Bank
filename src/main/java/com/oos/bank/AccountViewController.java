package com.oos.bank;

import bank.*;
import bank.exceptions.TransactionAttributeException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AccountViewController {
    private final ObservableList<Transaction> transactionsList = FXCollections.observableArrayList();
    private PrivateBank myBank;
    private String currentAccount;

    @FXML GridPane mainPane;
    @FXML Label accountsNameLabel;
    @FXML Label currentAccountBalanceLabel;
    @FXML MenuItem paymentMenuItem;
    @FXML MenuItem ascendingMenuItem;
    @FXML MenuItem descendingMenuItem;
    @FXML MenuItem positiveMenuItem;
    @FXML MenuItem negativeMenuItem;
    @FXML Button backButton;
    @FXML ListView<Transaction> transactionsListView;
    @FXML ContextMenu deleteTransactionContextMenuItem;

    private void updateListView(List<Transaction> _transactionsList) {
        if(_transactionsList == null)
            return;

        transactionsList.clear();
        transactionsList.addAll(_transactionsList);
        transactionsListView.setItems(transactionsList);
    }

    public void setData(PrivateBank bank, String account) {

        myBank = bank;
        currentAccount = account;
        accountsNameLabel.setText(account);
        currentAccountBalanceLabel.setText(bank.getAccountBalance(account) + " €");
        updateListView(myBank.getTransactions(currentAccount));
    }

    public void showNewPaymentDialog(ActionEvent actionEvent) {
        Dialog<Transaction> dialog = new Dialog<>();
        dialog.getDialogPane().setMinWidth(250);
        dialog.getDialogPane().setMinHeight(120);
        dialog.setTitle("NEW PAYMENT");

        createNewPaymentUI(dialog);
    }

    public void showNewTransferDialog(ActionEvent actionEvent) {
        Dialog<Transaction> dialog = new Dialog<>();
        dialog.getDialogPane().setMinWidth(260);
        dialog.getDialogPane().setMinHeight(190);
        dialog.setTitle("NEW TRANSFER");

        createNewTransferUI(dialog);
    }

    public void sortTransactionsAscending(ActionEvent actionEvent) {
        updateListView(myBank.getTransactionsSorted(currentAccount, true));
    }

    public void sortTransactionsDescending(ActionEvent actionEvent) {
        updateListView(myBank.getTransactionsSorted(currentAccount, false));
    }

    public void showPositiveTransactions(ActionEvent actionEvent) {
        updateListView(myBank.getTransactionsByType(currentAccount, true));
    }

    public void showNegativeTransactions(ActionEvent actionEvent) {
        updateListView(myBank.getTransactionsByType(currentAccount, false));
    }

    public void back(ActionEvent actionEvent) throws IOException {
        mainPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main-view.fxml")));

        Stage stage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(mainPane);
        stage.setScene(scene);
        stage.show();
    }

    public void deleteTransaction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("");
        alert.setHeaderText("Are you sure you want to delete the transaction?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            try {
                Transaction selectedTransaction = transactionsListView.getSelectionModel().getSelectedItem();
                myBank.removeTransaction(currentAccount, selectedTransaction);
                updateListView(myBank.getTransactions(currentAccount));
            } catch (Exception e) {
                showExceptionDialog(e.getMessage());
            }
        }
    }

    private void showExceptionDialog(String err) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(err);
        alert.showAndWait();
    }

    private void createNewTransferUI(Dialog<Transaction> dialog) {
        GridPane userInputGridPane = new GridPane();

        Label senderLabel = new Label("Sender:");
        TextField senderTextField = new TextField();
        userInputGridPane.add(senderLabel, 0, 0);
        userInputGridPane.add(senderTextField, 1, 0);

        Label recipientLabel = new Label("Recipient:");
        TextField recipientTextField = new TextField();
        userInputGridPane.add(recipientLabel, 0, 1);
        userInputGridPane.add(recipientTextField, 1, 1);

        Label amountLabel = new Label("Amount:");
        TextField amountTextField = new TextField();
        userInputGridPane.add(amountLabel, 0, 2);
        userInputGridPane.add(amountTextField, 1, 2);

        Label dateLabel = new Label("Date (d-m-y):");
        TextField dateTextField = new TextField();
        userInputGridPane.add(dateLabel, 0, 3);
        userInputGridPane.add(dateTextField, 1, 3);

        Label descriptionLabel = new Label("Description:");
        TextField descriptionTextField = new TextField();
        userInputGridPane.add(descriptionLabel, 0, 4);
        userInputGridPane.add(descriptionTextField, 1, 4);

        // DIALOG CONTENT
        ButtonType addButton = new ButtonType("ADD", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(addButton);

        GridPane dialogContent = new GridPane();
        dialogContent.add(userInputGridPane, 0, 0);

        dialog.getDialogPane().setContent(dialogContent);

        // FUNCTIONALITY
        dialog.show();

        dialog.setResultConverter(buttonType -> {
            if (buttonType==addButton){
                try {
                    String date = dateTextField.getText();
                    String amount = amountTextField.getText();
                    validateDateAndAmount(date, amount);

                    String sender = senderTextField.getText();
                    String recipient = recipientTextField.getText();
                    validateSenderAndRecipient(sender, recipient);

                    if(Objects.equals(currentAccount, recipient)) {
                        IncomingTransfer transfer = new IncomingTransfer(date, Double.parseDouble(amount), descriptionTextField.getText(), sender, recipient);
                        myBank.addTransaction(currentAccount, transfer);
                    } else {
                        OutgoingTransfer transfer = new OutgoingTransfer(date, Double.parseDouble(amount), descriptionTextField.getText(), sender, recipient);
                        myBank.addTransaction(currentAccount, transfer);
                    }
                }catch (Exception e){
                    showExceptionDialog(e.getMessage());
                }

                updateListView(myBank.getTransactions(currentAccount));
                currentAccountBalanceLabel.setText(myBank.getAccountBalance(currentAccount) + " €");
            }
            return null;
        });
    }

    private void createNewPaymentUI(Dialog<Transaction> dialog) {
        GridPane userInputGridPane = new GridPane();

        Label amountLabel = new Label("Amount:");
        TextField amountTextField = new TextField();
        userInputGridPane.add(amountLabel, 0, 0);
        userInputGridPane.add(amountTextField, 1, 0);

        Label dateLabel = new Label("Date (d-m-y):");
        TextField dateTextField = new TextField();
        userInputGridPane.add(dateLabel, 0, 1);
        userInputGridPane.add(dateTextField, 1, 1);

        Label descriptionLabel = new Label("Description:");
        TextField descriptionTextField = new TextField();
        userInputGridPane.add(descriptionLabel, 0, 2);
        userInputGridPane.add(descriptionTextField, 1, 2);

        // DIALOG CONTENT
        ButtonType addButton = new ButtonType("ADD", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(addButton);


        GridPane dialogContent = new GridPane();
        dialogContent.add(userInputGridPane, 0, 0);

        dialog.getDialogPane().setContent(dialogContent);

        // FUNCTIONALITY
        dialog.show();

        dialog.setResultConverter(buttonType -> {
            if (buttonType==addButton){
                try {
                    String date = dateTextField.getText();
                    String amount = amountTextField.getText();
                    validateDateAndAmount(date, amount);
                    Payment payment = new Payment(date,
                            Double.parseDouble(amount),
                            descriptionTextField.getText(),
                            myBank.getIncomingInterest(),
                            myBank.getOutgoingInterest());

                    myBank.addTransaction(currentAccount, payment);
                }catch (Exception e){
                    showExceptionDialog(e.getMessage());
                }

                updateListView(myBank.getTransactions(currentAccount));
                currentAccountBalanceLabel.setText(myBank.getAccountBalance(currentAccount) + " €");
            }
            return null;
        });
    }

    private void validateDateAndAmount(String date, String amount) throws TransactionAttributeException {
        if (Objects.equals(date, ""))
            throw new TransactionAttributeException("The date must not be empty, please enter again.");

        try {
            Double.parseDouble(amount);
        } catch (Exception e) {
            throw new TransactionAttributeException("The entered amount is not valid, please try again.");
        }
    }

    private void validateSenderAndRecipient(String sender, String recipient) throws TransactionAttributeException {
        if (Objects.equals(sender, ""))
            throw new TransactionAttributeException("The sender must no be empty, please enter again");

        if (Objects.equals(recipient, ""))
            throw new TransactionAttributeException("The recipient must no be empty, please enter again");
    }
}
