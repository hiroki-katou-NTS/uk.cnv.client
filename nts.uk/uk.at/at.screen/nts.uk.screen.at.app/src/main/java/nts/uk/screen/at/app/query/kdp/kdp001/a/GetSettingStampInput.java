package nts.uk.screen.at.app.query.kdp.kdp001.a;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.app.find.stamp.management.personalengraving.dto.StampDataOfEmployeesDto;
import nts.uk.ctx.at.record.app.find.stamp.management.personalengraving.dto.StampResultDisplayDto;
import nts.uk.ctx.at.record.app.find.stamp.management.personalengraving.dto.StampToSuppressDto;
import nts.uk.ctx.at.record.dom.stamp.application.StampResultDisplay;
import nts.uk.ctx.at.record.dom.stamp.application.StampResultDisplayRepository;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCard;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCardRepository;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampDakokuRepository;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampMeans;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampRecord;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampRecordRepository;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.EmployeeStampInfo;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.GetListStampEmployeeService;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.GetStampTypeToSuppressService;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.StampToSuppress;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.PortalStampSettings;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.PortalStampSettingsRepository;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampSetPerRepository;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampSettingPerson;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.settingforsmartphone.SettingsSmartphoneStamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.settingforsmartphone.SettingsSmartphoneStampRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingCondition;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.service.WorkingConditionService;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSettingRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author sonnlb
 *         UKDesign.UniversalK.就業.KDP_打刻.KDP001_打刻入力(ポータル).A:打刻入力(ポータル).メニュー別OCD.打刻入力(ポータル)の設定を取得する
 *         UKDesign.UniversalK.就業.KDP_打刻.KDP001_打刻入力(ポータル).A:打刻入力(ポータル).メニュー別OCD.打刻入力(ポータル)を起動する
 */
@Stateless
public class GetSettingStampInput {

	@Inject
	private PortalStampSettingsRepository potalSettingRepo;
	
	@Inject
	private StampResultDisplayRepository stampResultRepo;

	@Inject
	private StampSetPerRepository stampSetPerRepo;

	@Inject
	private SettingsSmartphoneStampRepository settingsSmartphoneStampRepo;

	@Inject
	private PortalStampSettingsRepository portalStampSettingsrepo;

	@Inject
	private StampRecordRepository stampRecordRepo;

	@Inject
	private StampDakokuRepository stampRepo;

	@Inject
	private StampCardRepository stampCardRepo;
	
	@Inject
	private PredetemineTimeSettingRepository preRepo;
	
	@Inject
	private WorkingConditionRepository workingService;

	public SettingPotalStampInputDto getSettingPotalStampInput() {
		// get ログイン会社ID
		String comppanyID = AppContexts.user().companyId();
		Optional<PortalStampSettings> settingOpt = this.potalSettingRepo.get(comppanyID);
		SettingPotalStampInputDto result = new SettingPotalStampInputDto();

		if (!settingOpt.isPresent()) {

			return null;
		}

		result.setPortalStampSettings(PortalStampSettingsDto.fromDomain(settingOpt.get()));
		// 2:ログイン会社ID
		
		Optional<StampResultDisplay> stampResultOpt = this.stampResultRepo.getStampSet(comppanyID);
		
		if (stampResultOpt.isPresent()) {
			StampResultDisplayDto stampResultDto = new StampResultDisplayDto(stampResultOpt);
			result.setStampResultDisplayDto(stampResultDto);
		}
		
		// 3: 取得する(@Require, 社員ID, 年月日)
		String employeeId = AppContexts.user().employeeId();

		DatePeriod period = new DatePeriod(GeneralDate.today(), GeneralDate.today().addDays(-3));

		GetListStampEmployeeServiceRequireImpl empRequire = new GetListStampEmployeeServiceRequireImpl(stampRecordRepo,
				stampRepo, stampCardRepo);

		List<EmpInfoPotalStampDto> empInfos = new ArrayList<EmpInfoPotalStampDto>();
		period.datesBetween().forEach(date -> {
			EmpInfoPotalStampDto infoPotal = null;

			Optional<EmployeeStampInfo> empInfoOpt = GetListStampEmployeeService.get(empRequire, employeeId, date);

			if (empInfoOpt.isPresent()) {
				infoPotal = new EmpInfoPotalStampDto();
				infoPotal.setStampDataOfEmp(new StampDataOfEmployeesDto(empInfoOpt.get()));

			}
			StampToSuppress stampToSuppress = null;

			if (settingOpt.get().isButtonEmphasisArt()) {
				GetStampTypeToSuppressServiceRequireImpl stampTypeRequire = new GetStampTypeToSuppressServiceRequireImpl(
						stampSetPerRepo, settingsSmartphoneStampRepo, portalStampSettingsrepo, stampRecordRepo,
						stampRepo, stampCardRepo, preRepo, workingService);
				if (infoPotal == null) {
					infoPotal = new EmpInfoPotalStampDto();
				}

				stampToSuppress = GetStampTypeToSuppressService.get(stampTypeRequire, employeeId, StampMeans.PORTAL);

				infoPotal.setStampToSuppress(new StampToSuppressDto(stampToSuppress));
			}

			if (infoPotal != null) {
				empInfos.add(infoPotal);
			}
		});

		return result;
	}

