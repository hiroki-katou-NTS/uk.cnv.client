package nts.uk.screen.at.app.dailyperformance.correction.dto;

import lombok.Value;

/**
 * 
 * @author HungTT - 積休残数
 *
 */

@Value
public class ReserveLeaveDto {
	
	/**
	 * 積休管理する
	 */
	private boolean manageRemainNumber;
	
	private Double remainNumber; 
	
}
