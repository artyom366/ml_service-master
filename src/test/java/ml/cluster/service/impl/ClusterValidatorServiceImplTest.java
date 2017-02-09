package ml.cluster.service.impl;

import static ml.cluster.service.TestOpticsGenerator.prepareValidClusterWithStatingLocationAndLimitation;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import ml.cluster.datastructure.optics.Cluster;

@RunWith(MockitoJUnitRunner.class)
public class ClusterValidatorServiceImplTest {

	private final double SILHOUETTE_INDEX_HIGH = 0.8d;
	private final double SILHOUETTE_INDEX_AVERAGE = 0.5d;
	private final double DUNN_INDEX_HIGH = 4d;
	private final double DUNN_INDEX_LOW = 0d;

	@Spy
	private OpticsNeighboursServiceImpl neighboursService;

	@InjectMocks
	private ClusterValidatorServiceImpl clusterValidatorService;

	@Test
	public void testGetSilhouetteIndexWithGoodClusteringStricture() throws Exception {

		final Cluster cluster_1 = prepareValidClusterWithStatingLocationAndLimitation(0, 0, 3);
		final Cluster cluster_2 = prepareValidClusterWithStatingLocationAndLimitation(10, 10, 3);

		final double silhouetteIndex = clusterValidatorService.getSilhouetteIndex(cluster_1, cluster_2);
		assertThatValueIsANumber(silhouetteIndex);
		assertTrue("Silhouette index should be positive value between 0 and 1", silhouetteIndex >= 0 && silhouetteIndex <= 1);
		assertTrue("Silhouette index should be higher than 0.8", silhouetteIndex > SILHOUETTE_INDEX_HIGH);
	}

	@Test
	public void testGetSilhouetteIndexWithAverageClusteringStricture() throws Exception {

		final Cluster cluster_1 = prepareValidClusterWithStatingLocationAndLimitation(0, 0, 3);
		final Cluster cluster_2 = prepareValidClusterWithStatingLocationAndLimitation(3, 2, 3);

		final double silhouetteIndex = clusterValidatorService.getSilhouetteIndex(cluster_1, cluster_2);
		assertThatValueIsANumber(silhouetteIndex);
		assertTrue("Silhouette index should be positive value between 0 and 1", silhouetteIndex >= 0 && silhouetteIndex <= 1);
		assertTrue("Silhouette index should be higher than 0.8", silhouetteIndex < 0.8d && silhouetteIndex > SILHOUETTE_INDEX_AVERAGE);
	}

	@Test
	public void testGetSilhouetteIndexWithPoorClusteringStricture() throws Exception {

		final Cluster cluster_1 = prepareValidClusterWithStatingLocationAndLimitation(0, 0, 3);
		final Cluster cluster_2 = prepareValidClusterWithStatingLocationAndLimitation(1, 1, 3);

		final double silhouetteIndex = clusterValidatorService.getSilhouetteIndex(cluster_1, cluster_2);
		assertThatValueIsANumber(silhouetteIndex);
		assertTrue("Silhouette index should be positive value between 0 and 1", silhouetteIndex >= 0 && silhouetteIndex <= 1);
		assertTrue("Silhouette index should be lower than 0.5", silhouetteIndex < SILHOUETTE_INDEX_AVERAGE);
	}

	@Test
	public void testGetDunnIndexGoodClusteringStricture() throws Exception {

		final Cluster cluster_1 = prepareValidClusterWithStatingLocationAndLimitation(0, 0, 3);
		final Cluster cluster_2 = prepareValidClusterWithStatingLocationAndLimitation(10, 10, 3);

		final double dunnIndex = clusterValidatorService.getDunnIndex(cluster_1, cluster_2);
		assertThatValueIsANumber(dunnIndex);
		assertTrue("Dunn index should be positive finite value", dunnIndex >= 0);
		assertTrue("Dunn index should be higher than 4", dunnIndex >= DUNN_INDEX_HIGH);
	}

	@Test
	public void testGetDunnIndexPoorClusteringStricture() throws Exception {

		final Cluster cluster_1 = prepareValidClusterWithStatingLocationAndLimitation(0, 0, 3);
		final Cluster cluster_2 = prepareValidClusterWithStatingLocationAndLimitation(2, 2, 3);

		final double dunnIndex = clusterValidatorService.getDunnIndex(cluster_1, cluster_2);
		assertThatValueIsANumber(dunnIndex);
		assertTrue("Dunn index should be positive finite value", dunnIndex >= 0);
		assertTrue("Dunn index should be exactly 0", dunnIndex == DUNN_INDEX_LOW);
	}

	private static void assertThatValueIsANumber(final double index) {
		assertFalse("Index should not be 'NaN'", Double.isNaN(index));
		assertTrue("Index should be finite number", Double.isFinite(index));
	}

}