package nts.uk.ctx.at.shared.dom.workrule.shiftmaster;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.dto.ShiftMasterDto;

/**
 * DS : 職場用の組織別シフトマスタを取得する
 * 
 * @author anhdt
 *
 */
@Stateless
public class GetShiftMasterByWorkplaceService {

	public static List<ShiftMasterDto> getShiftMasterByWorkplaceService(Require require, String companyId,
			TargetOrgIdenInfor targetOrg) {

        Optional<ShiftMasterOrganization> shiftMaterOrgOpt = require.getByTargetOrg(companyId, targetOrg);
        
        if (!shiftMaterOrgOpt.isPresent()) {
            return Collections.emptyList();
        }
        
        return require.getByListShiftMaterCd(companyId, shiftMaterOrgOpt.get().getListShiftMaterCode());
	}

	public static interface Require {

		/**
		 * 	[R-1] 組織別シフトマスタを取得する	
		 * @param companyId
		 * @param targetOrg
		 * @return
		 */
		Optional<ShiftMasterOrganization> getByTargetOrg(String companyId, TargetOrgIdenInfor targetOrg);

		/**
		 * [R-2] シフトマスタをすべて取得する
		 * @param companyId
		 * @return
		 */
		List<ShiftMasterDto> getAllByCid(String companyId);

		/**
		 * [R-3] シフトマスタを取得する	
		 * @param companyId
		 * @param listShiftMaterCode
		 * @return
		 */
		List<ShiftMasterDto> getByListShiftMaterCd(String companyId, List<String> listShiftMaterCode);
	}
}
