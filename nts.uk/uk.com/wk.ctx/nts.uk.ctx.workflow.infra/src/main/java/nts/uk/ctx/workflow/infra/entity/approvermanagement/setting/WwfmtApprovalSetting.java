package nts.uk.ctx.workflow.infra.entity.approvermanagement.setting;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@Entity
@Table(name = "WWFMT_APPROVAL")
@AllArgsConstructor
@NoArgsConstructor
public class WwfmtApprovalSetting extends ContractUkJpaEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 主キー */
	/** 会社ID */
	@Id
	@Column(name = "CID")
	public String companyId;

	/** 本人による承認 */
	@Column(name = "SELF_APPROVAL_ATR")
	public boolean selfApprovalAtr;
	
	// 会社単位
	@Column(name = "CMP_UNIT_SET")
	public boolean cmpUnitSet;
	
	// 職場単位
	@Column(name = "WKP_UNIT_SET")
	public boolean wkpUnitSet;
	
	// 社員単位
	@Column(name = "SYA_UNIT_SET")
	public boolean syaUnitSet;
	
	@Override
	protected Object getKey() {
		return companyId;
	}

}
