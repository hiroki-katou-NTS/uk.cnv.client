package nts.uk.ctx.at.record.app.command.stamp.card.stampcard.management.personalengraving;

import java.util.Optional;

import lombok.Data;
import nts.arc.time.GeneralDateTime;
import nts.gul.location.GeoCoordinate;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.EmpInfoTerminalCode;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.AuthcMethod;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.RefectActualResult;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Relieve;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampMeans;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.WorkInformationStamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.support.SupportCardNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeCalArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockAtr;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.SetPreClockArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampType;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.OvertimeDeclaration;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkGroup;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;

/**
 * @author anhdt 
 */
@Data
public class RegisterStampDataCommand {

	private String datetime;
	private Integer authcMethod;
	private Integer stampMeans;

	private Integer reservationArt;
	private boolean changeHalfDay;
	private Integer goOutArt;
	private Integer setPreClockArt;
	private Integer changeClockArt;
	private Integer changeCalArt;

	private String cardNumberSupport;
	private String workLocationCD;
	private String workTimeCode;
	private Optional<OvertimeDeclaration> overtimeDeclaration;
	private Integer overTime;
	private Integer overLateNightTime;

	private Double latitude;
	private Double longitude;
	
	private String empInfoTerCode;
	private WorkGroupCommand workGroup;
	
	public Relieve toRelieve() {
		return new Relieve(AuthcMethod.valueOf(authcMethod), StampMeans.valueOf(stampMeans));
	}
	
	public StampType toStampType() {
		StampType stampType = StampType.getStampType(changeHalfDay, GoingOutReason.valueOf(goOutArt),
					SetPreClockArt.valueOf(setPreClockArt), changeClockArt == null ? null : ChangeClockAtr.valueOf(changeClockArt),
					ChangeCalArt.valueOf(changeCalArt));
		
			return stampType;

	}

	public RefectActualResult toRefectActualResult() {
		WorkInformationStamp workInformationStamp = new WorkInformationStamp(Optional.empty(), Optional.empty(),
				this.workLocationCD == null ? Optional.empty() : Optional.of(new WorkLocationCD(this.workLocationCD)),
				this.cardNumberSupport == null ? Optional.empty() : Optional.of(new SupportCardNumber(Integer.valueOf(cardNumberSupport))));
		
		WorkGroup workGroupResult = null;
		
		if (workGroup != null) {
			if (workGroup.getWorkCode1() != null) {
				workGroupResult = new WorkGroup(new WorkCode(workGroup.getWorkCode1()),
						Optional.ofNullable(workGroup.getWorkCode2() == null ?  null : new WorkCode(workGroup.getWorkCode2())),
						Optional.ofNullable(workGroup.getWorkCode3() == null ?  null : new WorkCode(workGroup.getWorkCode3())),
						Optional.ofNullable(workGroup.getWorkCode4() == null ?  null : new WorkCode(workGroup.getWorkCode4())),
						Optional.ofNullable(workGroup.getWorkCode5() == null ?  null : new WorkCode(workGroup.getWorkCode5())));
			}
		}
		
		return new RefectActualResult(workInformationStamp,
				workTimeCode != null ? new WorkTimeCode(workTimeCode) : null,
				overTime != null && overLateNightTime != null ? new OvertimeDeclaration(new AttendanceTime(overTime), new AttendanceTime(overLateNightTime)) : null,
				workGroupResult);
	}
	
	public GeoCoordinate toGeoCoordinate() {
		if(latitude == null || longitude == null) {
			return null;
		}
		return new GeoCoordinate(latitude, longitude);
	}
	
	public EmpInfoTerminalCode toEmpInfoTerCode(){
		return empInfoTerCode != null ? new EmpInfoTerminalCode(empInfoTerCode) : null;
	}
	
	public GeneralDateTime retriveDateTime() {
		return GeneralDateTime.fromString(this.datetime, "yyyy/MM/dd HH:mm:ss");
		
	}

}
