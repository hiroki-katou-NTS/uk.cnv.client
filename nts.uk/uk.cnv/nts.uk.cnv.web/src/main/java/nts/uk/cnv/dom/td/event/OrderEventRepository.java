package nts.uk.cnv.dom.td.event;

public interface OrderEventRepository {
	String getNewestOrderId();

	void regist(OrderEvent orderEvent);

}
