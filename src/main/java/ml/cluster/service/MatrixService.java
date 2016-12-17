package ml.cluster.service;

import ml.cluster.datastructure.matrix.PickSegment;
import ml.cluster.error.CellNoAreaSpecifiedException;
import ml.cluster.error.MatrixException;
import ml.cluster.error.MatrixNoAreaSpecifiedException;
import ml.cluster.to.PickLocationViewDO;

import java.util.List;
import java.util.Map;

public interface MatrixService {
    Map<PickSegment, List<PickLocationViewDO>> getSegmentedLocations(List<PickLocationViewDO> pickLocationViewDOs) throws MatrixException;
}
