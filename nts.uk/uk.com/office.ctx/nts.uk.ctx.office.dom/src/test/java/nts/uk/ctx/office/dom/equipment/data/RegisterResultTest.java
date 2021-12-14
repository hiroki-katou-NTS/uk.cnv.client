package nts.uk.ctx.office.dom.equipment.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.integration.junit4.JMockit;
import nts.arc.error.I18NErrorMessage;
import nts.arc.i18n.I18NText;
import nts.arc.task.tran.AtomTask;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentItemNo;
import nts.uk.ctx.office.dom.equipment.achievement.ErrorItem;

@RunWith(JMockit.class)
public class RegisterResultTest {

	/**
	 * [C-1] エラーありで作成する
	 */
	@Test
	public void testConstructorWithError() {
		// given
		List<ErrorItem> errorItems = Arrays.asList(
				new ErrorItem(new EquipmentItemNo("1"), new I18NErrorMessage(I18NText.main("error1").build())),
				new ErrorItem(new EquipmentItemNo("5"), new I18NErrorMessage(I18NText.main("error2").build())));
		
		// when
		RegisterResult result = RegisterResult.withErrors(errorItems);
		
		// then
		assertThat(result.isHasError()).isTrue();
		assertThat(result.getErrorItems()).isEqualTo(errorItems);
	}
	
	/**
	 * [C-2] エラーなしで作成する	
	 */
	@Test
	public void testConstructorWithoutError() {
		// given
		AtomTask task = AtomTask.of(() -> this.doSomething());
		
		// when
		RegisterResult result = RegisterResult.withoutErrors(task);
		
		// then
		assertThat(result.isHasError()).isFalse();
		assertThat(result.getErrorItems()).isEmpty();
	}
	
	private void doSomething() {
		
	}
}
