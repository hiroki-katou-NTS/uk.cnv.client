package nts.uk.ctx.at.request.dom.application.lateleaveearly;

import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.lateorleaveearly.ArrivedLateLeaveEarly;
import nts.uk.ctx.at.request.dom.application.lateorleaveearly.ArrivedLateLeaveEarlyInfoOutput;

/**
 * @author anhnm
 *
 */
public interface LateLeaveEarlyRepository {

	void registerLateLeaveEarly(String cID, Application application, ArrivedLateLeaveEarlyInfoOutput infoOutput);

	ArrivedLateLeaveEarly getLateEarlyApp(String companyId, String appId, Application application);

	void updateLateLeaveEarly(String cID, Application application, ArrivedLateLeaveEarly infoOutput);
}
