package nts.uk.ctx.at.record.dom.monthlyprocess.aggr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import nts.arc.diagnose.stopwatch.concurrent.ConcurrentStopwatches;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.monthly.holidaymanagement.publicholiday.GetAggregationPeriodForPublicHoliday;
import nts.uk.ctx.at.record.dom.monthlyprocess.vacation.CreateDailyInterimRemainMngs;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.GetAnnAndRsvRemNumWithinPeriod;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.param.AggrResultOfAnnAndRsvLeave;
import nts.uk.ctx.at.record.dom.remainingnumber.childcarenurse.care.GetRemainingNumberCareService;
import nts.uk.ctx.at.record.dom.remainingnumber.childcarenurse.childcare.AggrResultOfChildCareNurse;
import nts.uk.ctx.at.record.dom.remainingnumber.childcarenurse.childcare.ChildCareNurseRequireImplFactory;
import nts.uk.ctx.at.record.dom.remainingnumber.childcarenurse.childcare.GetRemainingNumberChildCareService;
import nts.uk.ctx.at.record.dom.remainingnumber.specialleave.empinfo.grantremainingdata.ComplileInPeriodOfSpecialLeaveParam;
import nts.uk.ctx.at.record.dom.remainingnumber.specialleave.empinfo.grantremainingdata.InPeriodOfSpecialLeaveResultInfor;
import nts.uk.ctx.at.record.dom.remainingnumber.specialleave.export.SpecialLeaveManagementService;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.shared.dom.common.days.MonthlyDays;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.export.query.publicholiday.GetRemainingNumberPublicHolidayService;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.export.query.publicholiday.param.AggrResultOfPublicHoliday;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.interimdata.TempPublicHolidayManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.NumberCompensatoryLeavePeriodQuery;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.param.AbsRecMngInPeriodRefactParamInput;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimAbsMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimRecMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.DailyInterimRemainMngData;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainOffPeriodCreateData;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.GetDaysForCalcAttdRate;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.daynumber.AnnualLeaveUsedDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.UsedMinutes;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.InterimRemainMngMode;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.param.CalYearOffWorkAttendRate;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.interim.TempAnnualLeaveMngs;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.DayOffDayAndTimes;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.DayOffDayTimeUnUse;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.DayOffDayTimeUse;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.DayOffRemainCarryForward;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.DayOffRemainDayAndTimes;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.MonthlyDayoffRemainData;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.NumberRemainVacationLeaveRangeQuery;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.BreakDayOffRemainMngRefactParam;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimBreakMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimDayOffMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUndigestDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUndigestTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.MonthVacationGrantDay;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.MonthVacationGrantTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.InterimRemain;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.CreateAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.RemainType;
import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.care.interimdata.TempCareManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.childcare.interimdata.TempChildCareManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.empinfo.grantremainingdata.daynumber.ReserveLeaveRemainingDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.interim.TmpResereLeaveMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialholidaymng.interim.InterimSpecialHolidayMng;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.AggregateMonthlyRecordValue;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.MonthlyAggregationRemainingNumber;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.export.pererror.CreatePerErrorsFromLeaveErrors;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonAggrCompanySettings;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonAggrEmployeeSettings;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonthlyCalculatingDailys;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.breakinfo.FixedManagementDataMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.EmployeeMonthlyPerError;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.ClosureStatus;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.absenceleave.AbsenceLeaveRemainData;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.absenceleave.AttendanceDaysMonthToTal;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.absenceleave.RemainDataDaysMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.AnnLeaRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.AnnualLeaveAttdRateDays;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.AnnualLeaveUndigestedNumber;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.UndigestedAnnualLeaveDays;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.UndigestedTimeAnnualLeaveTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.care.CareRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.childcare.ChildcareRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.publicholiday.PublicHolidayRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.reserveleave.ReserveLeaveGrant;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.reserveleave.RsvLeaRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.specialholiday.SpecialHolidayRemainData;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHoliday;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

/**
 * 処理：ドメインサービス：月別実績を集計する
 * 
 * @author masaaki_jinno
 *
 */
@Getter
@Stateless
public class MonthlyAggregationRemainingNumberImpl implements MonthlyAggregationRemainingNumber {

	/** 会社ID */
	@Setter
	private String companyId;
	/** 社員ID */
	@Setter
	private String employeeId;
	/** 年月 */
	private YearMonth yearMonth;
	/** 締めID */
	private ClosureId closureId;
	/** 締め日 */
	private ClosureDate closureDate;

