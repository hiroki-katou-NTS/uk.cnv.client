package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampHelper;
/**
 * 
 * @author tutk
 *
 */
public class StampDataOfEmployeesTest {

	@Test
	public void getters() {
		StampDataOfEmployees stampDataOfEmployees = DomainServiceHeplper.getStampDataOfEmployeesDefault();
		NtsAssert.invokeGetters(stampDataOfEmployees);
	}
	
	@Test
	public void testStampDataOfEmployees() {
		String employeeId = "employee";//dummy
		GeneralDate date = GeneralDate.today();//dummy
//		List<StampRecord> listStampRecord = Arrays.asList(StampRecordHelper.getStampRecord());//dummy
		List<Stamp> listStamp =Arrays.asList(StampHelper.getStampDefault());//dummy
		StampDataOfEmployees stampDataOfEmployees = new StampDataOfEmployees(employeeId, date, listStamp);
		NtsAssert.invokeGetters(stampDataOfEmployees);
	}

}
