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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class OpticsServiceImplTest {

    @Spy
    private MatrixServiceImpl matrixService;

    @Spy
    private OpticsNeighboursServiceImpl opticsNeighboursService;

    @InjectMocks
    private OpticsServiceImpl opticsService;

    private Set<Segment> segmentsMatrices;
    private final static int LOCATION_POINT_QUANTITY = 10;

    @Before
    public void setUp() throws MatrixException {
        final Map<String, List<Point>> segmentGroups = TestLocationPointsGenerator.generateGroupedSpecificLocationPoints();
        final Map<Segment, List<Point>> pickSegments = matrixService.defineSegmentBoundaries(segmentGroups);
        matrixService.generateSegmentMatrix(pickSegments);
        matrixService.assignLocationPointsToMatrixCells(pickSegments);

        segmentsMatrices = matrixService.generateSegmentMatricesAndCells(pickSegments);
        matrixService.assignNeighboringMatrixCells(segmentsMatrices);
    }

    @Test
    public void testGetOrderingPoints() {
        final List<Point> result = opticsService.getOrderingPoints(segmentsMatrices);
        assertThat("", result, (is(notNullValue())));

    }

}