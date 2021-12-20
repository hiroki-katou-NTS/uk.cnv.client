package nts.uk.screen.at.app.ksu001.aggrerateresult;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AggregateResultParam {

	public GeneralDate startDate;            	 
	public GeneralDate endDate;    	
	
	public int unit;
	public String workplaceId;     	         
	public String workplaceGroupId;
	public List<String> sids;
	public boolean getActualData;

	// ・集計したい個人計：Optional<個人計カテゴリ>
	public Integer personalCounterOp;
	
	// ・集計したい職場計：Optional<職場計カテゴリ>
	
	public Integer workplaceCounterOp;
	
	// 締め日
	public int day;
	
	public boolean isShiftDisplay;
}
