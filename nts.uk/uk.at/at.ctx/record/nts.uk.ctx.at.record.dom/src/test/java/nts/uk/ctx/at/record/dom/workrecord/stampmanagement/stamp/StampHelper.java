package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.gul.location.GeoCoordinate;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCard;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.support.SupportCardNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeCalArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.SetPreClockArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampType;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.OvertimeDeclaration;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
/**
 * 
 * @author tutk
 *
 */
public class StampHelper {

	public static Stamp getStampDefault() {
		return new Stamp(new ContractCode("DUMMY"),
				new StampNumber("stampNumber"),
				GeneralDateTime.ymdhms(2021, 3, 15, 1, 20, 0), 
				new Relieve(
						AuthcMethod.valueOf(0), 
						StampMeans.valueOf(0)), 
				new StampType(
						false,
						GoingOutReason.valueOf(0), 
						SetPreClockArt.valueOf(0), 
						ChangeClockArt.valueOf(0),
						ChangeCalArt.valueOf(0)),
				new RefectActualResult(new WorkInformationStamp(Optional.empty(), Optional.empty(),
						Optional.of(new WorkLocationCD("workLocationCD")), 
						Optional.of(new SupportCardNumber(9999))),
						new WorkTimeCode("workTimeCode"), 
						new OvertimeDeclaration(
								new AttendanceTime(0),
								new AttendanceTime(0))),
				new ImprintReflectionState(false, Optional.empty()),
				Optional.ofNullable(getGeoCoordinateDefault()),
				Optional.empty(),
				"DUMMY"
				);
	}
	
	public static Stamp getStampDefaultIsTrue() {
		return new Stamp(new ContractCode("DUMMY"),
				new StampNumber("stampNumber"),
				GeneralDateTime.now(), 
				new Relieve(
						AuthcMethod.valueOf(0), 
						StampMeans.valueOf(0)), 
				new StampType(
						false,
						GoingOutReason.valueOf(0), 
						SetPreClockArt.valueOf(0), 
						ChangeClockArt.valueOf(0),
						ChangeCalArt.valueOf(0)),
				new RefectActualResult(
						new WorkInformationStamp(Optional.empty(), Optional.empty(),
								Optional.of(new WorkLocationCD("workLocationCD")), 
										Optional.of(new SupportCardNumber(9999))), 
						new WorkTimeCode("workTimeCode"), 
						new OvertimeDeclaration(
								new AttendanceTime(1),
								new AttendanceTime(2))),
				new ImprintReflectionState(true, Optional.empty()),
				Optional.ofNullable(getGeoCoordinateDefault()),
				Optional.empty(),
				"DUMMY");
	}
	
