package nts.uk.ctx.at.record.infra.entity.monthly.erroralarm;

import java.io.Serializable;
//import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.AnnualLeaveError;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.EmployeeMonthlyPerError;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.ErrorType;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.Flex;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.ReserveLeaveError;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.time.calendar.date.ClosureDate;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@NoArgsConstructor
@Entity
@Table(name = "KRCDT_MON_SYA_ERROR")
public class KrcdtEmployeeMonthlyPerError extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	public KrcdtEmployeeMonthlyPerErrorPK krcdtEmployeeMonthlyPerErrorPK;
	/**
	 * フレックス: フレックスエラー
	 */
	@Column(name = "FLEX")
	public Integer flex;
	/**
	 * 年休: 年休エラー
	 */
	@Column(name = "ANNUAL_HOLIDAY")
	public Integer annualHoliday;
	/**
	 * 積立年休: 積立年休エラー
	 */
	@Column(name = "YEARLY_RESERVED")
	public Integer yearlyReserved;

	@Override
	protected Object getKey() {
		return krcdtEmployeeMonthlyPerErrorPK;
	}

	public KrcdtEmployeeMonthlyPerError convertToEntity(EmployeeMonthlyPerError domain, boolean update){
	
		if (!update) {
			this.krcdtEmployeeMonthlyPerErrorPK =  new KrcdtEmployeeMonthlyPerErrorPK(domain.getNo(), domain.getErrorType().value, domain.getYearMonth().v(),
					domain.getEmployeeID(), domain.getClosureId().value, domain.getClosureDate().getClosureDay().v(),
					domain.getClosureDate().getLastDayOfMonth() ? 1 : 0);
		}
		
		this.flex = domain.getFlex().isPresent() ? domain.getFlex().get().value : null;
		this.annualHoliday = domain.getAnnualHoliday().isPresent() ? domain.getAnnualHoliday().get().value : null;
		this.yearlyReserved = domain.getYearlyReserved().isPresent() ? domain.getYearlyReserved().get().value : null;
		
		return this;
	}
	
	public EmployeeMonthlyPerError convertToDomain() {
		val key = this.krcdtEmployeeMonthlyPerErrorPK;
		EmployeeMonthlyPerError domain = new EmployeeMonthlyPerError(key.no, ErrorType.valueOf(key.errorType),
				new YearMonth(key.yearMonth), key.employeeID, ClosureId.valueOf(key.closureId),
				new ClosureDate(key.closeDay, key.isLastDay == 1), flex == null ? null : Flex.valueOf(flex),
				annualHoliday == null ? null : EnumAdaptor.valueOf(annualHoliday, AnnualLeaveError.class),
				yearlyReserved == null ? null : EnumAdaptor.valueOf(yearlyReserved, ReserveLeaveError.class));
		return domain;
	}
}
