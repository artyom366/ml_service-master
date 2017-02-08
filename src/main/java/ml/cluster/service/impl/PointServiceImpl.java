package ml.cluster.service.impl;

import ml.cluster.datastructure.optics.Point;
import ml.cluster.datastructure.optics.impl.PointImpl;
import ml.cluster.error.OpticsException;
import ml.cluster.error.location.LocationException;
import ml.cluster.error.location.LocationMissingCoordinatesException;
import ml.cluster.error.location.LocationMissingDeliveryParametersException;
import ml.cluster.service.PointService;
import ml.cluster.to.PickLocationViewDO;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service("pointService")
public class PointServiceImpl implements PointService {

    @Override
    public List<Point> getPoints(final List<PickLocationViewDO> locations) throws OpticsException {
        Validate.notEmpty(locations, "Locations are not defined");

        final List<Point> points = new ArrayList<>();

        for (final PickLocationViewDO location : locations) {
            validateLocation(location);
            final Point point = PointImpl.newInstance(location);
            points.add(point);
        }

        return Collections.unmodifiableList(points);
    }

    private void validateLocation(final PickLocationViewDO location) throws OpticsException {
        if (location.getX() == null && location.getY() == null && location.getLine() == null) {
            throw new LocationMissingCoordinatesException(String.format("X: %s, Y: %s, Line: %s", location.getX(), location.getY(), location.getLine()));

        } else if (location.getClientId() == null && location.getCustomerId() == null && location.getDeliveryId() == null) {
            throw new LocationMissingDeliveryParametersException(String.format("Client id: %s, Customer id: %s, Delivery id: %s",
                    location.getClientId(), location.getCustomerId(), location.getDeliveryId()));
        }
    }

    @Override
    public boolean isOutlierPoint(final Point point) {
        Validate.notNull(point, "OpticsImpl point is not defined");
        return point.getReachabilityDistance() == Double.POSITIVE_INFINITY && point.getCoreDistance() == Double.POSITIVE_INFINITY;
    }

    @Override
    public boolean isCorePoint(final Point point) {
        Validate.notNull(point, "OpticsImpl point is not defined");
        return point.getCoreDistance() < Double.POSITIVE_INFINITY && point.getReachabilityDistance() == Double.POSITIVE_INFINITY;
    }

    @Override
    public boolean isClusterPoint(final Point point) {
        Validate.notNull(point, "OpticsImpl point is not defined");
        return point.getCoreDistance() < Double.POSITIVE_INFINITY && point.getReachabilityDistance() < Double.POSITIVE_INFINITY;
    }

    @Override
    public boolean isBorderPoint(final Point point) {
        Validate.notNull(point, "OpticsImpl point is not defined");
        return point.getCoreDistance() == Double.POSITIVE_INFINITY && point.getReachabilityDistance() < Double.POSITIVE_INFINITY;
    }
}
