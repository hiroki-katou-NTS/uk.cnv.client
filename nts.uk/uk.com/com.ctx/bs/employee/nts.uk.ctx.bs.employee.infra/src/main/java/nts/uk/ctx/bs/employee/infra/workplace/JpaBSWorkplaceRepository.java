/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.bs.employee.infra.workplace;

import java.util.List;

import javax.ejb.Stateless;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.bs.employee.dom.workplace.Workplace;
import nts.uk.ctx.bs.employee.dom.workplace.WorkplaceRepository;
@Stateless
public class JpaBSWorkplaceRepository implements WorkplaceRepository {

	@Override
	public List<Workplace> findByStartDate(String companyId, GeneralDate date) {
		// TODO Auto-generated method stub
		return null;
	}

}
