package nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemIdContainer;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemUtil.AttendanceItemType;

/**
 * DS:日別勤怠(Work)から工数実績項目に変換する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.工数実績項目.日別勤怠(Work)から工数実績項目に変換する
 * 
 * @author tutt
 *
 */
@Stateless
public class DailyAttendenceWorkToManHrRecordItemConvertService {

	/**
	 * ■Public 日別勤怠(Work)からList<ItemValue>と工数実績項目に変換する [1] 変換する
	 * 
	 * @param require @Require
	 * @param intDaiy 日別勤怠(Work)
	 * @param itemIds List<工数実績項目ID>
	 * @return 工数実績変換結果
	 */
	public static ManHrRecordConvertResult convert(Require require, IntegrationOfDaily inteDaiy, List<Integer> itemIds) {

		// $勤怠項目値 = 日別勤怠(Work)からList<ItemValue>に変換する
		// 1.クラスを生成
		// AttendanceItemConvertFactory.createDailyConverter()
		DailyRecordToAttendanceItemConverter converter = require.createDailyConverter();

		// 2.データをセットする
		// DailyRecordToAttendanceItemConverter.setData(日別勤怠(Work))
		converter.setData(inteDaiy);
		List<Integer> pamramItemIds = AttendanceItemIdContainer.getIds(AttendanceItemType.DAILY_ITEM).stream()
				.map(x -> x.getItemId()).collect(Collectors.toList());
		// 3.項目値リストに変換する
		// DailyRecordToAttendanceItemConverter.convert(List.empty())
		List<ItemValue> itemValues = converter.convert(pamramItemIds);

		// $紐付け設定 = require.紐付け設定を取得する(工数実績項目リスト)
		List<ManHourRecordAndAttendanceItemLink> settings = require.get(itemIds);

		// $工数実績リスト = List.Empty
		List<ManHrTaskDetail> manHrRecords = new ArrayList<>();

		// $紐付け設定：
		for (ManHourRecordAndAttendanceItemLink l : settings) {

			// $値 = $勤怠項目値：filter $.itemId = $.勤怠項目ID
			// map $.value
			String value = itemValues.stream().filter(f -> f.getItemId() == l.getAttendanceItemId()).findAny()
					.map(m -> m.getValue()).orElse("");

			// $作業項目値 = 作業項目値#作業項目値($.工数実績項目ID,$値)
			TaskItemValue itemValue = new TaskItemValue(l.getItemId(), value);

			// $工数実績作業詳細 = $工数実績リスト：filter $.応援勤務枠No = $.応援勤務枠No
			Optional<ManHrTaskDetail> optManHrTaskDetail = manHrRecords.stream()
					.filter(f -> f.getSupNo().v().equals(l.getFrameNo().v())).findAny();

			// if $工数実績作業詳細.isEmpty
			if (!optManHrTaskDetail.isPresent()) {

				// $工数実績作業詳細 = 工数実績作業詳細#工数実績作業詳細($.応援勤務枠No, $作業項目値)
				ManHrTaskDetail detail = new ManHrTaskDetail(new ArrayList(Arrays.asList(itemValue)), l.getFrameNo());

				// $工数実績リスト.追加する($工数実績作業詳細)
				manHrRecords.add(detail);
			} else {

				// $工数実績作業詳細.工数項目リスト.追加する($作業項目値)
				optManHrTaskDetail.get().getTaskItemValues().add(itemValue);
			}
		}

		// return 工数実績変換結果#工数実績変換結果(日別勤怠(Work).年月日,$工数実績リスト,$勤怠項目値)

		return new ManHrRecordConvertResult(inteDaiy.getYmd(), manHrRecords, itemValues);

	}

	// ■Require
	public static interface Require {
		// [R-1] 紐付け設定を取得する
		// 工数実績項目と勤怠項目の紐付けRepository.Get*(会社ID,工数実績項目リスト)
		List<ManHourRecordAndAttendanceItemLink> get(List<Integer> items);
		
		// クラスを生成
		DailyRecordToAttendanceItemConverter createDailyConverter();
	}

}