	/** 月別集計で必要な会社別設定 */
	@Setter
	private MonAggrCompanySettings companySets;
	/** 月別集計で必要な社員別設定 */
	private MonAggrEmployeeSettings employeeSets;

	/** 集計結果 */
	private AggregateMonthlyRecordValue aggregateResult;

	@Setter
	private MonthlyCalculatingDailys monthlyCalculatingDailys;

	/** 暫定残数データ上書きフラグ */
	private boolean isOverWriteRemain;

	@Inject
	private RecordDomRequireService requireService;

	public AggregateMonthlyRecordValue aggregation(CacheCarrier cacheCarrier, DatePeriod period, String companyId,
			String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate,
			MonAggrCompanySettings companySets, MonAggrEmployeeSettings employeeSets,
			MonthlyCalculatingDailys monthlyCalculatingDailys, InterimRemainMngMode interimRemainMngMode,
			boolean isCalcAttendanceRate) {

		val require = requireService.createRequire();

		this.companyId = companyId;
		this.employeeId = employeeId;
		this.yearMonth = yearMonth;
		this.closureId = closureId;
		this.closureDate = closureDate;
		this.companySets = companySets;
		this.employeeSets = employeeSets;
		this.monthlyCalculatingDailys = monthlyCalculatingDailys;

		this.aggregateResult = new AggregateMonthlyRecordValue();

		ConcurrentStopwatches.start("12405:暫定データ作成：");

		// Workを考慮した月次処理用の暫定残数管理データを作成する
		List<DailyInterimRemainMngData> interimDatas = this.createDailyInterimRemainMngs(require, cacheCarrier, period);

		ConcurrentStopwatches.stop("12405:暫定データ作成：");
		ConcurrentStopwatches.start("12410:年休積休：");

		// 年休、積休
		this.annualAndReserveLeaveRemain(require, cacheCarrier, period, interimRemainMngMode, isCalcAttendanceRate,
				interimDatas);

		ConcurrentStopwatches.stop("12410:年休積休：");
		ConcurrentStopwatches.start("12420:振休：");

		// 振休
		this.absenceLeaveRemain(require, cacheCarrier, period, interimRemainMngMode, interimDatas);

		ConcurrentStopwatches.stop("12420:振休：");
		ConcurrentStopwatches.start("12430:代休：");

		// 代休
		this.dayoffRemain(require, cacheCarrier, period, interimRemainMngMode, interimDatas);

		ConcurrentStopwatches.stop("12430:代休：");
		ConcurrentStopwatches.start("12440:特別休暇：");

		// 特別休暇
		this.specialLeaveRemain(cacheCarrier, period, interimRemainMngMode, interimDatas);

		ConcurrentStopwatches.stop("12440:特別休暇：");
		ConcurrentStopwatches.start("12450:子の看護：");

		// 子の看護
		this.childCareRemain(cacheCarrier, period, interimRemainMngMode, interimDatas);

		ConcurrentStopwatches.stop("12450:子の看護：");
		ConcurrentStopwatches.start("12460:介護：");

		// 介護
		this.careRemain(cacheCarrier, period, interimRemainMngMode, interimDatas);

		ConcurrentStopwatches.stop("12460:介護：");
		ConcurrentStopwatches.start("12470:公休：");

		// 公休
		Optional<DatePeriod> aggregationPeriodForPublicHoliday = GetAggregationPeriodForPublicHoliday
				.getAggregationPeriodForPublicHoliday(require, cacheCarrier, companyId, employeeId, yearMonth, period);

		// Workを考慮した月次処理用の暫定残数管理データを作成する
		List<DailyInterimRemainMngData> interimDatasForPublicHoliday = new ArrayList<>();
		if (aggregationPeriodForPublicHoliday.isPresent())
			interimDatasForPublicHoliday = this.createDailyInterimRemainMngs(require, cacheCarrier,
					aggregationPeriodForPublicHoliday.get());

		this.publicHolidayRemain(require, cacheCarrier, period, aggregationPeriodForPublicHoliday, interimRemainMngMode,
				interimDatasForPublicHoliday);

		ConcurrentStopwatches.stop("12470:公休：");

		return this.aggregateResult;
	}

