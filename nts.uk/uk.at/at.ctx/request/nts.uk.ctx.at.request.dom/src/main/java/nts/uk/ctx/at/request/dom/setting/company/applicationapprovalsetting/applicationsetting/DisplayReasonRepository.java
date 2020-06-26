package nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting;

import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.appabsence.HolidayApplicationType;

public interface DisplayReasonRepository {
	
	public DisplayReason findByAppType(String companyID, ApplicationType appType);
	
	public DisplayReason findByHolidayAppType(String companyID, HolidayApplicationType holidayAppType);
	
}
