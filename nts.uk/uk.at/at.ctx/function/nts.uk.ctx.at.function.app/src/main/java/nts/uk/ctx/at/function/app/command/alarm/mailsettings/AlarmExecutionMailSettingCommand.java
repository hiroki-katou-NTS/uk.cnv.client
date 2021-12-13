package nts.uk.ctx.at.function.app.command.alarm.mailsettings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.function.app.find.alarm.mailsettings.ContentMailSettingDto;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.*;
import nts.uk.shr.com.context.AppContexts;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class AlarmExecutionMailSettingCommand {
    /**
     * 個人職場区分
     */
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
     * 0: false, 1: true
     */
    private boolean sendResult;

    public AlarmListExecutionMailSetting toDomain() {
        return new AlarmListExecutionMailSetting(
                IndividualWkpClassification.of(this.individualWkpClassify),
                NormalAutoClassification.of(this.normalAutoClassify),
                PersonalManagerClassification.of(this.personalManagerClassify),
                Optional.ofNullable(this.contentMailSettings != null ?
                        new MailSettings(
                                this.contentMailSettings.getSubject(),
                                this.contentMailSettings.getText(),
                                this.contentMailSettings.getMailAddressCC().stream().map(MailAddress::new).collect(Collectors.toList()),
                                this.contentMailSettings.getMailAddressBCC().stream().map(MailAddress::new).collect(Collectors.toList()),
                                this.contentMailSettings.getMailRely())
                        : null),
                Optional.ofNullable(this.senderAddress != null ? new MailAddress(this.senderAddress) : null),
                this.sendResult
        );
    }
}
