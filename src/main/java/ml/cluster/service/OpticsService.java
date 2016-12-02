package ml.cluster.service;

import ml.cluster.to.PickLocation;

import java.util.List;

public interface OpticsService {
    void getOptics(List<PickLocation> pickLocationList);
}
