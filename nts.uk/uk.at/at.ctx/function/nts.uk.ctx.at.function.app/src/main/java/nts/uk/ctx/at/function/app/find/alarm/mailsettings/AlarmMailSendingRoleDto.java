package nts.uk.ctx.at.function.app.find.alarm.mailsettings;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.IndividualWkpClassification;

import java.util.List;


@Data
@AllArgsConstructor
public class AlarmMailSendingRoleDto {
    /**
     * 個人職場区分
     */
    private int individualWkpClassify;

    /**
     * ロール設定
     */
    private boolean roleSetting;

    /**
     * マスタチェック結果を就業担当へ送信
     */
    private boolean sendResult;

    /**
     * ロールID
     */
    private List<String> roleIds;
}
