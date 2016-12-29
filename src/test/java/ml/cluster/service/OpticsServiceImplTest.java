package ml.cluster.service;

import ml.cluster.datastructure.optics.Point;
import ml.cluster.datastructure.segment.PickSegment;
import ml.cluster.error.MatrixException;
import ml.cluster.to.PickLocationViewDO;
import org.junit.Before;
import org.junit.Ignore;
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

    private Set<PickSegment> pickSegmentsMatrices;

    @Before
    public void setUp() throws MatrixException {
        final Map<String, List<Point>> segmentGroups = TestLocationsGenerator.generateGroupedLocations(100);
        final Map<PickSegment, List<Point>> pickSegments = matrixService.defineSegmentBoundaries(segmentGroups);
        matrixService.generateSegmentMatrix(pickSegments);
        matrixService.assignPickLocationsToMatrixCells(pickSegments);

        pickSegmentsMatrices = matrixService.generateSegmentMatricesAndCells(pickSegments);
        matrixService.assignNeighboringMatrixCells(pickSegmentsMatrices);
    }

    @Test
    public void test() {
        opticsService.getOrderingPoints(pickSegmentsMatrices);
    }

}