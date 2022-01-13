package nts.uk.ctx.at.function.dom.holidaysremaining;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.PublicHolidaySettingRepository;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHolidayRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveComSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.nursingleave.NursingCategory;
import nts.uk.ctx.at.shared.dom.vacation.setting.nursingleave.NursingLeaveSettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.EmploymentSettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.RetentionYearlySettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.sixtyhours.Com60HourVacationRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacationRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.EmpSubstVacationRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class VariousVacationControlService {

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
	@Inject
	private SpecialHolidayRepository specialHolidayFinder;
	@Inject
	private Com60HourVacationRepository com60HourVacationRepository;
	@Inject
	private EmploymentSettingRepository employmentSettingRepository;
	@Inject
	private PublicHolidaySettingRepository publicHolidaySettingRepository;

	@Inject
	private EmpSubstVacationRepository substVacationRepository;

	public VariousVacationControl getVariousVacationControl() {
		boolean annualHolidaySetting = false;
		boolean yearlyReservedSetting = false;
		boolean substituteHolidaySetting = false;
		boolean pauseItemHolidaySetting = false;
		boolean childNursingSetting = false;
		boolean nursingCareSetting = false;
		boolean com60HourVacationSetting = false;
		boolean publicHolidaySetting = false;
		boolean halfdayyearlySetting = false;
		boolean hourlyLeaveSetting = false;
		boolean pauseItemHolidaySettingCompany = false;

		String companyId = AppContexts.user().companyId();
		// 年休設定
		val annualPaidLeave = annualPaidLeaveSettingRepository.findByCompanyId(companyId);
		if (annualPaidLeave != null&& annualPaidLeave.getYearManageType() == ManageDistinct.YES ) {
			annualHolidaySetting = true;
		}
		if(annualPaidLeave!=null && annualPaidLeave.getManageAnnualSetting().getHalfDayManage().getManageType() == ManageDistinct.YES){
			halfdayyearlySetting = true;
		}
		if(annualPaidLeave!=null &&annualPaidLeave.getTimeSetting().getTimeManageType()== ManageDistinct.YES
				&& annualPaidLeave.getTimeSetting().getMaxYearDayLeave().manageType == ManageDistinct.YES){
			hourlyLeaveSetting = true;
		}

		// 積立年休設定
		val retentionYearly = retentionYearlySettingRepository.findByCompanyId(companyId);
		//val emptYearlyRetentionSetting = employmentSettingRepository.findAll(companyId);
		if ( retentionYearly.isPresent() && retentionYearly.get().getManagementCategory() == ManageDistinct.YES) {
			yearlyReservedSetting = true;
		}

		// 振休管理区分
		val substVacation = comSubstVacationRepository.findById(companyId);
		val empSubstVacation = substVacationRepository.findAll(companyId);
		if (!empSubstVacation.isEmpty()&& substVacation.isPresent()
				&& substVacation.get().getManageDistinct() == ManageDistinct.YES) {
			pauseItemHolidaySetting = true;
		}
		if ( substVacation.isPresent()&& substVacation.get().getManageDistinct() == ManageDistinct.YES) {
			pauseItemHolidaySettingCompany = true;
		}

		//ドメインモデル「60H超休管理設定」を取得する
		val com60HourVacation = com60HourVacationRepository.findById(companyId);
		if (com60HourVacation.isPresent() && com60HourVacation.get().getSetting().getIsManage() == ManageDistinct.YES) {
			com60HourVacationSetting = true;
		}
		//ドメインモデル「公休設定」を取得する
		val publicHoliday =  publicHolidaySettingRepository.get(companyId);

		if (publicHoliday.isPresent()&&publicHoliday.get().getIsManagePublicHoliday()== ManageDistinct.YES.value) {
			publicHolidaySetting = true;
		}

		// 代休管理区分
		val compLeaveCom = compensLeaveComSetRepository.find(companyId);
		if (compLeaveCom != null && compLeaveCom.getIsManaged() == ManageDistinct.YES) {
			substituteHolidaySetting = true;
		}

		val listNursingLeaveSetting = nursingLeaveSettingRepository.findByCompanyId(companyId);
		if (listNursingLeaveSetting != null ) {
			// 子の看護
			val childNursing = listNursingLeaveSetting.stream()
					.filter(i -> i.getNursingCategory() == NursingCategory.ChildNursing ).findFirst();
			if (childNursing.isPresent() && childNursing.get().getManageType() == ManageDistinct.YES) {
				childNursingSetting = true;
			}

			// 介護
			val nursingCare = listNursingLeaveSetting.stream()
					.filter(i -> i.getNursingCategory() == NursingCategory.Nursing).findFirst();
			if (nursingCare.isPresent() && nursingCare.get().getManageType() == ManageDistinct.YES) {
				nursingCareSetting = true;
			}
		}

		val listSpecialHoliday = specialHolidayFinder.findByCompanyIdWithTargetItem(companyId);

		return new VariousVacationControl(annualHolidaySetting, yearlyReservedSetting, substituteHolidaySetting,
				pauseItemHolidaySetting, childNursingSetting, nursingCareSetting,
				com60HourVacationSetting,publicHolidaySetting, halfdayyearlySetting,hourlyLeaveSetting,pauseItemHolidaySettingCompany,listSpecialHoliday);
	}
}
