package nts.uk.ctx.at.record.app.find.monthly;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import nts.arc.time.YearMonth;
import nts.uk.ctx.at.record.app.find.monthly.root.AbsenceLeaveRemainDataDto;
import nts.uk.ctx.at.record.app.find.monthly.root.AffiliationInfoOfMonthlyDto;
import nts.uk.ctx.at.record.app.find.monthly.root.AgreementTimeOfManagePeriodDto;
import nts.uk.ctx.at.record.app.find.monthly.root.AnnLeaRemNumEachMonthDto;
import nts.uk.ctx.at.record.app.find.monthly.root.AnyItemOfMonthlyDto;
import nts.uk.ctx.at.record.app.find.monthly.root.AttendanceTimeOfMonthlyDto;
import nts.uk.ctx.at.record.app.find.monthly.root.MonthlyCareHdRemainDto;
import nts.uk.ctx.at.record.app.find.monthly.root.MonthlyChildCareHdRemainDto;
import nts.uk.ctx.at.record.app.find.monthly.root.MonthlyDayoffRemainDataDto;
import nts.uk.ctx.at.record.app.find.monthly.root.MonthlyRemarksDto;
import nts.uk.ctx.at.record.app.find.monthly.root.RsvLeaRemNumEachMonthDto;
import nts.uk.ctx.at.record.app.find.monthly.root.common.ClosureDateDto;
import nts.uk.ctx.at.record.app.find.monthly.root.common.MonthlyItemCommon;
import nts.uk.ctx.at.record.app.find.monthly.root.dto.SpecialHolidayRemainDataDtoWrap;
import nts.uk.ctx.at.record.dom.attendanceitem.util.AttendanceItemConverterCommonService;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.MonthlyDayoffRemainData;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.ItemConst;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ConvertibleAttendanceItem;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.converter.MonthlyRecordToAttendanceItemConverter;
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
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.publicholiday.PublicHolidayRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.reserveleave.RsvLeaRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.specialholiday.SpecialHolidayRemainData;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItemRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

