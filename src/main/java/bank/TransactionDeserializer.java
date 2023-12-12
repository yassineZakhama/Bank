package bank;

import com.google.gson.*;

import java.lang.reflect.Type;

public class TransactionDeserializer implements JsonDeserializer<Transaction> {
    /**
     * Implementation of the method deserialize of the interface JsonSerializer
     *
     * @param jsonElement result of the deserialization
     * @param type
     * @param jsonDeserializationContext
     * @return the deserialized transaction
     * @throws JsonParseException class not found exception
     */
    @Override
    public Transaction deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        JsonObject instance = (JsonObject) jsonObject.get("INSTANCE");
        String className = jsonObject.get("CLASSNAME").getAsString();

        return switch (className) {
            case "Payment" -> new Gson().fromJson(instance, Payment.class);
            case "OutgoingTransfer" -> new Gson().fromJson(instance, OutgoingTransfer.class);
            case "IncomingTransfer" -> new Gson().fromJson(instance, IncomingTransfer.class);
            default -> throw new JsonParseException("Unknown class name.");
        };
    }
}
