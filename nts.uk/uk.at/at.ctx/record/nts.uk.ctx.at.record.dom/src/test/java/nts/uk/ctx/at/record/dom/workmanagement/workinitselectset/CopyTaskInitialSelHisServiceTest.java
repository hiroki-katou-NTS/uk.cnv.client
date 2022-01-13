package nts.uk.ctx.at.record.dom.workmanagement.workinitselectset;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.task.tran.AtomTask;
import nts.arc.testing.assertion.NtsAssert;

/**
 * 
 * @author HieuLt
 *
 */

@RunWith(JMockit.class)
public class CopyTaskInitialSelHisServiceTest {

	@Injectable
	CopyTaskInitialSelHisService.Require require;

	/**
	 * require.作業初期選択履歴を取得する(複写元社員) is empty
	 */
	@Test
	public void copyTaskInitialSelHist() {
		String empCopy = "EmpCopy";
		String empMain = "EmpMain";
		new Expectations() {
			{
				require.getById(empCopy);
			}
		};

		NtsAssert.businessException("Msg_1185", () -> CopyTaskInitialSelHisService.copy(require, empCopy, empMain));
	}

	/**
	 * require.作業初期選択履歴を取得する(複写元社員) not empty
	 * require.作業初期選択履歴を取得する(複写先社員) is empty
	 */
	@Test
	public void copyTaskInitialSelHist1() {
		String empCopy = "EmpCopy";
		String empMain = "EmpMain";
		TaskInitialSelHist itemCopy = new TaskInitialSelHist(empCopy, new ArrayList<>());
		new Expectations() {
			{
				require.getById(empCopy);
				result = Optional.of(itemCopy);
				
				require.getById(anyString);
			}
		};
		
		AtomTask result = CopyTaskInitialSelHisService.copy(require, empCopy, empMain);
		NtsAssert.atomTask(
				() -> result,
				any -> require.insert((TaskInitialSelHist) any.get())
		);
	}
	
	/**
	 * require.作業初期選択履歴を取得する(複写元社員) not empty
	 * require.作業初期選択履歴を取得する(複写先社員) not empty
	 */
	@Test
	public void copyTaskInitialSelHist2() {
		String empCopy = "EmpCopy";
		String empMain = "EmpMain";
		TaskInitialSelHist itemCopy = new TaskInitialSelHist(empCopy, new ArrayList<>());
		TaskInitialSelHist itemMain = new TaskInitialSelHist(empCopy, new ArrayList<>());
		new Expectations() {
			{
				require.getById(empCopy);
				result = Optional.of(itemCopy);
				
				require.getById(anyString);
				result = Optional.of(itemMain);
			}
		};
		
		AtomTask result = CopyTaskInitialSelHisService.copy(require, empCopy, empMain);
		NtsAssert.atomTask(
				() -> result,
				any -> require.delete((String) any.get()),
				any -> require.insert((TaskInitialSelHist) any.get())
		);
	}
	

}
