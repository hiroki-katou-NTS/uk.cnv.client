/**
 * 
 */
package nts.uk.screen.at.app.ksu001.getschedulesbyshift;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.screen.at.app.ksu001.displayinshift.ShiftMasterMapWithWorkStyle;
import nts.uk.screen.at.app.ksu001.getworkscheduleshift.ScheduleOfShiftDto;

/**
 * @author laitv
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SchedulesbyShiftDataResult {
	
	public List<ScheduleOfShiftDto> listWorkScheduleShift;  // List<勤務予定（シフト）dto>
	public List<ShiftMasterMapWithWorkStyle> listShiftMaster; // 取得したシフト一覧：List<シフトマスタ, Optional<出勤休日区分>>

}
