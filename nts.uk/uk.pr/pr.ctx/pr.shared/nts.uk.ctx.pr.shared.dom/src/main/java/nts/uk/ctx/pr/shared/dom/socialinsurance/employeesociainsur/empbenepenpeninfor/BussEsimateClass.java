package nts.uk.ctx.pr.shared.dom.socialinsurance.employeesociainsur.empbenepenpeninfor;

public enum BussEsimateClass {
    HEAL_INSUR_OFF_ARR_SYMBOL(0, "ENUM_healInsurOffArrSymbol_HEAL_INSUR_OFF_ARR_SYMBOL"),
    EMPEN_ESTAB_REARSIGN(1, "ENUM_emPenEstabRearSign_EMPEN_ESTAB_REARSIGN");

    /** The value. */
    public final int value;

    /** The name id. */
    public final String nameId;
    private BussEsimateClass(int value, String nameId)
    {
        this.value = value;
        this.nameId = nameId;
    }
}
