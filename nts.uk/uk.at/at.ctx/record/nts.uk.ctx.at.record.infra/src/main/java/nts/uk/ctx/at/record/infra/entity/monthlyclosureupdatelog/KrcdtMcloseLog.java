package nts.uk.ctx.at.record.infra.entity.monthlyclosureupdatelog;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.record.dom.monthlyclosureupdatelog.MonthlyClosureUpdateLog;
import nts.uk.shr.com.time.calendar.date.ClosureDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 
 * @author HungTT - 月締め更新ログ
 *
 */
@NoArgsConstructor
@Entity
@Table(name = "KRCDT_MCLOSE_LOG")
public class KrcdtMcloseLog extends ContractUkJpaEntity {

	// ID
	@Id
	@Column(name = "ID")
	public String id;

	// 会社ID
	@Column(name = "CID")
	public String companyId;

	// 実行状況
	@Column(name = "EXECUTE_STATUS")
	public int executionStatus;

	// 完了状態
	@Column(name = "COMPLETE_STATUS")
	public int completeStatus;

	// 実行日時
	@Column(name = "EXECUTE_DT")
	public GeneralDateTime executionDateTime;

	// 実行期間
	@Column(name = "EXECUTE_START")
	public GeneralDate executionStart;

	// 実行期間
	@Column(name = "EXECUTE_END")
	public GeneralDate executionEnd;

	// 実行社員ID
	@Column(name = "EXECUTE_SID")
	public String executeEmployeeId;

	// 対象年月
	@Column(name = "TARGET_YM")
	public int targetYearMonth;

	// 締めID
	@Column(name = "CLOSURE_ID")
	public int closureId;

	// 締め日
	@Column(name = "CLOSURE_DAY")
	public int closeDay;

	// 締め日
	@Column(name = "IS_LAST_DAY")
	public int isLastDay;

	@Override
	protected Object getKey() {
		return this.id;
	}

	public KrcdtMcloseLog(String id, String companyId, int executionStatus, int completeStatus,
			GeneralDateTime executionDateTime, GeneralDate executionStart, GeneralDate executionEnd,
			String executeEmployeeId, int targetYearMonth, int closureId, int closeDay, int isLastDay) {
		super();
		this.id = id;
		this.companyId = companyId;
		this.executionStatus = executionStatus;
		this.completeStatus = completeStatus;
		this.executionDateTime = executionDateTime;
		this.executionStart = executionStart;
		this.executionEnd = executionEnd;
		this.executeEmployeeId = executeEmployeeId;
		this.targetYearMonth = targetYearMonth;
		this.closureId = closureId;
		this.closeDay = closeDay;
		this.isLastDay = isLastDay;
	}

	public static KrcdtMcloseLog fromDomain(MonthlyClosureUpdateLog domain) {
		return new KrcdtMcloseLog(domain.getId(), domain.getCompanyId(), domain.getExecutionStatus().value,
				domain.getCompleteStatus().value, domain.getExecutionDateTime(), domain.getExecutionPeriod().start(),
				domain.getExecutionPeriod().end(), domain.getExecuteEmployeeId(), domain.getTargetYearMonth().v(),
				domain.getClosureId().value, domain.getClosureDate().getClosureDay().v(),
				domain.getClosureDate().getLastDayOfMonth() ? 1 : 0);
	}

	public MonthlyClosureUpdateLog toDomain() {
		return new MonthlyClosureUpdateLog(this.id, this.companyId, this.executionStatus, this.completeStatus,
				this.executionDateTime, new DatePeriod(this.executionStart, this.executionEnd), this.executeEmployeeId,
				new YearMonth(this.targetYearMonth), this.closureId,
				new ClosureDate(this.closeDay, this.isLastDay == 1));
	}

}
