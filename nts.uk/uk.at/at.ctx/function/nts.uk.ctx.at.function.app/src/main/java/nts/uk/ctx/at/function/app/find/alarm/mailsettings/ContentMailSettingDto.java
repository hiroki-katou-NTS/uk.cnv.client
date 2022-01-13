package nts.uk.ctx.at.function.app.find.alarm.mailsettings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.primitive.PrimitiveValueBase;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.MailSettings;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ContentMailSettingDto {
    private String subject;

    private String text;

    private List<String> mailAddressBCC;

    private List<String> mailAddressCC;

    private String mailRely;

    public static ContentMailSettingDto create(MailSettings mailSetting) {
        return new ContentMailSettingDto(
                mailSetting.getSubject().isPresent() ? mailSetting.getSubject().get().v() : null,
                mailSetting.getText().isPresent() ? mailSetting.getText().get().v() : null,
                mailSetting.getMailAddressBCC().stream().map(PrimitiveValueBase::v).collect(Collectors.toList()),
                mailSetting.getMailAddressCC().stream().map(PrimitiveValueBase::v).collect(Collectors.toList()),
                mailSetting.getMailRely().isPresent() ? mailSetting.getMailRely().get().v() : null
        );
    }
}
