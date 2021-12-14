package nts.uk.ctx.at.function.dom.alarm.alarmlist.persistenceextractresult;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDateTime;

import java.util.Optional;

@Getter
@AllArgsConstructor
public class WorkplaceHistoryTopAlarmParamMerged {
    private String employeeId;

    // 職場ID
    private String workplaceId;

    // 履歴ID
    private String historyID;

    //通常職場ID
    private String normalWorkplaceID;

    /**
     * アラーム分類
     */
    private int alarmClassification;

    /**
     * 発生日時
     */
    private GeneralDateTime occurrenceDateTime;

    /**
     * 表示社員ID
     */
    private String displaySId;

    /**
     * 表示社員区分
     */
    private int displayAtr;

    /**
     * パターンコード
     */
    private Optional<String> patternCode;

    /**
     * パターン名
     */
    private Optional<String> patternName;

    /**
     * リンクURL
     */
    private Optional<String> linkUrl;

    /**
     * 表示メッセージ
     */
    private Optional<String> displayMessage;
}
