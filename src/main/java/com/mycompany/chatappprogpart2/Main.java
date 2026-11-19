/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.chatappprogpart2;

/**
 *
 * @author butsha
 */


import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner input = new Scanner(System.in)) {
            System.out.println("WELCOME TO CHATAPP");
            System.out.println(); // Adds a blank line for better formatting
           
            
            // ask for the registration info
            System.out.print("Enter your first name: ");
            String firstName = input.nextLine();
            System.out.print("Enter your last name: ");
            String lastName = input.nextLine();
            System.out.print("Enter a username: ");
            String username = input.nextLine();
            System.out.print("Enter a password: ");
            String password = input.nextLine();
            System.out.print("Enter your South African cell phone number (+27...): ");
            String cellPhoneNumber = input.nextLine();
            
            Login login = new Login(firstName, lastName, username, password, cellPhoneNumber);
            
            // Execute registration and store the outcome message.
            String registrationMessage = login.registerUser();
            
            // Output the registration result to the console.
            System.out.println();
            System.out.println(registrationMessage);
            
            // Stop execution if registration failed to prevent login attempts.
            if (!login.isRegistered()) {
                System.out.println();
                System.out.println("Registration was not completed.");
                return;
            }           
            
            // Proceed to login only after successful registration.
            System.out.println();
            System.out.println("---- LOGIN ----");
            
            // Prompt for login credentials to verify against stored data.
            System.out.print("Enter your username : ");
            String loginUsername = input.nextLine();
            System.out.print("Enter your password to : ");
            String loginPassword = input.nextLine();
            
            // Record the user's login attempt in the Login object.
            login.setEnteredUsername(loginUsername);
            login.setEnteredPassword(loginPassword);
            
            // Validate credentials against stored registration data.
            // loginUser() returns true if the username and password match.
            boolean isLoggedIn = login.loginUser();
            
            // Retrieve and display the final login status message.
            String loginMessage = login.returnLoginStatus();
            System.out.println();
            System.out.println(loginMessage);
            
            // Stop execution if login failed
            if (!isLoggedIn) {
                return;
            }

            // ==========================================
            // --- QUICKCHAT PART 2 AND 3 PHASE ---
            // ==========================================
            System.out.println("\nWelcome to QuickChat.");

            // The logged in user is the sender of every new message
            Message.setLoggedInUser(login.getUsername());

            // Part 3: read the stored messages from the JSON file into the array
            int loadedCount = Message.loadStoredMessages();
            System.out.println(loadedCount + " stored message(s) loaded from the JSON file.");

            // Display the stored messages that were read from the JSON file
            if (loadedCount > 0) {
                System.out.println();
                System.out.println(Message.displayStoredReport());
            }
            
            System.out.print("How many messages do you wish to enter? ");
            int numMessages = input.nextInt();
            input.nextLine(); // Consume the leftover newline character

            boolean running = true;

            // Main application loop
            while (running) {
                System.out.println("\nMenu:");
                System.out.println("1) Send Messages");
                System.out.println("2) Show recently sent messages");
                System.out.println("3) Quit");
                System.out.println("4) Stored Messages");
                System.out.print("Choose an option: ");
                int choice = input.nextInt();
                input.nextLine();

                switch (choice) {
                    case 1:
                        // Check if the user has already entered the max amount
                        // (sent, disregarded and stored messages all count)
                        if (Message.returnMessagesEntered() >= numMessages) {
                            System.out.println("You have already entered your set number of messages.");
                            break;
                        }
                        
                        // Loop to process the specified number of messages
                        for (int i = Message.returnMessagesEntered(); i < numMessages; i++) {
                            System.out.println("\n--- Message " + (i + 1) + " of " + numMessages + " ---");
                            
                            System.out.print("Enter recipient cell number: ");
                            String recipient = input.nextLine();
                            System.out.print("Enter message: ");
                            String text = input.nextLine();

                            // Passes the loop counter 'i' into the Message constructor
                            // (i never repeats because it continues from the messages entered)
                            Message msg = new Message(recipient, text, i);

                            // Validates the message
                            String recCheck = msg.checkRecipientCell();
                            String lenCheck = msg.checkMessageLength();
                            
                            System.out.println(recCheck);
                            System.out.println(lenCheck);

                            // Proceed only if validation passed
                            if (recCheck.contains("successfully") && lenCheck.contains("ready")) {
                                System.out.println("Message ID generated: " + msg.getMessageID());
                                System.out.println("Message Hash: " + msg.getMessageHash());

                                int sendChoice = askSendChoice(input);

                                String sendResult = msg.SentMessage(sendChoice);
                                if (sendChoice == 2) {
                                    System.out.println("\nMessage disregarded.");
                                } else {
                                    System.out.println("\n" + sendResult);
                                }

                                // Print full details if message is sent
                                if (sendChoice == 1) {
                                    System.out.println("\n--- Message Details ---");
                                    msg.printMessageDetails();
                                }
                            } else {
                                System.out.println("Message could not be processed due to validation errors.");
                                break; // Exits the for loop if validation fails
                            }
                            
                            // Break out of the for loop if the limit is reached
                            if (Message.returnMessagesEntered() >= numMessages) {
                                System.out.println("\nYou have reached the maximum number of messages.");
                                break; 
                            }
                        }
                        break;

                    case 2:
                        // Report of all the messages sent (hash, recipient, message)
                        System.out.println();
                        System.out.println(Message.displayReport());
                        break;

                    case 3:
                        // Display final total before exiting
                        System.out.println("\nTotal messages sent: " + Message.returnTotalMessages());
                        System.out.println("Exiting QuickChat.");
                        running = false;
                        break;

                    case 4:
                        storedMessagesMenu(input);
                        break;

                    default:
                        System.out.println("Invalid option. Please choose 1, 2, 3, or 4.");
                }
            }
        }
    }

    // Asks the user to send, disregard or store the message and returns 1, 2 or 3.
    // A message is only disregarded after the user presses 0 to confirm.
    private static int askSendChoice(Scanner input) {
        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            System.out.print("> ");
            String sendChoice = input.nextLine().trim();

            if (sendChoice.equals("1") || sendChoice.equals("3")) {
                return Integer.parseInt(sendChoice);
            }

            if (sendChoice.equals("2")) {
                System.out.print("Press 0 to delete the message: ");
                if (input.nextLine().trim().equals("0")) {
                    return 2;
                }
                System.out.println("The message was not deleted.");
            } else {
                System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }
    }

    // Part 3: menu for the stored messages features
    private static void storedMessagesMenu(Scanner input) {
        boolean inStoredMenu = true;

        while (inStoredMenu) {
            System.out.println("\n--- Stored Messages ---");
            System.out.println("a) Display the sender and recipient of all stored messages");
            System.out.println("b) Display the longest stored message");
            System.out.println("c) Search for a message ID");
            System.out.println("d) Search for all the messages stored for a recipient");
            System.out.println("e) Delete a message using the message hash");
            System.out.println("f) Display a report of all stored messages");
            System.out.println("g) Back to the main menu");
            System.out.print("Choose an option: ");
            String option = input.nextLine().trim().toLowerCase();

            switch (option) {
                case "a":
                    System.out.println();
                    System.out.println(Message.displaySenderAndRecipient());
                    break;

                case "b":
                    String longestMessage = Message.displayLongestMessage();
                    System.out.println();
                    if (longestMessage.isEmpty()) {
                        System.out.println("No stored messages found.");
                    } else {
                        System.out.println("Longest stored message: " + longestMessage);
                    }
                    break;

                case "c":
                    System.out.print("Enter the message ID: ");
                    String searchID = input.nextLine().trim();
                    System.out.println();
                    System.out.println(Message.searchByMessageID(searchID));
                    break;

                case "d":
                    System.out.print("Enter the recipient cell number: ");
                    String searchRecipient = input.nextLine().trim();
                    ArrayList<String> foundMessages = Message.searchByRecipient(searchRecipient);
                    System.out.println();
                    if (foundMessages.isEmpty()) {
                        System.out.println("No messages found for that recipient.");
                    } else {
                        for (String foundMessage : foundMessages) {
                            System.out.println("- " + foundMessage);
                        }
                    }
                    break;

                case "e":
                    System.out.print("Enter the message hash: ");
                    String searchHash = input.nextLine().trim();
                    System.out.println();
                    System.out.println(Message.deleteByMessageHash(searchHash));
                    break;

                case "f":
                    System.out.println();
                    System.out.println(Message.displayStoredReport());
                    break;

                case "g":
                    inStoredMenu = false;
                    break;

                default:
                    System.out.println("Invalid option. Please choose a, b, c, d, e, f, or g.");
            }
        }
    }
}
