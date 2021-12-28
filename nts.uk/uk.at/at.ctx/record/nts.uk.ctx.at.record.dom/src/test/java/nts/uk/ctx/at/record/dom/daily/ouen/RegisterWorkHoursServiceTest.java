package nts.uk.ctx.at.record.dom.daily.ouen;


import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.integration.junit4.JMockit;
import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateSetting;

/**
 * 
 * @author chungnt
 *
 */
@RunWith(JMockit.class)
public class RegisterWorkHoursServiceTest {

//	@Injectable
//	private RegisterWorkHoursService.Require require;
//	
//	@Injectable
//	private RegisterOuenWorkTimeOfDailyService.Require require2;
	
	private String empId = "empId";
	private GeneralDate ymd = GeneralDate.today();
	private String cid = "cid";
	private EditStateSetting editStateSetting = EnumAdaptor.valueOf(0, EditStateSetting.class);
	
	//if 作業詳細一覧.isNotPresent
	@Test
	public void test() {
//		
//		List<WorkDetailsParam> workDetailsParams = new ArrayList<>();
//		
//		new MockUp<RegisterOuenWorkTimeSheetOfDailyService>() {
//			@Mock
//			public AtomTask register(RegisterOuenWorkTimeSheetOfDailyService.Require require, String empId, GeneralDate ymd, List<OuenWorkTimeSheetOfDailyAttendance> ouenWorkTimeSheetOfDailys, EditStateSetting editStateSetting) {
//				return AtomTask.bundle(new ArrayList<>());
//			}
//		};
//		
//		ManHourInputResult result = RegisterWorkHoursService.register(require, cid, empId, ymd, editStateSetting, workDetailsParams);
//		
//		assertThat(result.getIntegrationOfDaily().isPresent()).isFalse();
//		
	}
//	
//	//if 作業詳細一覧.isNotPresent
//	@Test
//	public void test_1() {
//		
//		List<WorkDetailsParam> workDetailsParams = RegisterWorkHoursServiceHelper.get();
//		
//		new MockUp<RegisterOuenWorkTimeSheetOfDailyService>() {
//			@Mock
//			public AtomTask register(RegisterOuenWorkTimeSheetOfDailyService.Require require, String empId, GeneralDate ymd, List<OuenWorkTimeSheetOfDailyAttendance> ouenWorkTimeSheetOfDailys, EditStateSetting editStateSetting) {
//				return AtomTask.bundle(new ArrayList<>());
//			}
//		};
//		
//		ManHourInputResult result = RegisterWorkHoursService.register(require, cid, empId, ymd, editStateSetting, workDetailsParams);
//		
//		assertThat(result.getIntegrationOfDaily().isPresent()).isFalse();
//		
//	}
}
