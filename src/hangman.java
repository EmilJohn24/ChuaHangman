import acm.program.*;
import acm.util.*;

import java.applet.AudioClip;
import java.io.*;    // for File
import java.util.*;  // for Scanner
public class hangman extends ConsoleProgram {
    private final String HINT_CHAR = "-";
    public void run() {
        // TODO: write this method
        int gamesCount = 0, gamesWon = 0, best = 0;
        int previousGuessCount;
        intro();
        String secretWord;
        do {
            secretWord = getRandomWord("assets/large.txt");
            previousGuessCount = playOneGame(secretWord);
            gamesCount += 1;
            if (previousGuessCount > 0){
                gamesWon += 1;
                if (previousGuessCount > best){
                    best = previousGuessCount;
                }
            }
            stats(gamesCount, gamesWon, best);
        }
        while (readBoolean("Play another game?", "Y", "N"));
    }

    // TODO: comment this method
    private void intro() {
        playAudio("start.wav");
        String border = "@@@@@@@@@@@@@@@@@@@@@@@@@@@@";
        int indentation = 20;
        printRelativeCenter(border, border.length(), indentation);
        printRelativeCenter("Welcome to Hangman!", border.length(), indentation);
        printRelativeCenter("I will think of a random word.", border.length(), indentation);
        printRelativeCenter("You'll try to guess its letters.", border.length(), indentation);
        printRelativeCenter("Every time you guess a letter", border.length(), indentation);
        printRelativeCenter("that isn't in my word, a new body", border.length(), indentation);
        printRelativeCenter("part of the hanging man appears.", border.length(), indentation);
        printRelativeCenter("Good luck!", border.length(), indentation);
        printRelativeCenter(border, border.length(), indentation);

    }



    // TODO: comment this method
    private int playOneGame(String secretWord) {
        // TODO: write this method
        String guessedLetters = new String();
        char newGuess;
        int lives = 8;
        while (lives != 0){
            resetCanvas();
            displayHangman(lives);
            newGuess = readGuess(guessedLetters);
            guessedLetters += newGuess;
            if (secretWord.indexOf(newGuess) == -1) {
                lives--;
                playAudio("wrong.wav");
            }
            else if (hasWon(secretWord, guessedLetters)) {
                victorySequence(secretWord);
                return lives;
            }
            else {
                println("Correct!");
                playAudio("correct.wav");
            }
            printHint(secretWord, guessedLetters);
        }

        gameOverSequence(secretWord);
        return lives;
    }

    private void resetCanvas(){
        canvas.clear();
        canvas.reset();
    }
    private void printHint(String secretWord, String guessedLetters){
        println("Hint: ");
        print(createHint(secretWord, guessedLetters));
        println("\nYou guessed: " + guessedLetters);
    }

    private void victorySequence(String secretWord){
        playAudio("victory.wav");
        println("You win! My word was " + secretWord);
    }

    private void gameOverSequence(String secretWord){
        playAudio("defeat.wav");
        println("You lose. My word was " + secretWord);
    }
    private boolean hasWon(String secretWord, String guessedLetters){
        return createHint(secretWord, guessedLetters).indexOf(HINT_CHAR) == -1;
    }

    // Generates a hint for the user e.g. P--P for the word POOP after the player inputs the letter "P"
    private String createHint(String secretWord, String guessedLetters) {
        String hint = new String();
        String currStr;
        int wordLen = secretWord.length();
        for (int i = 0; i < wordLen; i++){
            currStr = String.valueOf(secretWord.charAt(i));
            if (guessedLetters.indexOf(currStr) != -1)
                hint += currStr;
            else
                hint += HINT_CHAR;
        }
        return hint;
    }

    // Retrieves a guess from the user
    private char readGuess(String guessedLetters) {
        String guessStr = readLine("Make a guess: ");
        if (guessStr.isEmpty()){
            println("Please type something.");
            return readGuess(guessedLetters);
        }
        char guess = guessStr.charAt(0);
        guess = Character.toUpperCase(guess);
        if (guess < 'A' || guess > 'Z'){
            println("Invalid letter. ");
            return readGuess(guessedLetters);
        }
        else if (guessedLetters.indexOf(guess) != -1){
            println("You already guessed that letter.");
            return readGuess(guessedLetters);
        }
        return guess;

    }

    private void playAudio(String filename){
        AudioClip hornClip = MediaTools.loadAudioClip("assets/" + filename);
        hornClip.play();
    }

    // TODO: comment this method
    private void displayHangman(int guessCount) {
        // TODO: write this method

        try (BufferedReader br = new BufferedReader(new FileReader(
                "assets/display" + guessCount + ".txt"))) {
            String line = null;
            while ((line = br.readLine()) != null) {
               canvas.printText(line);
            }
        }
        catch (FileNotFoundException f){
            System.out.println("Error loading file");
        }
        catch (IOException f){
            System.out.println("Error loading file");
        }



    }

    private void indent(int indention){
        for (int i = 0; i != indention; i++)
            print(" ");
    }

    private void printRelativeCenter(String str, int space, int indention){
        indent(indention);
        int length = str.length();
        if (length <= space){
            indent((space - length) / 2);
            print(str);
        }
        else print(str);
        print("\n");
    }
    // TODO: comment this method
    private void stats(int gamesCount, int gamesWon, int best) {
        // TODO: write this method
        String border = "@@@@@@@@@@@@@@@@@@@@@@@@@@@@";
        int indentation = 20;
        String winPercent = ((double) gamesWon / gamesCount) * 100 + "%";
        printRelativeCenter(border, border.length(), indentation);
        printRelativeCenter("Overall statistics", border.length(), indentation);
        printRelativeCenter("Games played: " + gamesCount, border.length(), indentation);
        printRelativeCenter("Games won: " + gamesWon, border.length(), indentation);
        printRelativeCenter("Win percent: " + winPercent, border.length(), indentation);
        printRelativeCenter("Best game: " + best + " guesses remaining", border.length(), indentation);
        printRelativeCenter("Thanks for playing!", border.length(), indentation);
        printRelativeCenter(border, border.length(), indentation);
    }

    // TODO: comment this method
    private String getRandomWord(String filename) {
        // TODO: write this method
        Scanner file;
        int count;
        int index;
        try {
            file = new Scanner(new File(filename));
            count = file.nextInt();
            RandomGenerator rand = new RandomGenerator();
            index = rand.nextInt(0, count - 1);
            for (int i = 0; i < index && file.hasNextLine(); i++){
                file.next();
            }
            return file.next();
        }
        catch (FileNotFoundException f){
            println("File not found");
            return "PROGRAMMER";
        }

        catch (Exception e){
            println("ERROR: Unknown error occurred while loading file.");
            return "PROGRAMMER";
        }



    }

    // TODO: comment this method
    public void init() {
        canvas = new HangmanCanvas();
        add(canvas);
        // canvas.reset();  // sample canvas method call
    }



    private HangmanCanvas canvas;
}
