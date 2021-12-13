package nts.uk.screen.at.app.ksu001.changedisplayorganization;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.DateInMonth;
import nts.uk.screen.at.app.ksu001.displayinshift.ShiftMasterMapWithWorkStyle;
import nts.uk.screen.at.app.ksu001.start.ShiftPaletteWantGet;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChangeDisplayOrganizationParam {
	
	public static final Integer SHIFT_MODE = 0; // シフト表示の場合
	public static final Integer WORK_MODE = 1; // 勤務表示
	public static final Integer ABBREVIATION_MODE = 2; // 略名表示の場合
	
	public GeneralDate startDate;            	 
	public GeneralDate endDate;    	
	
	public int unit;
	public String workplaceId;     	         
	public String workplaceGroupId;
	public List<String> sids;
	public List<ShiftMasterMapWithWorkStyle> listShiftMasterNotNeedGetNew;
	public boolean getActualData;

	
	// ・集計したい個人計：Optional<個人計カテゴリ>
	public Integer personalCounterOp;
	
	// ・集計したい職場計：Optional<職場計カテゴリ>
	
	public Integer workplaceCounterOp;
	
	// 締め日
	public DateInMonth day;
	
	public int mode; 
	
	public boolean isNextMonth;
	
	public GeneralDate baseDate;
	
	public boolean cycle28Day;
	
	public ShiftPaletteWantGet shiftPaletteWantGet; // ・取得したいシフトパレット：Optional<単位, ページ>  単位

}
