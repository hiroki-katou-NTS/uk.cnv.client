package nts.uk.ctx.at.request.infra.entity.setting.company.applicationapprovalsetting.approvallistsetting;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.approvallistsetting.ApprovalListDisplaySetting;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * refactor 4
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.申請承認.設定.会社別.申請承認設定.承認一覧設定.承認一覧表示設定
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "KRQMT_APPROVAL")
public class KrqmtApproval extends ContractUkJpaEntity {
    /**
     * 会社ID
     */
    @Id
    @Column(name = "CID")
    private String companyID;

    /**
     * 申請理由
     */
    @Column(name = "REASON_DISP_ATR")
    private int appReasonDisAtr;

    /**
     * 事前申請の超過メッセージ
     */
    @Column(name = "PRE_EXCESS_ATR")
    private int advanceExcessMessDisAtr;

    /**
     * 実績超過メッセージ
     */
    @Column(name = "ATD_EXCESS_ATR")
    private int actualExcessMessDisAtr;

    /**
     * 申請対象日に対して警告表示
     */
    @Column(name = "WARNING_DAYS")
    private int warningDateDisAtr;

    /**
     * 所属職場名表示
     */
    @Column(name = "WKP_DISP_ATR")
    private int displayWorkPlaceName;

    @Column(name = "APPROVE_FUNC_USE")
    private int useApprovalFunction;

    @Override
    protected Object getKey() {
        return null;
    }

    public ApprovalListDisplaySetting toDomain() {
        return ApprovalListDisplaySetting.create(
                companyID,
                appReasonDisAtr,
                advanceExcessMessDisAtr,
                actualExcessMessDisAtr,
                warningDateDisAtr,
                displayWorkPlaceName,
                useApprovalFunction
        );
    }
}
