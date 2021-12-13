package nts.uk.ctx.at.function.dom.alarm.mailsettings;

import lombok.Getter;
import nts.arc.enums.EnumAdaptor;

/**
 * 通常自動区分
 */
@Getter
public enum NormalAutoClassification {
    NORMAL(0, "通常"),
    AUTO(1, "自動");

    public int value;
    public String nameId;

    NormalAutoClassification(int value, String nameId) {
        this.value = value;
        this.nameId = nameId;
    }

    public static NormalAutoClassification of(int value) {
        return EnumAdaptor.valueOf(value, NormalAutoClassification.class);
    }
}
