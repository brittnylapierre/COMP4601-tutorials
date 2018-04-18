package edu.carleton.comp4601.algorithms;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

import opennlp.tools.stemmer.PorterStemmer;

public class SimpleBayes {
/*• Boolean	Mul+nomial	Naïve	Bayes	
• Clips	all	the	word	counts	in	each	document	at	1	*/
	double classCount = 2;
	double numPoss = 0;
	double numNegs = 0;
	double numReviews = 0;
	ArrayList<String> POSITIVE;
	ArrayList<String> NEGATIVE;
	//HashMap<String, Double> POSITIVE_PROB = new HashMap<String, Double>();
	//HashMap<String, Double> NEGATIVE_PROB = new HashMap<String, Double>();
	ArrayList<String> STOP;
	ArrayList<String> PUNC;
	public SimpleBayes(){
		POSITIVE = new ArrayList<String>();
		NEGATIVE = new ArrayList<String>();
		PUNC = new ArrayList<String>(Arrays.asList(new String[]{
				"-",".",",","\"","!",":","(",")","=","?","/"
			}));
		File stopWordsFile = new File("stop.txt");
		String stopWordsString = "";
		try {
			stopWordsString = readFile(stopWordsFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		STOP = new ArrayList<String>(Arrays.asList(stopWordsString.split(" ")));
		
	}
	
	public void buildDataset() throws IOException{
		//Get pos and neg word bank
		PorterStemmer ps = new PorterStemmer();
		File posDir = new File("C:/Users/IBM_ADMIN/Documents/review_polarity/txt_sentoken/training/pos"); //training
		File[] poss = posDir.listFiles();
		numPoss = poss.length;
		numReviews += numPoss;
		for (File reviewFile : poss) {
			String review = readFile(reviewFile);
			ArrayList<String> reviewWords = new ArrayList<String>(Arrays.asList(review.split(" ")));
			reviewWords.removeAll(STOP);
			reviewWords.removeAll(PUNC);
			for(int i = 0; i < reviewWords.size(); i++){
				String currWord = reviewWords.get(i);
				currWord = ps.stem(currWord);
				reviewWords.set(i, currWord);
				///if(!POSITIVE.contains(currWord)){
					POSITIVE.add(currWord);
				//}
			}
		}
		/*
		 * # For every word in the text, we get the number of times that word occured in the reviews for a given class, 
		 * add 1 to smooth the value, and divide by the total number of words in the class (plus the class_count to also
		 *  smooth the denominator).
      # Smoothing ensures that we don't multiply the prediction by 0 if the word didn't exist in the training data.
      # We also smooth the denominator counts to keep things even.
		 * */
		
		File negDir = new File("C:/Users/IBM_ADMIN/Documents/review_polarity/txt_sentoken/training/neg"); //training
		File[] negs = negDir.listFiles();
		numNegs = negs.length;
		numReviews += numNegs;
		for (File reviewFile : negs) {
			String review = readFile(reviewFile);
			ArrayList<String> reviewWords = new ArrayList<String>(Arrays.asList(review.split(" ")));
			reviewWords.removeAll(STOP);
			reviewWords.removeAll(PUNC);
			for(int i = 0; i < reviewWords.size(); i++){
				String currWord = reviewWords.get(i);
				currWord = ps.stem(currWord);
				reviewWords.set(i, currWord);
				//if(!NEGATIVE.contains(currWord)){
					NEGATIVE.add(currWord);
				//}
			}
		}

		System.out.println("done training\n\n");
		
		System.out.println("POSITIVE WORDS:\n");
		System.out.println(POSITIVE.toString());
		System.out.println("\n\nNEGATIVE WORDS:\n");
		System.out.println(NEGATIVE.toString());
	}
	
	private void runSentimentAnalysis() throws IOException{		
		PorterStemmer ps = new PorterStemmer();
		File all = new File("C:/Users/IBM_ADMIN/Documents/review_polarity/txt_sentoken/all");
		File negDir = new File("C:/Users/IBM_ADMIN/Documents/review_polarity/txt_sentoken/neg");
		ArrayList<String> negfiles = new ArrayList<String>();
		for(File f : negDir.listFiles()){
			negfiles.add(f.getName());
		}
		File posDir = new File("C:/Users/IBM_ADMIN/Documents/review_polarity/txt_sentoken/pos");
		ArrayList<String> posfiles = new ArrayList<String>();
		for(File f : posDir.listFiles()){
			posfiles.add(f.getName());
		}
		int correct = 0;
		File[] allFiles = all.listFiles();
		for (File file : allFiles) {
			System.out.println("\n\nClassifing " + file.getName() + " ...");
			String review = readFile(file);
			ArrayList<String> reviewWords = new ArrayList<String>(Arrays.asList(review.split(" ")));
			reviewWords.removeAll(STOP);
			reviewWords.removeAll(PUNC);
			
			double generalPositiveProb = (numPoss / numReviews)*100;
			double generalNegativeProb = (numNegs / numReviews)*100;
			double probNeg = 1;
			double probPos = 1;
			double distinctWords = reviewWords.size();
			for(int i = 0; i < reviewWords.size(); i++){
				String currWord = reviewWords.get(i);
				double freqWord = (Collections.frequency(reviewWords,currWord));
				currWord = ps.stem(currWord);
				reviewWords.set(i, currWord);
				if(NEGATIVE.contains(currWord)){
					probNeg += (freqWord + (Collections.frequency(NEGATIVE, currWord) + 1)) / (NEGATIVE.size() + distinctWords);
				}
				
				if (POSITIVE.contains(currWord)){
					probPos += (freqWord + (Collections.frequency(POSITIVE, currWord) + 1)) / (POSITIVE.size() + distinctWords);
					
				}
			}
			//System.out.println(probPos);
			//System.out.println(probNeg);
			if(probPos > probNeg){
				System.out.println("Predicted Positive");
				if(posfiles.contains(file.getName())){
					System.out.println("Predicted correctly!");
					correct++;
				} else {
					System.out.println("Predicted incorrectly!");
				}
			} else if(probPos < probNeg){
				System.out.println("Predicted Negative");
				if(negfiles.contains(file.getName())){
					System.out.println("Predicted correctly!");
					correct++;
				} else {
					System.out.println("Predicted incorrectly!");
				}
			} else {
				System.out.println("Tie - could not predict");
				System.out.println(probPos);
				System.out.println(probNeg);
			}
		}
		System.out.println("\nSummary: " + correct +"/" + allFiles.length + " predictions correct.");
	}
	
	private String readFile(File file) throws IOException {
	    StringBuilder fileContents = new StringBuilder((int)file.length());
	    Scanner scanner = new Scanner(file);
	    String lineSeparator = System.getProperty("line.separator");

	    try {
	        while(scanner.hasNextLine()) {
	            fileContents.append(scanner.nextLine() + lineSeparator);
	        }
	        return fileContents.toString();
	    } finally {
	        scanner.close();
	    }
	}
	
	public static void main(String[] args) {
		System.out.println("SIMPLE BAYES SENTIMENT PREDICTION");
		SimpleBayes sb = new SimpleBayes();
		try {
			sb.buildDataset();
			sb.runSentimentAnalysis();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
