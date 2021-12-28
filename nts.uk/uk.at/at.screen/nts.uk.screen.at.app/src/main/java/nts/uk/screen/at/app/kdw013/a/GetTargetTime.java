package nts.uk.screen.at.app.kdw013.a;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourinput.EncouragedTargetApplication;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourinput.OvertimeLeaveEncourageConfirmationService;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.A:工数入力.メニュー別OCD.残業申請・休出時間申請の対象時間を取得する
 * 
 * @author tutt
 *
 */
@Stateless
public class GetTargetTime {

//	@Inject
//	private OvertimeLeaveEncourageConfirmationService.Require1 require1;
//
//	@Inject
//	private OvertimeLeaveEncourageConfirmationService.Require2 require2;
//
//	/**
//	 * 
//	 * @param sid        対象社員
//	 * @param inputDates 対象日リスト
//	 * @return List<残業休出時間>
//	 */
//	public List<EncouragedTargetApplication> get(String sId, List<GeneralDate> inputDates) {
//		return OvertimeLeaveEncourageConfirmationService.check(require1, require2, AppContexts.user().companyId(), sId,
//				inputDates);
//	}

}
