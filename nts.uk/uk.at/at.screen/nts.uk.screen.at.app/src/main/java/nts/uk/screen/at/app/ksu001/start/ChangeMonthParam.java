/**
 * 
 */
package nts.uk.screen.at.app.ksu001.start;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.screen.at.app.ksu001.displayinshift.ShiftMasterMapWithWorkStyle;

/**
 * @author laitv
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ChangeMonthParam {
	
	public String viewMode;    
	
	public String startDate;            	 
	public String endDate;    	        	 
	public boolean isNextMonth;            
	public boolean cycle28Day; 
	public int modePeriod;
	
	public int unit;
	public String workplaceId;     	         
	public String workplaceGroupId;
	public List<String> sids;
	public List<ShiftMasterMapWithWorkStyle> listShiftMasterNotNeedGetNew;
	public boolean getActualData;
	
	// param them vao cua ver5
	public String personTotalSelected; // 個人計選択 A11_1
	public String workplaceSelected;   // 職場計選択A12_1
	public int day; // 締め日
	public boolean isLastDay;// 締め日
	
	
}
