package nts.uk.ctx.sys.auth.infra.entity.grant.rolesetjob;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import nts.uk.ctx.sys.auth.dom.grant.rolesetjob.RoleSetGrantedJobTitle;
import nts.uk.ctx.sys.auth.dom.grant.rolesetjob.RoleSetGrantedJobTitleDetail;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 
 * @author HungTT
 *
 */

@NoArgsConstructor
@Entity
@Table(name = "SACMT_ROLESET_JOB")
public class SacmtRoleSetGrantedJobTitle extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "CID")
	public String companyId;

	@Basic(optional = false)
	@Column(name = "APPLY_CONCURRENT_PERSON")
	public boolean applyToConcurrentPerson;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="roleSetGrantedJobTitle", orphanRemoval = true)
	public List<SacmtRoleSetGrantedJobTitleDetail> details;

	@Override
	protected Object getKey() {
		return this.companyId;
	}

	public SacmtRoleSetGrantedJobTitle(String companyId, boolean applyToConcurrentPerson, List<SacmtRoleSetGrantedJobTitleDetail> details) {
		super();
		this.companyId = companyId;
		this.applyToConcurrentPerson = applyToConcurrentPerson;
		this.details = details;
	}
	
	public static RoleSetGrantedJobTitle toDomain(SacmtRoleSetGrantedJobTitle entity) {
		return new RoleSetGrantedJobTitle(entity.companyId, entity.applyToConcurrentPerson, entity.details.stream()
				.map(item -> new RoleSetGrantedJobTitleDetail(item.roleSetCd,
						item.roleSetGrantedJobTitleDetailPK.jobTitleId, item.roleSetGrantedJobTitleDetailPK.companyId))
				.collect(Collectors.toList()));
	}

	public static SacmtRoleSetGrantedJobTitle toEntity(RoleSetGrantedJobTitle domain) {
		return new SacmtRoleSetGrantedJobTitle(domain.getCompanyId(), domain.isApplyToConcurrentPerson(),
				domain.getDetails().stream().map(item -> new SacmtRoleSetGrantedJobTitleDetail(item.getRoleSetCd().v(),
						item.getJobTitleId(), item.getCompanyId())).collect(Collectors.toList()));
	}

}
