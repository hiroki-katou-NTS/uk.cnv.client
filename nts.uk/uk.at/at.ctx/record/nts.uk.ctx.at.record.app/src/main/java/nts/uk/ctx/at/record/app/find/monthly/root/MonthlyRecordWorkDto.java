package nts.uk.ctx.at.record.app.find.monthly.root;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.record.app.find.monthly.root.common.ClosureDateDto;
import nts.uk.ctx.at.record.app.find.monthly.root.common.MonthlyItemCommon;
import nts.uk.ctx.at.record.app.find.monthly.root.dto.SpecialHolidayRemainDataDtoWrap;
import nts.uk.ctx.at.shared.dom.attendance.util.item.AttendanceItemDataGate;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.MonthlyDayoffRemainData;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemUtil.AttendanceItemType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemRoot;
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
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItem;

@Data
/** 月別実績（WORK） */
@EqualsAndHashCode(callSuper = false)
@AttendanceItemRoot(isContainer = true, itemType = AttendanceItemType.MONTHLY_ITEM)
public class MonthlyRecordWorkDto extends MonthlyItemCommon {

	/***/
	private static final long serialVersionUID = 1L;
	
	/** 年月: 年月 */
	private YearMonth yearMonth;

	/** 社員ID: 社員ID */
	private String employeeId;

	/** 締めID: 締めID */
	private int closureID;

	/** 締め日: 日付 */
	private ClosureDateDto closureDate;

	/** 月別実績の所属情報: 月別実績の所属情報 */
	@AttendanceItemLayout(jpPropertyName = MONTHLY_AFFILIATION_INFO_NAME, layout = MONTHLY_AFFILIATION_INFO_CODE)
	private AffiliationInfoOfMonthlyDto affiliation;

	/** 月別実績の勤怠時間: 月別実績の勤怠時間 */
	@AttendanceItemLayout(jpPropertyName = MONTHLY_ATTENDANCE_TIME_NAME, layout = MONTHLY_ATTENDANCE_TIME_CODE)
	private AttendanceTimeOfMonthlyDto attendanceTime;

	/** 月別実績の任意項目 */
	@AttendanceItemLayout(jpPropertyName = MONTHLY_OPTIONAL_ITEM_NAME, layout = MONTHLY_OPTIONAL_ITEM_CODE)
	private AnyItemOfMonthlyDto anyItem;
	
	/** 年休月別残数データ: 年休月別残数データ */
	@AttendanceItemLayout(jpPropertyName = MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, layout = MONTHLY_ANNUAL_LEAVING_REMAIN_CODE)
	private AnnLeaRemNumEachMonthDto annLeave;

	/** 積立年休月別残数データ: 積立年休月別残数データ */
	@AttendanceItemLayout(jpPropertyName = MONTHLY_RESERVE_LEAVING_REMAIN_NAME, layout = MONTHLY_RESERVE_LEAVING_REMAIN_CODE)
	private RsvLeaRemNumEachMonthDto rsvLeave;

	/** 特別休暇月別残数データ: 特別休暇残数月別データ */
	@AttendanceItemLayout(jpPropertyName = MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME, layout = MONTHLY_SPECIAL_HOLIDAY_REMAIN_CODE)
	private SpecialHolidayRemainDataDtoWrap specialHoliday;

	/** 代休月別残数データ: 代休月別残数データ */
	@AttendanceItemLayout(jpPropertyName = MONTHLY_OFF_REMAIN_NAME, layout = MONTHLY_OFF_REMAIN_CODE)
	private MonthlyDayoffRemainDataDto dayOff;

	/** 振休月別残数データ: 振休月別残数データ */
	@AttendanceItemLayout(jpPropertyName = MONTHLY_ABSENCE_LEAVE_REMAIN_NAME, layout = MONTHLY_ABSENCE_LEAVE_REMAIN_CODE)
	private AbsenceLeaveRemainDataDto absenceLeave;
	
	/** 月別実績の備考: 月別実績の備考 */
	@AttendanceItemLayout(jpPropertyName = MONTHLY_REMARKS_NAME, layout = MONTHLY_REMARKS_CODE)
	private MonthlyRemarksDto remarks;
	
