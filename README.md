# Text-Me 🔒💬

I wanted to make a side-project that would combine Networking, Database management and Development into one project to help me learn them. So, I made **Text-me.** Currently, the UI is very barebones as the main focus of this project was more of the backend.

##  - Features

*   **Secure User Authentication:** Robust password storage system to protect user accounts.
*   **End-to-End Encryption:** Ensuring messages remain private between users. 
*   **Real-Time Messaging:** Instant message exchange with contacts.
*   **Modern User Interface:** Clean and intuitive UI built with JavaFX and MaterialFX.
*   **Cross-Platform:** Runs on any system with Java installed.
*   **Register UI:** Welcoming sign-up UI. 
*   **Forget password, email OTP and verify email: (SOON)** 
*   **User Profiles & Settings: (SOON)** 
*   **File Sharing: (PLANNED)** 
*   **Group chats: (PLANNED)**

## - Libraries & Tech Used

*   **Core:** Java 21
*   **UI:** JavaFX 23
*   **UI Library:** [MaterialFX](https://github.com/palexdev/MaterialFX) for a more modern look.
*   **Database:** SQLite
*   **Authentication:** JSON WebTokens using [Auth0 Java-JWT](https://github.com/auth0/java-jwt)
*   **Encryption:** Messages are encrypted using hybrid encryption using RSA for the encryption of AES keys and AES for the encryption of the messages.
* **Password Storage:** The passwords are hashed with a SHA-256 hashing algorithm and are salted.
*   **Networking:** [jakarta WebSockets](https://projects.eclipse.org/projects/ee4j.websocket) for the server & [TooTallNate's Java Websocket](https://github.com/TooTallNate/Java-WebSocket?tab=readme-ov-file) for the client.

## - Previews

*Main Chat Interface:*

![Main Chat UI](previews/31.gif)

*Contact List & Search:*

![Contact List UI](previews/21.gif)

*Login Screen:*

![Login UI](previews/11.gif)

## - Getting Started

If you would like to check out my project, please follow the following instructions.

### Prerequisites

*   Java Development Kit (JDK) Version `21` or above
*   Apache Maven installed (if you are going to run from source.)


### Run the Pre-Release
If you want to run the pre-built release version of the application download the server and the client release [here.](https://github.com/Youssef-codin/Text-Me/releases/tag/v0.1.0-alpha) 
    
* Open a terminal and navigate to the directory where the server .jar file is located


 1. Run the Server:
    ```bash
    java -jar Text-Me-Server.jar
     ```

 2. Run the Client:
    ```bash
    java -jar Text-Me-Client.jar
    ```

### Run from source

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/Youssef-codin/Text-Me.git
    cd Text-Me
    ```

2.  **Database Setup:**
    * The database file is included. However, you can always make your own SQLite DB and run the script.sql file under `database/script.sql`


3.  **Run the application:**
    1.  Run the **server** under **`arch.joe.server.ChatServer`**
        <details>
        <summary>Show command</summary>

        ```bash
        mvn exec:java -Dexec.mainClass="arch.joe.server.ChatServer"
        ```
        </details>

    2.  To **login** or register and start chatting, run **`arch.joe.Main`**
        <details>
        <summary>Show command</summary>

        ```bash
        mvn exec:java -Dexec.mainClass="arch.joe.Main"
        ```
        </details>

    Of course, you could also run these through an IDE (Vs Code, Intellij, etc.)

### Build from source

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/Youssef-codin/Text-Me.git
    cd Text-Me
    ```

2.  **Database Setup:**
    * The database file is included. However, you can always make your own SQLite DB and run the script.sql file under `database/script.sql`

3. **Build it:**
    1. To build the server package:
    ```
    mvn clean package -Pserver
    ```

    2. To build the server package ***(make sure to move the server package before building a new one so it doesn't overwrite it)***:
    ```
    mvn clean package -Pclient
    ```
4. **Run it:**
    ```
    java -jar (the-jar-file).jar
    ```
## - Known Bugs:
* If  the client doesn't receive a message, the client will freeze up until it receives a message. 

## - License

This project is licensed under the MIT license.
