package nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.schedule.monthly;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.attendanceitem.CheckedCondition;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.attendanceitem.CompareRange;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.attendanceitem.CompareSingleValue;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.daily.NameAlarmExtractCond;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.monthly.*;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcstErAlCompareRange;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcstErAlCompareSingle;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcstErAlSingleFixed;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.ErrorAlarmMessage;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHolidayCode;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

import javax.persistence.*;

import org.apache.commons.lang3.BooleanUtils;

import java.util.Optional;

/**
 * スケジュール月次の任意抽出条件 Entity
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "KSCDT_SCHE_ANY_COND_MONTH")
public class KscdtScheAnyCondMonth extends ContractUkJpaEntity {
    @EmbeddedId
    public KscdtScheAnyCondMonthPk pk;

    /* 使用区分 */
    @Column(name = "USE_ATR")
    public int useAtr;

    /* 名称 */
    @Column(name = "COND_NAME")
    public String condName;

    /* チェック項目種類 */
    @Column(name = "COND_TYPE")
    public int condType;

    /* スケジュール年間チェック条件 */
    @Column(name = "CHECK_TYPE")
    public int checkType;

    /* メッセージ */
    @Column(name = "COND_MESSAGE")
    public String condMsg;

    /* 特別休暇コード */
    @Column(name = "SPE_VACATION_CD")
    public String speVacCd;

    @Override
    protected Object getKey() {
        return this.pk;
    }

    public ExtractionCondScheduleMonth toDomain(KrcstErAlCompareSingle single, KrcstErAlSingleFixed singleFixed, KrcstErAlCompareRange range){

        ScheduleMonCheckCond scheCheckConditions = null;
        CheckedCondition checkedCondition = null;

        switch (EnumAdaptor.valueOf(condType, MonCheckItemType.class)){
            case CONTRAST:
                scheCheckConditions = new PublicHolidayCheckCond(EnumAdaptor.valueOf(checkType,TypeOfContrast.class));
                break;
            case TIME:
                scheCheckConditions = new TimeCheckCond(EnumAdaptor.valueOf(checkType,TypeOfTime.class ));
                break;
            case NUMBER_DAYS:
                scheCheckConditions = new DayCheckCond(EnumAdaptor.valueOf(checkType,TypeOfDays.class));
                break;
            case REMAIN_NUMBER:
                scheCheckConditions = new ScheduleMonRemainCheckCond(EnumAdaptor.valueOf(checkType,TypeOfVacations.class),Optional.ofNullable(speVacCd == null? null : new SpecialHolidayCode(Integer.parseInt(speVacCd))));
                break;
            default:
                break;
        }

        if (single != null) {
            checkedCondition = new CompareSingleValue<>(single.compareAtr, single.conditionType);
            ((CompareSingleValue) checkedCondition).setValue(singleFixed.fixedValue);
        } else if (range != null) {
            checkedCondition = new CompareRange<>(range.compareAtr);
            ((CompareRange) checkedCondition).setStartValue(range.startValue);
            ((CompareRange) checkedCondition).setEndValue(range.endValue);
        }

        return new ExtractionCondScheduleMonth(
        		pk.checkId,
        		scheCheckConditions,
        		checkedCondition, 
        		EnumAdaptor.valueOf(condType, MonCheckItemType.class),
        		pk.sortBy,
        		BooleanUtils.toBoolean(useAtr),
        		new NameAlarmExtractCond(condName), 
        		Optional.ofNullable(condMsg == null ? null : new ErrorAlarmMessage(condMsg)));
    }
}
