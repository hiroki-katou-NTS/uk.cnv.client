package nts.uk.ctx.at.request.app.command.setting.company.applicationapprovalsetting.applicationsetting.apptypeset;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.request.app.find.setting.company.applicationapprovalsetting.applicationsetting.apptypeset.BeforehandRestrictionDto;
import nts.uk.ctx.at.request.app.find.setting.company.applicationapprovalsetting.applicationsetting.apptypeset.OTAppBeforeAccepRestricDto;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.AfterhandRestriction;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.BeforehandRestriction;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.OTAppBeforeAccepRestric;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.ReceptionRestrictionSetting;

/**
 * refactor 4
 *
 * @author Doan Duy Hung
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReceptionRestrictionSetCommand {
    /**
     * 申請種類
     */
    private int appType;

    /**
     * 未来日許可しない
     */
    private boolean allowFutureDay;

    /**
     * 日数
     */
    private int dateBeforehandRestrictions;

    /**
     * 利用する
     */
    private boolean useAtr;

    /**
     * チェック方法
     */
    private int methodCheck;

    /**
     * 時刻（早出残業）
     */
    private Integer earlyOvertime;

    /**
     * 時刻（通常残業）
     */
    private Integer normalOvertime;

    /**
     * 時刻（早出残業・通常残業）
     */
    private Integer earlyNormalOvertime;

    /**
     * 時刻（複数回残業）
     */
    private Integer multipleOvertime;

    public ReceptionRestrictionSetting toDomain() {
        if (appType == ApplicationType.OVER_TIME_APPLICATION.value)
            return new ReceptionRestrictionSetting(
                    OTAppBeforeAccepRestric.createNew(
                            methodCheck,
                            dateBeforehandRestrictions,
                            useAtr,
                            earlyOvertime,
                            normalOvertime,
                            earlyNormalOvertime,
                            multipleOvertime
                    ),
                    new AfterhandRestriction(allowFutureDay),
                    null,
                    EnumAdaptor.valueOf(appType, ApplicationType.class));
        else
            return new ReceptionRestrictionSetting(
                    null,
                    new AfterhandRestriction(allowFutureDay),
                    BeforehandRestriction.createNew(dateBeforehandRestrictions, useAtr),
                    EnumAdaptor.valueOf(appType, ApplicationType.class));
    }
}
