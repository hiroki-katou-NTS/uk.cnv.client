package nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import org.apache.logging.log4j.util.Strings;
import org.eclipse.persistence.internal.xr.ValueObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 	フォーマット詳細設定
 */
@Getter
@AllArgsConstructor
public class DetailFormatSetting extends ValueObject {
    /** 表示形式 */
    private DisplayFormat displayFormat;
    /** 合計単位 */
    private TotalUnit totalUnit;
    /** 縦計・横計を表示する */
    private NotUseAtr displayVerticalHorizontalTotal;
    /** 集計項目一覧 */
    private List<SummaryItem> summaryItemList;

    /**
     * [C-1] フォーマット詳細設定
     * @return フォーマット詳細設定
     */
    public static DetailFormatSetting create(DisplayFormat displayFormat, TotalUnit totalUnit, NotUseAtr displayVertHoriTotal,
                               List<SummaryItem> summaryItemList) {
        summaryItemList.sort(Comparator.comparing(SummaryItem::getHierarchicalOrder));
        return new DetailFormatSetting(displayFormat, totalUnit, displayVertHoriTotal, summaryItemList);
    }

    /**
     * [1] 工数集計表出力内容を作成する
     * @param dateList List<年月日>
     * @param yearMonthList List<年月>
     * @param workDetailList List<作業詳細データ>
     * @param masterNameInfo マスタ名称情報
     * @return 工数集計表出力内容
     */
    public ManHourSummaryTableOutputContent createManHourSummaryTableOutputContent(List<GeneralDate> dateList, List<YearMonth> yearMonthList,
                                                                                   List<WorkDetailData> workDetailList,
                                                                                   MasterNameInformation masterNameInfo) {
        val firstItem = summaryItemList.stream().findFirst().orElse(null);
        val itemDetail = createSummaryItemDetail(dateList, yearMonthList, firstItem, workDetailList, masterNameInfo);
        val outputContent = ManHourSummaryTableOutputContent.create(itemDetail);

        if (displayVerticalHorizontalTotal == NotUseAtr.USE)
            outputContent.calculateTotal(totalUnit, dateList, yearMonthList);

        return outputContent;
    }

    /**
     * [prv-1] 集計項目詳細を作成する
     * @param dateList List<年月日>
     * @param yearMonthList List<年月>
     * @param summaryItem 集計項目
     * @param workDetailList List<作業詳細データ>
     * @param masterNameInfo マスタ名称情報
     * @return 集計項目詳細
     */
    private List<SummaryItemDetail> createSummaryItemDetail(List<GeneralDate> dateList, List<YearMonth> yearMonthList,
                                                      SummaryItem summaryItem, List<WorkDetailData> workDetailList,
                                                      MasterNameInformation masterNameInfo) {
        List<SummaryItemDetail> summaryItemDetails = new ArrayList<>();
        if (Objects.isNull(summaryItem)) return summaryItemDetails;

        int hierarchyNo = summaryItemList.indexOf(summaryItem);
        boolean lastFlag = hierarchyNo >= summaryItemList.size() - 1;
        //$作業データグループ = 作業詳細リスト：map groupingBy $.集計項目をマッピングする(対象項目)
        Map<String, List<WorkDetailData>> workDataGroup = workDetailList.stream()
                .filter(x -> !x.mapSummaryItem(summaryItem.getSummaryItemType()).equals(Strings.EMPTY))
                .collect(Collectors.groupingBy(x -> x.mapSummaryItem(summaryItem.getSummaryItemType())));

        for (val entry : workDataGroup.entrySet()) {
            val key = entry.getKey();
            val value = entry.getValue();
            if (lastFlag) {
                SummaryItemDetail itemDetail = createItemDetailForBottomLayer(dateList, yearMonthList, key, summaryItem.getSummaryItemType(), value, masterNameInfo);
                summaryItemDetails.add(itemDetail);
            } else {
                val itemDetail = createItemDetailOtherThanBottomLayer(dateList, yearMonthList, key, summaryItem, value, masterNameInfo);
                summaryItemDetails.add(itemDetail);
            }
        }

        return summaryItemDetails;
    }

    /**
     * [prv-2] 最下層以外の項目詳細を作成する
     * @param dateList List<年月日>
     * @param yearMonthList List<年月>
     * @param code コード
     * @param summaryItem 集計項目
     * @param workDetailList List<作業詳細データ>
     * @param masterNameInfo マスタ名称情報
     * @return 集計項目詳細
     */
    private SummaryItemDetail createItemDetailOtherThanBottomLayer(List<GeneralDate> dateList, List<YearMonth> yearMonthList,
                                                                   String code, SummaryItem summaryItem, List<WorkDetailData> workDetailList,
                                                                   MasterNameInformation masterNameInfo) {
        val displayInfo = masterNameInfo.getDisplayInfo(code, summaryItem.getSummaryItemType());
        val nextTargetItem = summaryItemList.stream().filter(x -> x.getHierarchicalOrder() == summaryItem.getHierarchicalOrder() + 1).findFirst().orElse(null);
        val nextLevelList = createSummaryItemDetail(dateList, yearMonthList, nextTargetItem, workDetailList, masterNameInfo);
        val summaryItemDetail = SummaryItemDetail.createNew(code, displayInfo, nextLevelList);

        if (displayVerticalHorizontalTotal == NotUseAtr.USE)
            summaryItemDetail.calculateTotal(totalUnit, dateList, yearMonthList);

        return summaryItemDetail;
    }

    /**
     * [prv-3] 最下層の集計項目詳細を作成する
     * @param dateList List<年月日>
     * @param yearMonthList List<年月>
     * @param code コード
     * @param summaryItemType 集計項目種類
     * @param workDetailList List<作業詳細データ>
     * @param masterNameInfo マスタ名称情報
     * @return 集計項目詳細
     */
    private SummaryItemDetail createItemDetailForBottomLayer(List<GeneralDate> dateList, List<YearMonth> yearMonthList,
                                                                   String code, SummaryItemType summaryItemType, List<WorkDetailData> workDetailList,
                                                                   MasterNameInformation masterNameInfo) {
        val displayInfo = masterNameInfo.getDisplayInfo(code, summaryItemType);
        val summaryItemDetail = SummaryItemDetail.createNewBottomLayer(code, displayInfo, totalUnit ,dateList, yearMonthList, workDetailList);

        if (displayVerticalHorizontalTotal == NotUseAtr.USE)
            summaryItemDetail.calculateHorizontalTotal();

        return summaryItemDetail;
    }
}
