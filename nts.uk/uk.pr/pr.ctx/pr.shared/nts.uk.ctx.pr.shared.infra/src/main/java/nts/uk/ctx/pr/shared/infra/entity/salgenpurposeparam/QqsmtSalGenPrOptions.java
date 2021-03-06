package nts.uk.ctx.pr.shared.infra.entity.salgenpurposeparam;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.pr.shared.dom.salgenpurposeparam.SalGenParamOptions;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/**
* 給与汎用パラメータ選択肢
*/
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "QQSMT_SAL_GEN_PR_OPTIONS")
public class QqsmtSalGenPrOptions extends UkJpaEntity implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    /**
    * ID
    */
    @EmbeddedId
    public QqsmtSalGenPrOptionsPk salGenPrOptionsPk;
    
    /**
    * 選択肢名称
    */
    @Basic(optional = false)
    @Column(name = "OPTION_NAME")
    public String optionName;
    
    @Override
    protected Object getKey()
    {
        return salGenPrOptionsPk;
    }

    public SalGenParamOptions toDomain() {
        return new SalGenParamOptions(this.salGenPrOptionsPk.paraNo, this.salGenPrOptionsPk.cid, this.salGenPrOptionsPk.optionNo, this.optionName);
    }
    public static QqsmtSalGenPrOptions toEntity(SalGenParamOptions domain) {
        return new QqsmtSalGenPrOptions(new QqsmtSalGenPrOptionsPk(domain.getParaNo(), domain.getCID(), domain.getOptionNo()),domain.getOptionName());
    }

}
