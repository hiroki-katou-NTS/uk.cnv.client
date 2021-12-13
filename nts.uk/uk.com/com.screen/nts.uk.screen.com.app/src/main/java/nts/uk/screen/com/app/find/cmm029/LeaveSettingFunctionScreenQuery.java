package nts.uk.screen.com.app.find.cmm029;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveComSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.nursingleave.NursingCategory;
import nts.uk.ctx.at.shared.dom.vacation.setting.nursingleave.NursingLeaveSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.nursingleave.NursingLeaveSettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.RetentionYearlySetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.RetentionYearlySettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacation;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacationRepository;
import nts.uk.ctx.sys.portal.dom.standardmenu.StandardMenu;
import nts.uk.screen.com.app.find.cmm029.DisplayDataDto.DisplayDataDtoBuilder;

/**
 * UKDesign.UniversalK.共通.CMM_マスタメンテナンス.CMM029_機能の選択.A :機能の選択.メニュー別OCD.休暇の設定機能を取得する.休暇の設定機能を取得する
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class LeaveSettingFunctionScreenQuery extends AbstractFunctionScreenQuery {

	@Inject
	private AnnualPaidLeaveSettingRepository annualPaidLeaveSettingRepository;

	@Inject
	private RetentionYearlySettingRepository retentionYearlySettingRepository;

	@Inject
	private CompensLeaveComSetRepository compensLeaveComSetRepository;

	@Inject
	private ComSubstVacationRepository comSubstVacationRepository;

	@Inject
	private NursingLeaveSettingRepository nursingLeaveSettingRepository;
	
	@Override
	protected DisplayDataDto getMainDisplayData(List<StandardMenu> standardMenus) {
		return this.findFromStandardMenu(standardMenus, "CMM029_20", "CMM029_21").build();
	}

	@Override
	protected List<DisplayDataDto> getFunctionSettings(String cid, List<StandardMenu> standardMenus) {
		List<DisplayDataDto> datas = new ArrayList<>();
		// 2. 取得する(会社ID＝Input. 会社ID)
		datas.addAll(this.createFromAnnualPaidLeaveSetting(cid, standardMenus));
		// 3. 取得する(会社ID＝Input. 会社ID)
		datas.addAll(this.createFromRetentionYearlySetting(cid, standardMenus));
		// 4. 取得する(会社ID＝Input. 会社ID)
		datas.addAll(this.createFromCompensLeaveComSet(cid, standardMenus));
		// 5. 取得する(会社ID＝Input. 会社ID)
		datas.addAll(this.createFromComSubsVacation(cid, standardMenus));
		// 6. 取得する(会社ID＝Input. 会社ID)
		datas.addAll(this.createFromNursingLeaveSetting(cid, standardMenus));
		return datas;
	}

	private List<DisplayDataDto> createFromAnnualPaidLeaveSetting(String cid, List<StandardMenu> standardMenus) {
		AnnualPaidLeaveSetting domain = this.annualPaidLeaveSettingRepository.findByCompanyId(cid);
		DisplayDataDtoBuilder builder1 = this.findFromStandardMenu(standardMenus, "CMM029_20", "CMM029_23")
				.useAtr(domain != null ? domain.getYearManageType().equals(ManageDistinct.YES) : true)
				.programId("CMM029_22");
		DisplayDataDtoBuilder builder2 = DisplayDataDto.builder().system(SYSTEM_TYPE).programId("CMM029_26")
				.useAtr(domain != null ? domain.getTimeSetting().getTimeManageType().equals(ManageDistinct.YES) : true);
		return Arrays.asList(builder1.build(), builder2.build());
	}

	private List<DisplayDataDto> createFromRetentionYearlySetting(String cid, List<StandardMenu> standardMenus) {
		Optional<RetentionYearlySetting> optDomain = this.retentionYearlySettingRepository.findByCompanyId(cid);
		DisplayDataDtoBuilder builder = this.findFromStandardMenu(standardMenus, "CMM029_20", "CMM029_24")
				.useAtr(optDomain.map(data -> data.getManagementCategory().equals(ManageDistinct.YES)).orElse(true))
				.programId("CMM029_25");
		return Arrays.asList(builder.build());
	}

	private List<DisplayDataDto> createFromCompensLeaveComSet(String cid, List<StandardMenu> standardMenus) {
		CompensatoryLeaveComSetting domain = this.compensLeaveComSetRepository.find(cid);
		DisplayDataDtoBuilder builder1 = this.findFromStandardMenu(standardMenus, "CMM029_20", "CMM029_27")
				.useAtr(domain != null ? domain.isManaged() : true).programId("CMM029_28");
		DisplayDataDtoBuilder builder2 = DisplayDataDto.builder().system(SYSTEM_TYPE).programId("CMM029_29")
				.useAtr(domain != null
						? domain.getCompensatoryDigestiveTimeUnit().getIsManageByTime().equals(ManageDistinct.YES)
						: false);
		return Arrays.asList(builder1.build(), builder2.build());
	}

	private List<DisplayDataDto> createFromComSubsVacation(String cid, List<StandardMenu> standardMenus) {
		Optional<ComSubstVacation> optDomain = this.comSubstVacationRepository.findById(cid);
		DisplayDataDtoBuilder builder = this.findFromStandardMenu(standardMenus, "CMM029_20", "CMM029_30")
				.useAtr(optDomain.map(data -> data.getManageDistinct().equals(ManageDistinct.YES)).orElse(true))
				.programId("CMM029_31");
		return Arrays.asList(builder.build());
	}

	private List<DisplayDataDto> createFromNursingLeaveSetting(String cid, List<StandardMenu> standardMenus) {
		List<NursingLeaveSetting> domains = this.nursingLeaveSettingRepository.findByCompanyId(cid);
		Optional<NursingLeaveSetting> optNursing = domains.stream()
				.filter(data -> data.getNursingCategory().equals(NursingCategory.Nursing)).findFirst();
		Optional<NursingLeaveSetting> optChildNursing = domains.stream()
				.filter(data -> data.getNursingCategory().equals(NursingCategory.ChildNursing)).findFirst();
		DisplayDataDtoBuilder builder1 = this.findFromStandardMenu(standardMenus, "CMM029_20", "CMM029_32")
				.useAtr(optNursing.map(NursingLeaveSetting::isManaged).orElse(true)).programId("CMM029_33");
		DisplayDataDtoBuilder builder2 = DisplayDataDto.builder().system(SYSTEM_TYPE).programId("CMM029_34")
				.useAtr(optNursing
						.map(data -> data.getTimeCareNursingSetting().getManageDistinct().equals(ManageDistinct.YES))
						.orElse(true));
		DisplayDataDtoBuilder builder3 = this.findFromStandardMenu(standardMenus, "CMM029_20", "CMM029_32")
				.useAtr(optChildNursing.map(NursingLeaveSetting::isManaged).orElse(true)).programId("CMM029_35");
		DisplayDataDtoBuilder builder4 = DisplayDataDto.builder().system(SYSTEM_TYPE).programId("CMM029_36")
				.useAtr(optChildNursing
						.map(data -> data.getTimeCareNursingSetting().getManageDistinct().equals(ManageDistinct.YES))
						.orElse(true));
		return Arrays.asList(builder1.build(), builder2.build(), builder3.build(), builder4.build());
	}
}
