package nts.uk.ctx.at.shared.app.find.workrule.shiftmaster;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.GetUsableShiftMasterService;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.GetUsableShiftMasterService.Require;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterOrgRepository;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterOrganization;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterRepository;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.dto.ShiftMasterDto;
import nts.uk.shr.com.context.AppContexts;

/**
 *
 * 
 * @author anhdt
 *
 */
@Stateless
public class ShiftMasterOrgFinder {
	
	@Inject 
	private GetUsableShiftMasterService getShiftMasterSv;
	
	@Inject
	private ShiftMasterOrgRepository shiftMasterOrgRp;
	
	@Inject
	private ShiftMasterRepository shiftMasterRepo;

	// 使用できるシフトマスタの勤務情報と補正済み所定時間帯を取得する
	public List<ShiftMasterDto> getShiftMastersByWorkPlace(String targetId, Integer targetUnit) {
		String companyId = AppContexts.user().companyId();
		Require require = new  RequireImpl(shiftMasterOrgRp, shiftMasterRepo);
		TargetOrganizationUnit unit = EnumAdaptor.valueOf(targetUnit, TargetOrganizationUnit.class);
		TargetOrgIdenInfor target = new TargetOrgIdenInfor(unit, targetId, targetId);
		
		@SuppressWarnings("static-access")
		List<ShiftMasterDto> shiftMasters = getShiftMasterSv.getUsableShiftMaster(require, companyId, target);
		
		if(CollectionUtil.isEmpty(shiftMasters)) {
			return Collections.emptyList();
		}
		
		return shiftMasters;

	}
	
	@AllArgsConstructor
	private static class RequireImpl implements GetUsableShiftMasterService.Require {
		
		@Inject
		private ShiftMasterOrgRepository shiftMasterOrgRp;
		
		@Inject
		private ShiftMasterRepository shiftMasterRepo;
		
		@Override
		public Optional<ShiftMasterOrganization> getByTargetOrg(String companyId, TargetOrgIdenInfor targetOrg) {
			return shiftMasterOrgRp.getByTargetOrg(companyId, targetOrg);
		}

		@Override
		public List<ShiftMasterDto> getAllByCid(String companyId) {
			return shiftMasterRepo.getAllByCid(companyId);
		}

		@Override
		public List<ShiftMasterDto> getByListShiftMaterCd(String companyId, List<String> listShiftMaterCode) {
			return shiftMasterRepo.getByListShiftMaterCd(companyId, listShiftMaterCode);
		}

	}

}
