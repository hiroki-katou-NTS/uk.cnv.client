package nts.uk.screen.at.app.query.kdp.kdps01.a;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCard;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCardRepository;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampDakokuRepository;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampMeans;
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
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.service.WorkingConditionService;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSettingRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author sonnlb
 *
 *         UKDesign.UniversalK.就業.KDP_打刻.KDPS01_打刻入力(スマホ).A:打刻入力(スマホ).メニュー別OCD.打刻入力(スマホ)の打刻ボタンを抑制の表示をする
 */
@Stateless
public class SuppressingStampButton {

	@Inject
	private StampSetPerRepository stampSetPerRepo;

	@Inject
	private SettingsSmartphoneStampRepository settingsSmartphoneStampRepo;

	@Inject
	private PortalStampSettingsRepository portalStampSettingsrepo;

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

	public StampToSuppress getSuppressingStampButton() {
		// 1.取得する(Require, 社員ID, 打刻手段)

		GetStampTypeToSuppressServiceImpl require = new GetStampTypeToSuppressServiceImpl(stampSetPerRepo,
				settingsSmartphoneStampRepo, portalStampSettingsrepo, stampRepo, stampCardRepo,
				preRepo,workingConditionRepository ,workingConditionItemRepository);

		return GetStampTypeToSuppressService.get(require, AppContexts.user().employeeId(), StampMeans.SMART_PHONE);

	}

	@AllArgsConstructor
	private class GetStampTypeToSuppressServiceImpl implements GetStampTypeToSuppressService.Require , WorkingConditionService.RequireM1 {

		@Inject
		private StampSetPerRepository stampSetPerRepo;

		@Inject
		private SettingsSmartphoneStampRepository settingsSmartphoneStampRepo;

		@Inject
		private PortalStampSettingsRepository portalStampSettingsrepo;

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

		@Override
		public List<StampCard> getListStampCard(String sid) {
			return this.stampCardRepo.getListStampCard(sid);
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
			return workingConditionRepository.getBySidAndStandardDate(companyId, employeeId, baseDate);
		}

		@Override
		public Optional<WorkingConditionItem> workingConditionItem(String historyId) {
			return workingConditionItemRepository.getByHistoryId(historyId);
		}

	}
}
