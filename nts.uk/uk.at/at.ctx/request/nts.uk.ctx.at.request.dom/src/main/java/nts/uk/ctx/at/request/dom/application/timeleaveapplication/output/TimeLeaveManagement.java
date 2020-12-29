package nts.uk.ctx.at.request.dom.application.timeleaveapplication.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.shared.dom.vacation.setting.TimeDigestiveUnit;

/**
 * 時間休暇管理
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TimeLeaveManagement {
    // 時間年休管理
    private TimeAnnualLeaveMng timeAnnualLeaveMng;

    // 時間代休管理
    private TimeSubstituteLeaveMng timeSubstituteLeaveMng;

    // 60H超休管理
    private Super60HLeaveMng super60HLeaveMng;

    // 子看護介護管理
    private NursingLeaveMng nursingLeaveMng;

    // 時間特別休暇管理
    private TimeSpecialLeaveMng timeSpecialLeaveMng;

    public static TimeVacationManagementOutput setDtaOutput(TimeLeaveManagement dto) {

        return new TimeVacationManagementOutput(
            new SupHolidayManagement(
                EnumAdaptor.valueOf(dto.super60HLeaveMng.getSuper60HLeaveUnit(), TimeDigestiveUnit.class),
                dto.super60HLeaveMng.getSuper60HLeaveMngAtr()
            ),
            new ChildNursingManagement(
                EnumAdaptor.valueOf(dto.nursingLeaveMng.getTimeCareLeaveUnit(), TimeDigestiveUnit.class),
                dto.nursingLeaveMng.getTimeCareLeaveMngAtr(),
                EnumAdaptor.valueOf(dto.nursingLeaveMng.getTimeChildCareLeaveUnit(), TimeDigestiveUnit.class),
                dto.nursingLeaveMng.getTimeChildCareLeaveMngAtr()
            ),
            new TimeAllowanceManagement(
                EnumAdaptor.valueOf(dto.timeSubstituteLeaveMng.getTimeSubstituteLeaveUnit(), TimeDigestiveUnit.class),
                dto.timeSubstituteLeaveMng.getTimeSubstituteLeaveMngAtr()
            ),
            new TimeAnnualLeaveManagement(
                EnumAdaptor.valueOf(dto.timeAnnualLeaveMng.getTimeAnnualLeaveUnit(), TimeDigestiveUnit.class),
                dto.timeAnnualLeaveMng.getTimeAnnualLeaveMngAtr()
            ),
            dto.timeSpecialLeaveMng
        );
    }
}
