package ml.cluster.service;

import ml.cluster.datastructure.FixedRadiusMatrix;
import ml.cluster.datastructure.MatrixCell;
import ml.cluster.datastructure.PickSegment;
import ml.cluster.to.PickLocationViewDO;
import org.apache.commons.lang3.Validate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service("opticsService")
public class OpticsServiceImpl implements OpticsService {

    @Override
    public void getOptics(final Map<PickSegment, List<PickLocationViewDO>> pickSegments) {
        Validate.notEmpty(pickSegments, "Pick locations are not defined");


    }

}