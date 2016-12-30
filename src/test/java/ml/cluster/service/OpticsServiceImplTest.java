package ml.cluster.service;

import ml.cluster.datastructure.optics.Point;
import ml.cluster.datastructure.segment.Segment;
import ml.cluster.error.MatrixException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class OpticsServiceImplTest {

    @Spy
    private MatrixServiceImpl matrixService;

    @Spy
    private OpticsNeighboursServiceImpl opticsNeighboursService;

    @InjectMocks
    private OpticsServiceImpl opticsService;

    private Set<Segment> segmentsMatrices;

    @Before
    public void setUp() throws MatrixException {
        final Map<String, List<Point>> segmentGroups = TestLocationPointsGenerator.generateGroupedLocationPoints(20, 10, 10);
        final Map<Segment, List<Point>> pickSegments = matrixService.defineSegmentBoundaries(segmentGroups);
        matrixService.generateSegmentMatrix(pickSegments);
        matrixService.assignLocationPointsToMatrixCells(pickSegments);

        segmentsMatrices = matrixService.generateSegmentMatricesAndCells(pickSegments);
        matrixService.assignNeighboringMatrixCells(segmentsMatrices);
    }

    @Test
    public void test() {
        opticsService.getOrderingPoints(segmentsMatrices);
    }

}