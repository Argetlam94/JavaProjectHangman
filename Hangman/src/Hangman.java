import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;


public class Hangman {
	
	public static int countDifferentChars(String word){
		boolean[] isCharPresent = new boolean[Character.MAX_VALUE];
		for(int i = 0; i < word.length(); i++){
			isCharPresent[word.charAt(i)] = true;
		}
		
		int counter = 0;
		for (int i = 0; i < isCharPresent.length; i++){
			if(isCharPresent[i]){
				counter++;
			}
		}
		return counter;
	}
	
	public static void maskOutWord(HashMap<Character, String> hiddenWord, String phrase){
		//mask out word
		for (int i = 0; i < phrase.length(); i++){
			if(phrase.charAt(i) == ' '){
				hiddenWord.put(' ', " ");
			}
			else if(phrase.charAt(i) == '-'){
				hiddenWord.put('-', "-");
			}
			else {
				hiddenWord.put(phrase.charAt(i), "_");
			}
		}
	}
	
	// function which represents a single game
	// returns 1 point if game is won or 0 if lost
	public static int play(HashMap<String, ArrayList<String>> dictionary){
		Scanner input = new Scanner(System.in);
		int mistakes = 0;
		System.out.println("Please choose a category: ");
		for (String key : dictionary.keySet()){
			System.out.println(key);
		}
		String category;
		do{
			System.out.print(">");
			category = input.nextLine();
			if(!dictionary.containsKey(category)){
				System.out.println("Please enter one of the listed categories. ");
			}	
		}while(!dictionary.containsKey(category));
		
		Random rand = new Random();
		int wordNumber = rand.nextInt(dictionary.get(category).size());
		String phrase = dictionary.get(category).get(wordNumber);
		HashMap<Character, String> hiddenWord = new HashMap<>();
		
		//mask out word
		maskOutWord(hiddenWord, phrase);
		
		// clear phrase from everything but letters so we can count distinct
		// ones
		String phraseCleaned = phrase.replaceAll("[\\s\\-+]", "");
		int pointsToWin = countDifferentChars(phraseCleaned);
		int points = 0;
		//loop to represent single turn letter game
		while(mistakes < 10){
			System.out.println("Attempts left: " + (10 - mistakes));
			System.out.print("Current word/phrase: ");
			for(int i = 0; i < phrase.length(); i++){
				System.out.print(hiddenWord.get(phrase.charAt(i)) + ' ');
			}
			System.out.println();
			System.out.println("Please enter a letter: ");
			char letter;
			do {
				System.out.print("> ");
				letter = input.next().charAt(0);
				if(!(letter >= 'A' && letter <= 'Z') && !(letter >= 'a' && letter <= 'z')){
					System.out.println("Enter a latin letter, please. ");
				}
			}while(!(letter >= 'A' && letter <= 'Z') && !(letter >= 'a' && letter <= 'z'));
			char upper = Character.toUpperCase(letter);
			char lower = Character.toLowerCase(upper);
			// remove point if point added twice for upper and lowercase letters
			if(hiddenWord.containsKey(upper) && hiddenWord.get(upper).equalsIgnoreCase("_")){
				hiddenWord.put(upper, upper + "");
				points++;
			}
			if(hiddenWord.containsKey(lower) && hiddenWord.get(lower).equalsIgnoreCase("_")){
				hiddenWord.put(lower, lower + "");	
				points++;
			}
			if(!hiddenWord.containsKey(lower) && !hiddenWord.containsKey(upper)){
				mistakes++;
				System.out.println("The word/phrase does not have this letter. ");
			}
			
			if(mistakes == 10){
				System.out.println("Game over! ");
			}
			
			if (points == pointsToWin){
				System.out.println("Congratulations you have revealed the word/phrase: ");
				for(int i = 0; i < phrase.length(); i++){
					System.out.print(hiddenWord.get(phrase.charAt(i)) + ' ');
				}
				System.out.println();
				return 1;
			}
		}
		return 0;
	}
	
	
	public static void main(String[] args) throws IOException {
		try{
			BufferedReader reader = new BufferedReader(new FileReader("dictionary.txt"));
			// using hashmap with topics for O(1) access when number of topics
			// grows towards infinity
			HashMap<String, ArrayList<String>> dictionary = new HashMap<>();
			String currentCategory = " ";
			String line = reader.readLine();
			//loop over file to create dictionary
			while(line != null){
				line = line.trim(); // deal with spaces on sides
				//avoid crazy character
				if(line.startsWith('\uFEFF' + "")){
					line = line.substring(1);
				}
				if(line.startsWith("_")){
					currentCategory = line.substring(1);
					dictionary.put(currentCategory,new ArrayList<String>());
				}
				else{
					dictionary.get(currentCategory).add(line);
				}
				line = reader.readLine();
			}
			int score = 0;
			while(play(dictionary) > 0){
				System.out.println("Current score: " + ++score);
			}
		} catch (FileNotFoundException e){
			System.out.println("Please supply provide correct dictionary name. ");
		} catch (IOException e){
			System.out.println("Problem when reading from file. ");
		}
	}
}
