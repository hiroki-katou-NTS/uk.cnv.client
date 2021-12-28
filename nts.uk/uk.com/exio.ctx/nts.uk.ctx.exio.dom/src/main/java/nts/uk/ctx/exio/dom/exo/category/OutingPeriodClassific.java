package nts.uk.ctx.exio.dom.exo.category;


/**
 * 外部出期間区分
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.外部入出力.外部出力.外部出力カテゴリ.外部出力カテゴリ.外部出期間区分
 */
public enum OutingPeriodClassific {
    /**
     * 基準日
     */
    REFERENCE_DATE(3),
    /**
     * 年月
     */
    YEAR_MONTH(2),
    /**
     * 年月日
     */
    DATE(1),
    /**
     * 設定なし
     */
    NO_SETTING(0);
    /**
     * The value.
     */
    public final int value;

    private OutingPeriodClassific(int value) {
        this.value = value;
    }
}