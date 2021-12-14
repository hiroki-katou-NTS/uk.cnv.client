package nts.uk.ctx.at.schedule.dom.shift.management.workavailability;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ColorCodeChar6;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.Remarks;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMaster;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterCode;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterDisInfor;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterName;

public class WorkAvailabilityByShiftMasterTestHelper {

	public static WorkAvailabilityByShiftMaster defaultCreate() {

		return new WorkAvailabilityByShiftMaster( Arrays.asList(new ShiftMasterCode("001")));
	}


	public static WorkAvailabilityByShiftMaster createWithShiftCodes(String ...shiftMasterCodes) {

		List<ShiftMasterCode> shiftMasterCodeList = Arrays.asList(shiftMasterCodes).stream()
				.map(c -> new ShiftMasterCode(c))
				.collect(Collectors.toList());

		return new WorkAvailabilityByShiftMaster(shiftMasterCodeList);

	}

	public static ShiftMaster createShiftMasterWithCode(String shiftMasterCode) {
		return new ShiftMaster(
				shiftMasterCode + "-sid",
    			new ShiftMasterCode(shiftMasterCode),
    			new ShiftMasterDisInfor(
    					new ShiftMasterName(shiftMasterCode + "-name"),
    					new ColorCodeChar6("000000"),
    					new ColorCodeChar6("000000"),
    					Optional.of(new Remarks(shiftMasterCode + "-r"))),
    			"001",
    			"001",
    			Optional.empty()
    			);

	}

	public static ShiftMaster createShiftMasterWithCodeName(String shiftMasterCode, String shiftMasterName) {
		return new ShiftMaster(
				shiftMasterCode + "-sid",
    			new ShiftMasterCode(shiftMasterCode),
    			new ShiftMasterDisInfor(
    					new ShiftMasterName(shiftMasterName),
    					new ColorCodeChar6("000000"),
    					new ColorCodeChar6("000000"),
    					Optional.of(new Remarks(shiftMasterCode + "-r"))),
    			"001",
    			"001",
    			Optional.empty()
    			);

	}

}
