package nts.uk.ctx.pr.shared.infra.entity.empinsqualifiinfo.empinsofficeinfo;
import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
* 社員雇用保険事業所履歴: 主キー情報
*/
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class QqsmtEmpInsEsmHistPk implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
    * 会社ID
    */
    @Basic(optional = false)
    @Column(name = "CID")
    public String cid;
    
    /**
    * 社員ID
    */
    @Basic(optional = false)
    @Column(name = "SID")
    public String sid;
    
    /**
    * 履歴ID
    */
    @Basic(optional = false)
    @Column(name = "HIST_ID")
    public String histId;

    /**
     * 労働保険事業所コード
     */
    @Basic(optional = false)
    @Column(name = "LABOR_INS_CD")
    public String laborInsCd;
    
}
