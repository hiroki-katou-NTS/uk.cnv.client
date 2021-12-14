package nts.uk.ctx.at.record.dom.monthlyprocess.vacation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailyattendance.IntegrationOfDailyGetter;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.DailyInterimRemainMngData;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.require.RemainNumberTempRequireService.Require;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.interim.TempAnnualLeaveMngs;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.IntegrationOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.IntegrationOfMonthlyGetter;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.breakinfo.FixedManagementDataMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.breakinfo.FixedRemainDataForMonthlyAgg;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.breakinfo.InterimRemainOffMonthProcess;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveComSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacationRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.time.calendar.date.ClosureDate;;

@Stateless
public class InterimRemainOffMonthProcessImpl implements InterimRemainOffMonthProcess {

	@Inject
	private ComSubstVacationRepository subRepos;
	@Inject
	private CompensLeaveComSetRepository compensLeaveComSetRepo;
	@Inject
	private IntegrationOfDailyGetter integrationOfDailyGetter;
	@Inject
	private IntegrationOfMonthlyGetter integrationOfMonthlyGetter;
	@Inject
	private RecordDomRequireService requireService;

	@Override
	public FixedRemainDataForMonthlyAgg monthInterimRemainData(CacheCarrier cacheCarrier, String cid, String sid,
			DatePeriod period, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate) {

		val require = requireService.createRequire();

		/** 代休設定を取得する */
		val absSettingOpt = subRepos.findById(cid);
		val dayOffSetting = compensLeaveComSetRepo.find(cid);

		/** 日別勤怠(Work)を取得 */
		val dailyRecord = integrationOfDailyGetter.getIntegrationOfDaily(sid, period);

		/** 月別実績(Work)を取得する */
		val monthlyRecord = integrationOfMonthlyGetter.get(sid, yearMonth, closureId, closureDate);

		/** Workを考慮した月次処理用の暫定残数管理データを作成する */
		val mapDataOutput = CreateDailyInterimRemainMngs.createDailyInterimRemainMngs(require, cacheCarrier, cid, sid, period,
				dailyRecord, absSettingOpt, dayOffSetting, monthlyRecord.getAttendanceTime());

		/** 日別実績と月別実績の残数比較 */
		return compareDailyAndMonthly(require, cacheCarrier, cid, sid, period, mapDataOutput, monthlyRecord);
	}

	/** 日別実績と月別実績の残数比較 */
	private FixedRemainDataForMonthlyAgg compareDailyAndMonthly(Require require, CacheCarrier cacheCarrier, String cid,
			String empId, DatePeriod period, List<DailyInterimRemainMngData> daily, IntegrationOfMonthly monthly) {

		/** 年休の比較 */
		//compareAnnualHoliday(require, cacheCarrier, cid, empId, period, getDaily(daily, c -> c.getAnnualHolidayData().orElse(null)), monthly);

		/** 積立年休の比較 */

		/** 特別休暇の比較 */

		/** 休出代休の比較 */

		/** 振出振休の比較 */

		/** 60H超休の比較 */

		/** 公休の比較 */

		/** 子の看護 */

		/** 介護 */

		/** TODO: 実装待ち　*/
		return new FixedRemainDataForMonthlyAgg(daily, new FixedManagementDataMonth(new ArrayList<>(), new ArrayList<>()));
	}

	/** 年休の比較 */
	private void compareAnnualHoliday(Require require, CacheCarrier cacheCarrier, String cid, String empId, DatePeriod period,
			List<TempAnnualLeaveMngs> dailyAnnualHol, IntegrationOfMonthly monthly) {

		/** 期間中の次回年休付与を取得 */
//		val nextAnnual = CalcNextAnnualLeaveGrantDate.algorithm(require, cacheCarrier, cid, empId, Optional.of(period));

//		if (nextAnnual.isEmpty()) {

			/** 期間の年休の比較 */

//		} else {

			/** 付与前期間と付与後期間を作成 */
//		}
	}

	/** 年休の比較処理 */
//	private void internalCompareAnnualHoliday() {

		/** 暫定年休管理データと月別実績の日数を比較 */
//	}

	/** 積立年休の比較 */

	/** 特別休暇の比較 */

	/** 休出代休の比較 */

	/** 振出振休の比較 */

	/** 60H超休の比較 */

	/** 公休の比較 */

	/** 子の看護 */

	/** 介護 */

	private <T> List<T> getDaily(List<DailyInterimRemainMngData> s, Function<DailyInterimRemainMngData, T> mapper) {

		return s.stream().filter(c -> mapper.apply(c) != null)
							.map(c -> mapper.apply(c))
							.collect(Collectors.toList());
	}
}