public class MonthlyRecordToAttendanceItemConverterImpl  extends AttendanceItemConverterCommonService
				implements MonthlyRecordToAttendanceItemConverter {

	private String employeeId;
	private YearMonth yearMonth;
	private ClosureId closureId;
	private ClosureDateDto closureDateDto;
	
	private MonthlyRecordToAttendanceItemConverterImpl(OptionalItemRepository optionalItem){

		super(new HashMap<>(), optionalItem);
		}
	
	public static MonthlyRecordToAttendanceItemConverter builder(OptionalItemRepository optionalItem) {
		return new MonthlyRecordToAttendanceItemConverterImpl(optionalItem);
	}

	@Override
	public OptionalItemRepository getOptionalItemRepo() {
		return optionalItem;
	}
	
	@Override
	
	public IntegrationOfMonthly toDomain() {
		return new IntegrationOfMonthly(toAttendanceTime(), 
										toAffiliation(), 
										toAnyItems(), 
										toAgreementTime(), 
										toAnnLeave(),
										toRsvLeave(),
										toAbsenceLeave(), 
										toDayOff(), 
										toSpecialHoliday(),
										toRemarks(),
										toMonCareHd(), 
										toMonChildHd(),
										toMonPublicHoliday());
	}
	
	@Override
	public MonthlyRecordToAttendanceItemConverter setData(IntegrationOfMonthly domain) {
		if (domain == null) return this;
		if (!domain.getAttendanceTime().isPresent()) return this;
		
		this.withAttendanceTime(domain.getAttendanceTime().orElse(null));
		this.withAffiliation(domain.getAffiliationInfo().orElse(null));
		this.withAnyItem(domain.getAnyItemList());
		this.withAnnLeave(domain.getAnnualLeaveRemain().orElse(null));
		this.withRsvLeave(domain.getReserveLeaveRemain().orElse(null));
		this.withSpecialLeave(domain.getSpecialLeaveRemain());
		this.withDayOff(domain.getMonthlyDayoffRemain().orElse(null));
		this.withAbsenceLeave(domain.getAbsenceLeaveRemain().orElse(null));
		this.withMonCareHd(domain.getCare().orElse(null));
		this.withMonChildHd(domain.getChildCare().orElse(null));
		this.withRemarks(domain.getRemarks());
		return this;
	}
	
	@Override
	public MonthlyRecordToAttendanceItemConverter withAffiliation(AffiliationInfoOfMonthly domain) {
		
		this.domainSource.put(ItemConst.MONTHLY_AFFILIATION_INFO_NAME, domain);
		this.dtoSource.put(ItemConst.MONTHLY_AFFILIATION_INFO_NAME, null);
		this.itemValues.put(ItemConst.MONTHLY_AFFILIATION_INFO_NAME, null);
		
		return this;
	}
	
	@Override
	public MonthlyRecordToAttendanceItemConverter withAttendanceTime(AttendanceTimeOfMonthly domain) {
		
		if (domain != null) {
			this.withBase(domain.getEmployeeId(), domain.getYearMonth(), domain.getClosureId(), domain.getClosureDate());
		}

		this.domainSource.put(ItemConst.MONTHLY_ATTENDANCE_TIME_NAME, domain);
		this.dtoSource.put(ItemConst.MONTHLY_ATTENDANCE_TIME_NAME, null);
		this.itemValues.put(ItemConst.MONTHLY_ATTENDANCE_TIME_NAME, null);
		
		return this;
	}
	
	@Override
	public MonthlyRecordToAttendanceItemConverter withBase(String employeeId, YearMonth yearMonth, ClosureId closureId,
			ClosureDate closureDate){
		this.employeeId = (employeeId);
		this.yearMonth = (yearMonth);
		this.closureId = (closureId);
//		this.closureDate = (closureDate);
		this.closureDateDto = new ClosureDateDto(closureDate.getClosureDay().v(), closureDate.getLastDayOfMonth());
		return this;
	}
	
	@Override
	public MonthlyRecordToAttendanceItemConverter withAnyItem(List<AnyItemOfMonthly> domains) {

		this.domainSource.put(ItemConst.MONTHLY_OPTIONAL_ITEM_NAME, domains);
		this.dtoSource.put(ItemConst.MONTHLY_OPTIONAL_ITEM_NAME, null);
		this.itemValues.put(ItemConst.MONTHLY_OPTIONAL_ITEM_NAME, null);
		
		return this;
	}
	
	@Override
	public MonthlyRecordToAttendanceItemConverter withAnnLeave(AnnLeaRemNumEachMonth domain) {

		this.domainSource.put(ItemConst.MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, domain);
		this.dtoSource.put(ItemConst.MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, null);
		this.itemValues.put(ItemConst.MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, null);
		
		return this;
	}
	
	@Override
	public MonthlyRecordToAttendanceItemConverter withRsvLeave(RsvLeaRemNumEachMonth domain) {
		
		this.domainSource.put(ItemConst.MONTHLY_RESERVE_LEAVING_REMAIN_NAME, domain);
		this.dtoSource.put(ItemConst.MONTHLY_RESERVE_LEAVING_REMAIN_NAME, null);
		this.itemValues.put(ItemConst.MONTHLY_RESERVE_LEAVING_REMAIN_NAME, null);
		
		return this;
	}

	@Override
	public MonthlyRecordToAttendanceItemConverter withDayOff(MonthlyDayoffRemainData domain) {
		
		this.domainSource.put(ItemConst.MONTHLY_OFF_REMAIN_NAME, domain);
		this.dtoSource.put(ItemConst.MONTHLY_OFF_REMAIN_NAME, null);
		this.itemValues.put(ItemConst.MONTHLY_OFF_REMAIN_NAME, null);
		
		return this;
	}

	@Override
	public MonthlyRecordToAttendanceItemConverter withSpecialLeave(List<SpecialHolidayRemainData> domain) {

		this.domainSource.put(ItemConst.MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME, domain);
		this.dtoSource.put(ItemConst.MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME, null);
		this.itemValues.put(ItemConst.MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME, null);
		return this;
	}

	@Override
	public MonthlyRecordToAttendanceItemConverter withAbsenceLeave(AbsenceLeaveRemainData domain) {

		this.domainSource.put(ItemConst.MONTHLY_ABSENCE_LEAVE_REMAIN_NAME, domain);
		this.dtoSource.put(ItemConst.MONTHLY_ABSENCE_LEAVE_REMAIN_NAME, null);
		this.itemValues.put(ItemConst.MONTHLY_ABSENCE_LEAVE_REMAIN_NAME, null);
		
		return this;
	}

	@Override
	public MonthlyRecordToAttendanceItemConverter withMonChildHd(ChildcareRemNumEachMonth monChildHdRemain) {

		this.domainSource.put(ItemConst.MONTHLY_CHILD_CARE_HD_REMAIN_NAME, monChildHdRemain);
		this.dtoSource.put(ItemConst.MONTHLY_CHILD_CARE_HD_REMAIN_NAME, null);
		this.itemValues.put(ItemConst.MONTHLY_CHILD_CARE_HD_REMAIN_NAME, null);
		
		return this;
	}

	@Override
	public MonthlyRecordToAttendanceItemConverter withMonCareHd(CareRemNumEachMonth monCareHdRemain) {
		
		this.domainSource.put(ItemConst.MONTHLY_CARE_HD_REMAIN_NAME, monCareHdRemain);
		this.dtoSource.put(ItemConst.MONTHLY_CARE_HD_REMAIN_NAME, null);
		this.itemValues.put(ItemConst.MONTHLY_CARE_HD_REMAIN_NAME, null);
		
		return this;
	}

	@Override
	public MonthlyRecordToAttendanceItemConverter withRemarks(List<RemarksMonthlyRecord> domain) {

		this.domainSource.put(ItemConst.MONTHLY_REMARKS_NAME, domain);
		this.dtoSource.put(ItemConst.MONTHLY_REMARKS_NAME, null);
		this.itemValues.put(ItemConst.MONTHLY_REMARKS_NAME, null);
		
		return this;
	}
	
	@Override
	public MonthlyRecordToAttendanceItemConverter completed() {
		return this;
	}

	@Override
	public MonthlyRecordToAttendanceItemConverter withAgreementTime(AgreementTimeOfManagePeriod domain) {

		this.domainSource.put(ItemConst.AGREEMENT_TIME_OF_MANAGE_PERIOD_NAME, domain);
		this.dtoSource.put(ItemConst.AGREEMENT_TIME_OF_MANAGE_PERIOD_NAME, null);
		this.itemValues.put(ItemConst.AGREEMENT_TIME_OF_MANAGE_PERIOD_NAME, null);
		
		return this;
	}
	
	@Override
	public Optional<AgreementTimeOfManagePeriod> toAgreementTime() {

		return Optional.ofNullable((AgreementTimeOfManagePeriod) getDomain(ItemConst.AGREEMENT_TIME_OF_MANAGE_PERIOD_NAME));
	}
	
	@Override
	public Optional<AffiliationInfoOfMonthly> toAffiliation() {

		return Optional.ofNullable((AffiliationInfoOfMonthly) getDomain(ItemConst.MONTHLY_AFFILIATION_INFO_NAME));
	}
	
	@Override
	public Optional<AttendanceTimeOfMonthly> toAttendanceTime() {

		return Optional.ofNullable((AttendanceTimeOfMonthly) getDomain(ItemConst.MONTHLY_ATTENDANCE_TIME_NAME));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<AnyItemOfMonthly> toAnyItems() {

		return (List<AnyItemOfMonthly>) getDomain(ItemConst.MONTHLY_OPTIONAL_ITEM_NAME);
	}
	
	@Override
	public Optional<AnnLeaRemNumEachMonth> toAnnLeave() {

		return Optional.ofNullable((AnnLeaRemNumEachMonth) getDomain(ItemConst.MONTHLY_ANNUAL_LEAVING_REMAIN_NAME));
	}
	
	@Override
	public Optional<RsvLeaRemNumEachMonth> toRsvLeave() {

		return Optional.ofNullable((RsvLeaRemNumEachMonth) getDomain(ItemConst.MONTHLY_RESERVE_LEAVING_REMAIN_NAME));
	}

	@Override
	public Optional<MonthlyDayoffRemainData> toDayOff() {

		return Optional.ofNullable((MonthlyDayoffRemainData) getDomain(ItemConst.MONTHLY_OFF_REMAIN_NAME));
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<SpecialHolidayRemainData> toSpecialHoliday() {

		return (List<SpecialHolidayRemainData>) getDomains(ItemConst.MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME);
	}

	@Override
	public Optional<AbsenceLeaveRemainData> toAbsenceLeave() {

		return Optional.ofNullable((AbsenceLeaveRemainData) getDomain(ItemConst.MONTHLY_ABSENCE_LEAVE_REMAIN_NAME));
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RemarksMonthlyRecord> toRemarks() {

		return (List<RemarksMonthlyRecord>) getDomains(ItemConst.MONTHLY_REMARKS_NAME);
	}

	@Override
	public Optional<CareRemNumEachMonth> toMonCareHd() {
		
		return Optional.ofNullable((CareRemNumEachMonth) getDomain(ItemConst.MONTHLY_CARE_HD_REMAIN_NAME));
	}

	@Override
	public Optional<ChildcareRemNumEachMonth> toMonChildHd() {
		
		return Optional.ofNullable((ChildcareRemNumEachMonth) getDomain(ItemConst.MONTHLY_CHILD_CARE_HD_REMAIN_NAME));
	}
	
	public Optional<PublicHolidayRemNumEachMonth> toMonPublicHoliday(){
		return Optional.ofNullable((PublicHolidayRemNumEachMonth) getDomain(ItemConst.MONTHLY_PUBLIC_HOLIDAYREMAIN_NAME));
	}

	@Override
	protected boolean isMonthly() {
		
		return true;
	}

	@Override
	protected boolean isAnyPeriod() {
		return false;
	}

	@Override
	protected Object correctOptionalItem(Object dto) {
		if (dto == null) {
			return dto;
		}
		
		AnyItemOfMonthlyDto optional = (AnyItemOfMonthlyDto) dto;
		
		optional.correctItems(loadOptionalItemMaster());
		
		return optional.toDomain(employeeId, yearMonth, closureId.value, closureDateDto);
	}

	@Override
	protected boolean isOpyionalItem(String type) {
		
		return type.equals(ItemConst.MONTHLY_OPTIONAL_ITEM_NAME);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void convertDomainToDto(String type) {
		switch (type) {
		case ItemConst.MONTHLY_ABSENCE_LEAVE_REMAIN_NAME:
			processOnDomain(type, c -> AbsenceLeaveRemainDataDto.from((AbsenceLeaveRemainData) c));
			break;
		case ItemConst.MONTHLY_AFFILIATION_INFO_NAME:
			processOnDomain(type, c -> AffiliationInfoOfMonthlyDto.from((AffiliationInfoOfMonthly) c));
			break;
		case ItemConst.MONTHLY_ATTENDANCE_TIME_NAME:
			processOnDomain(type, c -> AttendanceTimeOfMonthlyDto.from((AttendanceTimeOfMonthly) c));
			break;
		case ItemConst.MONTHLY_OPTIONAL_ITEM_NAME:
			processOnDomain(type, c -> AnyItemOfMonthlyDto.from((List<AnyItemOfMonthly>) c, loadOptionalItemMaster()));
			break;
		case ItemConst.MONTHLY_ANNUAL_LEAVING_REMAIN_NAME:
			processOnDomain(type, c -> AnnLeaRemNumEachMonthDto.from((AnnLeaRemNumEachMonth) c));
			break;
		case ItemConst.MONTHLY_RESERVE_LEAVING_REMAIN_NAME:
			processOnDomain(type, c -> RsvLeaRemNumEachMonthDto.from((RsvLeaRemNumEachMonth) c));
			break;
		case ItemConst.MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME:
			processOnDomain(type, c -> SpecialHolidayRemainDataDtoWrap.from((List<SpecialHolidayRemainData>) c));
			break;
		case ItemConst.MONTHLY_OFF_REMAIN_NAME:
			processOnDomain(type, c -> MonthlyDayoffRemainDataDto.from((MonthlyDayoffRemainData) c));
			break;
		case ItemConst.MONTHLY_REMARKS_NAME:
			processOnDomain(type, c -> MonthlyRemarksDto.from((List<RemarksMonthlyRecord>) c));
			break;
		case ItemConst.MONTHLY_CARE_HD_REMAIN_NAME:
			processOnDomain(type, c -> MonthlyCareHdRemainDto.from((CareRemNumEachMonth) c));
			break;
		case ItemConst.MONTHLY_CHILD_CARE_HD_REMAIN_NAME:
			processOnDomain(type, c -> MonthlyChildCareHdRemainDto.from((ChildcareRemNumEachMonth) c));
			break;
		case ItemConst.AGREEMENT_TIME_OF_MANAGE_PERIOD_NAME:
			processOnDomain(type, c -> AgreementTimeOfManagePeriodDto.from((AgreementTimeOfManagePeriod) c));
			break;
		default:
			break;
		}
	}

	@Override
	protected Object toDomain(ConvertibleAttendanceItem dto) {

		return ((MonthlyItemCommon) dto).toDomain(employeeId, yearMonth, closureId.value, closureDateDto);
	}
}
