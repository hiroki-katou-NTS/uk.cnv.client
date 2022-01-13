package nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskassign.taskassignworkplace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameNo;

/**
 * 
 * @author chungnt
 *
 */

 @RunWith(JMockit.class)
public class NarrowingDownTaskByWorkplaceFromEmployeesServiceTest {

	@Injectable
	private NarrowingDownTaskByWorkplaceFromEmployeesService.Require require;

	String employeeID = "employeeID";
	GeneralDate date = GeneralDate.today();
	String companyID = "companyID";
	TaskFrameNo taskFrameNo = new TaskFrameNo(1);
	List<String> listWpkIds = new ArrayList<>();
	NarrowingDownTaskByWorkplace narrowingDownTask = new NarrowingDownTaskByWorkplace("0000001", taskFrameNo,
			new ArrayList<>());
	NarrowingDownTaskByWorkplace narrowingDownTask2 = new NarrowingDownTaskByWorkplace("0000003", taskFrameNo,
			new ArrayList<>());

	// $職場リスト != require.職場を取得する(会社ID,社員ID,基準日) == empty
	@Test
	public void testGetEmployeesServiceTestTest() {

		new Expectations() {
			{
				require.findWpkIdsBySid(employeeID, date);
			}
		};

		Optional<NarrowingDownTaskByWorkplace> result = NarrowingDownTaskByWorkplaceFromEmployeesService.get(require,
				companyID, employeeID, date, taskFrameNo);

		assertThat(result.isPresent()).isFalse();
	}

	// $職場リスト = require.職場を取得する(会社ID,社員ID,基準日) == notEmpty
	// if $職場別作業の絞込 Not .isPresent()
	@Test
	public void testGetEmployeesServiceTestTest_1() {

		listWpkIds.add("0000003");
		listWpkIds.add("0000001");
		listWpkIds.add("0000002");

		new Expectations() {
			{
				require.findWpkIdsBySid(employeeID, date);
				result = listWpkIds;

				require.getNarrowingDownTaskByWorkplace("0000001", taskFrameNo);
			}
		};

		Optional<NarrowingDownTaskByWorkplace> result = NarrowingDownTaskByWorkplaceFromEmployeesService.get(require,
				companyID, employeeID, date, taskFrameNo);

		assertThat(result.isPresent()).isFalse();
	}

	// $職場リスト = require.職場を取得する(会社ID,社員ID,基準日) == notEmpty
	// $職場別作業の絞込.isPresent()
	@Test
	public void testGetEmployeesServiceTestTest_2() {

		listWpkIds.add("0000003");
		listWpkIds.add("0000001");
		listWpkIds.add("0000002");

		new Expectations() {
			{
				require.findWpkIdsBySid(employeeID, date);
				result = listWpkIds;

				require.getNarrowingDownTaskByWorkplace("0000001", taskFrameNo);
				result = Optional.of(narrowingDownTask2);
			}
		};

		Optional<NarrowingDownTaskByWorkplace> result = NarrowingDownTaskByWorkplaceFromEmployeesService.get(require,
				companyID, employeeID, date, taskFrameNo);

		assertThat(result.isPresent()).isTrue();
		assertThat(result.get().getTaskCodeList()).isEmpty();
		assertThat(result.get().getWorkPlaceId()).isEqualTo("0000003");
	}

	// $職場リスト = require.職場を取得する(会社ID,社員ID,基準日) == notEmpty
	// $職場別作業の絞込.isPresent()
	@Test
	public void testGetEmployeesServiceTestTest_3() {

		listWpkIds.add("0000002");
		listWpkIds.add("0000001");
		listWpkIds.add("0000003");

		new Expectations() {
			{
				require.findWpkIdsBySid(employeeID, date);
				result = listWpkIds;

				require.getNarrowingDownTaskByWorkplace("0000002", taskFrameNo);
				result = Optional.of(narrowingDownTask2);
			}
		};

		Optional<NarrowingDownTaskByWorkplace> result = NarrowingDownTaskByWorkplaceFromEmployeesService.get(require,
				companyID, employeeID, date, taskFrameNo);

		assertThat(result.isPresent()).isTrue();
		assertThat(result.get().getTaskCodeList()).isEmpty();
		assertThat(result.get().getWorkPlaceId()).isEqualTo("0000003");
	}
}
