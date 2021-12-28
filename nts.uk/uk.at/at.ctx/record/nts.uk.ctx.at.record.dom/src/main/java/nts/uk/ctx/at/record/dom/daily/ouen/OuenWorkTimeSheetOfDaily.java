package nts.uk.ctx.at.record.dom.daily.ouen;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemIdContainer;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemUtil.AttendanceItemType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;

@Getter
/** 日別実績の応援作業別勤怠時間帯 */
public class OuenWorkTimeSheetOfDaily extends AggregateRoot {

	/** 社員ID: 社員ID */
	private String empId;
	
	/** 年月日: 年月日 */
	private GeneralDate ymd;
	
	/** 応援時間帯: 日別勤怠の応援作業時間帯 */
	private List<OuenWorkTimeSheetOfDailyAttendance> ouenTimeSheet;

	public OuenWorkTimeSheetOfDaily(String empId, GeneralDate ymd, List<OuenWorkTimeSheetOfDailyAttendance> ouenTimeSheet) {
		super();
		this.empId = empId;
		this.ymd = ymd;
		this.ouenTimeSheet = ouenTimeSheet;
	}
	
	public static OuenWorkTimeSheetOfDaily create(String empId, GeneralDate ymd,
			List<OuenWorkTimeSheetOfDailyAttendance> ouenTimeSheet) {

		return new OuenWorkTimeSheetOfDaily(empId, ymd, ouenTimeSheet);
	}
	
	/**
	 * @name [1] 変更する
	 * @input 時間帯リスト	List<日別勤怠の応援作業時間帯>
	 * @output	変更する勤怠項目
	 */
	public AttendanceItemToChange change(List<OuenWorkTimeSheetOfDailyAttendance> ouenTimeSheet) {
		// việc lấy attendanceId anh tuấn bảo tham khảo anh phong và anh lai.
		List<Integer> itemValues = this.getAttendanceId();
		//@応援時間帯 = 時間帯リスト			
		List<Integer> itemValuesNew = this.getAttendanceId(ouenTimeSheet);

		List<Integer> attendanceId = itemValues.stream().filter(c-> itemValuesNew.contains(c)).collect(Collectors.toList());
		attendanceId.addAll(itemValuesNew.stream().filter(c->itemValues.contains(c)).collect(Collectors.toList()));
		
		this.ouenTimeSheet = ouenTimeSheet;
		
		return new AttendanceItemToChange(attendanceId.stream().distinct().collect(Collectors.toList()), this);
	}
	
	/**
	 * @name [2] 応援時間帯に対応する勤怠項目ID一覧
	 * @output 	勤怠項目リスト	List<勤怠項目ID>
	 */
	public List<Integer> getAttendanceId() {
		return this.getAttendanceId(this.ouenTimeSheet);
	}
	
	private List<Integer> getAttendanceId(List<OuenWorkTimeSheetOfDailyAttendance> ouenTimeSheet){
		
		List<ItemValue> itemValues = AttendanceItemIdContainer.getIds(AttendanceItemType.DAILY_ITEM);
		List<ItemValue> values = itemValues.stream().filter(x -> !x.path().replace(x.path().replaceAll("[0-9]+$", ""), "").equals("")).collect(Collectors.toList());
		Map<Integer, List<ItemValue>> mapWorkNoItemsValue = AttendanceItemIdContainer.mapWorkNoItemsValue(values);
		List<Integer> result = new ArrayList<Integer>();
		for (OuenWorkTimeSheetOfDailyAttendance i : ouenTimeSheet) {
			List<ItemValue> id = mapWorkNoItemsValue.get(i.getWorkNo().v());
			if (id != null) {
				result.addAll(id.stream().map(c -> c.getItemId()).collect(Collectors.toList()));
			}
		}
		return result.stream().distinct().collect(Collectors.toList());
	}
	
	public void updateOuenTimeSheet(OuenWorkTimeSheetOfDailyAttendance ouenTimeSheetNew) {
		Optional<OuenWorkTimeSheetOfDailyAttendance> ouenTimeSheetOld = this.ouenTimeSheet.stream().filter(c->c.getWorkNo().v() == ouenTimeSheetNew.getWorkNo().v()).findFirst();
		if(ouenTimeSheetOld.isPresent()) {
			this.ouenTimeSheet.removeIf(c->c.getWorkNo().v() == ouenTimeSheetNew.getWorkNo().v());
			this.ouenTimeSheet.add(ouenTimeSheetNew);
		}
	}
}
