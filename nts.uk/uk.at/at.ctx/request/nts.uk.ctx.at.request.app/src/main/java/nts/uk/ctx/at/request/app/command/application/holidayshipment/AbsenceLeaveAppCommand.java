package nts.uk.ctx.at.request.app.command.application.holidayshipment;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;

@Getter
@Setter
public class AbsenceLeaveAppCommand {
	private String appDate;
	private String wkTypeCD;
	private int changeWorkHoursType;
	private WkTimeCommand wkTime1;
	private WkTimeCommand wkTime2;
	private String appID;
	private String wkTimeCD;

	public GeneralDate getAppDate() {
		return appDate != null ? GeneralDate.fromString(appDate, "yyyy/MM/dd") : null;
	}
}