	/** 介護休暇月別残数データ: 介護休暇月別残数データ */
	@AttendanceItemLayout(jpPropertyName = MONTHLY_CARE_HD_REMAIN_NAME, layout = MONTHLY_CARE_HD_REMAIN_CODE)
	private MonthlyCareHdRemainDto care;
	
	/** 子の看護月別残数データ: 子の看護月別残数データ */
	@AttendanceItemLayout(jpPropertyName = MONTHLY_CHILD_CARE_HD_REMAIN_NAME, layout = MONTHLY_CHILD_CARE_HD_REMAIN_CODE)
	private MonthlyChildCareHdRemainDto childCare;
	/** 公休月別残数データ*/
	@AttendanceItemLayout(jpPropertyName = MONTHLY_PUBLIC_HOLIDAYREMAIN_NAME, layout = MONTHLY_PUBLIC_HOLIDAYREMAIN_CODE)
	private MonthlyPublicHolidayRemainDto publicHoliday;
	
	/** 管理期間の36協定時間: 管理期間の36協定時間 */
	@AttendanceItemLayout(jpPropertyName = AGREEMENT_TIME_OF_MANAGE_PERIOD_NAME, layout = AGREEMENT_TIME_OF_MANAGE_PERIOD_CODE)
	private AgreementTimeOfManagePeriodDto agreementTime;
	
	@Override
	public String employeeId() {
		return this.employeeId;
	}

	@Override
	public YearMonth yearMonth() {
		return this.yearMonth;
	}

	public MonthlyRecordWorkDto employeeId(String employeeId) {
		this.employeeId = employeeId;
		return this;
	}

	public MonthlyRecordWorkDto yearMonth(YearMonth yearMonth) {
		this.yearMonth = yearMonth;
		return this;
	}

	public MonthlyRecordWorkDto closureID(int closureID) {
		this.closureID = closureID;
		return this;
	}

	public MonthlyRecordWorkDto closureDate(ClosureDateDto closureDate) {
		this.closureDate = closureDate;
		return this;
	}
	
	public static MonthlyRecordWorkDto builder(){
		return new MonthlyRecordWorkDto();
	}
	
	public MonthlyRecordWorkDto withAffiliation(AffiliationInfoOfMonthlyDto affiliation){
		this.affiliation = affiliation;
		return this;
	}
	
	public MonthlyRecordWorkDto withAttendanceTime(AttendanceTimeOfMonthlyDto attendanceTime){
		this.attendanceTime = attendanceTime;
		return this;
	}
	
	public MonthlyRecordWorkDto withAnyItem(AnyItemOfMonthlyDto anyItem){
		this.anyItem = anyItem;
		return this;
	}
	
	public MonthlyRecordWorkDto withAnnLeave(AnnLeaRemNumEachMonthDto annLeave){
		this.annLeave = annLeave;
		return this;
	}
	
	public MonthlyRecordWorkDto withRsvLeave(RsvLeaRemNumEachMonthDto rsvLeave){
		this.rsvLeave = rsvLeave;
		return this;
	}
	
	public MonthlyRecordWorkDto withSpecialHoliday(SpecialHolidayRemainDataDtoWrap specialHoliday){
		this.specialHoliday = specialHoliday;
		return this;
	}
	
	public MonthlyRecordWorkDto withDayOff(MonthlyDayoffRemainDataDto dayOff){
		this.dayOff = dayOff;
		return this;
	}
	
	public MonthlyRecordWorkDto withAbsenceLeave(AbsenceLeaveRemainDataDto absenceLeave){
		this.absenceLeave = absenceLeave;
		return this;
	}
	
	public MonthlyRecordWorkDto withRemarks(MonthlyRemarksDto remarks){
		this.remarks = remarks;
		return this;
	}
	
	public MonthlyRecordWorkDto withCare(MonthlyCareHdRemainDto care){
		this.care = care;
		return this;
	}
	
	public MonthlyRecordWorkDto withChildCare(MonthlyChildCareHdRemainDto childCare){
		this.childCare = childCare;
		return this;
	}
	
	public MonthlyRecordWorkDto withAgreementTime(AgreementTimeOfManagePeriodDto agreementTime){
		this.agreementTime = agreementTime;
		return this;
	}
	
	public AffiliationInfoOfMonthly toAffiliation(){
		return this.affiliation == null ? null : this.affiliation.toDomain(getEmployeeId(), getYearMonth(), getClosureID(), getClosureDate());
	}
	
