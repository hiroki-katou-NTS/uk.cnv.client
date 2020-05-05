package nts.uk.ctx.bs.employee.dom.workplace.group;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;

import nts.arc.task.tran.AtomTask;
import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.bs.employee.dom.workplace.group.domainservice.DomainServiceHelper;
/**
 * 
 * @author phongtq
 *
 */
public class WorkplaceReplaceResultTest {
	AtomTask atomTask =  AtomTask.of(() -> {});
	@Test
	public void getters() {
		WorkplaceReplaceResult workplaceReplaceResult = DomainServiceHelper.getWorkplaceReplaceResultDefault(0);
		NtsAssert.invokeGetters(workplaceReplaceResult);
	}
	
	@Test
	public void getters1() {
		WorkplaceReplaceResult workplaceReplaceResult = new WorkplaceReplaceResult();
		NtsAssert.invokeGetters(workplaceReplaceResult);
	}
	
	
	@Test
	public void testAdd() {
		WorkplaceReplaceResult workplaceReplaceResult = DomainServiceHelper.getWorkplaceReplaceResultDefault(0);
		workplaceReplaceResult = WorkplaceReplaceResult.add(Optional.of(atomTask));
		assertThat(workplaceReplaceResult.getWorkplaceReplacement().equals(WorkplaceReplacement.ADD)).isTrue();
	}
	
	
	@Test
	public void testDelete () {
		WorkplaceReplaceResult workplaceReplaceResult = DomainServiceHelper.getWorkplaceReplaceResultDefault(1);
		workplaceReplaceResult = WorkplaceReplaceResult.delete(Optional.of(atomTask));
		assertThat(workplaceReplaceResult.getWorkplaceReplacement().equals(WorkplaceReplacement.DELETE)).isTrue();
	}
	
	@Test
	public void testAlreadyBelong () {
		WorkplaceReplaceResult workplaceReplaceResult = DomainServiceHelper.getWorkplaceReplaceResultDefault(2);
		workplaceReplaceResult = WorkplaceReplaceResult.alreadyBelong("01");
		assertThat(workplaceReplaceResult.getWorkplaceReplacement().equals(WorkplaceReplacement.ALREADY_BELONGED)).isTrue();
	}
	
	@Test
	public void testBelongAnother () {
		WorkplaceReplaceResult workplaceReplaceResult = DomainServiceHelper.getWorkplaceReplaceResultDefault(3);
		workplaceReplaceResult = WorkplaceReplaceResult.belongAnother("01");
		assertThat(workplaceReplaceResult.getWorkplaceReplacement().equals(WorkplaceReplacement.BELONGED_ANOTHER)).isTrue();
	}
}
