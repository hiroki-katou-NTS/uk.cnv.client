package nts.uk.ctx.at.record.app.command.stamp.card.stampcard.management.personalengraving;

import java.util.Optional;

import lombok.Data;
import nts.arc.time.GeneralDateTime;
import nts.gul.location.GeoCoordinate;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.EmpInfoTerminalCode;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.AuthcMethod;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.RefectActualResult;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Relieve;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampMeans;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonType;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeCalArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ReservationArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.SetPreClockArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampType;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailyattendance.breakouting.GoingOutReason;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailywork.worktime.overtimedeclaration.OvertimeDeclaration;
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
	
	private Integer empInfoTerCode;
	
	public Relieve toRelieve() {
		return new Relieve(AuthcMethod.valueOf(authcMethod), StampMeans.valueOf(stampMeans));
	}

	public ButtonType toButtonType() {
		
			StampType stampType = StampType.getStampType(changeHalfDay, GoingOutReason.valueOf(goOutArt),
					SetPreClockArt.valueOf(setPreClockArt), changeClockArt == null ? null : ChangeClockArt.valueOf(changeClockArt),
					ChangeCalArt.valueOf(changeCalArt));
		
		if(reservationArt != 2 && reservationArt != 1) {
			return new ButtonType(ReservationArt.valueOf(reservationArt), stampType);
		}
		return new ButtonType(ReservationArt.valueOf(reservationArt), null);
	}

	public RefectActualResult toRefectActualResult() {
		return new RefectActualResult(cardNumberSupport, new WorkLocationCD(workLocationCD),
				workTimeCode != null ? new WorkTimeCode(workTimeCode) : null,
				overTime != null && overLateNightTime != null ? new OvertimeDeclaration(new AttendanceTime(overTime), new AttendanceTime(overLateNightTime)) : null);
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
