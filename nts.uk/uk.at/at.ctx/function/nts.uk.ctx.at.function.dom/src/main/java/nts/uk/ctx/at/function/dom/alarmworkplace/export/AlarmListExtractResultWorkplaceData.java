package nts.uk.ctx.at.function.dom.alarmworkplace.export;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.function.dom.alarmworkplace.extractresult.AlarmListExtractInfoWorkplace;
import nts.uk.ctx.at.function.dom.alarmworkplace.extractresult.ExtractResult;
import nts.uk.ctx.at.shared.dom.scherec.alarm.alarmlistactractionresult.AlarmValueDate;
import nts.uk.shr.com.i18n.TextResource;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * data export excel : アラームリスト抽出結果（職場別）
 */
@Getter
@AllArgsConstructor
public class AlarmListExtractResultWorkplaceData {

    /**
     * Id
     */
    private String recordId;
    /**
     * アラーム値メッセージ
     */
    private String alarmValueMessage;
    /**
     * アラーム値日付
     */
    private String alarmValueDate;
    /**
     * アラーム項目名
     */
    private String alarmItemName;
    /**
     * カテゴリ名
     */
    private String categoryName;
    /**
     * チェック対象値
     */
    private String checkTargetValue;
    /**
     * 区分
     */
    private int category;
    /**
     * 開始日
     */
    private String startTime;
    /**
     * コメント
     */
    private String comment;
    /**
     * 職場ID
     */
    private String workplaceId;

    /**
     * 職場コード
     */
    private String workplaceCode;

    /**
     * 職場名
     */
    private String workplaceName;

    /**
     * 階層コード
     */
    private String hierarchyCode;

    /**
     * 取得した職場情報一覧から「アラーム抽出結果（職場別）」にデータをマッピングする
     */
    public static List<AlarmListExtractResultWorkplaceData> fromDomains(List<AlarmListExtractInfoWorkplace> alExtractInfos) {
        List<AlarmListExtractResultWorkplaceData> extractResultDtos = new ArrayList<>();
        for (AlarmListExtractInfoWorkplace alExtractInfo : alExtractInfos) {
            ExtractResult extractResult = alExtractInfo.getExtractResult();
            AlarmValueDate date = extractResult.getAlarmValueDate();
            String dateResult = convertAlarmValueDate(date.getStartDate());
            if (date.getEndDate().isPresent()) {
                dateResult += "～" + convertAlarmValueDate(date.getEndDate().get());
            }

            AlarmListExtractResultWorkplaceData result = new AlarmListExtractResultWorkplaceData(
                    alExtractInfo.getRecordId(),
                    extractResult.getAlarmValueMessage().v(),
                    dateResult,
                    extractResult.getAlarmItemName().v(),
                    alExtractInfo.getCategoryName(),
                    extractResult.getCheckTargetValue(),
                    alExtractInfo.getCategory().value,
                    date.getStartDate(),
                    extractResult.getComment().isPresent() ? extractResult.getComment().get().v() : "",
                    extractResult.getWorkplaceId().orElse(""),
                    extractResult.getWorkplaceCode().orElse(""),
                    extractResult.getWorkplaceName().orElse(""),
                    extractResult.getHierarchyCode().orElse(""));
            extractResultDtos.add(result);
        }

        Comparator<AlarmListExtractResultWorkplaceData> compare = Comparator
                .comparing(AlarmListExtractResultWorkplaceData::getHierarchyCode)
                .thenComparing(AlarmListExtractResultWorkplaceData::getWorkplaceCode)
                .thenComparing(AlarmListExtractResultWorkplaceData::getCategory)
                .thenComparing(AlarmListExtractResultWorkplaceData::getStartTime);
        return extractResultDtos.stream().sorted(compare).collect(Collectors.toList());
    }

    private static String convertAlarmValueDate(String date) {
        switch (date.length()) {
            case 6:
                return date.substring(0, 4) + "/" + date.substring(4, 6);
            case 8:
                return date.substring(0, 4) + "/" + date.substring(4, 6) + "/" + date.substring(6, 8);
            default:
                return date;
        }
    }

}
