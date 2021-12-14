package nts.uk.screen.at.app.command.kdp.kdp001.a;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.auth.dom.adapter.login.IGetInfoForLogin;
import nts.uk.ctx.at.record.dom.adapter.employee.EmployeeDataMngInfoImport;
import nts.uk.ctx.at.record.dom.adapter.employee.EmployeeRecordAdapter;
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
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.repo.EmpInfoTerminalRepository;
import nts.uk.ctx.at.record.dom.stamp.card.stamcardedit.StampCardEditing;
import nts.uk.ctx.at.record.dom.stamp.card.stamcardedit.StampCardEditingRepo;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCard;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCardRepository;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.RefectActualResult;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampDakokuRepository;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampRecord;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampRecordRepository;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.EnterStampFromPortalService;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.TimeStampInputResult;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonPositionNo;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.PortalStampSettings;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.PortalStampSettingsRepository;
import nts.uk.ctx.at.shared.dom.adapter.employment.BsEmploymentHistoryImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.ShareEmploymentAdapter;
import nts.uk.ctx.at.shared.dom.adapter.holidaymanagement.CompanyAdapter;
import nts.uk.ctx.at.shared.dom.adapter.holidaymanagement.CompanyImport622;
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
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.context.loginuser.LoginUserContextManager;

/**
 * 
 * @author sonnlb
 *         UKDesign.UniversalK.就業.KDP_打刻.KDP001_打刻入力(ポータル).A:打刻入力(ポータル).メニュー別OCD.打刻入力(ポータル)を登録する
 *
 */
