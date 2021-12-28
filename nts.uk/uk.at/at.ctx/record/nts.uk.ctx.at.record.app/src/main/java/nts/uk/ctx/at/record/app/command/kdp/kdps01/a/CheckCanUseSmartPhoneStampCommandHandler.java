package nts.uk.ctx.at.record.app.command.kdp.kdps01.a;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.adapter.employee.EmployeeDataMngInfoImport;
import nts.uk.ctx.at.record.dom.adapter.employee.EmployeeRecordAdapter;
import nts.uk.ctx.at.record.dom.adapter.employmentinfoterminal.infoterminal.EmpDataImport;
import nts.uk.ctx.at.record.dom.adapter.employmentinfoterminal.infoterminal.GetMngInfoFromEmpIDListAdapter;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.ExecutionTypeDaily;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyoneday.createdailyresults.CreateDailyResults;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults.OutputCreateDailyOneDay;
import nts.uk.ctx.at.record.dom.dailyresultcreationprocess.creationprocess.creationclass.dailywork.TemporarilyReflectStampDailyAttd;
import nts.uk.ctx.at.record.dom.stamp.application.SettingsUsingEmbossing;
import nts.uk.ctx.at.record.dom.stamp.application.SettingsUsingEmbossingRepository;
import nts.uk.ctx.at.record.dom.stamp.card.stamcardedit.StampCardEditing;
import nts.uk.ctx.at.record.dom.stamp.card.stamcardedit.StampCardEditingRepo;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCard;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCardCreateResult;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCardRepository;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampDakokuRepository;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampMeans;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.CanEngravingUsed;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.MakeUseJudgmentResults;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.StampFunctionAvailableService;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockAtr;
import nts.uk.ctx.at.shared.dom.adapter.holidaymanagement.CompanyAdapter;
import nts.uk.ctx.at.shared.dom.adapter.holidaymanagement.CompanyImport622;
import nts.uk.ctx.at.shared.dom.dailyattdcal.converter.DailyRecordShareFinder;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.ChangeDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.algorithmdailyper.OutputTimeReflectForWorkinfo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.algorithmdailyper.StampReflectRangeOutput;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.algorithmdailyper.TimeReflectFromWorkinfo;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrorMessageInfo;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author sonnlb
 *         UKDesign.UniversalK.就業.KDP_打刻.KDPS01_打刻入力(スマホ).A:打刻入力(スマホ).メニュー別OCD.打刻入力(スマホ)の実行可能判定を行う
 */

@Stateless
public class CheckCanUseSmartPhoneStampCommandHandler extends CommandHandlerWithResult<CheckCanUseSmartPhoneStampCommand, CheckCanUseSmartPhoneStampResult> {


	@Inject
	private SettingsUsingEmbossingRepository stampUsageRepo;

	@Inject
	private StampCardRepository stampCardRepo;

	@Inject
	private StampCardEditingRepo stampCardEditRepo;

	@Inject
	private CompanyAdapter companyAdapter;

	@Inject
	private EmployeeRecordAdapter sysEmpPub;

	@Inject
	private StampDakokuRepository stampDakokuRepo;

	@Inject
	private TemporarilyReflectStampDailyAttd temporarilyReflectStampDailyAttd;
	
	@Inject
	private GetMngInfoFromEmpIDListAdapter getMngInfoFromEmpIDListAdapter;
	
	@Inject
	private DailyRecordShareFinder dailyRecordShareFinder;
	
	@Inject
	private CreateDailyResults createDailyResults;
	
	@Inject
	private TimeReflectFromWorkinfo timeReflectFromWorkinfo;
	