	/**
	 * 年休、積休
	 *
	 * @param period
	 *            期間
	 * @param interimRemainMngMode
	 *            暫定残数データ管理モード
	 * @param isCalcAttendanceRate
	 *            出勤率計算フラグ
	 */
	public void annualAndReserveLeaveRemain(GetDaysForCalcAttdRate.RequireM2 require, CacheCarrier cacheCarrier, 
			DatePeriod period, InterimRemainMngMode interimRemainMngMode, boolean isCalcAttendanceRate,List<DailyInterimRemainMngData> interimDatas) {

		// 暫定残数データを年休・積立年休に絞り込む
		List<TempAnnualLeaveMngs> tmpAnnualLeaveMngs = new ArrayList<>();
		List<TmpResereLeaveMng> tmpReserveLeaveMngs = new ArrayList<>();
		for (val dailyInterimRemainMng : interimDatas) {
			if (dailyInterimRemainMng.getRecAbsData().size() <= 0)
				continue;

			// 年休
			dailyInterimRemainMng.getAnnualHolidayData().forEach(c -> tmpAnnualLeaveMngs.add(c));
			// 積立年休
			if (dailyInterimRemainMng.getResereData().isPresent()) {
				tmpReserveLeaveMngs.add(dailyInterimRemainMng.getResereData().get());
			}
		}

		// 月別実績の計算結果が存在するかチェック
		boolean isOverWriteAnnual = this.isOverWriteRemain;
		// if (this.aggregateResult.getAttendanceTime().isPresent()) {
		//
		// // 年休控除日数分の年休暫定残数データを作成する
		// val compensFlexWorkOpt =
		// CreateInterimAnnualMngData.ofCompensFlexToWork(
		// this.aggregateResult.getAttendanceTime().get(), period.end());
		// if (compensFlexWorkOpt.isPresent()) {
		// tmpAnnualLeaveMngs.add(compensFlexWorkOpt.get());
		// isOverWriteAnnual = true;
		// }
		// }

		// 「モード」をチェック
		CalYearOffWorkAttendRate daysForCalcAttdRate = new CalYearOffWorkAttendRate();
		if (interimRemainMngMode == InterimRemainMngMode.MONTHLY) {

			// 日別実績から出勤率計算用日数を取得 （月別集計用）
			daysForCalcAttdRate = GetDaysForCalcAttdRate.algorithm(require, this.companyId, this.employeeId, period,
					this.companySets, this.monthlyCalculatingDailys);
		}

		// 期間中の年休積休残数を取得
		AggrResultOfAnnAndRsvLeave aggrResult = GetAnnAndRsvRemNumWithinPeriod.algorithm(requireService.createRequire(),
				cacheCarrier, this.companyId, this.employeeId, period, interimRemainMngMode,
				// period.end(), true, isCalcAttendanceRate,
				period.end(), false, isCalcAttendanceRate, Optional.of(isOverWriteAnnual),
				Optional.of(tmpAnnualLeaveMngs), Optional.of(tmpReserveLeaveMngs), Optional.of(false),
				// あとで前回集計結果も渡せるようにする
				// Optional.of(this.employeeSets.isNoCheckStartDate()),
				// this.prevAggrResult.getAnnualLeave(),
				Optional.of(this.employeeSets.isNoCheckStartDate()), Optional.empty(),
				// あとで前回集計結果も渡せるようにする
				// this.prevAggrResult.getReserveLeave(),
				// Optional.of(this.companySets),
				// Optional.of(this.employeeSets),
				Optional.empty(), Optional.of(this.companySets), Optional.of(this.employeeSets),
				Optional.of(this.monthlyCalculatingDailys), Optional.of(period));

		// 2回目の取得以降は、締め開始日を確認させる
		this.employeeSets.setNoCheckStartDate(false);

		if (aggrResult.getAnnualLeave().isPresent()) {
			val asOfPeriodEnd = aggrResult.getAnnualLeave().get().getAsOfPeriodEnd();
			val asOfStartNextDayOfPeriodEnd = aggrResult.getAnnualLeave().get().getAsOfStartNextDayOfPeriodEnd();
			val remainingNumber = asOfPeriodEnd.getRemainingNumber();

			// 未消化数
			AnnualLeaveUndigestedNumber undigestedNumber = new AnnualLeaveUndigestedNumber();
			if (asOfStartNextDayOfPeriodEnd.getRemainingNumber().getAnnualLeaveUndigestNumber().isPresent()) {

				LeaveUndigestDayNumber days = asOfStartNextDayOfPeriodEnd.getRemainingNumber()
						.getAnnualLeaveUndigestNumber().get().getDays();
				Optional<LeaveUndigestTime> minutes = asOfStartNextDayOfPeriodEnd.getRemainingNumber()
						.getAnnualLeaveUndigestNumber().get().getMinutes();

				undigestedNumber = AnnualLeaveUndigestedNumber.of(
						UndigestedAnnualLeaveDays.of(new AnnualLeaveUsedDayNumber(days.v())),
						Optional.of(UndigestedTimeAnnualLeaveTime.of(new UsedMinutes(minutes.get().v()))));
			}

			// 年休月別残数データを更新
			AnnLeaRemNumEachMonth annLeaRemNum = AnnLeaRemNumEachMonth.of(this.employeeId, this.yearMonth,
					this.closureId, this.closureDate, period, ClosureStatus.UNTREATED,
					remainingNumber.getAnnualLeaveNoMinus(), remainingNumber.getAnnualLeaveWithMinus(),
					remainingNumber.getHalfDayAnnualLeaveNoMinus(), remainingNumber.getHalfDayAnnualLeaveWithMinus(),
					asOfStartNextDayOfPeriodEnd.getGrantInfo(), remainingNumber.getTimeAnnualLeaveNoMinus(),
					remainingNumber.getTimeAnnualLeaveWithMinus(),
					AnnualLeaveAttdRateDays.of(new MonthlyDays(daysForCalcAttdRate.getWorkingDays()),
							new MonthlyDays(daysForCalcAttdRate.getPrescribedDays()),
							new MonthlyDays(daysForCalcAttdRate.getDeductedDays())),
					asOfStartNextDayOfPeriodEnd.getRemainingNumber().getAnnualLeaveWithMinus().getRemainingNumberInfo()
							.getRemainingNumberAfterGrantOpt().isPresent(),
					undigestedNumber);
			this.aggregateResult.getAnnLeaRemNumEachMonthList().add(annLeaRemNum);

			// 年休エラーから月別残数エラー一覧を作成する
			this.aggregateResult.getPerErrors()
					.addAll(CreatePerErrorsFromLeaveErrors.fromAnnualLeave(this.employeeId, this.yearMonth,
							this.closureId, this.closureDate,
							aggrResult.getAnnualLeave().get().getAnnualLeaveErrors()));
		}

		if (aggrResult.getReserveLeave().isPresent()) {
			val asOfPeriodEnd = aggrResult.getReserveLeave().get().getAsOfPeriodEnd();
			val asOfStartNextDayOfPeriodEnd = aggrResult.getReserveLeave().get().getAsOfStartNextDayOfPeriodEnd();
			val remainingNumber = asOfPeriodEnd.getRemainingNumber();

			// 未消化数
			// ReserveLeaveUndigestedNumber undigestedNumber= new
			// ReserveLeaveUndigestedNumber();

			ReserveLeaveRemainingDayNumber undigested = asOfStartNextDayOfPeriodEnd.getRemainingNumber()
					.getReserveLeaveUndigestedNumber().getUndigestedDays();

			// Optional<LeaveUndigestTime> minutes =
			// asOfStartNextDayOfPeriodEnd.getRemainingNumber().getAnnualLeaveUndigestNumber().get().getMinutes();

			// undigestedNumber=ReserveLeaveUndigestedNumber.of(
			// new ReserveLeaveRemainingDayNumber(days.v())
			// );

			// 積立年休月別残数データを更新
			ReserveLeaveGrant reserveLeaveGrant = null;
			if (asOfStartNextDayOfPeriodEnd.getGrantInfo().isPresent()) {
				reserveLeaveGrant = ReserveLeaveGrant
						.of(asOfStartNextDayOfPeriodEnd.getGrantInfo().get().getGrantDays());
			}
			RsvLeaRemNumEachMonth rsvLeaRemNum = RsvLeaRemNumEachMonth
					.of(this.employeeId, this.yearMonth, this.closureId, this.closureDate, period,
							ClosureStatus.UNTREATED, remainingNumber.getReserveLeaveNoMinus(),
							remainingNumber.getReserveLeaveWithMinus(), Optional.ofNullable(reserveLeaveGrant),
							asOfStartNextDayOfPeriodEnd.getRemainingNumber().getReserveLeaveWithMinus()
									.getRemainingNumberInfo().getRemainingNumberAfterGrantOpt().isPresent(),
							undigested.v());

			this.aggregateResult.getRsvLeaRemNumEachMonthList().add(rsvLeaRemNum);

			// 積立年休エラーから月別残数エラー一覧を作成する
			this.aggregateResult.getPerErrors()
					.addAll(CreatePerErrorsFromLeaveErrors.fromReserveLeave(this.employeeId, this.yearMonth,
							this.closureId, this.closureDate,
							aggrResult.getReserveLeave().get().getReserveLeaveErrors()));
		}

		// 集計結果を前回集計結果に引き継ぐ
		// this.aggregateResult.setAggrResultOfAnnAndRsvLeave(aggrResult);
	}