	@AllArgsConstructor
	private class GetListStampEmployeeServiceRequireImpl implements GetListStampEmployeeService.Require {

		@Inject
		private StampRecordRepository stampRecordRepo;

		@Inject
		private StampDakokuRepository stampRepo;

		@Inject
		private StampCardRepository stampCardRepo;

		@Override
		public List<StampRecord> getStampRecord(List<StampNumber> stampNumbers, GeneralDate date) {
			return this.stampRecordRepo.get(AppContexts.user().contractCode(), stampNumbers, date);
		}

		@Override
		public List<Stamp> getStamp(List<StampNumber> stampNumbers, GeneralDate date) {
			String contractCode = AppContexts.user().contractCode();
			return this.stampRepo.get(contractCode, stampNumbers, date);
		}

		@Override
		public List<StampCard> getListStampCard(String sid) {
			return this.stampCardRepo.getListStampCard(sid);
		}

	}

	@AllArgsConstructor
	private class GetStampTypeToSuppressServiceRequireImpl 
		implements GetStampTypeToSuppressService.Require,
					WorkingConditionService.RequireM1{

		@Inject
		private StampSetPerRepository stampSetPerRepo;

		@Inject
		private SettingsSmartphoneStampRepository settingsSmartphoneStampRepo;

		@Inject
		private PortalStampSettingsRepository portalStampSettingsrepo;

		@Inject
		private StampRecordRepository stampRecordRepo;

		@Inject
		private StampDakokuRepository stampRepo;

		@Inject
		private StampCardRepository stampCardRepo;
		
		@Inject
		private PredetemineTimeSettingRepository preRepo;
		
		@Inject
		private WorkingConditionRepository workingConditionRepo;
		

		@Override
		public List<StampCard> getListStampCard(String sid) {
			return this.stampCardRepo.getListStampCard(sid);
		}

		@Override
		public List<StampRecord> getStampRecord(List<StampNumber> stampNumbers, GeneralDate date) {
			return this.stampRecordRepo.get(AppContexts.user().contractCode(), stampNumbers, date);
		}

		@Override
		public List<Stamp> getStamp(List<StampNumber> stampNumbers, GeneralDate date) {
			String contractCode = AppContexts.user().contractCode();
			return this.stampRepo.get(contractCode, stampNumbers, date);
		}
		@Override
		public Optional<WorkingConditionItem> findWorkConditionByEmployee(String employeeId, GeneralDate baseDate) {
			return WorkingConditionService.findWorkConditionByEmployee(this, employeeId, baseDate);
		}

		@Override
		public Optional<PredetemineTimeSetting> findByWorkTimeCode(String workTimeCode) {
			String companyId = AppContexts.user().companyId();
			return this.preRepo.findByWorkTimeCode(companyId, workTimeCode);
		}

		@Override
		public Optional<StampSettingPerson> getStampSet() {
			String companyId = AppContexts.user().companyId();
			return this.stampSetPerRepo.getStampSet(companyId);
		}

		@Override
		public Optional<SettingsSmartphoneStamp> getSettingsSmartphone() {
			String companyId = AppContexts.user().companyId();
			return this.settingsSmartphoneStampRepo.get(companyId);
		}

		@Override
		public Optional<PortalStampSettings> getPotalSettings() {
			String companyId = AppContexts.user().companyId();
			return this.portalStampSettingsrepo.get(companyId);
		}

		@Override
		public Optional<WorkingCondition> workingCondition(String companyId, String employeeId, GeneralDate baseDate) {
			return workingConditionRepo.getBySidAndStandardDate(companyId, employeeId, baseDate);
		}

		@Override
		public Optional<WorkingConditionItem> workingConditionItem(String historyId) {
			return workingConditionRepo.getWorkingConditionItem(historyId);
		}

		

	}

}
