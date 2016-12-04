package ml.cluster.service;

import ml.cluster.datastructure.FixedRadiusMatrix;
import ml.cluster.datastructure.MatrixCell;
import ml.cluster.datastructure.PickSegment;
import ml.cluster.error.CellNoAreaSpecifiedException;
import ml.cluster.error.MatrixNoAreaSpecifiedException;
import ml.cluster.to.PickLocationViewDO;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

@Service("matrixService")
public class MatrixServiceImpl implements MatrixService {

    @Override
    public Map<PickSegment, List<PickLocationViewDO>> getSegmentedLocations(final List<PickLocationViewDO> pickLocationViewDOs) throws MatrixNoAreaSpecifiedException, CellNoAreaSpecifiedException {
        Validate.notEmpty(pickLocationViewDOs, "Pick locations are not defined");

        final Map<String, List<PickLocationViewDO>> segmentGroups = groupByLine(pickLocationViewDOs);
        final Map<PickSegment, List<PickLocationViewDO>> pickSegments = defineSegmentBoundaries(segmentGroups);

        generateSegmentMatrix(pickSegments);
        assignPickLocationsToMatrixCells(pickSegments);
        assignNeighboringMatrixCells(pickSegments);

        return pickSegments;
    }

    protected Map<String, List<PickLocationViewDO>> groupByLine(final List<PickLocationViewDO> pickLocations) {
        return Collections.unmodifiableMap(pickLocations.stream().collect(Collectors.groupingBy(PickLocationViewDO::getLine)));
    }

    protected Map<PickSegment, List<PickLocationViewDO>> defineSegmentBoundaries(final Map<String, List<PickLocationViewDO>> segmentGroups) {
        final Map<PickSegment, List<PickLocationViewDO>> pickSegments = new HashMap<>();

        segmentGroups.forEach((line, pickLocations) -> {
            final Stream<PickLocationViewDO> stream = pickLocations.stream();
            final DoubleSummaryStatistics xStats = stream.mapToDouble(PickLocationViewDO::getX).summaryStatistics();
            final DoubleSummaryStatistics yStats = stream.mapToDouble(PickLocationViewDO::getY).summaryStatistics();

            final double minY = yStats.getMin();
            final double maxY = yStats.getMax();
            final double minX = xStats.getMin();
            final double maxX = xStats.getMax();

            pickSegments.put(new PickSegment(line, minY, maxY, minX, maxX), pickLocations);
        });

        return Collections.unmodifiableMap(pickSegments);
    }

    protected void generateSegmentMatrix(final Map<PickSegment, List<PickLocationViewDO>> pickSegments) throws MatrixNoAreaSpecifiedException, CellNoAreaSpecifiedException {
        for (final PickSegment segment : pickSegments.keySet()) {

            final double matrixHeight = segment.getMaxY() - segment.getMinY();
            final double matrixWidth = segment.getMaxX() - segment.getMinX();
            validateMatrixSize(matrixHeight, matrixWidth);

            final FixedRadiusMatrix matrix = new FixedRadiusMatrix.MatrixBuilder()
                    .height(matrixHeight)
                    .width(matrixWidth)
                    .build();

            validateCellSize(matrix);

            segment.setMatrix(matrix);
            generateSegments(segment, 0, 0, 0L, 0L);
        }
    }

    private void validateMatrixSize(final double matrixHeight, final double matrixWidth) throws MatrixNoAreaSpecifiedException {
        if (matrixHeight == 0 || matrixWidth == 0) {
            throw new MatrixNoAreaSpecifiedException(matrixWidth, matrixHeight);
        }
    }

    private void validateCellSize(final FixedRadiusMatrix matrix) throws CellNoAreaSpecifiedException {
        if (matrix.getCellHeight() == 0 || matrix.getCellWidth() == 0) {
            throw new CellNoAreaSpecifiedException(matrix.getCellWidth(), matrix.getCellHeight());
        }
    }

