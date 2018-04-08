package edu.carleton.comp4601.cf.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class SimpleDataAccessObject {
	
	File file;
	private int[][] ratings;
	private String[] items;
	private String[] users;
	
	public SimpleDataAccessObject(File file) {
		this.file = file;
	}
	
	public int[][] getRatings() {
		return ratings;
	}

	public String[] getItems() {
		return items;
	}

	public String[] getUsers() {
		return users;
	}

	public boolean input() throws FileNotFoundException {
		boolean okay = true;
		
		Scanner s = new Scanner(file);
		int nUsers = s.nextInt();
		int nItems = s.nextInt();
		
		users = new String[nUsers];
		for (int i = 0; i < nUsers; i++)
			users[i] = s.next();
		items = new String[nItems];
		for (int j = 0; j < nItems; j++)
			items[j] = s.next();
		
		ratings = new int[nUsers][nItems];
		for (int i = 0; i < nUsers; i++) {
			for (int j = 0; j < nItems; j++) {
				ratings[i][j] = s.nextInt();
			}
		}
				
		s.close();
		return okay;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("SimpleDataAccessObject\n\n");
		for (String u : users) {
			buf.append(u);
			buf.append(" ");
		}
		buf.append("\n");
		for (String i : items) {
			buf.append(i);
			buf.append(" ");
		}		
		buf.append("\n");
		for (int i = 0; i < users.length; i++) {
			for (int j = 0; j < items.length; j++) {
				if (ratings[i][j] == -1)
					buf.append("?");
				else
					buf.append(ratings[i][j]);
				buf.append(" ");
			}
			buf.append("\n");
		}
		return buf.toString();
	}

	public static void main(String[] args) throws FileNotFoundException {
		SimpleDataAccessObject sdao = new SimpleDataAccessObject(new File("test.txt"));
		sdao.input();
		System.out.println(sdao);
		System.out.println("\n");
		System.out.println(sdao.toStringAnswerUsers());
		System.out.println("\n");
		System.out.println(sdao.toStringAnswerItems());
		System.out.println("=====================================");
		
		sdao = new SimpleDataAccessObject(new File("test2.txt"));
		sdao.input();
		System.out.println(sdao);
		System.out.println("\n");
		System.out.println(sdao.toStringAnswerUsers());
		System.out.println("\n");
		System.out.println(sdao.toStringAnswerItems());
		
		System.out.println("=====================================");
		
		sdao = new SimpleDataAccessObject(new File("test3.txt"));
		sdao.input();
		System.out.println(sdao);
		System.out.println("\n");
		System.out.println(sdao.toStringAnswerUsers());
		System.out.println("\n");
		System.out.println(sdao.toStringAnswerItems());
		System.out.println("=====================================");
		
	}
	
	
	public double predict(int a, int p){
		double numinator = 0;
		double denominator = 0;
		double aAvg = getAverageRatingForUserPearson(a);
		
		for(int i = 0; i < users.length; i++){
			if(i != a && ratings[i][p] != -1){
				double similarity = similarity(a, i);
				if(similarity > 0){
					//include user i in sum
					double iAvg = getAverageRatingForUserPearson(i);//,a);
					numinator += similarity * (ratings[i][p] - iAvg);
					denominator += similarity;
				}
			}
		}
		
		return denominator != 0 ? aAvg + ( numinator / denominator ) : 0;
	}
	
	
	public double similarity(int a, int b){
		double numinator = 0;
		double denominator = 0;
		double sqASum = 0;
		double sqBSum = 0;
		double aAvg = getAverageRatingForUserPearson(a);//,b);
		double bAvg = getAverageRatingForUserPearson(b);//,a);
		
		for (int j = 0; j < items.length; j++) {
			numinator += (ratings[a][j] + aAvg) * (ratings[b][j] + bAvg);
			sqASum += Math.pow((ratings[a][j] - aAvg), 2);
			sqBSum += Math.pow((ratings[b][j] - bAvg), 2);
		}
		
		denominator = Math.sqrt(sqASum) * Math.sqrt(sqBSum);
		
		return denominator != 0 ? numinator / denominator : 0;
	}
	
	public double getAverageRatingForUserPearson(int userToAvg){//, int otherUser){
		double sum = 0;
		for (int j = 0; j < items.length; j++) {
			//Sum here
			if (ratings[userToAvg][j] != -1){// && ratings[otherUser][j] != -1){
				// get average for item?
				sum += ratings[userToAvg][j];
			}
		}
		return items.length > 0 ? sum / items.length : 0;
	}
	
	public String toStringAnswerUsers() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("SimpleDataAccessObject With Predictions by Users\n\n");
		for (String u : users) {
			buf.append(u);
			buf.append(" ");
		}
		buf.append("\n");
		for (String i : items) {
			buf.append(i);
			buf.append(" ");
		}		
		buf.append("\n");
		for (int i = 0; i < users.length; i++) {
			for (int j = 0; j < items.length; j++) {
				if (ratings[i][j] == -1){
					//calculateValue
					buf.append(predict(i,j));
				}
				else
					buf.append(ratings[i][j]);
				buf.append(" ");
			}
			buf.append("\n");
		}
		return buf.toString();
	}
	
	public double predictItem(int a, int p){
		double numinator = 0;
		double denominator = 0;
		//double aAvg = getAverageRatingForUserPearson(a);
		
		for(int j = 0; j < items.length; j++){
			if(j != p && ratings[a][j] != -1){
				double similarity = similarityItem(j, p);
				if(similarity > 0){
					//include user i in sum
					numinator += similarity * (ratings[a][j]);
					denominator += similarity;
				}
			}
		}
		
		return denominator != 0 ? (numinator / denominator) : 0;
	}
	
	
	public double similarityItem(int itemA, int itemB){
		double numinator = 0;
		double denominator = 0;
		double sqASum = 0;
		double sqBSum = 0;
		
		for (int i = 0; i < users.length; i++) {
			double userAvg = getAverageForItem(i);
			numinator += (ratings[i][itemA] - userAvg) * (ratings[i][itemB] - userAvg);
			sqASum += Math.pow((ratings[i][itemA] - userAvg), 2);
			sqBSum += Math.pow((ratings[i][itemB]- userAvg), 2);
		}
		
		denominator = Math.sqrt(sqASum) * Math.sqrt(sqBSum);
		
		return denominator != 0 ? numinator / denominator : 0;
	}
	
	public double getAverageForItem(int userToAvg){
		double sum = 0;
		for (int j = 0; j < items.length; j++) {
			//Sum here
			if (ratings[userToAvg][j] != -1){
				// get average for item?
				sum += ratings[userToAvg][j];
			}
		}
		return items.length > 0 ? sum / items.length : 0;
	}
	
	public String toStringAnswerItems() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("SimpleDataAccessObject With Predictions by Items\n\n");
		for (String u : users) {
			buf.append(u);
			buf.append(" ");
		}
		buf.append("\n");
		for (String i : items) {
			buf.append(i);
			buf.append(" ");
		}		
		buf.append("\n");
		for (int i = 0; i < users.length; i++) {
			for (int j = 0; j < items.length; j++) {
				if (ratings[i][j] == -1){
					//calculateValue
					buf.append(predictItem(i,j));
				}
				else
					buf.append(ratings[i][j]);
				buf.append(" ");
			}
			buf.append("\n");
		}
		return buf.toString();
	}
	
}


