package cluster;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import util.Util;
import foldpath.Sample;

public class SampleCluster {

		
		/*
		 * Returns the index of the mediods in the samples
		 */
		public static List<Integer> mediods(List<Integer> clusters, List<Double> weights) {
			Integer numClusters = Collections.max(clusters) + 1;
			Double[] mediodWeights = new Double[numClusters];
			Integer[] mediodInds = new Integer[numClusters];
			Arrays.fill(mediodWeights,0.0);
			for(int i = 0; i < clusters.size(); i++){
				if(mediodWeights[clusters.get(i)] == null || mediodWeights[clusters.get(i)] < weights.get(i)){
					mediodWeights[clusters.get(i)] +=  weights.get(i);
					mediodInds[clusters.get(i)] = i;
				}
			}
			return Util.list(mediodInds);
		}
		
		/*
		 * Returns the total weight of each cluster
		 */
		public static List<Double> weights(List<Integer> clusters, List<Double> weights) {
			Integer numClusters = Collections.max(clusters) + 1;
			Double[] mediodWeights = new Double[numClusters];
			for(int i = 0; i < clusters.size(); i++){
				if(mediodWeights[clusters.get(i)] == null)
					mediodWeights[clusters.get(i)] =  weights.get(i);
				else
					mediodWeights[clusters.get(i)] +=  weights.get(i);
			}
			return Util.list(mediodWeights);
		}
		
		/*
		 * Returns the total weight of a list of samples
		 */
		public static double weight(Collection<Sample> samples) {
			double sum = 0;
			for(Sample s : samples)
				sum += s.weight;
			return sum;
		}
		
		public static List<Double> probabilities(List<Integer> clusters, List<Double> probabilities) {
			Integer numClusters = Collections.max(clusters) + 1;
			Double[] mediodProbs = new Double[numClusters];
			for(int i = 0; i < clusters.size(); i++){
				if(mediodProbs[clusters.get(i)] == null)
					mediodProbs[clusters.get(i)] =  probabilities.get(i);
				else
					mediodProbs[clusters.get(i)] +=  probabilities.get(i);
			}
			return Util.list(mediodProbs);
		}

		public static boolean acceptableClusters(List<Integer> clusters, List<Double> weights, double threshold){
			Integer numClusters = Collections.max(clusters) + 1;
			Double[] clusterWeights = new Double[numClusters];
			Arrays.fill(clusterWeights, 0.0);

			double sum = 0.0;
			for(int i = 0; i < clusters.size(); i++){
				clusterWeights[clusters.get(i)] += weights.get(i);
				sum += weights.get(i);
			}

			for(int i = 0; i < clusters.size(); i++){
				if(clusterWeights[clusters.get(i)] / sum <= threshold) return false;
			}

			return true;
		}

	
}
