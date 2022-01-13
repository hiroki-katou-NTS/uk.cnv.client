package nts.uk.ctx.at.request.infra.entity.application.overtime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.*;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTime;
import nts.uk.ctx.at.request.dom.application.overtime.ApplicationTime;
import nts.uk.ctx.at.request.dom.application.overtime.HolidayMidNightTime;
import nts.uk.ctx.at.request.dom.application.overtime.OverTimeShiftNight;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeAppAtr;
import nts.uk.ctx.at.request.dom.application.overtime.ReasonDivergence;
import nts.uk.ctx.at.request.dom.application.overtime.CommonAlgorithm.DivergenceReason;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.common.TimeZoneWithWorkNo;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeOfExistMinus;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.deviationtime.DiverdenceReasonCode;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.holidaywork.StaturoryAtrOfHolidayWork;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * Refactor5
 * @author hoangnd
 *
 */
@Entity
@Table(name = "KRQDT_APP_OVERTIME")
@AllArgsConstructor
@NoArgsConstructor
public class KrqdtAppOverTime extends ContractUkJpaEntity implements Serializable{
	
	public static final long serialVersionUID = 1L;
	
	@EmbeddedId
	public KrqdtAppOvertimePK krqdtAppOvertimePK;
	
	@Column(name = "OVERTIME_ATR")
	public int overtimeAtr;
	
	@Column(name = "WORK_TYPE_CD")
	public String workTypeCode;
	
	@Column(name = "WORK_TIME_CD")
	public String workTimeCode;
	
	@Column(name = "WORK_TIME_START1")
	public Integer workTimeStart1;
	
	@Column(name = "WORK_TIME_END1")
	public Integer workTimeEnd1;
	
	@Column(name = "WORK_TIME_START2")
	public Integer workTimeStart2;
	
	@Column(name = "WORK_TIME_END2")
	public Integer workTimeEnd2;
	
	@Column(name = "DIVERGENCE_NO1")
	public Integer divergenceNo1;
	
	@Column(name = "DIVERGENCE_CD1")
	public String divergenceCD1;
	
	@Column(name = "DIVERGENCE_REASON1")
	public String divergenceReason1;
	
	@Column(name = "DIVERGENCE_NO2")
	public Integer divergenceNo2;
	
	@Column(name = "DIVERGENCE_CD2")
	public String divergenceCD2;
	
	@Column(name = "DIVERGENCE_REASON2")
	public String divergenceReason2;
	
	@Column(name = "FLEX_EXCESS_TIME")
	public Integer flexExcessTime;
	
	@Column(name = "OVERTIME_NIGHT")
	public Integer overTimeNight;
	
	@Column(name = "TOTAL_NIGHT")
	public Integer totalNight;
	
	@Column(name = "LEGAL_HD_NIGHT")
	public Integer legalHdNight;
	
	@Column(name = "NON_LEGAL_HD_NIGHT")
	public Integer nonLegalHdNight;
	
	@Column(name = "NON_LEGAL_PUBLIC_HD_NIGHT")
	public Integer nonLegalPublicHdNight;
	
	@Column(name = "BREAK_TIME_START1")
	public Integer breakTimeStart1;
	
	@Column(name = "BREAK_TIME_END1")
	public Integer breakTimeEnd1;
	
	@Column(name = "BREAK_TIME_START2")
	public Integer breakTimeStart2;
	
	@Column(name = "BREAK_TIME_END2")
	public Integer breakTimeEnd2;
	
	@Column(name = "BREAK_TIME_START3")
	public Integer breakTimeStart3;
	
	@Column(name = "BREAK_TIME_END3")
	public Integer breakTimeEnd3;
	
	@Column(name = "BREAK_TIME_START4")
	public Integer breakTimeStart4;
	
	@Column(name = "BREAK_TIME_END4")
	public Integer breakTimeEnd4;
	
	@Column(name = "BREAK_TIME_START5")
	public Integer breakTimeStart5;
	
	@Column(name = "BREAK_TIME_END5")
	public Integer breakTimeEnd5;
	
	@Column(name = "BREAK_TIME_START6")
	public Integer breakTimeStart6;
	
	@Column(name = "BREAK_TIME_END6")
	public Integer breakTimeEnd6;
	
	@Column(name = "BREAK_TIME_START7")
	public Integer breakTimeStart7;
	
