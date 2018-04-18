package edu.carleton.comp4601.algorithms;

import org.apache.spark.ml.clustering.LDA;
import org.apache.spark.ml.clustering.LDAModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class LDAExample {

	public static void main(String[] args) {
		
		SparkSession spark = SparkSession
			      .builder()
			      .appName("JavaLDAExample")
			      .master("local")
			      .getOrCreate();

		// Loads data.
		Dataset<Row> dataset = spark.read().format("libsvm")
		  .load("sample_lda_libsvm_data.txt");

		// Trains a LDA model.
		// Uses 10 topics 
		// Runs the algorithm for 10 iterations
		LDA lda = new LDA().setK(10).setMaxIter(10);
		LDAModel model = lda.fit(dataset);

		double ll = model.logLikelihood(dataset);
		double lp = model.logPerplexity(dataset);
		System.out.println("The lower bound on the log likelihood of the entire corpus: " + ll);
		System.out.println("The upper bound on perplexity: " + lp);

		// Describe topics.
		// Only want to output the 3 top-weighted terms
		Dataset<Row> topics = model.describeTopics(3);
		System.out.println("The topics described by their top-weighted terms:");
		topics.show(false);

		// Shows the result.
		Dataset<Row> transformed = model.transform(dataset);
		transformed.show(false);
		spark.stop();
	}
	
}