	/**
	 * 振休
	 *
	 * @param period
	 *            期間
	 * @param interimRemainMngMode
	 *            暫定残数データ管理モード
	 */
	public void absenceLeaveRemain(NumberCompensatoryLeavePeriodQuery.Require require, CacheCarrier cacheCarrier, 
			DatePeriod period, InterimRemainMngMode interimRemainMngMode,List<DailyInterimRemainMngData> interimDatas) {

		// 暫定残数データを振休・振出に絞り込む
		List<InterimRemain> interimMng = new ArrayList<>();
		List<InterimAbsMng> useAbsMng = new ArrayList<>();
		List<InterimRecMng> useRecMng = new ArrayList<>();
		for (val dailyInterimRemainMng : interimDatas) {
			if (dailyInterimRemainMng.getRecAbsData().size() <= 0)
				continue;
			interimMng.addAll(dailyInterimRemainMng.getRecAbsData().stream()
					.filter(x -> x.getRemainType() == RemainType.PAUSE || x.getRemainType() == RemainType.PICKINGUP)
					.collect(Collectors.toList()));

			// 振休
			if (dailyInterimRemainMng.getInterimAbsData().isPresent()) {
				useAbsMng.add(dailyInterimRemainMng.getInterimAbsData().get());
			}

			// 振出
			if (dailyInterimRemainMng.getRecData().isPresent()) {
				useRecMng.add(dailyInterimRemainMng.getRecData().get());
			}
		}

		// 期間内の振休振出残数を取得する
		val inputParam = new AbsRecMngInPeriodRefactParamInput(this.companyId, this.employeeId, period, period.end(),
				(interimRemainMngMode == InterimRemainMngMode.MONTHLY), this.isOverWriteRemain, useAbsMng, interimMng,
				useRecMng, Optional.empty(), Optional.empty(), Optional.of(period), new FixedManagementDataMonth());
		val aggrResult = NumberCompensatoryLeavePeriodQuery.process(require, inputParam);
		if (aggrResult != null) {

			// 振休月別残数データを更新
			AbsenceLeaveRemainData absLeaRemNum = new AbsenceLeaveRemainData(this.employeeId, this.yearMonth,
					this.closureId.value, this.closureDate.getClosureDay().v(), this.closureDate.getLastDayOfMonth(),
					ClosureStatus.UNTREATED, period.start(), period.end(),
					new RemainDataDaysMonth(aggrResult.getOccurrenceDay().v()),
					new RemainDataDaysMonth(aggrResult.getDayUse().v()),
					new AttendanceDaysMonthToTal(aggrResult.getRemainDay().v()),
					new AttendanceDaysMonthToTal(aggrResult.getCarryoverDay().v()),
					new RemainDataDaysMonth(aggrResult.getUnusedDay().v()));
			this.aggregateResult.getAbsenceLeaveRemainList().add(absLeaRemNum);

			// 振休エラーから月別残数エラー一覧を作成する
			this.aggregateResult.getPerErrors().addAll(CreatePerErrorsFromLeaveErrors.fromPause(this.employeeId,
					this.yearMonth, this.closureId, this.closureDate, aggrResult.getPError()));

			// 集計結果を前回集計結果に引き継ぐ
			// this.aggregateResult.setAbsRecRemainMngOfInPeriodOpt(Optional.of(aggrResult));
		}
	}

