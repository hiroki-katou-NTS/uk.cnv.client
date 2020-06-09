package nts.uk.ctx.bs.employee.dom.workplace.group.domainservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import nts.arc.enums.EnumAdaptor;
import nts.arc.task.tran.AtomTask;
import nts.uk.ctx.bs.employee.dom.workplace.group.AffWorkplaceGroup;
import nts.uk.ctx.bs.employee.dom.workplace.group.WorkplaceGroup;
import nts.uk.ctx.bs.employee.dom.workplace.group.WorkplaceGroupCode;
import nts.uk.ctx.bs.employee.dom.workplace.group.WorkplaceGroupName;
import nts.uk.ctx.bs.employee.dom.workplace.group.WorkplaceGroupType;
import nts.uk.ctx.bs.employee.dom.workplace.group.WorkplaceReplaceResult;
import nts.uk.ctx.bs.employee.dom.workplace.group.WorkplaceReplacement;
import nts.uk.ctx.bs.employee.dom.workplace.group.domainservice.ReplaceWorkplacesService.Require;

/**
 * 
 * @author phongtq
 *
 */
@RunWith(JMockit.class)
public class ReplaceWorkplacesServiceTest {
	@Injectable
	private Require require;

	AtomTask atomTakss = AtomTask.of(() -> {
	});

	@Test
	public void testBelongAnother() {
		
		new MockUp<AddWplOfWorkGrpService>() {
			@Mock
			public WorkplaceReplaceResult addWorkplace(AddWplOfWorkGrpService.Require require, WorkplaceGroup group,
					String lstWorkplaceId) {
				return WorkplaceReplaceResult.belongAnother("000000000000000000000000000000000014");
			}
		};
		
		WorkplaceGroup group = new WorkplaceGroup("000000000000000000000000000000000016", "00000000000001",
				new WorkplaceGroupCode("0000000001"), // dummy
				new WorkplaceGroupName("00000000000000000011"), // dummy
				EnumAdaptor.valueOf(1, WorkplaceGroupType.class));// dummy

		List<String> lstWorkplaceId = Arrays.asList("000000000000000000000000000000000012",
				"000000000000000000000000000000000014", "000000000000000000000000000000000016");

		List<AffWorkplaceGroup> lstFormerAffInfo = Arrays.asList(
				new AffWorkplaceGroup("00000000000001", "000000000000000000000000000000000011"),
				new AffWorkplaceGroup("00000000000001", "000000000000000000000000000000000012"));

		new Expectations() {
			{
				require.getByWKPGRPID("00000000000001");// dummy
				result = lstFormerAffInfo;
			}
		};

		Map<String, WorkplaceReplaceResult> results = ReplaceWorkplacesService.getWorkplace(require, group,
				lstWorkplaceId);
		assertThat(results.get("000000000000000000000000000000000014").getWorkplaceReplacement().name()
				.equals(WorkplaceReplacement.BELONGED_ANOTHER.name())).isTrue();
	}

	@Test
	public void testAdd() {
		new MockUp<AddWplOfWorkGrpService>() {
			@Mock
			public WorkplaceReplaceResult addWorkplace(AddWplOfWorkGrpService.Require require, WorkplaceGroup group,
					String lstWorkplaceId) {
				return WorkplaceReplaceResult.add(atomTakss);
			}
		};
		
		WorkplaceGroup group = new WorkplaceGroup("000000000000000000000000000000000016", "00000000000001",
				new WorkplaceGroupCode("0000000001"), // dummy
				new WorkplaceGroupName("00000000000000000011"), // dummy
				EnumAdaptor.valueOf(1, WorkplaceGroupType.class));// dummy

		List<String> lstWorkplaceId = Arrays.asList("000000000000000000000000000000000012",
				"000000000000000000000000000000000014", "000000000000000000000000000000000016");

		List<AffWorkplaceGroup> lstFormerAffInfo = Arrays.asList(
				new AffWorkplaceGroup("00000000000001", "000000000000000000000000000000000011"),
				new AffWorkplaceGroup("00000000000001", "000000000000000000000000000000000012"));

		new Expectations() {
			{
				require.getByWKPGRPID("00000000000001");// dummy
				result = lstFormerAffInfo;
			}
		};

		Map<String, WorkplaceReplaceResult> results = ReplaceWorkplacesService.getWorkplace(require, group,
				lstWorkplaceId);
		assertThat(results.get("000000000000000000000000000000000016").getWorkplaceReplacement().name()
				.equals(WorkplaceReplacement.ADD.name())).isTrue();
	}

