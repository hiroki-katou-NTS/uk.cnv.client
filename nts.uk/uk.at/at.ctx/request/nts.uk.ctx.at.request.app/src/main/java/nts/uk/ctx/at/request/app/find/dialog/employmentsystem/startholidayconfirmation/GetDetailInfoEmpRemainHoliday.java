package nts.uk.ctx.at.request.app.find.dialog.employmentsystem.startholidayconfirmation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.request.app.find.dialog.employmentsystem.holidaysubstitute.GetRemainNumberConfirmInfo;
import nts.uk.ctx.at.request.app.find.dialog.employmentsystem.holidaysubstitute.RemainNumberConfirmDto;
import nts.uk.ctx.at.request.app.find.dialog.employmentsystem.holidaysubstitute.RemainNumberConfirmInfo;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmpEmployeeAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.BsEmploymentHistoryImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.EmploymentHistShareImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.ShareEmploymentAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employment.SharedSidPeriodDateEmploymentImport;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.AbsenceReruitmentMngInPeriodQuery;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.NumberCompensatoryLeavePeriodProcess;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.param.AbsRecMngInPeriodRefactParamInput;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.param.CompenLeaveAggrResult;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimAbsMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimRecAbasMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimRecMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.require.RemainNumberTempRequireService;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.BreakDayOffMngInPeriodQuery;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimBreakDayOffMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimBreakMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimDayOffMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingDayNumber;
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
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.breakinfo.FixedManagementDataMonth;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.processten.AbsenceTenProcessCommon;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.processten.LeaveSetOutput;
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
import nts.uk.ctx.at.shared.dom.workrule.closure.service.GetClosureStartForEmployee;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;

/**
 * 社員の振休残数詳細情報を取得
 * UKDesign.UniversalK.就業.KDL_ダイアログ.KDL009_振休確認ダイアログ.アルゴリズム.社員の振休残数詳細情報を取得
 * @author tutk
 *
 */
@Stateless
public class GetDetailInfoEmpRemainHoliday {
	
	@Inject
	private AbsenceTenProcessCommon absenceTenProcessCommon;
	
	@Inject
	private RemainNumberTempRequireService requireService;
	
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
    private CompensLeaveComSetRepository compensLeaveComSetRepo;
	
	@Inject
	private PayoutSubofHDManaRepository payoutHdManaRepo;
	
	@Inject
	private GetRemainNumberConfirmInfo numberConfirmInfo;
	
	@Inject
	private NumberCompensatoryLeavePeriodProcess numberCompensatoryLeavePeriodProcess;
	
	public RemainNumberConfirmDto getByEmpId(String employeeId) {
		String companyId = AppContexts.user().companyId();
		GeneralDate baseDate  =  GeneralDate.today();	
		
		RemainNumberConfirmInfo remainNumberConfirmInfo = null;
		String currentRemainNumber = "";
		
		// Step 10-3.振休の設定を取得する
		LeaveSetOutput leaveSet = this.absenceTenProcessCommon.getSetForLeave(companyId, employeeId, baseDate);
		if(leaveSet.isSubManageFlag()) {
			// 社員に対応する締め開始日を取得する
			Optional<GeneralDate> closureDate = this.getClosureStartForEmployee(employeeId);
			RequireM11Imp requireM11Imp = new RequireM11Imp();
			//[No.506]振休残数を取得する 
			LeaveRemainingDayNumber leaveRemainingDayNumber = AbsenceReruitmentMngInPeriodQuery.getAbsRecMngRemain(
					requireM11Imp, new CacheCarrier(), employeeId, GeneralDate.today());
			currentRemainNumber = leaveRemainingDayNumber.v().toString();
			
			DatePeriod datePeriod = new DatePeriod(closureDate.get(), closureDate.get().addYears(1).addDays(-1));
			AbsRecMngInPeriodRefactParamInput inputParam = new AbsRecMngInPeriodRefactParamInput(
					companyId,
					employeeId,
					datePeriod,
					closureDate.get(),
					false,
					false, 
					Collections.emptyList(),
					Collections.emptyList(),
					Collections.emptyList(),
					Optional.empty(),
					Optional.empty(),
					Optional.empty(),
					new FixedManagementDataMonth(Collections.emptyList(), Collections.emptyList()));
			//[No204]期間内の振出振休残数を取得する
			CompenLeaveAggrResult compenLeaveAggrResult = this.numberCompensatoryLeavePeriodProcess.process(inputParam);
			
			//残数確認情報を調整
			remainNumberConfirmInfo  = numberConfirmInfo.getRemainNumberConfirmInfo(compenLeaveAggrResult.getLstSeqVacation(),
					compenLeaveAggrResult.getVacationDetails(), false); 
		}	
		// 残数確認ダイアログDTOを作成して返す
		RemainNumberConfirmDto confirmDto = new RemainNumberConfirmDto (
				remainNumberConfirmInfo != null ? remainNumberConfirmInfo.getExpiredWithinMonth() : "", // 1ヶ月以内期限切れ数
						0, // 単位 0 : 日, 1 : 時間
				remainNumberConfirmInfo != null ? remainNumberConfirmInfo.getDayCloseDeadline() : "", // 期限の一番近い日
				remainNumberConfirmInfo != null ? remainNumberConfirmInfo.getDetailedInfos() : new ArrayList<>(), // 残数詳細一覧 - 残数詳細情報
				currentRemainNumber + TextResource.localize("KDL005_47"), // 現時点残数
				employeeId,
				leaveSet.isSubManageFlag() ? 1 : 0); // 管理する
		return confirmDto;
	}
	
	private Optional<GeneralDate> getClosureStartForEmployee(String sID){
		 val require = requireService.createRequire();
		 val cache = new CacheCarrier();
		 Optional<GeneralDate> closure = GetClosureStartForEmployee.algorithm(require, cache, sID);
		 
		 return closure;
	}
	
	@AllArgsConstructor
    private class RequireM11Imp implements BreakDayOffMngInPeriodQuery.RequireM11, AbsenceReruitmentMngInPeriodQuery.RequireM11 {

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
