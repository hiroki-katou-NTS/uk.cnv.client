package nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.daily.timegroup.TaskTimeGroup;
import nts.uk.ctx.at.record.dom.daily.timegroup.TaskTimeZone;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.工数実績項目.工数実績作業ブロックを作成する
 * DS: 工数実績作業ブロックを作成する
 * 
 * @author tutt
 *
 */
public class ActualManHrTaskBlockCreationService {

	/**
	 * ■Public [1] 作成する 説明:工数実績作業詳細データと日別実績の作業時間帯グループから工数実績作業ブロックを作成する
	 * 
	 * @param require     Require
	 * @param sId         社員ID
	 * @param date        年月日
	 * @param taskDetails 工数実績項目リスト
	 * @return 日別実績の工数実績作業
	 */
	public static DailyActualManHrActualTask create(Require require, String sId, GeneralDate date,
			List<ManHrTaskDetail> taskDetails) {

		// $作業ブロックリスト = List.Empty
		List<ManHrPerformanceTaskBlock> taskBlocks = new ArrayList<>();

		// $作業時間帯グループ = require.作業時間帯グループを取得する(社員ID,年月日)
		Optional<TaskTimeGroup> timeGroupOpt = require.get(sId, date);

		// if not $作業時間帯グループ.isEmpty
		if (timeGroupOpt.isPresent()) {
			
			// $作業時間帯グループ.時間帯リスト：
			for (TaskTimeZone t : timeGroupOpt.get().getTimezones()) {

				// $作業詳細 = 工数実績項目リスト：$2.応援勤務枠No.含む($1.対象応援勤務枠)
				List<ManHrTaskDetail> lstTaskDetail = taskDetails.stream().filter(x -> t.getSupNos().stream()
						.filter(sn -> sn.v().equals(x.getSupNo().v())).findFirst().isPresent())
						.collect(Collectors.toList());

				// $作業ブロック = 工数実績作業ブロック#工数実績作業ブロック($.時間帯,$作業詳細)
				ManHrPerformanceTaskBlock taskBlock = new ManHrPerformanceTaskBlock(t.getCaltimeSpan(), lstTaskDetail);

				// $作業ブロックリスト.追加する($作業ブロック)
				taskBlocks.add(taskBlock);
				
				// 工数実績項目リスト = 工数実績項目リスト：except $作業詳細
				taskDetails = taskDetails
						.stream().filter(x -> !lstTaskDetail.stream().filter(lt -> lt.getSupNo().v().equals(x.getSupNo().v())).findFirst().isPresent())
						.collect(Collectors.toList());
			}
		}
		
		// 工数実績項目リスト：
		for (ManHrTaskDetail d : taskDetails) {
			// $開始時刻
			String startTime = "";

			// $終了時刻
			String endTime = "";

			for (TaskItemValue v : d.getTaskItemValues()) {

				// $開始時刻 = $1.工数項目リスト：$2.工数実績項目ID = 1
				if (v.getItemId() == 1) {
					startTime = v.getValue();
				}

				// $終了時刻 = $1.工数項目リスト：$2.工数実績項目ID = 2
				if (v.getItemId() == 2) {
					endTime = v.getValue();
				}
			}
			// if not ($開始時刻.isEmpty OR $終了時刻.isEmpty)
			if (!(startTime.isEmpty() || endTime.isEmpty())) {

				// $時間帯 = 計算用時間帯#計算用時間帯($開始時刻,$終了時刻)
				TimeSpanForCalc timeSpanForCalc = new TimeSpanForCalc(new TimeWithDayAttr(Integer.parseInt(startTime)),new TimeWithDayAttr(Integer.parseInt(endTime)));

				// $作業ブロック = 工数実績作業ブロック#工数実績作業ブロック($時間帯,$作業詳細)
				ManHrPerformanceTaskBlock taskBlock = new ManHrPerformanceTaskBlock(timeSpanForCalc, Arrays.asList(d));

				// $作業ブロックリスト.追加する($作業ブロック)
				taskBlocks.add(taskBlock);
			}
		}

		// return 日別実績の工数実績作業#日別実績の工数実績作業(年月日,$作業ブロックリスト)
		return new DailyActualManHrActualTask(date, taskBlocks);
	}

	// ■Public
	// ■Require
	public static interface Require {

		// [R-1] 作業時間帯グループを取得する
		// 日別実績の作業時間帯グループRepository.Get(社員ID,年月日)
		Optional<TaskTimeGroup> get(String sID, GeneralDate date);
	}
}
