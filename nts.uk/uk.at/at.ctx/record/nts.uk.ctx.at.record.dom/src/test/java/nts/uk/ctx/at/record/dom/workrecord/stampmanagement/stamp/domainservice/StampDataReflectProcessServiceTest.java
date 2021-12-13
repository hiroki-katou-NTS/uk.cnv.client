package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampHelper;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.RegisterStampData.Require;
/**
 * 
 * @author tutk
 *
 */
@RunWith(JMockit.class)
public class StampDataReflectProcessServiceTest {

	@Injectable
	private Require require;
	
	/**
	 * employeeId is null
	 */
	@Test
	public void testStampDataReflectProcessService_1() {
		Optional<String> employeeId = Optional.empty();
//		StampRecord stampRecord = StampRecordHelper.getStampRecord();//dummy
		Optional<Stamp> stamp = Optional.empty();//dummy
		
//		assertThat(StampDataReflectProcessService.reflect(require, "", employeeId, stampRecord, stamp).getReflectDate().isPresent()).isFalse();
	}
	
	/**
	 * stamp is null
	 */
	@Test
	public void testStampDataReflectProcessService_2() {
		Optional<String> employeeId = Optional.of("employeeId");//dummy
//		StampRecord stampRecord = StampRecordHelper.getStampRecord();//dummy
		Optional<Stamp> stamp = Optional.empty();
		
//		assertThat(StampDataReflectProcessService.reflect(require, "", employeeId, stampRecord, stamp).getReflectDate().isPresent()).isFalse();
	}
	/**
	 * employeeId not null
	 * stamp not null
	 */
//	@Test
//	public void testStampDataReflectProcessService_3() {
//		Optional<String> employeeId = Optional.of("employeeId");//dummy
//		StampRecord stampRecord = StampRecordHelper.getStampRecord();//dummy
//		Optional<Stamp> stamp = Optional.of(StampHelper.getStampDefault());
//		new Expectations() {
//			{
//				require.insert((StampRecord)any);
//				
//				require.insert((Stamp)any);
//			}
//		};
//		StampDataReflectResult stampDataReflectResult = StampDataReflectProcessService.reflect(require, "", employeeId, stampRecord, stamp);
//		assertThat(stampDataReflectResult.getReflectDate().isPresent()).isFalse();
//		NtsAssert.atomTask(
//				() -> stampDataReflectResult.getAtomTask(),
//				any -> require.insert((StampRecord) any.get()),
//				any -> require.insert((Stamp) any.get())
//				);
//	}
	/**
	 * employeeId not null
	 * stamp is null
	 */
	@Test
	public void testStampDataReflectProcessService_4() {
		Optional<String> employeeId = Optional.of("employeeId");//dummy
//		StampRecord stampRecord = StampRecordHelper.getStampRecord();//dummy
		Optional<Stamp> stamp = Optional.of(StampHelper.getStampDefault());
//		new Verifications(){
//			{
//				require.insert((StampRecord)any);
//				times = 0; 
//			}
//		};
//		StampDataReflectResult stampDataReflectResult = StampDataReflectProcessService.reflect(require, "", employeeId, stampRecord, stamp);
//		assertThat(stampDataReflectResult.getReflectDate().isPresent()).isFalse();
//		AtomTask persist = stampDataReflectResult.getAtomTask();
//		persist.run();
//		new Verifications() {
//			{
//				require.insert((StampRecord)any);
//				times = 1; 
//			}
//		};
	}

}
