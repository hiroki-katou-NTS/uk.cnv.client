package nts.uk.screen.at.app.kaf021.query.a;

import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeOfYear;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.oneyear.OneYearErrorAlarmTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.oneyear.OneYearTime;

@Getter
public class AggreementTimeDto {

    public AggreementTimeDto() {
        this.time = 0;
        this.maxTime = 0;
        this.error = 0;
        this.alarm = 0;
    }

    public AggreementTimeDto(AgreementTimeOfMonthly time) {
        this.time = time.getAgreementTime().v();
        this.maxTime = time.getThreshold().getUpperLimit().v();
        this.error = time.getThreshold().getErAlTime().getError().v();
        this.alarm = time.getThreshold().getErAlTime().getAlarm().v();
    }

    public AggreementTimeDto(AgreementTimeOfYear time, OneYearErrorAlarmTime basic) {
        this.time = time.getTargetTime().v();
        this.maxTime = time.getThreshold().getUpperLimit().v();
        this.error = basic.getError().v();
        this.alarm = basic.getAlarm().v();
    }

    public AggreementTimeDto(AgreementTimeOfYear time, OneYearTime specConditionLimit) {
        this.time = time.getTargetTime().v();
        this.maxTime = time.getThreshold().getUpperLimit().v();
        this.error = specConditionLimit.getErAlTime().getError().v();
        this.alarm = specConditionLimit.getErAlTime().getAlarm().v();
    }

    /**
     * 36協定時間
     */
    private Integer time;
    /**
     * 上限時間
     */
    private Integer maxTime;
    /**
     * エラー時間
     */
    private Integer error;
    /**
     * アラーム時間
     */
    private Integer alarm;
}
