package nts.uk.ctx.workflow.app.query.approvermanagement;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.PersonApprovalRootRepository;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.WorkplaceApprovalRootRepository;

/**
 * <<Query>> 異動者の承認ルート指定状況を取得する
 * @author NWS-DungDV
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ApprovalRouteSpStatusQueryProcessor {

	@Inject
	private WorkplaceApprovalRootRepository workplaceApprovalRootRepository;
	
	@Inject
	private PersonApprovalRootRepository personApprovalRootRepository;
	
	/**
	 * 異動者の承認ルート指定状況を取得する
	 * @param sid 社員ID
	 * @param referDate 基準日
	 * @return 承認ルート状況
	 */
	public ApprovalRouteSpStatusQuery get(String sid, GeneralDate referDate) {
		
		/**
		 * 1: 承認者として登録されている職場リストを取得する
		 * Arg: 社員ID,基準日
		 * Return: 職場リスト
		 */
		List<String> workplaceList = this.workplaceApprovalRootRepository.getWkpsAsApprovers(sid, referDate);
		
		/**
		 * 2: 承認者として登録されている対象社員リストを取得する
		 * Arg: 社員ID,基準日
		 * Return 承認対象社員リスト
		 */
		List<String> appoveEmpList = this.personApprovalRootRepository.getListSidRegistered(sid, referDate);
		
		/**
		 * 3: 承認者を取得する
		 * Arg: 社員ID,基準日
		 * Return: 承認者リスト
		 */
		List<String> approverList = this.personApprovalRootRepository.getListAppover(sid, referDate);
		
		// Return 承認ルート状況
		return new ApprovalRouteSpStatusQuery
			(
				sid, 				// 指定者：社員ID　←　INPUT「社員ID」
				workplaceList, 		// 承認職場リスト：List<職場ID>　←　取得した「職場リスト」
				appoveEmpList, 		// 承認対象社員リスト：List<社員ID>　←　取得した「承認対象社員リスト」
				approverList 		// 指定者の承認者リスト：List<社員ID>　←　取得した「承認者リスト」
			);
	}
}