	/**
	 * 代休
	 *
	 * @param period
	 *            期間
	 * @param interimRemainMngMode
	 *            暫定残数データ管理モード
	 */
	public void dayoffRemain(NumberRemainVacationLeaveRangeQuery.Require require, CacheCarrier cacheCarrier, 
			DatePeriod period, InterimRemainMngMode interimRemainMngMode, List<DailyInterimRemainMngData> interimDatas) {

		// 暫定残数データを休出・代休に絞り込む
		List<InterimRemain> interimMng = new ArrayList<>();
		List<InterimBreakMng> breakMng = new ArrayList<>();
		List<InterimDayOffMng> dayOffMng = new ArrayList<>();
		for (val dailyInterimRemainMng : interimDatas) {
			if (dailyInterimRemainMng.getRecAbsData().size() <= 0)
				continue;
			interimMng.addAll(dailyInterimRemainMng.getRecAbsData().stream()
					.filter(x -> x.getRemainType() == RemainType.BREAK || x.getRemainType() == RemainType.SUBHOLIDAY)
					.collect(Collectors.toList()));

			// 休出
			if (dailyInterimRemainMng.getBreakData().isPresent()) {
				breakMng.add(dailyInterimRemainMng.getBreakData().get());
			}
			// 代休
			dailyInterimRemainMng.getDayOffData().forEach(c -> dayOffMng.add(c));
		}

		// 期間内の休出代休残数を取得する

		BreakDayOffRemainMngRefactParam inputRefactor = new BreakDayOffRemainMngRefactParam(this.companyId,
				this.employeeId, period, (interimRemainMngMode == InterimRemainMngMode.MONTHLY), period.end(),
				this.isOverWriteRemain, interimMng, Optional.empty(), Optional.of(period), Optional.empty(),
				new FixedManagementDataMonth());
		// 代休残数 ← 残日数 （アルゴリズム「期間内の代休残数を取得する」のoutput）
		val aggrResult = NumberRemainVacationLeaveRangeQuery.getBreakDayOffMngInPeriod(require, inputRefactor);
		if (aggrResult != null) {

			// 代休月別残数データを更新
			MonthlyDayoffRemainData monDayRemNum = new MonthlyDayoffRemainData(this.employeeId, this.yearMonth,
					this.closureId.value, this.closureDate.getClosureDay().v(), this.closureDate.getLastDayOfMonth(),
					ClosureStatus.UNTREATED, period.start(), period.end(),
					aggrResult.getOocr(),
					aggrResult.getUse(),
					aggrResult.getRemain(),
					aggrResult.getCarryForward(),
					aggrResult.getUnUsed());
			this.aggregateResult.getMonthlyDayoffRemainList().add(monDayRemNum);

			// 代休エラーから月別残数エラー一覧を作成する
			this.aggregateResult.getPerErrors().addAll(CreatePerErrorsFromLeaveErrors.fromDayOff(this.employeeId,
					this.yearMonth, this.closureId, this.closureDate, aggrResult.getDayOffErrors()));

			// 集計結果を前回集計結果に引き継ぐ
			// this.aggregateResult.setBreakDayOffRemainMngOfInPeriodOpt(Optional.of(aggrResult));
		}
	}