	public AgreementTimeOfManagePeriod toAgreementTime(){
		return this.agreementTime == null ? null : this.agreementTime.toDomain(getEmployeeId(), getYearMonth(), getClosureID(), getClosureDate());
	}
	
	public AttendanceTimeOfMonthly toAttendanceTime(){
		return this.attendanceTime == null ? null : this.attendanceTime.toDomain(getEmployeeId(), getYearMonth(), getClosureID(), getClosureDate());
	}
	
	public List<AnyItemOfMonthly> toAnyItems(){
		return this.anyItem.toDomain(getEmployeeId(), getYearMonth(), getClosureID(), getClosureDate());
	}
	
	public AnnLeaRemNumEachMonth toAnnLeave(){
		return this.annLeave == null ? null : this.annLeave.toDomain(getEmployeeId(), getYearMonth(), getClosureID(), getClosureDate());
	}
	
	public RsvLeaRemNumEachMonth toRsvLeave(){
		return this.rsvLeave == null ? null : this.rsvLeave.toDomain(getEmployeeId(), getYearMonth(), getClosureID(), getClosureDate());
	}
	
	public List<SpecialHolidayRemainData> toSpecialHoliday(){
		return this.specialHoliday.toDomain(getEmployeeId(), getYearMonth(), getClosureID(), getClosureDate());
	}
	
	public MonthlyDayoffRemainData toDayOff(){
		return this.dayOff == null ? null : this.dayOff.toDomain(getEmployeeId(), getYearMonth(), getClosureID(), getClosureDate());
	}
	
	public AbsenceLeaveRemainData toAbsenceLeave(){
		return this.absenceLeave == null ? null :  this.absenceLeave.toDomain(getEmployeeId(), getYearMonth(), getClosureID(), getClosureDate());
	}
	
	public List<RemarksMonthlyRecord> toRemarks(){
		return this.remarks == null ? new ArrayList<>() : this.remarks.toDomain(getEmployeeId(), getYearMonth(), getClosureID(), getClosureDate());
	}
	
	public CareRemNumEachMonth toMonCare(){
		return this.care == null ? null :  this.care.toDomain(getEmployeeId(), getYearMonth(), getClosureID(), getClosureDate());
	}
	
	public ChildcareRemNumEachMonth toMonChildCare(){
		return this.childCare == null ? null :  this.childCare.toDomain(getEmployeeId(), getYearMonth(), getClosureID(), getClosureDate());
	}
	
	public PublicHolidayRemNumEachMonth toMonPublicHpliday(){
		return this.publicHoliday == null ? null : this.publicHoliday.toDomain(getEmployeeId(), getYearMonth(), getClosureID(), getClosureDate());
	}
	
	

	@Override
	public IntegrationOfMonthly toDomain(String employeeId, YearMonth ym, int closureID, ClosureDateDto closureDate) {
		if (this.attendanceTime == null) return new IntegrationOfMonthly();
		return new IntegrationOfMonthly(
				Optional.ofNullable(this.attendanceTime == null ? null : this.attendanceTime.toDomain(employeeId, ym, closureID, closureDate)),
				Optional.ofNullable(this.affiliation == null ? null : this.affiliation.toDomain(employeeId, ym, closureID, closureDate)),
				this.anyItem == null ? new ArrayList<>() : this.anyItem.toDomain(employeeId, ym, closureID, closureDate),
				Optional.ofNullable(agreementTime == null ? null : agreementTime.toDomain(employeeId, ym, closureID, closureDate)),
				Optional.ofNullable(this.annLeave == null ? null : this.annLeave.toDomain(employeeId, ym, closureID, closureDate)),
				Optional.ofNullable(this.rsvLeave == null ? null : this.rsvLeave.toDomain(employeeId, ym, closureID, closureDate)),
				Optional.ofNullable(this.absenceLeave == null ? null : this.absenceLeave.toDomain(employeeId, ym, closureID, closureDate)),
				Optional.ofNullable(this.dayOff == null ? null : this.dayOff.toDomain(employeeId, ym, closureID, closureDate)),
				this.specialHoliday == null ? new ArrayList<>()
						: this.specialHoliday.toDomain(employeeId, ym, closureID, closureDate),
				this.remarks == null ? new ArrayList<>(): this.remarks.toDomain(employeeId, ym, closureID, closureDate),
				Optional.ofNullable(this.care == null ? null : this.care.toDomain(employeeId, ym, closureID, closureDate)),
				Optional.ofNullable(this.childCare == null ? null : this.childCare.toDomain(employeeId, ym, closureID, closureDate)),
				Optional.ofNullable(this.publicHoliday == null ? null : this.publicHoliday.toDomain(employeeId, ym, closureID, closureDate)));
	}
	
