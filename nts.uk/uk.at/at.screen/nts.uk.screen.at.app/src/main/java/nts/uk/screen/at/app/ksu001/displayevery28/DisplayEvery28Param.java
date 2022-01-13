package nts.uk.screen.at.app.ksu001.displayevery28;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.screen.at.app.ksu001.displayinshift.ShiftMasterMapWithWorkStyle;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class DisplayEvery28Param {

	public GeneralDate date;
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
	public int day;
	public boolean isLastDay;
	
	public int mode;
	
	public boolean isNextMonth; // 期間を進めるか
}
