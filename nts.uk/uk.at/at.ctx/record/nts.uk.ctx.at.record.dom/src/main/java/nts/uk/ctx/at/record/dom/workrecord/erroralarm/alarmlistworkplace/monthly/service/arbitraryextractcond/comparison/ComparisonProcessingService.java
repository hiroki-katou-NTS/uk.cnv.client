package nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.monthly.service.arbitraryextractcond.comparison;

import lombok.val;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.extractresult.ExtractResultDto;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.monthly.ExtractionMonthlyCon;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.monthly.enums.AverageNumberOfDays;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.monthly.enums.AverageNumberOfTimes;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.monthly.enums.AverageRatio;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.monthly.enums.AverageTime;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.enums.ConvertCompareTypeToText;
import nts.uk.ctx.at.shared.dom.scherec.alarm.alarmlistactractionresult.AlarmValueDate;
import nts.uk.ctx.at.shared.dom.scherec.alarm.alarmlistactractionresult.AlarmValueMessage;
import nts.uk.ctx.at.shared.dom.scherec.alarm.alarmlistactractionresult.MessageDisplay;
import nts.uk.ctx.at.shared.dom.workrecord.alarm.attendanceitemconditions.CheckConditions;
import nts.uk.ctx.at.shared.dom.workrecord.alarm.attendanceitemconditions.CompareRange;
import nts.uk.ctx.at.shared.dom.workrecord.alarm.attendanceitemconditions.CompareSingleValue;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.com.time.TimeWithDayAttr;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.text.DecimalFormat;
import java.util.Optional;

import static nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.monthly.enums.CheckMonthlyItemsType.AVERAGE_NUMBER_TIME;
import static nts.uk.ctx.at.record.dom.workrecord.erroralarm.alarmlistworkplace.monthly.enums.CheckMonthlyItemsType.AVERAGE_TIME;

/**
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.勤務実績.勤務実績.勤務実績のエラーアラーム設定.アラームリスト（職場）.月次のアラームチェック.アルゴリズム.月次の集計処理.任意抽出条件をチェック.比較処理
 *
 * @author Le Huu Dat
 */
@Stateless
public class ComparisonProcessingService {

    @Inject
    private ConvertCompareTypeToText convertCompareTypeToText;

    /**
     * 比較処理
     *
     * @param workplaceId     職場ID
     * @param condition       チェック条件
     * @param avgTime         比較値
     * @param averageTimeName 平均値　（Enum）
     * @param ym              年月
     * @return 抽出結果
     */
    public ExtractResultDto compare(String workplaceId, ExtractionMonthlyCon condition,
                                    Double avgTime, String averageTimeName, YearMonth ym) {
        // 「Input．比較値」とチェック条件を比較
        boolean check = condition.checkTarget(avgTime);
        CheckConditions checkConditions = condition.getCheckConditions();

        String averageTime = avgTime.toString();
        if(condition.getCheckMonthlyItemsType() == AVERAGE_TIME) {
            TimeWithDayAttr avgTime1 = new TimeWithDayAttr(avgTime.intValue());
            averageTime = avgTime1.getRawTimeWithFormat();
        }
        String message;
        if (checkConditions.isSingleValue()) {
            CompareSingleValue compareSingleValue = ((CompareSingleValue) checkConditions);
            if (!check) return null;
            String timeToCompare = compareSingleValue.getValue().toString();
            if(condition.getCheckMonthlyItemsType() == AVERAGE_TIME) {
                TimeWithDayAttr time = new TimeWithDayAttr(Integer.parseInt(timeToCompare));
                timeToCompare = time.getRawTimeWithFormat();
            }
            message = TextResource.localize(
                    "KAL020_402",
                    averageTimeName,
                    convertCompareTypeToText.convertCompareType(compareSingleValue.getCompareOpertor().value).getCompareLeft(),
                    timeToCompare,
                    averageTime
            );
        } else {
            CompareRange compareRange = ((CompareRange) checkConditions);
            if (!check) return null;

            message = getFormula(averageTimeName, compareRange, condition, averageTime);
        }

        // 抽出結果を作成
        // 作成した抽出結果を返す
        return new ExtractResultDto(new AlarmValueMessage(message),
                new AlarmValueDate(String.valueOf(ym.v()), Optional.empty()),
                condition.getMonExtracConName().v(),
                Optional.ofNullable(TextResource.localize("KAL020_408", averageTime)),
                Optional.of(new MessageDisplay(condition.getMessageDisp().v())),
                workplaceId);
    }

    private String getFormula(String averageTimeName, CompareRange compareRange,ExtractionMonthlyCon condition, String averageTime) {
        String formula = "";
        String timeStart = compareRange.getStartValue().toString();
        String timeEnd = compareRange.getEndValue().toString();
        if(condition.getCheckMonthlyItemsType() == AVERAGE_TIME) {
            TimeWithDayAttr timeStart1 = new TimeWithDayAttr(Integer.parseInt(timeStart));
            timeStart = timeStart1.getRawTimeWithFormat();
            TimeWithDayAttr timeEnd1 = new TimeWithDayAttr(Integer.parseInt(timeEnd));
            timeEnd = timeEnd1.getRawTimeWithFormat();
        }
        switch (compareRange.getCompareOperator()) {
            case BETWEEN_RANGE_OPEN:
                formula = TextResource.localize("KAL020_404", timeStart, averageTimeName, timeEnd, averageTime);
                break;
            case BETWEEN_RANGE_CLOSED:
                formula = TextResource.localize("KAL020_405", timeStart, averageTimeName, timeEnd, averageTime);
                break;
            case OUTSIDE_RANGE_OPEN:
                formula = TextResource.localize("KAL020_406", timeStart, averageTimeName, timeEnd, averageTime);
                break;
            case OUTSIDE_RANGE_CLOSED:
                formula = TextResource.localize("KAL020_407", timeStart, averageTimeName, timeEnd, averageTime);
                break;
        }

        return formula;
    }
}
