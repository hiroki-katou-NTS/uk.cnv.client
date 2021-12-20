package nts.uk.ctx.at.record.infra.entity.workrecord.closurestatus;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.shared.dom.scherec.closurestatus.ClosureStatusManagement;
import nts.uk.shr.com.time.calendar.date.ClosureDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 
 * @author HungTT - 締め状態管理
 *
 */

@NoArgsConstructor
@Entity
@Table(name = "KRCDT_CLOSURE_STS")
public class KrcdtClosureSts extends ContractUkJpaEntity {

	@EmbeddedId
	public KrcdtClosureSttMngPk pk;

	// 実行期間
	@Column(name = "START_DATE")
	public GeneralDate start;

	// 実行期間
	@Column(name = "END_DATE")
	public GeneralDate end;

	// @Column(name = "STATUS")
	// public int status;

	public KrcdtClosureSts(int yearMonth, String employeeId, int closureId, ClosureDate closureDate,
			DatePeriod period) {
		super();
		this.pk = new KrcdtClosureSttMngPk(yearMonth, employeeId, closureId, closureDate.getClosureDay().v(),
				closureDate.getLastDayOfMonth());
		this.start = period.start();
		this.end = period.end();
		// this.status = status;
	}

	public static KrcdtClosureSts fromDomain(ClosureStatusManagement domain) {
		return new KrcdtClosureSts(domain.getYearMonth().v(), domain.getEmployeeId(), domain.getClosureId().value,
				domain.getClosureDate(), domain.getPeriod());
	}

	public ClosureStatusManagement toDomain() {
		return new ClosureStatusManagement(new YearMonth(this.pk.yearMonth), this.pk.employeeId, this.pk.closureId,
				new ClosureDate(this.pk.closeDay, this.pk.isLastDay), new DatePeriod(this.start, this.end));
	}
	
	public static ClosureStatusManagement toDomain(KrcdtClosureSts entity) {
		return entity.toDomain();
	}

	@Override
	protected Object getKey() {
		return this.pk;
	}

}