	public static MonthlyRecordWorkDto fromOnlyAttTime(IntegrationOfMonthly domain) {
		return fromDtoWithOptional(domain, new HashMap<>());
	}
	
	public static MonthlyRecordWorkDto fromDtoWithOptional(IntegrationOfMonthly domain, Map<Integer, OptionalItem> optionalMaster){
		MonthlyRecordWorkDto dto = new MonthlyRecordWorkDto();
		if(domain != null) {
			dto.setAttendanceTime(AttendanceTimeOfMonthlyDto.from(domain.getAttendanceTime().orElse(null)));
			dto.setAbsenceLeave(AbsenceLeaveRemainDataDto.from(domain.getAbsenceLeaveRemain().orElse(null)));
			dto.setAgreementTime(AgreementTimeOfManagePeriodDto.from(domain.getAgreementTime().orElse(null)));
			dto.setAnnLeave(AnnLeaRemNumEachMonthDto.from(domain.getAnnualLeaveRemain().orElse(null)));
			dto.setAnyItem(AnyItemOfMonthlyDto.from(domain.getAnyItemList(), optionalMaster));
			dto.setCare(MonthlyCareHdRemainDto.from(domain.getCare().orElse(null)));
			dto.setChildCare(MonthlyChildCareHdRemainDto.from(domain.getChildCare().orElse(null)));
			dto.setDayOff(MonthlyDayoffRemainDataDto.from(domain.getMonthlyDayoffRemain().orElse(null)));
			dto.setRemarks(MonthlyRemarksDto.from(domain.getRemarks()));
			dto.setRsvLeave(RsvLeaRemNumEachMonthDto.from(domain.getReserveLeaveRemain().orElse(null)));
			dto.setSpecialHoliday(SpecialHolidayRemainDataDtoWrap.from(domain.getSpecialLeaveRemain()));
			dto.setPublicHoliday(MonthlyPublicHolidayRemainDto.from(domain.getPublicHolidayLeaveRemain().orElse(null)));
			domain.getAffiliationInfo().ifPresent(aff -> {
				dto.setAffiliation(AffiliationInfoOfMonthlyDto.from(aff));
				dto.setYearMonth(aff.getYearMonth());
				dto.setEmployeeId(aff.getEmployeeId());
				dto.setClosureDate(ClosureDateDto.from(aff.getClosureDate()));
				dto.setClosureID(aff.getClosureId().value);
			});
			dto.exsistData();
		}
		return dto;
	}
	