	public static Stamp getStampByChangeClockArt(String stampNumber,ChangeClockArt changeClockArt,GeneralDateTime dateTime) {
		return new Stamp(new ContractCode("DUMMY"),
				new StampNumber(stampNumber),
				dateTime, 
				new Relieve(
						AuthcMethod.valueOf(0), 
						StampMeans.valueOf(0)), 
				new StampType(
						false,
						GoingOutReason.valueOf(0), 
						SetPreClockArt.valueOf(0), 
						changeClockArt,
						ChangeCalArt.valueOf(0)),
				new RefectActualResult(
						new WorkInformationStamp(Optional.empty(), Optional.empty(),
								Optional.of(new WorkLocationCD("workLocationCD")), 
										Optional.of(new SupportCardNumber(9999))), 
						new WorkTimeCode("workTimeCode"), 
						new OvertimeDeclaration(
								new AttendanceTime(1),
								new AttendanceTime(2))),
				new ImprintReflectionState(false, Optional.empty()),
				Optional.ofNullable(getGeoCoordinateDefault()),
				Optional.empty(),
				"DUMMY"
				);
	}
	public static List<Stamp> getListStampDefault() {
		List<Stamp> data = new ArrayList<>();
		data.add(getStampDefault());
		data.add(new Stamp(new ContractCode("DUMMY"),new StampNumber("stampNumber"), GeneralDateTime.now(),
				new Relieve(AuthcMethod.valueOf(0), StampMeans.valueOf(0)),
				new StampType(false, GoingOutReason.valueOf(0), SetPreClockArt.valueOf(0), ChangeClockArt.valueOf(0),
						ChangeCalArt.valueOf(0)),
				new RefectActualResult(new WorkInformationStamp(Optional.empty(), Optional.empty(),
						Optional.of(new WorkLocationCD("workLocationCD")), 
								Optional.of(new SupportCardNumber(9999))),
						new WorkTimeCode("workTimeCode"),
						new OvertimeDeclaration(new AttendanceTime(0), new AttendanceTime(0))),
				new ImprintReflectionState(false, Optional.empty()),
				Optional.ofNullable(getGeoCoordinateDefault()),
				Optional.empty(),
				"DUMMY")
				);
		data.add(new Stamp(new ContractCode("DUMMY"),new StampNumber("stampNumber"), GeneralDateTime.now(),
				new Relieve(AuthcMethod.valueOf(0), StampMeans.valueOf(0)),
				new StampType(false, GoingOutReason.valueOf(0), SetPreClockArt.valueOf(0), ChangeClockArt.valueOf(0),
						ChangeCalArt.valueOf(0)),
				new RefectActualResult(new WorkInformationStamp(Optional.empty(), Optional.empty(),
						Optional.of(new WorkLocationCD("workLocationCD")), 
								Optional.of(new SupportCardNumber(9999))),
						new WorkTimeCode("workTimeCode"),
						new OvertimeDeclaration(new AttendanceTime(0), new AttendanceTime(0))),
				new ImprintReflectionState(false, Optional.empty()),
				Optional.ofNullable(getGeoCoordinateDefault()),
				Optional.empty(),
				"DUMMY")
				);
		return data;
	}
	
	public static StampType getStampTypeDefault() {
		return new StampType(
				false,
				GoingOutReason.valueOf(0), 
				SetPreClockArt.valueOf(0), 
				ChangeClockArt.valueOf(0),
				ChangeCalArt.valueOf(0));
	}
	public static StampType getStampTypeHaveInput(boolean changeHalfDay, GoingOutReason goOutArt, SetPreClockArt setPreClockArt,
			ChangeClockArt changeClockArt, ChangeCalArt changeCalArt) {
		return new StampType(
				changeHalfDay, goOutArt, setPreClockArt, changeClockArt, changeCalArt);
	}
	
	public static OvertimeDeclaration getOvertimeDeclarationDefault() {
		return new OvertimeDeclaration(
				new AttendanceTime(1), new AttendanceTime(10));
	}
	
	public static RefectActualResult getRefectActualResultDefault() {
		return new RefectActualResult(new WorkInformationStamp(Optional.empty(), Optional.empty(),
				Optional.of(new WorkLocationCD("workLocationCD")), 
						Optional.of(new SupportCardNumber(9999))),
				new WorkTimeCode("workTimeCode"),
				getOvertimeDeclarationDefault());
	}
	
	public static Relieve getRelieveDefault() {
		return new Relieve(AuthcMethod.valueOf(1), StampMeans.valueOf(0));
	}
	
	public static StampLocationInfor getStampLocationInforDefault() {
		return new StampLocationInfor(
				getGeoCoordinateDefault(),
				false);
	}
	public static GeoCoordinate getGeoCoordinateDefault() {
		return new GeoCoordinate(1, 2);
		
	}

	public static StampCard getStampCardByInput(String stampCardId, String stampNumber, GeneralDate registerDate) {
		return new StampCard(new ContractCode("contractCd"), new StampNumber(stampNumber), "employeeId", registerDate,
				stampCardId);
		
	}
	
}
