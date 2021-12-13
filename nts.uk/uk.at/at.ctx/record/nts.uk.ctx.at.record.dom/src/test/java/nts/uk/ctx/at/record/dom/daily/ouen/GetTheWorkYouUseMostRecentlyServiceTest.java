package nts.uk.ctx.at.record.dom.daily.ouen;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkGroup;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkGroup.Require;

/**
 * 
 * @author chungnt
 *
 */

@RunWith(JMockit.class)
public class GetTheWorkYouUseMostRecentlyServiceTest {

	@Injectable
	private GetTheWorkYouUseMostRecentlyService.Require require;

	private String empId = "empId";
	private DatePeriod targetPeriod = new DatePeriod(GeneralDate.today().addMonths(-1), GeneralDate.today());
	private List<OuenWorkTimeSheetOfDaily> list = GetTheWorkYouUseMostRecentlyServiceHelper.get();

	// $実績作業一覧 isEmpty
	@Test
	public void test_1() {

		new Expectations() {
			{
				require.findOuenWorkTimeSheetOfDaily(empId, targetPeriod);
			}
		};

		List<WorkGroup> result = GetTheWorkYouUseMostRecentlyService.get(require, empId);
		assertThat(result.isEmpty()).isTrue();
	}

	@Test
	public void test_2() {

		new Expectations() {
			{
				require.findOuenWorkTimeSheetOfDaily(empId, targetPeriod);
				result = list;
			}
		};
		
		new MockUp<WorkGroup>() {
			@Mock
			public boolean checkWorkContents(Require require) {
				return true;

			}
		};

		List<WorkGroup> result = GetTheWorkYouUseMostRecentlyService.get(require, empId);
		assertThat(result.isEmpty()).isFalse();
		assertThat(result.size()).isEqualTo(10);
		assertThat(result.get(5).getWorkCD1().v()).isEqualTo("000060");
		assertThat(result.get(5).getWorkCD2().get().v()).isEqualTo("000061");
		assertThat(result.get(5).getWorkCD3().get().v()).isEqualTo("000062");
		assertThat(result.get(5).getWorkCD4().get().v()).isEqualTo("000063");
		assertThat(result.get(5).getWorkCD5().get().v()).isEqualTo("000064");
	}

}
