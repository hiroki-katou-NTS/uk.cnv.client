package nts.uk.ctx.sys.auth.infra.repository.grant.rolesetjob;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.transaction.Transactional;

import lombok.val;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.sys.auth.dom.grant.rolesetjob.RoleSetGrantedJobTitle;
import nts.uk.ctx.sys.auth.dom.grant.rolesetjob.RoleSetGrantedJobTitleRepository;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSetCode;
import nts.uk.ctx.sys.auth.infra.entity.grant.rolesetjob.SacmtRoleSetGrantedJobTitleDetail;
import nts.uk.ctx.sys.auth.infra.entity.grant.rolesetjob.SacmtRoleSetGrantedJobTitleDetailPK;

/**
 * 
 * @author HungTT
 *
 */

@Stateless
@Transactional
public class JpaRoleSetGrantedJobTitleRepository extends JpaRepository implements RoleSetGrantedJobTitleRepository {

	private static final String FIND_BY_CID_JOBTITLES = "SELECT c FROM SacmtRoleSetGrantedJobTitleDetail c "
			+ " WHERE c.roleSetGrantedJobTitleDetailPK.companyId = :companyId";

	private static final String FIND_BY_CID_JOBTITLES_AND_LISTCD = "SELECT c FROM SacmtRoleSetGrantedJobTitleDetail c "
			+ " WHERE c.roleSetGrantedJobTitleDetailPK.companyId = :companyId"
			+ " AND c.roleSetCd IN :roleCDLst";

	private RoleSetGrantedJobTitle toDomain(SacmtRoleSetGrantedJobTitleDetail entity) {

		return new RoleSetGrantedJobTitle(
				entity.roleSetGrantedJobTitleDetailPK.companyId,
				entity.roleSetGrantedJobTitleDetailPK.jobTitleId,
				new RoleSetCode(entity.roleSetCd)
		);
	}
	private SacmtRoleSetGrantedJobTitleDetail toEntity(RoleSetGrantedJobTitle domain) {
		SacmtRoleSetGrantedJobTitleDetailPK key = new SacmtRoleSetGrantedJobTitleDetailPK(domain.getJobTitleId(), domain.getCompanyId());
		return new SacmtRoleSetGrantedJobTitleDetail(
				domain.getRoleSetCd().v(),
				key.jobTitleId,
				key.companyId
		);
	}

	@Override
	public List<RoleSetGrantedJobTitle> getByCompanyId(String companyId) {
		return this.queryProxy().query(FIND_BY_CID_JOBTITLES, SacmtRoleSetGrantedJobTitleDetail.class)
				.setParameter("companyId", companyId)
				.getList(c -> toDomain(c));
	}
	
	@Override
	public Optional<RoleSetGrantedJobTitle> getByJobTitleId(String companyId, String jobTitleId) {
		SacmtRoleSetGrantedJobTitleDetailPK pk = new SacmtRoleSetGrantedJobTitleDetailPK(jobTitleId, companyId);
		return this.queryProxy().find(pk, SacmtRoleSetGrantedJobTitleDetail.class).map(c -> toDomain(c));
	}

	@Override
	public void insert(RoleSetGrantedJobTitle domain) {
		this.commandProxy().insert(toEntity(domain));
	}

	@Override
	public void update(RoleSetGrantedJobTitle domain) {
		Optional<SacmtRoleSetGrantedJobTitleDetail> upEntity = this.queryProxy().find(
				new SacmtRoleSetGrantedJobTitleDetailPK(domain.getJobTitleId(), domain.getCompanyId()),
				SacmtRoleSetGrantedJobTitleDetail.class);
		if (upEntity.isPresent()) {
			upEntity.get().roleSetCd = domain.getRoleSetCd().v();
			this.commandProxy().update(upEntity.get());
		}
	}

	@Override
	public boolean checkRoleSetCdExist(String companyId, RoleSetCode roleSetCd) {
		List<String> listCd = Collections.singletonList(roleSetCd.v());
		val listItem = this.findJobTitleByRoleCDLst(companyId,listCd);
		return !listItem.isEmpty();
	}

	@Override
	public List<String> findJobTitleByRoleCDLst(String companyID, List<String> roleCDLst){
		if(roleCDLst.isEmpty()){
			return new ArrayList<>();
		}
		List<String> resultList = new ArrayList<>();
		CollectionUtil.split(roleCDLst, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(FIND_BY_CID_JOBTITLES_AND_LISTCD ,SacmtRoleSetGrantedJobTitleDetail.class )
				.setParameter("companyId", companyID)
				.setParameter("roleCDLst", subList)
				.getList( c -> c.roleSetGrantedJobTitleDetailPK.jobTitleId));
		});
		return resultList;
	}

}
