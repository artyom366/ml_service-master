package ml.cluster.service.impl;

import static ml.cluster.service.TestOpticsGenerator.generateOptics;
import static ml.cluster.service.TestOpticsGenerator.generateOpticsWithDistanceErrorInCluster;
import static ml.cluster.service.TestOpticsGenerator.generateOpticsWithSizeErrorInCluster;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import ml.cluster.datastructure.optics.Cluster;
import ml.cluster.datastructure.optics.Optics;
import ml.cluster.datastructure.optics.OpticsSummary;
import ml.cluster.error.cluster.ClusterDistanceException;
import ml.cluster.error.cluster.ClusterSizeException;

@RunWith(MockitoJUnitRunner.class)
public class OpticsExtractorServiceImplTest {

	private static Optics OPTICS_WITH_THREE_CLUSTERS = generateOptics();
	private static Optics OPTICS_WITH_DISTANCE_ERROR_IN_CLUSTER = generateOpticsWithDistanceErrorInCluster();
	private static Optics OPTICS_WITH_SIZE_ERROR_IN_CLUSTER = generateOpticsWithSizeErrorInCluster();

	@Spy
	private PointServiceImpl pointService;

	@InjectMocks
	private OpticsExtractorServiceImpl opticsExtractorService;

	@Test
	public void testExtractClusters() throws Exception {
		final OpticsSummary opticsSummary = opticsExtractorService.extractClusters(OPTICS_WITH_THREE_CLUSTERS);
		final List<Cluster> result = opticsSummary.getClusters();

		assertThat("ClusterImpl extraction result should not be null", result, is(notNullValue()));
		assertThat("There should be 3 clusters extracted", result.size(), is(3));

		final Cluster cluster_0 = result.get(0);
		final Cluster cluster_1 = result.get(1);
		final Cluster cluster_2 = result.get(2);

		assertThat("ClusterImpl should not be null", cluster_0, is(notNullValue()));
		assertThat("ClusterImpl should not be null", cluster_1, is(notNullValue()));
		assertThat("ClusterImpl should not be null", cluster_2, is(notNullValue()));

		assertThat("ClusterImpl points should not be null", cluster_0.getClusterPoints(), is(notNullValue()));
		assertThat("There should be 9 point in the cluster", cluster_0.getClusterPoints().size(), is(9));
		assertThat("ClusterImpl points should not be null", cluster_1.getClusterPoints(), is(notNullValue()));
		assertThat("There should be 7 point in the cluster", cluster_1.getClusterPoints().size(), is(7));
		assertThat("ClusterImpl points should not be null", cluster_2.getClusterPoints(), is(notNullValue()));
		assertThat("There should be 8 point in the cluster", cluster_2.getClusterPoints().size(), is(8));
	}

	@Test(expected = ClusterDistanceException.class)
	public void testExtractClustersWithDistanceError() throws Exception {
		opticsExtractorService.extractClusters(OPTICS_WITH_DISTANCE_ERROR_IN_CLUSTER);
	}

	@Test(expected = ClusterSizeException.class)
	public void testExtractClustersWithSizeError() throws Exception {
		opticsExtractorService.extractClusters(OPTICS_WITH_SIZE_ERROR_IN_CLUSTER);
	}
}