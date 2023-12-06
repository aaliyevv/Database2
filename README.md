# Database2# Running the Application: A Step-by-Step Guide

## Essential Installations

Before initiating the setup, please ensure these are installed:

- PostgreSQL
- IntelliJ IDEA Community Edition
- PostgreSQL JDBC Driver: [Download Link](https://jdbc.postgresql.org/download/)

## How to Download the Project

- *Step 1:* Access the project's GitHub page: [Assignment-2 Repository](https://github.com/aaliyevv/Database2).
- *Step 2:* Locate and click the 'Code' button, then opt for 'Download ZIP'.

## Configuring IntelliJ IDEA

### Initial Setup

- Launch IntelliJ IDEA and open the directory named 'the_app'.
- Head to 'File' in the menu and click on 'Project Structure'.

### Project Settings

- Under 'Project', choose an SDK (preferably version 20).
- Align the language level with your SDK choice (e.g., opt for '20-no new language features' but avoid preview versions).

### Dependencies

- Proceed to 'Modules' and then to 'Dependencies'.
- Confirm the Module SDK matches your earlier selection.
- Use the add ('+') button to include 'JARs or Directories'. Here, add the PostgreSQL JDBC driver (`postgresql-XX.X.X.jar`).
- Your dependencies should be arranged as follows:
    1. Oracle OpenJDK version (e.g., 20)
    2. postgresql-XX.X.X.jar
    3. Module Source

- At the bottom, set 'Dependencies storage format' to IntelliJ IDEA (.iml), then apply the settings and close the window.

## Database Creation Process

- Utilize either the terminal or pgAdmin to establish a PostgreSQL database.
- Find the necessary SQL queries in the Word files provided.
- Execute the CREATE queries sequentially, whether through the terminal or pgAdmin's query tool.
- Remember to note down your database name, the PostgreSQL username (typically 'postgres'), and your chosen password.

## Code Adjustment

- In the src folder, find and open 'bookstore.java'.
- Modify the following segment with your database information:

    java
    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/your_database_name";
    private static final String DB_USER = "your_username";
    private static final String DB_PASSWORD = "your_password";
    

- To run the program, click the 'Run' icon located at the top right of IntelliJ.

## Operating the Application

- Input the number corresponding to your desired function in the console and hit 'Enter'. Ensure no additional spaces are included.
