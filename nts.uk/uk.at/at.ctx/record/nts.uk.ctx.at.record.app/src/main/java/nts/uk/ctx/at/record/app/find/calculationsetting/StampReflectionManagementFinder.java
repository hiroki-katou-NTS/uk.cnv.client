package nts.uk.ctx.at.record.app.find.calculationsetting;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.app.find.workrecord.goout.OutManageDto;
import nts.uk.ctx.at.record.app.find.workrecord.temporarywork.ManageWorkTemporaryDto;
import nts.uk.ctx.at.record.dom.workrecord.goout.OutManageRepository;
import nts.uk.ctx.at.record.dom.workrecord.temporarywork.ManageWorkTemporaryRepository;
import nts.uk.ctx.at.shared.dom.calculationsetting.StampReflectionManagement;
import nts.uk.ctx.at.shared.dom.calculationsetting.repository.StampReflectionManagementRepository;
import nts.uk.ctx.at.shared.dom.workrule.workuse.TemporaryWorkUseManageRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Class StampReflectionManagementFinder.
 * UKDesign.UniversalK.就業.KDW_日別実績.KMK_計算マスタ.KMK013_計算設定(Calculation setting).KMK013_計算設定（New）.H：日別実績の作成　詳細設定.アルゴリズム.起動処理.起動処理
 *
 * @author phongtq
 */
@Stateless
public class StampReflectionManagementFinder {

	/** The repository. */
	@Inject
	private StampReflectionManagementRepository repository;
	
	@Inject
	private OutManageRepository outManageRepository;
	
	@Inject
	private TemporaryWorkUseManageRepository temporaryWorkUseManageRepository;
	
	@Inject
	private ManageWorkTemporaryRepository manageWorkTemporaryRepository;

	/**
	 * Find by code.
	 *
	 * @return the stamp reflection management dto
	 */
	public StampReflectionManagementDto findByCode() {
		String companyId = AppContexts.user().companyId();
		Optional<StampReflectionManagement> optional = repository.findByCid(companyId);
		if (!optional.isPresent()) {
			return null;
		}
		StampReflectionManagementDto managementDto = new StampReflectionManagementDto(optional.get().getCompanyId(),
				optional.get().getBreakSwitchClass().value, optional.get().getAutoStampReflectionClass().value,
				optional.get().getActualStampOfPriorityClass().value, optional.get().getReflectWorkingTimeClass().value,
				optional.get().getGoBackOutCorrectionClass().value, 
				optional.get().getAutoStampForFutureDayClass().value
				);
		return managementDto;
	}

	public UsageDataDto findUsageData() {
		String cid = AppContexts.user().companyId();
		OutManageDto outManage = this.outManageRepository.findByID(cid)
				.map(data -> new OutManageDto(data.getMaxUsage().v(), data.getInitValueReasonGoOut().value)).orElse(null);
		int tempWorkUseManageAtr = this.temporaryWorkUseManageRepository.findByCid(cid)
				.map(data -> data.getUseClassification().value).orElse(0);
		ManageWorkTemporaryDto tempWorkManage = this.manageWorkTemporaryRepository.findByCID(cid)
				.map(data -> new ManageWorkTemporaryDto(data.getMaxUsage().v(), data.getTimeTreatTemporarySame().v()))
				.orElse(null);
		return new UsageDataDto(outManage, tempWorkUseManageAtr, tempWorkManage);
	}
}
