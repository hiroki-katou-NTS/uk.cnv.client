package nts.uk.ctx.at.record.app.find.stamp.management.personalengraving.dto;

import java.util.Optional;

import lombok.Data;
import nts.arc.i18n.I18NText;
import nts.arc.time.GeneralDateTime;
import nts.gul.location.GeoCoordinate;
import nts.uk.ctx.at.record.app.find.stamp.management.ButtonSettingsDto;
import nts.uk.ctx.at.record.app.find.stamp.management.StampTypeDto;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.RefectActualResult;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampLocationInfor;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampMeans;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.StampInfoDisp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeCalArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockAtr;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ContentsStampType;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.SetPreClockArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.OvertimeDeclaration;

/**
 * @author anhdt
 *
 */
@Data
public class StampRecordDto {

	private String stampNumber;
	private String stampDate;
	private String stampTime;
	private String stampTimeWithSec;
	private String stampHow;
	private String stampArt;
	private String stampArtName;
	private Integer revervationAtr;
//	private String empInfoTerCode;
	private String timeStampType;

	// stamp
	private Integer authcMethod;
	private Integer stampMeans;

	private boolean changeHalfDay;
	private Integer goOutArt;
	private Integer setPreClockArt;
	private Integer changeClockArt;
	private String changeClockArtName;
	private Integer changeCalArt;

	private String cardNumberSupport;
	private String workLocationCD;
	private String workTimeCode;
	private String overTime;
	private String overLateNightTime;

	private boolean reflectedCategory;

	private Optional<StampLocationInfor> locationInfor;
	private boolean outsideAreaAtr;
	private Double latitude;
	private Double longitude;

	private String attendanceTime;

	private int correctTimeStampValue;

	private int buttonValueType;

	public StampRecordDto(Stamp stamp) {
//		this.stampNumber = stampRecord.getStampNumber().v();
//		GeneralDateTime stampDate = stampRecord.getStampDateTime();
		GeneralDateTime stampDate = stamp.getStampDateTime();
		this.stampDate = stampDate.toString("yyyy/MM/dd");
		this.stampTime = stampDate.toString("HH:mm");
		this.stampTimeWithSec = stampDate.toString();
		this.stampHow = getCorrectTimeString(stamp != null ? stamp.getRelieve().getStampMeans() : null);
		this.stampArt = "";

		// this.revervationAtr = stampRecord.getRevervationAtr().value;
//		this.empInfoTerCode = stampRecord.getEmpInfoTerCode().isPresent() ? stampRecord.getEmpInfoTerCode().get().v()
//				: null;

		// stamp
		if (stamp != null) {
			this.authcMethod = stamp.getRelieve().getAuthcMethod().value;
			this.stampMeans = stamp.getRelieve().getStampMeans().value;

			StampType type = stamp.getType();
			this.stampArtName = type.createStampTypeDisplay();
			this.changeHalfDay = type.isChangeHalfDay();
			this.goOutArt = type.getGoOutArt().isPresent() ? type.getGoOutArt().get().value : null;
			this.setPreClockArt = type.getSetPreClockArt().value;
			this.changeClockArt = type.getChangeClockArt().value;
			this.changeClockArtName = type.getChangeClockArt().nameId;
			this.changeCalArt = type.getChangeCalArt().value;

			RefectActualResult raResult = stamp.getRefActualResults();
			this.cardNumberSupport = (raResult.getWorkInforStamp().isPresent() && raResult.getWorkInforStamp().get().getCardNumberSupport().isPresent()) ? raResult.getWorkInforStamp().get().getCardNumberSupport().get().toString()
					: null;
			this.workLocationCD = (raResult.getWorkInforStamp().isPresent() && raResult.getWorkInforStamp().get().getWorkLocationCD().isPresent()) ? raResult.getWorkInforStamp().get().getWorkLocationCD().get().v()
					: null;
			this.workTimeCode = raResult.getWorkTimeCode().isPresent() ? raResult.getWorkTimeCode().get().v() : null;
			if (raResult.getOvertimeDeclaration().isPresent()) {
				OvertimeDeclaration overtime = raResult.getOvertimeDeclaration().get();
				this.overTime = getTimeString(overtime.getOverTime().v());
				this.overLateNightTime = getTimeString(overtime.getOverLateNightTime().v());
			}
			this.reflectedCategory = stamp.getImprintReflectionStatus().isReflectedCategory();
			if (stamp.getLocationInfor().isPresent()) {
				GeoCoordinate stampLocate = stamp.getLocationInfor().get();
				this.latitude = stampLocate.getLatitude();
				this.longitude = stampLocate.getLongitude();
			}
			this.attendanceTime = stamp.getAttendanceTime().isPresent()
					? getTimeString(stamp.getAttendanceTime().get().v())
					: null;
			this.timeStampType = this.stampArtName;
			this.correctTimeStampValue = StampRecordDto.getCorrectTimeStampValue(this.changeHalfDay, this.goOutArt,
					this.setPreClockArt, this.changeClockArt, this.changeCalArt);
			this.buttonValueType = ButtonSettingsDto.toButtonValueType(StampTypeDto.fromDomain(stamp.getType()));
		} else {
			this.buttonValueType = ButtonSettingsDto.toButtonValueType(null);
		}
	}

