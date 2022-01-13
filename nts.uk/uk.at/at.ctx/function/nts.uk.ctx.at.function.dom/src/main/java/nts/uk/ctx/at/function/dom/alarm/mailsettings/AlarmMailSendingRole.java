package nts.uk.ctx.at.function.dom.alarm.mailsettings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;

import java.util.List;

/**
 * 	アラームメール送信ロール
 */
@AllArgsConstructor
@Getter
public class AlarmMailSendingRole extends AggregateRoot {
    /** 個人職場区分 */
    private IndividualWkpClassification individualWkpClassify;

    /** ロール設定 */
    private boolean roleSetting;

    /** マスタチェック結果を就業担当へ送信 */
    private boolean sendResult;

    /** ロールID */
    private List<String> roleIds;
}
