package ml.cluster.service;

import ml.cluster.datastructure.optics.OpticsPoint;
import ml.cluster.datastructure.optics.Point;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class OpticsNeighboursServiceImplTest {

    private final static int MAX_X_AXIS_VALUE = 10;
    private final static int MAX_Y_AXIS_VALUE = 10;
    private final static int NEIGHBOURING_LOCATIONS_COUNT = (MAX_X_AXIS_VALUE + MAX_X_AXIS_VALUE) / 2;
    private final static int RADIUS = (MAX_X_AXIS_VALUE + MAX_X_AXIS_VALUE) / 4;
    private final static int MIN_PTS = 5;
    private final static double CENTER_X = MAX_X_AXIS_VALUE / 2;
    private final static double CENTER_Y = MAX_Y_AXIS_VALUE / 2;

    @Spy
    private OpticsNeighboursServiceImpl opticsNeighboursService;

    @Test
    public void testGetNearestNeighbours() throws Exception {
        final List<OpticsPoint> neighboringLocations = TestLocationPointsGenerator.generateLocationPoints(NEIGHBOURING_LOCATIONS_COUNT, MAX_X_AXIS_VALUE, MAX_Y_AXIS_VALUE);
        final OpticsPoint centerLocation = TestLocationPointsGenerator.generateSingleLocationPoint(CENTER_X, CENTER_Y);

        final List<OpticsPoint> result = opticsNeighboursService.getNearestNeighbours(centerLocation, neighboringLocations, RADIUS);
        assertThat("Nearest neighbours list should not be null", result, is(notNullValue()));
        assertThat("Nearest neighbours quantity should be greater then 0", result.isEmpty(), is(false));
        assertThat("Nearest neighbours quantity should be less then all neighboring locations quantity", result.size() < neighboringLocations.size(), is(true));

        result.forEach(point -> {
            final double x = point.getX();
            final double y = point.getY();
            assertThat("The distance between center point and its nearest neighbour should net be more then defined radius", Math.hypot((CENTER_X - x), (CENTER_Y - y)) <= RADIUS, is(true));
        });
    }

    @Test
    public void testGetCoreDistance() throws Exception {
        final Point currentPoint = TestLocationPointsGenerator.generateSingleLocationPoint(CENTER_X, CENTER_Y);
        final List<OpticsPoint> nearestNeighbours = TestLocationPointsGenerator.generateLocationPointWithDistance(NEIGHBOURING_LOCATIONS_COUNT, MAX_X_AXIS_VALUE, MAX_Y_AXIS_VALUE, RADIUS);

        final double result = opticsNeighboursService.getCoreDistance(currentPoint, nearestNeighbours, MIN_PTS);
        assertThat("Core distance should be a defined double value", result != Double.NaN, is(true));
    }

}