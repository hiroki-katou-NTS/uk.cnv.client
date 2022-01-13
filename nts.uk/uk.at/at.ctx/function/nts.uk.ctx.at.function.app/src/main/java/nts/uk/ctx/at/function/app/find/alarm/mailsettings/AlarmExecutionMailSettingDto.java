package nts.uk.ctx.at.function.app.find.alarm.mailsettings;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AlarmExecutionMailSettingDto {
    private MailSettingInfo mailSettingInfo;
    private boolean alreadyConfigured;
}

@AllArgsConstructor
@Getter
class MailSettingInfo {
    private int individualWkpClassify;

    /**
     * 通常自動区分
     */
    private int normalAutoClassify;

    /**
     * 本人管理区分
     */
    private int personalManagerClassify;

    /**
     * 内容メール設定
     */
    private ContentMailSettingDto contentMailSettings;

    /**
     * 送信元アドレス
     */
    private String senderAddress;

    /**
     * マスタチェック結果を就業担当へ送信
     */
    private boolean sendResult;
}
