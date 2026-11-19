/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

import com.mycompany.chatappprogpart2.Message;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author butsha
 */

/**
 * Unit tests for the Message class.
 * Part 2 tests verify the validation logic for message length, recipient numbers,
 * hash generation, and the different send/store options.
 * Part 3 tests verify the arrays, search, delete and report features using the
 * five test data messages from the project brief.
 */
public class MessageTest {

    // Temporary folder so the tests never touch the real messages.json file
    @TempDir
    Path tempFolder;

    private Map<String, Object> testData;

    @BeforeEach
    public void setUp() throws IOException {
        // Testdata.yml is the source of the five assignment messages and expected results.
        testData = loadTestData();

        // Start every test with empty arrays and a temporary storage file.
        Message.clearAllArrays();
        Message.setStorageFile(tempFolder.resolve("messages.json").toString());
        Message.setLoggedInUser("Developer");

        // Populate the arrays from the required YAML test data.
        populateMessagesThrough(5);
    }

    // Reads Testdata.yml safely. The GitHub Actions workflow runs Maven from the
    // project root, so this is the same file used locally and in GitHub.
    @SuppressWarnings("unchecked")
    private Map<String, Object> loadTestData() throws IOException {
        Path yamlPath = Paths.get("Testdata.yml");
        assertTrue(Files.exists(yamlPath), "Required Testdata.yml file is missing.");

        try (InputStream input = Files.newInputStream(yamlPath)) {
            Object loaded = new Yaml(new SafeConstructor(new LoaderOptions())).load(input);
            assertTrue(loaded instanceof Map, "Testdata.yml must contain a YAML mapping.");
            return (Map<String, Object>) loaded;
        }
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> yamlMessages() {
        return (List<Map<String, Object>>) testData.get("messages");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> yamlTestCase(String name) {
        Map<String, Object> cases = (Map<String, Object>) testData.get("testCases");
        return (Map<String, Object>) cases.get(name);
    }

    private String yamlString(String testCase, String field) {
        return String.valueOf(yamlTestCase(testCase).get(field));
    }

    @SuppressWarnings("unchecked")
    private List<String> yamlStringList(String testCase, String field) {
        return (List<String>) yamlTestCase(testCase).get(field);
    }

    // Uses the assignment's flag names from Testdata.yml:
    // SENT = 1, DISREGARDED = 2 and STORED = 3.
    private int flagNumber(String flag) {
        switch (flag) {
            case "SENT":
                return 1;
            case "DISREGARDED":
                return 2;
            case "STORED":
                return 3;
            default:
                throw new IllegalArgumentException("Unknown message flag in Testdata.yml: " + flag);
        }
    }

    private void populateMessagesThrough(int lastTestNumber) {
        for (Map<String, Object> row : yamlMessages()) {
            int testNumber = ((Number) row.get("testNumber")).intValue();
            if (testNumber > lastTestNumber) {
                continue;
            }

            String recipient = String.valueOf(row.get("recipient"));
            String text = String.valueOf(row.get("message"));
            String id = String.valueOf(row.get("messageID"));
            int messageNumber = ((Number) row.get("messageNumber")).intValue();
            int flag = flagNumber(String.valueOf(row.get("flag")));

            Message message = new Message(recipient, text, messageNumber);
            message.setMessageID(id);
            message.SentMessage(flag);
        }
    }

    // Re-populates only Test Data Messages 1 to 4 when a test requires that subset.
    private void addTestMessages1To4() {
        populateMessagesThrough(4);
    }

    // Collects the text of every message in an array
    private List<String> getTexts(ArrayList<Message> messages) {
        List<String> texts = new ArrayList<>();
        for (Message msg : messages) {
            texts.add(msg.getMessageText());
        }
        return texts;
    }

    // ==========================================
    // Part 2 tests
    // ==========================================

    @Test
    public void testMessageLengthSuccess() {
        // Create a valid message under 250 characters
        Message msg = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?", 0);
        
        // Verify that the method returns the exact success string
        assertEquals("Message ready to send.", msg.checkMessageLength());
    }

    @Test
    public void testMessageLengthFailure() {
        // Create a string of 'a's with exactly 260 characters 
        // (10 characters over the 250 limit)
        String longMsg = "a".repeat(260); 
        Message msg = new Message("+27718693002", longMsg, 0);
        
        // Verify that the method calculates the excess correctly (260 - 250 = 10)
        // and returns the required failure message
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.", msg.checkMessageLength());
    }

    @Test
    public void testRecipientCellSuccess() {
        // Create a message with a valid South African international number
        Message msg = new Message("+27718693002", "Hi", 0);
        
        // Verify the recipient is validated successfully
        assertEquals("Cell phone number successfully captured.", msg.checkRecipientCell());
    }

    @Test
    public void testRecipientCellFailure() {
        // Create a message with an invalid number (missing international code, too short)
        Message msg = new Message("08966553", "Hi", 0);
        
        // Verify that the invalid number triggers the correct error message
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.", msg.checkRecipientCell());
    }

    @Test
    public void testMessageHashGeneration() {
        // Create a message containing multiple words and punctuation
        Message msg = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?", 0);
        
        // Force the Message ID to start with "00" so the output perfectly matches 
        // the required test data output ("00:0:HITONIGHT")
        msg.setMessageID("0012345678");
        
        // Verify the hash format: first two digits of ID + message number + first and last words (uppercase)
        assertEquals("00:0:HITONIGHT", msg.getMessageHash());
    }

    @Test
    public void testMessageIDCreated() {
        // Create a new message
        Message msg = new Message("+27718693002", "Hi", 0);
        
        // Verify that a random 10-digit ID was successfully generated and passes validation
        assertTrue(msg.checkMessageID());
    }

    @Test
    public void testSentMessageOptions() {
        // Test Option 1: User selects "Send Message"
        Message msg1 = new Message("+27718693002", "Hi", 0);
        assertEquals("Message successfully sent.", msg1.SentMessage(1));
        
        // Test Option 2: User selects "Disregard Message"
        Message msg2 = new Message("+27718693002", "Hi", 1);
        assertEquals("Press 0 to delete the message.", msg2.SentMessage(2));
        
        // Test Option 3: User selects "Store Message"
        Message msg3 = new Message("+27718693002", "Hi", 2);
        assertEquals("Message successfully stored", msg3.SentMessage(3));
    }

    // ==========================================
    // Part 3 tests
    // ==========================================

    @Test
    public void testTestdataYamlContainsFiveMessagesAndAllRequiredPart3Tests() {
        assertEquals(5, yamlMessages().size());

        Map<String, Object> cases = (Map<String, Object>) testData.get("testCases");
        assertTrue(cases.containsKey("sentMessages"));
        assertTrue(cases.containsKey("disregardedMessages"));
        assertTrue(cases.containsKey("storedMessages"));
        assertTrue(cases.containsKey("messageIDs"));
        assertTrue(cases.containsKey("messageHashes"));
        assertTrue(cases.containsKey("longestStoredMessage"));
        assertTrue(cases.containsKey("searchByMessageID"));
        assertTrue(cases.containsKey("searchByRecipient"));
        assertTrue(cases.containsKey("deleteByMessageHash"));
        assertTrue(cases.containsKey("displayReport"));
    }

    @Test
    public void testSentMessagesArrayCorrectlyPopulated() {
        // Use test data messages 1 to 4 only
        Message.clearAllArrays();
        addTestMessages1To4();

        // Test data message 1 and 4 were flagged as Sent
        List<String> expected = yamlStringList("sentMessages", "expected");
        assertEquals(expected, getTexts(Message.returnSentMessages()));
    }

    @Test
    public void testDisregardedMessagesArrayCorrectlyPopulated() {
        // Test data message 3 was flagged as Disregarded
        List<String> expected = yamlStringList("disregardedMessages", "expected");
        assertEquals(expected, getTexts(Message.returnDisregardedMessages()));
    }

    @Test
    public void testStoredMessagesArrayCorrectlyPopulated() {
        // Test data message 2 and 5 were flagged as Stored
        List<String> expected = yamlStringList("storedMessages", "expected");
        assertEquals(expected, getTexts(Message.returnStoredMessages()));
    }

    @Test
    public void testMessageHashAndIDArraysCorrectlyPopulated() {
        // All five messages have an ID and a hash, in the same order (parallel arrays)
        List<String> expectedIDs = yamlStringList("messageIDs", "expected");
        List<String> expectedHashes = yamlStringList("messageHashes", "expected");

        assertEquals(expectedIDs, Message.returnMessageIDs());
        assertEquals(expectedHashes, Message.returnMessageHashes());
    }

    @Test
    public void testDisplayLongestMessage() {
        // Use test data messages 1 to 4 only
        Message.clearAllArrays();
        addTestMessages1To4();

        // Message 2 is the longest of the test data messages
        assertEquals(yamlString("longestStoredMessage", "expected"),
            Message.displayLongestMessage());
    }

    @Test
    public void testSearchForMessageID() {
        // The assignment's search test data specifies 0838884567 as the ID to search.
        String expected = "\"" + yamlString("searchByMessageID", "expectedRecipient")
                + "\", \"" + yamlString("searchByMessageID", "expectedMessage") + "\"";
        assertEquals(expected, Message.searchByMessageID(yamlString("searchByMessageID", "messageID")));
    }

    @Test
    public void testSearchForMessageIDNotFound() {
        assertEquals("Message ID not found.", Message.searchByMessageID("9999999999"));
    }

    @Test
    public void testSearchMessagesForRecipient() {
        // Message 2 and 5 were stored for +27838884567
        List<String> expected = yamlStringList("searchByRecipient", "expected");
        assertEquals(expected, Message.searchByRecipient(yamlString("searchByRecipient", "recipient")));
    }

    @Test
    public void testSearchMessagesForRecipientIgnoresDisregarded() {
        // Message 3 was disregarded, so it must not be found
        assertTrue(Message.searchByRecipient("+27834484567").isEmpty());
    }

    @Test
    public void testDeleteMessageUsingHash() {
        // Test data message 2 is deleted using its hash
        String result = Message.deleteByMessageHash(yamlString("deleteByMessageHash", "messageHash"));
        String expectedMessage = yamlString("deleteByMessageHash", "expectedDeletedMessage");

        assertEquals("Message: \"" + expectedMessage + "\" successfully deleted.", result);

        // The message must be gone from the flag array and from the parallel arrays
        assertEquals(List.of("Ok, I am leaving without you."), getTexts(Message.returnStoredMessages()));
        assertEquals(4, Message.returnMessageHashes().size());
        assertFalse(Message.returnMessageHashes().contains("00:1:WHERETIME"));
        assertEquals(List.of("0000000001", "0000000003", "0838884567", "0000000005"),
                Message.returnMessageIDs());
        assertEquals(List.of("00:0:DIDCAKE", "00:2:YOHOOOOGATE", "08:3:ITTIME", "00:4:OKYOU"),
                Message.returnMessageHashes());
        assertEquals("Message ID not found.", Message.searchByMessageID("0000000002"));
    }

    @Test
    public void testDeleteMessageHashNotFound() {
        assertEquals("Message hash not found.", Message.deleteByMessageHash("99:9:NOTHING"));
        assertEquals(5, Message.returnMessageHashes().size());
    }

    @Test
    public void testDeleteByHashOnlyDeletesStoredMessages() {
        String sentHash = Message.returnMessageHashes().get(0);
        String disregardedHash = Message.returnMessageHashes().get(2);

        assertEquals("Message is not stored.", Message.deleteByMessageHash(sentHash));
        assertEquals("Message is not stored.", Message.deleteByMessageHash(disregardedHash));

        assertEquals(5, Message.returnMessageIDs().size());
        assertEquals(5, Message.returnMessageHashes().size());
        assertEquals(2, Message.returnStoredMessages().size());
        assertTrue(Message.returnSentMessages().stream()
                .anyMatch(message -> message.getMessageID().equals("0000000001")));
        assertTrue(Message.returnDisregardedMessages().stream()
                .anyMatch(message -> message.getMessageID().equals("0000000003")));
    }

    @Test
    public void testDisplayReport() {
        // The assignment report must include hashes, recipients and messages for sent messages.
        String report = Message.displayReport();
        assertTrue(report.contains("--- Sent Messages Report ---"));

        for (String field : yamlStringList("displayReport", "requiredFields")) {
            assertTrue(report.contains(field));
        }
        for (String hash : yamlStringList("displayReport", "expectedSentHashes")) {
            assertTrue(report.contains(hash));
        }
        assertTrue(report.contains("Did you get the cake?"));
        assertTrue(report.contains("It is dinner time!"));
    }

    @Test
    public void testDisplaySenderAndRecipient() {
        String expected = "Sender: Developer | Recipient: +27838884567\n"
            + "Sender: Developer | Recipient: +27838884567";
        assertEquals(expected, Message.displaySenderAndRecipient());
    }

    @Test
    public void testDisplayStoredReportHasFullDetails() {
        String report = Message.displayStoredReport();

        assertTrue(report.contains("Message ID: 0000000002"));
        assertTrue(report.contains("Message Hash: 00:1:WHERETIME"));
        assertTrue(report.contains("Sender: Developer"));
        assertTrue(report.contains("Recipient: +27838884567"));
        assertTrue(report.contains("Message: Ok, I am leaving without you."));
    }

    @Test
    public void testLoadStoredMessagesFromJSONFile() {
        // Empty the arrays so the stored messages can only come from the JSON file
        Message.clearAllArrays();
        int loadedCount = Message.loadStoredMessages();

        assertEquals(2, loadedCount);
        assertEquals(List.of(
            "Where are you? You are late! I have asked you to be on time.",
            "Ok, I am leaving without you."), getTexts(Message.returnStoredMessages()));

        // The loaded messages also fill the ID and hash arrays
        assertEquals(List.of("0000000002", "0000000005"), Message.returnMessageIDs());
        assertEquals(List.of("00:1:WHERETIME", "00:4:OKYOU"), Message.returnMessageHashes());
    }

    @Test
    public void testDeletedMessageIsRemovedFromJSONFile() {
        Message.deleteByMessageHash("00:1:WHERETIME");

        // Reload the file: only message 5 should be left
        Message.clearAllArrays();
        assertEquals(1, Message.loadStoredMessages());
        assertEquals(List.of("Ok, I am leaving without you."), getTexts(Message.returnStoredMessages()));
    }

    @Test
    public void testStoredMessageWithQuotationMarksIsReadCorrectly() {
        Message.clearAllArrays();
        Message msg = new Message("+27834557896", "He said \"hello\" to me.", 0);
        msg.SentMessage(3);

        // Reload from the file and compare the text
        Message.clearAllArrays();
        Message.loadStoredMessages();
        assertEquals(List.of("He said \"hello\" to me."), getTexts(Message.returnStoredMessages()));
    }

    // ==========================================
    // Tests for the JSON file, message display, message counting
    // and multiple message scenarios
    // ==========================================

    // Reads the JSON file used by the tests
    private String readJSONFile() throws IOException {
        return Files.readString(tempFolder.resolve("messages.json")).trim();
    }

    // Counts how many times a piece of text appears in another text
    private int countOccurrences(String text, String search) {
        return text.split(java.util.regex.Pattern.quote(search), -1).length - 1;
    }

    @Test
    public void testJSONFileIsOneValidArray() throws IOException {
        String content = readJSONFile();

        // Two stored messages: one array with two objects separated by a comma
        assertTrue(content.startsWith("["));
        assertTrue(content.endsWith("]"));
        assertEquals(2, countOccurrences(content, "\"messageID\""));
        assertTrue(content.contains("},\n  {"));
    }

    @Test
    public void testJSONFileContents() throws IOException {
        String content = readJSONFile();

        assertTrue(content.contains("\"messageID\": \"0000000002\""));
        assertTrue(content.contains("\"messageHash\": \"00:1:WHERETIME\""));
        assertTrue(content.contains("\"sender\": \"Developer\""));
        assertTrue(content.contains("\"recipient\": \"+27838884567\""));
        assertTrue(content.contains("\"message\": \"Ok, I am leaving without you.\""));
    }

    @Test
    public void testOnlyStoredMessagesAreWrittenToJSONFile() throws IOException {
        String content = readJSONFile();

        // Message 1 and 4 were sent and message 3 was disregarded
        assertFalse(content.contains("Did you get the cake?"));
        assertFalse(content.contains("It is dinner time!"));
        assertFalse(content.contains("Yohoooo"));
    }

    @Test
    public void testJSONFileIsEmptyArrayAfterDeletingAllStoredMessages() throws IOException {
        Message.deleteByMessageHash("00:1:WHERETIME");
        Message.deleteByMessageHash("00:4:OKYOU");

        assertEquals("[]", readJSONFile());
    }

    @Test
    public void testSpecialCharactersAreStoredAndReadCorrectly() throws IOException {
        String specialText = "Line one\nLine two\ttabbed \"quoted\" back\\slash caf\u00e9";
        Message msg = new Message("+27834557896", specialText, 5);
        msg.SentMessage(3);

        // The new line must be escaped, so the value stays on one line in the file
        assertFalse(readJSONFile().contains("Line one\nLine two"));

        // Reload from the file and compare the text
        Message.clearAllArrays();
        Message.loadStoredMessages();
        assertEquals(3, Message.returnStoredMessages().size());
        assertEquals(specialText, Message.returnStoredMessages().get(2).getMessageText());
    }

    @Test
    public void testMultipleStoredMessagesAreAllReadFromJSONFile() {
        // Store three more messages, so the file has five messages
        for (int i = 5; i < 8; i++) {
            Message msg = new Message("+27834557896", "Stored message " + i, i);
            msg.SentMessage(3);
        }

        Message.clearAllArrays();
        assertEquals(5, Message.loadStoredMessages());
        assertEquals("Stored message 7", Message.returnStoredMessages().get(4).getMessageText());
    }

    @Test
    public void testLoadingTwiceDoesNotDuplicateMessages() {
        // The messages are already in the arrays, so nothing new is loaded
        assertEquals(0, Message.loadStoredMessages());
        assertEquals(2, Message.returnStoredMessages().size());
    }

    @Test
    public void testLoadWithoutJSONFile() {
        Message.clearAllArrays();
        Message.setStorageFile(tempFolder.resolve("doesNotExist.json").toString());

        assertEquals(0, Message.loadStoredMessages());
        assertTrue(Message.returnStoredMessages().isEmpty());
    }

    @Test
    public void testCompactJSONIsLoadedCorrectly() throws IOException {
        String compactJson = "[{\"messageID\":\"1234567890\",\"messageNumber\":7,"
                + "\"messageHash\":\"12:7:COMPACTJSON\",\"sender\":\"Tester\","
                + "\"recipient\":\"+27834557896\",\"message\":\"Compact JSON message\"}]";
        Files.writeString(tempFolder.resolve("messages.json"), compactJson);
        Message.clearAllArrays();

        assertEquals(1, Message.loadStoredMessages());
        assertEquals(List.of("Compact JSON message"), getTexts(Message.returnStoredMessages()));
        assertEquals(List.of("1234567890"), Message.returnMessageIDs());
    }

    @Test
    public void testMalformedJSONDoesNotCrashOrPopulateArrays() throws IOException {
        Files.writeString(tempFolder.resolve("messages.json"), "[{ this is not valid JSON");
        Message.clearAllArrays();

        assertEquals(0, Message.loadStoredMessages());
        assertTrue(Message.returnStoredMessages().isEmpty());
        assertTrue(Message.returnMessageIDs().isEmpty());
        assertTrue(Message.returnMessageHashes().isEmpty());
    }

    @Test
    public void testJSONRecordsWithMissingRequiredFieldsAreSkipped() throws IOException {
        String json = "["
                + "{\"messageID\":\"1111111111\",\"messageNumber\":1,"
                + "\"sender\":\"Tester\",\"message\":\"Missing recipient\"},"
                + "{\"messageID\":\"2222222222\",\"messageNumber\":2,"
                + "\"sender\":\"Tester\",\"recipient\":\"+27834557896\","
                + "\"message\":\"Valid stored message\"}"
                + "]";
        Files.writeString(tempFolder.resolve("messages.json"), json);
        Message.clearAllArrays();

        assertEquals(1, Message.loadStoredMessages());
        assertEquals(List.of("Valid stored message"), getTexts(Message.returnStoredMessages()));
        assertEquals(List.of("2222222222"), Message.returnMessageIDs());
        assertEquals(List.of("22:2:VALIDMESSAGE"), Message.returnMessageHashes());
    }

    @Test
    public void testPrintMessageDetails() {
        Message msg = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?", 0);
        msg.setMessageID("0012345678");

        // Capture what the method prints to the console
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput));
        try {
            msg.printMessageDetails();
        } finally {
            System.setOut(originalOutput);
        }

        String expected = "Message ID: 0012345678\n"
            + "Message Hash: 00:0:HITONIGHT\n"
            + "Recipient: +27718693002\n"
            + "Message: Hi Mike, can you join us for dinner tonight?\n";
        assertEquals(expected, capturedOutput.toString().replace("\r\n", "\n"));
    }

