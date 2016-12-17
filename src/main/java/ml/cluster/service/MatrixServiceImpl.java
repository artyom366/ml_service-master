package ml.cluster.service;

import ml.cluster.datastructure.matrix.FixedRadiusMatrix;
import ml.cluster.datastructure.matrix.MatrixCell;
import ml.cluster.datastructure.segment.PickSegment;
import ml.cluster.error.CellNeighborsInconsistencyException;
import ml.cluster.error.CellNoAreaSpecifiedException;
import ml.cluster.error.MatrixException;
import ml.cluster.error.MatrixNoAreaSpecifiedException;
import ml.cluster.to.PickLocationViewDO;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("matrixService")
public class MatrixServiceImpl implements MatrixService {

    @Override
    public Set<PickSegment> getSegmentedLocations(final List<PickLocationViewDO> pickLocationViewDOs) throws MatrixException {
        Validate.notEmpty(pickLocationViewDOs, "Pick locations are not defined");

        final Map<String, List<PickLocationViewDO>> segmentGroups = groupByLine(pickLocationViewDOs);
        final Map<PickSegment, List<PickLocationViewDO>> pickSegments = defineSegmentBoundaries(segmentGroups);

        return generateSegmentMatricesAndCells(pickSegments);
    }

    protected Map<String, List<PickLocationViewDO>> groupByLine(final List<PickLocationViewDO> pickLocations) {
        return Collections.unmodifiableMap(pickLocations.stream().collect(Collectors.groupingBy(PickLocationViewDO::getLine)));
    }

    protected Map<PickSegment, List<PickLocationViewDO>> defineSegmentBoundaries(final Map<String, List<PickLocationViewDO>> segmentGroups) {
        final Map<PickSegment, List<PickLocationViewDO>> pickSegments = new HashMap<>();

        segmentGroups.forEach((line, pickLocations) -> {
            final DoubleSummaryStatistics xStats = pickLocations.stream().mapToDouble(PickLocationViewDO::getX).summaryStatistics();
            final DoubleSummaryStatistics yStats = pickLocations.stream().mapToDouble(PickLocationViewDO::getY).summaryStatistics();

            final double minY = yStats.getMin();
            final double maxY = yStats.getMax();
            final double minX = xStats.getMin();
            final double maxX = xStats.getMax();

            pickSegments.put(new PickSegment(line, minY, maxY, minX, maxX), pickLocations);
        });

        return Collections.unmodifiableMap(pickSegments);
    }

