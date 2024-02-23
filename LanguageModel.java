import java.util.HashMap;
import java.util.Random;

import com.sun.tools.javac.Main;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
	private Random randomGenerator;

    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName) {
		String window = "";
        char c;
        In in = new In(fileName);
        List probList;
        // Reads just enough characters to form the first window
        for (int i = 0; i < this.windowLength; i++) {
            c = in.readChar();
            window += c;
        }
        // Processes the entire text, one character at a time
        while (!in.isEmpty()) {
            // Gets the next character
            c = in.readChar();
            // Checks if the window is already in the map
            if (this.CharDataMap.containsKey(window)) {
                probList = this.CharDataMap.get(window);
            }
            else {
                probList = new List();
            }
            
            probList.update(c);
            this.CharDataMap.put(window, probList);

            // Advances the window: adds c to the window’s end, and deletes the
            // window's first character.
            window += c;
            window = window.substring(1);
        }
        // The entire file has been processed, and all the characters have been counted.
        // Proceeds to compute and set the p and cp fields of all the CharData objects
        // in each linked list in the map.
        for (List probs : this.CharDataMap.values())
            calculateProbabilities(probs);
	}

    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */
	public void calculateProbabilities(List probs) {				
		
        double numOfChr = 0.0;
        double p, cp = 0.0;
        Node current = probs.getNode();

        while (current != null) {
            numOfChr += current.cp.count;
            current = current.next;
        }

        current = probs.getNode();
        while (current != null) {
            p = current.cp.count / numOfChr;
            cp += p;
            current.cp.p = p;
            current.cp.cp = cp;
            current = current.next;
        }
	}

    // Returns a random character from the given probabilities list.
	public char getRandomChar(List probs) {
		
        Node current = probs.getNode();
        double r = this.randomGenerator.nextDouble();

        while (current != null) {
            if (r < current.cp.cp) {
                return current.cp.chr;
            }
            current = current.next;
        }

        return ' ';
	}

    /**
	 * Generates a random text, based on the probabilities that were learned during training. 
	 * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
	 * doesn't appear as a key in Map, we generate no text and return only the initial text. 
	 * @param numberOfLetters - the size of text to generate
	 * @return the generated text
	 */
	public String generate(String initialText, int textLength) {
		if (!this.CharDataMap.containsKey(initialText.substring(initialText.length() - this.windowLength)) || initialText.length() < this.windowLength) {
            return initialText;
        }

        String generatedText = initialText, currentWindow = initialText;
        char c;

        for (int i = 0; i < textLength; i++) {
            c = this.getRandomChar(this.CharDataMap.get(currentWindow));
            generatedText += c;
            currentWindow += c;
            currentWindow = currentWindow.substring(1);
        }

        return generatedText;
	}

    /** Returns a string representing the map of this language model. */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String key : CharDataMap.keySet()) {
			List keyProbs = CharDataMap.get(key);
			str.append(key + " : " + keyProbs + "\n");
		}
		return str.toString();
	}


    public static void main(String[] args) {
        if (args.length > 4) {
            int windowLength = Integer.parseInt(args[0]);
            String initialText = args[1];
            int generatedTextLength = Integer.parseInt(args[2]);
            Boolean randomGeneration = args[3].equals("random");
            String fileName = args[4];
            // Create the LanguageModel object
            LanguageModel lm;
            if (randomGeneration) {
                lm = new LanguageModel(windowLength);
            }
            else {
                lm = new LanguageModel(windowLength, 20);
            }
            // Trains the model, creating the map.
            lm.train(fileName);
            // Generates text, and prints it.
            System.out.println(lm.generate(initialText, generatedTextLength));
        }

    }
}