	@Override
	public Optional<AttendanceItemDataGate> get(String path) {
		switch (path) {
		case MONTHLY_AFFILIATION_INFO_NAME:
			return Optional.ofNullable(this.affiliation);
		case MONTHLY_ATTENDANCE_TIME_NAME:
			return Optional.ofNullable(this.attendanceTime);
		case MONTHLY_OPTIONAL_ITEM_NAME:
			return Optional.ofNullable(this.anyItem);
		case MONTHLY_ANNUAL_LEAVING_REMAIN_NAME:
			return Optional.ofNullable(this.annLeave);
		case MONTHLY_RESERVE_LEAVING_REMAIN_NAME:
			return Optional.ofNullable(this.rsvLeave);
		case MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME:
			return Optional.ofNullable(this.specialHoliday);
		case MONTHLY_OFF_REMAIN_NAME:
			return Optional.ofNullable(this.dayOff);
		case MONTHLY_ABSENCE_LEAVE_REMAIN_NAME:
			return Optional.ofNullable(this.absenceLeave);
		case MONTHLY_REMARKS_NAME:
			return Optional.ofNullable(this.remarks);
		case MONTHLY_CARE_HD_REMAIN_NAME:
			return Optional.ofNullable(this.care);
		case MONTHLY_CHILD_CARE_HD_REMAIN_NAME:
			return Optional.ofNullable(this.childCare);
		case AGREEMENT_TIME_OF_MANAGE_PERIOD_NAME:
			return Optional.ofNullable(this.agreementTime);
		case MONTHLY_PUBLIC_HOLIDAYREMAIN_NAME:
			return Optional.ofNullable(this.publicHoliday);
		default:
			return Optional.empty();
		}
	}
	
	
	@Override
	public void set(String path, AttendanceItemDataGate value) {
		switch (path) {
		case MONTHLY_AFFILIATION_INFO_NAME:
			this.affiliation = (AffiliationInfoOfMonthlyDto) value;
			break;
		case MONTHLY_ATTENDANCE_TIME_NAME:
			this.attendanceTime = (AttendanceTimeOfMonthlyDto) value;
			break;
		case MONTHLY_OPTIONAL_ITEM_NAME:
			this.anyItem = (AnyItemOfMonthlyDto) value;
			break;
		case MONTHLY_ANNUAL_LEAVING_REMAIN_NAME:
			this.annLeave = (AnnLeaRemNumEachMonthDto) value;
			break;
		case MONTHLY_RESERVE_LEAVING_REMAIN_NAME:
			this.rsvLeave = (RsvLeaRemNumEachMonthDto) value;
			break;
		case MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME:
			this.specialHoliday = (SpecialHolidayRemainDataDtoWrap) value;
			break;
		case MONTHLY_OFF_REMAIN_NAME:
			this.dayOff = (MonthlyDayoffRemainDataDto) value;
			break;
		case MONTHLY_ABSENCE_LEAVE_REMAIN_NAME:
			this.absenceLeave = (AbsenceLeaveRemainDataDto) value;
			break;
		case MONTHLY_REMARKS_NAME:
			this.remarks = (MonthlyRemarksDto) value;
			break;
		case MONTHLY_CARE_HD_REMAIN_NAME:
			this.care = (MonthlyCareHdRemainDto) value;
			break;
		case MONTHLY_CHILD_CARE_HD_REMAIN_NAME:
			this.childCare = (MonthlyChildCareHdRemainDto) value;
			break;
		case AGREEMENT_TIME_OF_MANAGE_PERIOD_NAME:
			this.agreementTime = (AgreementTimeOfManagePeriodDto) value;
			break;
		case MONTHLY_PUBLIC_HOLIDAYREMAIN_NAME:
			this.publicHoliday = (MonthlyPublicHolidayRemainDto) value;
			break;
		default:
			break;
		}
	}
	
	@Override
	public AttendanceItemDataGate newInstanceOf(String path) {
		switch (path) {
		case MONTHLY_AFFILIATION_INFO_NAME:
			return new AffiliationInfoOfMonthlyDto();
		case MONTHLY_ATTENDANCE_TIME_NAME:
			return new AttendanceTimeOfMonthlyDto();
		case MONTHLY_OPTIONAL_ITEM_NAME:
			return new AnyItemOfMonthlyDto();
		case MONTHLY_ANNUAL_LEAVING_REMAIN_NAME:
			return new AnnLeaRemNumEachMonthDto();
		case MONTHLY_RESERVE_LEAVING_REMAIN_NAME:
			return new RsvLeaRemNumEachMonthDto();
		case MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME:
			return new SpecialHolidayRemainDataDtoWrap();
		case MONTHLY_OFF_REMAIN_NAME:
			return new MonthlyDayoffRemainDataDto();
		case MONTHLY_ABSENCE_LEAVE_REMAIN_NAME:
			return new AbsenceLeaveRemainDataDto();
		case MONTHLY_REMARKS_NAME:
			return new MonthlyRemarksDto();
		case MONTHLY_CARE_HD_REMAIN_NAME:
			return new MonthlyCareHdRemainDto();
		case MONTHLY_CHILD_CARE_HD_REMAIN_NAME:
			return new MonthlyChildCareHdRemainDto();
		case AGREEMENT_TIME_OF_MANAGE_PERIOD_NAME:
			return new AgreementTimeOfManagePeriodDto();
		case MONTHLY_PUBLIC_HOLIDAYREMAIN_NAME:
			return new MonthlyPublicHolidayRemainDto();
		default:
			return null;
		}
	}
	
	@Override
	public boolean isRoot() { return true; }
	
	@Override
	public boolean isContainer() { return true; }
}
