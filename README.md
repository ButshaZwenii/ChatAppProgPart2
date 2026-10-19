# ChatAppProgPart2 - QuickChat Application

## 📖 Project Overview
Welcome to **ChatAppProgPart2**, a robust Java-based console application developed for the Programming 1A (PROG5121) module. This project is the second part of a larger application, focusing on building the core messaging functionality called "QuickChat."

The application simulates a real-world messaging environment where users must first securely register and authenticate. Once logged in, they gain access to the QuickChat menu, where they can compose, validate, send, disregard, or store messages to a local JSON database. The project emphasizes clean code architecture, robust data validation, string manipulation, object-oriented principles, and automated testing.

## ✨ Key Features & Functionality

### 1. Secure User Authentication (Part 1 Integration)
*   **Username Validation:** Ensures the username contains an underscore (`_`) and is no more than five characters long.
*   **Password Complexity:** Requires a minimum of eight characters, at least one capital letter, one number, and one special character.
*   **Cell Phone Validation:** Uses Regular Expressions (Regex) to ensure the cell number is a valid South African number, requiring the `+27` or `0` international code.

### 2. QuickChat Menu System
*   A fully interactive numeric menu displayed via a `while` loop that allows the user to:
    *   `1) Send Messages`: Initiates the message creation process.
    *   `2) Show recently sent messages`: Displays a "Coming Soon" placeholder.
    *   `3) Quit`: Safely exits the application and displays the total number of messages sent during the session.

### 3. Advanced Message Processing
*   **Message Limit:** Utilizes a `for` loop to allow the user to enter a specific, pre-defined number of messages.
*   **Character Limit Validation:** Prevents the user from sending messages longer than 250 characters. If exceeded, the system returns the exact excess character count.
*   **Message ID Generation:** Automatically generates a unique, random 10-digit identifier for every message.
*   **Message Hash Generation:** This is a core feature built using advanced string manipulation. It combines the first two digits of the message ID, the loop counter (message number), and the first and last words of the message text (stripped of punctuation and converted to uppercase). Format: `XX:Y:FIRSTLAST`.
*   **Action Menu:** After composing, the user can choose to:
    *   **Send:** Adds the message to the session list and increments the global counter.
    *   **Disregard:** Cancels the message and prompts for deletion.
    *   **Store:** Saves the message details to a local `messages.json` file for later retrieval.

## 🏗️ Class Architecture
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

## 🧪 Testing & Quality Assurance
This project includes a comprehensive suite of unit tests written using **JUnit 5**. 

*   **`LoginTest.java`:** Tests both successful and failed registration and login attempts, verifying exact string outputs and boolean states.
*   **`MessageTest.java`:** Tests the validation logic (e.g., ensuring messages over 250 characters throw the correct error) and verifies that the Message Hash is generated correctly using test data (`00:0:HITONIGHT`).

## ⚙️ Continuous Integration / Continuous Deployment (CI/CD)
The project is integrated with **GitHub Actions**. A custom workflow file (`.github/workflows/testjava.yml`) is configured to trigger on every `push` or `pull_request` to the `main` branch. It sets up a Java environment (JDK 17) and automatically runs `mvn test` to ensure that no new code breaks the existing unit tests.

## 🚀 Installation & How to Run

### Prerequisites
*   Java Development Kit (JDK) 17 or higher installed.
*   Apache NetBeans IDE (recommended) or any Java IDE.
*   Apache Maven (included in NetBeans).

### Steps
1. Clone the repository to your local machine:
   ```bash
   git clone https://github.com/ButshaZwenii/ChatAppProgPart2.git

### Youtube Link
https://youtu.be/R9Zhe1Djp3Q?si=a9d_Bh_XZv6ewHRr




   


   
