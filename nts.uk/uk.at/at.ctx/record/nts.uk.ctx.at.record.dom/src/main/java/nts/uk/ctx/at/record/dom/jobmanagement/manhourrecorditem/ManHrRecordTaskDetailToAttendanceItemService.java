package nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;

/**
 * DS: 工数実績作業詳細から勤怠項目に変換する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.工数実績項目.工数実績作業詳細から勤怠項目に変換する
 * 
 * @author tutt
 *
 */
public class ManHrRecordTaskDetailToAttendanceItemService {

	/**
	 * ■Public [1] 変換する 工数実績作業詳細を勤怠項目リストの値に置き換える
	 * 
	 * @param require     @Require
	 * @param attItems    勤怠項目リスト
	 * @param taskDetails 作業リスト
	 * @return List<ItemValue>
	 */
	public static List<ItemValue> convert(Require require, List<ItemValue> attItems,
			List<ManHrTaskDetail> taskDetails) {
		// $対象作業詳細 = 作業リスト：flatMap $.工数項目リスト
		List<TaskItemValue> taskItemValues = taskDetails.stream().flatMap(m -> m.getTaskItemValues().stream())
				.collect(Collectors.toList());

		// $対象項目リスト = $対象作業詳細：ｍap $.工数実績項目ID distinct
		List<Integer> itemLst = taskItemValues.stream().map(m -> m.getItemId()).distinct().collect(Collectors.toList());

		// $紐付け設定 = require.紐付け設定を取得する($対象項目リスト)
		List<ManHourRecordAndAttendanceItemLink> setting = require.get(itemLst);

		// $更新後のItemValue = List.Empty
		List<ItemValue> result = new ArrayList<>();

		// $紐付け設定：
		for (ManHourRecordAndAttendanceItemLink link : setting) {

			// $工数項目リスト = 作業リスト：filter $.応援勤務枠No = $.応援勤務枠No
			List<ManHrTaskDetail> manHrItems = taskDetails.stream().filter(d -> d.getSupNo().equals(link.getFrameNo()))
					.collect(Collectors.toList());

			// $値 = $工数項目リスト.工数項目リスト：filter $.工数実績項目ID = $.工数実績項目ID
			// map $.値
			Optional<TaskItemValue> taskItem = manHrItems.stream().flatMap(m -> m.getTaskItemValues().stream())
					.filter(f -> f.getItemId() == link.getItemId()).findAny();

			// $対象勤怠項目 = 勤怠項目リスト：find $.itemId == $.勤怠項目ID
			// $対象勤怠項目.value($値)

			Optional<ItemValue> itemValueOpt = attItems.stream().filter(f -> f.getItemId() == link.getAttendanceItemId())
					.findAny();

			if (itemValueOpt.isPresent() && taskItem.isPresent()) {
				ItemValue itemValue = itemValueOpt.get();
				itemValue.value(taskItem.get().getValue());

				// $更新後のItemValue.Add($対象勤怠項目)
				result.add(itemValue);
			}
		}

		return result;
	}

	// ■Require
	public static interface Require {
		// [R-1] 紐付け設定を取得する
		// 工数実績項目と勤怠項目の紐付けRepository.Get*(会社ID,工数実績項目リスト)
		List<ManHourRecordAndAttendanceItemLink> get(List<Integer> items);
	}

}