	public StampRecordDto(StampInfoDisp info) {

		this.stampNumber = info.getStampNumber().v();
		GeneralDateTime stampDate = info.getStampDatetime();
		this.stampDate = info.getStampDatetime().toString("yyyy/MM/dd");
		this.stampTime = info.getStampDatetime().toString("HH:mm");
		this.stampTimeWithSec = stampDate.toString();
		Stamp stamp = info.getStamp().map(m -> m).orElse(null);
		this.stampHow = getCorrectTimeString(stamp != null ? stamp.getRelieve().getStampMeans() : null);
		this.stampArt = info.getStampAtr();
		this.stampArtName = info.getStampAtr();
		this.timeStampType = this.stampArtName;

		// stamp
		if (stamp != null) {
			this.authcMethod = stamp.getRelieve().getAuthcMethod().value;
			this.stampMeans = stamp.getRelieve().getStampMeans().value;

			StampType type = stamp.getType();
			this.changeHalfDay = type.isChangeHalfDay();
			this.goOutArt = type.getGoOutArt().isPresent() ? type.getGoOutArt().get().value : null;
			this.setPreClockArt = type.getSetPreClockArt().value;
			this.changeClockArt = type.getChangeClockArt().value;
			this.changeClockArtName = type.getChangeClockArt().nameId;
			this.changeCalArt = type.getChangeCalArt().value;

			RefectActualResult raResult = stamp.getRefActualResults();
			this.cardNumberSupport = (raResult.getWorkInforStamp().isPresent() && raResult.getWorkInforStamp().get().getCardNumberSupport().isPresent()) ? raResult.getWorkInforStamp().get().getCardNumberSupport().get().toString()
					: null;
			this.workLocationCD = (raResult.getWorkInforStamp().isPresent() && raResult.getWorkInforStamp().get().getWorkLocationCD().isPresent()) ? raResult.getWorkInforStamp().get().getWorkLocationCD().get().v()
					: null;
			this.workTimeCode = raResult.getWorkTimeCode().isPresent() ? raResult.getWorkTimeCode().get().v() : null;
			if (raResult.getOvertimeDeclaration().isPresent()) {
				OvertimeDeclaration overtime = raResult.getOvertimeDeclaration().get();
				this.overTime = getTimeString(overtime.getOverTime().v());
				this.overLateNightTime = getTimeString(overtime.getOverLateNightTime().v());
			}
			this.reflectedCategory = stamp.getImprintReflectionStatus().isReflectedCategory();
			if (stamp.getLocationInfor().isPresent()) {
				GeoCoordinate stampLocate = stamp.getLocationInfor().get();
				this.latitude = stampLocate.getLatitude();
				this.longitude = stampLocate.getLongitude();
			}
			this.attendanceTime = stamp.getAttendanceTime().isPresent()
					? getTimeString(stamp.getAttendanceTime().get().v())
					: null;
			this.correctTimeStampValue = StampRecordDto.getCorrectTimeStampValue(this.changeHalfDay, this.goOutArt,
					this.setPreClockArt, this.changeClockArt, this.changeCalArt);
			this.buttonValueType = ButtonSettingsDto.toButtonValueType(StampTypeDto.fromDomain(stamp.getType()));

		} else {
			this.buttonValueType = ButtonSettingsDto.toButtonValueType(null);
		}
	}

