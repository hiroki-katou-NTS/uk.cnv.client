package nts.uk.ctx.at.schedule.dom.shift.workcycle;

import lombok.val;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Injectable;
import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.workrule.ErrorStatusWorkInfo;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import mockit.integration.junit4.JMockit;

import java.util.*;

import static mockit.Deencapsulation.invoke;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JMockit.class)
public class WorkCycleTest {

    @Injectable
    private WorkInformation.Require require;

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void testEmptyCycleInfoList() {
        List<WorkCycleInfo> infos = new ArrayList<>();
        NtsAssert.businessException("Msg_1688", ()->{
            WorkCycle item = WorkCycle.create("cid","code","name", infos);
        });
    }

    @Test
    public void testEmptyCycleInfoList_1() {
        List<WorkCycleInfo> infos = new ArrayList<>();
        for (int i = 0; i < 100; i++){
            infos.add(new WorkCycleInfo(new NumOfWorkingDays(1), new WorkInformation("WType001", "WTime001")));
        }
        NtsAssert.businessException("Msg_1688", ()->{
            WorkCycle item = WorkCycle.create("cid","code","name", infos);
        });
    }

    @Test
    public void testGetWorkInfo_01() {
        WorkCycle item = WorkCycle.create("CID001", "COD001", "Name001", Arrays.asList(
                WorkCycleInfo.create(2, new WorkInformation("WType001", "WTime001")),
                WorkCycleInfo.create(3, new WorkInformation("WType002", "WTime002")),
                WorkCycleInfo.create(4, new WorkInformation("WType003", "WTime003"))
        ));
        assertThat(item.getWorkInfo(12, 1)).isEqualToComparingFieldByField(item.getInfos().get(0));
        assertThat(item.getWorkInfo(5, 0)).isEqualToComparingFieldByField(item.getInfos().get(1));
        assertThat(item.getWorkInfo(3, 5)).isEqualToComparingFieldByField(item.getInfos().get(2));
    }

	@Test
	public void testGetWorkInfo_02_RuntimeException() {
		List<WorkCycleInfo> infos = null;
		NtsAssert.systemError(() -> {
			new WorkCycle("cid",new WorkCycleCode("code"),new WorkCycleName("name"), infos).getWorkInfo(1,0);
		});
	}

    @Test
    public void testGetter(){
        WorkCycle item = WorkCycle.create("CID001", "COD001", "Name001", Arrays.asList(
                WorkCycleInfo.create(2, new WorkInformation("WType001", "WTime001")),
                WorkCycleInfo.create(3, new WorkInformation("WType002", "WTime002")),
                WorkCycleInfo.create(4, new WorkInformation("WType003", "WTime003"))
        ));
        NtsAssert.invokeGetters(item);
    }

    @Test
    public void testGetErrorList() {
        WorkCycle item = WorkCycle.create("CID001", "COD001", "Name001", Arrays.asList(
                WorkCycleInfo.create(2, new WorkInformation("WType001", "WTime001")),
                WorkCycleInfo.create(3, new WorkInformation("WType002", "WTime002")),
                WorkCycleInfo.create(4, new WorkInformation("WType004", null)),
                WorkCycleInfo.create(4, new WorkInformation("WType005", null)),
                WorkCycleInfo.create(4, new WorkInformation("WType006", null)),
                WorkCycleInfo.create(4, new WorkInformation("WType003", "WTime003"))
                )
        );
        new Expectations(WorkInformation.class) {
            {
                item.getInfos().get(0).getWorkInformation().checkErrorCondition(require);
                returns(ErrorStatusWorkInfo.WORKTYPE_WAS_DELETE,ErrorStatusWorkInfo.WORKTYPE_WAS_ABOLISHED,
                        ErrorStatusWorkInfo.WORKTIME_ARE_REQUIRE_NOT_SET,ErrorStatusWorkInfo.NORMAL,
                        ErrorStatusWorkInfo.NORMAL,ErrorStatusWorkInfo.WORKTIME_ARE_SET_WHEN_UNNECESSARY);
            }
        };
        List<ErrorStatusWorkInfo> errorStatusWorkInfos = item.checkError(require);
        assertThat(errorStatusWorkInfos.get(0)).isEqualByComparingTo(ErrorStatusWorkInfo.WORKTYPE_WAS_DELETE);
        assertThat(errorStatusWorkInfos.get(1)).isEqualByComparingTo(ErrorStatusWorkInfo.WORKTYPE_WAS_ABOLISHED);
        assertThat(errorStatusWorkInfos.get(2)).isEqualByComparingTo(ErrorStatusWorkInfo.WORKTIME_ARE_REQUIRE_NOT_SET);
        assertThat(errorStatusWorkInfos.get(3)).isEqualByComparingTo(ErrorStatusWorkInfo.NORMAL);
        assertThat(errorStatusWorkInfos.get(4)).isEqualByComparingTo(ErrorStatusWorkInfo.NORMAL);
        assertThat(errorStatusWorkInfos.get(5)).isEqualByComparingTo(ErrorStatusWorkInfo.WORKTIME_ARE_SET_WHEN_UNNECESSARY);

    }

    @Test
	public void testGetWorkInfoByPosition01() {
    	val wInfo1 = WorkCycleInfo.create(2, new WorkInformation("WType001", "WTime001"));
    	val wInfo2 = WorkCycleInfo.create(3, new WorkInformation("WType002", "WTime002"));
		WorkCycle instance = WorkCycle.create("CID001", "COD001", "Name001", Arrays.asList(wInfo1,wInfo2));
		val result = NtsAssert.Invoke.privateMethod(instance, "getWorkInfoByPosition", 5);
		assertThat(result).isEqualTo(wInfo2);
	}

	@Test
	public void testGetWorkInfoByPosition02() {
		val wInfo1 = WorkCycleInfo.create(2, new WorkInformation("WType001", "WTime001"));
		val wInfo2 = WorkCycleInfo.create(3, new WorkInformation("WType002", "WTime002"));
		WorkCycle instance = WorkCycle.create("CID001", "COD001", "Name001", Arrays.asList(wInfo1,wInfo2));
		val result = NtsAssert.Invoke.privateMethod(instance, "getWorkInfoByPosition", 6);
		assertThat(result).isEqualTo(wInfo1);
	}

	@Test
	public void testGetWorkInfoByPosition_EmptyInfos_RuntimeException_01() {
		List<WorkCycleInfo> infos = Collections.emptyList();
		WorkCycle instance = new WorkCycle("cid",new WorkCycleCode("code"),new WorkCycleName("name"), infos);
		NtsAssert.systemError(() -> {
			invoke(instance, "getWorkInfoByPosition", 1);
		});
	}

	@Test
	public void testGetWorkInfoByPosition_NullInfos_RuntimeException_02() {
		List<WorkCycleInfo> infos = null;
		WorkCycle instance = new WorkCycle("cid",new WorkCycleCode("code"),new WorkCycleName("name"), infos);
		NtsAssert.systemError(() -> {
			invoke(instance, "getWorkInfoByPosition", 1);
		});
	}
}
