package nts.uk.ctx.pr.core.dom.wageprovision.statementitem.paymentitemset;

import java.util.Optional;
import java.util.List;

/**
 * 
 * @author thanh.tq 支給項目設定
 *
 */
public interface PaymentItemSetRepository {

	List<PaymentItemSet> getAllPaymentItemSt();

	List<PaymentItemSet> getPaymentItemSt(String cid, int categoryAtr);

	Optional<PaymentItemSet> getPaymentItemStById(String cid, int categoryAtr, String itemNameCode);

	void add(PaymentItemSet domain);

	void update(PaymentItemSet domain);

	void updateAll(List<String> lstCode);

	void remove(String cid, int categoryAtr, String itemNameCd);

}
