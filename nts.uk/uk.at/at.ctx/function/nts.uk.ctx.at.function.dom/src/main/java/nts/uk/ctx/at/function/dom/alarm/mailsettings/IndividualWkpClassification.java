package nts.uk.ctx.at.function.dom.alarm.mailsettings;

import lombok.Getter;
import nts.arc.enums.EnumAdaptor;

/**
 * 個人職場区分
 */
@Getter
public enum IndividualWkpClassification {
    INDIVIDUAL(0, "個人別"),
    WORKPLACE(1, "職場別");

    public int value;
    public String nameId;

    IndividualWkpClassification(int value, String nameId) {
        this.value = value;
        this.nameId = nameId;
    }

    public static IndividualWkpClassification of(int value) {
        return EnumAdaptor.valueOf(value, IndividualWkpClassification.class);
    }
}
