package ml.cluster.service;

import java.util.List;

import ml.cluster.datastructure.optics.OpticsPoint;
import ml.cluster.error.location.LocationException;
import ml.cluster.to.PickLocationViewDO;

public interface PointService {

	List<OpticsPoint> getPoints(List<PickLocationViewDO> locations) throws LocationException;

	boolean isOutlierPoint(OpticsPoint point);

	boolean isCorePoint(OpticsPoint point);

	boolean isClusterPoint(OpticsPoint point);

	boolean isBorderPoint(OpticsPoint point);
}
