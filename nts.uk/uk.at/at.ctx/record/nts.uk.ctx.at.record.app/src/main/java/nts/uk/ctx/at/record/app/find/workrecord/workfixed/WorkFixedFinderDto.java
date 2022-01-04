/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.record.app.find.workrecord.workfixed;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;

/**
 * The Class WorkFixedFinderDto.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WorkFixedFinderDto  {
	
	/** The closure id. */
	// 締めID
	private Integer closureId;
	
	/** The confirm pid. */
	//確定者ID
	private String confirmPid;

	/** The wkp id. */
	//職場ID
	private String wkpId;
	
	/** The confirm cls status. */
	//確定区分
	private Integer confirmClsStatus;
	
	/** The fixed date. */
	//確定日
	private GeneralDate fixedDate;
	
	/** The process date. */
	//処理年月
	private Integer processDate;
	
	/** The cid. */
    //会社ID
    private String cid;
    
    /** The employee name. */
    private String employeeName;
	

	/**
	 * Instantiates a new work fixed finder dto.
	 *
	 * @param closureId the closure id
	 * @param confirmPid the confirm pid
	 * @param wkpId the wkp id
	 * @param confirmClsStatus the confirm cls status
	 * @param fixedDate the fixed date
	 * @param processDate the process date
	 * @param cid the cid
	 * @param employeeName the employee name
	 */
	
}
