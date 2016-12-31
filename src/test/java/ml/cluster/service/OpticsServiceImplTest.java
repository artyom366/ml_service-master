package ml.cluster.service;

import ml.cluster.datastructure.optics.Point;
import ml.cluster.datastructure.segment.Segment;
import ml.cluster.error.matrix.MatrixException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static ml.cluster.service.TestLocationPointsGenerator.generateGroupedSpecificLocationPoints;
import static ml.cluster.service.TestLocationPointsGenerator.getSpecificLocationPointsQuantity;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

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
        final Map<String, List<Point>> segmentGroups = generateGroupedSpecificLocationPoints();
        final Map<Segment, List<Point>> pickSegments = matrixService.defineSegmentBoundaries(segmentGroups);
        matrixService.generateSegmentMatrix(pickSegments);
        matrixService.assignLocationPointsToMatrixCells(pickSegments);

        segmentsMatrices = matrixService.generateSegmentMatricesAndCells(pickSegments);
        matrixService.assignNeighboringMatrixCells(segmentsMatrices);
    }

    @Test
    public void testGetOrderingPoints() {
        final List<Point> result = opticsService.getOrderingPoints(segmentsMatrices);
        assertThat("Ordering location points should not be null", result, (is(notNullValue())));
        assertThat("Ordering location points count should equals to total location points count", result.size(), is(getSpecificLocationPointsQuantity()));

        int coreLocationPointsCounter = 0;
        int clusterLocationPointCounter = 0;
        int borderLocationPointCounter = 0;
        int outlierLocationPointsCounter = 0;

        for (final Point locationPoint : result) {
            assertThat("Ordering location point should be processed", locationPoint.isProcessed(), is(true));

            if (isCoreLocationPoint(locationPoint)) {
                coreLocationPointsCounter++;

            } else if (isClusterLocationPoint(locationPoint)) {
                clusterLocationPointCounter++;

            } else if (isBorderLocationPoint(locationPoint)) {
                borderLocationPointCounter++;

            } else if (isOutlierLocationPoint(locationPoint)) {
                outlierLocationPointsCounter++;

            } else {
                fail("This should never happen, but it is happened, unclassified location point detected");
            }
        }

        assertThat("There should be 2 cluster, hence 2 core location points", coreLocationPointsCounter, is(2));
        assertThat("There should be 12 cluster location point in all the clusters", clusterLocationPointCounter, is(12));
        assertThat("There should be 2 border location point in all the clusters", borderLocationPointCounter, is(2));
        assertThat("There should be 1 outlier location point", outlierLocationPointsCounter, is(1));
    }

    private boolean isCoreLocationPoint(final Point locationPoint) {
        return locationPoint.getCoreDistance() < Double.POSITIVE_INFINITY && locationPoint.getReachabilityDistance() == Double.POSITIVE_INFINITY;
    }

    private boolean isClusterLocationPoint(final Point locationPoint) {
        return locationPoint.getCoreDistance() < Double.POSITIVE_INFINITY && locationPoint.getReachabilityDistance() < Double.POSITIVE_INFINITY;
    }

    private boolean isBorderLocationPoint(final Point locationPoint) {
        return locationPoint.getCoreDistance() == Double.POSITIVE_INFINITY && locationPoint.getReachabilityDistance() < Double.POSITIVE_INFINITY;
    }

    private boolean isOutlierLocationPoint(final Point locationPoint) {
        return locationPoint.getCoreDistance() == Double.POSITIVE_INFINITY && locationPoint.getReachabilityDistance() == Double.POSITIVE_INFINITY;
    }

}