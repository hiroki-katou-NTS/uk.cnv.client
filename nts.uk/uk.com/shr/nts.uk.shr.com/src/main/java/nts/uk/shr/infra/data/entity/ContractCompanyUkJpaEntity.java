package nts.uk.shr.infra.data.entity;

import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

import lombok.Getter;
import lombok.Setter;
import nts.uk.shr.com.context.AppContexts;

@Getter
@Setter
@MappedSuperclass
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class ContractCompanyUkJpaEntity extends UkJpaEntity {
	
	/** 契約コード */
	@Column(name = "CONTRACT_CD")
	public String contractCd;
	
	/** 会社コード */
	@Column(name = "CID")
	public String companyId;
	
	@PrePersist
    private void setInsertingContractInfo() {
		this.contractCd = AppContexts.user().contractCode();
		this.companyId = AppContexts.user().companyId();
	}

}
