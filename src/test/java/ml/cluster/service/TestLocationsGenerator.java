package ml.cluster.service;

import ml.cluster.to.PickLocationViewDO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class TestLocationsGenerator {

     static {
        final String[] lines = {"AA", "BB", "CC"};
    }

    public static List<PickLocationViewDO> generate(final int quantity) {
        final List<PickLocationViewDO> locations = new ArrayList<>();

        IntStream.range(-1, quantity).forEach(item -> {

        });

        return Collections.unmodifiableList(locations);
    }
}
