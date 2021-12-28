package nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import org.eclipse.persistence.internal.xr.ValueObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 	集計項目詳細
 */
@AllArgsConstructor
@Getter
public class SummaryItemDetail extends ValueObject {
    /** コード */
    private final String code;
    /** 表示情報 */
    private final DisplayInformation displayInfo;
    /** 子階層リスト */
    private final List<SummaryItemDetail> childHierarchyList;
    /** 縦計リスト */
    private List<VerticalValueDaily> verticalTotalList;
    /** 期間合計 */
    private Optional<Integer> totalPeriod;

    /**
     * 	[C-1] 新規作成
     * @param code コード
     * @param displayInfo 表示情報
     * @param childHierarchyList 子階層リスト
     * @return 集計項目詳細
     */
    public static SummaryItemDetail createNew(String code, DisplayInformation displayInfo, List<SummaryItemDetail> childHierarchyList) {
        return new SummaryItemDetail(code, displayInfo, childHierarchyList, Collections.emptyList(), Optional.empty());
    }

    /**
     * [C-2] 最下層を新規作成
     * @param code コード
     * @param displayInfo 表示情報
     * @param totalUnit 合計単位
     * @param dateList List<年月日>
     * @param yearMonthList List<年月>
     * @param workDetailList List<作業詳細データ>
     * @return 集計項目詳細
     */
    public static SummaryItemDetail createNewBottomLayer(String code, DisplayInformation displayInfo, TotalUnit totalUnit,
                                                         List<GeneralDate> dateList, List<YearMonth> yearMonthList,
                                                         List<WorkDetailData> workDetailList) {
        List<VerticalValueDaily> verticalTotalList;
        if (totalUnit == TotalUnit.DATE)
            verticalTotalList = createWorkTimeDetailByDate(dateList, workDetailList);
        else
            verticalTotalList = createWorkTimeDetailByYearMonth(yearMonthList, workDetailList);

        return new SummaryItemDetail(code, displayInfo, Collections.emptyList(), verticalTotalList, Optional.empty());
    }

    /**
     * 	[1] 合計を計算する
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
     * [2] 横計を計算する
     */
    public void calculateHorizontalTotal() {
        val workingHours = verticalTotalList.stream().mapToInt(VerticalValueDaily::getWorkingHours).sum();
        totalPeriod = Optional.of(workingHours);
    }

    /**
     * [prv-1] 年月日別に縦計を計算する
     * @param dateList 	List<年月日>
     */
    private void calculateVerticalTotalByDate(List<GeneralDate> dateList) {
        List<VerticalValueDaily> lstVertical = new ArrayList<>();
        for (GeneralDate date : dateList) {
            val childVerticalList = childHierarchyList.stream().flatMap(x -> x.getVerticalTotalList().stream()).collect(Collectors.toList());
            val workingTime = childVerticalList.stream().filter(x -> x.getDate().equals(date)).mapToInt(VerticalValueDaily::getWorkingHours).sum();
            lstVertical.add(new VerticalValueDaily(workingTime, null, date));
        }
        if (!lstVertical.isEmpty())
            verticalTotalList = lstVertical;
    }

    /**
     * [prv-2] 年月別に縦計を計算する
     * @param yearMonthList List<年月>
     */
    private void calculateVerticalTotalByYearMonth(List<YearMonth> yearMonthList) {
        List<VerticalValueDaily> lstVertical = new ArrayList<>();
        for (val ym : yearMonthList) {
            val childVerticalList = childHierarchyList.stream().flatMap(x -> x.getVerticalTotalList().stream()).collect(Collectors.toList());
            val workingTime = childVerticalList.stream().filter(x -> x.getYearMonth().equals(ym)).mapToInt(VerticalValueDaily::getWorkingHours).sum();
            lstVertical.add(new VerticalValueDaily(workingTime, ym, null));
        }
        if (!lstVertical.isEmpty())
            verticalTotalList = lstVertical;
    }

    /**
     * [prv-3] 年月日別に作業時間明細を作成する
     * @param dateList List<年月日>
     * @param workDetailList List<作業詳細データ>
     * @return List<日々縦計値>
     */
    private static List<VerticalValueDaily> createWorkTimeDetailByDate(List<GeneralDate> dateList, List<WorkDetailData> workDetailList) {
        List<VerticalValueDaily> lstVertical = new ArrayList<>();
        for (val date : dateList) {
            val workingTime = workDetailList.stream().filter(x -> x.getDate().equals(date)).mapToInt(WorkDetailData::getTotalWorkingHours).sum();
            lstVertical.add(new VerticalValueDaily(workingTime, null, date));
        }

        return lstVertical;
    }

    /**
     * [prv-4] 年月別に作業時間明細を作成する
     * @param yearMonthList List<年月>
     * @param workDetailList List<作業詳細データ>
     * @return List<日々縦計値>
     */
    private static List<VerticalValueDaily> createWorkTimeDetailByYearMonth(List<YearMonth> yearMonthList, List<WorkDetailData> workDetailList) {
        List<VerticalValueDaily> lstVertical = new ArrayList<>();
        for (val ym : yearMonthList) {
            val workingTime = workDetailList.stream().filter(x -> x.getDate().month() == ym.month()).mapToInt(WorkDetailData::getTotalWorkingHours).sum();
            lstVertical.add(new VerticalValueDaily(workingTime, ym, null));
        }

        return lstVertical;
    }
}
