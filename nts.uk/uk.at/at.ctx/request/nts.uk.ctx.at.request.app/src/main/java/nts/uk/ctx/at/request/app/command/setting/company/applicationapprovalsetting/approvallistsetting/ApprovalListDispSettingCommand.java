package nts.uk.ctx.at.request.app.command.setting.company.applicationapprovalsetting.approvallistsetting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.approvallistsetting.ApprovalListDisplaySetting;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalListDispSettingCommand {
    private int appReasonDispAtr;
    private int preExcessAtr;
    private int atdExcessAtr;
    private int warningDays;
    private int dispWorkplaceNameAtr;
    private int useApprovalFunction;

    public ApprovalListDisplaySetting toDomain(String companyId) {
        return ApprovalListDisplaySetting.create(
                companyId,
                appReasonDispAtr,
                preExcessAtr,
                atdExcessAtr,
                warningDays,
                dispWorkplaceNameAtr,
                useApprovalFunction
        );
    }
}