    protected void generateSegmentMatrix(final Map<PickSegment, List<PickLocationViewDO>> pickSegments) throws MatrixException {
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

    private void validateMatrixSize(final double matrixHeight, final double matrixWidth) throws MatrixException {
        if (matrixHeight == 0 || matrixWidth == 0) {
            throw new MatrixNoAreaSpecifiedException(matrixWidth, matrixHeight);
        }
    }

    private void validateCellSize(final FixedRadiusMatrix matrix) throws MatrixException {
        if (matrix.getCellHeight() == 0 || matrix.getCellWidth() == 0) {
            throw new CellNoAreaSpecifiedException(matrix.getCellWidth(), matrix.getCellHeight());
        }
    }

    private void generateSegments(final PickSegment segment, final double currentHeight, final double currentWidth, final long currentRow, final long currentColumn) {

        if (segment.getMatrix().getRows() > currentRow) {

            if (segment.getMatrix().getColumns() > currentColumn) {
                createMatrixCell(segment, currentRow, currentColumn);
                generateSegments(segment, currentHeight, currentWidth + segment.getMatrix().getCellWidth(), currentRow, currentColumn + 1);

            } else if (segment.getMatrix().getRows() > currentRow + 1) {
                createMatrixCell(segment, currentRow, 0);
                generateSegments(segment, currentHeight + segment.getMatrix().getCellHeight(), 0, currentRow + 1, 0);
            }
        }
    }

    private void createMatrixCell(final PickSegment segment, final long currentRow, final long currentColumn) {

        final long cellMinX = (long) segment.getMinX() + currentColumn * segment.getMatrix().getCellWidth();
        final long cellMaxX = cellMinX + segment.getMatrix().getCellWidth();
        final long cellMinY = (long) segment.getMinY() + currentRow * segment.getMatrix().getCellHeight();
        final long cellMaxY = cellMinY + segment.getMatrix().getCellHeight();

        final MatrixCell cell = new MatrixCell(cellMaxX, cellMinX, cellMaxY, cellMinY);
        segment.getMatrix().addToSegmentPickLocations(new ImmutablePair<>(currentRow, currentColumn), cell);
    }

    protected void assignPickLocationsToMatrixCells(final Map<PickSegment, List<PickLocationViewDO>> pickSegments) {
        pickSegments.forEach((segment, locations) -> {

            final Map<Pair<Long, Long>, MatrixCell> segmentPickCells = segment.getMatrix().getSegmentPickCells();

            segmentPickCells.forEach((coordinates, cell) -> {

                locations.forEach(location -> {
                    if (isLocationInCell(location, cell)) {
                        cell.addToPickLocations(location);
                    }
                });

            });
        });
    }

    private boolean isLocationInCell(final PickLocationViewDO location, final MatrixCell cell) {
        return location.getX() < cell.getMaxX() && location.getX() >= cell.getMinX() && location.getY() < cell.getMaxY() && location.getY() >= cell.getMinY();
    }

    protected Set<PickSegment> generateSegmentMatricesAndCells(final Map<PickSegment, List<PickLocationViewDO>> pickSegments) throws MatrixException {
        generateSegmentMatrix(pickSegments);
        assignPickLocationsToMatrixCells(pickSegments);
        return finishSegmentMatrix(pickSegments);
    }

    private Set<PickSegment> finishSegmentMatrix(final Map<PickSegment, List<PickLocationViewDO>> pickSegments) throws MatrixException {
        final Set<PickSegment> pickingSegmentsMatrices = getPickingSegments(pickSegments);
        assignNeighboringMatrixCells(pickingSegmentsMatrices);
        return pickingSegmentsMatrices;
    }

    private Set<PickSegment> getPickingSegments(final Map<PickSegment, List<PickLocationViewDO>> pickSegments) {
        return Collections.unmodifiableSet(pickSegments.keySet());
    }

    protected void assignNeighboringMatrixCells(final Set<PickSegment> pickSegmentsMatrices) throws MatrixException {

        for (final PickSegment segment : pickSegmentsMatrices) {
            final Map<Pair<Long, Long>, MatrixCell> segmentPickCells = segment.getMatrix().getSegmentPickCells();

            for (final Pair<Long, Long> position : segmentPickCells.keySet()) {
                final MatrixCell cell = segmentPickCells.get(position);

                final long maxRow = segment.getMatrix().getRows();
                final long maxColumn = segment.getMatrix().getColumns();

                final long row = position.getLeft();
                final long column = position.getRight();

                final List<Pair<Long, Long>> potentialNeighbors = getPotentialNeighbors(row, column);
                final Set<Pair<Long, Long>> neighbors = refinePotentialNeighbors(potentialNeighbors, maxRow, maxColumn);

                cell.addToNeighborPickingLocations(neighbors);
            }
        }
    }

    private List<Pair<Long, Long>> getPotentialNeighbors(final long baseRow, final long baseColumn) {
        final List<Pair<Long, Long>> neighbors = new ArrayList<>();

        for (long row = -1; row < 2; row++) {
            for (long column = -1; column < 2; column++) {
                addNewNeighbor(neighbors, baseRow, baseColumn, row, column);
            }
        }

        return Collections.unmodifiableList(neighbors);
    }

    private void addNewNeighbor(final List<Pair<Long, Long>> neighbors, final long baseRow, final long baseColumn, final long rowIndex, final long columnIndex) {
        if (!(rowIndex == 0 && columnIndex == 0)) {
            neighbors.add(new ImmutablePair<>(baseRow + rowIndex, baseColumn + columnIndex));
        }
    }

    private Set<Pair<Long, Long>> refinePotentialNeighbors(final List<Pair<Long, Long>> potentialNeighbors, final long maxRow, final long maxColumn) throws MatrixException {
        final List<Pair<Long, Long>> refinedNeighbors = potentialNeighbors.stream().filter(neighbor -> isValidNeighbor(neighbor, maxRow, maxColumn)).collect(Collectors.toList());
        return Collections.unmodifiableSet(getValidatedCellNeighbors(refinedNeighbors));
    }

    private boolean isValidNeighbor(final Pair<Long, Long> neighbor, final long maxRow, final long maxColumn) {
        return neighbor.getLeft() >= 0 && neighbor.getLeft() <= maxRow && neighbor.getRight() >= 0 && neighbor.getRight() <= maxColumn;
    }

    private Set<Pair<Long, Long>> getValidatedCellNeighbors(final List<Pair<Long, Long>> refinedNeighbors) throws CellNeighborsInconsistencyException {
        final Set<Pair<Long, Long>> neighbors = new HashSet<>(refinedNeighbors);

        if (neighbors.size() != refinedNeighbors.size()) {
            throw new CellNeighborsInconsistencyException(neighbors.size(), refinedNeighbors.size());
        }

        return neighbors;
    }
}
