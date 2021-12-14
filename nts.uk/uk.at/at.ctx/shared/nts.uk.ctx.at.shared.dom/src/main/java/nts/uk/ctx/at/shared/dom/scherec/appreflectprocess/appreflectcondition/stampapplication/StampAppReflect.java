package nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.stampapplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.at.shared.dom.common.TimeZoneWithWorkNo;
import nts.uk.ctx.at.shared.dom.scherec.application.common.PrePostAtrShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.AppStampShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.StartEndClassificationShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.TimeStampAppEnumShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.TimeStampAppShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.TimeZoneStampClassificationShare;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.DailyRecordOfApplication;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.condition.ReflectStartEndWork;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.stampapplication.algorithm.CancelAppStamp;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.stampapplication.algorithm.CancelTimeZoneApplication;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.stampapplication.algorithm.ReflectAttendanceLeav;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.stampapplication.algorithm.ReflectBreakTime;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.stampapplication.algorithm.ReflectOutingTimeZone;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.stampapplication.algorithm.ReflectShortTime;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.stampapplication.algorithm.ReflectTemporaryAttLeav;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.stampapplication.algorithm.support.CancelSupportStartEnd;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.stampapplication.algorithm.support.ReflectSupportStartEnd;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * refactor 4 refactor4
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.shared(勤務予定、勤務実績).申請反映処理.申請反映条件.打刻申請
 * 打刻申請の反映
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StampAppReflect extends AggregateRoot {
	/**
	 * 会社ID
	 */
	private String companyId;

	/**
	 * 出退勤を反映する
	 */
	private NotUseAtr workReflectAtr;

	/**
	 * 臨時出退勤を反映する
	 */
	private NotUseAtr extraWorkReflectAtr;

	/**
	 * 外出時間帯を反映する
	 */
	private NotUseAtr goOutReflectAtr;

	/**
	 * 育児時間帯を反映する
	 */
	private NotUseAtr childCareReflectAtr;

	/**
	 * 応援開始、終了を反映する
	 */
	private NotUseAtr supportReflectAtr;

	/**
	 * 介護時間帯を反映する
	 */
	private NotUseAtr careReflectAtr;

	/**
	 * 休憩時間帯を反映する
	 */
	private NotUseAtr breakReflectAtr;

	public static StampAppReflect create(String companyId, int workReflectAtr, int extraWorkReflectAtr,
			int goOutReflectAtr, int childCareReflectAtr, int supportReflectAtr, int careReflectAtr,
			int breakReflectAtr) {
		return new StampAppReflect(companyId, EnumAdaptor.valueOf(workReflectAtr, NotUseAtr.class),
				EnumAdaptor.valueOf(extraWorkReflectAtr, NotUseAtr.class),
				EnumAdaptor.valueOf(goOutReflectAtr, NotUseAtr.class),
				EnumAdaptor.valueOf(childCareReflectAtr, NotUseAtr.class),
				EnumAdaptor.valueOf(supportReflectAtr, NotUseAtr.class),
				EnumAdaptor.valueOf(careReflectAtr, NotUseAtr.class),
				EnumAdaptor.valueOf(breakReflectAtr, NotUseAtr.class));
	}

	/**
	 * @author thanh_nx
	 *
	 *         打刻申請を反映する（勤務実績）
	 */
	public Collection<Integer> reflectRecord(Require require, AppStampShare application,
			DailyRecordOfApplication dailyApp) {

		Set<Integer> lstItemId = new HashSet<>();

		// [input. 打刻申請.事前事後区分]をチェック
		if (application.getPrePostAtr() == PrePostAtrShare.POSTERIOR) {
			// 事後
			// 打刻申請の反映
			lstItemId.addAll(reflectStampApp(require, application, dailyApp));
			// 応援の反映
			lstItemId.addAll(reflectSupport(require, application, dailyApp));
		} else {
			// [事前]
			// 始業終業の反映
			Arrays.asList(1, 2).stream().forEach(no -> {
				lstItemId.addAll(reflectStartEndWork(require, dailyApp, PrePostAtrShare.PREDICT,
						application.getListTimeStampApp(), new WorkNo(no)));
			});
		}

		return lstItemId;
	}

	// 始業終業時刻を反映する
	private List<Integer> reflectStartEndWork(Require require, DailyRecordOfApplication dailyApp,
			PrePostAtrShare prePostAtr, List<TimeStampAppShare> listTimeStampApp, WorkNo workNo) {
		// 開始、終了時刻を取得する
		List<Pair<StartEndClassificationShare, Integer>> timeZoneWithWorkNoLst = listTimeStampApp.stream()
				.filter(x -> x.getDestinationTimeApp().getEngraveFrameNo() == workNo.v()
						&& x.getDestinationTimeApp()
								.getTimeStampAppEnum() == TimeStampAppEnumShare.ATTEENDENCE_OR_RETIREMENT
						&& x.getTimeOfDay() != null)
				.map(x -> {
					return Pair.of(x.getDestinationTimeApp().getStartEndClassification(), x.getTimeOfDay().v());
				}).collect(Collectors.toList());

		if (timeZoneWithWorkNoLst.isEmpty())
			return new ArrayList<>();

		Integer startTime = timeZoneWithWorkNoLst.stream().filter(x -> x.getKey() == StartEndClassificationShare.START)
				.map(x -> x.getRight()).findFirst().orElse(null);

		Integer endTime = timeZoneWithWorkNoLst.stream().filter(x -> x.getKey() == StartEndClassificationShare.END)
				.map(x -> x.getRight()).findFirst().orElse(null);

		// 時間帯を作成
		TimeZoneWithWorkNo timzone = new TimeZoneWithWorkNo(workNo.v(), startTime, endTime);

		// 反映する
		return ReflectStartEndWork.reflect(require, companyId, dailyApp, Arrays.asList(timzone), 
				timzone.getTimeZone().getStartTime() != null, timzone.getTimeZone().getEndTime() != null);
	}

	/**
	 * @author thanh_nx
	 *
	 *         打刻申請を反映する（勤務予定）
	 */
	public Collection<Integer> reflectSchedule(Require require, AppStampShare application, DailyRecordOfApplication dailyApp) {
		return reflectStampApp(require, application, dailyApp);
	}

	/**
	 * @author thanh_nx
	 *
	 *         打刻申請の反映
	 */

	public Collection<Integer> reflectStampApp(Require require, AppStampShare application, DailyRecordOfApplication dailyApp) {
		Set<Integer> lstItemId = new HashSet<>();
		// [出退勤を反映する]をチェック

		if (this.getWorkReflectAtr() == NotUseAtr.USE) {
			// 出退勤の反映
			lstItemId.addAll(ReflectAttendanceLeav.reflect(require, companyId, dailyApp,
					application.getListTimeStampApp().stream()
							.filter(x -> x.getDestinationTimeApp()
									.getTimeStampAppEnum() == TimeStampAppEnumShare.ATTEENDENCE_OR_RETIREMENT)
							.collect(Collectors.toList())));

			// 時刻申請の取消
			lstItemId.addAll(CancelAppStamp.process(dailyApp,
					application.getListDestinationTimeApp().stream()
							.filter(x -> x.getTimeStampAppEnum() == TimeStampAppEnumShare.ATTEENDENCE_OR_RETIREMENT)
							.collect(Collectors.toList())));
		}

		// [臨時出退勤を反映する]をチェック

		if (this.getExtraWorkReflectAtr() == NotUseAtr.USE) {
			// 臨時出退勤の反映
			ReflectTemporaryAttLeav.reflect(dailyApp,
					application.getListTimeStampApp().stream().filter(
							x -> x.getDestinationTimeApp().getTimeStampAppEnum() == TimeStampAppEnumShare.EXTRAORDINARY)
							.collect(Collectors.toList()));

			// 時刻申請の取消
			lstItemId.addAll(CancelAppStamp.process(dailyApp,
					application.getListDestinationTimeApp().stream()
							.filter(x -> x.getTimeStampAppEnum() == TimeStampAppEnumShare.EXTRAORDINARY)
							.collect(Collectors.toList())));

		}

		// [育児時間帯を反映する]をチェック

		if (this.getChildCareReflectAtr() == NotUseAtr.USE) {
			// 短時間勤務時間帯の反映
			lstItemId
					.addAll(ReflectShortTime.reflect(dailyApp,
							application.getListTimeStampAppOther().stream().filter(x -> x.getDestinationTimeZoneApp()
									.getTimeZoneStampClassification() == TimeZoneStampClassificationShare.PARENT)
									.collect(Collectors.toList())));

			// 時間帯申請の取消
			lstItemId.addAll(CancelTimeZoneApplication.process(dailyApp,
					application.getListDestinationTimeZoneApp().stream()
							.filter(x -> x.getTimeZoneStampClassification() == TimeZoneStampClassificationShare.PARENT)
							.collect(Collectors.toList())));
		}

		// [介護時間帯を反映する]をチェック

		if (this.getCareReflectAtr() == NotUseAtr.USE) {
			// 短時間勤務時間帯の反映
			lstItemId.addAll(ReflectShortTime.reflect(dailyApp,
					application.getListTimeStampAppOther().stream()
							.filter(x -> x.getDestinationTimeZoneApp()
									.getTimeZoneStampClassification() == TimeZoneStampClassificationShare.NURSE)
							.collect(Collectors.toList())));

			// 時間帯申請の取消
			lstItemId.addAll(CancelTimeZoneApplication.process(dailyApp,
					application.getListDestinationTimeZoneApp().stream()
							.filter(x -> x.getTimeZoneStampClassification() == TimeZoneStampClassificationShare.NURSE)
							.collect(Collectors.toList())));
		}

		// [外出時間帯を反映する]をチェック

		if (this.getGoOutReflectAtr() == NotUseAtr.USE) {
			// 外出時間帯の反映
			lstItemId.addAll(ReflectOutingTimeZone.process(dailyApp, application.getListTimeStampApp().stream().filter(
					x -> x.getDestinationTimeApp().getTimeStampAppEnum() == TimeStampAppEnumShare.GOOUT_RETURNING)
					.collect(Collectors.toList())));

			// 時刻申請の取消
			lstItemId.addAll(CancelAppStamp.process(dailyApp,
					application.getListDestinationTimeApp().stream()
							.filter(x -> x.getTimeStampAppEnum() == TimeStampAppEnumShare.GOOUT_RETURNING)
							.collect(Collectors.toList())));
		}

		// [休憩時間帯を反映する]をチェック

		if (this.getBreakReflectAtr() == NotUseAtr.USE) {
			// 休憩時間帯の反映
			lstItemId.addAll(ReflectBreakTime.reflect(dailyApp,
					application.getListTimeStampAppOther().stream()
							.filter(x -> x.getDestinationTimeZoneApp()
									.getTimeZoneStampClassification() == TimeZoneStampClassificationShare.BREAK)
							.collect(Collectors.toList())));

			// 時間帯申請の取消
			lstItemId.addAll(CancelTimeZoneApplication.process(dailyApp,
					application.getListDestinationTimeZoneApp().stream()
							.filter(x -> x.getTimeZoneStampClassification() == TimeZoneStampClassificationShare.BREAK)
							.collect(Collectors.toList())));
		}
		return lstItemId;
	}

	/**
	 * @author thanh_nx
	 *
	 *         応援の反映
	 */

	public List<Integer> reflectSupport(Require require, AppStampShare application, DailyRecordOfApplication dailyApp) {

		List<Integer> lstItemId = new ArrayList<Integer>();
		// [応援開始・終了を反映する]をチェック

		if (this.getSupportReflectAtr() == NotUseAtr.NOT_USE) {
			return lstItemId;
		}
		// 応援開始・終了の反映
		lstItemId.addAll(ReflectSupportStartEnd.reflect(require, dailyApp,
				application.getListTimeStampApp().stream()
						.filter(x -> x.getDestinationTimeApp().getTimeStampAppEnum() == TimeStampAppEnumShare.CHEERING)
						.collect(Collectors.toList())));

		// 応援開始・終了打刻の取消
		lstItemId.addAll(CancelSupportStartEnd.process(dailyApp, application.getListDestinationTimeApp().stream()
				.filter(x -> x.getTimeStampAppEnum() == TimeStampAppEnumShare.CHEERING).collect(Collectors.toList())));

		return lstItemId;
	}

	public static interface Require extends ReflectSupportStartEnd.Require, ReflectStartEndWork.Require, ReflectAttendanceLeav.Require {

	}	

}
