package nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.service;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.service.JudgingApplication.Require;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationTypeShare;

/**
 * 
 * @author tutt
 *
 */
@Stateless
public class JudgingApplicationAdapterImpl implements JudgingApplicationAdapter {

	@Inject
	private JudgingApplicationByWorkType app;

	@Override
	public Optional<ApplicationTypeShare> judge(Require require, String cid, String sid, GeneralDate date,
			String workTypeCode) {
		return app.judgingApp(require, cid, sid, date, workTypeCode);
	}

}
