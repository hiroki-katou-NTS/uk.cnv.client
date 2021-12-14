/**
 * 
 */
package nts.uk.screen.at.app.ksu001.displayinshift;

import java.util.List;

import lombok.Value;
import nts.uk.screen.at.app.ksu001.getshiftpalette.PageInfo;
import nts.uk.screen.at.app.ksu001.getshiftpalette.TargetShiftPalette;
import nts.uk.screen.at.app.ksu001.getworkscheduleshift.ScheduleOfShiftDto;

/**
 * @author laitv
 *
 */
@Value
public class DisplayInShiftResult {
	
	// data cua shift pallet
	public List<PageInfo> listPageInfo ;                      // List<ページ, 名称>
	public TargetShiftPalette targetShiftPalette ; // 対象のシフトパレット： Optional<ページ, シフトパレット>
	public List<ShiftMasterMapWithWorkStyle> shiftMasterWithWorkStyleLst;  // 取得したシフト一覧： List<シフトマスタ, 出勤休日区分> ==> thằng này sẽ lưu vào localStorage
	 
	// data cua Grid 
	public List<ScheduleOfShiftDto> listWorkScheduleShift;  // List<勤務予定（シフト）dto> ==> data hiển thị trên grid
	
}