	/**
	 * 特別休暇
	 *
	 * @param period
	 *            期間
	 * @param interimRemainMngMode
	 *            暫定残数データ管理モード
	 */
	// public void specialLeaveRemain(RequireM8 require, CacheCarrier
	// cacheCarrier, DatePeriod period,
	public void specialLeaveRemain(CacheCarrier cacheCarrier, DatePeriod period,
			InterimRemainMngMode interimRemainMngMode, List<DailyInterimRemainMngData> interimDatas) {

		val require = requireService.createRequire();

		// 暫定残数データを特別休暇に絞り込む
		List<InterimRemain> interimMng = new ArrayList<>();
		List<InterimSpecialHolidayMng> interimSpecialData = new ArrayList<>();
		for (val dailyInterimRemainMng : interimDatas) {
			if (dailyInterimRemainMng.getRecAbsData().size() <= 0)
				continue;
			if (dailyInterimRemainMng.getSpecialHolidayData().size() <= 0)
				continue;
			interimMng.addAll(dailyInterimRemainMng.getRecAbsData());
			interimSpecialData.addAll(dailyInterimRemainMng.getSpecialHolidayData());
		}

		// 「特別休暇」を取得する
		List<SpecialHoliday> specialHolidays = require.specialHoliday(this.companyId);

		for (SpecialHoliday specialHoliday : specialHolidays) {
			Integer specialLeaveCode = specialHoliday.getSpecialHolidayCode().v();

			// 前回集計結果を確認する
			// Optional<InPeriodOfSpecialLeaveResultInfor>
			// prevSpecialLeaveResult = Optional.empty();
			// if (this.prevSpecialLeaveResultMap.containsKey(specialLeaveCode))
			// {
			// prevSpecialLeaveResult =
			// Optional.of(this.prevSpecialLeaveResultMap.get(specialLeaveCode));
			// }

			// マイナスなしを含めた期間内の特別休暇残を集計する
			// 期間内の特別休暇残を集計する
			ComplileInPeriodOfSpecialLeaveParam param = new ComplileInPeriodOfSpecialLeaveParam(this.companyId,
					this.employeeId, period,
					// (interimRemainMngMode == InterimRemainMngMode.MONTHLY),
					// period.end(), specialLeaveCode, true,
					(interimRemainMngMode == InterimRemainMngMode.MONTHLY), period.end(), specialLeaveCode, false,
					this.isOverWriteRemain, interimSpecialData, Optional.of(period));

			// 特別休暇暫定データに、親ドメインの情報を更新する。
			// ※暫定データの作成処理がまだ対応中のため、親ドメインと子ドメインが別々になっているので。 }

			// 残数処理
			InPeriodOfSpecialLeaveResultInfor aggrResult = SpecialLeaveManagementService
					.complileInPeriodOfSpecialLeave(require, cacheCarrier, param);

			// 特別休暇月別残数データを更新
			SpecialHolidayRemainData speLeaRemNum = aggrResult.createSpecialHolidayRemainData(this.employeeId,
					this.yearMonth, this.closureId, this.closureDate, period, specialLeaveCode, aggrResult);

			this.aggregateResult.getSpecialLeaveRemainList().add(speLeaRemNum);

			// 特別休暇エラーから月別残数エラー一覧を作成する
			this.aggregateResult.getPerErrors()
					.addAll(CreatePerErrorsFromLeaveErrors.fromSpecialLeave(this.employeeId, this.yearMonth,
							this.closureId, this.closureDate, specialLeaveCode, aggrResult.getSpecialLeaveErrors()));

			// 集計結果を前回集計結果に引き継ぐ
			// this.aggregateResult.getInPeriodOfSpecialLeaveResultInforMap().put(specialLeaveCode,
			// aggrResult);
		}
	}

