package nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.processten;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.adapter.employment.BsEmploymentHistoryImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.ShareEmploymentAdapter;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveComSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveEmSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveEmSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.sixtyhours.Com60HourVacation;
import nts.uk.ctx.at.shared.dom.vacation.setting.sixtyhours.Com60HourVacationRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacation;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacationRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.EmpSubstVacation;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.EmpSubstVacationRepository;

@Stateless
public class AbsenceTenProcessCommon {

	@Inject
	private CompensLeaveEmSetRepository compensLeaveEmSetRepository;

	@Inject
	private CompensLeaveComSetRepository compensLeaveComSetRepository;

	@Inject
	private ShareEmploymentAdapter shareEmploymentAdapter;

	@Inject
	private EmpSubstVacationRepository empSubstVacationRepository;

	@Inject
	private ComSubstVacationRepository comSubstVacationRepository;
	
	@Inject
    private Com60HourVacationRepository com60HourVacationRepo;

	/**
	 * 10-2.代休の設定を取得する
	 * 
	 * @param companyID
	 * @param employeeID
	 * @param baseDate
	 * @return
	 */
	public SubstitutionHolidayOutput getSettingForSubstituteHoliday(String companyID, String employeeID,
			GeneralDate baseDate) {
		RequireImp impl = new RequireImp(compensLeaveEmSetRepository, compensLeaveComSetRepository,
				shareEmploymentAdapter);
		return SettingSubstituteHolidayProcess.getSettingForSubstituteHoliday(impl, companyID, employeeID, baseDate);
	}

	/**
	 * 10-3.振休の設定を取得する
	 * 
	 * @param companyID
	 * @param employeeID
	 * @param baseDate
	 * @return
	 */
	public LeaveSetOutput getSetForLeave(String companyID, String employeeID, GeneralDate baseDate) {
		RequireImp impl = new RequireImp(shareEmploymentAdapter, empSubstVacationRepository,
				comSubstVacationRepository);
		return GetSettingCompensaLeave.process(impl, companyID, employeeID, baseDate);
	}
	
	/**
	 * 10-5.60H超休の設定を取得する
	 * 
	 * @param companyId
	 * @param employeeId
	 * @param baseDate
	 * @return
	 */
	public SixtyHourSettingOutput getSixtyHourSetting(String companyId, String employeeId, GeneralDate baseDate) {
	    Com60HourVacation com60HourVacation = com60HourVacationRepo.findById(companyId).orElse(null);
	    SixtyHourSettingOutput super60HLeaveMng = new SixtyHourSettingOutput(
                com60HourVacation != null && com60HourVacation.isManaged(),
                com60HourVacation == null ? null : com60HourVacation.getSetting().getDigestiveUnit().value
        );
	    
	    return super60HLeaveMng;
	}

	public class RequireImp implements GetSettingCompensaLeave.Require, SettingSubstituteHolidayProcess.Require {

		private CompensLeaveEmSetRepository compensLeaveEmSetRepository;

		private CompensLeaveComSetRepository compensLeaveComSetRepository;

		private ShareEmploymentAdapter shareEmploymentAdapter;

		private EmpSubstVacationRepository empSubstVacationRepository;

		private ComSubstVacationRepository comSubstVacationRepository;

		@Override
		public CompensatoryLeaveEmSetting findComLeavEmpSet(String companyId, String employmentCode) {
			return compensLeaveEmSetRepository.find(companyId, employmentCode);
		}

		@Override
		public CompensatoryLeaveComSetting findComLeavComSet(String companyId) {
			return compensLeaveComSetRepository.find(companyId);
		}

		@Override
		public Optional<BsEmploymentHistoryImport> findEmploymentHistory(String companyId, String employeeId,
				GeneralDate baseDate) {
			return shareEmploymentAdapter.findEmploymentHistory(companyId, employeeId, baseDate);
		}

		@Override
		public Optional<EmpSubstVacation> findEmpById(String companyId, String contractTypeCode) {
			return empSubstVacationRepository.findById(companyId, contractTypeCode);
		}

		@Override
		public Optional<ComSubstVacation> findComById(String companyId) {
			return comSubstVacationRepository.findById(companyId);
		}

		public RequireImp(CompensLeaveEmSetRepository compensLeaveEmSetRepository,
				CompensLeaveComSetRepository compensLeaveComSetRepository,
				ShareEmploymentAdapter shareEmploymentAdapter) {
			this.compensLeaveEmSetRepository = compensLeaveEmSetRepository;
			this.compensLeaveComSetRepository = compensLeaveComSetRepository;
			this.shareEmploymentAdapter = shareEmploymentAdapter;
		}

		public RequireImp(ShareEmploymentAdapter shareEmploymentAdapter,
				EmpSubstVacationRepository empSubstVacationRepository,
				ComSubstVacationRepository comSubstVacationRepository) {
			super();
			this.shareEmploymentAdapter = shareEmploymentAdapter;
			this.empSubstVacationRepository = empSubstVacationRepository;
			this.comSubstVacationRepository = comSubstVacationRepository;
		}

	}

}
