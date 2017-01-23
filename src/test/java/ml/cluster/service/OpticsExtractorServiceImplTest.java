package ml.cluster.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import ml.cluster.datastructure.optics.Cluster;
import ml.cluster.datastructure.optics.OpticsPoint;
import ml.cluster.datastructure.optics.Point;
import ml.cluster.to.PickLocationViewDO;

@RunWith(MockitoJUnitRunner.class)
public class OpticsExtractorServiceImplTest {

	private static PickLocationViewDO LOCATION;
	private static List<OpticsPoint> OPTICS;

	@Spy
	private PointServiceImpl pointService;

	@InjectMocks
	private OpticsExtractorServiceImpl opticsExtractorService;

	@BeforeClass
	public static void setUpClass() {
		prepareLocation();
		prepareOptics();

	}

	private static void prepareLocation() {
		LOCATION = new PickLocationViewDO();
		LOCATION.setX(5d);
		LOCATION.setY(5d);
		LOCATION.setLine("AA");
	}

	private static void prepareOptics() {
		OPTICS = new LinkedList<>();
		OPTICS.add(prepareCorePoint());
		OPTICS.add(prepareClusterPoint());
		OPTICS.add(prepareClusterPoint());
		OPTICS.add(prepareClusterPoint());
		OPTICS.add(prepareClusterPoint());
		OPTICS.add(prepareClusterPoint());
		OPTICS.add(prepareBorderPoint());
		OPTICS.add(prepareBorderPoint());
		OPTICS.add(prepareBorderPoint());
		OPTICS.add(prepareOutlierPoint());

		OPTICS.add(prepareCorePoint());
		OPTICS.add(prepareClusterPoint());
		OPTICS.add(prepareClusterPoint());
		OPTICS.add(prepareClusterPoint());
		OPTICS.add(prepareClusterPoint());
		OPTICS.add(prepareClusterPoint());
		OPTICS.add(prepareBorderPoint());
		OPTICS.add(prepareBorderPoint());

		OPTICS.add(prepareCorePoint());
		OPTICS.add(prepareClusterPoint());
		OPTICS.add(prepareClusterPoint());
		OPTICS.add(prepareClusterPoint());
		OPTICS.add(prepareClusterPoint());
		OPTICS.add(prepareClusterPoint());
		OPTICS.add(prepareBorderPoint());
		OPTICS.add(prepareOutlierPoint());
	}

	private static OpticsPoint prepareCorePoint() {
		final OpticsPoint point = Point.newInstance(LOCATION);
		point.setCoreDistance(5d);
		return point;
	}

	private static OpticsPoint prepareClusterPoint() {
		final OpticsPoint point = Point.newInstance(LOCATION);
		point.setCoreDistance(5d);
		point.setReachabilityDistance(5d);
		return point;
	}

	private static OpticsPoint prepareBorderPoint() {
		final OpticsPoint point = Point.newInstance(LOCATION);
		point.setReachabilityDistance(5d);
		return point;
	}

	private static OpticsPoint prepareOutlierPoint() {
		return Point.newInstance(LOCATION);
	}

	@Test
	public void testExtractClusters() {
		final List<Cluster> result = opticsExtractorService.extractClusters(OPTICS);
		assertThat("Cluster extraction result should not be null", result, is(notNullValue()));
		assertThat("There should be 3 clusters extracted", result.size(), is(3));

		final Cluster cluster_0 = result.get(0);
		final Cluster cluster_1 = result.get(1);
		final Cluster cluster_2 = result.get(2);

		assertThat("Cluster should not be null", cluster_0, is(notNullValue()));
		assertThat("Cluster should not be null", cluster_1, is(notNullValue()));
		assertThat("Cluster should not be null", cluster_2, is(notNullValue()));

		assertThat("Cluster points should not be null", cluster_0.getClusterPoints(), is(notNullValue()));
		assertThat("There should be 9 point in the cluster", cluster_0.getClusterPoints().size(), is(9));
		assertThat("Cluster points should not be null", cluster_1.getClusterPoints(), is(notNullValue()));
		assertThat("There should be 7 point in the cluster", cluster_1.getClusterPoints().size(), is(7));
		assertThat("Cluster points should not be null", cluster_2.getClusterPoints(), is(notNullValue()));
		assertThat("There should be 8 point in the cluster", cluster_2.getClusterPoints().size(), is(8));
	}
}