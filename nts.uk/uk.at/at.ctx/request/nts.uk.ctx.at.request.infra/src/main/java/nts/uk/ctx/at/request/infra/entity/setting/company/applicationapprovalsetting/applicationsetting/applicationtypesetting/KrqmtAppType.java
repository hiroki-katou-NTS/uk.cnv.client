package nts.uk.ctx.at.request.infra.entity.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting;

import lombok.*;
import nts.arc.enums.EnumAdaptor;
import nts.arc.primitive.PrimitiveValueBase;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.DisplayReason;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.*;
import nts.uk.ctx.at.request.infra.entity.setting.company.applicationapprovalsetting.applicationsetting.KrqmtApplicationSet;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;
import org.apache.commons.lang3.BooleanUtils;

import javax.persistence.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "KRQMT_APP_TYPE")
public class KrqmtAppType extends ContractUkJpaEntity {
    @EmbeddedId
    private KrqmtAppTypePk pk;

    @Column(name = "PRE_POST_INIT_ATR")
    private Integer prePostInitAtr;

    @Column(name = "PRE_POST_CHANGE_ATR")
    private Integer prePostChangeAtr;

    @Column(name = "APV_SEND_MAIL_ATR")
    private int apvSendMailAtr;

    @Column(name = "APP_SEND_MAIL_ATR")
    private int appSendMailAtr;

    @Column(name = "POST_FUTURE_ALLOW_ATR")
    private int postFutureAllowAtr;

    @Column(name = "PRE_EARLY_DAYS")
    private int preEarlyDays;

    @Column(name = "USE_ATR")
    private int useAtr;

    @Column(name = "PRE_OT_CHECK_SET")
    private Integer preOTCheckSet;

    @Column(name = "PRE_OT_BEF_WORK_TIME")
    private Integer preOTBeforeWorkTime;

    @Column(name = "PRE_OT_AFT_WORK_TIME")
    private Integer preOTAfterWorkTime;

    @Column(name = "PRE_OT_BEF_AFT_WORK_TIME")
    private Integer preOTBeforeAfterWorkTime;

    @Column(name = "FIXED_REASON_DISP_ATR")
    private Integer fixedReasonDisplayAtr;

    @Column(name = "REASON_DISP_ATR")
    private Integer reasonDisplayAtr;

    @Column(name = "PRE_OT_MUL_WORK_TIME")
    private Integer preOTMultipleWorkTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CID", insertable = false, updatable = false)
    private KrqmtApplicationSet applicationSetting;

    @Override
    protected Object getKey() {
        return this.pk;
    }

    public AppTypeSetting toApTypeSetting() {
        return AppTypeSetting.createNew(
                this.pk.applicationType,
                BooleanUtils.toBoolean(this.appSendMailAtr),
                BooleanUtils.toBoolean(apvSendMailAtr),
                this.prePostInitAtr,
                this.prePostChangeAtr == null ? null : BooleanUtils.toBoolean(this.prePostChangeAtr)
        );
    }

    public ReceptionRestrictionSetting toReceptionRestrictSetting() {
        if (this.pk.applicationType == ApplicationType.OVER_TIME_APPLICATION.value)
            return new ReceptionRestrictionSetting(
                    OTAppBeforeAccepRestric.createNew(
                            this.preOTCheckSet,
                            this.preEarlyDays,
                            BooleanUtils.toBoolean(this.useAtr),
                            this.preOTBeforeWorkTime,
                            this.preOTAfterWorkTime,
                            this.preOTBeforeAfterWorkTime,
                            this.preOTMultipleWorkTime
                    ),
                    new AfterhandRestriction(BooleanUtils.toBoolean(postFutureAllowAtr)),
                    null,
                    EnumAdaptor.valueOf(this.pk.applicationType, ApplicationType.class)
            );
        else
            return new ReceptionRestrictionSetting(
                    null,
                    new AfterhandRestriction(BooleanUtils.toBoolean(postFutureAllowAtr)),
                    BeforehandRestriction.createNew(
                            preEarlyDays,
                            BooleanUtils.toBoolean(useAtr)
                    ),
                    EnumAdaptor.valueOf(this.pk.applicationType, ApplicationType.class)
            );
    }

    public DisplayReason toDisplayReason() {
        return DisplayReason.createAppDisplayReason(pk.companyId, fixedReasonDisplayAtr, reasonDisplayAtr, pk.applicationType);
    }

    public static KrqmtAppType fromDomain(String companyId, AppTypeSetting appTypeSetting, ReceptionRestrictionSetting receptionRestrictSetting, DisplayReason displayReason) {
        return KrqmtAppType.builder()
                .pk(new KrqmtAppTypePk(companyId, appTypeSetting.getAppType().value))
                .appSendMailAtr(BooleanUtils.toInteger(appTypeSetting.isSendMailWhenRegister()))
                .apvSendMailAtr(BooleanUtils.toInteger(appTypeSetting.isSendMailWhenApproval()))
                .prePostInitAtr(appTypeSetting.getDisplayInitialSegment().map(i -> i.value).orElse(null))
                .prePostChangeAtr(appTypeSetting.getCanClassificationChange().map(x -> BooleanUtils.toInteger(x)).orElse(null))
                .preOTCheckSet(receptionRestrictSetting.getAppType() == ApplicationType.OVER_TIME_APPLICATION ? receptionRestrictSetting.getOtAppBeforeAccepRestric().get().getMethodCheck().value : null)
                .preEarlyDays(receptionRestrictSetting.getAppType() == ApplicationType.OVER_TIME_APPLICATION ? receptionRestrictSetting.getOtAppBeforeAccepRestric().get().getDateBeforehandRestrictions().value : receptionRestrictSetting.getBeforehandRestriction().get().getDateBeforehandRestrictions().value)
                .useAtr(BooleanUtils.toInteger(receptionRestrictSetting.getAppType() == ApplicationType.OVER_TIME_APPLICATION ? receptionRestrictSetting.getOtAppBeforeAccepRestric().get().isToUse() : receptionRestrictSetting.getBeforehandRestriction().get().isToUse()))
                .preOTBeforeWorkTime(receptionRestrictSetting.getOtAppBeforeAccepRestric().isPresent() ? receptionRestrictSetting.getOtAppBeforeAccepRestric().get().getOpEarlyOvertime().map(PrimitiveValueBase::v).orElse(null) : null)
                .preOTAfterWorkTime(receptionRestrictSetting.getOtAppBeforeAccepRestric().isPresent() ? receptionRestrictSetting.getOtAppBeforeAccepRestric().get().getOpNormalOvertime().map(PrimitiveValueBase::v).orElse(null) : null)
                .preOTBeforeAfterWorkTime(receptionRestrictSetting.getOtAppBeforeAccepRestric().isPresent() ? receptionRestrictSetting.getOtAppBeforeAccepRestric().get().getOpEarlyNormalOvertime().map(PrimitiveValueBase::v).orElse(null) : null)
                .postFutureAllowAtr(BooleanUtils.toInteger(receptionRestrictSetting.getAfterhandRestriction().isAllowFutureDay()))
                .reasonDisplayAtr(displayReason == null ? null : displayReason.getDisplayAppReason().value)
                .fixedReasonDisplayAtr(displayReason == null ? null : displayReason.getDisplayFixedReason().value)
                .preOTMultipleWorkTime(receptionRestrictSetting.getOtAppBeforeAccepRestric().isPresent() ? receptionRestrictSetting.getOtAppBeforeAccepRestric().get().getOpMultipleOvertime().map(PrimitiveValueBase::v).orElse(null) : null)
                .build();
    }
}
