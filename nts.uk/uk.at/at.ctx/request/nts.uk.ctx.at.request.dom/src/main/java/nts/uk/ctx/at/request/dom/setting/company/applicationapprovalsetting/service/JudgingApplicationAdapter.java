package nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.service;

import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.service.JudgingApplication.Require;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationTypeShare;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.Imported.勤務種類から促す申請を判断する
 * 
 * @author tutt
 *
 */
public interface JudgingApplicationAdapter {

	public Optional<ApplicationTypeShare> judge(Require require, String cid, String sid, GeneralDate date,
			String workTypeCode);

}
