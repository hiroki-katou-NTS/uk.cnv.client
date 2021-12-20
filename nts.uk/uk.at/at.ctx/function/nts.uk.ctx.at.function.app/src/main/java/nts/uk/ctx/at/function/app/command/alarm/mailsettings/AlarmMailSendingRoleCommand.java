package nts.uk.ctx.at.function.app.command.alarm.mailsettings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.AlarmMailSendingRole;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.IndividualWkpClassification;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;

@AllArgsConstructor
@Getter
public class AlarmMailSendingRoleCommand {
    /** 個人職場区分 */
    private int individualWkpClassify;

    /** ロール設定 */
    private boolean roleSetting;

    /** マスタチェック結果を就業担当へ送信 */
    private boolean sendResult;

    /** ロールID */
    private List<String> roleIds;

    public AlarmMailSendingRole toDomain() {
        return new AlarmMailSendingRole(
                IndividualWkpClassification.of(this.individualWkpClassify),
                this.roleSetting,
                this.sendResult,
                roleIds
        );
    }
}
