package nts.uk.ctx.at.record.pubimp.employmentinfoterminal.infoterminal;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.auth.dom.adapter.login.IGetInfoForLogin;
import nts.uk.ctx.at.record.dom.adapter.employeemanage.EmployeeManageRCAdapter;
import nts.uk.ctx.at.record.dom.adapter.employmentinfoterminal.infoterminal.EmpDataImport;
import nts.uk.ctx.at.record.dom.adapter.employmentinfoterminal.infoterminal.GetMngInfoFromEmpIDListAdapter;
import nts.uk.ctx.at.record.dom.daily.DailyRecordAdUpService;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.ExecutionTypeDaily;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyoneday.createdailyresults.CreateDailyResults;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults.OutputCreateDailyOneDay;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.CalculateDailyRecordServiceCenter;
import nts.uk.ctx.at.record.dom.dailyresultcreationprocess.creationprocess.creationclass.dailywork.TemporarilyReflectStampDailyAttd;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.EmpInfoTerminal;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.EmpInfoTerminalCode;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.receive.StampReceptionData;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.receive.StampReceptionData.StampDataBuilder;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.repo.EmpInfoTerminalRepository;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.service.ConvertTimeRecordStampService;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCard;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCardRepository;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampDakokuRepository;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampRecord;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampRecordRepository;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.StampDataReflectResult;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockArt;
import nts.uk.ctx.at.record.pub.employmentinfoterminal.infoterminal.ConvertTimeRecordStampPub;
import nts.uk.ctx.at.record.pub.employmentinfoterminal.infoterminal.StampDataReflectResultExport;
import nts.uk.ctx.at.record.pub.employmentinfoterminal.infoterminal.StampReceptionDataExport;
import nts.uk.ctx.at.shared.dom.adapter.employment.BsEmploymentHistoryImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.ShareEmploymentAdapter;
import nts.uk.ctx.at.shared.dom.adapter.holidaymanagement.CompanyAdapter;
import nts.uk.ctx.at.shared.dom.adapter.holidaymanagement.CompanyInfo;
import nts.uk.ctx.at.shared.dom.dailyattdcal.converter.DailyRecordShareFinder;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainDataMngRegisterDateChange;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.service.AttendanceItemConvertFactory;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.ChangeDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.ICorrectionAttendanceRule;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.algorithmdailyper.OutputTimeReflectForWorkinfo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.algorithmdailyper.StampReflectRangeOutput;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.algorithmdailyper.TimeReflectFromWorkinfo;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrorMessageInfo;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionType;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmployment;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.shr.com.context.loginuser.LoginUserContextManager;

/**
 * @author ThanhNX
 *
 */
@Stateless
public class ConvertTimeRecordStampPubImpl implements ConvertTimeRecordStampPub {

	@Inject
	private EmpInfoTerminalRepository empInfoTerminalRepository;

	@Inject
	private StampDakokuRepository stampDakokuRepository;

	@Inject
	private StampRecordRepository stampRecordRepository;

	@Inject
	private StampCardRepository stampCardRepository;

	@Inject
	private EmployeeManageRCAdapter employeeManageRCAdapter;

	@Inject
	private CreateDailyResults createDailyResults;
	
	@Inject
	private TemporarilyReflectStampDailyAttd temporarilyReflectStampDailyAttd;
	
	@Inject
	private DailyRecordAdUpService dailyRecordAdUpService;
	
	@Inject
	private GetMngInfoFromEmpIDListAdapter getMngInfoFromEmpIDListAdapter;
	
	@Inject
	private CompanyAdapter companyAdapter;
	
	@Inject
	private IGetInfoForLogin iGetInfoForLogin;
	
	@Inject
	private LoginUserContextManager loginUserContextManager;
	
    @Inject
    private CalculateDailyRecordServiceCenter calcService;
    
    @Inject
    private ClosureRepository closureRepo;

    @Inject
	 private ClosureEmploymentRepository closureEmploymentRepo;

    @Inject
    private ShareEmploymentAdapter shareEmploymentAdapter;
    
    @Inject
	private AttendanceItemConvertFactory attendanceItemConvertFactory;
    
    @Inject
    private ICorrectionAttendanceRule iCorrectionAttendanceRule;
    
    @Inject
    private InterimRemainDataMngRegisterDateChange interimRemainDataMngRegisterDateChange;
    
    @Inject
    private DailyRecordShareFinder dailyRecordShareFinder;
    
    @Inject
    private TimeReflectFromWorkinfo timeReflectFromWorkinfo;
    
