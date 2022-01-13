package nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.service;

import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.service.JudgingApplication.Require;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationTypeShare;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.申請承認.Export.勤務種類から促す申請を判断する.勤務種類から促す申請を判断する
 * 
 * @author tutt
 *
 */
@Stateless
public class JudgingApplicationByWorkType {

	/**
	 * 
	 * @param require
	 * @param cid          会社ID
	 * @param sid          社員ID
	 * @param date         年月日
	 * @param workTypeCode 勤務種類コード
	 * @return Optional<申請種類>
	 */
	public Optional<ApplicationTypeShare> judgingApp(Require require, String cid, String sid, GeneralDate date,
			String workTypeCode) {

		return JudgingApplication.toDecide(require, cid, sid, date, workTypeCode);
	}

}
