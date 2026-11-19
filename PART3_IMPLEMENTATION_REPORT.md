# Part 3 implementation and rubric report

## Files updated

- `Testdata.yml` — added the five assignment messages and the expected data for the required Part 3 test cases. The file is read by `MessageTest.java`, so it is part of the actual test run.
- `src/main/java/com/mycompany/chatappprogpart2/Message.java` — replaced the manual line-by-line JSON reader/writer with Gson JSON parsing and writing. The reader validates required fields, skips incomplete records, handles invalid JSON without terminating the application, and avoids loading duplicate message IDs. Deletion by hash is restricted to stored messages and removes the message from the stored, all-message, message-ID and message-hash arrays together.
- `src/test/java/MessageTest.java` — changed the test setup to load the five required messages from `Testdata.yml`; changed the Message 4 ID search test to search for `0838884567`; added tests for the YAML test data, compact JSON, malformed JSON, incomplete JSON records, and preventing deletion of sent/disregarded messages. Existing tests were retained.
- `pom.xml` — added Gson for JSON processing and SnakeYAML for reading the assignment test data in JUnit tests. Configured Java 17 compilation and Maven Surefire for JUnit 5.
- `.github/workflows/testjava.yml` — kept the existing GitHub Actions workflow, added a check for `Testdata.yml`, and configured it to run `mvn -B clean verify` on pushes and pull requests to `main` or `master`.
- `README.md` — documented the test-data file and how to run the test suite.

## Part 3 rubric coverage

| Rubric requirement | Implementation in this project |
|---|---|
| Arrays correctly populated | `MessageTest` populates the sent, disregarded, stored, message-ID and message-hash arrays from the five records in `Testdata.yml`. |
| Display longest stored message | Existing `displayLongestMessage()` retained and tested against the assignment expected message. |
| Search messages by recipient | Existing `searchByRecipient()` retained and tested using the recipient and expected messages in `Testdata.yml`. |
| Delete message using hash | `deleteByMessageHash()` only removes stored messages and updates the corresponding arrays and JSON file. |
| Read JSON file into an array | `loadStoredMessages()` now parses the JSON array with Gson and validates each record. |
| Display message report | Existing sent-message report and stored-message full report retained and tested. |
| Unit tests | Existing tests retained; additional tests cover YAML data and JSON edge cases. |
| Automated unit tests | GitHub Actions runs the Maven/JUnit suite, and those tests load `Testdata.yml`. |
| Application integration | Existing Part 1 and Part 2 source files and main menu were left unchanged. |
| Coding standards | Changes are limited to the assignment requirements and include explanatory comments. |

## Message 4 test-data interpretation

The Part 3 unit-test instruction explicitly gives `0838884567` as the value to search for in the Message ID test. The fixture now uses that value as Message 4's message ID. The recipient remains `0838884567`, matching the original project test fixture. The generated ID for normal application messages remains random.

## Verification status

- `Testdata.yml` was successfully parsed and checked: it contains all five assignment messages and ten test-case sections.
- `pom.xml` was checked for valid XML.
- Java source and test syntax was checked with Java 17 using temporary compile-time API stubs for external libraries.
- **The actual Maven/JUnit suite was not run in this environment** because Maven is not installed and Maven Central could not be reached to download dependencies. The workflow is configured to run the real suite on GitHub, but a green GitHub Actions result still needs to be confirmed after pushing the project.

Do not treat the tests as passed until GitHub Actions completes successfully.
