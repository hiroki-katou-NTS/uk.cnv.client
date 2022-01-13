package nts.uk.ctx.at.function.app.find.alarm.mailsettings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailSettingsContentDto {

    /**
     * 件名
     */
    private String subject;
    /**
     * 本文
     */
    private String text;
    /**
     * BBCメールアドレス
     */
    private List<String> mailAddressBCC;
    /**
     * CCメールアドレス
     */
    private List<String> mailAddressCC;
    /**
     * 返信用メールアドレス
     */
    private String mailRely;
}