	@Test
	public void testAlreadyBelong() {
		
		new MockUp<AddWplOfWorkGrpService>() {
			@Mock
			public WorkplaceReplaceResult addWorkplace(AddWplOfWorkGrpService.Require require, WorkplaceGroup group,
					String lstWorkplaceId) {
				return WorkplaceReplaceResult.alreadyBelong("000000000000000000000000000000000012");
			}
		};
		
		WorkplaceGroup group = new WorkplaceGroup("000000000000000000000000000000000016", "00000000000001",
				new WorkplaceGroupCode("0000000001"), // dummy
				new WorkplaceGroupName("00000000000000000011"), // dummy
				EnumAdaptor.valueOf(1, WorkplaceGroupType.class));// dummy

		List<String> lstWorkplaceId = Arrays.asList("000000000000000000000000000000000012",
				"000000000000000000000000000000000014", "000000000000000000000000000000000016");

		List<AffWorkplaceGroup> lstFormerAffInfo = Arrays.asList(
				new AffWorkplaceGroup("00000000000001", "000000000000000000000000000000000011"),
				new AffWorkplaceGroup("00000000000001", "000000000000000000000000000000000012"));

		new Expectations() {
			{
				require.getByWKPGRPID("00000000000001");// dummy
				result = lstFormerAffInfo;
			}
		};

		Map<String, WorkplaceReplaceResult> results = ReplaceWorkplacesService.getWorkplace(require, group,
				lstWorkplaceId);
		assertThat(results.get("000000000000000000000000000000000012").getWorkplaceReplacement().name()
				.equals(WorkplaceReplacement.ALREADY_BELONGED.name())).isTrue();
	}

	@Test
	public void testDel() {
		WorkplaceGroup group = new WorkplaceGroup("000000000000000000000000000000000010", // dummy
				"00000000000001", // dummy
				new WorkplaceGroupCode("0000000001"), // dummy
				new WorkplaceGroupName("00000000000000000011"), // dummy
				EnumAdaptor.valueOf(1, WorkplaceGroupType.class));// dummy
		List<String> lstWorkplaceId = Arrays.asList("000000000000000000000000000000000012",
				"000000000000000000000000000000000016", "000000000000000000000000000000000010");
		List<AffWorkplaceGroup> lstFormerAffInfo = Arrays.asList(
				new AffWorkplaceGroup("00000000000001", "000000000000000000000000000000000016"),
				new AffWorkplaceGroup("00000000000001", "000000000000000000000000000000000012"),
				new AffWorkplaceGroup("00000000000001", "000000000000000000000000000000000011"));
		new Expectations() {
			{
				require.getByWKPGRPID("00000000000001");// dummy
				result = lstFormerAffInfo;
			}
		};
		Map<String, WorkplaceReplaceResult> workplacesService = ReplaceWorkplacesService.getWorkplace(require, group,
				lstWorkplaceId);
		workplacesService.forEach((WKPID, result) -> {
			result.getPersistenceProcess().get().run();
			require.deleteByWKPID(WKPID);
			result = WorkplaceReplaceResult.delete(atomTakss);
		});

		assertThat(workplacesService.get("000000000000000000000000000000000011").getWorkplaceReplacement().name()
				.equals(WorkplaceReplacement.DELETE.name())).isTrue();
	}

}
