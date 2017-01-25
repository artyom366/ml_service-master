package ml.cluster.datastructure.optics.impl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.Validate;

import ml.cluster.datastructure.optics.Cluster;
import ml.cluster.datastructure.optics.OpticsSummary;
import ml.cluster.datastructure.optics.Point;

public final class OpticsSummaryImpl implements OpticsSummary {

	private final List<Point> outlierPoints;
	private final List<Cluster> clusters;

	public OpticsSummaryImpl() {
		this.clusters = new LinkedList<>();
		this.outlierPoints = new LinkedList<>();
	}

	public OpticsSummaryImpl(final List<Cluster> clusters, final List<Point> outlierPoints) {
		this.clusters = new LinkedList<>(clusters);
		this.outlierPoints = new LinkedList<>(outlierPoints);
	}

	public void addToClusters(final Cluster cluster) {
		Validate.notNull(cluster, "ClusterImpl is not defined");
		this.clusters.add(cluster);
	}

	@Override
	public List<Cluster> getClusters() {
		return Collections.unmodifiableList(clusters);
	}

	public void addToOutlierPoints(final Point point) {
		Validate.notNull(point, "PointImpl is not defined");
		this.outlierPoints.add(point);
	}

	public List<Point> getOutlierPoints() {
		return Collections.unmodifiableList(outlierPoints);
	}

	@Override
	public boolean clustersExist() {
		return !this.clusters.isEmpty();
	}

	@Override
	public int getClusterCount() {
		return this.clusters.size();
	}

	@Override
	public boolean outlierPointExist() {
		return !this.outlierPoints.isEmpty();
	}

	@Override
	public int getOutlierPointCount() {
		return this.outlierPoints.size();
	}
 }