    protected void generateSegments(final PickSegment segment, final double currentHeight, final double currentWidth, final long currentRow, final long currentColumn) {

        if (segment.getMatrix().getMatrixHeight() < currentHeight && segment.getMatrix().getMatrixWidth() < currentWidth) {

            if (segment.getMatrix().getMatrixWidth() < currentWidth) {
                createMatrixCell(segment, currentRow, currentColumn);
                generateSegments(segment, currentHeight, currentWidth + segment.getMatrix().getCellWidth(), currentRow, currentColumn + 1);

            } else if (segment.getMatrix().getMatrixHeight() < currentHeight) {
                createMatrixCell(segment, currentRow, currentColumn);
                generateSegments(segment, currentHeight + segment.getMatrix().getCellHeight(), 0, currentRow + 1, 0);
            }
        }
    }

    protected void createMatrixCell(final PickSegment segment, final long currentRow, final long currentColumn) {

        final double cellMinX = segment.getMinX() + currentColumn * segment.getMatrix().getCellWidth();
        final double cellMaxX = cellMinX + segment.getMatrix().getCellWidth();
        final double cellMinY = segment.getMinY() + currentRow * segment.getMatrix().getCellHeight();
        final double cellMaxY = cellMinY + segment.getMatrix().getCellHeight();

        final MatrixCell cell = new MatrixCell(cellMaxX, cellMinX, cellMaxY, cellMinY);
        segment.getMatrix().addToSegmentPickLocations(new ImmutablePair<>(currentRow, currentColumn), cell);
    }

    protected void assignPickLocationsToMatrixCells(final Map<PickSegment, List<PickLocationViewDO>> pickSegments) {
        pickSegments.forEach((segment, locations) -> {

            final Map<Pair<Long, Long>, MatrixCell> segmentPickCells = segment.getMatrix().getSegmentPickCells();

            locations.forEach(location -> {

                segmentPickCells.forEach((coordinates, cell) -> {

                    if (isLocationInCell(location, cell)) {
                        cell.addToPickLocations(location);
                    }
                });
            });
        });
    }

    private boolean isLocationInCell(final PickLocationViewDO location, final MatrixCell cell) {
        return location.getX() < cell.getMaxX() && location.getX() > cell.getMinX() && location.getY() < cell.getMaxY() && location.getY() > cell.getMinY();
    }

    protected void assignNeighboringMatrixCells(final Map<PickSegment, List<PickLocationViewDO>> pickSegments) {
        pickSegments.forEach((segment, locations) -> {

            segment.getMatrix().getSegmentPickCells().forEach((coordinates, cell) -> {

                final long maxRow = segment.getMatrix().getRows();
                final long maxColumn = segment.getMatrix().getColumns();

                final long row = coordinates.getLeft();
                final long column = coordinates.getRight();

                final List<Pair<Long, Long>> potentialNeighbors = getPotentialNeighbors(row, column);
                final List<Pair<Long, Long>> neighbors = refinePotentialNeighbors(potentialNeighbors, maxRow, maxColumn);

                cell.addToNeighborPickingLocations(neighbors);
            });
        });
    }

    private List<Pair<Long, Long>> getPotentialNeighbors(final long row, final long column) {
        final List<Pair<Long, Long>> neighbors = new ArrayList<>();

        LongStream.range(-1, 2).forEach(rowIndex -> {
            LongStream.range(-1, 2).forEach(columnIndex -> {
                neighbors.add(new ImmutablePair<>(row + rowIndex, column + columnIndex));
            });
        });

        return neighbors;
    }

    private List<Pair<Long, Long>> refinePotentialNeighbors(final List<Pair<Long, Long>> neighbors, final long maxRow, final long maxColumn) {
        return neighbors.stream().filter(neighbor -> isValidNeighbor(neighbor, maxRow, maxColumn)).collect(Collectors.toList());
    }

    private boolean isValidNeighbor(final Pair<Long, Long> neighbor, final long maxRow, final long maxColumn) {
        return neighbor.getLeft() >= 0 && neighbor.getLeft() <= maxRow && neighbor.getRight() >= 0 && neighbor.getRight() <= maxColumn;
    }
}
