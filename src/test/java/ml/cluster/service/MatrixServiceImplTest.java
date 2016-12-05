package ml.cluster.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import ml.cluster.datastructure.PickSegment;
import ml.cluster.to.PickLocationViewDO;

@RunWith(MockitoJUnitRunner.class)
public class MatrixServiceImplTest {

	private final static int NUMBER_OF_LOCATIONS = 50;
	private final static int NUMBER_OF_LOCATION_IN_GROUP = 10;

	@Spy
	private MatrixServiceImpl matrixService;

	@Test
	public void testGetSegmentedLocations() throws Exception {
		final List<PickLocationViewDO> locations = TestLocationsGenerator.generate(NUMBER_OF_LOCATIONS);

		final Map<String, List<PickLocationViewDO>> result = matrixService.groupByLine(locations);
		assertThat(result, is(notNullValue()));
		assertThat(result.size(), is(TestLocationsGenerator.getLocationLinesQuantity()));

		final int locationCount = result.values().stream().mapToInt(List::size).sum();
		assertThat(NUMBER_OF_LOCATIONS, is(locationCount));
	}

	@Test
	public void testDefineSegmentBoundaries() {
        final Map<String, List<PickLocationViewDO>> segmentGroups = TestLocationsGenerator.generateGrouped(NUMBER_OF_LOCATION_IN_GROUP);

        final Map<PickSegment, List<PickLocationViewDO>> result = matrixService.defineSegmentBoundaries(segmentGroups);
        assertThat(result, is(notNullValue()));
		assertThat(result.size() > 0, is(true));

		result.forEach((segment, locations) -> {
			final DoubleSummaryStatistics xStats = locations.stream().mapToDouble(PickLocationViewDO::getX).summaryStatistics();
			final DoubleSummaryStatistics yStats = locations.stream().mapToDouble(PickLocationViewDO::getY).summaryStatistics();

			final double minY = yStats.getMin();
			final double maxY = yStats.getMax();
			final double minX = xStats.getMin();
			final double maxX = xStats.getMax();

			assertThat(locations.size(), is(NUMBER_OF_LOCATION_IN_GROUP));
			assertThat(segment.getMaxX(), is(maxX));
			assertThat(segment.getMinX(), is(minX));
			assertThat(segment.getMaxY(), is(maxY));
			assertThat(segment.getMinY(), is(minY));
			assertThat(segment.getLine(), is(locations.get(0).getLine()));
		});
    }

	@Test
	public void testGenerateSegmentMatrix() {



	}

}