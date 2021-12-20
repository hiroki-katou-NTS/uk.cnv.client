package nts.uk.ctx.bs.employee.app.query.employee;

import java.util.List;

import lombok.Value;
import nts.uk.ctx.bs.employee.dom.jobtitle.affiliate.AffJobTitleHistoryItemWithPeriod;
import nts.uk.ctx.bs.employee.dom.jobtitle.affiliate.AffWorkplaceHistoryItemWPeriod;

/**
 * OUTPUT: 職場または職位履歴を変更している社員を取得する
 * @author NWS-DungDV
 *
 */
@Value
public class EmpsChangeHistoryQuery {
	
	// 職場履歴リスト：List<期間付き職場履歴項目>
	List<AffWorkplaceHistoryItemWPeriod> awhItems;
	
	// 職位履歴リスト：List<期間付き職位履歴項目>
	List<AffJobTitleHistoryItemWithPeriod> ajthItems;
}
