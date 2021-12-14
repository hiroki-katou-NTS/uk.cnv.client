package nts.uk.screen.at.app.query.kdp.kdps01.a;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.stamp.application.StampResultDisplayRepository;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCard;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCardRepository;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampDakokuRepository;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampMeans;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampRecord;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampRecordRepository;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.GetStampTypeToSuppressService;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.PortalStampSettings;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.PortalStampSettingsRepository;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampSetPerRepository;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampSettingPerson;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.settingforsmartphone.SettingsSmartphoneStamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.settingforsmartphone.SettingsSmartphoneStampRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingCondition;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.service.WorkingConditionService;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSettingRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author sonnlb
 *         UKDesign.UniversalK.就業.KDP_打刻.KDPS01_打刻入力(スマホ).A:打刻入力(スマホ).メニュー別OCD.打刻入力(スマホ)を起動する
 */
@Stateless
public class GetSettingSmartPhone {

	@Inject
	private SettingsSmartphoneStampRepository settingRepo;

	@Inject
	private StampResultDisplayRepository resultRepo;

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
	private WorkingConditionRepository workingConditionRepository;
	@Inject
	private WorkingConditionItemRepository workingConditionItemRepository;

	public SettingSmartPhoneDto GetSetting() {

		SettingSmartPhoneDto result = new SettingSmartPhoneDto();

		String companyId = AppContexts.user().companyId();

		// 1 .get ログイン会社ID
		this.settingRepo.get(companyId).ifPresent(setting -> {
			result.setSetting(SettingsSmartphoneStampDto.fromDomain(setting));
		});

		// 2 .get ログイン会社ID
		this.resultRepo.getStampSet(companyId).ifPresent(resulDisplay -> {
			result.setResulDisplay(resulDisplay);
		});

		// 取得する(Require, 社員ID, 打刻手段)

		GetStampTypeToSuppressServiceRequireImpl require = new GetStampTypeToSuppressServiceRequireImpl();

		result.setStampToSuppress(
				GetStampTypeToSuppressService.get(require, AppContexts.user().employeeId(), StampMeans.SMART_PHONE));

		return result;
	}

	@AllArgsConstructor
	private class GetStampTypeToSuppressServiceRequireImpl implements GetStampTypeToSuppressService.Require,
		WorkingConditionService.RequireM1{


		@Override
		public List<StampCard> getListStampCard(String sid) {
			return stampCardRepo.getListStampCard(sid);
		}

		@Override
		public List<StampRecord> getStampRecord(List<StampNumber> stampNumbers, GeneralDate date) {
			return stampRecordRepo.get(AppContexts.user().contractCode(), stampNumbers, date);
		}

		@Override
		public List<Stamp> getStamp(List<StampNumber> stampNumbers, GeneralDate date) {
			String contractCode = AppContexts.user().contractCode();
			return stampRepo.get(contractCode, stampNumbers, date);
		}

		@Override
		public Optional<WorkingConditionItem> findWorkConditionByEmployee(String employeeId, GeneralDate baseDate) {
			return WorkingConditionService.findWorkConditionByEmployee(this, employeeId, baseDate);
		}

		@Override
		public Optional<PredetemineTimeSetting> findByWorkTimeCode(String workTimeCode) {
			String companyId = AppContexts.user().companyId();
			return preRepo.findByWorkTimeCode(companyId, workTimeCode);
		}

		@Override
		public Optional<StampSettingPerson> getStampSet() {
			String companyId = AppContexts.user().companyId();
			return stampSetPerRepo.getStampSet(companyId);
		}

		@Override
		public Optional<SettingsSmartphoneStamp> getSettingsSmartphone() {
			String companyId = AppContexts.user().companyId();
			return settingsSmartphoneStampRepo.get(companyId);
		}

		@Override
		public Optional<PortalStampSettings> getPotalSettings() {
			String companyId = AppContexts.user().companyId();
			return portalStampSettingsrepo.get(companyId);
		}

		@Override
		public Optional<WorkingCondition> workingCondition(String companyId, String employeeId, GeneralDate baseDate) {
			return workingConditionRepository.getBySidAndStandardDate(companyId, employeeId, baseDate);
		}

		@Override
		public Optional<WorkingConditionItem> workingConditionItem(String historyId) {
			return workingConditionItemRepository.getByHistoryId(historyId);
		}

	}

}
