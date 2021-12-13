package nts.uk.ctx.at.record.dom.monthly.updatedomain;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.adapter.createmonthlyapprover.CreateMonthlyApproverAdapter;
import nts.uk.ctx.at.record.dom.attendanceitem.StoredProcdureProcess;
import nts.uk.ctx.at.record.dom.monthly.TimeOfMonthly;
import nts.uk.ctx.at.record.dom.monthly.TimeOfMonthlyRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeOfManagePeriod;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeOfManagePeriodRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.IntegrationOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.affiliation.AffiliationInfoOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.anyitem.AnyItemOfMonthlyRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.EmployeeMonthlyPerErrorRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.remainmerge.MonthMergeKey;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.remainmerge.RemainMerge;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.remainmerge.RemainMergeRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.weekly.AttendanceTimeOfWeeklyRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class UpdateAllDomainMonthServiceImpl implements UpdateAllDomainMonthService {
	
	@Inject
	private EmployeeMonthlyPerErrorRepository empErrorRepo;
	
	@Inject 
	private TimeOfMonthlyRepository timeRepo;
	
	@Inject 
	private RemainMergeRepository remainRepo;
//	
//	@Inject
//	private RemarksMonthlyRecordRepository remarksRepo;
	
	@Inject
	private AttendanceTimeOfWeeklyRepository timeWeekRepo;
	
	@Inject
	private AnyItemOfMonthlyRepository anyItemRepo;
	
	@Inject
	private AgreementTimeOfManagePeriodRepository agreementRepo;
	
	@Inject
	private StoredProcdureProcess storedProcedureProcess;
	
	@Inject
	private CreateMonthlyApproverAdapter createMonthlyApproverAd;

	@Override
	public void merge(List<IntegrationOfMonthly> domains, GeneralDate targetDate) {
		String companyId = AppContexts.user().companyId();
		domains.forEach(d -> {
			if (d.getAffiliationInfo().isPresent() && d.getAttendanceTime().isPresent()){
				
				val employeeId =  d.getAffiliationInfo().get().getEmployeeId();
				val yearMonth = d.getAffiliationInfo().get().getYearMonth();
				val closureId = d.getAffiliationInfo().get().getClosureId();
				val closureDate = d.getAffiliationInfo().get().getClosureDate();
				val datePeriod = d.getAttendanceTime().get().getDatePeriod();
				
				// 最大の週Noを確認する
				int maxWeekNo = 0;
				for (val timeWeek : d.getAttendanceTimeOfWeek()){
					int weekNo = timeWeek.getWeekNo();
					if (maxWeekNo < weekNo) maxWeekNo = weekNo;
				}
				
				// 更新データと同月データ・締めID違い かつ 期間重複データの削除
				val timeOlds = this.timeRepo.findByYearMonthOrderByStartYmd(employeeId, yearMonth);
				for (val oldData : timeOlds){
					if (!oldData.getAttendanceTime().isPresent()) continue;
					val oldClosureId = oldData.getClosureId();
					val oldClosureDate = oldData.getClosureDate();
					val oldAttendanceTime = oldData.getAttendanceTime().get();
					
					MonthMergeKey oldDomainsKey = new MonthMergeKey();
					oldDomainsKey.setEmployeeId(employeeId);
					oldDomainsKey.setYearMonth(yearMonth);
					oldDomainsKey.setClosureId(oldClosureId);
					oldDomainsKey.setClosureDate(oldClosureDate);
					
					if (!this.periodCompareEx(oldAttendanceTime.getDatePeriod(), datePeriod)) continue;
					boolean isTarget = false;
					if (oldClosureId.value != closureId.value) isTarget = true;
					if (oldClosureDate.getClosureDay().v() != closureDate.getClosureDay().v()) isTarget = true;
					if (oldClosureDate.getLastDayOfMonth() != closureDate.getLastDayOfMonth()) isTarget = true;
					if (!isTarget) {
						d.getAffiliationInfo().ifPresent(aff -> {
							aff.setVersion(oldData.getAttendanceTime().get().getVersion());
						});
						d.getAttendanceTime().ifPresent(att -> {
							att.setVersion(oldData.getAttendanceTime().get().getVersion());
						});
						continue;
					}
					
					this.timeRepo.remove(employeeId, yearMonth, oldClosureId, oldClosureDate);
					this.empErrorRepo.removeAll(employeeId, yearMonth, oldClosureId, oldClosureDate);
					this.timeWeekRepo.removeByClosure(employeeId, yearMonth, oldClosureId, oldClosureDate);
					this.anyItemRepo.removeByMonthlyAndClosure(employeeId, yearMonth, oldClosureId, oldClosureDate);
					this.remainRepo.remove(oldDomainsKey);
					// プライマリキーに仕様不具合があるため、一旦コメント。 2018.10.10 shuichi_ishida
//					this.remarksRepo.remove(employeeId, yearMonth, oldClosureId, oldClosureDate);
				}
				
				// 同月の週次データは、一旦全削除　（親子での不整合を防ぐため）
				this.timeWeekRepo.removeByClosure(employeeId, yearMonth, closureId, closureDate);
				
				timeRepo.persistAndUpdate(new TimeOfMonthly(d.getAttendanceTime(), d.getAffiliationInfo()));
				
				if(d.getAttendanceTime().isPresent()){
					this.createMonthlyApproverAd.createApprovalStatusMonth(
							employeeId, targetDate, yearMonth, closureId.value, closureDate);
				}
				
				d.getEmployeeMonthlyPerError().forEach(x -> empErrorRepo.insertAll(x));
				if (d.getEmployeeMonthlyPerError().isEmpty()){
					this.empErrorRepo.removeAll(employeeId, yearMonth, closureId, closureDate);
				}
				
				// 上で全削除しているので、INSERTのみ
				d.getAttendanceTimeOfWeek().stream().forEach(atw -> this.timeWeekRepo.persist(atw));
				
				if (AppContexts.optionLicense().customize().ootsuka()) {
					
					this.storedProcedureProcess.monthlyProcessing(
							companyId, employeeId, yearMonth, closureId, closureDate,
							d.getAttendanceTime(), d.getAnyItemList());
				} else {

					anyItemRepo.persistAndUpdate(d.getAnyItemList());	
				}
				
				d.getAgreementTime().ifPresent(c ->  this.agreementRepo.persistAndUpdate(c));
				
				MonthMergeKey key = createKey(d.getAffiliationInfo().get());
				remainRepo.persistAndUpdate(key, getRemains(d, key));
				
				// プライマリキーに仕様不具合があるため、一旦コメント。 2018.10.10 shuichi_ishida
//				d.getRemarks().forEach(r -> remarksRepo.persistAndUpdate(r));
//				if (d.getRemarks().isEmpty()){
//					this.remarksRepo.remove(employeeId, yearMonth, closureId, closureDate);
//				}
			}
		});
	}
	
	private MonthMergeKey createKey(AffiliationInfoOfMonthly d) {
		MonthMergeKey key = new MonthMergeKey();
		
		key.setClosureId(d.getClosureId());
		key.setClosureDate(d.getClosureDate());
		key.setEmployeeId(d.getEmployeeId());
		key.setYearMonth(d.getYearMonth());
		
		return key;
	}
	
	private RemainMerge getRemains(IntegrationOfMonthly d, MonthMergeKey key) {
		RemainMerge remains = new RemainMerge();
		
		remains.setAbsenceLeaveRemainData(d.getAbsenceLeaveRemain().orElse(null));
		remains.setAnnLeaRemNumEachMonth(d.getAnnualLeaveRemain().orElse(null));
		remains.setMonCareHdRemain(d.getCare().orElse(null));
		remains.setMonChildHdRemain(d.getChildCare().orElse(null));
		remains.setMonthlyDayoffRemainData(d.getMonthlyDayoffRemain().orElse(null));
		remains.setMonthMergeKey(key);
		remains.setRsvLeaRemNumEachMonth(d.getReserveLeaveRemain().orElse(null));
		remains.setSpecialHolidayRemainData(d.getSpecialLeaveRemain());
		remains.setMonPublicHoliday(d.getPublicHolidayLeaveRemain().orElse(null));
		return remains;
	}

	/**
	 * 期間重複があるか
	 * @param period1 期間1
	 * @param period2 期間2
	 * @return true：重複あり、false：重複なし
	 */
	private boolean periodCompareEx(DatePeriod period1, DatePeriod period2){
		
		if (period1.start().after(period2.end())) return false;
		if (period1.end().before(period2.start())) return false;
		return true;
	}
}
