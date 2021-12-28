package nts.uk.ctx.at.shared.infra.repository.worktime.filtercondition;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.shared.dom.worktime.filtercriteria.WorkHoursFilterCondition;
import nts.uk.ctx.at.shared.dom.worktime.filtercriteria.WorkHoursFilterConditionName;
import nts.uk.ctx.at.shared.dom.worktime.filtercriteria.WorkHoursFilterConditionNo;
import nts.uk.ctx.at.shared.dom.worktime.filtercriteria.WorkHoursFilterConditionRepository;
import nts.uk.ctx.at.shared.infra.entity.worktime.filtercondition.KshmtWtSearchCondition;
import nts.uk.ctx.at.shared.infra.entity.worktime.filtercondition.KshmtWtSearchConditionPK;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@Stateless
public class JpaWorkHoursFilterConditionRepository extends JpaRepository implements WorkHoursFilterConditionRepository {
	
	private static final String SELECT_BY_CID = "SELECT t FROM KshmtWtSearchCondition t "
			+ "WHERE t.pk.cid = :cid";

	private WorkHoursFilterCondition toDomain(KshmtWtSearchCondition entity) {
		return new WorkHoursFilterCondition(entity.pk.cid, new WorkHoursFilterConditionNo(entity.pk.no),
				NotUseAtr.valueOf(entity.useAtr),
				Optional.ofNullable(entity.name).map(WorkHoursFilterConditionName::new));
	}

	private KshmtWtSearchCondition toEntity(WorkHoursFilterCondition domain) {
		KshmtWtSearchConditionPK pk = new KshmtWtSearchConditionPK(domain.getCid(), domain.getNo().v());
		return new KshmtWtSearchCondition(pk, domain.getNotUseAtr().isUse(),
				domain.getName().map(WorkHoursFilterConditionName::v).orElse(null));
	}

	@Override
	public void insert(WorkHoursFilterCondition domain) {
		this.commandProxy().insert(this.toEntity(domain));
	}

	@Override
	public void update(WorkHoursFilterCondition domain) {
		this.commandProxy().update(this.toEntity(domain));
	}

	@Override
	public void delete(WorkHoursFilterCondition domain) {
		this.commandProxy().remove(KshmtWtSearchCondition.class, 
				new KshmtWtSearchConditionPK(domain.getCid(), domain.getNo().v()));
	}

	@Override
	public List<WorkHoursFilterCondition> findByCid(String cid) {
		return this.queryProxy().query(SELECT_BY_CID, KshmtWtSearchCondition.class)
				.setParameter("cid", cid).getList(this::toDomain);
	}

}
