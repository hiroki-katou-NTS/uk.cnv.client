package nts.uk.ctx.at.request.dom.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.ReflectedState;
import nts.uk.ctx.at.request.dom.application.common.adapter.scherec.convert.ConvertApplicationToShare;
import nts.uk.ctx.at.request.dom.applicationreflect.service.getapp.GetApplicationForReflect;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationShare;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

/**
 * @author thanh_nx
 *
 *
 *         予定or実績への全ての申請反映結果を取得
 */
public class ObtainAllAppReflecResultInScheduleRecord {

	public static Optional<IntegrationOfDaily> getData(Require require, String cid, String sid, GeneralDate baseDate,
			String appId) {

		Optional<IntegrationOfDaily> domainDaily = Optional.empty();
		// 日別勤怠(Work)を取得する
		domainDaily = require.findDailyRecord(sid, baseDate);
		if (!domainDaily.isPresent()) {
			// 勤務予定を取得する
			domainDaily = require.findSchedule(sid, baseDate);
		}
		if (!domainDaily.isPresent())
			return Optional.empty();

		// 申請を取得する
		List<Application> lstApp = getApp(require, cid, sid, baseDate, appId);
		for (Application app : lstApp) {
			// 申請反映結果を取得
			domainDaily = require.getAppReflectResult(cid, ConvertApplicationToShare.toAppliction(app, baseDate),
					baseDate, domainDaily);
		}

		return domainDaily;

	}

	// 申請を取得する
	private static List<Application> getApp(Require require, String cid, String sid, GeneralDate baseDate,
			String appId) {
		List<Integer> lstApptype = Arrays.asList(ApplicationType.values()).stream().map(x -> x.value)
				.collect(Collectors.toList());

		List<Integer> lstRecordStatus = new ArrayList<>();
		List<Integer> lstScheStatus = new ArrayList<>();
		// 反映状態="未反映"or"反映待ち"待ちの申請を取得
		List<Integer> stateQuery = Arrays.asList(ReflectedState.NOTREFLECTED.value, ReflectedState.WAITREFLECTION.value,
				ReflectedState.REFLECTED.value, ReflectedState.REMAND.value);
		lstRecordStatus.addAll(stateQuery);
		lstScheStatus.addAll(stateQuery);

		// 申請日でソートする（条件：入力日 ASC）
		List<Application> lstApp = require.getAppForReflect(sid, baseDate, lstRecordStatus, lstScheStatus, lstApptype);
		Optional<Application> processingApp = lstApp.stream().filter(x -> x.getAppID().equals(appId)).findFirst();
		if (!processingApp.isPresent())
			return new ArrayList<>();

		// 申請日でソートする（条件：入力日 ASC）
		List<Application> lstResult = new ArrayList<>();
		lstApp.stream().filter(x -> x.getInputDate().before(processingApp.get().getInputDate())).forEach(c -> {
			Application app = GetApplicationForReflect.getAppData(require, cid, c.getAppType(), c.getAppID(), c);
			if (app != null)
				lstResult.add(app);
		});

		return lstResult;
	}

	public static interface Require extends GetApplicationForReflect.Require {

		// 日別勤怠(Work)を取得する
		public Optional<IntegrationOfDaily> findDailyRecord(String sid, GeneralDate date);

		// 勤務予定を取得する
		public Optional<IntegrationOfDaily> findSchedule(String sid, GeneralDate date);

		// 申請反映結果を取得
		// GetApplicationReflectionResultAdapter.getApp
		public Optional<IntegrationOfDaily> getAppReflectResult(String cid, ApplicationShare application,
				GeneralDate baseDate, Optional<IntegrationOfDaily> dailyData);

		// 申請を取得する
		// ApplicationRepository.getAppForReflect
		public List<Application> getAppForReflect(String sid, GeneralDate dateData, List<Integer> recordStatus,
				List<Integer> scheStatus, List<Integer> appType);
	}
}
