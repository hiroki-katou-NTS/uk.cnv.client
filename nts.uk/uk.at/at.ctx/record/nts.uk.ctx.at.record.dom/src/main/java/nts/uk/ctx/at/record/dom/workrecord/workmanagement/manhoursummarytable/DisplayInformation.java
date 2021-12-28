package nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.shr.com.i18n.TextResource;
import org.eclipse.persistence.internal.xr.ValueObject;

/**
 * 表示情報
 */
@AllArgsConstructor
@Getter
public class DisplayInformation extends ValueObject {
    /** コード */
    private final String code;
    /** 名称 */
    private final String name;

    /**
     * [C-1] 削除済みマスタ
     *
     * @return 表示情報
     */
    public static DisplayInformation deletedMaster() {
        return new DisplayInformation(TextResource.localize("KHA003_101"), TextResource.localize("KHA003_101"));
    }

    /**
     * [C-2] 名称取得できない
     *
     * @param code コード
     * @return 表示情報
     */
    public static DisplayInformation cannotGetName(String code) {
        return new DisplayInformation(code, TextResource.localize("KHA003_101"));
    }
}
