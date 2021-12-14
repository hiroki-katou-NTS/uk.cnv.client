package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.converter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import nts.arc.time.YearMonth;
import nts.uk.ctx.at.shared.dom.attendance.util.AttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.MonthlyDayoffRemainData;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeOfManagePeriod;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.IntegrationOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.affiliation.AffiliationInfoOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.anyitem.AnyItemOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.remarks.RemarksMonthlyRecord;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.absenceleave.AbsenceLeaveRemainData;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.AnnLeaRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.care.CareRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.childcare.ChildcareRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.reserveleave.RsvLeaRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.specialholiday.SpecialHolidayRemainData;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

/**
 * 月別実績と勤怠項目の相互変換
 * @author shuichu_ishida
 */
public interface MonthlyRecordToAttendanceItemConverter extends AttendanceItemConverter {

	Optional<ItemValue> convert(int attendanceItemId);

	List<ItemValue> convert(Collection<Integer> attendanceItemIds);
	
	void merge(ItemValue value);
	
	void merge(Collection<ItemValue> values);

	IntegrationOfMonthly toDomain();
	
	MonthlyRecordToAttendanceItemConverter setData(IntegrationOfMonthly domain);

	MonthlyRecordToAttendanceItemConverter withBase(String employeeId, YearMonth yearMonth, ClosureId closureId,
			ClosureDate closureDate);
	
	MonthlyRecordToAttendanceItemConverter withAffiliation(AffiliationInfoOfMonthly domain);

	MonthlyRecordToAttendanceItemConverter withAttendanceTime(AttendanceTimeOfMonthly domain);

	MonthlyRecordToAttendanceItemConverter withAnyItem(List<AnyItemOfMonthly> domains);

	MonthlyRecordToAttendanceItemConverter withAnnLeave(AnnLeaRemNumEachMonth domain);

	MonthlyRecordToAttendanceItemConverter withRsvLeave(RsvLeaRemNumEachMonth domain);

	MonthlyRecordToAttendanceItemConverter withDayOff(MonthlyDayoffRemainData domains);

	MonthlyRecordToAttendanceItemConverter withSpecialLeave(List<SpecialHolidayRemainData> domain);

	MonthlyRecordToAttendanceItemConverter withAbsenceLeave(AbsenceLeaveRemainData domain);

	MonthlyRecordToAttendanceItemConverter withMonChildHd(ChildcareRemNumEachMonth monChildHdRemain);

	MonthlyRecordToAttendanceItemConverter withMonCareHd(CareRemNumEachMonth monCareHdRemain);

	MonthlyRecordToAttendanceItemConverter withRemarks(List<RemarksMonthlyRecord> domain);
	
	MonthlyRecordToAttendanceItemConverter withAgreementTime(AgreementTimeOfManagePeriod domain);

	MonthlyRecordToAttendanceItemConverter completed();
	
	Optional<AffiliationInfoOfMonthly> toAffiliation();
	
	Optional<AttendanceTimeOfMonthly> toAttendanceTime();
	
	Optional<AgreementTimeOfManagePeriod> toAgreementTime();
	
	List<AnyItemOfMonthly> toAnyItems();
	
	Optional<AnnLeaRemNumEachMonth> toAnnLeave();
	
	Optional<CareRemNumEachMonth> toMonCareHd();
	
	Optional<ChildcareRemNumEachMonth> toMonChildHd();
	
	Optional<RsvLeaRemNumEachMonth> toRsvLeave();
	
	Optional<MonthlyDayoffRemainData> toDayOff();
	
	List<SpecialHolidayRemainData> toSpecialHoliday();
	
	Optional<AbsenceLeaveRemainData> toAbsenceLeave();
	
	List<RemarksMonthlyRecord> toRemarks();
}
