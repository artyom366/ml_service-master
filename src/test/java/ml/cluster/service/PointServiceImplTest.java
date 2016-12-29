package ml.cluster.service;

import ml.cluster.datastructure.optics.Point;
import ml.cluster.error.location.LocationMissingCoordinatesException;
import ml.cluster.error.location.LocationMissingDeliveryParametersException;
import ml.cluster.to.PickLocationViewDO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class PointServiceImplTest {

    @Spy
    private PointServiceImpl pointService;

    private List<PickLocationViewDO> locations;

    private final static double X = 5;
    private final static double Y = 5;
    private final static String LINE = "A";
    private final static long CLIENT_ID = 1;
    private final static long CUSTOMER_ID = 2;
    private final static long DELIVERY_ID = 3;

    @Before
    public void setUpTest() {
        final PickLocationViewDO location = new PickLocationViewDO();
        locations = Collections.singletonList(location);
    }

    @Test
    public void testGetPointsValid() throws Exception {
        mockValidLocation();

        final List<Point> result = pointService.getPoints(locations);
        assertThat("Points should not be null", result, is(notNullValue()));
        assertThat("Points count should be equal to location count", result.size(), is(locations.size()));
    }

    private void mockValidLocation() {
        locations.get(0).setX(X);
        locations.get(0).setY(Y);
        locations.get(0).setLine(LINE);
        locations.get(0).setClientId(CLIENT_ID);
        locations.get(0).setCustomerId(CUSTOMER_ID);
        locations.get(0).setDeliveryId(DELIVERY_ID);
    }

    @Test(expected = LocationMissingCoordinatesException.class)
    public void testGetPointsNotValidLocation() throws Exception {
        pointService.getPoints(locations);
    }

    @Test(expected = LocationMissingDeliveryParametersException.class)
    public void testGetPointsNotValidDeliveryParameters() throws Exception {
        mockNotValidDeliveryParametersLocation();
        pointService.getPoints(locations);
    }

    private void mockNotValidDeliveryParametersLocation() {
        locations.get(0).setX(X);
        locations.get(0).setY(Y);
        locations.get(0).setLine(LINE);
    }
}