	@Inject
	private ChildCareNurseRequireImplFactory childCareNurseRequireImplFactory;

	// 子の看護
	public void childCareRemain(CacheCarrier cacheCarrier, DatePeriod period, InterimRemainMngMode interimRemainMngMode,
			List<DailyInterimRemainMngData> interimDatas) {

		List<TempChildCareManagement> overWriteList = new ArrayList<>();
		for (val dailyInterimRemainMng : interimDatas) {
			dailyInterimRemainMng.getChildCareData().forEach(c -> overWriteList.add(c));
		}

		val require = childCareNurseRequireImplFactory.createRequireImpl();

		// 残数処理
		AggrResultOfChildCareNurse result = GetRemainingNumberChildCareService.getChildCareRemNumWithinPeriod(companyId,
				employeeId, period, interimRemainMngMode, period.end(), Optional.of(true), overWriteList,
				Optional.empty(), Optional.of(CreateAtr.RECORD), Optional.of(period), cacheCarrier, require);

		// 月別残数データを更新
		ChildcareRemNumEachMonth childcareLeaRemNum = result.createChildCareRemainData(this.employeeId, this.yearMonth,
				this.closureId, this.closureDate, period);

		this.aggregateResult.getChildHdRemainList().add(childcareLeaRemNum);

		// 月別残数エラー一覧を作成する
		this.aggregateResult.getPerErrors().addAll(CreatePerErrorsFromLeaveErrors.fromChildCareLeave(this.employeeId,
				this.yearMonth, this.closureId, this.closureDate, result.getChildCareNurseErrors()));

		// 集計結果を前回集計結果に引き継ぐ
	}

	// 介護
	public void careRemain(CacheCarrier cacheCarrier, DatePeriod period, InterimRemainMngMode interimRemainMngMode,
			List<DailyInterimRemainMngData> interimDatas) {

		List<TempCareManagement> overWriteList = new ArrayList<>();
		for (val dailyInterimRemainMng : interimDatas) {
			dailyInterimRemainMng.getCareData().forEach(c -> overWriteList.add(c));
		}

		val require = childCareNurseRequireImplFactory.createRequireImpl();

		// 残数処理
		AggrResultOfChildCareNurse result = GetRemainingNumberCareService.getCareRemNumWithinPeriod(companyId,
				employeeId, period, interimRemainMngMode, period.end(), Optional.of(true), overWriteList,
				Optional.empty(), Optional.of(CreateAtr.RECORD), Optional.of(period), cacheCarrier, require);

		// 月別残数データを更新
		CareRemNumEachMonth careLeaRemNum = result.createCareRemainData(this.employeeId, this.yearMonth, this.closureId,
				this.closureDate, period);

		this.aggregateResult.getCareHdRemainList().add(careLeaRemNum);

		// 月別残数エラー一覧を作成する
		this.aggregateResult.getPerErrors().addAll(CreatePerErrorsFromLeaveErrors.fromCareLeave(this.employeeId,
				this.yearMonth, this.closureId, this.closureDate, result.getChildCareNurseErrors()));

		// 集計結果を前回集計結果に引き継ぐ
	}
	
