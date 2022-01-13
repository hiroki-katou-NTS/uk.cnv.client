package nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * TP:  工数集計表出力内容
 */
@Getter
@AllArgsConstructor
public class ManHourSummaryTableOutputContent {
    /** 項目詳細 */
    private final List<SummaryItemDetail> itemDetails;
    /** 縦計値 */
    private List<VerticalValueDaily> verticalTotalValues;
    /** 期間合計 */
    private Optional<Integer> totalPeriod;

    /**
     * [C-1] 新規作成
     * @param lstItemDetail 項目詳細
     */
    public static ManHourSummaryTableOutputContent create(List<SummaryItemDetail> lstItemDetail){
        return new ManHourSummaryTableOutputContent(
                lstItemDetail,
                Collections.emptyList(),
                Optional.empty()
        );
    }

    /**
     * [1] 合計を計算する
     * @param totalUnit 合計単位
     * @param dateList List<年月日>
     * @param yearMonthList List<年月>
     */
    public void calculateTotal(TotalUnit totalUnit, List<GeneralDate> dateList, List<YearMonth> yearMonthList) {
        if (totalUnit == TotalUnit.DATE)
            calculateVerticalTotalByDate(dateList);
        else
            calculateVerticalTotalByYearMonth(yearMonthList);

        calculateHorizontalTotal();
    }

    /**
     * [prv-1] 年月日別に縦計を計算する
     * @param dateList List<年月日>
     */
    private void calculateVerticalTotalByDate(List<GeneralDate> dateList) {
        List<VerticalValueDaily> lstVertical = new ArrayList<>();
        for (GeneralDate date : dateList) {
            val childVerticalList = itemDetails.stream().flatMap(x -> x.getVerticalTotalList().stream()).collect(Collectors.toList());
            val workingTime = childVerticalList.stream().filter(x -> x.getDate().equals(date)).mapToInt(VerticalValueDaily::getWorkingHours).sum();
            lstVertical.add(new VerticalValueDaily(workingTime, null, date));
        }
        if (!lstVertical.isEmpty())
            verticalTotalValues = lstVertical;
    }

    /**
     * [prv-2] 年月別に縦計を計算する
     * @param yearMonthList List<年月>
     */
    private void calculateVerticalTotalByYearMonth(List<YearMonth> yearMonthList) {
        List<VerticalValueDaily> lstVertical = new ArrayList<>();
        for (val ym : yearMonthList) {
            val childVerticalList = itemDetails.stream().flatMap(x -> x.getVerticalTotalList().stream()).collect(Collectors.toList());
            val workingTime = childVerticalList.stream().filter(x -> x.getYearMonth().equals(ym)).mapToInt(VerticalValueDaily::getWorkingHours).sum();
            lstVertical.add(new VerticalValueDaily(workingTime, ym, null));
        }
        if (!lstVertical.isEmpty())
            verticalTotalValues = lstVertical;
    }

    /**
     * [prv-3] 横計を計算する
     */
    private void calculateHorizontalTotal() {
        val workTime = verticalTotalValues.stream().mapToInt(VerticalValueDaily::getWorkingHours).sum();
        totalPeriod = Optional.of(workTime);
    }
}
