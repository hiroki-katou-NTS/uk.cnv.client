package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.adapter.employmentinfoterminal.infoterminal.EmpDataImport;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.ExecutionTypeDaily;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults.OutputCreateDailyOneDay;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCard;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampHelper;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.ChangeDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.algorithmdailyper.EndStatus;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.algorithmdailyper.OutputTimeReflectForWorkinfo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.algorithmdailyper.StampReflectRangeOutput;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrorMessageInfo;

/**
 * 
 * @author tutk
 *
 */

@RunWith(JMockit.class)
public class ReflectDataStampDailyServiceTest {

	String employeeId = "employeeId";
	String cid = "cid";
	GeneralDate date = GeneralDate.today();
	List<ErrorMessageInfo> errorMessageInfos = new ArrayList<>();
	ReflectDataStampDailyServiceHelper helper = new ReflectDataStampDailyServiceHelper();

	ChangeDailyAttendance changeDailyAtt = new ChangeDailyAttendance(true, true, true, true,
			ScheduleRecordClassifi.RECORD, true);

	OutputTimeReflectForWorkinfo info = new OutputTimeReflectForWorkinfo();

	// Test all date is error
	@Test
	public void testNotCard(@Mocked ReflectDataStampDailyService.Require require) {

		Stamp stamp = StampHelper.getStampDefault();

		new Expectations() {
			{
				require.getByCardNoAndContractCode((ContractCode) any, (StampNumber) any);
				result = Optional.empty();
			}
		};

		Optional<InfoReflectDestStamp> optional = ReflectDataStampDailyService.getJudgment(require, stamp);

		assertThat(optional.isPresent()).isFalse();

	}

	// Test all date is error
	// 反映対象日がない
	@Test
	public void test_all_date_is_error(@Mocked ReflectDataStampDailyService.Require require) {

		Stamp stamp = StampHelper.getStampDefault();

		new Expectations() {
			{
				require.getByCardNoAndContractCode((ContractCode) any, (StampNumber) any);
				result = Optional.of(new StampCard(null, null, employeeId));
			}
		};

		Optional<InfoReflectDestStamp> optional = ReflectDataStampDailyService.getJudgment(require, stamp);

		assertThat(optional.isPresent()).isFalse();

	}

	// Test all date is not error and not date is true
	@Test
	public void test2() {

		// GeneralDate.ymd(2021, 03, 15)
		Stamp stamp = StampHelper.getStampDefault();
		Optional<InfoReflectDestStamp> optional = ReflectDataStampDailyService.getJudgment(new RequireImpl(), stamp);

		assertThat(optional.isPresent()).isTrue();

		assertThat(optional.get().getDate()).isEqualTo(GeneralDate.ymd(2021, 03, 15));
	}

	public class RequireImpl implements ReflectDataStampDailyService.Require{

		@Override
		public Optional<IntegrationOfDaily> find(String employeeId, GeneralDate date) {
			return Optional.empty();
		}

		@Override
		public Optional<OutputCreateDailyOneDay> createDailyResult(String companyId, String employeeId, GeneralDate ymd,
				ExecutionTypeDaily executionType) {
			return Optional.of(new OutputCreateDailyOneDay(new ArrayList<>(), createDefault(employeeId, ymd), new ArrayList<>()));
		}

		@Override
		public OutputTimeReflectForWorkinfo get(String companyId, String employeeId, GeneralDate ymd,
				WorkInfoOfDailyAttendance workInformation) {
			if(ymd.before(GeneralDate.ymd(2021, 03, 15))) {
				return new OutputTimeReflectForWorkinfo(EndStatus.NO_HOLIDAY_SETTING, new StampReflectRangeOutput(), new ArrayList<>());
			}
			return new OutputTimeReflectForWorkinfo(EndStatus.NORMAL, new StampReflectRangeOutput(), new ArrayList<>());
		}

		@Override
		public List<ErrorMessageInfo> reflectStamp(String companyId, Stamp stamp,
				StampReflectRangeOutput stampReflectRangeOutput, IntegrationOfDaily integrationOfDaily,
				ChangeDailyAttendance changeDailyAtt) {
			stamp.getImprintReflectionStatus().markAsReflected(GeneralDate.ymd(2021, 03, 15));
			return new ArrayList<ErrorMessageInfo>();
		}

		@Override
		public Optional<StampCard> getByCardNoAndContractCode(ContractCode contractCode, StampNumber stampNumber) {
			return Optional.of(new StampCard("", "", employeeId));
		}

		@Override
		public List<EmpDataImport> getEmpData(List<String> empIDList) {
			return Arrays.asList(new EmpDataImport(cid, "", employeeId, "", Optional.empty()));
		}
		
	}
	
	private IntegrationOfDaily createDefault(String sid, GeneralDate dateData) {
		return new IntegrationOfDaily(
				sid,
				dateData,
				null, 
				null, 
				null,
				Optional.empty(), 
				new ArrayList<>(), 
				Optional.empty(), 
				new BreakTimeOfDailyAttd(), 
				Optional.empty(), 
				Optional.empty(), 
				Optional.empty(), 
				Optional.empty(), 
				Optional.empty(), 
				Optional.empty(), 
				new ArrayList<>(),
				Optional.empty(),
				new ArrayList<>(),
				new ArrayList<>(),
				new ArrayList<>(),
				Optional.empty());
	}
}