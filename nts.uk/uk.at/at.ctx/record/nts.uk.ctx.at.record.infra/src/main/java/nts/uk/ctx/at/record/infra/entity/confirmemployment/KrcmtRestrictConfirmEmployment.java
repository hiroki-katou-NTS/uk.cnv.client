package nts.uk.ctx.at.record.infra.entity.confirmemployment;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.confirmemployment.RestrictConfirmEmployment;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
* 就業確定の機能制限
*/
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KRCMT_WORK_FIXED_CTR")
public class KrcmtRestrictConfirmEmployment extends ContractUkJpaEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
    * ID
    */
    @EmbeddedId
    public KrcmtRestrictConfirmEmploymentPk restrictConfirmEmploymentPk;
    
	/**
	 * 就業確定を行う
	 */
	 @Column(name = "USAGE_ATR")
	 public boolean usageAtr;

	@Override
	protected Object getKey() {
		return restrictConfirmEmploymentPk;
	}
	
	public RestrictConfirmEmployment toDomain() {
        return new RestrictConfirmEmployment(this.restrictConfirmEmploymentPk.cid, this.usageAtr);
    }
	
    public static KrcmtRestrictConfirmEmployment toEntity(RestrictConfirmEmployment domain) {
        return new KrcmtRestrictConfirmEmployment(
        		new KrcmtRestrictConfirmEmploymentPk(domain.getCompanyID()), domain.isConfirmEmployment()
        		);
    }
}