	@Override
	public  Optional<StampDataReflectResultExport> convertData(String empInfoTerCode,
			String contractCode, StampReceptionDataExport stampReceptData) {

		RequireImpl require = new RequireImpl(empInfoTerminalRepository,
				stampDakokuRepository, stampRecordRepository, stampCardRepository,
				employeeManageRCAdapter, createDailyResults, temporarilyReflectStampDailyAttd,
				dailyRecordAdUpService, getMngInfoFromEmpIDListAdapter, companyAdapter, iGetInfoForLogin, loginUserContextManager,
				calcService, closureRepo, closureEmploymentRepo, shareEmploymentAdapter, attendanceItemConvertFactory, iCorrectionAttendanceRule,
				interimRemainDataMngRegisterDateChange, dailyRecordShareFinder, timeReflectFromWorkinfo);

		Optional<StampDataReflectResult> convertDataOpt = ConvertTimeRecordStampService
				.convertData(require, new EmpInfoTerminalCode(empInfoTerCode), new ContractCode(contractCode),
						new StampReceptionData(
								new StampDataBuilder(stampReceptData.getIdNumber(), stampReceptData.getCardCategory(),
										stampReceptData.getShift(), stampReceptData.getLeavingCategory(),
										stampReceptData.getYmd(), stampReceptData.getSupportCode())
												.overTimeHours(stampReceptData.getOverTimeHours())
												.midnightTime(stampReceptData.getMidnightTime())
												.time(stampReceptData.getTime())));
		return convertDataOpt.map(x -> (new StampDataReflectResultExport(x.getReflectDate(),
								x.getAtomTask())));
	}

	@AllArgsConstructor
	public static class RequireImpl implements ConvertTimeRecordStampService.Require {

		private final EmpInfoTerminalRepository empInfoTerminalRepository;

		private final StampDakokuRepository stampDakokuRepository;

		private final StampRecordRepository stampRecordRepository;

		private final StampCardRepository stampCardRepository;

		private final EmployeeManageRCAdapter employeeManageRCAdapter;

		private CreateDailyResults createDailyResults;

		private TemporarilyReflectStampDailyAttd temporarilyReflectStampDailyAttd;
		
		private DailyRecordAdUpService dailyRecordAdUpService;
		
		private GetMngInfoFromEmpIDListAdapter getMngInfoFromEmpIDListAdapter;
		
		private CompanyAdapter companyAdapter;
		
		private IGetInfoForLogin iGetInfoForLogin;
		
		private LoginUserContextManager loginUserContextManager;
		
		 private CalculateDailyRecordServiceCenter calcService;
		 
		 private ClosureRepository closureRepo;

		 private ClosureEmploymentRepository closureEmploymentRepo;
		 
		 private ShareEmploymentAdapter shareEmploymentAdapter;
		 
		 private AttendanceItemConvertFactory attendanceItemConvertFactory;
		
		 private ICorrectionAttendanceRule iCorrectionAttendanceRule;
		 
		 private InterimRemainDataMngRegisterDateChange interimRemainDataMngRegisterDateChange;
		 
		 private DailyRecordShareFinder dailyRecordShareFinder;

		 private TimeReflectFromWorkinfo timeReflectFromWorkinfo;
		 
		@Override
		public Optional<EmpInfoTerminal> getEmpInfoTerminal(EmpInfoTerminalCode empInfoTerCode,
				ContractCode contractCode) {
			return empInfoTerminalRepository.getEmpInfoTerminal(empInfoTerCode, contractCode);
		}

		@Override
		public Optional<StampRecord> getStampRecord(ContractCode contractCode, StampNumber stampNumber,
				GeneralDateTime dateTime) {
			return stampRecordRepository.get(contractCode.v(), stampNumber.v(), dateTime);
		}

		@Override
		public void insert(StampRecord stampRecord) {
			stampRecordRepository.insert(stampRecord);

		}

		@Override
		public Optional<StampCard> getByCardNoAndContractCode(ContractCode contractCode, StampNumber stampNumber) {
			return stampCardRepository.getByCardNoAndContractCode(stampNumber.v(), contractCode.v());
		}

		@Override
		public void insert(Stamp stamp) {

			stampDakokuRepository.insert(stamp);
		}
		
		@Override
		public List<String> getListEmpID(String companyID, GeneralDate referenceDate) {
			return employeeManageRCAdapter.getListEmpID(companyID, referenceDate);
		}

		@Override
		public void addAllDomain(IntegrationOfDaily domain) {
			dailyRecordAdUpService.addAllDomain(domain);
		}

		@Override
		public List<EmpDataImport> getEmpData(List<String> empIDList) {
			return getMngInfoFromEmpIDListAdapter.getEmpData(empIDList);
		}

