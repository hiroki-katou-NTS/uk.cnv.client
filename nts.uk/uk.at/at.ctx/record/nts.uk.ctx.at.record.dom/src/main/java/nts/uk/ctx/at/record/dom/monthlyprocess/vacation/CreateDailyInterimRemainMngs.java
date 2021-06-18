package nts.uk.ctx.at.record.dom.monthlyprocess.vacation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.DailyInterimRemainMngData;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.DailyResult;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainCreateDataInputPara;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainOffPeriodCreateData;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.RecordRemainCreateInfor;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainOffPeriodCreateData.RequireM4;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.CreateInterimAnnualMngData;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.CompanyHolidayMngSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacation;
import nts.uk.shr.com.context.AppContexts;

/** Workを考慮した月次処理用の暫定残数管理データを作成する */
public class CreateDailyInterimRemainMngs {

	/**
	 * Workを考慮した月次処理用の暫定残数管理データを作成する
	 */
	public static List<DailyInterimRemainMngData> createDailyInterimRemainMngs(
			InterimRemainOffPeriodCreateData.RequireM4 require, CacheCarrier cacheCarrier,
			String cid, String sid, DatePeriod period, List<IntegrationOfDaily> dailyRecord, Optional<ComSubstVacation> absSettingOpt,
			CompensatoryLeaveComSetting dayOffSetting, Optional<AttendanceTimeOfMonthly> monthlyAttendanceTime) {

		/** 日別勤怠（Work）から暫定管理データを作成*/
		val dailyRemains = createDailyRemains( require, cacheCarrier, cid, sid, period, dailyRecord, absSettingOpt, dayOffSetting);

		/** ○月別実績(Work)から年休フレックス補填分の暫定年休管理データを作成する */
		return createRemainFromMonthly(dailyRemains, monthlyAttendanceTime, period);
	}

	private static List<DailyInterimRemainMngData> createRemainFromMonthly(List<DailyInterimRemainMngData> daily,
			Optional<AttendanceTimeOfMonthly> monthlyAttendanceTime, DatePeriod period) {

		/** ○パラメータ「月別実績(Work)」が存在するかチェック */
		if (monthlyAttendanceTime.isPresent()) {

			/** 大塚モードかを確認する */
			if (!AppContexts.optionLicense().customize().ootsuka())
				return daily;

			/** ○月別実績(Work)から年休フレックス補填分の暫定年休管理データを作成する */
			CreateInterimAnnualMngData.ofCompensFlex(monthlyAttendanceTime.get(), period.end())
										.ifPresent(c -> daily.add(c));
		}

		return daily;
	}

	private static List<DailyInterimRemainMngData> createDailyRemains(InterimRemainOffPeriodCreateData.RequireM4 require, CacheCarrier cacheCarrier, String cid,
			String sid, DatePeriod period, List<IntegrationOfDaily> dailyRecord, Optional<ComSubstVacation> absSettingOpt,
			CompensatoryLeaveComSetting dayOffSetting) {

		val workInfoMap = dailyRecord.stream().collect(Collectors.toMap(c -> c.getYmd(), c -> c.getWorkInformation()));

		/** ○パラメータ「日別実績(Work)」が存在するかチェック */
		if (workInfoMap.isEmpty()) {

			return new ArrayList<>();
		}

		/** 残数作成元情報(実績)を作成する */
		List<DailyResult> dailys = dailyRecord.stream()
				.map(c-> DailyResult.builder()
						.ymd(c.getYmd())
						.workInfo(c.getWorkInformation())
						.attendanceTime(c.getAttendanceTimeOfDailyPerformance())
						.build())
				.collect(java.util.stream.Collectors.toList());
		List<RecordRemainCreateInfor> recordRemains = require.lstResultFromRecord(sid, dailys);


		InterimRemainCreateDataInputPara inputPara = new InterimRemainCreateDataInputPara(cid,
				sid, period, recordRemains, Collections.emptyList(), Collections.emptyList(), false);
		CompanyHolidayMngSetting comHolidaySetting = new CompanyHolidayMngSetting(cid, absSettingOpt, dayOffSetting);

		/** 指定期間の暫定残数管理データを作成する */
		val dailyRemains = InterimRemainOffPeriodCreateData.createInterimRemainDataMng(require, cacheCarrier, inputPara, comHolidaySetting);

		return dailyRemains.entrySet().stream().map(c -> new DailyInterimRemainMngData(c.getKey(), c.getValue().getInterimAbsData(),
																c.getValue().getRecAbsData(), c.getValue().getRecData(), c.getValue().getDayOffData(), c.getValue().getAnnualHolidayData(),
																c.getValue().getResereData(), c.getValue().getBreakData(), c.getValue().getSpecialHolidayData(),
																c.getValue().getChildCareData(),c.getValue().getCareData()))
							.collect(Collectors.toList());
	}

}
