package workshop05code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
//Included for the logging exercise
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    // Start code for logging exercise
    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
    }

    private static final Logger logger = Logger.getLogger(App.class.getName());
    // End code for logging exercise
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            logger.log(Level.INFO, "Wordle created and connected.");
        } else {
            logger.log(Level.SEVERE, "Not able to connect. Sorry!");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {
            logger.log(Level.INFO, "Wordle structures in place.");
        } else {
            logger.log(Level.SEVERE, "Not able to launch. Sorry!");
            return;
        }

        // let's add some words to valid 4 letter words from the data.txt file

        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            int i = 1;
            ArrayList<String> ignoredWords = new ArrayList();

            while ((line = br.readLine()) != null) {
                System.out.println(line);
                if (line.matches("[a-z]{4}")){
                    wordleDatabaseConnection.addValidWord(i, line);
                }
                else{
                    ignoredWords.add(line);
                }
                i++;
            }

            for (String ignoredWord : ignoredWords) {
                String msg = "The word '" + ignoredWord + "' was not added...";
                logger.log(Level.WARNING, msg);
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Not able to load . Sorry!");
            logger.log(Level.INFO, e.getMessage());
            return;
        }

        // let's get them to enter a word

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter a 4 letter word for a guess or q to quit: ");
            String guess = scanner.nextLine();

            while (!guess.equals("q")) {

                if (guess.matches("[a-z]{4}")){
                    System.out.println("You've guessed '" + guess+"'.");

                    if (wordleDatabaseConnection.isValidWord(guess)) { 
                        System.out.println("Success! It is in the the list.\n");
                    }else{
                        System.out.println("Sorry. This word is NOT in the the list.\n");
                    }
                }
                else{
                    System.out.println("Please enter a four character word with only lowercase letters");
                    logger.log(Level.INFO, String.format("Invalid Input: %s", guess));
                }

                System.out.print("Enter a 4 letter word for a guess or q to quit: " );
                guess = scanner.nextLine();
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            e.printStackTrace();
        }

    }
}