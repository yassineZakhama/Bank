package bank;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Class for a custom serialisation of Transactions
 */
public class TransactionSerializer implements JsonSerializer<Transaction> {

    /**
     * Implementation of the method serialize of the interface JsonSerializer
     *
     * @param transaction the object to be serialized
     * @param type
     * @param jsonSerializationContext
     * @return JSON object with custom serialisation
     */
    @Override
    public JsonElement serialize(Transaction transaction, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        JsonObject jsonInstance = new JsonObject();

        if (transaction instanceof Payment payment) {

            jsonObject.addProperty("CLASSNAME", "Payment");

            jsonInstance.addProperty("incomingInterest", payment.getIncomingInterest());
            jsonInstance.addProperty("outgoingInterest", payment.getOutgoingInterest());
            jsonInstance.addProperty("date", payment.getDate());
            jsonInstance.addProperty("amount", payment.getAmount());
            jsonInstance.addProperty("description", payment.getDescription());

        } else if (transaction instanceof IncomingTransfer incomingTransfer) {

            jsonObject.addProperty("CLASSNAME", "IncomingTransfer");

            jsonInstance.addProperty("sender", incomingTransfer.getSender());
            jsonInstance.addProperty("recipient", incomingTransfer.getRecipient());
            jsonInstance.addProperty("date", incomingTransfer.getDate());
            jsonInstance.addProperty("amount", incomingTransfer.getAmount());
            jsonInstance.addProperty("description", incomingTransfer.getDescription());

        } else if (transaction instanceof OutgoingTransfer outgoingTransfer) {

            jsonObject.addProperty("CLASSNAME", "OutgoingTransfer");

            jsonInstance.addProperty("sender", outgoingTransfer.getSender());
            jsonInstance.addProperty("recipient", outgoingTransfer.getRecipient());
            jsonInstance.addProperty("date", outgoingTransfer.getDate());
            jsonInstance.addProperty("amount", outgoingTransfer.getAmount());
            jsonInstance.addProperty("description", outgoingTransfer.getDescription());

        }

        jsonObject.add("INSTANCE", jsonInstance);
        return jsonObject;
    }
}
