package ml.cluster.datastructure.optics;

import ml.cluster.to.PickLocationViewDO;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;

public final class Point {

	private Boolean isProcessed;
	private Double coreDistance;
	private Double reachabilityDistance;
	private Pair<Long, Long> cell;
	private final Long id;
	private final Double x;
	private final Double y;
	private final String line;
	private final String scanCode;
	private final Long deliveryId;
	private final Long customerId;
	private final Long trx;
	private final Long coSeq;
	private final Long clientId;
	private final Long wrin;
	private final BigDecimal quantity;
	private final Long goodsId;
	private final String warehouseId;
	private final String location;
	private final Long pickSeq;
	private final String pickZoneId;
	private final BigDecimal grWeight;
	private final BigDecimal cbm;
	private final Integer refillEvent;
	private final BigDecimal quantityBase;
	private final Integer pslipTypeId;
	private final String dPack;

	private Point(final Long id,
				 final Double x,
				 final Double y,
				 final String line,
				 final String scanCode,
				 final Long deliveryId,
				 final Long customerId,
				 final Long trx,
				 final Long coSeq,
				 final Long clientId,
				 final Long wrin,
				 final BigDecimal quantity,
				 final Long goodsId,
				 final String warehouseId,
				 final String location,
				 final Long pickSeq,
				 final String pickZoneId,
				 final BigDecimal grWeight,
				 final BigDecimal cbm,
				 final Integer refillEvent,
				 final BigDecimal quantityBase,
				 final Integer pslipTypeId,
				 final String dPack) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.line = line;
		this.scanCode = scanCode;
		this.deliveryId = deliveryId;
		this.customerId = customerId;
		this.trx = trx;
		this.coSeq = coSeq;
		this.clientId = clientId;
		this.wrin = wrin;
		this.quantity = quantity;
		this.goodsId = goodsId;
		this.warehouseId = warehouseId;
		this.location = location;
		this.pickSeq = pickSeq;
		this.pickZoneId = pickZoneId;
		this.grWeight = grWeight;
		this.cbm = cbm;
		this.refillEvent = refillEvent;
		this.quantityBase = quantityBase;
		this.pslipTypeId = pslipTypeId;
		this.dPack = dPack;
		this.coreDistance = Double.POSITIVE_INFINITY;
		this.reachabilityDistance = Double.POSITIVE_INFINITY;
		this.isProcessed = false;
	}

	public static Point newInstance(final PickLocationViewDO location) {
		Validate.notNull(location, "Location is not defined");

		return new Point(location.getId(),
				location.getX(),
				location.getY(),
				location.getLine(),
				location.getScanCode(),
				location.getDeliveryId(),
				location.getCustomerId(),
				location.getTrx(),
				location.getCoSeq(),
				location.getClientId(),
				location.getWrin(),
				location.getQuantity(),
				location.getGoodsId(),
				location.getWarehouseId(),
				location.getLocation(),
				location.getPickSeq(),
				location.getPickZoneId(),
				location.getGrWeight(),
				location.getCbm(),
				location.getRefillEvent(),
				location.getQuantityBase(),
				location.getPslipTypeId(),
				location.getdPack());
	}

	public Long getId() {
		return id;
	}

	public Double getX() {
		return x;
	}

	public Double getY() {
		return y;
	}

	public String getLine() {
		return line;
	}

	public String getScanCode() {
		return scanCode;
	}

	public Long getDeliveryId() {
		return deliveryId;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public Long getTrx() {
		return trx;
	}

	public Long getCoSeq() {
		return coSeq;
	}

	public Long getClientId() {
		return clientId;
	}

	public Long getWrin() {
		return wrin;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public String getWarehouseId() {
		return warehouseId;
	}

	public String getLocation() {
		return location;
	}

	public Long getPickSeq() {
		return pickSeq;
	}

	public String getPickZoneId() {
		return pickZoneId;
	}

	public BigDecimal getGrWeight() {
		return grWeight;
	}

	public BigDecimal getCbm() {
		return cbm;
	}

	public Integer getRefillEvent() {
		return refillEvent;
	}

	public BigDecimal getQuantityBase() {
		return quantityBase;
	}

	public Integer getPslipTypeId() {
		return pslipTypeId;
	}

	public String getdPack() {
		return dPack;
	}

	public boolean isProcessed() {
		return isProcessed;
	}

	public void setProcessed(boolean processed) {
		isProcessed = processed;
	}

	public double getCoreDistance() {
		return coreDistance;
	}

	public void setCoreDistance(double coreDistance) {
		this.coreDistance = coreDistance;
	}

	public double getReachabilityDistance() {
		return reachabilityDistance;
	}

	public void setReachabilityDistance(double reachabilityDistance) {
		this.reachabilityDistance = reachabilityDistance;
	}

	public Pair<Long, Long> getCell() {
		return cell;
	}

	public void setCell(Pair<Long, Long> cell) {
		this.cell = cell;
	}
}
