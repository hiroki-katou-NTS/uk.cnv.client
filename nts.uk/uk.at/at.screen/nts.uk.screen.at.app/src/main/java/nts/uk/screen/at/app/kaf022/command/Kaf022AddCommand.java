package nts.uk.screen.at.app.kaf022.command;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.request.app.command.setting.company.applicationapprovalsetting.approvallistsetting.ApprovalListDispSettingCommand;
import nts.uk.ctx.at.request.app.command.setting.company.applicationapprovalsetting.hdworkapplicationsetting.HolidayWorkAppSetCommand;
import nts.uk.ctx.at.request.app.command.setting.company.applicationapprovalsetting.stampsetting.AppStampSettingCommand;
import nts.uk.ctx.at.request.app.command.setting.company.applicationapprovalsetting.substituteapplicationsetting.SubstituteHdWorkAppSetCommand;
import nts.uk.ctx.at.request.app.command.setting.company.applicationapprovalsetting.vacationapplicationsetting.HolidayApplicationSettingCommand;
import nts.uk.ctx.at.request.app.command.setting.company.applicationapprovalsetting.workchange.AppWorkChangeSetCommand;
import nts.uk.ctx.at.request.app.command.applicationreflect.AppReflectExeConditionCommand;
import nts.uk.ctx.at.request.app.command.setting.company.applicationapprovalsetting.applicationsetting.ApplicationSettingCommand;
import nts.uk.ctx.at.request.app.command.setting.company.applicationapprovalsetting.applicationsetting.DisplayReasonCommand;
import nts.uk.ctx.at.request.app.command.setting.company.applicationapprovalsetting.appovertime.OvertimeAppSetCommand;
import nts.uk.ctx.at.request.app.command.setting.company.emailset.AppEmailSetCommand;
import nts.uk.ctx.at.request.app.command.setting.request.application.businesstrip.BusinessTripSetCommand;
import nts.uk.ctx.at.shared.app.command.scherec.appreflectprocess.appreflectcondition.othdwork.hdworkapply.HdWorkAppReflectCommand_New;
import nts.uk.ctx.at.shared.app.command.scherec.appreflectprocess.appreflectcondition.othdwork.otworkapply.OtWorkAppReflectCommand;
import nts.uk.ctx.at.shared.app.command.scherec.appreflectprocess.appreflectcondition.stampapplication.StampAppReflectCommand;
import nts.uk.ctx.at.shared.app.command.scherec.appreflectprocess.appreflectcondition.substituteworkapplication.SubstituteWorkAppReflectCommand;
import nts.uk.ctx.at.shared.app.command.scherec.appreflectprocess.appreflectcondition.timeleaveapplication.TimeLeaveAppReflectCommand;
import nts.uk.ctx.at.shared.app.command.scherec.appreflectprocess.appreflectcondition.vacationapplication.leaveapplication.HolidayApplicationReflectCommand;
import nts.uk.ctx.at.shared.app.command.scherec.appreflectprocess.appreflectcondition.vacationapplication.subleaveapp.SubLeaveAppReflectCommand;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Kaf022AddCommand {
	private ApplicationSettingCommand applicationSetting;
	private List<DisplayReasonCommand> reasonDisplaySettings;

	private int nightOvertimeReflectAtr;

	private int approvalByPersonAtr;

	private AppReflectExeConditionCommand appReflectCondition;

	private OvertimeAppSetCommand overtimeApplicationSetting;
	private OtWorkAppReflectCommand overtimeApplicationReflect;

	private HolidayApplicationSettingCommand holidayApplicationSetting;
	private HolidayApplicationReflectCommand holidayApplicationReflect;

	private AppWorkChangeSetCommand appWorkChangeSetting;

	private BusinessTripSetCommand tripRequestSetting;

	private int goBackReflectAtr;

	private HolidayWorkAppSetCommand holidayWorkApplicationSetting;
	private HdWorkAppReflectCommand_New holidayWorkApplicationReflect;

	private TimeLeaveAppReflectCommand timeLeaveApplicationReflect;

	private int lateEarlyCancelAtr;
	private int lateEarlyClearAlarmAtr;

	private AppStampSettingCommand appStampSetting;
	private StampAppReflectCommand appStampReflect;

	private SubstituteHdWorkAppSetCommand substituteHdWorkAppSetting;
	private SubLeaveAppReflectCommand substituteLeaveAppReflect;
	private SubstituteWorkAppReflectCommand substituteWorkAppReflect;

	private ApprovalListDispSettingCommand approvalListDisplaySetting;

	private AppEmailSetCommand appMailSetting;
}
