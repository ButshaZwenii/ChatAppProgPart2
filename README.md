# ChatAppProgPart2 - QuickChat Application

##  Project Overview
Welcome to **ChatAppProgPart2**, a robust Java-based console application developed for the Programming 1A (PROG5121) module. This project is the second part of a larger application, focusing on building the core messaging functionality called "QuickChat."

The application simulates a real-world messaging environment where users must first securely register and authenticate. Once logged in, they gain access to the QuickChat menu, where they can compose, validate, send, disregard, or store messages to a local JSON database. The project emphasizes clean code architecture, robust data validation, string manipulation, object-oriented principles, and automated testing.

## Key Features & Functionality

### 1. Secure User Authentication (Part 1 Integration)
*   **Username Validation:** Ensures the username contains an underscore (`_`) and is no more than five characters long.
*   **Password Complexity:** Requires a minimum of eight characters, at least one capital letter, one number, and one special character.
*   **Cell Phone Validation:** Uses Regular Expressions (Regex) to ensure the cell number is a valid South African number, requiring the `+27` or `0` international code.

### 2. QuickChat Menu System
*   A fully interactive numeric menu displayed via a `while` loop that allows the user to:
    *   `1) Send Messages`: Initiates the message creation process.
    *   `2) Show recently sent messages`: Displays a report of all sent messages (message hash, recipient and message).
    *   `3) Quit`: Safely exits the application and displays the total number of messages sent during the session.
    *   `4) Stored Messages` (Part 3): Opens the stored messages menu described below.

### 3. Advanced Message Processing
*   **Message Limit:** Utilizes a `for` loop to allow the user to enter a specific, pre-defined number of messages. Sent, disregarded and stored messages all count towards the limit, and message numbers never repeat.
*   **Character Limit Validation:** Prevents the user from sending messages longer than 250 characters. If exceeded, the system returns the exact excess character count.
*   **Message ID Generation:** Automatically generates a unique, random 10-digit identifier for every message.
*   **Message Hash Generation:** This is a core feature built using advanced string manipulation. It combines the first two digits of the message ID, the loop counter (message number), and the first and last words of the message text (stripped of punctuation and converted to uppercase). Format: `XX:Y:FIRSTLAST`.
*   **Action Menu:** After composing, the user can choose to:
    *   **Send:** Adds the message to the session list and increments the global counter.
    *   **Disregard:** Cancels the message only after the user presses `0` to confirm.
    *   **Store:** Saves the message to a local `messages.json` file as one valid JSON array (special characters are escaped).

### 4. Stored Messages (Part 3)
*   **Arrays (no hard-coding):** The application populates arrays for sent messages, disregarded messages, stored messages, message hashes and message IDs. The stored messages array is filled by reading the `messages.json` file when the application starts, and the loaded messages are displayed to the user. The message ID and hash arrays are parallel arrays (the same index is the same message).
*   **Stored Messages menu:**
    *   `a)` Display the sender and recipient of all stored messages.
    *   `b)` Display the longest stored message.
    *   `c)` Search for a message ID and display the corresponding recipient and message.
    *   `d)` Search for all the sent or stored messages for a particular recipient.
    *   `e)` Delete a message using the message hash (the JSON file is updated too).
    *   `f)` Display a report with the full details of all stored messages.

##  Class Architecture
The application is divided into three core classes to maintain a clean separation of concerns:

1.  **`Main.java` (The Driver Class):** 
    *   Handles all user input and output via the `Scanner` class.
    *   Manages the flow of the application, from registration and login to the main QuickChat menu loops.
2.  **`Login.java` (The Authentication Class):** 
    *   Contains all the validation logic for user credentials.
    *   Manages the state of the user (registered vs. logged in).
3.  **`Message.java` (The Messaging Entity Class):** 
    *   Responsible for creating message objects.
    *   Contains the logic for ID generation, Hash creation, length validation, and JSON file writing.

##  Testing & Quality Assurance

This project uses JUnit 5 for the Login and Message classes.

* `src/test/java/LoginTest.java` tests registration and login.
* `src/test/java/MessageTest.java` tests message validation, the five required assignment messages, all Part 3 array/search/delete/report features, JSON storage and JSON loading.
* `Testdata.yml` is the assignment test-data file. `MessageTest.java` reads this file to populate the five messages and to obtain the expected results for the required Part 3 tests. It is therefore used during the Maven test run, not kept as an unused YAML document.

Run the complete test suite from the project root with:

```bash
mvn -B clean verify
```

The GitHub Actions workflow is `.github/workflows/testjava.yml`. It checks that `Testdata.yml` exists and runs the complete Maven test suite using JDK 17, matching the Java version configured in `pom.xml`.

##  Continuous Integration / Continuous Deployment (CI/CD)
The project is integrated with **GitHub Actions**. The workflow (`.github/workflows/testjava.yml`) runs on pushes and pull requests to `main` or `master`. It sets up JDK 17, confirms that `Testdata.yml` is present, and runs `mvn -B clean verify` so the full JUnit suite is executed.

## Installation & How to Run

### Prerequisites
*   Java Development Kit (JDK) 17 or higher installed.
*   Apache NetBeans IDE (recommended) or any Java IDE.
*   Apache Maven (included in NetBeans).

### Steps
1. Clone the repository to your local machine:
   ```bash
   git clone https://github.com/ButshaZwenii/ChatAppProgPart2.git

### Youtube Link
https://github.com/ButshaZwenii/ChatAppProgPart2.git




   


   
