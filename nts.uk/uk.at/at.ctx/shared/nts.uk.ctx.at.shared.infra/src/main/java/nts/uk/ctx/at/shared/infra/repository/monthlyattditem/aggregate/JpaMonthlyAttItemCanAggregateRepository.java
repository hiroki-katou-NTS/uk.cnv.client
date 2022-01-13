package nts.uk.ctx.at.shared.infra.repository.monthlyattditem.aggregate;

import java.util.List;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.shared.dom.monthlyattditem.MonthlyAttItemId;
import nts.uk.ctx.at.shared.dom.monthlyattditem.aggregate.MonthlyAttItemCanAggregateRepository;
import nts.uk.ctx.at.shared.infra.entity.monthlyattditem.aggregrate.KrcctMonCalAttenItem;

/**
 * The Class JpaMonthlyAttItemCanAggregateRepository.
 *
 * @author LienPTK
 */
@Stateless
public class JpaMonthlyAttItemCanAggregateRepository extends JpaRepository
		implements MonthlyAttItemCanAggregateRepository {

	/** The Constant SELECT_MONTHLY_ATTENDANCE_CAN_AGGREGATE. */
	private static final String SELECT_MONTHLY_ATTENDANCE_BY_CID = "SELECT att FROM KrcctMonCalAttenItem att "
																 + "WHERE att.id.cid = :cid"
																 + " AND  att.calculable = :calculable";

	@Override
	public List<MonthlyAttItemId> getMonthlyAtdItemCanAggregate(String cid) {
		return this.queryProxy().query(SELECT_MONTHLY_ATTENDANCE_BY_CID, KrcctMonCalAttenItem.class)
				.setParameter("cid", cid)
				.setParameter("calculable", true)
				.getList(t -> new MonthlyAttItemId(t.getAttItemId()));
	}

	@Override
	public List<MonthlyAttItemId> getMonthlyAtdItemCanNotAggregate(String cid) {
		return this.queryProxy().query(SELECT_MONTHLY_ATTENDANCE_BY_CID, KrcctMonCalAttenItem.class)
				.setParameter("cid", cid)
				.setParameter("calculable", false)
				.getList(t -> new MonthlyAttItemId(t.getAttItemId()));
	}
}
