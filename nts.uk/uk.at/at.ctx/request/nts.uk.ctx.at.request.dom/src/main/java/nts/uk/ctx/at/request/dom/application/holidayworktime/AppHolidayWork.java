package nts.uk.ctx.at.request.dom.application.holidayworktime;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.overtime.ApplicationTime;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.common.TimeZoneWithWorkNo;
import nts.uk.ctx.at.shared.dom.workdayoff.frame.NotUseAtr;


/**
 * 休日出勤申請
 * @author huylq
 *Refactor5
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AppHolidayWork extends Application{
	
	/**
	 * 勤務情報
	 */
	private WorkInformation workInformation;
	
	/**
	 * 申請時間
	 */
	private ApplicationTime applicationTime;
	
	/**
	 * 直帰区分
	 */
	private NotUseAtr backHomeAtr;
	
	/**
	 * 直行区分
	 */
	private NotUseAtr goWorkAtr;
	
	/**
	 * 休憩時間帯
	 */
	private Optional<List<TimeZoneWithWorkNo>> breakTimeList = Optional.empty();
	
	/**
	 * 勤務時間帯
	 */
	private Optional<List<TimeZoneWithWorkNo>> workingTimeList = Optional.empty();
	
	public AppHolidayWork(Application application) {
		super(application);
	}
	
	public void setApplication(Application application) {
		this.setVersion(application.getVersion());
		this.setAppID(application.getAppID());
		this.setPrePostAtr(application.getPrePostAtr());
		this.setEmployeeID(application.getEmployeeID());
		this.setAppType(application.getAppType());
		this.setAppDate(application.getAppDate());
		this.setEnteredPersonID(application.getEnteredPersonID());
		this.setInputDate(application.getInputDate());
		this.setReflectionStatus(application.getReflectionStatus());
		this.setOpStampRequestMode(application.getOpStampRequestMode());
		this.setOpReversionReason(application.getOpReversionReason());
		this.setOpAppStartDate(application.getOpAppStartDate());
		this.setOpAppEndDate(application.getOpAppEndDate());
		this.setOpAppReason(application.getOpAppReason());
		this.setOpAppStandardReasonCD(application.getOpAppStandardReasonCD());
	}
	
	public Application getApplication() {
		return new Application(
				this.getVersion(),
				this.getAppID(),
				this.getPrePostAtr(),
				this.getEmployeeID(),
				this.getAppType(),
				this.getAppDate(),
				this.getEnteredPersonID(),
				this.getInputDate(),
				this.getReflectionStatus(),
				this.getOpStampRequestMode(),
				this.getOpReversionReason(),
				this.getOpAppStartDate(),
				this.getOpAppEndDate(),
				this.getOpAppReason(),
				this.getOpAppStandardReasonCD());
	}
}