	public static int getCorrectTimeStampValue(boolean changeHalfDay, Integer goOutArt, Integer setPreClockArt,
			Integer changeClockArt, Integer changeCalArt) {
		// 19 ?????? NONE NONE False => ?????????
		// 20 ???????????? NONE NONE False => ??????
		// do 19 v?? 20 c??ng ??i???u ki???n nh?? nhau v?? hi???n th??? nh?? nhau n??n ch??? tr??? v??? 1 gi??
		// tr??? 19 th??i
		if (changeClockArt == null) {
			return 19;
		}

		// 1 ?????? None None False => ?????????
		if (changeClockArt == ChangeClockAtr.GOING_TO_WORK.value && setPreClockArt == SetPreClockArt.NONE.value
				&& changeCalArt == ChangeCalArt.NONE.value && changeHalfDay == false) {
			return ContentsStampType.WORK.value;
		}
		// 2 ?????? ?????? None False => ?????????
		if (changeClockArt == ChangeClockAtr.GOING_TO_WORK.value && changeCalArt == ChangeCalArt.NONE.value
				&& setPreClockArt == SetPreClockArt.DIRECT.value && changeHalfDay == false) {
			return ContentsStampType.WORK_STRAIGHT.value;
		}
		// 3 ?????? ?????? None False => ?????????
		if (changeClockArt == ChangeClockAtr.GOING_TO_WORK.value && changeCalArt == ChangeCalArt.EARLY_APPEARANCE.value
				&& setPreClockArt == SetPreClockArt.NONE.value && changeHalfDay == false) {
			return ContentsStampType.WORK_EARLY.value;
		}
		// 4 ?????? ?????? None False => ?????????
		if (changeClockArt == ChangeClockAtr.GOING_TO_WORK.value && changeCalArt == ChangeCalArt.BRARK.value
				&& setPreClockArt == SetPreClockArt.NONE.value && changeHalfDay == false) {
			return ContentsStampType.WORK_BREAK.value;
		}
		// 5 ?????? None None False => ?????????
		if (changeClockArt == ChangeClockAtr.WORKING_OUT.value && changeCalArt == ChangeCalArt.NONE.value
				&& setPreClockArt == SetPreClockArt.NONE.value && changeHalfDay == false) {
			return ContentsStampType.DEPARTURE.value;
		}
		// 6 ?????? None ?????? False => ?????????
		if (changeClockArt == ChangeClockAtr.WORKING_OUT.value && changeCalArt == ChangeCalArt.NONE.value
				&& setPreClockArt == SetPreClockArt.BOUNCE.value && changeHalfDay == false) {
			return ContentsStampType.DEPARTURE_BOUNCE.value;
		}
		// 7 ?????? None ?????? False => ?????????
		if (changeClockArt == ChangeClockAtr.WORKING_OUT.value && changeCalArt == ChangeCalArt.OVER_TIME.value
				&& setPreClockArt == SetPreClockArt.NONE.value && changeHalfDay == false) {
			return ContentsStampType.DEPARTURE_OVERTIME.value;
		}
		// 8 ?????? None None False => ?????????
		if (changeClockArt == ChangeClockAtr.GO_OUT.value && changeCalArt == ChangeCalArt.NONE.value
				&& setPreClockArt == SetPreClockArt.NONE.value && changeHalfDay == false) {
			return ContentsStampType.OUT.value;
		}
		// 9 ?????? None None False => ?????????
		if (changeClockArt == ChangeClockAtr.RETURN.value && changeCalArt == ChangeCalArt.NONE.value
				&& setPreClockArt == SetPreClockArt.NONE.value && changeHalfDay == false) {
			return ContentsStampType.RETURN.value;
		}
		// 10 ?????? None None False => ?????????
		if (changeClockArt == ChangeClockAtr.OVER_TIME.value && changeCalArt == ChangeCalArt.NONE.value
				&& setPreClockArt == SetPreClockArt.NONE.value && changeHalfDay == false) {
			return ContentsStampType.GETTING_STARTED.value;
		}
		// 11 ?????? None None False => ?????????
		if (changeClockArt == ChangeClockAtr.BRARK.value && changeCalArt == ChangeCalArt.NONE.value
				&& setPreClockArt == SetPreClockArt.NONE.value && changeHalfDay == false) {
			return ContentsStampType.DEPAR.value;
		}
		// 12 ???????????? None None False => ?????????
		if (changeClockArt == ChangeClockAtr.TEMPORARY_WORK.value && changeCalArt == ChangeCalArt.NONE.value
				&& setPreClockArt == SetPreClockArt.NONE.value && changeHalfDay == false) {
			return ContentsStampType.TEMPORARY_WORK.value;
		}
		// 13 ???????????? None None False => ?????????
		if (changeClockArt == ChangeClockAtr.TEMPORARY_LEAVING.value && changeCalArt == ChangeCalArt.NONE.value
				&& setPreClockArt == SetPreClockArt.NONE.value && changeHalfDay == false) {
			return ContentsStampType.TEMPORARY_LEAVING.value;
		}
		// 14 ???????????? None None False => ?????????
		if (changeClockArt == ChangeClockAtr.START_OF_SUPPORT.value && changeCalArt == ChangeCalArt.NONE.value
				&& setPreClockArt == SetPreClockArt.NONE.value && changeHalfDay == false) {
			return ContentsStampType.START_SUPPORT.value;
		}
		// 15 ???????????? None None False => ?????????
		if (changeClockArt == ChangeClockAtr.END_OF_SUPPORT.value && changeCalArt == ChangeCalArt.NONE.value
				&& setPreClockArt == SetPreClockArt.NONE.value && changeHalfDay == false) {
			return ContentsStampType.END_SUPPORT.value;
		}
		// 16 ??????+?????? None None False => ?????????
//		if (changeClockArt == ChangeClockArt.SUPPORT.value && changeCalArt == ChangeCalArt.NONE.value
//				&& setPreClockArt == SetPreClockArt.NONE.value && changeHalfDay == false) {
//			return ContentsStampType.WORK_SUPPORT.value;
//		}
		// 17 ???????????? ?????? NONE False => ?????????
		if (changeClockArt == ChangeClockAtr.START_OF_SUPPORT.value && changeCalArt == ChangeCalArt.EARLY_APPEARANCE.value
				&& setPreClockArt == SetPreClockArt.NONE.value && changeHalfDay == false) {
			return ContentsStampType.START_SUPPORT_EARLY_APPEARANCE.value;
		}
		// 18 ???????????? ?????? NONE False => ?????????
		if (changeClockArt == ChangeClockAtr.START_OF_SUPPORT.value && changeCalArt == ChangeCalArt.BRARK.value
				&& setPreClockArt == SetPreClockArt.NONE.value && changeHalfDay == false) {
			return ContentsStampType.START_SUPPORT_BREAK.value;
		}

		// 19 va 20 tam thoi khong co
		// 19 ?????? NONE NONE False => ?????????
		// 20 ???????????? NONE NONE False => ??????

		return -1;
	}

