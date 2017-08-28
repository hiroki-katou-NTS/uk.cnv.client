/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.ac.budget.external.actualresult;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import nts.uk.ctx.at.schedule.dom.budget.external.actualresult.WorkplaceAdapter;

/**
 * The Class ScWorkplaceAdapterImpl.
 */
@Stateless
public class ScWorkplaceAdapterImpl implements WorkplaceAdapter {

    /** The workplace pub. */
//    @Inject
//    private WorkplacePub workplacePub;

    /*
     * 
     * (non-Javadoc)
     * 
     * @see
     * nts.uk.ctx.at.schedule.dom.budget.external.actualresult.WorkplaceAdapter#
     * findWpkIdList(java.lang.String, java.util.Date)
     */
    @Override
    public List<String> findWpkIdList(String companyId, String wpkCode, Date baseDate) {
        // GeneralDate generalDate = GeneralDate.legacyDate(baseDate);
        // return this.workplacePub.findWpkIdsByWkpCode(companyId, wpkCode,
        // generalDate);

        // TODO: fake return wpkId.
        StringBuilder wpkId = new StringBuilder(wpkCode);
        String zero = "0";
        while (wpkId.length() < 36) {
            wpkId.append(zero);
        }
        return Arrays.asList(wpkId.toString());

    }

}
