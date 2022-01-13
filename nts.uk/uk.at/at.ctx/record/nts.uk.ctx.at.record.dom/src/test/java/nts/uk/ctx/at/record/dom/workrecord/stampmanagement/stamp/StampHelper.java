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
 * 
 * @author tutk
 *
 */
public class StampHelper {
	
	public static WorkGroup group = new WorkGroup(new WorkCode("DUMMY"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()); //dummy

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
						ChangeClockAtr.valueOf(0),
						ChangeCalArt.valueOf(0)),
				new RefectActualResult(new WorkInformationStamp(Optional.empty(), 
																Optional.empty(),
																Optional.of(new WorkLocationCD("workLocationCD")), 
																Optional.of(new SupportCardNumber(9999))),
										new WorkTimeCode("workTimeCode"), 
										new OvertimeDeclaration(new AttendanceTime(0),
																new AttendanceTime(0)),
																group),
				new ImprintReflectionState(false, Optional.empty()),
				Optional.ofNullable(getGeoCoordinateDefault()),
				Optional.empty()
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
						ChangeClockAtr.valueOf(0),
						ChangeCalArt.valueOf(0)),
				new RefectActualResult(
						new WorkInformationStamp(Optional.empty(), Optional.empty(),
								Optional.of(new WorkLocationCD("workLocationCD")), 
										Optional.of(new SupportCardNumber(9999))), 
						new WorkTimeCode("workTimeCode"), 
						new OvertimeDeclaration(
								new AttendanceTime(1),
								new AttendanceTime(2)),
								group),
				new ImprintReflectionState(true, Optional.empty()),
				Optional.ofNullable(getGeoCoordinateDefault()),
				Optional.empty());

	}
	
	public static Stamp getStampByChangeClockArt(String stampNumber,ChangeClockAtr changeClockArt,GeneralDateTime dateTime) {
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
								new AttendanceTime(2)),
								group),
				new ImprintReflectionState(false, Optional.empty()),
				Optional.ofNullable(getGeoCoordinateDefault()),
				Optional.empty());
	}
	public static List<Stamp> getListStampDefault() {
		List<Stamp> data = new ArrayList<>();
		data.add(getStampDefault());
		data.add(new Stamp(new ContractCode("DUMMY"),new StampNumber("stampNumber"), GeneralDateTime.now(),
				new Relieve(AuthcMethod.valueOf(0), StampMeans.valueOf(0)),
				new StampType(false, GoingOutReason.valueOf(0), SetPreClockArt.valueOf(0), ChangeClockAtr.valueOf(0),
						ChangeCalArt.valueOf(0)),
				new RefectActualResult(new WorkInformationStamp(Optional.empty(), Optional.empty(),
						Optional.of(new WorkLocationCD("workLocationCD")), 
								Optional.of(new SupportCardNumber(9999))),
						new WorkTimeCode("workTimeCode"),
						new OvertimeDeclaration(new AttendanceTime(0), new AttendanceTime(0)),
						group),
				new ImprintReflectionState(false, Optional.empty()),
				Optional.ofNullable(getGeoCoordinateDefault()),
				Optional.empty())
				);
		data.add(new Stamp(new ContractCode("DUMMY"),new StampNumber("stampNumber"), GeneralDateTime.now(),
				new Relieve(AuthcMethod.valueOf(0), StampMeans.valueOf(0)),
				new StampType(false, GoingOutReason.valueOf(0), SetPreClockArt.valueOf(0), ChangeClockAtr.valueOf(0),
						ChangeCalArt.valueOf(0)),
				new RefectActualResult(new WorkInformationStamp(Optional.empty(), Optional.empty(),
						Optional.of(new WorkLocationCD("workLocationCD")), 
								Optional.of(new SupportCardNumber(9999))),
						new WorkTimeCode("workTimeCode"),
						new OvertimeDeclaration(new AttendanceTime(0), new AttendanceTime(0)),
						group),
				new ImprintReflectionState(false, Optional.empty()),
				Optional.ofNullable(getGeoCoordinateDefault()),
				Optional.empty())
				);
		return data;
	}
	
	public static StampType getStampTypeDefault() {
		return new StampType(
				false,
				GoingOutReason.valueOf(0), 
				SetPreClockArt.valueOf(0), 
				ChangeClockAtr.valueOf(0),
				ChangeCalArt.valueOf(0));
	}
	public static StampType getStampTypeHaveInput(boolean changeHalfDay, GoingOutReason goOutArt, SetPreClockArt setPreClockArt,
			ChangeClockAtr changeClockArt, ChangeCalArt changeCalArt) {
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
				getOvertimeDeclarationDefault(),
				group);
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
