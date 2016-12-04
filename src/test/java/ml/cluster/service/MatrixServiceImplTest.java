package ml.cluster.service;

import ml.cluster.to.PickLocationViewDO;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class MatrixServiceImplTest {

    private final static int NUMBER_OF_LOCATIONS = 200;

    private static List<PickLocationViewDO> LOCATIONS;

    @Spy
    private MatrixServiceImpl matrixService;

    @BeforeClass
    public static void prepareTest() {
        LOCATIONS = TestLocationsGenerator.generate(NUMBER_OF_LOCATIONS);
    }

    @Test
    public void testGetSegmentedLocations() throws Exception {
        final Map<String, List<PickLocationViewDO>> result = matrixService.groupByLine(LOCATIONS);
        assertThat(result, is(notNullValue()));
        assertThat(result.size(), is(TestLocationsGenerator.getLocationLinesQuantity()));

        final int locationCount = result.values().stream().mapToInt(List::size).sum();
        assertThat(NUMBER_OF_LOCATIONS, is(locationCount));
    }

    @Test
    public void testDefineSegmentBoundaries() {




    }

}