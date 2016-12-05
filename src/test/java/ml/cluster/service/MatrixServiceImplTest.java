package ml.cluster.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;

import ml.cluster.datastructure.FixedRadiusMatrix;
import ml.cluster.datastructure.MatrixCell;
import ml.cluster.error.CellNoAreaSpecifiedException;
import ml.cluster.error.MatrixNoAreaSpecifiedException;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import ml.cluster.datastructure.PickSegment;
import ml.cluster.to.PickLocationViewDO;

@RunWith(MockitoJUnitRunner.class)
public class MatrixServiceImplTest {

	private final static int NUMBER_OF_LOCATIONS = 50;
	private final static int NUMBER_OF_LOCATIONS_IN_GROUP = 10;
	private final static int NUMBER_OF_LOCATIONS_IN_MATRIX = 200;

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
        final Map<String, List<PickLocationViewDO>> segmentGroups = TestLocationsGenerator.generateGrouped(NUMBER_OF_LOCATIONS_IN_GROUP);

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

			assertThat(locations.size(), is(NUMBER_OF_LOCATIONS_IN_GROUP));
			assertThat(segment.getMaxX(), is(maxX));
			assertThat(segment.getMinX(), is(minX));
			assertThat(segment.getMaxY(), is(maxY));
			assertThat(segment.getMinY(), is(minY));
			assertThat(segment.getLine(), is(locations.get(0).getLine()));
		});
    }

	@Ignore
	public void testGenerateSegmentMatrix() throws MatrixNoAreaSpecifiedException, CellNoAreaSpecifiedException {
        final Map<String, List<PickLocationViewDO>> segmentGroups = TestLocationsGenerator.generateGrouped(NUMBER_OF_LOCATIONS_IN_MATRIX);
        final Map<PickSegment, List<PickLocationViewDO>> pickSegments = matrixService.defineSegmentBoundaries(segmentGroups);

        matrixService.generateSegmentMatrix(pickSegments);
        assertThat(pickSegments, is(notNullValue()));
        assertThat(pickSegments.size() > 0, is(true));

        pickSegments.forEach((segment, locations) -> {
            final FixedRadiusMatrix matrix = segment.getMatrix();

            final double matrixWidth = matrix.getMatrixWidth();
            final double matrixHeight = matrix.getMatrixHeight();

            final double cellWidth = matrix.getCellWidth();
            final double cellHeight = matrix.getCellHeight();

            final long columns = matrix.getColumns();
            final long rows = matrix.getRows();

            assertThat(matrixWidth <= cellWidth * columns, is(true));
            assertThat(matrixHeight <= cellHeight * rows, is(true));

            final Map<Pair<Long, Long>, MatrixCell> cells = matrix.getSegmentPickCells();

            assertThat(cells, is(notNullValue()));
            assertThat(cells.size(), is(rows * columns));

            cells.forEach((position, cell) -> {
                final List<PickLocationViewDO> cellLocations = cell.getLocations();
                cellLocations.forEach(cellLocation -> {
                    final Double x = cellLocation.getX();
                    final Double y = cellLocation.getY();
                    assertThat(x <= cell.getMaxX() && x >= cell.getMinX() && y <= cell.getMaxY() && y >= cell.getMinY(), is(true));
                });
            });
        });
    }
}