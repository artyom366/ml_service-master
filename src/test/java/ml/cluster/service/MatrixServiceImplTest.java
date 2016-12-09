package ml.cluster.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collection;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import ml.cluster.datastructure.matrix.FixedRadiusMatrix;
import ml.cluster.datastructure.matrix.MatrixCell;
import ml.cluster.datastructure.matrix.PickSegment;
import ml.cluster.error.CellNoAreaSpecifiedException;
import ml.cluster.error.MatrixNoAreaSpecifiedException;
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

	@Test
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

			final long cellWidth = matrix.getCellWidth();
			final long cellHeight = matrix.getCellHeight();

			final long columns = matrix.getColumns();
			final long rows = matrix.getRows();

			assertThat(matrixWidth < cellWidth * columns, is(true));
			assertThat(matrixHeight < cellHeight * rows, is(true));

			final Map<Pair<Long, Long>, MatrixCell> cells = matrix.getSegmentPickCells();

			assertThat(cells, is(notNullValue()));
			assertThat((long)cells.size(), is(rows * columns));

			for (long row = 0, height = 0; row < matrix.getRows(); row++, height += cellHeight) {
				for (long column = 0, width = 0; column < matrix.getColumns(); column++, width += cellWidth) {
					final MatrixCell cell = cells.get(new ImmutablePair<>(row, column));
					assertThat(cell.getMinX(), is(width));
					assertThat(cell.getMaxX(), is(width + cellWidth));
					assertThat(cell.getMinY(), is(height));
					assertThat(cell.getMaxY(), is(height + cellHeight));
				}
			}
		});
	}

	@Test
	public void testAssignPickLocationsToMatrixCells() throws MatrixNoAreaSpecifiedException, CellNoAreaSpecifiedException {
		final Map<String, List<PickLocationViewDO>> segmentGroups = TestLocationsGenerator.generateGrouped(NUMBER_OF_LOCATIONS_IN_MATRIX);
		final Map<PickSegment, List<PickLocationViewDO>> pickSegments = matrixService.defineSegmentBoundaries(segmentGroups);
		matrixService.generateSegmentMatrix(pickSegments);
		matrixService.assignPickLocationsToMatrixCells(pickSegments);

		pickSegments.forEach((segment, locations) -> {
			final Map<Pair<Long, Long>, MatrixCell> cells = segment.getMatrix().getSegmentPickCells();

			cells.forEach((position, cell) -> {
				final List<PickLocationViewDO> cellLocations = cell.getLocations();

				cellLocations.forEach(cellLocation -> {
					assertThat(cellLocation.getX() >= cell.getMinX(), is(true));
					assertThat(cellLocation.getX() <= cell.getMaxX(), is(true));
					assertThat(cellLocation.getY() >= cell.getMinY(), is(true));
					assertThat(cellLocation.getY() <= cell.getMaxY(), is(true));
				});
			});

			final int numberOfLocationInMatrix = cells.values().stream().collect(Collectors.toList()).stream().map(MatrixCell::getLocations)
				.flatMap(Collection::stream).collect(Collectors.toList()).size();

			assertThat(numberOfLocationInMatrix, is(NUMBER_OF_LOCATIONS_IN_MATRIX));
		});
	}

	@Test
	public void testAssignNeighboringMatrixCells() throws MatrixNoAreaSpecifiedException, CellNoAreaSpecifiedException {
		final int fixedMaxXAxisValue = 15;
		final int fixedMaxYAxisValue = 15;

		final Map<String, List<PickLocationViewDO>> segmentGroups = TestLocationsGenerator.generateGrouped(NUMBER_OF_LOCATIONS, fixedMaxXAxisValue, fixedMaxYAxisValue);
		final Map<PickSegment, List<PickLocationViewDO>> pickSegments = matrixService.defineSegmentBoundaries(segmentGroups);
		matrixService.generateSegmentMatrix(pickSegments);
		matrixService.assignPickLocationsToMatrixCells(pickSegments);
		matrixService.assignNeighboringMatrixCells(pickSegments);

		final Set<PickSegment> key = pickSegments.keySet();
		final PickSegment segment = key.iterator().next();
		final FixedRadiusMatrix matrix = segment.getMatrix();
		final Map<Pair<Long, Long>, MatrixCell> cells = matrix.getSegmentPickCells();

		final MatrixCell cell0 = cells.get(new ImmutablePair<>(0L, 0L));
		final List<Pair<Long, Long>> neighboringCells0 = cell0.getNeighboringCells();
		assertThat(neighboringCells0.size() == 3, is(true));

		final Pair<Long, Long> cell0Neighbor0 = neighboringCells0.get(0);
		final Pair<Long, Long> cell0Neighbor1 = neighboringCells0.get(1);
		final Pair<Long, Long> cell0Neighbor2 = neighboringCells0.get(2);
		assertThat(cell0Neighbor0.getLeft() == 0 && cell0Neighbor0.getRight() == 1, is(true));
		assertThat(cell0Neighbor1.getLeft() == 1 && cell0Neighbor1.getRight() == 0, is(true));
		assertThat(cell0Neighbor2.getLeft() == 1 && cell0Neighbor2.getRight() == 1, is(true));

		final MatrixCell cell1 = cells.get(new ImmutablePair<>(0L, 1L));
		final List<Pair<Long, Long>> neighboringCells1 = cell1.getNeighboringCells();
		assertThat(neighboringCells1.size() == 5, is(true));

		final Pair<Long, Long> cell1Neighbor0 = neighboringCells1.get(0);
		final Pair<Long, Long> cell1Neighbor1 = neighboringCells1.get(1);
		final Pair<Long, Long> cell1Neighbor2 = neighboringCells1.get(2);
		final Pair<Long, Long> cell1Neighbor3 = neighboringCells1.get(3);
		final Pair<Long, Long> cell1Neighbor4 = neighboringCells1.get(4);
		assertThat(cell1Neighbor0.getLeft() == 0 && cell1Neighbor0.getRight() == 0, is(true));
		assertThat(cell1Neighbor1.getLeft() == 0 && cell1Neighbor1.getRight() == 2, is(true));
		assertThat(cell1Neighbor2.getLeft() == 1 && cell1Neighbor2.getRight() == 0, is(true));
		assertThat(cell1Neighbor3.getLeft() == 1 && cell1Neighbor3.getRight() == 1, is(true));
		assertThat(cell1Neighbor4.getLeft() == 1 && cell1Neighbor4.getRight() == 2, is(true));

		final MatrixCell cell4 = cells.get(new ImmutablePair<>(1L, 1L));
		final List<Pair<Long, Long>> neighboringCells4 = cell4.getNeighboringCells();
		assertThat(neighboringCells4.size() == 8, is(true));

		final Pair<Long, Long> cell4Neighbor0 = neighboringCells4.get(0);
		final Pair<Long, Long> cell4Neighbor1 = neighboringCells4.get(1);
		final Pair<Long, Long> cell4Neighbor2 = neighboringCells4.get(2);
		final Pair<Long, Long> cell4Neighbor3 = neighboringCells4.get(3);
		final Pair<Long, Long> cell4Neighbor4 = neighboringCells4.get(4);
		final Pair<Long, Long> cell4Neighbor5 = neighboringCells4.get(5);
		final Pair<Long, Long> cell4Neighbor6 = neighboringCells4.get(6);
		final Pair<Long, Long> cell4Neighbor7 = neighboringCells4.get(7);
		assertThat(cell4Neighbor0.getLeft() == 0 && cell4Neighbor0.getRight() == 0, is(true));
		assertThat(cell4Neighbor1.getLeft() == 0 && cell4Neighbor1.getRight() == 1, is(true));
		assertThat(cell4Neighbor2.getLeft() == 0 && cell4Neighbor2.getRight() == 2, is(true));
		assertThat(cell4Neighbor3.getLeft() == 1 && cell4Neighbor3.getRight() == 0, is(true));
		assertThat(cell4Neighbor4.getLeft() == 1 && cell4Neighbor4.getRight() == 2, is(true));
		assertThat(cell4Neighbor5.getLeft() == 2 && cell4Neighbor5.getRight() == 0, is(true));
		assertThat(cell4Neighbor6.getLeft() == 2 && cell4Neighbor6.getRight() == 1, is(true));
		assertThat(cell4Neighbor7.getLeft() == 2 && cell4Neighbor7.getRight() == 2, is(true));
	}
}