package nts.uk.screen.at.app.dailyperformance.correction;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.request.dom.adapter.monthly.vacation.childcarenurse.ChildCareNursePeriodImport;
import nts.uk.ctx.at.request.dom.adapter.monthly.vacation.childcarenurse.care.GetRemainingNumberCareAdapter;
import nts.uk.ctx.at.request.dom.adapter.monthly.vacation.childcarenurse.care.GetRemainingNumberChildCareAdapter;
import nts.uk.ctx.at.request.dom.adapter.monthly.vacation.childcarenurse.care.GetRemainingNumberNursingAdapter;
import nts.uk.ctx.at.request.dom.adapter.monthly.vacation.childcarenurse.childcare.GetRemainingNumberChildCareNurseAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.annualholidaymanagement.AnnualHolidayManagementAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.annualholidaymanagement.NextAnnualLeaveGrantImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.annualleave.AnnLeaveRemainNumberAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.annualleave.ReNumAnnLeaveImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.rsvleamanager.ReserveLeaveManagerApdater;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.rsvleamanager.rsvimport.RsvLeaManagerImport;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmpEmployeeAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.BsEmploymentHistoryImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.EmploymentHistShareImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.ShareEmploymentAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employment.SharedSidPeriodDateEmploymentImport;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.AbsenceReruitmentMngInPeriodQuery;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimAbsMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimRecAbasMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimRecMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.BreakDayOffMngInPeriodQuery;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.procwithbasedate.NumberConsecutiveVacation;
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
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.processten.AbsenceTenProcess;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.processten.AnnualHolidaySetOutput;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.processten.LeaveSetOutput;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.processten.SubstitutionHolidayOutput;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveComSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveEmSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveEmSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.nursingleave.NursingCategory;
import nts.uk.ctx.at.shared.dom.vacation.setting.nursingleave.NursingLeaveSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.nursingleave.NursingLeaveSettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacation;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacationRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.EmpSubstVacation;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.EmpSubstVacationRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmployment;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.UseClassification;
import nts.uk.screen.at.app.dailyperformance.correction.dto.Com60HVacationDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.CompensLeaveComDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.HolidayRemainNumberDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.NursingRemainDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.NursingVacationDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ReserveLeaveDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.SubstVacationDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.YearHolidaySettingDto;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author HungTT - 休暇残数を表示する
 *
 */

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DisplayRemainingHolidayNumber {

	@Inject
	private AnnLeaveRemainNumberAdapter annLeaveRemainAdapter;
	
	@Inject
	private ReserveLeaveManagerApdater rsvLeaveRemainAdapter;
	
	@Inject
	private AnnualHolidayManagementAdapter annualHolidayMng;
	
	@Inject
	private RecordDomRequireService requireService;
	
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
    
    @Inject
    private LeaveComDayOffManaRepository leaveComDayOffManaRepo;
    
    @Inject
    private PayoutSubofHDManaRepository payoutHdManaRepo;
    
    @Inject
    private NursingLeaveSettingRepository nursingLeaveSettingRepo;
    
    @Inject
    private GetRemainingNumberChildCareNurseAdapter getRemainingNumberChildCareNurseAdapter;
    
    @Inject
    private GetRemainingNumberChildCareAdapter getRemainingNumberChildCareAdapter;
    
    @Inject
    private GetRemainingNumberNursingAdapter getRemainingNumberNursingAdapter;
    
    @Inject
    private GetRemainingNumberCareAdapter getRemainingNumberCareAdapter;

	public YearHolidaySettingDto getAnnualLeaveSetting(String companyId, String employeeId, GeneralDate date) {
		AnnualHolidaySetOutput output = AbsenceTenProcess.getSettingForAnnualHoliday(
				requireService.createRequire(), companyId);
		
		if (output.isYearHolidayManagerFlg()) {
			//RequestList198
		    ReNumAnnLeaveImport remainNum = annLeaveRemainAdapter
					.getReferDateAnnualLeaveRemain(employeeId, date);
			return new YearHolidaySettingDto(
			        output.isYearHolidayManagerFlg(), 
			        output.isSuspensionTimeYearFlg(),
					remainNum.getRemainingDays(),
					remainNum.getRemainingTime());
		} else {
			return new YearHolidaySettingDto(false, false, null, null);
		}
	}

	public ReserveLeaveDto getReserveLeaveSetting(String companyId, String employeeId, GeneralDate date) {
		boolean manageAtr = AbsenceTenProcess.getSetForYearlyReserved(
				requireService.createRequire(),
				new CacheCarrier(),
				companyId, employeeId, date);
		if (manageAtr) {
			// call requestlist201
			Optional<RsvLeaManagerImport> optOutput = rsvLeaveRemainAdapter.getRsvLeaveManager(employeeId, date);
			return new ReserveLeaveDto(manageAtr, optOutput.isPresent() && optOutput.get().getReserveLeaveInfo() != null
					? optOutput.get().getReserveLeaveInfo().getRemainingDays() : 0);
		} else
			return new ReserveLeaveDto(false, null);
	}
	
	public SubstVacationDto getSubsitutionVacationSetting(String companyId, String employeeId, GeneralDate date) {
		LeaveSetOutput output = AbsenceTenProcess.getSetForLeave(
				requireService.createRequire(), new CacheCarrier(),
				companyId, employeeId, date);
		RequireM11Imp requireM11Imp = new RequireM11Imp(comDayOffManaDataRepo, leaveComDayOffManaRepo, leaveManaDataRepo, shareEmploymentAdapter, compensLeaveEmSetRepo, compensLeaveComSetRepo, interimBreakDayOffMngRepo, closureEmploymentRepo, closureRepo, empEmployeeAdapter, substitutionOfHDManaDataRepo, payoutSubofHDManaRepo, payoutManagementDataRepo, empSubstVacationRepo, comSubstVacationRepo, interimRecAbasMngRepo, payoutHdManaRepo);

		if (output.isSubManageFlag()) {
			double remain = AbsenceReruitmentMngInPeriodQuery.getAbsRecMngRemain(
			        requireM11Imp, new CacheCarrier(),
					employeeId, date).v();
			return new SubstVacationDto(output.isSubManageFlag(), remain);
		}
		return new SubstVacationDto(false, null);
	}

	public CompensLeaveComDto getCompensatoryLeaveSetting(String companyId, String employeeId, GeneralDate date) {
		SubstitutionHolidayOutput output = AbsenceTenProcess.getSettingForSubstituteHoliday(
				requireService.createRequire(), new CacheCarrier(),
				companyId, employeeId, date);
		RequireM11Imp requireM11Imp = new RequireM11Imp(comDayOffManaDataRepo, leaveComDayOffManaRepo, leaveManaDataRepo, shareEmploymentAdapter, compensLeaveEmSetRepo, compensLeaveComSetRepo, interimBreakDayOffMngRepo, closureEmploymentRepo, closureRepo, empEmployeeAdapter, substitutionOfHDManaDataRepo, payoutSubofHDManaRepo, payoutManagementDataRepo, empSubstVacationRepo, comSubstVacationRepo, interimRecAbasMngRepo, payoutHdManaRepo);

		if (output != null && output.isSubstitutionFlg()) {
		    NumberConsecutiveVacation remain = BreakDayOffMngInPeriodQuery.getBreakDayOffMngRemain(
			        requireM11Imp, new CacheCarrier(),
					employeeId, date);
		    Double days = 0.0;
		    int time = 0;
		    if (remain != null) {
		        if (remain.getDays() != null) {
		            days = remain.getDays().v();
		        }
		        if (remain.getRemainTime() != null) {
		            time = remain.getRemainTime().v();
		        }
		    }
			return new CompensLeaveComDto(output.isSubstitutionFlg(), output.isTimeOfPeriodFlg(), days, time);
		} else {
			return new CompensLeaveComDto(false, false, 0.0, 0);
		}
	}

	public Com60HVacationDto getCom60HVacationSetting(String companyId, String employeeId, GeneralDate date) {
		Com60HVacationDto output = new Com60HVacationDto("", false, null, null);
		return output;
	}
	
	public NursingRemainDto getNursingSetting(String companyId, String employeeId, GeneralDate date, String closureDate) {
	    GeneralDate closureStart = GeneralDate.fromString(closureDate, "yyyy/MM/dd");
	    
	    // 子看護介護の設定の取得
	    NursingLeaveSetting childCareSettings = nursingLeaveSettingRepo.findByCompanyIdAndNursingCategory(companyId, NursingCategory.ChildNursing.value);
        // 子看護介護の設定の取得
        NursingLeaveSetting nursingLeaveSetting = nursingLeaveSettingRepo.findByCompanyIdAndNursingCategory(companyId, NursingCategory.Nursing.value);
        
        NursingVacationDto childCareVacation = new NursingVacationDto();
        NursingVacationDto longTermCareVacation = new NursingVacationDto();
        if (childCareSettings.getManageType().equals(ManageDistinct.YES)) {
            // [NO.206]期間中の子の看護休暇残数を取得
//            ChildCareNursePeriodImport childNursePeriod = getRemainingNumberChildCareNurseAdapter.getChildCareNurseRemNumWithinPeriod(
//                    employeeId, 
//                    new DatePeriod(closureStart, closureStart.addYears(1).addDays(-1)), 
//                    InterimRemainMngMode.OTHER, 
//                    date, 
//                    Optional.of(false), 
//                    Optional.empty(), 
//                    Optional.empty(), 
//                    Optional.empty(), 
//                    Optional.empty());
         // 基準日時点の子の看護残数を取得する
            ChildCareNursePeriodImport childNursePeriod = getRemainingNumberChildCareAdapter
                    .getRemainingNumberChildCare(companyId, employeeId, GeneralDate.today());
            
            // OUTPUT「子の看護残数」をセットする
            childCareVacation = new NursingVacationDto(
                    childCareSettings.isManaged(), 
                    childCareSettings.getTimeCareNursingSetting().getManageDistinct().equals(ManageDistinct.YES), 
                    childNursePeriod.getStartdateDays().getThisYear().getRemainingNumber().getUsedDays(), 
                    childNursePeriod.getStartdateDays().getThisYear().getRemainingNumber().getUsedTime().isPresent() ? 
                            childNursePeriod.getStartdateDays().getThisYear().getRemainingNumber().getUsedTime().get() : 0);
        }
        
        if (nursingLeaveSetting.getManageType().equals(ManageDistinct.YES)) {
            // [NO.207]期間中の介護休暇残数を取得
//            ChildCareNursePeriodImport longtermCarePeriod = getRemainingNumberCareAdapter.getCareRemNumWithinPeriod(
//                    companyId, 
//                    employeeId, 
//                    new DatePeriod(closureStart, closureStart.addYears(1).addDays(-1)), 
//                    InterimRemainMngMode.OTHER, 
//                    date, 
//                    Optional.of(false), 
//                    new ArrayList<TempChildCareNurseManagementImport>(),
//                    Optional.empty(), 
//                    Optional.empty(), 
//                    Optional.empty());
         // 基準日時点の介護残数を取得する
            ChildCareNursePeriodImport longtermCarePeriod = getRemainingNumberNursingAdapter
                    .getRemainingNumberNursing(companyId, employeeId, GeneralDate.today());
            
            // OUTPUT「介護残数」をセットする
            longTermCareVacation = new NursingVacationDto(
                    nursingLeaveSetting.isManaged(), 
                    nursingLeaveSetting.getTimeCareNursingSetting().getManageDistinct().equals(ManageDistinct.YES), 
                    longtermCarePeriod.getStartdateDays().getThisYear().getRemainingNumber().getUsedDays(), 
                    longtermCarePeriod.getStartdateDays().getThisYear().getRemainingNumber().getUsedTime().isPresent() ? 
                            longtermCarePeriod.getStartdateDays().getThisYear().getRemainingNumber().getUsedTime().get() : 0);
        }
        
        return new NursingRemainDto(childCareVacation, longTermCareVacation);
	}
	
	private NextAnnualLeaveGrantImport getNextGrantDate(String companyId, String employeeId, GeneralDate date) {
		List<NextAnnualLeaveGrantImport> lstOutput = this.annualHolidayMng.acquireNextHolidayGrantDate(companyId, employeeId, date);
		Optional<NextAnnualLeaveGrantImport> futureGrant = lstOutput.stream()
                .filter(holiday -> holiday.grantDate.after(GeneralDate.today()))
                .min(Comparator.comparing(h -> h.grantDate));
		return futureGrant.isPresent() ? futureGrant.get() : null;
	}
	
	public HolidayRemainNumberDto getRemainingHolidayNumber(String employeeId, String closureDate) {
		String companyId = AppContexts.user().companyId();
		GeneralDate baseDate = GeneralDate.today();
		HolidayRemainNumberDto result = new HolidayRemainNumberDto();
		result.setAnnualLeave(this.getAnnualLeaveSetting(companyId, employeeId, baseDate));
		result.setCompensatoryLeave(this.getCompensatoryLeaveSetting(companyId, employeeId, baseDate));
		result.setReserveLeave(this.getReserveLeaveSetting(companyId, employeeId, baseDate));
		result.setSubstitutionLeave(this.getSubsitutionVacationSetting(companyId, employeeId, baseDate));
		result.setCom60HVacation(this.getCom60HVacationSetting(companyId, employeeId, baseDate));
		NursingRemainDto nursingRemainDto = this.getNursingSetting(companyId, employeeId, baseDate, closureDate);
		result.setChildCareVacation(nursingRemainDto.getChildCareVacation());
		result.setLongTermCareVacation(nursingRemainDto.getLongTermCareVacation());
		if (result.getAnnualLeave().isManageYearOff()) {
		    NextAnnualLeaveGrantImport nextAnnualLeaveGrantImport = this.getNextGrantDate(companyId, employeeId, baseDate);
		    if (nextAnnualLeaveGrantImport != null) {
		        result.setNextGrantDate(nextAnnualLeaveGrantImport.getGrantDate());
		        result.setGrantDays(nextAnnualLeaveGrantImport.getGrantDays());
		    } else {
		        result.setNextGrantDate(null);
		        result.setGrantDays(0.0);
		    }
		}
//			result.setNextGrantDate(this.getNextGrantDate(companyId, employeeId, baseDate));
		return result;
	}

	@AllArgsConstructor
    public class RequireM11Imp implements BreakDayOffMngInPeriodQuery.RequireM11, AbsenceReruitmentMngInPeriodQuery.RequireM11 {
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
