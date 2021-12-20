package nts.uk.ctx.bs.employee.app.query.employee;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.bs.employee.dom.jobtitle.affiliate.AffJobTitleHistoryItemRepository;
import nts.uk.ctx.bs.employee.dom.jobtitle.affiliate.AffJobTitleHistoryItemWithPeriod;
import nts.uk.ctx.bs.employee.dom.jobtitle.affiliate.AffJobTitleHistoryRepository;
import nts.uk.ctx.bs.employee.dom.jobtitle.affiliate.AffWorkplaceHistoryItemWPeriod;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * «Query» 職場または職位履歴を変更している社員を取得する
 * @author NWS-DungDV
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EmpsChangeHistoryQueryProcessor {
	
	@Inject
	private AffWorkplaceHistoryRepository affWorkplaceHistoryRepository;
	
	@Inject
	private AffJobTitleHistoryRepository affJobTitleHistoryRepository;
	
	@Inject
	private AffJobTitleHistoryItemRepository affJobTitleHistoryItemRepository;
	
	public EmpsChangeHistoryQuery get(DatePeriod period) {
		// Get login companyID
		String cid = AppContexts.user().companyId();
		
		/**
		 * 1: 期間内に履歴変更している社員を取得する
		 * Param: 会社ID,期間
		 * Arg: ログイン会社ID,期間
		 * Return: 社員リスト
		 */
		List<String> wkpSIds = this.affWorkplaceHistoryRepository.empHasChangedWkpWithinPeriod(cid, period);
		
		/**
		 * 2: 期間内に履歴変更している社員を取得する
		 * Param: 会社ID,期間
		 * Arg: ログイン会社ID,期間
		 * Return: 社員リスト
		 */
		List<String> jtSids = this.affJobTitleHistoryRepository.getByDatePeriod(cid, period);
		
		// 対象者リスト = 取得した２つの「社員リスト」を合体して重複社員を除く
		List<String> sids = Stream.of(wkpSIds, jtSids)
				.flatMap(mapper -> mapper.stream())
				.distinct()
				.collect(Collectors.toList());
		
		/**
		 * 3: 全ての職場履歴を取得する
		 * Param: 社員リスト,期間
		 * Arg: 対象者リスト,期間
		 * Return: 職場履歴リスト
		 */
		List<AffWorkplaceHistoryItemWPeriod> awhItems = this.affWorkplaceHistoryRepository.getAllWkpHist(sids, period);
		
		/**
		 * 4: 全ての職位履歴を取得する
		 * Param: 社員リスト,期間
		 * Arg: 対象者リスト,期間
		 * Return: 職位履歴リスト
		 */
		List<AffJobTitleHistoryItemWithPeriod> ajthItems = this.affJobTitleHistoryItemRepository.getBySidAndDatePeriod(sids, period);
		
		return new EmpsChangeHistoryQuery(awhItems, ajthItems);
	}

}
