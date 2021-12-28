package nts.uk.ctx.at.request.dom.application.overtime;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.error.BusinessException;
import nts.uk.ctx.at.request.dom.application.AppReason;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.AppStandardReasonCode;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.common.TimeZoneWithWorkNo;
/**
 * Refactor5 
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.申請承認.申請.残業休出申請
 * @author hoangnd
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
// 残業申請
public class AppOverTime extends Application {
	// 残業区分
	private OvertimeAppAtr overTimeClf;
	
	// 申請時間
	private ApplicationTime applicationTime;
	
	// 休憩時間帯
	private Optional<List<TimeZoneWithWorkNo>> breakTimeOp = Optional.empty();
	
	// 勤務時間帯
	private Optional<List<TimeZoneWithWorkNo>> workHoursOp = Optional.empty();
	
	// 勤務情報
	private Optional<WorkInformation> workInfoOp = Optional.empty();

	//複数回残業内容
	private Optional<OvertimeWorkMultipleTimes> multipleTimesOp = Optional.empty();
	
	public AppOverTime(Application application) {
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
		this.validate();
		Optional<AppReason> opAppReason = this.overTimeClf == OvertimeAppAtr.MULTIPLE_OVERTIME
				? this.multipleTimesOp.get().createApplyReason()
				: this.getOpAppReason();
		Optional<AppStandardReasonCode> opAppStandardReasonCD = this.overTimeClf == OvertimeAppAtr.MULTIPLE_OVERTIME
				? this.multipleTimesOp.get().createFixedReason()
				: this.getOpAppStandardReasonCD();
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
				opAppReason,
				opAppStandardReasonCD
		);
	}

	@Override
	public void validate() {
		super.validate();
		if (overTimeClf == OvertimeAppAtr.MULTIPLE_OVERTIME && !multipleTimesOp.isPresent())
			throw new BusinessException("Msg_3248");
	}
	
}
