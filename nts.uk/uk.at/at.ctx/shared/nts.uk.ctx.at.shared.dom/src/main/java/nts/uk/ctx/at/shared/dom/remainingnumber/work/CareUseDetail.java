package nts.uk.ctx.at.shared.dom.remainingnumber.work;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author sonnlb
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CareUseDetail {
	/**
	 * 看護介護区分
	 */
	private CareType careType;

	/**
	 * 日数
	 */
	private double days;
	
	public CareUseDetail clone(){
		CareUseDetail clone = new CareUseDetail();
		clone.setCareType(this.careType);
		clone.setDays(this.days);
		
		return clone;
	}
	
}
