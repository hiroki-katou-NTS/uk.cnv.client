package nts.uk.screen.com.app.find.cmm029;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.entranceexit.ManageEntryExit;
import nts.uk.ctx.at.shared.dom.entranceexit.ManageEntryExitRepository;
import nts.uk.ctx.at.shared.dom.workmanagementmultiple.UseATR;
import nts.uk.ctx.at.shared.dom.workmanagementmultiple.WorkManagementMultiple;
import nts.uk.ctx.at.shared.dom.workmanagementmultiple.WorkManagementMultipleRepository;
import nts.uk.ctx.at.shared.dom.workrule.deformed.AggDeformedLaborSetting;
import nts.uk.ctx.at.shared.dom.workrule.deformed.AggDeformedLaborSettingRepository;
import nts.uk.ctx.at.shared.dom.workrule.workform.FlexWorkMntSetRepository;
import nts.uk.ctx.at.shared.dom.workrule.workform.FlexWorkSet;
import nts.uk.ctx.at.shared.dom.workrule.workuse.TemporaryWorkUseManage;
import nts.uk.ctx.at.shared.dom.workrule.workuse.TemporaryWorkUseManageRepository;
import nts.uk.ctx.sys.portal.dom.standardmenu.StandardMenu;

/**
 * UKDesign.UniversalK.共通.CMM_マスタメンテナンス.CMM029_機能の選択.A :機能の選択.メニュー別OCD.勤務の設定機能を取得する.勤務の設定機能を取得する
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class WorkSettingFunctionScreenQuery extends AbstractFunctionScreenQuery {

	@Inject
	private FlexWorkMntSetRepository flexWorkSettingRepository;

	@Inject
	private AggDeformedLaborSettingRepository aggDeformedLaborSettingRepository;

	@Inject
	private WorkManagementMultipleRepository workManagementMultipleRepository;

	@Inject
	private TemporaryWorkUseManageRepository tempWorkUseManageRepository;

	@Inject
	private ManageEntryExitRepository manageEntryExitRepository;

	@Override
	protected DisplayDataDto getMainDisplayData(List<StandardMenu> standardMenus) {
		return this.findFromStandardMenu(standardMenus, "CMM029_13", "CMM029_14").build();
	}

	@Override
	protected List<DisplayDataDto> getFunctionSettings(String cid, List<StandardMenu> standardMenus) {
		List<DisplayDataDto> datas = new ArrayList<>();
		// 2. フレックス勤務の設定を取得する(会社ID＝Input. 会社ID)
		Optional<FlexWorkSet> optFlexWorkSet = this.flexWorkSettingRepository.find(cid);
		datas.add(DisplayDataDto.builder().system(SYSTEM_TYPE).programId("CMM029_15")
				.useAtr(optFlexWorkSet.map(data -> data.getUseFlexWorkSetting().isUse()).orElse(false)).build());
		// 3. 変形労働の集計設定を取得する(会社ID＝Input. 会社ID)
		Optional<AggDeformedLaborSetting> optLaborSetting = this.aggDeformedLaborSettingRepository.findByCid(cid);
		datas.add(DisplayDataDto.builder().system(SYSTEM_TYPE).programId("CMM029_16")
				.useAtr(optLaborSetting.map(data -> data.getUseDeformedLabor().isUse()).orElse(false)).build());
		// 4. 複数回勤務管理を取得する(会社ID＝Input. 会社ID)
		Optional<WorkManagementMultiple> optManageMulti = this.workManagementMultipleRepository.findByCode(cid);
		datas.add(DisplayDataDto.builder().system(SYSTEM_TYPE).programId("CMM029_17")
				.useAtr(optManageMulti.map(data -> data.getUseATR().equals(UseATR.use)).orElse(false)).build());
		// 5. 臨時勤務利用管理を取得する(会社ID＝Input. 会社ID)
		Optional<TemporaryWorkUseManage> optTempWorkUse = this.tempWorkUseManageRepository.findByCid(cid);
		datas.add(DisplayDataDto.builder().system(SYSTEM_TYPE).programId("CMM029_18")
				.useAtr(optTempWorkUse.map(data -> data.getUseClassification().isUse()).orElse(false)).build());
		// 6. 入退門管理を取得する(会社ID＝Input. 会社ID)
		Optional<ManageEntryExit> optEntryExit = this.manageEntryExitRepository.findByID(cid);
		datas.add(DisplayDataDto.builder().system(SYSTEM_TYPE).programId("CMM029_19")
				.useAtr(optEntryExit.map(data -> data.getUseClassification().isUse()).orElse(false)).build());
		return datas;
	}
}
