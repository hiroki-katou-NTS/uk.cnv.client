package nts.uk.ctx.at.request.app.find.setting.company.applicationapprovalsetting.approvallistsetting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.approvallistsetting.ApprovalListDisplaySetting;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalListDispSettingDto {
    private int appReasonDispAtr;
    private int preExcessAtr;
    private int atdExcessAtr;
    private int warningDays;
    private int dispWorkplaceNameAtr;
    private int useApprovalFunction;

    public static ApprovalListDispSettingDto fromDomain(ApprovalListDisplaySetting domain) {
        return new ApprovalListDispSettingDto(
                domain.getAppReasonDisAtr().value,
                domain.getAdvanceExcessMessDisAtr().value,
                domain.getActualExcessMessDisAtr().value,
                domain.getWarningDateDisAtr().v(),
                domain.getDisplayWorkPlaceName().value,
                domain.getUseApprovalFunction().value
        );
    }
}
