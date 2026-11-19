/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chatappprogpart2;

/**
 *
 * @author butsha
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Message {
    // Instance variables for each specific message
    private String messageID;
    private int messageNumber;
    private String sender;
    private String recipient;
    private String messageText;
    private String messageHash;

    // Arrays that hold the messages according to their flag (Part 2 and Part 3)
    private static ArrayList<Message> sentMessages = new ArrayList<>();
    private static ArrayList<Message> disregardedMessages = new ArrayList<>();
    private static ArrayList<Message> storedMessages = new ArrayList<>();

    // Parallel arrays: the same index in each array refers to the same message
    private static ArrayList<Message> allMessages = new ArrayList<>();
    private static ArrayList<String> messageIDs = new ArrayList<>();
    private static ArrayList<String> messageHashes = new ArrayList<>();

    // Static variables to track data across the whole application session
    private static int totalMessages = 0;     // messages that were sent
    private static int messagesEntered = 0;   // every message sent, disregarded or stored
    private static String loggedInUser = "Developer";
    private static String storageFile = "messages.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public Message(String recipient, String messageText, int messageNumber) {
        // Generate a random 10-digit number for the ID
        long randomNum = (long) (Math.random() * 9000000000L) + 1000000000L;
        this.messageID = String.valueOf(randomNum);
        
        // Uses the loop counter passed from Main
        this.messageNumber = messageNumber;
        this.sender = loggedInUser;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = createMessageHash();
    }

    // Ensures ID is not empty and max 10 characters
    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    // Ensures recipient number is valid
    public String checkRecipientCell() {
        // Regex matches +27 or 0 followed by 9 digits
        if (recipient != null && recipient.matches("^(\\+27|0)[6-8]\\d{8}$")) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    }

    // Checks if message is under the 250 character limit
    public String checkMessageLength() {
        if (messageText.length() <= 250) {
            return "Message ready to send.";
        } else {
            // Calculates how many characters over the limit the message is
            int excess = messageText.length() - 250;
            return "Message exceeds 250 characters by " + excess + "; please reduce the size.";
        }
    }

    // Creates the hash using string manipulation
    public String createMessageHash() {
        if (messageText == null || messageText.trim().isEmpty()) return "";
        
        // Extract the first two characters of the ID
        String idPrefix = messageID.substring(0, 2);
        
        // Split the message into an array of words
        String[] words = messageText.trim().split("\\s+");
        
        // Remove punctuation from the first and last words
        String firstWord = words[0].replaceAll("[^a-zA-Z]", "");
        String lastWord = words[words.length - 1].replaceAll("[^a-zA-Z]", "");
        
        // Format the hash: XX:Y:FIRSTLAST and convert to uppercase
        return (idPrefix + ":" + messageNumber + ":" + (firstWord + lastWord)).toUpperCase();
    }

    // Handles the user's choice of sending, disregarding, or storing
    public String SentMessage(int choice) {
        switch (choice) {
            case 1:
                sentMessages.add(this);
                recordMessage();
                totalMessages++;
                messagesEntered++;
                return "Message successfully sent.";
            case 2:
                disregardedMessages.add(this);
                recordMessage();
                messagesEntered++;
                return "Press 0 to delete the message.";
            case 3:
                storedMessages.add(this);
                recordMessage();
                messagesEntered++;
                storeMessage();
                return "Message successfully stored";
            default:
                return "Invalid choice.";
        }
    }

    // Adds the message ID and hash to the parallel arrays (only once per message)
    private void recordMessage() {
        if (!allMessages.contains(this)) {
            allMessages.add(this);
            messageIDs.add(messageID);
            messageHashes.add(messageHash);
        }
    }

    // Returns the static total count of messages sent
    public static int returnTotalMessages() {
        return totalMessages;
    }

    // Returns how many messages the user has entered (sent, disregarded or stored).
    // This never goes down, so message numbers do not repeat.
    public static int returnMessagesEntered() {
        return messagesEntered;
    }

    // Stores the message by saving all the stored messages to the JSON file
    public void storeMessage() {
        saveStoredMessages();
    }

    // Writes all stored messages to the JSON file as one valid JSON array.
    // Gson handles quotes, backslashes, line breaks and other special characters.
    private static void saveStoredMessages() {
        JsonArray jsonMessages = new JsonArray();

        for (Message storedMessage : storedMessages) {
            JsonObject jsonMessage = new JsonObject();
            jsonMessage.addProperty("messageID", storedMessage.messageID);
            jsonMessage.addProperty("messageNumber", storedMessage.messageNumber);
            jsonMessage.addProperty("messageHash", storedMessage.messageHash);
            jsonMessage.addProperty("sender", storedMessage.sender);
            jsonMessage.addProperty("recipient", storedMessage.recipient);
            jsonMessage.addProperty("message", storedMessage.messageText);
            jsonMessages.add(jsonMessage);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(storageFile), StandardCharsets.UTF_8)) {
            GSON.toJson(jsonMessages, writer);
        } catch (IOException e) {
            System.out.println("Error storing messages to JSON: " + e.getMessage());
        }
    }

    // Reads a JSON string field. Missing fields or fields with another data type
    // return null so that an incomplete record can be skipped safely.
    private static String readJsonString(JsonObject jsonMessage, String fieldName) {
        JsonElement value = jsonMessage.get(fieldName);
        if (value == null || value.isJsonNull() || !value.isJsonPrimitive()
                || !value.getAsJsonPrimitive().isString()) {
            return null;
        }
        return value.getAsString();
    }

    // Reads every valid stored message from the JSON file into storedMessages.
    // The method returns the number of new messages loaded.
    public static int loadStoredMessages() {
        Path filePath = Paths.get(storageFile);

        if (!Files.exists(filePath)) {
            return 0;
        }

        int loadedCount = 0;

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            JsonElement root = JsonParser.parseReader(reader);

            // The assignment stores messages in a JSON array.
            if (root == null || !root.isJsonArray()) {
                System.out.println("The stored messages JSON file must contain an array.");
                return 0;
            }

            JsonArray jsonMessages = root.getAsJsonArray();

            for (JsonElement element : jsonMessages) {
                // Ignore array entries that are not JSON objects.
                if (element == null || !element.isJsonObject()) {
                    continue;
                }

                JsonObject jsonMessage = element.getAsJsonObject();
                String id = readJsonString(jsonMessage, "messageID");
                String fileSender = readJsonString(jsonMessage, "sender");
                String fileRecipient = readJsonString(jsonMessage, "recipient");
                String text = readJsonString(jsonMessage, "message");
                JsonElement numberElement = jsonMessage.get("messageNumber");

                // These fields are required to reconstruct the stored message.
                if (id == null || !id.matches("\\d{10}")
                        || fileSender == null || fileSender.isBlank()
                        || fileRecipient == null || fileRecipient.isBlank()
                        || text == null || text.isBlank()
                        || numberElement == null || numberElement.isJsonNull()
                        || !numberElement.isJsonPrimitive()
                        || !numberElement.getAsJsonPrimitive().isNumber()) {
                    continue;
                }

                int number;
                try {
                    number = numberElement.getAsInt();
                } catch (NumberFormatException | IllegalStateException e) {
                    continue;
                }

                if (number < 0 || messageIDs.contains(id)) {
                    continue;
                }

                // Rebuild the Message object and its hash from the saved values.
                Message loadedMessage = new Message(fileRecipient, text, number);
                loadedMessage.setMessageID(id);
                loadedMessage.sender = fileSender;

                storedMessages.add(loadedMessage);
                loadedMessage.recordMessage();
                loadedCount++;
            }
        } catch (IOException | JsonParseException | IllegalStateException e) {
            // A malformed file is reported without stopping the application.
            System.out.println("Error reading stored messages from JSON: " + e.getMessage());
            return 0;
        }

        return loadedCount;
    }

    // ==========================================
    // PART 3: Stored Messages menu features
    // ==========================================

    // a) Displays the sender and recipient of all stored messages
    public static String displaySenderAndRecipient() {
        if (storedMessages.isEmpty()) {
            return "No stored messages found.";
        }

        ArrayList<String> lines = new ArrayList<>();
        for (Message storedMessage : storedMessages) {
            lines.add("Sender: " + storedMessage.sender + " | Recipient: " + storedMessage.recipient);
        }
        return String.join("\n", lines);
    }

    // b) Returns the text of the longest stored message
    public static String displayLongestMessage() {
        String longestMessage = "";

        // Search the parallel arrays and only check the messages flagged as stored
        for (int i = 0; i < allMessages.size(); i++) {
            Message currentMessage = allMessages.get(i);
            boolean isStored = storedMessages.contains(currentMessage);
            if (isStored && currentMessage.messageText.length() > longestMessage.length()) {
                longestMessage = currentMessage.messageText;
            }
        }
        return longestMessage;
    }

    // c) Searches for a message ID and returns the recipient and message
    public static String searchByMessageID(String searchID) {
        // The index of the ID is the same index of the message (parallel arrays)
        int index = messageIDs.indexOf(searchID);
        if (index == -1) {
            return "Message ID not found.";
        }

        Message foundMessage = allMessages.get(index);
        // Output format from the brief: "recipient", "message"
        return "\"" + foundMessage.recipient + "\", \"" + foundMessage.messageText + "\"";
    }

    // d) Returns all the sent or stored messages for a particular recipient
    public static ArrayList<String> searchByRecipient(String searchRecipient) {
        ArrayList<String> foundMessages = new ArrayList<>();
        for (Message currentMessage : allMessages) {
            boolean isSentOrStored = sentMessages.contains(currentMessage)
                || storedMessages.contains(currentMessage);
            if (isSentOrStored && currentMessage.recipient.equals(searchRecipient)) {
                foundMessages.add(currentMessage.messageText);
            }
        }
        return foundMessages;
    }

    // e) Deletes a message using the message hash
    public static String deleteByMessageHash(String searchHash) {
        int index = messageHashes.indexOf(searchHash);
        if (index == -1) {
            return "Message hash not found.";
        }

        Message removedMessage = allMessages.get(index);

        // This option is in the Stored Messages menu, so only stored messages
        // may be deleted here. Sent and disregarded messages remain unchanged.
        if (!storedMessages.contains(removedMessage)) {
            return "Message is not stored.";
        }

        // Remove the same message from every parallel array and the stored array.
        allMessages.remove(index);
        messageIDs.remove(index);
        messageHashes.remove(index);
        storedMessages.remove(removedMessage);

        // Keep the JSON file up to date so the deleted message does not return.
        saveStoredMessages();
        // The brief shows the deleted message without its final full stop
        String deletedText = removedMessage.messageText;
        if (deletedText.endsWith(".")) {
            deletedText = deletedText.substring(0, deletedText.length() - 1);
        }
        return "Message: \"" + deletedText + "\" successfully deleted.";
    }

    // Report of all the sent messages: hash, recipient and message
    public static String displayReport() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent yet.";
        }

        ArrayList<String> lines = new ArrayList<>();
        lines.add("--- Sent Messages Report ---");
        for (Message sentMessage : sentMessages) {
            lines.add("");
            lines.add("Message Hash: " + sentMessage.messageHash);
            lines.add("Recipient: " + sentMessage.recipient);
            lines.add("Message: " + sentMessage.messageText);
        }
        return String.join("\n", lines);
    }

    // f) Report with the full details of all the stored messages
    public static String displayStoredReport() {
        if (storedMessages.isEmpty()) {
            return "No stored messages found.";
        }

        ArrayList<String> lines = new ArrayList<>();
        lines.add("--- Stored Messages Report ---");
        for (Message storedMessage : storedMessages) {
            lines.add("");
            lines.add("Message ID: " + storedMessage.messageID);
            lines.add("Message Hash: " + storedMessage.messageHash);
            lines.add("Sender: " + storedMessage.sender);
            lines.add("Recipient: " + storedMessage.recipient);
            lines.add("Message: " + storedMessage.messageText);
        }
        return String.join("\n", lines);
    }

    // Displays the details of a single message
    public void printMessageDetails() {
        System.out.println("Message ID: " + messageID);
        System.out.println("Message Hash: " + messageHash);
        System.out.println("Recipient: " + recipient);
        System.out.println("Message: " + messageText);
    }
    
    // Setter used for unit testing hash generation
    public void setMessageID(String id) {
        this.messageID = id;
        this.messageHash = createMessageHash(); // Update hash if ID changes
    }

    // Sets the name of the logged in user, used as the sender of new messages
    public static void setLoggedInUser(String username) {
        loggedInUser = username;
    }

    // Sets the file used to store messages (lets the unit tests use a temporary file)
    public static void setStorageFile(String fileName) {
        storageFile = fileName;
    }

    // Empties all the arrays so each unit test starts fresh
    public static void clearAllArrays() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        allMessages.clear();
        messageIDs.clear();
        messageHashes.clear();
        totalMessages = 0;
        messagesEntered = 0;
    }

    // Returns the array of sent messages
    public static ArrayList<Message> returnSentMessages() {
        return sentMessages;
    }

    // Returns the array of disregarded messages
    public static ArrayList<Message> returnDisregardedMessages() {
        return disregardedMessages;
    }

    // Returns the array of stored messages
    public static ArrayList<Message> returnStoredMessages() {
        return storedMessages;
    }

    // Returns the array of message IDs (parallel to the message hashes)
    public static ArrayList<String> returnMessageIDs() {
        return messageIDs;
    }

    // Returns the array of message hashes (parallel to the message IDs)
    public static ArrayList<String> returnMessageHashes() {
        return messageHashes;
    }
    
    // Returns the ID of this message
    public String getMessageID() {
        return messageID;
    }
    
    // Returns the hash of this message
    public String getMessageHash() {
        return messageHash;
    }

    // Returns the sender of this message
    public String getSender() {
        return sender;
    }

    // Returns the recipient cell number of this message
    public String getRecipient() {
        return recipient;
    }

    // Returns the text of this message
    public String getMessageText() {
        return messageText;
    }
}
