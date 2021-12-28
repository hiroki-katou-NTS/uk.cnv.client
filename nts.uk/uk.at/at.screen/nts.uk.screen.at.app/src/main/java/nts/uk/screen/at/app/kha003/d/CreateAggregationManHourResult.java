package nts.uk.screen.at.app.kha003.d;

import lombok.val;
import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.*;
import nts.uk.screen.at.app.kha003.*;
import nts.uk.screen.at.app.kha003.exportcsv.ManHourSummaryTableOutputContentDto;
import nts.uk.shr.com.context.AppContexts;
import org.apache.logging.log4j.util.Strings;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ＜＜ScreenQuery＞＞ 集計結果を作成する
 */
@Stateless
public class CreateAggregationManHourResult {
    @Inject
    private ManHourSummaryTableFormatRepository manHourSummaryRepo;

    /**
     * @param code           工数集計表コード
     * @param masterNameInfo マスタ名称情報　（C画面で指定したマスタ）
     * @param workDetailList 工数集計データ．作業詳細データ
     * @param dateList       年月日<List>
     * @param yearMonthList  年月<List>
     */
    public ManHourAggregationResultDto get(String code, MasterNameInformation masterNameInfo, List<WorkDetailData> workDetailList,
                                           List<GeneralDate> dateList, List<YearMonth> yearMonthList) {
        // 1. 作業詳細データを絞り込む(作業詳細データ)
        val workDetailFilters = masterNameInfo.filterWorkDetailData(workDetailList);
        // 2. List<作業詳細データ>＝NULL
        if (CollectionUtil.isEmpty(workDetailFilters))
            throw new BusinessException("Msg_2171");

        // 3. get(工数集計表コード)
        val optManHour = this.manHourSummaryRepo.get(AppContexts.user().companyId(), code);
        if (!optManHour.isPresent()) return null;

        // 4. 工数集計表出力内容を作成する(年月日, 年月, 作業詳細データ, マスタ名称情報)
        val manHourOutputContent = optManHour.get().createOutputContent(dateList, yearMonthList, workDetailFilters, masterNameInfo);

        // Convert domain ManHourSummaryTableOutputContent to dto
        val dispFormat = optManHour.get().getDetailFormatSetting().getDisplayFormat();
        val itemDetails = getSummaryItemDetails(manHourOutputContent.getItemDetails(), dispFormat);
        val totalPeriod = manHourOutputContent.getTotalPeriod().orElse(0);
        val outputContent = new ManHourSummaryTableOutputContentDto(
                itemDetails,
                convertValueDaily(manHourOutputContent.getVerticalTotalValues(), dispFormat),
                formatValue((double)totalPeriod, dispFormat)
        );

        // Convert domain to dto
        val manHourObj =  optManHour.map(opt -> new ManHourSummaryTableFormatDto(
                opt.getCode().v(),
                opt.getName().v(),
                opt.getDetailFormatSetting().getTotalUnit().value,
                opt.getDetailFormatSetting().getDisplayFormat().value,
                opt.getDetailFormatSetting().getDisplayVerticalHorizontalTotal().value,
                getSummaryItemList(opt.getDetailFormatSetting().getSummaryItemList())
        )).orElse(null);

        CountTotalLevel totalLevel = new CountTotalLevel(0);
        countHierarchy(outputContent.getItemDetails(), totalLevel);

        return new ManHourAggregationResultDto(manHourObj, outputContent, Collections.emptyList(), totalLevel.getCountTotalLevel());
    }

    private List<SummaryItemDto> getSummaryItemList(List<SummaryItem> lstItem) {
        return lstItem.stream().map(item -> new SummaryItemDto(item.getHierarchicalOrder(),
                item.getSummaryItemType().value,
                item.getSummaryItemType().nameId
        )).sorted(Comparator.comparingInt(SummaryItemDto::getHierarchicalOrder)).collect(Collectors.toList());
    }

    private List<SummaryItemDetailDto> getSummaryItemDetails(List<SummaryItemDetail> childList, DisplayFormat displayFormat) {
        return childList.isEmpty() ? Collections.emptyList()
                : childList.stream().map(x -> new SummaryItemDetailDto(
                x.getCode(),
                new DisplayInfoDto(x.getDisplayInfo().getCode(), x.getDisplayInfo().getName()),
                getSummaryItemDetails(x.getChildHierarchyList(), displayFormat),
                convertValueDaily(x.getVerticalTotalList(), displayFormat),
                formatValue((double)x.getTotalPeriod().orElse(0), displayFormat)
        )).sorted(Comparator.comparing(x -> x.getDisplayInfo().getCode())).collect(Collectors.toList());
    }

    private List<VerticalValueDailyDto> convertValueDaily(List<VerticalValueDaily> dailyValues, DisplayFormat displayFormat) {
        return dailyValues.stream().map(x -> new VerticalValueDailyDto(
                formatValue((double) x.getWorkingHours(), displayFormat),
                x.getYearMonth() != null ? x.getYearMonth().toString() : null,
                x.getDate() != null ? x.getDate().toString() : null
        )).collect(Collectors.toList());
    }

    /**
     * Format value by display format
     *
     * @param value
     * @param displayFormat
     * @return String
     */
    private String formatValue(Double value, DisplayFormat displayFormat) {
        if (value == 0) return Strings.EMPTY;

        String targetValue = null;
        switch (displayFormat) {
            case DECIMAL:
                BigDecimal decimaValue = new BigDecimal(value);
                decimaValue = decimaValue.divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
                targetValue = String.valueOf(decimaValue.doubleValue());
                break;
            case HEXA_DECIMAL:
                BigDecimal decimalValue = new BigDecimal(value);
                BigDecimal intValue = decimalValue.divideToIntegralValue(BigDecimal.valueOf(60));
                BigDecimal remainValue = decimalValue.subtract(intValue.multiply(BigDecimal.valueOf(60)));
                val valueStr = intValue.add(remainValue.divide(BigDecimal.valueOf(100.00), 2, RoundingMode.HALF_UP)).toPlainString();
                targetValue = valueStr.replace(".", ":");
                break;
            case MINUTE:
                DecimalFormat df = new DecimalFormat("#,###");
                targetValue = df.format(value);
                break;
        }

        return targetValue;
    }

    private void countHierarchy(List<SummaryItemDetailDto> parentList, CountTotalLevel result) {
        int totalLevel = result.getCountTotalLevel();
        if (CollectionUtil.isEmpty(parentList)) return;
        List<SummaryItemDetailDto> childHierarchy = parentList.stream().flatMap(x -> x.getChildHierarchyList().stream()).collect(Collectors.toList());
        totalLevel += 1;
        result.setCountTotalLevel(totalLevel);
        countHierarchy(childHierarchy, result);
    }
}
