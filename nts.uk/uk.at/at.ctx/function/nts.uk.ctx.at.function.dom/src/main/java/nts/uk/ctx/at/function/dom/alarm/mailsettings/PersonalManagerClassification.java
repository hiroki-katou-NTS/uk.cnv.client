package nts.uk.ctx.at.function.dom.alarm.mailsettings;

import lombok.Getter;
import nts.arc.enums.EnumAdaptor;

/**
 * 本人管理区分
 */
@Getter
public enum PersonalManagerClassification {
    EMAIL_SETTING_FOR_PERSON(0, "本人宛メール設定"),
    EMAIL_SETTING_FOR_ADMIN(1, "管理者宛メール設定");

    public int value;
    public String nameId;

    PersonalManagerClassification(int value, String nameId) {
        this.value = value;
        this.nameId = nameId;
    }

    public static PersonalManagerClassification of(int value) {
        return EnumAdaptor.valueOf(value, PersonalManagerClassification.class);
    }
}
