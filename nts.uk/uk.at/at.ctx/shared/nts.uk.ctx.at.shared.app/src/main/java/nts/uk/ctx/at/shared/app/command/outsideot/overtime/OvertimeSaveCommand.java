/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.app.command.outsideot.overtime;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * The Class OvertimeSaveCommand.
 */
@Getter
@Setter
public class OvertimeSaveCommand {

	/** The overtimes. */
	private List<OvertimeSaveDto> overtimes;
	
}
