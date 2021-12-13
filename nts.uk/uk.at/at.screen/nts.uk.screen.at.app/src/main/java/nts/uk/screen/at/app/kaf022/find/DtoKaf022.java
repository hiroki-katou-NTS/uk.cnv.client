package nts.uk.screen.at.app.kaf022.find;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.request.app.find.application.gobackdirectly.GoBackReflectDto;
import nts.uk.ctx.at.request.app.find.application.stamp.dto.AppStampSettingDto;
import nts.uk.ctx.at.request.app.find.application.workchange.AppWorkChangeSetDto;
import nts.uk.ctx.at.request.app.find.applicationreflect.AppReflectExeConditionDto;
import nts.uk.ctx.at.request.app.find.setting.company.applicationapprovalsetting.applicationsetting.ApplicationSettingDto;
import nts.uk.ctx.at.request.app.find.setting.company.applicationapprovalsetting.applicationsetting.DisplayReasonDto;
import nts.uk.ctx.at.request.app.find.setting.company.applicationapprovalsetting.appovertime.OvertimeAppSetDto;
import nts.uk.ctx.at.request.app.find.setting.company.applicationapprovalsetting.approvallistsetting.ApprovalListDispSettingDto;
import nts.uk.ctx.at.request.app.find.setting.company.applicationapprovalsetting.hdworkapplicationsetting.HolidayWorkAppSetDto;
import nts.uk.ctx.at.request.app.find.setting.company.applicationapprovalsetting.optionalitemappsetting.OptionalItemAppSetDto;
import nts.uk.ctx.at.request.app.find.setting.company.applicationapprovalsetting.substituteapplicationsetting.SubstituteHdWorkAppSetDto;
import nts.uk.ctx.at.request.app.find.setting.company.applicationapprovalsetting.vacationapplicationsetting.HolidayApplicationSettingDto;
import nts.uk.ctx.at.request.app.find.setting.company.emailset.AppEmailSetDto;
import nts.uk.ctx.at.request.app.find.setting.request.application.businesstrip.BusinessTripSetDto;
import nts.uk.ctx.at.shared.app.find.scherec.appreflectprocess.appreflectcondition.othdwork.hdworkapply.HdWorkAppReflectDto_Old;
import nts.uk.ctx.at.shared.app.find.scherec.appreflectprocess.appreflectcondition.othdwork.otworkapply.OtWorkAppReflectDto;
import nts.uk.ctx.at.shared.app.find.scherec.appreflectprocess.appreflectcondition.stampapplication.StampAppReflectDto;
import nts.uk.ctx.at.shared.app.find.scherec.appreflectprocess.appreflectcondition.substituteworkapplication.SubstituteWorkAppReflectDto;
import nts.uk.ctx.at.shared.app.find.scherec.appreflectprocess.appreflectcondition.timeleaveapplication.TimeLeaveAppReflectDto;
import nts.uk.ctx.at.shared.app.find.scherec.appreflectprocess.appreflectcondition.vacationapplication.leaveapplication.HolidayApplicationReflectDto;
import nts.uk.ctx.at.shared.app.find.scherec.appreflectprocess.appreflectcondition.vacationapplication.subleaveapp.SubLeaveAppReflectDto;
import nts.uk.ctx.at.shared.app.find.workrule.closure.dto.ClosureHistoryFindDto;
import nts.uk.ctx.sys.portal.pub.standardmenu.StandardMenuNameExport;
import nts.uk.ctx.workflow.app.find.approvermanagement.setting.ApprovalSettingDto;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class DtoKaf022 {
	private List<ClosureHistoryFindDto> allClosure;
	private ApplicationSettingDto applicationSetting;
	private List<DisplayReasonDto> reasonDisplaySettings;
	private List<StandardMenuNameExport> menus;
	private ApprovalSettingDto approvalSettingDto;
	private AppReflectExeConditionDto appReflectCondition;
	private Integer nightOvertimeReflectAtr;

	private OvertimeAppSetDto overtimeAppSetting;
	private OtWorkAppReflectDto overtimeAppReflect;

	private HolidayApplicationSettingDto holidayApplicationSetting;
	private HolidayApplicationReflectDto holidayApplicationReflect;

	private AppWorkChangeSetDto appChange;
	private Integer workTimeReflectAtr;

	private BusinessTripSetDto tripRequestSetting;

	private GoBackReflectDto goBackReflect;

	private HolidayWorkAppSetDto holidayWorkApplicationSetting;
	private HdWorkAppReflectDto_Old holidayWorkApplicationReflect;

	private TimeLeaveAppReflectDto timeLeaveApplicationReflect;

	private int lateEarlyCancelAtr;
	private int lateEarlyClearAlarmAtr;

	private AppStampSettingDto appStampSetting;
	private StampAppReflectDto appStampReflect;

	private SubstituteHdWorkAppSetDto substituteHdWorkApplicationSetting;
	private SubLeaveAppReflectDto substituteLeaveApplicationReflect;
	private SubstituteWorkAppReflectDto substituteWorkApplicationReflect;

	private List<OptionalItemAppSetDto> optionalItemApplicationSettings;

	private ApprovalListDispSettingDto approvalListDisplaySetting;

	private AppEmailSetDto appMailSetting;
}
