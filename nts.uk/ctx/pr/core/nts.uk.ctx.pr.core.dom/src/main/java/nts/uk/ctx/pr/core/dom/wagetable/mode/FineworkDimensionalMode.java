/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.pr.core.dom.wagetable.mode;

import java.util.Map;

import lombok.Getter;
import nts.uk.ctx.pr.core.dom.wagetable.DemensionOrder;
import nts.uk.ctx.pr.core.dom.wagetable.ElementCount;
import nts.uk.ctx.pr.core.dom.wagetable.WageTableElement;

/**
 * The Class FineworkDimensionalMode.
 */
@Getter
public class FineworkDimensionalMode implements DimensionalMode {

	/** The Constant mode. */
	public static final ElementCount mode = ElementCount.Finework;

	@Override
	public Map<DemensionOrder, WageTableElement> getElements() {
		// TODO Auto-generated method stub
		return null;
	}


}