    @Test
    public void testMessagesEnteredCountsSentDisregardedAndStoredMessages() {
        // Test data: 2 sent, 1 disregarded and 2 stored
        assertEquals(5, Message.returnMessagesEntered());
        assertEquals(2, Message.returnTotalMessages());
    }

    @Test
    public void testDisregardedAndStoredMessagesDoNotChangeTotalSent() {
        new Message("+27834557896", "Hi", 5).SentMessage(2);
        new Message("+27834557896", "Hi", 6).SentMessage(3);

        assertEquals(7, Message.returnMessagesEntered());
        assertEquals(2, Message.returnTotalMessages());
    }

    @Test
    public void testInvalidChoiceIsNotCounted() {
        assertEquals("Invalid choice.", new Message("+27834557896", "Hi", 5).SentMessage(7));
        assertEquals(5, Message.returnMessagesEntered());
    }

    @Test
    public void testSearchMessagesForRecipientIncludesSentMessages() {
        assertEquals(List.of("Did you get the cake?"), Message.searchByRecipient("+27834557896"));
    }

    @Test
    public void testDisplayFeaturesWhenThereAreNoMessages() {
        Message.clearAllArrays();

        assertEquals("No messages have been sent yet.", Message.displayReport());
        assertEquals("No stored messages found.", Message.displayStoredReport());
        assertEquals("No stored messages found.", Message.displaySenderAndRecipient());
        assertEquals("", Message.displayLongestMessage());
    }
}
