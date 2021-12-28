package nts.uk.screen.at.app.ktgwidget.ktg004;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.function.dom.adapter.widgetKtg.AnnualLeaveRemainingNumberImport;
import nts.uk.ctx.at.function.dom.adapter.widgetKtg.OptionalWidgetAdapter;
import nts.uk.ctx.at.record.dom.remainingnumber.specialleave.empinfo.grantremainingdata.ComplileInPeriodOfSpecialLeaveParam;
import nts.uk.ctx.at.record.dom.remainingnumber.specialleave.empinfo.grantremainingdata.InPeriodOfSpecialLeaveResultInfor;
import nts.uk.ctx.at.record.dom.remainingnumber.specialleave.export.SpecialLeaveManagementService;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmpEmployeeAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.BsEmploymentHistoryImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.EmploymentHistShareImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.ShareEmploymentAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employment.SharedSidPeriodDateEmploymentImport;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeOfExistMinus;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.AbsenceReruitmentMngInPeriodQuery;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimAbsMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimRecAbasMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimRecMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.BreakDayOffMngInPeriodQuery;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimBreakDayOffMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimBreakMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimDayOffMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutManagementData;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutManagementDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutSubofHDManaRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutSubofHDManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.SubstitutionOfHDManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.SubstitutionOfHDManagementData;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.ComDayOffManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.CompensatoryDayOffManaData;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveComDayOffManaRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveComDayOffManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveManagementData;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHoliday;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHolidayRepository;
/*import nts.uk.ctx.at.shared.pub.remainingnumber.nursingcareleavemanagement.interimdata.NursingMode;
import nts.uk.ctx.at.shared.pub.remainingnumber.nursingcareleavemanagement.interimdata.ShNursingLeaveSettingPub;*/
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveComSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveEmSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveEmSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacation;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacationRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.EmpSubstVacation;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.EmpSubstVacationRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmployment;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.UseClassification;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class GetTheNumberOfVacationsLeft {

	@Inject
	private OptionalWidgetAdapter optionalWidgetAdapter;

	@Inject
	private RecordDomRequireService requireService;

	@Inject
	private LeaveComDayOffManaRepository leaveComDayOffManaRepo;

	@Inject
	private PayoutSubofHDManaRepository payoutHdManaRepo;

	@Inject
    private CompensLeaveComSetRepository compensLeaveComSetRepo;

    @Inject
    private ComDayOffManaDataRepository comDayOffManaDataRepo;
    
    @Inject
    private LeaveManaDataRepository leaveManaDataRepo;
    
    @Inject
    private ShareEmploymentAdapter shareEmploymentAdapter;
    
    @Inject
    private CompensLeaveEmSetRepository compensLeaveEmSetRepo;
    
    @Inject
    private InterimBreakDayOffMngRepository interimBreakDayOffMngRepo;
    
    @Inject
    private ClosureEmploymentRepository closureEmploymentRepo;
    
    @Inject
    private ClosureRepository closureRepo;
    
    @Inject
    private EmpEmployeeAdapter empEmployeeAdapter;
    
    @Inject
    private SubstitutionOfHDManaDataRepository substitutionOfHDManaDataRepo;
    
    @Inject
    private PayoutSubofHDManaRepository payoutSubofHDManaRepo;
    
    @Inject
    private PayoutManagementDataRepository payoutManagementDataRepo;
    
    @Inject
    private EmpSubstVacationRepository empSubstVacationRepo;
    
    @Inject
    private ComSubstVacationRepository comSubstVacationRepo;
    
    @Inject
    private InterimRecAbasMngRepository interimRecAbasMngRepo;
	

	/*
	 * @Inject private ShNursingLeaveSettingPub shNursingLeaveSettingPub;
	 */

	@Inject
	private SpecialHolidayRepository specialHolidayRepository;

	/**
	 * @name 15.年休残数表示
	 * @param employeeId 社員ID
	 * @param datePeriod 基準日　（システム日付）
	 * @return
	 */
	public AnnualLeaveRemainingNumberImport annualLeaveResidualNumberIndication(String employeeId, GeneralDate date) {
		//※「基準日時点の年休残数を取得する」はRequestList198
		return optionalWidgetAdapter.getReferDateAnnualLeaveRemainNumber(employeeId, date).getAnnualLeaveRemainNumberImport();
	}

	/**
	 * @name 16.積立年休残数表示
	 * @param employeeId 社員ID
	 * @param datePeriod 基準日　　（基準日終了日）
	 * @return
	 */
	public Double numberOfAccumulatedAnnualLeave(String employeeId, GeneralDate date) {
		//※「基準日時点の積立年休残数を取得する」はRequestList201
		return optionalWidgetAdapter.getNumberOfReservedYearsRemain(employeeId, date).getRemainingDays();
	}

	/**
	 * @name 18.代休残数表示
	 * @param employeeId 社員ID
	 * @param datePeriod 基準日
	 * @return
	 */
	public Double remainingNumberOfSubstituteHolidays(String employeeId, GeneralDate date) {
		//RQ#505　BreakDayOffMngInPeriodQuery.getBreakDayOffMngRemain
		
	    RequireM11Imp requireM11Imp = new RequireM11Imp(
	    		comDayOffManaDataRepo,
	    		leaveComDayOffManaRepo,
	    		leaveManaDataRepo,
	    		shareEmploymentAdapter,
	    		compensLeaveEmSetRepo,
	    		compensLeaveComSetRepo,
	    		interimBreakDayOffMngRepo,
	    		closureEmploymentRepo,
	    		closureRepo,
	    		empEmployeeAdapter,
	    		substitutionOfHDManaDataRepo,
	    		payoutSubofHDManaRepo,
	    		payoutManagementDataRepo,
	    		empSubstVacationRepo,
	    		comSubstVacationRepo,
	    		interimRecAbasMngRepo,
	    		payoutHdManaRepo);

		return BreakDayOffMngInPeriodQuery.getBreakDayOffMngRemain(
				requireM11Imp,
				new CacheCarrier(),
				employeeId,
				date)
				.getDays().v();
	}

	/**
	 * @name 19.振休残数表示
	 * @param employeeId 社員ID
	 * @param datePeriod 基準日
	 * @return
	 */
	public Double vibrationResidualNumberRepresentation(String employeeId, GeneralDate date) {
		//RQ#506 AbsenceReruitmentMngInPeriodQuery.getAbsRecMngRemain
	    RequireM11Imp requireM11Imp = new RequireM11Imp(
	    		comDayOffManaDataRepo,
	    		leaveComDayOffManaRepo,
	    		leaveManaDataRepo,
	    		shareEmploymentAdapter,
	    		compensLeaveEmSetRepo,
	    		compensLeaveComSetRepo,
	    		interimBreakDayOffMngRepo,
	    		closureEmploymentRepo,
	    		closureRepo,
	    		empEmployeeAdapter,
	    		substitutionOfHDManaDataRepo,
	    		payoutSubofHDManaRepo,
	    		payoutManagementDataRepo,
	    		empSubstVacationRepo,
	    		comSubstVacationRepo,
	    		interimRecAbasMngRepo,
	    		payoutHdManaRepo);

	    
		
		return AbsenceReruitmentMngInPeriodQuery.getAbsRecMngRemain(
				requireM11Imp,
				new CacheCarrier(),
				employeeId,
				date)
				.v();
	}

	/**
	 * @name 21.子の看護休暇残数表示
	 * @param employeeId 社員ID
	 * @param datePeriod 基準日
	 * @return
	 */
	public Double remainingNumberOfChildNursingLeave(String cid, String employeeId, DatePeriod datePeriod) {
		//※「期間内の子看護残を集計する」はRequestList206
		//return shNursingLeaveSettingPub.aggrChildNursingRemainPeriod(cid, employeeId, datePeriod, NursingMode.Other).getPreGrantStatement().getResidual();
		return new Double(0);
	}

	/**
	 * @name 22.介護休暇残数表示
	 * @param employeeId 社員ID
	 * @param datePeriod 基準日
	 * @return
	 */
	public Double remainingNumberOfNursingLeave(String cid, String employeeId, DatePeriod datePeriod) {
		//※「期間内の介護残を集計する」はRequestList207
		//return shNursingLeaveSettingPub.aggrNursingRemainPeriod(cid, employeeId, datePeriod.start(), datePeriod.end(), NursingMode.Other).getPreGrantStatement().getResidual();
		return new Double(0);
	}

	/**
	 * @name 23.特休残数表示
	 * @param employeeId 社員ID
	 * @param datePeriod 基準日
	 * @return
	 */
	public List<SpecialHolidaysRemainingDto> remnantRepresentation(String cid, String employeeId, DatePeriod datePeriod) {

		List<SpecialHolidaysRemainingDto> result = new ArrayList<>();

		List<SpecialHoliday> specialHolidays = specialHolidayRepository.findByCompanyId(cid);
		for (SpecialHoliday specialHoliday : specialHolidays) {
			//get request list 208 rồi trả về
			//・上書きフラグ ← falseを渡してください(muto)
			//・上書き用の暫定管理データ ← 空（null or Empty）で渡してください
			ComplileInPeriodOfSpecialLeaveParam param = new ComplileInPeriodOfSpecialLeaveParam(cid, employeeId,
					new DatePeriod(datePeriod.start(),datePeriod.end()),
					false,
					GeneralDate.today(),
					specialHoliday.getSpecialHolidayCode().v(),
					false, false,
					new ArrayList<>(),
					Optional.empty());
			InPeriodOfSpecialLeaveResultInfor inPeriodOfSpecialLeave = SpecialLeaveManagementService
					.complileInPeriodOfSpecialLeave(requireService.createRequire(), new CacheCarrier(), param);

			result.add(new SpecialHolidaysRemainingDto(
					// new RemainingDaysAndTimeDto(inPeriodOfSpecialLeave.getRemainDays().getGrantDetailBefore().getRemainDays(), new AttendanceTime(0)),
					new RemainingDaysAndTimeDto(
							inPeriodOfSpecialLeave.getAsOfPeriodEnd()
							.getRemainingNumber().getSpecialLeaveWithMinus().getRemainingNumberInfo()
							.getRemainingNumberBeforeGrant().getDayNumberOfRemain().v(), new AttendanceTimeOfExistMinus(0)),
					specialHoliday.getSpecialHolidayCode().v(),
					specialHoliday.getSpecialHolidayName().v()));
		}
		return result;
	}
	
	
	@AllArgsConstructor
    private class RequireM11Imp implements BreakDayOffMngInPeriodQuery.RequireM11, AbsenceReruitmentMngInPeriodQuery.RequireM11 {
        private ComDayOffManaDataRepository comDayOffManaDataRepo;
        
        private LeaveComDayOffManaRepository leaveComDayOffManaRepo;
        
        private LeaveManaDataRepository leaveManaDataRepo;
        
        private ShareEmploymentAdapter shareEmploymentAdapter;
        
        private CompensLeaveEmSetRepository compensLeaveEmSetRepo;
        
        private CompensLeaveComSetRepository compensLeaveComSetRepo;
        
        private InterimBreakDayOffMngRepository interimBreakDayOffMngRepo;
        
        private ClosureEmploymentRepository closureEmploymentRepo;
        
        private ClosureRepository closureRepo;
        
        private EmpEmployeeAdapter empEmployeeAdapter;
        
        private SubstitutionOfHDManaDataRepository substitutionOfHDManaDataRepo;
        
        private PayoutSubofHDManaRepository payoutSubofHDManaRepo;
        
        private PayoutManagementDataRepository payoutManagementDataRepo;
        
        private EmpSubstVacationRepository empSubstVacationRepo;
        
        private ComSubstVacationRepository comSubstVacationRepo;
        
        private InterimRecAbasMngRepository interimRecAbasMngRepo;
        
        private PayoutSubofHDManaRepository payoutHdManaRepo;

        @Override
        public Optional<BsEmploymentHistoryImport> findEmploymentHistory(String companyId, String employeeId,
                GeneralDate baseDate) {
            return shareEmploymentAdapter.findEmploymentHistory(companyId, employeeId, baseDate);
        }

        @Override
        public CompensatoryLeaveEmSetting findComLeavEmpSet(String companyId, String employmentCode) {
            return compensLeaveEmSetRepo.find(companyId, employmentCode);
        }

        @Override
        public CompensatoryLeaveComSetting findComLeavComSet(String companyId) {
            return compensLeaveComSetRepo.find(companyId);
        }

        @Override
        public List<EmploymentHistShareImport> findByEmployeeIdOrderByStartDate(String employeeId) {
            return shareEmploymentAdapter.findByEmployeeIdOrderByStartDate(employeeId);
        }

        @Override
        public Optional<ClosureEmployment> employmentClosure(String companyID, String employmentCD) {
            return closureEmploymentRepo.findByEmploymentCD(companyID, employmentCD);
        }

        @Override
        public List<SharedSidPeriodDateEmploymentImport> employmentHistory(CacheCarrier cacheCarrier, List<String> sids,
                DatePeriod datePeriod) {
            return shareEmploymentAdapter.getEmpHistBySidAndPeriodRequire(cacheCarrier, sids, datePeriod);
        }

        @Override
        public List<Closure> closure(String companyId) {
            return closureRepo.findAll(companyId);
        }

        @Override
        public EmployeeImport employee(CacheCarrier cacheCarrier, String empId) {
            return empEmployeeAdapter.findByEmpIdRequire(cacheCarrier, empId);
        }

        @Override
        public List<PayoutSubofHDManagement> getPayoutSubWithDateUse(String sid, GeneralDate dateOfUse,
                GeneralDate baseDate) {
            return payoutHdManaRepo.getWithDateUse(sid, dateOfUse, baseDate);
        }

        @Override
        public List<PayoutSubofHDManagement> getPayoutSubWithOutbreakDay(String sid, GeneralDate outbreakDay,
                GeneralDate baseDate) {
            return payoutHdManaRepo.getWithOutbreakDay(sid, outbreakDay, baseDate);
        }

        @Override
        public List<LeaveComDayOffManagement> getLeaveComWithDateUse(String sid, GeneralDate dateOfUse,
                GeneralDate baseDate) {
            return leaveComDayOffManaRepo.getLeaveComWithDateUse(sid, dateOfUse, baseDate);
        }

        @Override
        public List<LeaveComDayOffManagement> getLeaveComWithOutbreakDay(String sid, GeneralDate outbreakDay,
                GeneralDate baseDate) {
            return leaveComDayOffManaRepo.getLeaveComWithOutbreakDay(sid, outbreakDay, baseDate);
        }

        @Override
        public Optional<EmpSubstVacation> findEmpById(String companyId, String contractTypeCode) {
            return empSubstVacationRepo.findById(companyId, contractTypeCode);
        }

        @Override
        public Optional<ComSubstVacation> findComById(String companyId) {
            return comSubstVacationRepo.findById(companyId);
        }

        @Override
        public List<InterimAbsMng> getAbsBySidDatePeriod(String sid, DatePeriod period) {
            return interimRecAbasMngRepo.getAbsBySidDatePeriod(sid, period);
        }

        @Override
        public List<InterimRecMng> getRecBySidDatePeriod(String sid, DatePeriod period) {
            return interimRecAbasMngRepo.getRecBySidDatePeriod(sid, period);
        }

        @Override
        public List<Closure> closureActive(String companyId, UseClassification useAtr) {
            return closureRepo.findAllActive(companyId, useAtr);
        }
        
        @Override
        public List<LeaveComDayOffManagement> getDigestOccByListComId(String sid, DatePeriod period) {
            return leaveComDayOffManaRepo.getDigestOccByListComId(sid, period);
        }

        @Override
        public List<InterimDayOffMng> getTempDayOffBySidPeriod(String sid, DatePeriod period) {
            return interimBreakDayOffMngRepo.getDayOffBySidPeriod(sid, period);
        }

        @Override
        public List<CompensatoryDayOffManaData> getFixByDayOffDatePeriod(String sid) {
            return comDayOffManaDataRepo.getBySid(AppContexts.user().companyId(), sid);
        }

        @Override
        public List<InterimBreakMng> getTempBreakBySidPeriod(String sid, DatePeriod period) {
            return interimBreakDayOffMngRepo.getBySidPeriod(sid, period);
        }

        @Override
        public List<LeaveManagementData> getFixLeavByDayOffDatePeriod(String sid) {
            return leaveManaDataRepo.getBySid(AppContexts.user().companyId(), sid);
        }

        @Override
        public List<PayoutSubofHDManagement> getOccDigetByListSid(String sid, DatePeriod date) {
            return payoutSubofHDManaRepo.getOccDigetByListSid(sid, date);
        }

        @Override
        public List<SubstitutionOfHDManagementData> getByYmdUnOffset(String sid) {
            return substitutionOfHDManaDataRepo.getBysiD(AppContexts.user().companyId(), sid);
        }

        @Override
        public List<PayoutManagementData> getPayoutMana(String sid) {
            return payoutManagementDataRepo.getSid(AppContexts.user().companyId(), sid);
        }
    }
}
