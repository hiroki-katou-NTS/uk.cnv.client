package nts.uk.ctx.at.record.dom.monthly;

import lombok.Getter;
import lombok.val;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.record.dom.monthly.calc.MonthlyCalculation;
import nts.uk.ctx.at.record.dom.monthly.verticaltotal.VerticalTotalOfMonthly;
import nts.uk.ctx.at.record.dom.monthlyprocess.aggr.work.RepositoriesRequiredByMonthlyAggr;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingSystem;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureDate;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.time.calendar.period.DatePeriod;

/**
 * 月別実績の勤怠時間
 * @author shuichi_ishida
 */
@Getter
public class AttendanceTimeOfMonthly extends AggregateRoot {

	/** 社員ID */
	private final String employeeId;
	/** 年月 */
	private final YearMonth yearMonth;
	/** 締めID */
	private final ClosureId closureId;
	/** 締め日付 */
	private final ClosureDate closureDate;

	/** 期間 */
	private DatePeriod datePeriod;
	/** 月の計算 */
	private MonthlyCalculation monthlyCalculation;
	/** 縦計 */
	private VerticalTotalOfMonthly verticalTotal;
	/** 集計日数 */
	private AttendanceDaysMonth aggregateDays;
	/** 回数集計 */
	//aggregateTimes
	/** 休暇 */
	//holiday
	/** 時間外超過 */
	//excessOutsideTime
	/** 任意項目 */
	//anyItem

	/**
	 * コンストラクタ
	 * @param employeeId 社員ID
	 * @param yearMonth 年月
	 * @param closureId 締めID
	 * @param closureDate 締め日付
	 * @param datePeriod 期間
	 */
	public AttendanceTimeOfMonthly(String employeeId, YearMonth yearMonth,
			ClosureId closureId, ClosureDate closureDate, DatePeriod datePeriod){
		
		super();
		this.employeeId = employeeId;
		this.yearMonth = yearMonth;
		this.closureId = closureId;
		this.closureDate = closureDate;
		this.datePeriod = datePeriod;
		this.monthlyCalculation = new MonthlyCalculation();
		this.verticalTotal = new VerticalTotalOfMonthly();
		this.aggregateDays = new AttendanceDaysMonth(0.0);
	}
	
	/**
	 * ファクトリー
	 * @param employeeId 社員ID
	 * @param yearMonth 年月
	 * @param closureId 締めID
	 * @param closureDate 締め日付
	 * @param datePeriod 期間
	 * @param monthlyCalculation 月の計算
	 * @param verticalTotal 縦計
	 * @param aggregateDays 集計日数
	 * @return 月別実績の勤怠時間
	 */
	public static AttendanceTimeOfMonthly of(
			String employeeId,
			YearMonth yearMonth,
			ClosureId closureId,
			ClosureDate closureDate,
			DatePeriod datePeriod,
			MonthlyCalculation monthlyCalculation,
			VerticalTotalOfMonthly verticalTotal,
			AttendanceDaysMonth aggregateDays){
		
		val domain = new AttendanceTimeOfMonthly(employeeId, yearMonth, closureId, closureDate, datePeriod);
		domain.monthlyCalculation = monthlyCalculation;
		domain.verticalTotal = verticalTotal;
		domain.aggregateDays = aggregateDays;
		return domain;
	}
	
	/**
	 * 履歴ごとに月別実績を集計する
	 * @param companyId 会社ID
	 * @param workingSystem 労働制
	 * @param isRetireMonth 退職月かどうか
	 * @param repositories 月次集計が必要とするリポジトリ
	 */
	public void aggregate(String companyId, WorkingSystem workingSystem, boolean isRetireMonth,
			RepositoriesRequiredByMonthlyAggr repositories){
		
		this.monthlyCalculation.aggregate(companyId, this.employeeId, this.yearMonth,
				this.closureId, this.closureDate, this.datePeriod, workingSystem, isRetireMonth, repositories);
	}
	
	/**
	 * 縦計
	 * @param companyId 会社ID
	 * @param workingSystem 労働制
	 * @param repositories 月次集計が必要とするリポジトリ
	 */
	public void verticalTotal(String companyId, WorkingSystem workingSystem,
			RepositoriesRequiredByMonthlyAggr repositories){
	
		// 月の縦計
		this.verticalTotal.verticalTotal(companyId, this.employeeId, this.datePeriod,
				workingSystem, repositories);
		
		// 開始週の終了日を計算
		
		// 週の縦計
		
		// 次の週の期間を計算
		
	}
}