	public String getCorectTtimeStampType() {
		if (getCorrectTimeStampValue(this.changeHalfDay, this.goOutArt, this.setPreClockArt, this.changeClockArt,
				this.changeCalArt) == -1) {
			return null;
		}

		return ContentsStampType.valueOf(getCorrectTimeStampValue(this.changeHalfDay, this.goOutArt,
				this.setPreClockArt, this.changeClockArt, this.changeCalArt)).nameId;
	}

	public String getCorrectTimeString(StampMeans mean) {

		if (mean == null) {
			return " ";
		}

		switch (mean) {
		case NAME_SELECTION:
			return I18NText.getText("KDP002_120");
		case FINGER_AUTHC:
			return I18NText.getText("KDP002_120");
		case IC_CARD:
			return I18NText.getText("KDP002_120");
		case INDIVITION:
			return I18NText.getText("KDP002_120");
		case PORTAL:
			return I18NText.getText("KDP002_120");
		case SMART_PHONE:
			return I18NText.getText("KDP002_121");
		case TIME_CLOCK:
			return I18NText.getText("KDP002_122");
		case TEXT:
			return " ";
		case RICOH_COPIER:
			return I18NText.getText("KDP002_120");
		default:
			break;
		}

		return " ";
	}

	public String getTimeString(Integer t) {
		int hours = t / 60;
		int minutes = t % 60;
		return String.format("%02d:%02d", hours, minutes);
	}

}