		@Override
		public CompanyInfo getCompanyInfoById(String companyId) {
			return companyAdapter.getCompanyInfoById(companyId);
		}

		@Override
		public Optional<String> getUserIdFromLoginId(String perId) {
			return iGetInfoForLogin.getUserIdFromLoginId(perId);
		}

		@Override
		public void loggedInAsEmployee(String userId, String personId, String contractCode, String companyId,
				String companyCode, String employeeId, String employeeCode) {
			loginUserContextManager.loggedInAsEmployee(userId, personId, contractCode, companyId, companyCode, employeeId, employeeCode);
		}

		@Override
		public List<IntegrationOfDaily> calculatePassCompanySetting(String cid,
				List<IntegrationOfDaily> integrationOfDaily, ExecutionType reCalcAtr) {
			return calcService.calculatePassCompanySetting(integrationOfDaily, Optional.empty(), reCalcAtr);
		}

		@Override
		public void loggedOut() {
			loginUserContextManager.loggedOut();
		}

		@Override
		public Optional<BsEmploymentHistoryImport> employmentHistory(CacheCarrier cacheCarrier, String companyId,
				String employeeId, GeneralDate baseDate) {
			return shareEmploymentAdapter.findEmploymentHistoryRequire(cacheCarrier, companyId, employeeId, baseDate);
		}

		@Override
		public Optional<ClosureEmployment> employmentClosure(String companyID, String employmentCD) {
			return closureEmploymentRepo.findByEmploymentCD(companyID, employmentCD);
		}

		@Override
		public Optional<Closure> closure(String companyId, int closureId) {
			return closureRepo.findById(companyId, closureId);
		}

		@Override
		public boolean existsStamp(ContractCode contractCode, StampNumber stampNumber, GeneralDateTime dateTime,
				ChangeClockArt changeClockArt) {
			return stampDakokuRepository.existsStamp(contractCode, stampNumber, dateTime, changeClockArt);
		}
		
		public Map<String, BsEmploymentHistoryImport> employmentHistoryClones(String companyId, List<String> employeeId,
				GeneralDate baseDate) {
			return shareEmploymentAdapter.findEmpHistoryVer2(companyId, employeeId, baseDate);
		}

		@Override
		public List<ClosureEmployment> employmentClosureClones(String companyID, List<String> employmentCD) {
			return closureEmploymentRepo.findListEmployment(companyID, employmentCD);
		}

		@Override
		public List<Closure> closureClones(String companyId, List<Integer> closureId) {
			return closureRepo.findByListId(companyId, closureId);
		}

		@Override
		public DailyRecordToAttendanceItemConverter createDailyConverter() {
			return attendanceItemConvertFactory.createDailyConverter();
		}
		
		@Override
		public IntegrationOfDaily restoreData(DailyRecordToAttendanceItemConverter converter,
				IntegrationOfDaily integrationOfDaily, List<ItemValue> listItemValue) {
			return createDailyResults.restoreData(converter, integrationOfDaily, listItemValue);
		}
		
		@Override
		public IntegrationOfDaily process(IntegrationOfDaily domainDaily, ChangeDailyAttendance changeAtt) {
			return iCorrectionAttendanceRule.process(domainDaily, changeAtt);
		}
		
		@Override
		public void registerDateChange(String cid, String sid, List<GeneralDate> lstDate) {
			interimRemainDataMngRegisterDateChange.registerDateChange(cid, sid, lstDate);
		}

		@Override
		public Optional<IntegrationOfDaily> find(String employeeId, GeneralDate date) {
			return dailyRecordShareFinder.find(employeeId, date);
		}

		@Override
		public Optional<OutputCreateDailyOneDay> createDailyResult(String companyId, String employeeId, GeneralDate ymd,
				ExecutionTypeDaily executionType) {
			return createDailyResults.createDailyResult(companyId, employeeId, ymd, executionType);
		}

		@Override
		public List<ErrorMessageInfo> reflectStamp(String companyId, Stamp stamp,
				StampReflectRangeOutput stampReflectRangeOutput, IntegrationOfDaily integrationOfDaily,
				ChangeDailyAttendance changeDailyAtt) {
			return temporarilyReflectStampDailyAttd.reflectStamp(companyId, stamp, stampReflectRangeOutput, integrationOfDaily, changeDailyAtt);
		}

		@Override
		public OutputTimeReflectForWorkinfo get(String companyId, String employeeId, GeneralDate ymd,
				WorkInfoOfDailyAttendance workInformation) {
			return timeReflectFromWorkinfo.get(companyId, employeeId, ymd, workInformation);
		}
	}
}