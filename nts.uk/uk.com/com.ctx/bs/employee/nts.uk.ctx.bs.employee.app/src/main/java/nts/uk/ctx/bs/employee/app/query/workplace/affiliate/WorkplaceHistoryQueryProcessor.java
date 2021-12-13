package nts.uk.ctx.bs.employee.app.query.workplace.affiliate;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryRepository;

/**
 * <<Query>> 基準日時点に所属職場履歴変更している社員を取得する
 * @author NWS-DungDV
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class WorkplaceHistoryQueryProcessor {
	
	@Inject
	private AffWorkplaceHistoryRepository affWorkplaceHistoryRepository;
	
	/**
	 * 基準日時点に所属職場履歴変更している社員を取得する
	 * @param sids 社員リスト
	 * @param baseDate 基準日
	 */
	public List<String> getEmployees(List<String> sids, GeneralDate baseDate) {
		
		/**
		 * 1: 基準日時点に所属職場履歴変更している社員を取得する
		 * Arg: 社員リスト,基準日
		 * Return: 社員リスト
		 */
		return this.affWorkplaceHistoryRepository.empHasChangedWkpWithPeriod(sids, baseDate);
	}

}
