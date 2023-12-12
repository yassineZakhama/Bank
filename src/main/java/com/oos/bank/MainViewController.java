package com.oos.bank;

import bank.PrivateBank;
import bank.exceptions.AccountAlreadyExistsException;
import bank.exceptions.TransactionAlreadyExistException;
import bank.exceptions.TransactionAttributeException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {
    @FXML GridPane mainPane;
    @FXML ListView<String> accountsListView;
    @FXML MenuItem selectAccountMenuItem;
    @FXML MenuItem deleteAccountMenuItem;
    @FXML Button addAccountButton;

    private Stage stage;
    private final ObservableList<String> accountsList = FXCollections.observableArrayList();
    private final PrivateBank myBank = new PrivateBank("My Bank", 0, 0, "accounts/");


    public MainViewController() throws TransactionAlreadyExistException, AccountAlreadyExistsException, TransactionAttributeException, IOException {
    }

    private void updateListView(){
        accountsList.clear();
        accountsList.addAll(myBank.getAllAccounts());
        accountsList.sort(Comparator.naturalOrder());
        accountsListView.setItems(accountsList);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        updateListView();
    }


    public void selectAccount(ActionEvent actionEvent) throws IOException {
        String selectedAccount = accountsListView.getSelectionModel().getSelectedItem();

        stage = (Stage) mainPane.getScene().getWindow();

        FXMLLoader fxmlLoader=new FXMLLoader(MainViewController.class.getResource("account-view.fxml"));
        mainPane = fxmlLoader.load();

        AccountViewController accountViewController = fxmlLoader.getController();
        accountViewController.setData(myBank, selectedAccount);

        Scene scene=new Scene(mainPane);
        stage.setTitle(selectedAccount);
        stage.setScene(scene);
        stage.show();
    }

    public void deleteAccount(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("");
        alert.setHeaderText("Are you sure you want to delete the account?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            try {
                String selectedAccount = accountsListView.getSelectionModel().getSelectedItem();
                myBank.deleteAccount(selectedAccount);
                updateListView();
            } catch (Exception e) {
                showExceptionDialog(e.getMessage());
            }
        }
    }

    public void addAccount(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("ADD NEW ACCOUNT");
        dialog.setHeaderText("Please enter the accounts name:");
        dialog.setContentText("Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            try {
                myBank.createAccount(name);
                updateListView();
            } catch (Exception e) {
                showExceptionDialog(e.getMessage());
            }
        });
    }

    private void showExceptionDialog(String err) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(err);
        alert.showAndWait();
    }
}
