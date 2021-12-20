package nts.uk.ctx.at.record.app.command.stamp.card.stampcard.management.personalengraving;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author chungnt
 *
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WorkGroupCommand {

	/** 作業CD1 */
	private String workCode1;
	
	/** 作業CD2 */
	private String workCode2;
	
	/** 作業CD3 */
	private String workCode3;
	
	/** 作業CD4 */
	private String workCode4;
	
	/** 作業CD5 */
	private String workCode5;
	
}
