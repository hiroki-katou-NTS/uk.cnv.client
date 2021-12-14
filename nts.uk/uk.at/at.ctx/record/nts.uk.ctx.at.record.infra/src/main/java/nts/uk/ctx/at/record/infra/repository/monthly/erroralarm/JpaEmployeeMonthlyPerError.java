package nts.uk.ctx.at.record.infra.repository.monthly.erroralarm;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.record.infra.entity.monthly.erroralarm.KrcdtEmployeeMonthlyPerError;
import nts.uk.ctx.at.record.infra.entity.monthly.erroralarm.KrcdtEmployeeMonthlyPerErrorPK;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.EmployeeMonthlyPerError;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.EmployeeMonthlyPerErrorRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Stateless
public class JpaEmployeeMonthlyPerError extends JpaRepository implements EmployeeMonthlyPerErrorRepository {
	
	private final static String REMOVE_EMP;
	private final static String FIND_ERROR;
	
	static{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("DELETE FROM KrcdtEmployeeMonthlyPerError f ");
		stringBuilder.append("WHERE f.krcdtEmployeeMonthlyPerErrorPK.employeeID = :employeeID ");
		stringBuilder.append("AND f.krcdtEmployeeMonthlyPerErrorPK.yearMonth = :yearMonth ");
		stringBuilder.append("AND f.krcdtEmployeeMonthlyPerErrorPK.closureId = :closureId ");
		stringBuilder.append("AND f.krcdtEmployeeMonthlyPerErrorPK.closeDay = :closeDay ");
		stringBuilder.append("AND f.krcdtEmployeeMonthlyPerErrorPK.isLastDay = :isLastDay ");
		REMOVE_EMP = stringBuilder.toString();
		
		stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT f  FROM KrcdtEmployeeMonthlyPerError f ");
		stringBuilder.append("WHERE f.krcdtEmployeeMonthlyPerErrorPK.employeeID IN :employeeIds ");
		stringBuilder.append("AND f.krcdtEmployeeMonthlyPerErrorPK.yearMonth = :yearMonth ");
		stringBuilder.append("AND f.krcdtEmployeeMonthlyPerErrorPK.closureId = :closureId ");
		stringBuilder.append("AND f.krcdtEmployeeMonthlyPerErrorPK.closeDay = :closeDay ");
		stringBuilder.append("AND f.krcdtEmployeeMonthlyPerErrorPK.isLastDay = :isLastDay ");
		FIND_ERROR = stringBuilder.toString();
				
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void insertAll(EmployeeMonthlyPerError domain) {
	
		// キー
		val key = new KrcdtEmployeeMonthlyPerErrorPK(
				domain.getNo(),
				domain.getErrorType().value,
				domain.getYearMonth().v(),
				domain.getEmployeeID(),
				domain.getClosureId().value,
				domain.getClosureDate().getClosureDay().v(),
				domain.getClosureDate().getLastDayOfMonth() ? 1 : 0);
		
		// 登録・更新
		KrcdtEmployeeMonthlyPerError entity = this.getEntityManager().find(KrcdtEmployeeMonthlyPerError.class, key);
		if (entity == null){
			KrcdtEmployeeMonthlyPerError entity2 = new KrcdtEmployeeMonthlyPerError();
			this.getEntityManager().persist(entity2.convertToEntity(domain, false));
			this.getEntityManager().flush();
		}
		else {
			this.commandProxy().update(entity.convertToEntity(domain, true));
		}
	}

	@Override
	public void removeAll(String employeeID, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate) {
		
		this.getEntityManager().createQuery(REMOVE_EMP).setParameter("employeeID", employeeID)
				.setParameter("yearMonth", yearMonth.v()).setParameter("closureId", closureId.value)
				.setParameter("closeDay", closureDate.getClosureDay().v())
				.setParameter("isLastDay", closureDate.getLastDayOfMonth() ? 1 : 0).executeUpdate();
		
//		this.getEntityManager().flush();
	}

	@Override
	public List<EmployeeMonthlyPerError> findError(List<String> employeeID, YearMonth yearMonth, ClosureId closureId,
			ClosureDate closureDate) {
		return this.getEntityManager().createQuery(FIND_ERROR, KrcdtEmployeeMonthlyPerError.class)
				.setParameter("employeeIds", employeeID).setParameter("yearMonth", yearMonth.v())
				.setParameter("closureId", closureId.value).setParameter("closeDay", closureDate.getClosureDay().v())
				.setParameter("isLastDay", closureDate.getLastDayOfMonth() ? 1 : 0).getResultList().stream()
				.map(x -> x.convertToDomain()).collect(Collectors.toList());
	}

}