@Stateless
public class RegisterStampInputCommandHandler
		extends CommandHandlerWithResult<RegisterStampInputCommand, RegisterStampInputResult> {

	@Inject
	private PortalStampSettingsRepository settingRepo;

	@Inject
	private StampCardRepository stampCardRepo;

	@Inject
	private EmployeeRecordAdapter sysEmpPub;

	@Inject
	private CompanyAdapter companyAdapter;

	@Inject
	private StampRecordRepository stampRecordRepo;

	@Inject
	private StampDakokuRepository stampDakokuRepo;

	@Inject
	private StampCardEditingRepo stampCardEditRepo;

	@Inject
	private EmpInfoTerminalRepository empInfoTerminalRepository;

	@Inject
	private EmployeeManageRCAdapter employeeManageRCAdapter;

	@Inject
	private DailyRecordAdUpService dailyRecordAdUpService;
	
	@Inject
	private GetMngInfoFromEmpIDListAdapter getMngInfoFromEmpIDListAdapter;
	
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
    private TemporarilyReflectStampDailyAttd temporarilyReflectStampDailyAttd;
    
    @Inject
    private ShareEmploymentAdapter shareEmploymentAdapter;
    
    @Inject
    private AttendanceItemConvertFactory attendanceItemConvertFactory;
    
    @Inject
    private ICorrectionAttendanceRule iCorrectionAttendanceRule;
    
    @Inject
	private InterimRemainDataMngRegisterDateChange interimRemainDataMngRegisterDateChange;
    
    @Inject
	private CreateDailyResults createDailyResults;
    
    @Inject
    private DailyRecordShareFinder dailyRecordShareFinder;
    
    @Inject
    private TimeReflectFromWorkinfo timeReflectFromWorkinfo;

	@Override
	protected RegisterStampInputResult handle(CommandHandlerContext<RegisterStampInputCommand> context) {
		RegisterStampInputCommand cmd = context.getCommand();

		EnterStampFromPortalServiceRequireImpl require = new EnterStampFromPortalServiceRequireImpl(settingRepo,
				stampCardRepo, sysEmpPub, companyAdapter, stampRecordRepo, stampDakokuRepo, stampCardEditRepo,
				empInfoTerminalRepository, stampCardRepo, employeeManageRCAdapter, dailyRecordAdUpService,
				getMngInfoFromEmpIDListAdapter, iGetInfoForLogin, loginUserContextManager, calcService, closureRepo,
				closureEmploymentRepo, shareEmploymentAdapter, attendanceItemConvertFactory, iCorrectionAttendanceRule,
				interimRemainDataMngRegisterDateChange, temporarilyReflectStampDailyAttd, createDailyResults,
				dailyRecordShareFinder, timeReflectFromWorkinfo);

		ContractCode contractCode = new ContractCode(AppContexts.user().contractCode());

		String employeeID = AppContexts.user().employeeId();

		GeneralDateTime datetime = cmd.getDatetime();

		ButtonPositionNo buttonPositionNo = new ButtonPositionNo(cmd.getButtonPositionNo());

		RefectActualResult refActualResults = cmd.getRefActualResults().toDomainValue();

		// 作成する(@Require, 契約コード, 社員ID, 日時, 打刻ボタン位置NO, 実績への反映内容)

		TimeStampInputResult inputResult = EnterStampFromPortalService.create(require, AppContexts.user().companyId(),
				contractCode, employeeID, datetime, buttonPositionNo, refActualResults);

		if (inputResult == null) {
			return null;
		}

		// not 打刻入力結果 empty
		Optional<AtomTask> atomInput = inputResult.getAt();

		if (atomInput.isPresent()) {
			transaction.execute(() -> {
				atomInput.get().run();
			});
		}

		if (inputResult.getStampDataReflectResult() == null) {
			return null;
		}

		// not 打刻データ反映処理結果 empty
		AtomTask atomResult = inputResult.getStampDataReflectResult().getAtomTask();

		transaction.execute(() -> {
			atomResult.run();
		});

		Optional<GeneralDate> refDateOpt = inputResult.getStampDataReflectResult().getReflectDate();

		return new RegisterStampInputResult(refDateOpt.isPresent() ? refDateOpt.get() : null);
	}

	@AllArgsConstructor
	private class EnterStampFromPortalServiceRequireImpl implements EnterStampFromPortalService.Require {
		@Inject
		private PortalStampSettingsRepository settingRepo;

		@Inject
		private StampCardRepository stampCardRepo;

		@Inject
		private EmployeeRecordAdapter sysEmpPub;

		@Inject
		private CompanyAdapter companyAdapter;

		@Inject
		private StampRecordRepository stampRecordRepo;

		@Inject
		private StampDakokuRepository stampDakokuRepo;

		@Inject
		private StampCardEditingRepo stampCardEditRepo;

		private EmpInfoTerminalRepository empInfoTerminalRepository;

		private StampCardRepository stampCardRepository;

		private EmployeeManageRCAdapter employeeManageRCAdapter;

		private DailyRecordAdUpService dailyRecordAdUpService;

		private GetMngInfoFromEmpIDListAdapter getMngInfoFromEmpIDListAdapter;

		private IGetInfoForLogin iGetInfoForLogin;

		private LoginUserContextManager loginUserContextManager;

		private CalculateDailyRecordServiceCenter calcService;

		private ClosureRepository closureRepo;

		private ClosureEmploymentRepository closureEmploymentRepo;

		private ShareEmploymentAdapter shareEmploymentAdapter;
		
		private AttendanceItemConvertFactory attendanceItemConvertFactory;
	    
	    private ICorrectionAttendanceRule iCorrectionAttendanceRule;
	    
	    private InterimRemainDataMngRegisterDateChange interimRemainDataMngRegisterDateChange;
	    
	    private TemporarilyReflectStampDailyAttd temporarilyReflectStampDailyAttd;
	    
	    private CreateDailyResults createDailyResults;
	    
	    private DailyRecordShareFinder dailyRecordShareFinder;
	    
	    private TimeReflectFromWorkinfo timeReflectFromWorkinfo;

		@Override
		public List<StampCard> getLstStampCardBySidAndContractCd(String sid) {
			return this.stampCardRepo.getLstStampCardBySidAndContractCd(AppContexts.user().contractCode(), sid);
		}

		@Override
		public List<EmployeeDataMngInfoImport> findBySidNotDel(List<String> sids) {
			return this.sysEmpPub.findBySidNotDel(sids);
		}

		@Override
		public Optional<CompanyImport622> getCompanyNotAbolitionByCid(String cid) {
			return this.companyAdapter.getCompanyNotAbolitionByCid(cid);
		}

		@Override
		public void add(StampCard domain) {
			this.stampCardRepo.add(domain);
		}

		@Override
		public void insert(StampRecord stampRecord) {
			this.stampRecordRepo.insert(stampRecord);
		}

		@Override
		public void insert(Stamp stamp) {
			this.stampDakokuRepo.insert(stamp);
		}
		
		@Override
		public Optional<PortalStampSettings> getPortalStampSetting() {
			return this.settingRepo.get(AppContexts.user().companyId());
		}

		@Override
		public Optional<StampCard> getByCardNoAndContractCode(String stampNumber, String contractCode) {
			return this.stampCardRepo.getByCardNoAndContractCode(stampNumber, contractCode);
		}

		@Override
		public Optional<StampCardEditing> get(String companyId) {

			StampCardEditing stampCardEdit = this.stampCardEditRepo.get(companyId);
			return Optional.ofNullable(stampCardEdit);
		}

		public Optional<StampRecord> getStampRecord(ContractCode contractCode, StampNumber stampNumber,
				GeneralDateTime dateTime) {
			return stampRecordRepo.get(contractCode.v(), stampNumber.v(), dateTime);
		}

		@Override
		public Optional<EmpInfoTerminal> getEmpInfoTerminal(EmpInfoTerminalCode empInfoTerCode,
				ContractCode contractCode) {
			return empInfoTerminalRepository.getEmpInfoTerminal(empInfoTerCode, contractCode);
		}

		@Override
		public Optional<StampCard> getByCardNoAndContractCode(ContractCode contractCode, StampNumber stampNumber) {
			return stampCardRepository.getByCardNoAndContractCode(stampNumber.v(), contractCode.v());
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
			loginUserContextManager.loggedInAsEmployee(userId, personId, contractCode, companyId, companyCode,
					employeeId, employeeCode);
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
			return stampDakokuRepo.existsStamp(contractCode, stampNumber, dateTime, changeClockArt);
		}
		
		@Override
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
		public OutputTimeReflectForWorkinfo get(String companyId, String employeeId, GeneralDate ymd,
				WorkInfoOfDailyAttendance workInformation) {
			return timeReflectFromWorkinfo.get(companyId, employeeId, ymd, workInformation);
		}

		@Override
		public List<ErrorMessageInfo> reflectStamp(String companyId, Stamp stamp,
				StampReflectRangeOutput stampReflectRangeOutput, IntegrationOfDaily integrationOfDaily,
				ChangeDailyAttendance changeDailyAtt) {
			return temporarilyReflectStampDailyAttd.reflectStamp(companyId, stamp, stampReflectRangeOutput, integrationOfDaily, changeDailyAtt);
		}
	}

}