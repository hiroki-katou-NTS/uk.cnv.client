package nts.uk.ctx.at.shared.dom.workrule.shiftmaster;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.WorkStyle;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.GetCombinationrAndWorkHolidayAtrService.Require;

@RunWith(JMockit.class)
public class GetCombinationrAndWorkHolidayAtrServiceTest {
	@Injectable
	private Require require;

	@Test
	public void testSetWorkHolidayClassification() {

		List<ShiftMaster> listShiftMaster = new ArrayList<>();
		ShiftMaster shiftMater = ShiftMasterHelper.createDummyWithCode("001");
		ShiftMaster shiftMater2 = ShiftMasterHelper.createDummyWithCode("002");
		ShiftMaster shiftMater3 = ShiftMasterHelper.createDummyWithCode("003");
		listShiftMaster.add(shiftMater);
		listShiftMaster.add(shiftMater2);
		listShiftMaster.add(shiftMater3);

		new Expectations(shiftMater, shiftMater2, shiftMater3) {
			{
				shiftMater.getWorkStyle(require);
				result = Optional.of(WorkStyle.ONE_DAY_REST);
			}
			{
				shiftMater2.getWorkStyle(require);
				result = Optional.of(WorkStyle.MORNING_WORK);
			}
			{
				shiftMater3.getWorkStyle(require);
				// result = Optional.empty()
			}
		};

		// Execute
		val instance = new GetCombinationrAndWorkHolidayAtrService();
		@SuppressWarnings("unchecked")
		val result = (Map<ShiftMaster,Optional<WorkStyle>>)NtsAssert.Invoke.privateMethod(
				instance,
				"setWorkHolidayClassification",
				require,
				listShiftMaster
			);

		assertThat(result).containsOnly(
				entry(shiftMater, Optional.of(WorkStyle.ONE_DAY_REST)),
				entry(shiftMater2, Optional.of(WorkStyle.MORNING_WORK)),
				entry(shiftMater3, Optional.empty())
				);

	}

	@Test
	public void testGetCode() {
		String companyID = "cid";
		List<String> lstShiftMasterCd = Arrays.asList("001","002", "003", "004");

		List<ShiftMaster> listShiftMaster = new ArrayList<>();
		ShiftMaster shiftMater = ShiftMasterHelper.createDummyWithCode("001");
		ShiftMaster shiftMater2 = ShiftMasterHelper.createDummyWithCode("002");
		ShiftMaster shiftMater3 = ShiftMasterHelper.createDummyWithCode("003");
		listShiftMaster.add(shiftMater);
		listShiftMaster.add(shiftMater2);
		listShiftMaster.add(shiftMater3);

		new Expectations(shiftMater, shiftMater2, shiftMater3) {
			{
				require.getByListEmp(companyID, lstShiftMasterCd);
				result = listShiftMaster;
			}
			{
				shiftMater.getWorkStyle(require);
				result = Optional.of(WorkStyle.ONE_DAY_REST);
			}
			{
				shiftMater2.getWorkStyle(require);
				result = Optional.of(WorkStyle.MORNING_WORK);
			}
			{
				shiftMater3.getWorkStyle(require);
				// result = Optional.empty()
			}
		};

		Map<ShiftMaster,Optional<WorkStyle>> datas = GetCombinationrAndWorkHolidayAtrService
				.getCode(require, companyID, lstShiftMasterCd);

		assertThat(datas).containsOnly(
				entry(shiftMater, Optional.of(WorkStyle.ONE_DAY_REST)),
				entry(shiftMater2, Optional.of(WorkStyle.MORNING_WORK)),
				entry(shiftMater3, Optional.empty())
				);
	}

	@Test
	public void testGetbyWorkInfo() {
		String companyID = "cid";

		List<WorkInformation> lstWorkInformation = Arrays.asList(
				new WorkInformation("001", "001"),
				new WorkInformation("002", "002"),
				new WorkInformation("003", "003"),
				new WorkInformation("004", "004"));

		List<ShiftMaster> listShiftMaster = new ArrayList<>();
		ShiftMaster shiftMater = ShiftMasterHelper.createDummyWithCode("001");
		ShiftMaster shiftMater2 = ShiftMasterHelper.createDummyWithCode("002");
		ShiftMaster shiftMater3 = ShiftMasterHelper.createDummyWithCode("003");
		listShiftMaster.add(shiftMater);
		listShiftMaster.add(shiftMater2);
		listShiftMaster.add(shiftMater3);

		new Expectations(shiftMater, shiftMater2, shiftMater3) {
			{
				require.getByListWorkInfo(companyID,lstWorkInformation);
				result = listShiftMaster;
			}
			{
				shiftMater.getWorkStyle(require);
				result = Optional.of(WorkStyle.ONE_DAY_REST);
			}
			{
				shiftMater2.getWorkStyle(require);
				result = Optional.of(WorkStyle.MORNING_WORK);
			}
			{
				shiftMater3.getWorkStyle(require);
				// result = Optional.empty()
			}
		};

		Map<ShiftMaster,Optional<WorkStyle>> datas = GetCombinationrAndWorkHolidayAtrService
				.getbyWorkInfo(require, companyID, lstWorkInformation);

		assertThat(datas).containsOnly(
				entry(shiftMater, Optional.of(WorkStyle.ONE_DAY_REST)),
				entry(shiftMater2, Optional.of(WorkStyle.MORNING_WORK)),
				entry(shiftMater3, Optional.empty())
				);

	}

}
