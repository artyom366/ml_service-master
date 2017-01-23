package ml.cluster.datastructure.optics;

import org.apache.commons.lang3.tuple.Pair;

public interface OpticsPoint {

	boolean isProcessed();

	double getCoreDistance();

	double getReachabilityDistance();

	Double getX();

	Double getY();

	String getLine();

	Pair<Long, Long> getCell();

	void setProcessed(boolean isProcessed);

	void setCoreDistance(double codeDistance);

	void setReachabilityDistance(double reachabilityDistance);

	void setCell(Pair<Long, Long> cell);
}
