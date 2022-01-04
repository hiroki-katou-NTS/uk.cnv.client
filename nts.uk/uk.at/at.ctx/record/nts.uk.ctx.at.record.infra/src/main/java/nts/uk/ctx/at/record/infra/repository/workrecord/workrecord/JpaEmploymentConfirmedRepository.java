package nts.uk.ctx.at.record.infra.repository.workrecord.workrecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.record.dom.workrecord.workrecord.EmploymentConfirmed;
import nts.uk.ctx.at.record.dom.workrecord.workrecord.EmploymentConfirmedRepository;
import nts.uk.ctx.at.record.infra.entity.workrecord.workrecord.KrcdtWorkFixed;
import nts.uk.ctx.at.record.infra.entity.workrecord.workrecord.KrcdtWorkFixedPk;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.common.WorkplaceId;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;

/**
 * 就業確定Repository	
 * @author chungnt
 *
 */
@Stateless
public class JpaEmploymentConfirmedRepository extends JpaRepository implements EmploymentConfirmedRepository {

	private static final String SELECT_LIST_WORK_FIXED = "SELECT r FROM KrcdtWorkFixed r WHERE r.pk.companyId = :companyId "
			+ "and r.pk.workplaceId in :workplaceId " 
			+ "and r.pk.closureId = :closureId " 
			+ "and r.pk.processYM = :processYM ";
	
	private static final String SELECT_LIST_WORK_FIXED_BY_COMPANY = "SELECT r FROM KrcdtWorkFixed r WHERE r.pk.companyId = :companyId "
			+ "and r.pk.workplaceId in :workplaceId " 
			+ "and r.pk.closureId = :closureId " 
			+ "and r.pk.processYM = :processYM ";
	
	@Override
	public void insert(EmploymentConfirmed domain) {
		this.commandProxy().insert(toEntity(domain));
	}

	@Override
	public void delete(EmploymentConfirmed domain) {
		this.commandProxy().remove(
				KrcdtWorkFixed.class, 
				new KrcdtWorkFixedPk(
					domain.getCompanyId().v(), 
					domain.getWorkplaceId().v(), 
					domain.getClosureId().value, 
					domain.getProcessYM().v())
		);
	}

	@Override
	public Optional<EmploymentConfirmed> get(String companyId, String workplaceId, ClosureId closureId, YearMonth processYM) {
		KrcdtWorkFixed entity = this.getEntityManager().find(KrcdtWorkFixed.class, new KrcdtWorkFixedPk(companyId, workplaceId, closureId.value, processYM.v()));
		if (entity == null) {
			return Optional.empty();
		}
		return Optional.of(toDomain(entity));
	}

	@Override
	public List<EmploymentConfirmed> get(String companyId, List<String> workplaceId, ClosureId closureId,
			YearMonth processYM) {
		if(workplaceId.isEmpty()) {
			return new ArrayList<>();
		}
		return this.queryProxy().query(SELECT_LIST_WORK_FIXED, KrcdtWorkFixed.class)
				.setParameter("companyId", companyId)
				.setParameter("workplaceId", workplaceId)
				.setParameter("closureId", closureId.value)
				.setParameter("processYM", processYM.v()).getList(x ->toDomain(x));
	}
	
	public KrcdtWorkFixed toEntity(EmploymentConfirmed domain) {
		return new KrcdtWorkFixed(
				new KrcdtWorkFixedPk(
						domain.getCompanyId().v(), 
						domain.getWorkplaceId().v(), 
						domain.getClosureId().value, 
						domain.getProcessYM().v()), 
				domain.getEmployeeId(), 
				domain.getDate());
	}
	
	public EmploymentConfirmed toDomain(KrcdtWorkFixed entity) {
		
		EmploymentConfirmed domain = new EmploymentConfirmed(new CompanyId(entity.pk.companyId),
				new WorkplaceId(entity.pk.workplaceId),
				ClosureId.valueOf(entity.pk.closureId),
				new YearMonth(entity.pk.processYM),
				entity.employeeId,
				entity.confirm_date_time);
		
		return domain;
	}

	@Override
	public List<EmploymentConfirmed> getListByCompany(String companyId) {
		return this.queryProxy().query(SELECT_LIST_WORK_FIXED_BY_COMPANY, KrcdtWorkFixed.class)
				.setParameter("companyId", companyId).getList(x ->toDomain(x));
	}

}
