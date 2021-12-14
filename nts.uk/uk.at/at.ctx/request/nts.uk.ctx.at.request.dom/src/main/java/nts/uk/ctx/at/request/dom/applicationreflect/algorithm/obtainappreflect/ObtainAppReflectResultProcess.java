package nts.uk.ctx.at.request.dom.applicationreflect.algorithm.obtainappreflect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
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
 *         日別実績への申請反映結果を取得
 */
public class ObtainAppReflectResultProcess {

	public static Optional<IntegrationOfDaily> process(Require require, String cid, String employeeId,
			GeneralDate baseDate, Optional<Application> application) {

		// 該当日の申請を取得する
		// [条件]//dieu kien
		// 申請者=input.社員ID
		// 申請開始日 <= input.年月日 <= 申請終了日
		// 反映状態="未反映"or"反映待ち"
		List<Application> lstApp = getListApp(require, cid, employeeId, baseDate);
		application.ifPresent(x -> {
			// input.申請をlistの最後に追加
			lstApp.add(x);
		});

		Optional<IntegrationOfDaily> optDaily = Optional.empty();
		for (Application app : lstApp) {
			/// 申請反映結果を取得
			Optional<IntegrationOfDaily> dailyResult = require
					.getAppReflectResult(cid, ConvertApplicationToShare.toAppliction(app, baseDate), baseDate, optDaily);
			if (dailyResult.isPresent()) {
				optDaily = dailyResult;
			}

		}
		// 日別実績を返す
		return optDaily;

	}

	// ドメインモデル「申請」を取得する// Get domain 「申請」
	private static List<Application> getListApp(Require require, String cid, String employeeId, GeneralDate baseDate) {
		List<Integer> lstApptype = Arrays.asList(ApplicationType.values()).stream().map(x -> x.value).collect(Collectors.toList());

		List<Integer> lstRecordStatus = new ArrayList<>();
		List<Integer> lstScheStatus = new ArrayList<>();
		// 反映状態="未反映"or"反映待ち"待ちの申請を取得
		lstRecordStatus.addAll(Arrays.asList(ReflectedState.NOTREFLECTED.value, ReflectedState.WAITREFLECTION.value));
		lstScheStatus.addAll(Arrays.asList(ReflectedState.NOTREFLECTED.value, ReflectedState.WAITREFLECTION.value));

		List<Application> lstApp = require.getAppForReflect(employeeId, baseDate, lstRecordStatus, lstScheStatus,
				lstApptype);

		lstApp = lstApp.stream().filter(distinctByKey(x -> x.getAppID())).collect(Collectors.toList());
		// 申請日でソートする（条件：入力日 ASC）
		// 申請を取得 (lấy đơn)
		List<Application> lstResult = new ArrayList<>();
		lstApp.forEach(c -> {
			Application app = GetApplicationForReflect.getAppData(require, cid, c.getAppType(), c.getAppID(), c);
			if (app != null)
				lstResult.add(app);
		});

		return lstResult;
	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		final Set<Object> seen = new HashSet<>();
		return t -> seen.add(keyExtractor.apply(t));
	}

	public static interface Require extends GetApplicationForReflect.Require {

		// ApplicationRepository.getAppForReflect
		public List<Application> getAppForReflect(String sid, GeneralDate dateData, List<Integer> recordStatus,
				List<Integer> scheStatus, List<Integer> appType);

		// GetApplicationReflectionResultAdapter.getApp
		public Optional<IntegrationOfDaily> getAppReflectResult(String cid, ApplicationShare application, GeneralDate baseDate,
				Optional<IntegrationOfDaily> dailyData);

	}

}
