package nts.uk.ctx.at.request.dom.applicationreflect.algorithm.reflectprocess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.arc.enums.EnumAdaptor;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTime;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeAppAtr;
import nts.uk.ctx.at.request.dom.reasonappdaily.ApplicationReasonInfo;
import nts.uk.ctx.at.request.dom.reasonappdaily.ApplicationTypeReason;
import nts.uk.ctx.at.request.dom.reasonappdaily.ReasonApplicationDailyResult;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;

/**
 * @author thanh_nx
 *
 *         申請理由の反映
 */
public class ReflectApplicationReason {

	private static List<ItemContent> ITEMCONTENT;
	static {
		ITEMCONTENT = new ArrayList<>();
		// Map domain ReasonApplicationDailyResult to itemId
		ITEMCONTENT.addAll(ItemContent.create(869, 0, 0));
		ITEMCONTENT.addAll(ItemContent.create(870, 0, 1));
		ITEMCONTENT.addAll(ItemContent.create(871, 0, 2));
		ITEMCONTENT.addAll(ItemContent.create(872, 6, null));
		ITEMCONTENT.addAll(ItemContent.create(873, 1, null));
		ITEMCONTENT.addAll(ItemContent.create(874, 2, null));
		ITEMCONTENT.addAll(ItemContent.create(875, 3, null));
		ITEMCONTENT.addAll(ItemContent.create(876, 4, null));
		ITEMCONTENT.addAll(ItemContent.create(877, 7, null));
		ITEMCONTENT.addAll(ItemContent.create(878, 8, null));
		ITEMCONTENT.addAll(ItemContent.create(879, 9, null));
		ITEMCONTENT.addAll(ItemContent.create(880, 10, null));
		ITEMCONTENT.addAll(ItemContent.create(881, 15, null));
	}

	public static Optional<AtomTask> reflectReason(Require require, Application application, GeneralDate dateRefer, GeneralDateTime reflectTime) {

		// 申請があるかのチェック
		if (!application.getOpAppReason().isPresent() && !application.getOpAppStandardReasonCD().isPresent()) {
			return Optional.empty();
		}

		// 日別実績の申請理由を取得
		List<ReasonApplicationDailyResult> lstReasonDai = require.findReasonAppDaily(application.getEmployeeID(),
				dateRefer, application.getPrePostAtr(), application.getAppType(),
				application.getAppType() == ApplicationType.OVER_TIME_APPLICATION ? Optional.of(
						EnumAdaptor.valueOf(((AppOverTime) application).getOverTimeClf().value, OvertimeAppAtr.class))
						: Optional.empty());

		AtomTask task = AtomTask.of(() -> {
			if (lstReasonDai.isEmpty()) {
				// 新規追加
				require.addUpdateReason(Arrays.asList(createReason(application, dateRefer)));
			} else {
				// 更新
				require.addUpdateReason(lstReasonDai.stream().map(x -> {

					return new ReasonApplicationDailyResult(x.getEmployeeId(), x.getDate(),
							x.getApplicationTypeReason(), x.getPrePostAtr(),
							new ApplicationReasonInfo(
									application.getOpAppStandardReasonCD()
											.orElse(x.getReasonInfo().getStandardReasonCode()),
									application.getOpAppReason().orElse(x.getReasonInfo().getOpAppReason())));
				}).collect(Collectors.toList()));
			}

			Map<Integer, String> mapItemDomain = createMap(lstReasonDai, application);
			// 申請理由の編集状態と履歴を作成する
			require.processCreateHist(application.getEmployeeID(), dateRefer, application.getAppID(),
					ScheduleRecordClassifi.RECORD, mapItemDomain, reflectTime);
		});

		return Optional.of(task);
	}

	private static Map<Integer, String> createMap(List<ReasonApplicationDailyResult> apps, Application application) {
		Map<Integer, String> result = new HashMap<>();
		for (ReasonApplicationDailyResult app : apps) {
			List<ItemContent> conts = ITEMCONTENT.stream()
					.filter(x -> x.beforeAfter == app.getPrePostAtr()
							&& x.type == app.getApplicationTypeReason().getAppType()
							&& (app.getApplicationTypeReason().getAppType() == ApplicationType.OVER_TIME_APPLICATION
									? app.getApplicationTypeReason().getOverAppAtr().get() == x.getOver()
									: true))
					.collect(Collectors.toList());
			if (application.getOpAppReason().isPresent()) {
				result.put(conts.get(0).getItemId(), app.getReasonInfo().getOpAppReason() == null ? null : app.getReasonInfo().getOpAppReason().v());
			} 
			if (application.getOpAppStandardReasonCD().isPresent()) {
				result.put(conts.get(1).getItemId(), app.getReasonInfo().getStandardReasonCode() == null ? null
						: String.valueOf(app.getReasonInfo().getStandardReasonCode().v()));
			}
		}
		return result;
	}

	@Data
	@AllArgsConstructor
	private static class ItemContent {

		public int itemId;

		public ApplicationType type;

		public PrePostAtr beforeAfter;

		public OvertimeAppAtr over;

		public static List<ItemContent> create(int itemId, int type, Integer over) {
			return Arrays.asList(
					new ItemContent(itemId, EnumAdaptor.valueOf(type, ApplicationType.class),
							EnumAdaptor.valueOf(0, PrePostAtr.class),
							over == null ? null : EnumAdaptor.valueOf(over, OvertimeAppAtr.class)),

					new ItemContent(itemId + 13, EnumAdaptor.valueOf(type, ApplicationType.class),
							EnumAdaptor.valueOf(0, PrePostAtr.class),
							over == null ? null : EnumAdaptor.valueOf(over, OvertimeAppAtr.class)),

					new ItemContent(itemId + 26, EnumAdaptor.valueOf(type, ApplicationType.class),
							EnumAdaptor.valueOf(1, PrePostAtr.class),
							over == null ? null : EnumAdaptor.valueOf(over, OvertimeAppAtr.class)),

					new ItemContent(itemId + 39, EnumAdaptor.valueOf(type, ApplicationType.class),
							EnumAdaptor.valueOf(1, PrePostAtr.class),
							over == null ? null : EnumAdaptor.valueOf(over, OvertimeAppAtr.class)));
		}
	}

	public static ReasonApplicationDailyResult createReason(Application application, GeneralDate date) {
		return new ReasonApplicationDailyResult(application.getEmployeeID(), date, new ApplicationTypeReason(
				application.getAppType(),
				application.getAppType() == ApplicationType.OVER_TIME_APPLICATION ? Optional.of(
						EnumAdaptor.valueOf(((AppOverTime) application).getOverTimeClf().value, OvertimeAppAtr.class))
						: Optional.empty()),
				application.getPrePostAtr(),
				new ApplicationReasonInfo(application.getOpAppStandardReasonCD().orElse(null),
						application.getOpAppReason().orElse(null)));
	}

	public static interface Require {

		// 日別実績の申請理由を取得
		public List<ReasonApplicationDailyResult> findReasonAppDaily(String employeeId, GeneralDate date,
				PrePostAtr preAtr, ApplicationType apptype, Optional<OvertimeAppAtr> overAppAtr);

		public void addUpdateReason(List<ReasonApplicationDailyResult> reason);

		// CreateEditStatusHistAppReasonAdapter.process
		public void processCreateHist(String employeeId, GeneralDate date, String appId,
				ScheduleRecordClassifi classification, Map<Integer, String> mapValue, GeneralDateTime reflectTime);
	}
}
