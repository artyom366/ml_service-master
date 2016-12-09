package ml.cluster.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import ml.cluster.datastructure.matrix.FixedRadiusMatrix;
import ml.cluster.datastructure.matrix.MatrixCell;
import ml.cluster.datastructure.matrix.PickSegment;
import ml.cluster.datastructure.optics.Optics;
import ml.cluster.datastructure.optics.Point;
import ml.cluster.to.PickLocationViewDO;

@Service("opticsService")
public class OpticsServiceImpl implements OpticsService {

	@Override
	public void getOrderingPoints(final Map<PickSegment, List<PickLocationViewDO>> pickSegments) {
		Validate.notEmpty(pickSegments, "Pick locations are not defined");

		final Optics optics = new Optics.OpticsBuilder().minPts(5).build();
        processOnSegmentMatrixLevel(pickSegments, optics);
	}

	private void processOnSegmentMatrixLevel(final Map<PickSegment, List<PickLocationViewDO>> pickSegments, final Optics optics) {
		pickSegments.forEach((segment, locations) -> {
			final FixedRadiusMatrix matrix = segment.getMatrix();
			final Map<Pair<Long, Long>, MatrixCell> cells = matrix.getSegmentPickCells();
            processOnMatrixCellLevel(cells, optics);
		});
	}

	private void processOnMatrixCellLevel(Map<Pair<Long, Long>, MatrixCell> cells, final Optics optics) {

        cells.forEach((position, cell) -> {

		});
	}

    private void getNearestNeighbors(final Map<Pair<Long, Long>, MatrixCell> cells, final MatrixCell cell) {
        final List<PickLocationViewDO> cellLocations = cell.getLocations();

        if (cellLocations.isEmpty()) {
            return;
        }

        for (int i = 0; i < cellLocations.size(); i++) {
            for (int j = i + 1; j < cellLocations.size(); j++) {

            }
        }



    }

}