/**
 * 
 */
package nts.uk.screen.at.app.ksu001.getsendingperiod;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author laitv
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ChangePeriodInWorkInfoParam {
	
	public String startDate;            	 
	public String endDate;    	
	
	public int unit;
	public String workplaceId;     	         
	public String workplaceGroupId;
	public List<String> sids;
	public boolean getActualData;
	
}
