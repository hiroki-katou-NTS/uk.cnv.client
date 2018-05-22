/******************************************************************
 * Copyright (c) 2018 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.query.model.employement;

import lombok.Builder;
import lombok.Data;

/**
 * The Class EmploymentModel.
 */
//所属雇用
@Data
@Builder
public class EmploymentModel {

	/** The employment code. */
	private String employmentCode; //雇用コード
	
	/** The employment name. */
	private String employmentName; //雇用名称
	
}
