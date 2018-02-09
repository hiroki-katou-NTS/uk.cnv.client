package nts.uk.ctx.at.request.dom.application.holidayworktime.service;

import java.util.List;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.Application_New;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWork;
import nts.uk.ctx.at.request.dom.application.holidayworktime.HolidayWorkInput;

public interface IFactoryHolidayWork {
	// 申請
		Application_New buildApplication(String appID, GeneralDate applicationDate, int prePostAtr, String appReasonID,
				String applicationReason);

		// 残業申請
		AppHolidayWork buildHolidayWork(String companyID, String appID, String workTypeCode, String siftCode,
				int workClockStart1, int workClockEnd1, int goAtr1,int backAtr1,int workClockStart2, int workClockEnd2,int goAtr2, int backAtr2, String divergenceReason,
				int overTimeShiftNight, List<HolidayWorkInput> holidayWorkInputs);

}
