package org.openimaj.experiment.validation.cross;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.openimaj.data.RandomData;
import org.openimaj.experiment.dataset.ListBackedDataset;
import org.openimaj.experiment.dataset.ListDataset;
import org.openimaj.experiment.dataset.util.DatasetAdaptors;
import org.openimaj.experiment.validation.DefaultValidationData;
import org.openimaj.experiment.validation.ValidationData;
import org.openimaj.util.list.AcceptingListView;
import org.openimaj.util.list.SkippingListView;

/**
 * K-Fold cross validation for {@link ListDataset}s. The data is broken
 * into K approximately equally sized non-overlapping randomised subsets. 
 * On each iteration, one subset is picked as the validation data and the 
 * remaining subsets are combined to make the training data. The number of
 * iterations is equal to the number of subsets.
 * <p>
 * If the number of subsets is equal to the number of instances, then
 * the K-Fold Cross Validation scheme becomes equivalent to the 
 * LOOCV scheme. The implementation of LOOCV in the {@link LeaveOneOut}
 * class is considerably more memory efficient than using this class
 * however.
 * 
 * @author Jonathon Hare (jsh2@ecs.soton.ac.uk)
 *
 * @param <INSTANCE> Type of instances
 */
public class KFold<INSTANCE> implements CrossValidator<ListDataset<INSTANCE>> {
	private class KFoldIterable implements CrossValidationIterable<ListDataset<INSTANCE>> {
		private List<INSTANCE> listView;
		private int[][] subsetIndices;

		/**
		 * Construct with the given dataset and number of folds.
		 * 
		 * @param dataset the dataset
		 * @param k the number of folds.
		 */
		public KFoldIterable(ListDataset<INSTANCE> dataset, int k) {
			if (k > dataset.size())
				throw new IllegalArgumentException("The number of folds must be less than the number of items in the dataset");

			if (k <= 0)
				throw new IllegalArgumentException("The number of folds must be at least one");

			this.listView = DatasetAdaptors.asList(dataset);

			int[] allIndices = RandomData.getUniqueRandomInts(dataset.size(), 0, dataset.size());
			subsetIndices = new int[k][];

			int splitSize = dataset.size() / k;
			for (int i=0; i<k-1; i++) { 
				subsetIndices[i] = Arrays.copyOfRange(allIndices, splitSize * i, splitSize * (i + 1));
			}
			subsetIndices[k-1] = Arrays.copyOfRange(allIndices, splitSize * (k - 1), allIndices.length);
		}

		/**
		 * Get the number of iterations that the {@link Iterator}
		 * returned by {@link #iterator()} will perform.
		 * 
		 * @return the number of iterations that will be performed
		 */
		@Override
		public int numberIterations() {
			return subsetIndices.length;
		}

		@Override
		public Iterator<ValidationData<ListDataset<INSTANCE>>> iterator() {
			return new Iterator<ValidationData<ListDataset<INSTANCE>>>() {
				int validationSubset = 0;

				@Override
				public boolean hasNext() {
					return validationSubset < subsetIndices.length;
				}

				@Override
				public ValidationData<ListDataset<INSTANCE>> next() {
					ListDataset<INSTANCE> training = new ListBackedDataset<INSTANCE>(new SkippingListView<INSTANCE>(listView, subsetIndices[validationSubset]));
					ListDataset<INSTANCE> validation = new ListBackedDataset<INSTANCE>(new AcceptingListView<INSTANCE>(listView, subsetIndices[validationSubset]));

					validationSubset++;

					return new DefaultValidationData<ListDataset<INSTANCE>>(training, validation);
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
	}

	private int k;

	/**
	 * Construct with the given number of folds.
	 * 
	 * @param k the number of folds.
	 */
	public KFold(int k) {
		this.k = k;
	}
	
	@Override
	public CrossValidationIterable<ListDataset<INSTANCE>> createIterable(ListDataset<INSTANCE> data) {
		return new KFoldIterable(data, k);
	}
}