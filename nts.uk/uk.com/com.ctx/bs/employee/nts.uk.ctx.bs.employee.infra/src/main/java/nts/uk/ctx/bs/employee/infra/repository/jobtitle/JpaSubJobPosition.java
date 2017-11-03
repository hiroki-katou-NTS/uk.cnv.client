package nts.uk.ctx.bs.employee.infra.repository.jobtitle;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.bs.employee.dom.position.jobposition.SubJobPosRepository;
import nts.uk.ctx.bs.employee.dom.position.jobposition.SubJobPosition;
import nts.uk.ctx.bs.employee.infra.entity.jobtitle.BsymtSubJobPosition;
import nts.uk.ctx.bs.employee.infra.entity.jobtitle.BsymtSubJobPositionPK;

@Stateless
public class JpaSubJobPosition extends JpaRepository implements SubJobPosRepository {

	private static final String SELECT_SUB_JOB_POS_BY_DEPT_ID = "SELECT s FROM BsymtSubJobPosition s"
			+ " WHERE s.affiDeptId = :affiDeptId";

	private static final String SELECT_BY_EID_STD = "select s from BsymtSubJobPosition s"
			+ " where s.bsymtCurrAffiDept.sid = :empId and s.strD <= :std and s.endD >= :std";

	private SubJobPosition toDomainEmployee(BsymtSubJobPosition entity) {
		val domain = SubJobPosition.createFromJavaType(entity.subJobPosId, entity.affiDeptId, entity.jobTitleId,
				entity.strD, entity.endD);
		return domain;
	}

	@Override
	public List<SubJobPosition> getSubJobPosByDeptId(String deptId) {
		return this.queryProxy().query(SELECT_SUB_JOB_POS_BY_DEPT_ID, BsymtSubJobPosition.class)
				.setParameter("affiDeptId", deptId).getList().stream().map(x -> this.toDomainEmployee(x))
				.collect(Collectors.toList());
	}

	@Override
	public Optional<SubJobPosition> getByEmpIdAndStandDate(String employeeId, GeneralDate standandDate) {
		Optional<BsymtSubJobPosition> dataOpt = this.queryProxy().query(SELECT_BY_EID_STD, BsymtSubJobPosition.class)
				.setParameter("empId", employeeId).setParameter("std", standandDate).getSingle();
		if (dataOpt.isPresent()) {
			BsymtSubJobPosition ent = dataOpt.get();
			return Optional.of(SubJobPosition.createFromJavaType(ent.subJobPosId, ent.affiDeptId, ent.jobTitleId,
					ent.strD, ent.endD));
		}
		return Optional.empty();
	}
	/**
	 * Convert from domain to entity
	 * @param domain
	 * @return
	 */
	private BsymtSubJobPosition toEntity(SubJobPosition domain){
		BsymtSubJobPositionPK key = new BsymtSubJobPositionPK(domain.getSubJobPosId());
		return new BsymtSubJobPosition(key, domain.getAffiDeptId(), domain.getJobTitleId(), domain.getStartDate(), domain.getEndDate(), null);
	}
	private void updateEntity(SubJobPosition domain, BsymtSubJobPosition entity){
		entity.setAffiDeptId(domain.getAffiDeptId());
		entity.setJobTitleId(domain.getJobTitleId());
		entity.setStrD(domain.getStartDate());
		entity.setEndD(domain.getEndDate());
	}
	/**
	 * ドメインモデル「職務職位（兼務）」を新規登録する
	 * @param domain
	 */
	@Override
	public void addSubJobPosition(SubJobPosition domain) {
		this.commandProxy().insert(toEntity(domain));
	}
	/**
	 * 取得した「職務職位（兼務）」を更新する
	 * @param domain
	 */
	@Override
	public void updateSubJobPosition(SubJobPosition domain) {
		// Get exist entity
		BsymtSubJobPositionPK key = new BsymtSubJobPositionPK(domain.getSubJobPosId());
		Optional<BsymtSubJobPosition> existItem = this.queryProxy().find(key, BsymtSubJobPosition.class);
		if(!existItem.isPresent()){
			return;
		}
		// Update entity
		updateEntity(domain, existItem.get());
		// Update table
		this.commandProxy().update(existItem.get());
	}

}
