package nts.uk.ctx.at.function.dom.holidaysremaining;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHolidayRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveComSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.nursingleave.NursingCategory;
import nts.uk.ctx.at.shared.dom.vacation.setting.nursingleave.NursingLeaveSettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.RetentionYearlySettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacationRepository;
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

	public VariousVacationControl getVariousVacationControl() {
		boolean annualHolidaySetting = false;
		boolean yearlyReservedSetting = false;
		boolean substituteHolidaySetting = false;
		boolean pauseItemHolidaySetting = false;
		boolean childNursingSetting = false;
		boolean nursingCareSetting = false;
		String companyId = AppContexts.user().companyId();
		// 年休設定
		val annualPaidLeave = annualPaidLeaveSettingRepository.findByCompanyId(companyId);
		if (annualPaidLeave != null && annualPaidLeave.getYearManageType() == ManageDistinct.YES) {
			annualHolidaySetting = true;
		}

		// 積立年休設定
		val retentionYearly = retentionYearlySettingRepository.findByCompanyId(companyId);
		if (retentionYearly.isPresent() && retentionYearly.get().getManagementCategory() == ManageDistinct.YES) {
			yearlyReservedSetting = true;
		}

		// 代休管理区分
		val compLeaveCom = compensLeaveComSetRepository.find(companyId);
		if (compLeaveCom != null && compLeaveCom.getIsManaged() == ManageDistinct.YES) {
			substituteHolidaySetting = true;
		}

		// 振休管理区分
		val substVacation = comSubstVacationRepository.findById(companyId);
		if (substVacation.isPresent() && substVacation.get().getManageDistinct() == ManageDistinct.YES) {
			pauseItemHolidaySetting = true;
		}
		val listNursingLeaveSetting = nursingLeaveSettingRepository.findByCompanyId(companyId);
		if (listNursingLeaveSetting != null) {
			// 子の看護
			val childNursing = listNursingLeaveSetting.stream()
					.filter(i -> i.getNursingCategory() == NursingCategory.ChildNursing).findFirst();
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

		val listSpecialHoliday = specialHolidayFinder.findByCompanyId(companyId);

		return new VariousVacationControl(annualHolidaySetting, yearlyReservedSetting, substituteHolidaySetting,
				pauseItemHolidaySetting, childNursingSetting, nursingCareSetting, listSpecialHoliday);
	}
}
