package ml.cluster.service;

import ml.cluster.datastructure.PickSegment;
import ml.cluster.jpa.PickLocationService;
import ml.cluster.to.PickLocationViewDO;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class MatrixServiceImplTest {

    private final static int NUMBER_OF_LOCATIONS = 200;

    private static List<PickLocationViewDO> LOCATIONS;

    @BeforeClass
    public static void prepareTest() {
        LOCATIONS = TestLocationsGenerator.generate(NUMBER_OF_LOCATIONS);
    }

    @Test
    public void testGetSegmentedLocations() throws Exception {




    }

}