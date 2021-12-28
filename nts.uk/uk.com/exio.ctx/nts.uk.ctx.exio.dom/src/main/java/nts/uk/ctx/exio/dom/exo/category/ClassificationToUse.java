package nts.uk.ctx.exio.dom.exo.category;

/**
 * 締め使う区分
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.外部入出力.外部出力.外部出力カテゴリ.外部出力カテゴリ.締め使う区分
 */
public enum ClassificationToUse {
    /**
     * 使う
     */
    USE(1),
    /**
     * 年月
     */
    DO_NOT_USE(0);

    /**
     * The value.
     */
    public final int value;

    private ClassificationToUse(int value) {
        this.value = value;
    }
}
