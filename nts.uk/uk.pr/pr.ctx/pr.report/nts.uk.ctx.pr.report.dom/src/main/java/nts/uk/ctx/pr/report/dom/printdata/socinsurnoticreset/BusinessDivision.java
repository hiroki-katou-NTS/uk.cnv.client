package nts.uk.ctx.pr.report.dom.printdata.socinsurnoticreset;

public enum BusinessDivision {
    //会社名・住所を出力
    OUTPUT_COMPANY_NAME(0, "Enum_BusinessDivision_OUTPUT_COMPANY_NAME"),
    //社会保険事業所名・住所を出力
    OUTPUT_SIC_INSURES(1, "Enum_BusinessDivision_OUTPUT_SIC_INSURES"),
    DO_NOT_OUTPUT(2, "Enum_BusinessDivision_DO_NOT_OUTPUT");

    /** The value. */
    public final int value;

    /** The name id. */
    public final String nameId;
    private BusinessDivision(int value, String nameId)
    {
        this.value = value;
        this.nameId = nameId;
    }
}
