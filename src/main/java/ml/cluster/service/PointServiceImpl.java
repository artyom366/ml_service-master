package ml.cluster.service;

import ml.cluster.datastructure.optics.Point;
import ml.cluster.error.location.LocationException;
import ml.cluster.error.location.LocationMissingCoordinatesException;
import ml.cluster.error.location.LocationMissingDeliveryParametersException;
import ml.cluster.to.PickLocationViewDO;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("pointService")
public class PointServiceImpl implements PointService {

    @Override
    public List<Point> getPoints(final List<PickLocationViewDO> locations) throws LocationException {
        Validate.notEmpty(locations, "Locations are not defined");

        final List<Point> points = new ArrayList<>();

        for (final PickLocationViewDO location : locations) {
            validateLocation(location);
            final Point point = Point.newInstance(location);
            points.add(point);
        }

        return points;
    }

    private void validateLocation(final PickLocationViewDO location) throws LocationException {
        if (location.getX() == null && location.getY() == null && location.getLine() == null) {
            throw new LocationMissingCoordinatesException(String.format("X: %s, Y: %s, Line: %s", location.getX(), location.getY(), location.getLine()));

        } else if (location.getClientId() == null && location.getCustomerId() == null && location.getDeliveryId() == null) {
            throw new LocationMissingDeliveryParametersException(String.format("Client id: %s, Customer id: %s, Delivery id: %s",
                    location.getClientId(), location.getCustomerId(), location.getDeliveryId()));
        }
    }
}
