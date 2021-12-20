package nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.alarmlistworkplace;

import lombok.val;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.monthly.enums.CheckMonthlyItemsType;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.monthly.primitivevalue.AverageNumberDays;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.monthly.primitivevalue.AverageNumberTimes;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.monthly.primitivevalue.AverageRatio;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.monthly.primitivevalue.AverageTime;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.schedule.CheckDayItemsType;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.schedule.primitivevalue.*;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcstErAlCompareRange;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcstErAlCompareSingle;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcstErAlSingleFixed;
import nts.uk.ctx.at.shared.dom.workrecord.alarm.attendanceitemconditions.CheckConditions;
import nts.uk.ctx.at.shared.dom.workrecord.alarm.attendanceitemconditions.CompareRange;
import nts.uk.ctx.at.shared.dom.workrecord.alarm.attendanceitemconditions.CompareSingleValue;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author Thanh.LNP
 */
public class ToCheckConditions {
    public static <V> CheckConditions<V> checkSchedaiExCon(int checkDayItemsType, Optional<KrcstErAlCompareSingle> compareSingle, Optional<KrcstErAlCompareRange> compareRange, Optional<KrcstErAlSingleFixed> singleFixed) {
        if (singleFixed.isPresent() && compareSingle.isPresent()) {
            if (checkDayItemsType == CheckDayItemsType.CONTRAST.value) {
                return setCompareSingleValue((V) new Comparison((int) singleFixed.get().fixedValue), compareSingle.get().compareAtr, compareSingle.get().conditionType);
            } else if (checkDayItemsType == CheckDayItemsType.NUMBER_PEOPLE_COMPARISON.value) {
                return setCompareSingleValue((V) new NumberOfPeople((int) singleFixed.get().fixedValue), compareSingle.get().compareAtr, compareSingle.get().conditionType);
            } else if (checkDayItemsType == CheckDayItemsType.TIME_COMPARISON.value) {
                return setCompareSingleValue((V) new Time((int) singleFixed.get().fixedValue), compareSingle.get().compareAtr, compareSingle.get().conditionType);
            } else if (checkDayItemsType == CheckDayItemsType.AMOUNT_COMPARISON.value) {
                return setCompareSingleValue((V) new Amount((int) singleFixed.get().fixedValue), compareSingle.get().compareAtr, compareSingle.get().conditionType);
            } else if (checkDayItemsType == CheckDayItemsType.RATIO_COMPARISON.value) {
                return setCompareSingleValue((V) new RatioComparison((int) singleFixed.get().fixedValue), compareSingle.get().compareAtr, compareSingle.get().conditionType);
            } else {
                return null;
            }
        } else if (compareRange.isPresent()) {
            if (checkDayItemsType == CheckDayItemsType.CONTRAST.value) {
                return setCompareRange((V) new Comparison((int) compareRange.get().startValue), (V) new Comparison((int) compareRange.get().endValue), compareRange.get().compareAtr);
            } else if (checkDayItemsType == CheckDayItemsType.NUMBER_PEOPLE_COMPARISON.value) {
                return setCompareRange((V) new NumberOfPeople((int) compareRange.get().startValue), (V) new NumberOfPeople((int) compareRange.get().endValue), compareRange.get().compareAtr);
            } else if (checkDayItemsType == CheckDayItemsType.TIME_COMPARISON.value) {
                return setCompareRange((V) new Time((int) compareRange.get().startValue), (V) new Time((int) compareRange.get().endValue), compareRange.get().compareAtr);
            } else if (checkDayItemsType == CheckDayItemsType.AMOUNT_COMPARISON.value) {
                return setCompareRange((V) new Amount((int) compareRange.get().startValue), (V) new Amount((int) compareRange.get().endValue), compareRange.get().compareAtr);
            } else if (checkDayItemsType == CheckDayItemsType.RATIO_COMPARISON.value) {
                return setCompareRange((V) new RatioComparison((int) compareRange.get().startValue), (V) new RatioComparison((int) compareRange.get().endValue), compareRange.get().compareAtr);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static <V> CheckConditions<V> checkMonExtracCon(int checkMonthlyItemsType, Optional<KrcstErAlCompareSingle> compareSingle, Optional<KrcstErAlCompareRange> compareRange, Optional<KrcstErAlSingleFixed> singleFixed) {
        if (singleFixed.isPresent() && compareSingle.isPresent()) {
            if (checkMonthlyItemsType == CheckMonthlyItemsType.AVERAGE_TIME.value) {
                return setCompareSingleValue((V) new AverageTime((int) singleFixed.get().fixedValue), compareSingle.get().compareAtr, compareSingle.get().conditionType);
            } else if (checkMonthlyItemsType == CheckMonthlyItemsType.AVERAGE_NUMBER_DAY.value ) {
                return setCompareSingleValue((V) new AverageNumberDays(BigDecimal.valueOf(singleFixed.get().fixedValue)), compareSingle.get().compareAtr, compareSingle.get().conditionType);
            } else if (checkMonthlyItemsType == CheckMonthlyItemsType.AVERAGE_NUMBER_TIME.value ) {
                return setCompareSingleValue((V) new AverageNumberTimes((int) singleFixed.get().fixedValue), compareSingle.get().compareAtr, compareSingle.get().conditionType);
            } else if (checkMonthlyItemsType == CheckMonthlyItemsType.AVERAGE_RATIO.value ) {
                return setCompareSingleValue((V) new AverageRatio((int) singleFixed.get().fixedValue), compareSingle.get().compareAtr, compareSingle.get().conditionType);
            } else {
                return null;
            }
        } else if (compareRange.isPresent()) {
            if (checkMonthlyItemsType == CheckMonthlyItemsType.AVERAGE_TIME.value) {
                return setCompareRange((V) new AverageTime((int) compareRange.get().startValue), (V) new AverageTime((int) compareRange.get().endValue), compareRange.get().compareAtr);
            } else if (checkMonthlyItemsType == CheckMonthlyItemsType.AVERAGE_NUMBER_DAY.value) {
                return setCompareRange((V) new AverageNumberDays(BigDecimal.valueOf(compareRange.get().startValue)), (V) new AverageNumberDays(BigDecimal.valueOf(compareRange.get().endValue)), compareRange.get().compareAtr);
            } else if (checkMonthlyItemsType == CheckMonthlyItemsType.AVERAGE_NUMBER_TIME.value ) {
                return setCompareRange((V) new AverageNumberTimes((int) compareRange.get().startValue), (V) new AverageNumberTimes((int) compareRange.get().endValue), compareRange.get().compareAtr);
            } else if (checkMonthlyItemsType == CheckMonthlyItemsType.AVERAGE_RATIO.value) {
                return setCompareRange((V) new AverageRatio((int) compareRange.get().startValue), (V) new AverageRatio((int) compareRange.get().endValue), compareRange.get().compareAtr);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private static <V> CompareRange<V> setCompareRange(V startValue, V endValue, int compareOperator) {
        val compareRange = new CompareRange<V>(compareOperator);
        compareRange.setValue(startValue, endValue);
        return compareRange;
    }

    private static <V> CompareSingleValue<V> setCompareSingleValue(V value, int compareOpertor, int conditionType) {
        val compareSingleValue = new CompareSingleValue<V>(compareOpertor, conditionType);
        compareSingleValue.setValue(value);
        return compareSingleValue;
    }
}
