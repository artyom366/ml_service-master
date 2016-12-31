package ml.cluster.service;

import ml.cluster.datastructure.matrix.FixedRadiusMatrix;
import ml.cluster.datastructure.matrix.MatrixCell;
import ml.cluster.datastructure.optics.Point;
import ml.cluster.datastructure.segment.Segment;
import ml.cluster.error.matrix.CellNeighborsInconsistencyException;
import ml.cluster.error.matrix.CellNoAreaSpecifiedException;
import ml.cluster.error.matrix.MatrixException;
import ml.cluster.error.matrix.MatrixNoAreaSpecifiedException;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("matrixService")
public class MatrixServiceImpl implements MatrixService {

    @Override
    public Set<Segment> getSegmentedLocationPoints(final List<Point> points) throws MatrixException {
        Validate.notEmpty(points, "Location points are not defined");

        final Map<String, List<Point>> segmentGroups = groupLocationPointsInSegment(points);
        final Map<Segment, List<Point>> segments = defineSegmentBoundaries(segmentGroups);

        return generateSegmentMatricesAndCells(segments);
    }

    protected Map<String, List<Point>> groupLocationPointsInSegment(final List<Point> points) {
        return Collections.unmodifiableMap(points.stream().collect(Collectors.groupingBy(Point::getLine)));
    }

    protected Map<Segment, List<Point>> defineSegmentBoundaries(final Map<String, List<Point>> segmentGroups) {
        final Map<Segment, List<Point>> segments = new HashMap<>();

        segmentGroups.forEach((line, locationPoints) -> {
            final DoubleSummaryStatistics xStats = locationPoints.stream().mapToDouble(Point::getX).summaryStatistics();
            final DoubleSummaryStatistics yStats = locationPoints.stream().mapToDouble(Point::getY).summaryStatistics();

            final double minY = yStats.getMin();
            final double maxY = yStats.getMax();
            final double minX = xStats.getMin();
            final double maxX = xStats.getMax();

            segments.put(new Segment(line, minY, maxY, minX, maxX), locationPoints);
        });

        return Collections.unmodifiableMap(segments);
    }

    protected Set<Segment> generateSegmentMatricesAndCells(final Map<Segment, List<Point>> segments) throws MatrixException {
        generateSegmentMatrix(segments);
        assignLocationPointsToMatrixCells(segments);
        return finishSegmentMatrixCreation(segments);
    }

    protected void generateSegmentMatrix(final Map<Segment, List<Point>> segments) throws MatrixException {
        for (final Segment segment : segments.keySet()) {

            final double matrixHeight = segment.getMaxY() - segment.getMinY();
            final double matrixWidth = segment.getMaxX() - segment.getMinX();
            validateMatrixSize(matrixHeight, matrixWidth);

            final FixedRadiusMatrix matrix = new FixedRadiusMatrix.MatrixBuilder()
                    .height(matrixHeight)
                    .width(matrixWidth)
                    .build();

            validateCellSize(matrix);

            segment.setMatrix(matrix);
            generateCells(segment, 0, 0, 0L, 0L);
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

    private void generateCells(final Segment segment, final double currentHeight, final double currentWidth, final long currentRow, final long currentColumn) {

        if (segment.getMatrix().getRows() > currentRow) {

            if (segment.getMatrix().getColumns() > currentColumn) {
                createMatrixCell(segment, currentRow, currentColumn);
                generateCells(segment, currentHeight, currentWidth + segment.getMatrix().getCellWidth(), currentRow, currentColumn + 1);

            } else if (segment.getMatrix().getRows() > currentRow + 1) {
                createMatrixCell(segment, currentRow, 0);
                generateCells(segment, currentHeight + segment.getMatrix().getCellHeight(), 0, currentRow + 1, 0);
            }
        }
    }

    private void createMatrixCell(final Segment segment, final long currentRow, final long currentColumn) {

        final long cellMinX = (long) segment.getMinX() + currentColumn * segment.getMatrix().getCellWidth();
        final long cellMaxX = cellMinX + segment.getMatrix().getCellWidth();
        final long cellMinY = (long) segment.getMinY() + currentRow * segment.getMatrix().getCellHeight();
        final long cellMaxY = cellMinY + segment.getMatrix().getCellHeight();

        final MatrixCell cell = new MatrixCell(cellMaxX, cellMinX, cellMaxY, cellMinY);
        segment.getMatrix().addCell(new ImmutablePair<>(currentRow, currentColumn), cell);
    }

    protected void assignLocationPointsToMatrixCells(final Map<Segment, List<Point>> segments) {
        segments.forEach((segment, locationPoints) -> {

            final Map<Pair<Long, Long>, MatrixCell> cells = segment.getMatrix().getCells();

            cells.forEach((position, cell) -> {

                locationPoints.forEach(locationPoint -> {
                    if (locationPointCoordinatesMatchesCell(locationPoint, cell)) {
                        cell.addToLocationPoints(locationPoint);
                        locationPoint.setCell(position);
                    }
                });

            });
        });
    }

    private boolean locationPointCoordinatesMatchesCell(final Point Point, final MatrixCell cell) {
        return Point.getX() < cell.getMaxX() && Point.getX() >= cell.getMinX() && Point.getY() < cell.getMaxY() && Point.getY() >= cell.getMinY();
    }

    private Set<Segment> finishSegmentMatrixCreation(final Map<Segment, List<Point>> segments) throws MatrixException {
        final Set<Segment> segmentMatrices = getSegments(segments);
        assignNeighboringMatrixCells(segmentMatrices);
        return segmentMatrices;
    }

    private Set<Segment> getSegments(final Map<Segment, List<Point>> segments) {
        return Collections.unmodifiableSet(segments.keySet());
    }

    protected void assignNeighboringMatrixCells(final Set<Segment> segmentsMatrices) throws MatrixException {

        for (final Segment segment : segmentsMatrices) {
            final Map<Pair<Long, Long>, MatrixCell> cells = segment.getMatrix().getCells();

            for (final Pair<Long, Long> position : cells.keySet()) {
                final MatrixCell cell = cells.get(position);

                if (cell.getLocationPoints().isEmpty()) {
                    continue;
                }

                final long maxRow = segment.getMatrix().getRows();
                final long maxColumn = segment.getMatrix().getColumns();

                final long row = position.getLeft();
                final long column = position.getRight();

                final List<Pair<Long, Long>> potentialNeighbors = getPotentialNeighbors(row, column);
                final Set<Pair<Long, Long>> neighbors = refinePotentialNeighbors(potentialNeighbors, maxRow, maxColumn);

                cell.addToNeighboringCells(neighbors);
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

        return Collections.unmodifiableSet(neighbors);
    }
}
