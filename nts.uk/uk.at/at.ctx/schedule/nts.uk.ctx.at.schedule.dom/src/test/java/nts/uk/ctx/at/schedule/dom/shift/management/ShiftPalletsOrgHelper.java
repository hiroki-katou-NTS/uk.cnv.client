package nts.uk.ctx.at.schedule.dom.shift.management;

import java.util.Arrays;

import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterCode;
import nts.uk.shr.com.enumcommon.NotUseAtr;

public class ShiftPalletsOrgHelper {
	public static ShiftPalletsOrg getShiftPalletsOrgDefault() {
		return new ShiftPalletsOrg(
				new TargetOrgIdenInfor(
						TargetOrganizationUnit.WORKPLACE, //dummy
						"e34d86c4-1e32-463e-b86c-68551e0bbf18", //dummy
						"e6fea7af-0365-4332-9943-e2c17f65bea6"), //dummy
				1, 
				new ShiftPallet(
						new ShiftPalletDisplayInfor(
								new ShiftPalletName("shpaName"), //dummy
								NotUseAtr.USE, //dummy
								new ShiftRemarks("shRemar")), //dummy
						Arrays.asList(
								new ShiftPalletCombinations(
										1, //dummy
										new ShiftCombinationName("shComName1"), //dummy
										Arrays.asList(new Combinations(
												1, //dummy
												new ShiftMasterCode("0000001"))))))); //dummy
	}
}
