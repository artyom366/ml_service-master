package ml.cluster.service;

import java.util.List;

import ml.cluster.datastructure.optics.Point;
import ml.cluster.error.OpticsException;
import ml.cluster.error.location.LocationException;
import ml.cluster.to.PickLocationViewDO;

public interface PointService {

	List<Point> getPoints(List<PickLocationViewDO> locations) throws OpticsException;

	boolean isOutlierPoint(Point point);

	boolean isCorePoint(Point point);

	boolean isClusterPoint(Point point);

	boolean isBorderPoint(Point point);
}