	@Override
	protected CheckCanUseSmartPhoneStampResult handle(CommandHandlerContext<CheckCanUseSmartPhoneStampCommand> context) {

		StampFunctionAvailableServiceRequireImpl require = new StampFunctionAvailableServiceRequireImpl(stampUsageRepo,
				stampCardRepo, stampCardEditRepo, companyAdapter, sysEmpPub, stampDakokuRepo,
				temporarilyReflectStampDailyAttd, getMngInfoFromEmpIDListAdapter, dailyRecordShareFinder,
				createDailyResults, timeReflectFromWorkinfo);

		String employeeId = AppContexts.user().employeeId();
		// 2.1 判断する(@Require, 社員ID, 打刻手段)
		MakeUseJudgmentResults jugResult = StampFunctionAvailableService.decide(require, employeeId, StampMeans.SMART_PHONE);
		// not 打刻カード作成結果 empty
		Optional<StampCardCreateResult> cradResultOpt = jugResult.getCardResult();
		
		cradResultOpt.ifPresent(x -> {
			x.getAtomTask().ifPresent(atom -> {
				transaction.execute(() -> {
					atom.run();
				});
			});
		});
		
		Optional<String> cardNumber = cradResultOpt.map(x -> x.getCardNumber());
		
		// EA3833
		CanEngravingUsed used = jugResult.getUsed();
		
		// 打刻機能利用不可
		if (used.equals(CanEngravingUsed.NOT_PURCHASED_STAMPING_OPTION)) {
			throw new BusinessException("Msg_1644");
		}
		
		// 打刻カード未登録
		if (used.equals(CanEngravingUsed.UNREGISTERED_STAMP_CARD)) {
			throw new BusinessException("Msg_1619");
		}
		
		return new CheckCanUseSmartPhoneStampResult(cardNumber.isPresent() ? cardNumber.get() : null, used.value);
	}

	@AllArgsConstructor
	private class StampFunctionAvailableServiceRequireImpl implements StampFunctionAvailableService.Require {
		
		@Inject
		private SettingsUsingEmbossingRepository stampUsageRepo;

		@Inject
		private StampCardRepository stampCardRepo;

		@Inject
		private StampCardEditingRepo stampCardEditRepo;

		@Inject
		private CompanyAdapter companyAdapter;

		@Inject
		private EmployeeRecordAdapter sysEmpPub;

		@Inject
		private StampDakokuRepository stampDakokuRepo;

		private TemporarilyReflectStampDailyAttd temporarilyReflectStampDailyAttd;
		
		private GetMngInfoFromEmpIDListAdapter getMngInfoFromEmpIDListAdapter;
		
		private DailyRecordShareFinder dailyRecordShareFinder;
		
		private CreateDailyResults createDailyResults;
		
		private TimeReflectFromWorkinfo timeReflectFromWorkinfo;
		
		@Override
		public List<EmployeeDataMngInfoImport> findBySidNotDel(List<String> sids) {
			return this.sysEmpPub.findBySidNotDel(sids);
		}

		@Override
		public Optional<CompanyImport622> getCompanyNotAbolitionByCid(String cid) {
			return this.companyAdapter.getCompanyNotAbolitionByCid(cid);
		}

		@Override
		public Optional<StampCardEditing> get(String companyId) {
			return Optional.ofNullable(this.stampCardEditRepo.get(companyId));
		}

		@Override
		public void add(StampCard domain) {
			this.stampCardRepo.add(domain);
		}

		@Override
		public List<StampCard> getListStampCard(String sid) {
			return this.stampCardRepo.getListStampCard(sid);
		}

		@Override
		public Optional<SettingsUsingEmbossing> get() {
			return this.stampUsageRepo.get(AppContexts.user().companyId());
		}

		@Override
		public void insert(Stamp stamp) {
			this.stampDakokuRepo.insert(stamp);
		}

		@Override
		public Optional<StampCard> getByCardNoAndContractCode(String stampNumber, String contractCode) {
			return this.stampCardRepo.getByCardNoAndContractCode(stampNumber, contractCode);
		}

		@Override
		public Optional<StampCard> getByCardNoAndContractCode(ContractCode contractCode, StampNumber stampNumber) {
			return stampCardRepo.getByCardNoAndContractCode(stampNumber.v(), contractCode.v());
		}

		@Override
		public List<EmpDataImport> getEmpData(List<String> empIDList) {
			return getMngInfoFromEmpIDListAdapter.getEmpData(empIDList);
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
		
		@Override
		public boolean existsStamp(ContractCode contractCode, StampNumber stampNumber, GeneralDateTime dateTime,
				ChangeClockAtr changeClockArt) {
			return stampDakokuRepo.existsStamp(contractCode, stampNumber, dateTime, changeClockArt);
		}
	}

}