	@Column(name = "BREAK_TIME_END7")
	public Integer breakTimeEnd7;
	
	@Column(name = "BREAK_TIME_START8")
	public Integer breakTimeStart8;
	
	@Column(name = "BREAK_TIME_END8")
	public Integer breakTimeEnd8;
	
	@Column(name = "BREAK_TIME_START9")
	public Integer breakTimeStart9;
	
	@Column(name = "BREAK_TIME_END9")
	public Integer breakTimeEnd9;
	
	@Column(name = "BREAK_TIME_START10")
	public Integer breakTimeStart10;
	
	@Column(name = "BREAK_TIME_END10")
	public Integer breakTimeEnd10;
	
	@OneToMany(targetEntity = KrqdtAppOvertimeInput.class, mappedBy = "appOvertime", cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = "KRQDT_APP_OVERTIME_INPUT")
	public List<KrqdtAppOvertimeInput> overtimeInputs;

	@OneToMany(targetEntity = KrqdtAppOvertimeMultiTimes.class, mappedBy = "appOvertime", cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = "KRQDT_APP_OVERTIME_MULTI_TIMES")
	@OrderBy("pk.overtimeNumber ASC")
	public List<KrqdtAppOvertimeMultiTimes> multipleOvertimes;

	@Override
	protected Object getKey() {
		return krqdtAppOvertimePK;
	}
	
