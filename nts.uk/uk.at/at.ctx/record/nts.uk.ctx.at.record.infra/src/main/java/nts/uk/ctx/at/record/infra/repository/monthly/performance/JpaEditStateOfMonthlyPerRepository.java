package nts.uk.ctx.at.record.infra.repository.monthly.performance;

import java.util.List;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.uk.ctx.at.record.infra.entity.monthly.performance.KrcdtEditStateOfMothlyPer;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.editstate.EditStateOfMonthlyPerRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.editstate.EditStateOfMonthlyPerformance;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

/**
 * リポジトリ実装：月別実績の編集状態
 * @author shuichu_ishida
 */
@Stateless
public class JpaEditStateOfMonthlyPerRepository extends JpaRepository implements EditStateOfMonthlyPerRepository{

	private static final String FIND_BY_CLOSURE = "SELECT a FROM KrcdtEditStateOfMothlyPer a "
			+ "WHERE a.krcdtEditStateOfMothlyPerPK.employeeID = :employeeId "
			+ "AND a.krcdtEditStateOfMothlyPerPK.processDate = :yearMonth "
			+ "AND a.krcdtEditStateOfMothlyPerPK.closureID = :closureId "
			+ "AND a.krcdtEditStateOfMothlyPerPK.closeDay = :closureDay "
			+ "AND a.krcdtEditStateOfMothlyPerPK.isLastDay = :isLastDay ";

	private static final String DELETE_BY_CLOSURE = "DELETE FROM KrcdtEditStateOfMothlyPer a "
			+ "WHERE a.krcdtEditStateOfMothlyPerPK.employeeID = :employeeId "
			+ "AND a.krcdtEditStateOfMothlyPerPK.processDate = :yearMonth "
			+ "AND a.krcdtEditStateOfMothlyPerPK.closureID = :closureId "
			+ "AND a.krcdtEditStateOfMothlyPerPK.closeDay = :closureDay "
			+ "AND a.krcdtEditStateOfMothlyPerPK.isLastDay = :isLastDay ";
	private static final String FIND_BY_SIDS = "SELECT a FROM KrcdtEditStateOfMothlyPer a "
			+ "WHERE a.krcdtEditStateOfMothlyPerPK.employeeID IN :lstSid "
			+ "AND a.krcdtEditStateOfMothlyPerPK.processDate >= :startDate "
			+ "AND a.krcdtEditStateOfMothlyPerPK.processDate <= :endDate";
	
	/** 検索　（締め） */
	@Override
	public List<EditStateOfMonthlyPerformance> findByClosure(
			String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate) {
		
		return this.queryProxy().query(FIND_BY_CLOSURE, KrcdtEditStateOfMothlyPer.class)
				.setParameter("employeeId", employeeId)
				.setParameter("yearMonth", yearMonth.v())
				.setParameter("closureId", closureId.value)
				.setParameter("closureDay", closureDate.getClosureDay().v())
				.setParameter("isLastDay", (closureDate.getLastDayOfMonth() ? 1 : 0))
				.getList(c -> c.toDomain());
	}
	
	/** 削除 */
	@Override
	public void remove(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate) {
		
		this.getEntityManager().createQuery(DELETE_BY_CLOSURE)
				.setParameter("employeeId", employeeId)
				.setParameter("yearMonth", yearMonth.v())
				.setParameter("closureId", closureId.value)
				.setParameter("closureDay", closureDate.getClosureDay().v())
				.setParameter("isLastDay", (closureDate.getLastDayOfMonth() ? 1 : 0))
				.executeUpdate();
	}

	@Override
	public List<EditStateOfMonthlyPerformance> findBySidsAndYM(List<String> lstSid, YearMonthPeriod mPeriod) {
		return this.queryProxy().query(FIND_BY_SIDS, KrcdtEditStateOfMothlyPer.class)
				.setParameter("lstSid", lstSid)
				.setParameter("startDate", mPeriod.start())
				.setParameter("endDate", mPeriod.end())
				.getList(c -> c.toDomain());
	}
}
