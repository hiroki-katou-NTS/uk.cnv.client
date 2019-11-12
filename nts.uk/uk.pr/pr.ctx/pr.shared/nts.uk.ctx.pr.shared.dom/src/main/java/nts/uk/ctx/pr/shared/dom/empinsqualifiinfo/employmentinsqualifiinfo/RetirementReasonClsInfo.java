package nts.uk.ctx.pr.shared.dom.empinsqualifiinfo.employmentinsqualifiinfo;

import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.dom.AggregateRoot;

@Getter
@Setter
/**
 * 退職解雇理由区分情報
 */
public class RetirementReasonClsInfo extends AggregateRoot{

    /**
     * 会社ID
     */
    private String CID;

    /**
     * 退職解雇理由区分コード
     */
    private RetirementReasonClsCode retirementReasonClsCode;

    /**
     * 退職解雇理由名称
     */
    private CauseOfLossEmpInsurance retirementReasonClsName;

    public RetirementReasonClsInfo() {};

    public RetirementReasonClsInfo(String CID, String reasonTermination, String retirementReasonClsName){
        this.CID = CID;
        this.retirementReasonClsCode = new RetirementReasonClsCode(reasonTermination);
        this.retirementReasonClsName = new CauseOfLossEmpInsurance(retirementReasonClsName);
    }

}