	public AppOverTime toDomain() {
		if (getKey() == null) return null;
		AppOverTime appOverTime = new AppOverTime();
		
		appOverTime.setOverTimeClf(EnumAdaptor.valueOf(overtimeAtr, OvertimeAppAtr.class));
		if (StringUtils.isNotBlank(workTypeCode) || StringUtils.isNotBlank(workTimeCode)) {
			WorkInformation workInformation = new WorkInformation("", "");
			appOverTime.setWorkInfoOp(Optional.of(workInformation));
			if (StringUtils.isNotBlank(workTypeCode)) {
				workInformation.setWorkTypeCode(workTypeCode);
			}
			if (StringUtils.isNotBlank(workTimeCode)) {
				workInformation.setWorkTimeCode(workTimeCode);
			}
		}
		List<TimeZoneWithWorkNo> workHoursOp = new ArrayList<TimeZoneWithWorkNo>();
		List<TimeZoneWithWorkNo> breakTimeOp = new ArrayList<TimeZoneWithWorkNo>();
		if (workTimeStart1 != null && workTimeEnd1 != null) {
			
			TimeZoneWithWorkNo timeZoneWithWorkNo = new TimeZoneWithWorkNo(1, workTimeStart1, workTimeEnd1);
			workHoursOp.add(timeZoneWithWorkNo);
			appOverTime.setWorkHoursOp(Optional.of(workHoursOp));
		}
		if (workTimeStart2 != null && workTimeEnd2 != null) {
			
			TimeZoneWithWorkNo timeZoneWithWorkNo = new TimeZoneWithWorkNo(2, workTimeStart2, workTimeEnd2);
			workHoursOp.add(timeZoneWithWorkNo);		
		}
		appOverTime.setOpReversionReason(Optional.empty());
		ReasonDivergence reasonDivergence1 = new ReasonDivergence();
		ReasonDivergence reasonDivergence2 = new ReasonDivergence();
		
		ApplicationTime applicationTime = new ApplicationTime();
		applicationTime.setFlexOverTime(Optional.ofNullable(flexExcessTime != null ? new AttendanceTimeOfExistMinus(flexExcessTime) : null));
		// 112610
		if (divergenceNo1 != null) {
			reasonDivergence1.setDiviationTime(divergenceNo1);
		}
		if (divergenceCD1 != null) {
			DiverdenceReasonCode reasonCode = new DiverdenceReasonCode(divergenceCD1);
			reasonDivergence1.setReasonCode(reasonCode);
		}
		if (divergenceReason1 != null) {
			DivergenceReason diReason = new DivergenceReason(divergenceReason1);
			reasonDivergence1.setReason(diReason);
		}
		
		if (divergenceNo2 != null) {
			reasonDivergence2.setDiviationTime(divergenceNo2);
		}
		if (divergenceCD2 != null) {
			DiverdenceReasonCode reasonCode = new DiverdenceReasonCode(divergenceCD2);
			reasonDivergence2.setReasonCode(reasonCode);
		}
		if (divergenceReason2 != null) {
			DivergenceReason diReason = new DivergenceReason(divergenceReason2);
			reasonDivergence2.setReason(diReason);
		}
		List<ReasonDivergence> reasonDissociation;
		List<ReasonDivergence> reasonDissociation2;
		
		if (!reasonDivergence1.isNullProp()) {
			
			reasonDissociation = new ArrayList<ReasonDivergence>();
			reasonDissociation.add(reasonDivergence1);
			applicationTime.setReasonDissociation(Optional.of(reasonDissociation));
		}
		
		if (!reasonDivergence2.isNullProp()) {
			reasonDissociation2 = new ArrayList<ReasonDivergence>();
			reasonDissociation2.add(reasonDivergence2);
			if(!applicationTime.getReasonDissociation().isPresent()) {
				applicationTime.setReasonDissociation(Optional.of(reasonDissociation2));
			}
			if (applicationTime.getReasonDissociation().isPresent()) {
				applicationTime.getReasonDissociation().get().add(reasonDivergence2);			
			} 
		}

		OverTimeShiftNight overTimeShiftNight = new OverTimeShiftNight();
		if (overTimeNight == null 
				&& totalNight == null
				&& legalHdNight == null 
				&& nonLegalHdNight == null 
				&& nonLegalPublicHdNight == null) {
			applicationTime.setOverTimeShiftNight(Optional.empty());
			
		} else {
			if (overTimeNight != null) {
				overTimeShiftNight.setOverTimeMidNight(new AttendanceTime(overTimeNight));
			}
			if (totalNight != null) {
				overTimeShiftNight.setMidNightOutSide(new AttendanceTime(totalNight));
			}
			List<HolidayMidNightTime> midNightHolidayTimes = new ArrayList<HolidayMidNightTime>();
			if (legalHdNight != null) {
				HolidayMidNightTime holidayMidNightTime = new HolidayMidNightTime();
				holidayMidNightTime.setAttendanceTime(new AttendanceTime(legalHdNight));
				holidayMidNightTime.setLegalClf(StaturoryAtrOfHolidayWork.WithinPrescribedHolidayWork);
				midNightHolidayTimes.add(holidayMidNightTime);
			}
			
			if (nonLegalHdNight != null) {
				HolidayMidNightTime holidayMidNightTime = new HolidayMidNightTime();
				holidayMidNightTime.setAttendanceTime(new AttendanceTime(nonLegalHdNight));
				holidayMidNightTime.setLegalClf(StaturoryAtrOfHolidayWork.ExcessOfStatutoryHolidayWork);
				midNightHolidayTimes.add(holidayMidNightTime);
			}
			
			
			if (nonLegalPublicHdNight != null) {
				HolidayMidNightTime holidayMidNightTime = new HolidayMidNightTime();
				holidayMidNightTime.setAttendanceTime(new AttendanceTime(nonLegalPublicHdNight));
				holidayMidNightTime.setLegalClf(StaturoryAtrOfHolidayWork.PublicHolidayWork);
				midNightHolidayTimes.add(holidayMidNightTime);
			}
			overTimeShiftNight.setMidNightHolidayTimes(midNightHolidayTimes);
			applicationTime.setOverTimeShiftNight(Optional.of(overTimeShiftNight));
		
			
		}
		
		if (breakTimeStart1 != null && breakTimeEnd1 != null) {
			TimeZoneWithWorkNo timeZoneWithWorkNo = new TimeZoneWithWorkNo(1, breakTimeStart1, breakTimeEnd1);
			breakTimeOp.add(timeZoneWithWorkNo);
			appOverTime.setBreakTimeOp(Optional.of(breakTimeOp));
		}
		
		if (breakTimeStart2 != null && breakTimeEnd2 != null) {
			TimeZoneWithWorkNo timeZoneWithWorkNo = new TimeZoneWithWorkNo(2, breakTimeStart2, breakTimeEnd2);
			breakTimeOp.add(timeZoneWithWorkNo);
			if (!appOverTime.getBreakTimeOp().isPresent()) {
				appOverTime.setBreakTimeOp(Optional.of(breakTimeOp));				
			}
		}
		if (breakTimeStart3 != null && breakTimeEnd3 != null) {
			TimeZoneWithWorkNo timeZoneWithWorkNo = new TimeZoneWithWorkNo(3, breakTimeStart3, breakTimeEnd3);
			breakTimeOp.add(timeZoneWithWorkNo);
			if (!appOverTime.getBreakTimeOp().isPresent()) {
				appOverTime.setBreakTimeOp(Optional.of(breakTimeOp));				
			}
		}
		if (breakTimeStart4 != null && breakTimeEnd4 != null) {
			TimeZoneWithWorkNo timeZoneWithWorkNo = new TimeZoneWithWorkNo(4, breakTimeStart4, breakTimeEnd4);
			breakTimeOp.add(timeZoneWithWorkNo);
			if (!appOverTime.getBreakTimeOp().isPresent()) {
				appOverTime.setBreakTimeOp(Optional.of(breakTimeOp));				
			}
		}
		
		if (breakTimeStart5 != null && breakTimeEnd5 != null) {
			TimeZoneWithWorkNo timeZoneWithWorkNo = new TimeZoneWithWorkNo(5, breakTimeStart5, breakTimeEnd5);
			breakTimeOp.add(timeZoneWithWorkNo);
			if (!appOverTime.getBreakTimeOp().isPresent()) {
				appOverTime.setBreakTimeOp(Optional.of(breakTimeOp));				
			}
		}
		
		if (breakTimeStart6 != null && breakTimeEnd6 != null) {
			TimeZoneWithWorkNo timeZoneWithWorkNo = new TimeZoneWithWorkNo(6, breakTimeStart6, breakTimeEnd6);
			breakTimeOp.add(timeZoneWithWorkNo);
			if (!appOverTime.getBreakTimeOp().isPresent()) {
				appOverTime.setBreakTimeOp(Optional.of(breakTimeOp));				
			}
		}
		
		if (breakTimeStart7 != null && breakTimeEnd7 != null) {
			TimeZoneWithWorkNo timeZoneWithWorkNo = new TimeZoneWithWorkNo(7, breakTimeStart7, breakTimeEnd7);
			breakTimeOp.add(timeZoneWithWorkNo);
			if (!appOverTime.getBreakTimeOp().isPresent()) {
				appOverTime.setBreakTimeOp(Optional.of(breakTimeOp));				
			}
		}
		
		if (breakTimeStart8 != null && breakTimeEnd8 != null) {
			TimeZoneWithWorkNo timeZoneWithWorkNo = new TimeZoneWithWorkNo(8, breakTimeStart8, breakTimeEnd8);
			breakTimeOp.add(timeZoneWithWorkNo);
			if (!appOverTime.getBreakTimeOp().isPresent()) {
				appOverTime.setBreakTimeOp(Optional.of(breakTimeOp));				
			}
		}
		
		if (breakTimeStart9 != null && breakTimeEnd9 != null) {
			TimeZoneWithWorkNo timeZoneWithWorkNo = new TimeZoneWithWorkNo(9, breakTimeStart9, breakTimeEnd9);
			breakTimeOp.add(timeZoneWithWorkNo);
			if (!appOverTime.getBreakTimeOp().isPresent()) {
				appOverTime.setBreakTimeOp(Optional.of(breakTimeOp));				
			}
		}
		
		if (breakTimeStart10 != null && breakTimeEnd10 != null) {
			TimeZoneWithWorkNo timeZoneWithWorkNo = new TimeZoneWithWorkNo(10, breakTimeStart10, breakTimeEnd10);
			breakTimeOp.add(timeZoneWithWorkNo);
			if (!appOverTime.getBreakTimeOp().isPresent()) {
				appOverTime.setBreakTimeOp(Optional.of(breakTimeOp));				
			}
		}
		appOverTime.setApplicationTime(applicationTime);
		if (!CollectionUtil.isEmpty(overtimeInputs)) {
			appOverTime.getApplicationTime().setApplicationTime(overtimeInputs.stream()
																			  .map(x -> x.toDomain())
																			  .collect(Collectors.toList()));
		}

		if (!CollectionUtil.isEmpty(multipleOvertimes)) {
			appOverTime.setMultipleTimesOp(Optional.of(KrqdtAppOvertimeMultiTimes.toDomain(multipleOvertimes)));
		}

		return appOverTime;
	}
	
}