	//公休
	public void publicHolidayRemain(GetRemainingNumberPublicHolidayService.RequireM1 require, CacheCarrier cacheCarrier, DatePeriod period,
			Optional<DatePeriod> periodForOverWrite, InterimRemainMngMode interimRemainMngMode,
			List<DailyInterimRemainMngData> interimDatas) {
		
		//暫定残数データを公休に絞り込む
		List<TempPublicHolidayManagement> overWriteList = new ArrayList<>();
		for (val dailyInterimRemainMng : interimDatas) {
			dailyInterimRemainMng.getPublicHolidayData().forEach(c -> overWriteList.add(c));
		}

		AggrResultOfPublicHoliday result = GetRemainingNumberPublicHolidayService.getPublicHolidayRemNumWithinPeriod(
				companyId, employeeId, Arrays.asList(yearMonth), period.end(), interimRemainMngMode, Optional.of(true),
				overWriteList, Optional.of(CreateAtr.RECORD), periodForOverWrite, cacheCarrier, require);

		// 月別残数データを作成
		PublicHolidayRemNumEachMonth publicLeaRemNum = result.createPublicHolidayRemainData(this.employeeId,
				this.yearMonth, this.closureId, this.closureDate);
		// 月別残数データを更新
		this.aggregateResult.getPublicRemainList().add(publicLeaRemNum);

		// 月別残数エラー一覧を作成する
		Optional<EmployeeMonthlyPerError> error = CreatePerErrorsFromLeaveErrors.fromPublicLeave(this.employeeId,
				this.yearMonth, this.closureId, this.closureDate,
				result.publicHolidayInformation.stream().filter(x -> x.getYearMonth().equals(yearMonth)).findFirst()
						.flatMap(x -> x.getPublicHolidayErrors()));
		if (error.isPresent()) {
			this.aggregateResult.getPerErrors().add(error.get());
		}
	}

	/**
	 * Workを考慮した月次処理用の暫定残数管理データを作成する
	 *
	 * @param period
	 *            期間
	 */
	private List<DailyInterimRemainMngData> createDailyInterimRemainMngs(CreateDailyInterimRemainMngs.Require require, CacheCarrier cacheCarrier, DatePeriod period) {
		// 【参考：旧処理】 月次処理用の暫定残数管理データを作成する
		// this.dailyInterimRemainMngs =
		// this.interimRemOffMonth.monthInterimRemainData(
		// this.companyId, this.employeeId, period);

		this.isOverWriteRemain = true;

		/** Workを考慮した月次処理用の暫定残数管理データを作成する */
		return CreateDailyInterimRemainMngs.createDailyInterimRemainMngs(require, cacheCarrier, this.companyId,
				this.employeeId, period, this.monthlyCalculatingDailys.getDailyWorks(this.employeeId),
				this.companySets.getAbsSettingOpt(), this.companySets.getDayOffSetting(),
				this.aggregateResult.getAttendanceTime());

	}

	public List<DailyInterimRemainMngData> createDailyInterimRemainMngs(CacheCarrier cacheCarrier, String companyId,
			String employeeId, DatePeriod period, MonAggrCompanySettings comSetting, MonthlyCalculatingDailys dailys) {

		val require = requireService.createRequire();

		MonthlyAggregationRemainingNumberImpl proc = new MonthlyAggregationRemainingNumberImpl();

		proc.setCompanyId(companyId);
		proc.setEmployeeId(employeeId);
		proc.setCompanySets(comSetting);
		proc.setMonthlyCalculatingDailys(dailys);
		// Tạm fix bug NullPointerException (a Tín sửa)
		proc.aggregateResult = new AggregateMonthlyRecordValue();
		proc.aggregateResult.setAttendanceTime(Optional.empty());
		return proc.createDailyInterimRemainMngs(require, cacheCarrier, period);
	}

}
