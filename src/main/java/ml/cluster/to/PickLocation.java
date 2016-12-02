package ml.cluster.to;

import java.math.BigDecimal;

public class PickLocation {

    private Long id;
    private Double x;
    private Double y;
    private String line;
    private Long deliveryId;
    private Long customerId;
    private Long trx;
    private Long coSeq;
    private Long clientId;
    private Long wrin;
    private BigDecimal quantity;
    private Long goodsId;
    private String warehouseId;
    private String location;
    private Long pickSeq;
    private String pickZoneId;
    private BigDecimal grWeight;
    private BigDecimal cbm;
    private Integer refillEvent;
    private BigDecimal quantityBase;
    private Integer pslipTypeId;
    private String dPack;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public Long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(Long deliveryId) {
        this.deliveryId = deliveryId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getTrx() {
        return trx;
    }

    public void setTrx(Long trx) {
        this.trx = trx;
    }

    public Long getCoSeq() {
        return coSeq;
    }

    public void setCoSeq(Long coSeq) {
        this.coSeq = coSeq;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getWrin() {
        return wrin;
    }

    public void setWrin(Long wrin) {
        this.wrin = wrin;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getPickSeq() {
        return pickSeq;
    }

    public void setPickSeq(Long pickSeq) {
        this.pickSeq = pickSeq;
    }

    public String getPickZoneId() {
        return pickZoneId;
    }

    public void setPickZoneId(String pickZoneId) {
        this.pickZoneId = pickZoneId;
    }

    public BigDecimal getGrWeight() {
        return grWeight;
    }

    public void setGrWeight(BigDecimal grWeight) {
        this.grWeight = grWeight;
    }

    public BigDecimal getCbm() {
        return cbm;
    }

    public void setCbm(BigDecimal cbm) {
        this.cbm = cbm;
    }

    public Integer getRefillEvent() {
        return refillEvent;
    }

    public void setRefillEvent(Integer refillEvent) {
        this.refillEvent = refillEvent;
    }

    public BigDecimal getQuantityBase() {
        return quantityBase;
    }

    public void setQuantityBase(BigDecimal quantityBase) {
        this.quantityBase = quantityBase;
    }

    public Integer getPslipTypeId() {
        return pslipTypeId;
    }

    public void setPslipTypeId(Integer pslipTypeId) {
        this.pslipTypeId = pslipTypeId;
    }

    public String getdPack() {
        return dPack;
    }

    public void setdPack(String dPack) {
        this.dPack = dPack;
    }

    @Override
    public String toString() {
        return "PickLocation {" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", line='" + line + '\'' +
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
