package ml.cluster.datastructure.optics;

import ml.cluster.to.PickLocationViewDO;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;

public final class Point implements OpticsPoint {

	private boolean isProcessed;
	private double coreDistance;
	private double reachabilityDistance;
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

	@Override
	public Double getX() {
		return x;
	}

	@Override
	public Double getY() {
		return y;
	}

	@Override
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

	@Override
	public boolean isProcessed() {
		return isProcessed;
	}

	@Override
	public void setProcessed(boolean processed) {
		isProcessed = processed;
	}

	@Override
	public double getCoreDistance() {
		return coreDistance;
	}

	@Override
	public void setCoreDistance(double coreDistance) {
		this.coreDistance = coreDistance;
	}

	@Override
	public double getReachabilityDistance() {
		return reachabilityDistance;
	}

	@Override
	public void setReachabilityDistance(double reachabilityDistance) {
		this.reachabilityDistance = reachabilityDistance;
	}

	@Override
	public Pair<Long, Long> getCell() {
		return cell;
	}

	@Override
	public void setCell(Pair<Long, Long> cell) {
		this.cell = cell;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Point point = (Point)o;

		if (isProcessed != point.isProcessed)
			return false;
		if (Double.compare(point.coreDistance, coreDistance) != 0)
			return false;
		if (Double.compare(point.reachabilityDistance, reachabilityDistance) != 0)
			return false;
		if (cell != null ? !cell.equals(point.cell) : point.cell != null)
			return false;
		if (id != null ? !id.equals(point.id) : point.id != null)
			return false;
		if (x != null ? !x.equals(point.x) : point.x != null)
			return false;
		if (y != null ? !y.equals(point.y) : point.y != null)
			return false;
		if (line != null ? !line.equals(point.line) : point.line != null)
			return false;
		if (scanCode != null ? !scanCode.equals(point.scanCode) : point.scanCode != null)
			return false;
		if (deliveryId != null ? !deliveryId.equals(point.deliveryId) : point.deliveryId != null)
			return false;
		if (customerId != null ? !customerId.equals(point.customerId) : point.customerId != null)
			return false;
		if (trx != null ? !trx.equals(point.trx) : point.trx != null)
			return false;
		if (coSeq != null ? !coSeq.equals(point.coSeq) : point.coSeq != null)
			return false;
		if (clientId != null ? !clientId.equals(point.clientId) : point.clientId != null)
			return false;
		if (wrin != null ? !wrin.equals(point.wrin) : point.wrin != null)
			return false;
		if (quantity != null ? !quantity.equals(point.quantity) : point.quantity != null)
			return false;
		if (goodsId != null ? !goodsId.equals(point.goodsId) : point.goodsId != null)
			return false;
		if (warehouseId != null ? !warehouseId.equals(point.warehouseId) : point.warehouseId != null)
			return false;
		if (location != null ? !location.equals(point.location) : point.location != null)
			return false;
		if (pickSeq != null ? !pickSeq.equals(point.pickSeq) : point.pickSeq != null)
			return false;
		if (pickZoneId != null ? !pickZoneId.equals(point.pickZoneId) : point.pickZoneId != null)
			return false;
		if (grWeight != null ? !grWeight.equals(point.grWeight) : point.grWeight != null)
			return false;
		if (cbm != null ? !cbm.equals(point.cbm) : point.cbm != null)
			return false;
		if (refillEvent != null ? !refillEvent.equals(point.refillEvent) : point.refillEvent != null)
			return false;
		if (quantityBase != null ? !quantityBase.equals(point.quantityBase) : point.quantityBase != null)
			return false;
		if (pslipTypeId != null ? !pslipTypeId.equals(point.pslipTypeId) : point.pslipTypeId != null)
			return false;
		return dPack != null ? dPack.equals(point.dPack) : point.dPack == null;

	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		result = (isProcessed ? 1 : 0);
		temp = Double.doubleToLongBits(coreDistance);
		result = 31 * result + (int)(temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(reachabilityDistance);
		result = 31 * result + (int)(temp ^ (temp >>> 32));
		result = 31 * result + (cell != null ? cell.hashCode() : 0);
		result = 31 * result + (id != null ? id.hashCode() : 0);
		result = 31 * result + (x != null ? x.hashCode() : 0);
		result = 31 * result + (y != null ? y.hashCode() : 0);
		result = 31 * result + (line != null ? line.hashCode() : 0);
		result = 31 * result + (scanCode != null ? scanCode.hashCode() : 0);
		result = 31 * result + (deliveryId != null ? deliveryId.hashCode() : 0);
		result = 31 * result + (customerId != null ? customerId.hashCode() : 0);
		result = 31 * result + (trx != null ? trx.hashCode() : 0);
		result = 31 * result + (coSeq != null ? coSeq.hashCode() : 0);
		result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
		result = 31 * result + (wrin != null ? wrin.hashCode() : 0);
		result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
		result = 31 * result + (goodsId != null ? goodsId.hashCode() : 0);
		result = 31 * result + (warehouseId != null ? warehouseId.hashCode() : 0);
		result = 31 * result + (location != null ? location.hashCode() : 0);
		result = 31 * result + (pickSeq != null ? pickSeq.hashCode() : 0);
		result = 31 * result + (pickZoneId != null ? pickZoneId.hashCode() : 0);
		result = 31 * result + (grWeight != null ? grWeight.hashCode() : 0);
		result = 31 * result + (cbm != null ? cbm.hashCode() : 0);
		result = 31 * result + (refillEvent != null ? refillEvent.hashCode() : 0);
		result = 31 * result + (quantityBase != null ? quantityBase.hashCode() : 0);
		result = 31 * result + (pslipTypeId != null ? pslipTypeId.hashCode() : 0);
		result = 31 * result + (dPack != null ? dPack.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Point {" +
				"isProcessed=" + isProcessed +
				", coreDistance=" + coreDistance +
				", reachabilityDistance=" + reachabilityDistance +
				", cell=" + cell +
				", id=" + id +
				", x=" + x +
				", y=" + y +
				", line='" + line + '\'' +
				", scanCode='" + scanCode + '\'' +
				", deliveryId=" + deliveryId +
				", customerId=" + customerId +
				", trx=" + trx +
				", coSeq=" + coSeq +
				", clientId=" + clientId +
				", wrin=" + wrin +
				", quantity=" + quantity +
				", goodsId=" + goodsId +
				", warehouseId='" + warehouseId + '\'' +
				", location='" + location + '\'' +
				", pickSeq=" + pickSeq +
				", pickZoneId='" + pickZoneId + '\'' +
				", grWeight=" + grWeight +
				", cbm=" + cbm +
				", refillEvent=" + refillEvent +
				", quantityBase=" + quantityBase +
				", pslipTypeId=" + pslipTypeId +
				", dPack='" + dPack + '\'' +
				'}';
	}
}
