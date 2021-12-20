package nts.uk.screen.at.app.ksu003.d.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class CompletionMethodControlDto {
    /**
     * 完了実行方法
     */
    Integer completionExecutionMethod;

    /**
     * 完了方法制御
     */
    List<Integer> completionMethodControl;

    /**
     * アラームチェックコードリスト
     */
    List<String> alarmCheckCodeList;
}
