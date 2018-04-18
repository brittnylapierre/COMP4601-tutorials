package edu.carleton.comp4601.cf.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

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
		System.out.println("\n");
		//System.out.println(sdao.toStringAnswerSVD());
		System.out.println("\n");
		System.out.println(sdao.toStringAnswerBayes());
		System.out.println("=====================================");

		
		sdao = new SimpleDataAccessObject(new File("test2.txt"));
		sdao.input();
		System.out.println(sdao);
		System.out.println("\n");
		System.out.println(sdao.toStringAnswerUsers());
		System.out.println("\n");
		System.out.println(sdao.toStringAnswerItems());
		System.out.println("\n");
		//System.out.println(sdao.toStringAnswerSVD());
		System.out.println("\n");
		System.out.println(sdao.toStringAnswerBayes());
		System.out.println("=====================================");

		
		sdao = new SimpleDataAccessObject(new File("test3.txt"));
		sdao.input();
		System.out.println(sdao);
		System.out.println("\n");
		System.out.println(sdao.toStringAnswerUsers());
		System.out.println("\n");
		System.out.println(sdao.toStringAnswerItems());
		System.out.println("\n");
		//System.out.println(sdao.toStringAnswerSVD());
		System.out.println("\n");
		System.out.println(sdao.toStringAnswerBayes());
		System.out.println("=====================================");

		sdao = new SimpleDataAccessObject(new File("test4.txt"));
		sdao.input();
		System.out.println(sdao);
		System.out.println("\n");
		System.out.println(sdao.toStringAnswerUsers());
		System.out.println("\n");
		System.out.println(sdao.toStringAnswerItems());
		System.out.println("\n");
		//System.out.println(sdao.toStringAnswerSVD());
		System.out.println("\n");
		System.out.println(sdao.toStringAnswerBayes());
		System.out.println("=====================================");
		
		sdao = new SimpleDataAccessObject(new File("test5.txt"));
		sdao.input();
		System.out.println(sdao);
		System.out.println("\n");
		System.out.println(sdao.toStringAnswerUsers());
		System.out.println("\n");
		System.out.println(sdao.toStringAnswerItems());
		System.out.println("\n");
		//System.out.println(sdao.toStringAnswerSVD());
		System.out.println("\n");
		System.out.println(sdao.toStringAnswerBayes());
		System.out.println("=====================================");
	}
	
	
	public double predict(int a, int p){
		double numinator = 0;
		double denominator = 0;
		//System.out.println("s");
		//System.out.println(aAvg);
		double aAvg = 0;
		System.out.println(Arrays.deepToString(ratings));
		for(int i = 0; i < users.length; i++){
			if(i != a && ratings[i][p] != -1){
				double[] simData = similarity(a, i);
				double similarity = simData[0];
				aAvg = simData[1];
				double iAvg = simData[2];
				System.out.println(similarity);
				if(similarity >= 0){
					numinator += similarity * (ratings[i][p] - iAvg);
					denominator += similarity;
				}
			}
		}
		
		//System.out.println(( numinator / denominator ));
		return denominator != 0 ? aAvg + ( numinator / denominator ) : 0;
	}
	
	
	public double[] similarity(int a, int b){ //rolem in here first one should be 
		double numinator = 0;
		double denominator = 0;
		double sqASum = 0;
		double sqBSum = 0;
		double n = 0;
		double aAvg = 0;//,b);
		double bAvg = 0;//,a);
		for (int j = 0; j < items.length; j++) {
			if(ratings[a][j] != -1 && ratings[b][j] != -1){
				n++;
				aAvg+=ratings[a][j];
				bAvg+=ratings[b][j];
			}
		}
		aAvg/=n;
		bAvg/=n;
		
		for (int j = 0; j < items.length; j++) {
			if(ratings[a][j] != -1 && ratings[b][j] != -1){
				numinator += (ratings[a][j] - aAvg) * (ratings[b][j] - bAvg);
				sqASum += Math.pow((ratings[a][j] - aAvg) , 2);
				sqBSum += Math.pow((ratings[b][j] - bAvg), 2);
			}
		}
		denominator = Math.sqrt(sqASum) * Math.sqrt(sqBSum);
		double sim = (denominator != 0 ? numinator / denominator : 0);
		return new double[]{Double.parseDouble(String.format("%.2f", sim)), Double.parseDouble(String.format("%.2f", aAvg)), Double.parseDouble(String.format("%.2f", bAvg))};
	}
	
	public double getAverageRatingForUserPearson(int userToAvg){//, int otherUser){
		double sum = 0;
		int num = 0;
		for (int j = 0; j < items.length; j++) {
			//Sum here
			if (ratings[userToAvg][j] != -1){// && ratings[otherUser][j] != -1){
				// get average for item?
				sum += ratings[userToAvg][j];
			}
			num++;
		}
		return num > 0 ? sum / num : 0;
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
					buf.append(String.format("%.3f", predict(i,j)));
					//predictionSDV(i, j);
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
				System.out.println(similarity);
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
			if(ratings[i][itemA] != -1 && ratings[i][itemB] != -1){
				double userAvg = 0;
				int n = 0;
				for (int j = 0; j < items.length; j++) {
					if(ratings[i][j] != -1){
						n++;
						userAvg += ratings[i][j];
					}
				}
				userAvg = n > 0 ? userAvg/n : 0;
				numinator += (ratings[i][itemA] - userAvg) * (ratings[i][itemB] - userAvg);
				sqASum += Math.pow((ratings[i][itemA] - userAvg), 2);
				sqBSum += Math.pow((ratings[i][itemB]- userAvg), 2);
			}
		}
		
		denominator = Math.sqrt(sqASum) * Math.sqrt(sqBSum);
		
		return denominator != 0 ? numinator / denominator : 0;
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
					buf.append(String.format("%.3f", predictItem(i,j)));
				}
				else
					buf.append(ratings[i][j]);
				buf.append(" ");
			}
			buf.append("\n");
		}
		return buf.toString();
	}
	
	
	
	//SVD
	
	
	public double predictionSDV(int a, int p){
		double[][] dRatings = new double[users.length-1][items.length-1];
		ArrayList<Integer> uRatings = new ArrayList<Integer>();
		int count = 0;
		int curr = 0;
		for (int i = 0; i < users.length; i++) {
			for (int j = 0; j < items.length-1; j++) {
				if(i != a){
					dRatings[curr][j] = (double) ratings[i][j];
				}
				if(i == a && ratings[i][j] > -1){
					uRatings.add(ratings[i][j]);
					count++;
				}
			}
			if(i != a){
				curr++;
			}
		}
		System.out.println(Arrays.deepToString(dRatings));
		double[] uRatingsArr = uRatings.stream().mapToDouble(i -> i).toArray();
		System.out.println(Arrays.toString(uRatingsArr));
		
		Array2DRowRealMatrix matrix = new Array2DRowRealMatrix(dRatings);
		SingularValueDecomposition matrixSVD = new SingularValueDecomposition(matrix);
		//System.out.println(matrixSVD.getU().toString());
		//REDUCE TO FIRST 2
		int i = matrixSVD.getU().getRowDimension();
	    int j = matrixSVD.getU().getColumnDimension();
		double[][] u2 = new double[i][j];
	    matrixSVD.getU().copySubMatrix(0, i-1, 0, j-1, u2);
		Array2DRowRealMatrix U2 = new Array2DRowRealMatrix(u2);
		System.out.println("U2:");
		System.out.println(U2.toString());
		System.out.println("U2T:");
		System.out.println(U2.transpose().toString());
		RealMatrix U2T = U2.transpose();
		
		i = matrixSVD.getS().getRowDimension();
	    j = matrixSVD.getS().getColumnDimension();
		double[][] s2 = new double[i][j];
	    matrixSVD.getS().copySubMatrix(0, i-1, 0, j-1, s2);
		RealMatrix S2 = new Array2DRowRealMatrix(s2); //diagonal
		System.out.println("S2:");
	    System.out.println(S2.toString());
	    RealMatrix S2I = MatrixUtils.inverse(MatrixUtils.createRealMatrix(matrixSVD.getS().getData()));
	    //System.out.println("S2 inv:");
	    //System.out.println(S2.toString());
	    
		i = matrixSVD.getV().getRowDimension();
	    j = matrixSVD.getV().getColumnDimension();
	    double[][] v2 = new double[i][j];
	    matrixSVD.getV().copySubMatrix(0, i-1, 0, j-1, v2);
	    Array2DRowRealMatrix V2 = new Array2DRowRealMatrix(v2);
	    System.out.println("V2:");
	    System.out.println(V2.toString());

	    double[][] user2 = new double[1][uRatingsArr.length];
	    user2[0] = uRatingsArr;
	    Array2DRowRealMatrix user2M = new Array2DRowRealMatrix(user2);
	    System.out.println("U2m:");
	    System.out.println(user2M);
	    
	    RealMatrix user2D = user2M.multiply(U2).multiply(S2I);	    
	    double[][] user2A = new double[1][2]; 
	    user2D.copySubMatrix(0, 0, 0, 1, user2A);
	    
	    double[] alice = user2A[0];
	    System.out.println("U2d:");
	    System.out.println(Arrays.toString(alice));
	    
	    
	    double[][] full = new double[users.length][items.length];
	    for (int user = 0; user < users.length; user++) {
			for (int item = 0; item < items.length; item++) {
				full[user][item] = (double) ratings[user][item] < 0 ? 0:ratings[user][item];
			}
		}
	    System.out.println("full:");
	    System.out.println(Arrays.deepToString(full));
	    
		Array2DRowRealMatrix fmatrix = new Array2DRowRealMatrix(full);
		SingularValueDecomposition fmatrixSVD = new SingularValueDecomposition(fmatrix);

	    System.out.println("\nU:");
	    System.out.println(fmatrixSVD.getV().toString());
	    RealMatrix U_M = fmatrixSVD.getV().getSubMatrix(a, a, 0, 1);//V2.getSubMatrix(a, a, 0, 1);
	    System.out.println(U_M.toString());
	    RealMatrix S_M = fmatrixSVD.getS().getSubMatrix(0, 1, 0, 1);//S2.getSubMatrix(0, 1, 0, 1);
	    System.out.println("S:");
	    System.out.println(S_M.toString());
	    
	    System.out.println("V:");
	    System.out.println(fmatrixSVD.getU().toString());
	    RealMatrix V_M = fmatrixSVD.getU().getSubMatrix(0, 1, p, p);//U2.getSubMatrix(0, 1, p, p);
	    System.out.println(V_M.toString());
	    
	    RealMatrix results = U_M.multiply(S_M).multiply(V_M);
	    double[][] dataArray = results.getData();
	    System.out.println(Arrays.deepToString(dataArray));
	    //double svd  = dataArray.length != 0 ? dataArray[0][0] : -1;
	    /*
	    
	    
		i = matrixSVD.getU().getRowDimension();
	    j = matrixSVD.getU().getColumnDimension();
	    
	    double[][] ua = new double[1][j];
	    matrixSVD.getU().copySubMatrix(a, a, 0, j - 1, ua);
	    Array2DRowRealMatrix UA = new Array2DRowRealMatrix(ua);
	    
	    Array2DRowRealMatrix S = (Array2DRowRealMatrix) matrixSVD.getS(); //diagonal


	    i = matrixSVD.getV().getRowDimension();
	    j = matrixSVD.getV().getColumnDimension();
	    
	    double[][] vt = new double[i][1];
	    matrixSVD.getV().copySubMatrix(0, i - 1, p, p, vt);
	    Array2DRowRealMatrix VT = new Array2DRowRealMatrix(vt);
	    
	    RealMatrix results = VT.multiply(S).multiply(UA.transpose());
	    double[][] dataArray = results.getData();
	    
	    double svd  = dataArray.length != 0 ? dataArray[0][0] : -1;
	    
	    double userAvg = getAverageRatingForUserPearson(a);
	    System.out.println(userAvg);
	    System.out.println(svd);
	    System.out.println(userAvg + svd);
		return userAvg + svd;*/
		return 0;
	}
	
	
	public String toStringAnswerSVD() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("SimpleDataAccessObject With Predictions by SVD\n\n");
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
					buf.append(String.format("%.3f", predictionSDV(i,j)));//predictItem(i,j));
				}
				else
					buf.append(ratings[i][j]);
				buf.append(" ");
			}
			buf.append("\n");
		}
		return buf.toString();
	}
	
	public int predictionBayes(int user, int itemNumber){
		double highest = 0;
		int score = 0;
		for(int i = 1; i <= 5; i++){
			double p = calculateProbabilityBayes(user, itemNumber, i);
			if(p > highest){
				highest = p;
				score = i;
			}
		}
		return score;
	}
	
	public double calculateProbabilityBayes(int user, int itemNumber, int rating){
		double denominator = 0;
		ArrayList<Integer> nominators = new ArrayList<Integer>(); 
		for(int j = 0; j < items.length; j++){
			nominators.add(0);
		}
		
		for (int i = 0; i < users.length; i++) {
			if(i != user && ratings[i][itemNumber] == rating){
				for (int j = 0; j < items.length; j++) {
					if (ratings[i][j] != -1 && ratings[i][j] == ratings[user][j]){
						nominators.set(j, nominators.get(j)+1);
					}
				}
				denominator++;
			}
		}
		
		double probability = 0;
		if(denominator != 0){
			probability = 1;
			for(int i = 0; i < nominators.size(); i++){
				if(i != itemNumber){
					probability *= (double)nominators.get(i)/denominator;	
					//System.out.println(nominators.get(i)/denominator);
				}
			}
		}
		//System.out.println(probability);
		return probability;
	}
	
	public String toStringAnswerBayes() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("SimpleDataAccessObject With Predictions by Bayes\n\n");
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
					buf.append(predictionBayes(i,j));
				}
				else
					buf.append(ratings[i][j]);
				buf.append(" ");
			}
			buf.append("\n");
		}
		return buf.toString();
		/*TODO:
		 * Use Slope One predictors to predict unrated items.	
		 * Use RF-Rec predictors to predict unrated items.	
		 * */
	}
	
}


