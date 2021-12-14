package nts.uk.ctx.at.record.dom.workrecord.erroralarm.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.monthlycheckcondition.algorithm.MonthlyRecordValuesDto;
import nts.uk.ctx.at.shared.dom.adapter.attendanceitemname.MonthlyAttendanceItemNameDto;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.MonthlyDayoffRemainData;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.service.AttendanceItemConvertFactory;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthlyRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.affiliation.AffiliationInfoOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.affiliation.AffiliationInfoOfMonthlyRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.anyitem.AnyItemOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.anyitem.AnyItemOfMonthlyRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.absenceleave.AbsenceLeaveRemainData;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.absenceleave.AbsenceLeaveRemainDataRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.AnnLeaRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.AnnLeaRemNumEachMonthRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.dayoff.MonthlyDayoffRemainDataRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.reserveleave.RsvLeaRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.reserveleave.RsvLeaRemNumEachMonthRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.specialholiday.SpecialHolidayRemainData;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.specialholiday.SpecialHolidayRemainDataRepository;

@Stateless
public class DataCheckAlarmListService {
	/** 月別実績の勤怠時間 */
	@Inject
	private AttendanceTimeOfMonthlyRepository attendanceTimeRepo;
	/** 月別実績の所属情報 */
	@Inject
	private AffiliationInfoOfMonthlyRepository affiliationInfoRepo;
	/** 月別実績の任意項目 */
	@Inject
	private AnyItemOfMonthlyRepository anyItemRepo;
	/** 年休月別残数データ */
	@Inject
	private AnnLeaRemNumEachMonthRepository annualLeaveRepo;
	/** 積立年休月別残数データ */
	@Inject
	private RsvLeaRemNumEachMonthRepository reserveLeaveRepo;
	/** 振休月別残数データ */
	@Inject
	private AbsenceLeaveRemainDataRepository absLeaRemRepo;
	/** 代休月別残数データ */
	@Inject
	private MonthlyDayoffRemainDataRepository monDayoffRemRepo;
	/** 特別休暇月別残数データ */
	@Inject
	private SpecialHolidayRemainDataRepository spcLeaRemRepo;
	/** 勤怠項目変換 */
	@Inject
	private AttendanceItemConvertFactory attendanceItemConverterFact;
	/**
	 * 月別実績データを取得する
	 * @param lstSid
	 * @param mPeriod
	 * @param itemIds 勤怠項目Id一覧
	 */
	public Map<String, List<MonthlyRecordValuesDto>> getMonthData(List<String> employeeIds,
			YearMonthPeriod period, List<Integer> itemIds) {
		Map<String, List<MonthlyRecordValuesDto>> results = new HashMap<>();

		// 検索キーを準備する
		val yearMonths = period.yearMonthsBetween();
		
		// 月別実績の勤怠時間を取得する
		val attendanceTimes = attendanceTimeRepo.findBySidsAndYearMonths(employeeIds, yearMonths);
		Map<String, Map<YearMonth, AttendanceTimeOfMonthly>> attendanceTimeMap = new HashMap<>();
		for (val attendanceTime : attendanceTimes){
			val employeeId = attendanceTime.getEmployeeId();
			val yearMonth = attendanceTime.getYearMonth();
			attendanceTimeMap.putIfAbsent(employeeId, new HashMap<>());
			if (attendanceTimeMap.get(employeeId).containsKey(yearMonth)){
				attendanceTime.sum(attendanceTimeMap.get(employeeId).get(yearMonth));
				attendanceTimeMap.get(employeeId).put(yearMonth, attendanceTime);
				continue;
			}
			attendanceTimeMap.get(employeeId).putIfAbsent(yearMonth, attendanceTime);
		}
		
		// 月別実績の所属情報を取得する
		val affiliationInfos = affiliationInfoRepo.findBySidsAndYearMonths(employeeIds, yearMonths);
		Map<String, Map<YearMonth, AffiliationInfoOfMonthly>> affiliationInfoMap = new HashMap<>();
		for (val affiliationInfo : affiliationInfos){
			val employeeId = affiliationInfo.getEmployeeId();
			val yearMonth = affiliationInfo.getYearMonth();
			affiliationInfoMap.putIfAbsent(employeeId, new HashMap<>());
			affiliationInfoMap.get(employeeId).putIfAbsent(yearMonth, affiliationInfo);
		}
		
		// 月別実績の任意項目を取得する
		val anyItems = anyItemRepo.findBySidsAndMonths(employeeIds, yearMonths);
		Map<String, Map<YearMonth, List<AnyItemOfMonthly>>> anyItemMap = new HashMap<>();
		for (val anyItem : anyItems){
			val employeeId = anyItem.getEmployeeId();
			val yearMonth = anyItem.getYearMonth();
			anyItemMap.putIfAbsent(employeeId, new HashMap<>());
			anyItemMap.get(employeeId).putIfAbsent(yearMonth, new ArrayList<>());
			ListIterator<AnyItemOfMonthly> itrAnyItem = anyItemMap.get(employeeId).get(yearMonth).listIterator();
			boolean isSum = false;
			while (itrAnyItem.hasNext()){
				val outAnyItem = itrAnyItem.next();
				if (outAnyItem.getAnyItemId() == anyItem.getAnyItemId()){
					outAnyItem.sum(anyItem);
					itrAnyItem.set(outAnyItem);
					isSum = true;
					break;
				}
			}
			if (!isSum) anyItemMap.get(employeeId).get(yearMonth).add(anyItem);
		}
		
		// 年休月別残数データを取得する
		val annualLeaves = annualLeaveRepo.findBySidsAndYearMonths(employeeIds, yearMonths);
		Map<String, Map<YearMonth, AnnLeaRemNumEachMonth>> annualLeaveMap = new HashMap<>();
		for (val annualLeave : annualLeaves){
			val employeeId = annualLeave.getEmployeeId();
			val yearMonth = annualLeave.getYearMonth();
			annualLeaveMap.putIfAbsent(employeeId, new HashMap<>());
			annualLeaveMap.get(employeeId).put(yearMonth, annualLeave);
		}
		
		// 積立年休月別残数データを取得する
		val reserveLeaves = reserveLeaveRepo.findBySidsAndYearMonths(employeeIds, yearMonths);
		Map<String, Map<YearMonth, RsvLeaRemNumEachMonth>> reserveLeaveMap = new HashMap<>();
		for (val reserveLeave : reserveLeaves){
			val employeeId = reserveLeave.getEmployeeId();
			val yearMonth = reserveLeave.getYearMonth();
			reserveLeaveMap.putIfAbsent(employeeId, new HashMap<>());
			reserveLeaveMap.get(employeeId).put(yearMonth, reserveLeave);
		}
		
		// 振休月別残数データを取得する
		val absenceLeaves = absLeaRemRepo.findBySidsAndYearMonths(employeeIds, yearMonths);
		Map<String, Map<YearMonth, AbsenceLeaveRemainData>> absenceLeaveMap = new HashMap<>();
		for (val absenceLeave : absenceLeaves){
			val employeeId = absenceLeave.getSId();
			val yearMonth = absenceLeave.getYm();
			absenceLeaveMap.putIfAbsent(employeeId, new HashMap<>());
			absenceLeaveMap.get(employeeId).put(yearMonth, absenceLeave);
		}
		
		// 代休月別残数データを取得する
		val monDayoffs = monDayoffRemRepo.findBySidsAndYearMonths(employeeIds, yearMonths);
		Map<String, Map<YearMonth, MonthlyDayoffRemainData>> monDayoffMap = new HashMap<>();
		for (val monDayoff : monDayoffs){
			val employeeId = monDayoff.getSId();
			val yearMonth = monDayoff.getYm();
			monDayoffMap.putIfAbsent(employeeId, new HashMap<>());
			monDayoffMap.get(employeeId).put(yearMonth, monDayoff);
		}
		
		// 特別休暇月別残数データを取得する
		val specialLeaves = spcLeaRemRepo.findBySidsAndYearMonths(employeeIds, yearMonths);
		Map<String, Map<YearMonth, List<SpecialHolidayRemainData>>> specialLeaveMap = new HashMap<>();
		for (val specialLeave : specialLeaves){
			val employeeId = specialLeave.getSid();
			val yearMonth = specialLeave.getYm();
			specialLeaveMap.putIfAbsent(employeeId, new HashMap<>());
			specialLeaveMap.get(employeeId).putIfAbsent(yearMonth, new ArrayList<>());
			ListIterator<SpecialHolidayRemainData> itrSpecialLeave =
					specialLeaveMap.get(employeeId).get(yearMonth).listIterator();
			boolean isNotExist = false;
			while (itrSpecialLeave.hasNext()){
				val outSpecialLeave = itrSpecialLeave.next();
				if (outSpecialLeave.getSpecialHolidayCd() == specialLeave.getSpecialHolidayCd()){
					itrSpecialLeave.set(outSpecialLeave);
					isNotExist = true;
					break;
				}
			}
			if (!isNotExist) specialLeaveMap.get(employeeId).get(yearMonth).add(specialLeave);
		}
		
		for (val employeeId : employeeIds){
			for (val yearMonth : yearMonths){
				if (!attendanceTimeMap.containsKey(employeeId)) continue;
				if (!attendanceTimeMap.get(employeeId).containsKey(yearMonth)) continue;
				val attendanceTime = attendanceTimeMap.get(employeeId).get(yearMonth);

				// 勤怠項目値リストに変換する準備をする
				val monthlyConverter = attendanceItemConverterFact.createMonthlyConverter();
				monthlyConverter.withAttendanceTime(attendanceTime);
				if (affiliationInfoMap.containsKey(employeeId)){
					if (affiliationInfoMap.get(employeeId).containsKey(yearMonth)){
						monthlyConverter.withAffiliation(affiliationInfoMap.get(employeeId).get(yearMonth));
					}
				}
				if (anyItemMap.containsKey(employeeId)){
					if (anyItemMap.get(employeeId).containsKey(yearMonth)){
						monthlyConverter.withAnyItem(anyItemMap.get(employeeId).get(yearMonth));
					}
				}
				if (annualLeaveMap.containsKey(employeeId)){
					if (annualLeaveMap.get(employeeId).containsKey(yearMonth)){
						monthlyConverter.withAnnLeave(annualLeaveMap.get(employeeId).get(yearMonth));
					}
				}
				if (reserveLeaveMap.containsKey(employeeId)){
					if (reserveLeaveMap.get(employeeId).containsKey(yearMonth)){
						monthlyConverter.withRsvLeave(reserveLeaveMap.get(employeeId).get(yearMonth));
					}
				}
				if (absenceLeaveMap.containsKey(employeeId)){
					if (absenceLeaveMap.get(employeeId).containsKey(yearMonth)){
						monthlyConverter.withAbsenceLeave(absenceLeaveMap.get(employeeId).get(yearMonth));
					}
				}
				if (monDayoffMap.containsKey(employeeId)){
					if (monDayoffMap.get(employeeId).containsKey(yearMonth)){
						monthlyConverter.withDayOff(monDayoffMap.get(employeeId).get(yearMonth));
					}
				}
				if (specialLeaveMap.containsKey(employeeId)){
					if (specialLeaveMap.get(employeeId).containsKey(yearMonth)){
						monthlyConverter.withSpecialLeave(specialLeaveMap.get(employeeId).get(yearMonth));
					}
				}
				
				// 月別実績データ値リストに追加する
				results.putIfAbsent(employeeId, new ArrayList<>());
				results.get(employeeId).add(MonthlyRecordValuesDto.of(
						yearMonth,
						attendanceTime.getClosureId(),
						attendanceTime.getClosureDate(),
						monthlyConverter.convert(itemIds)));
			}
		}
		
		return results;
	}
	/**
	 * アラームのチェック条件の勤怠項目名を作成
	 * @param attendanceItemNames　勤怠項目名
	 * @param type　0：「+」、1：「-」
	 * @param nameErrorAlarm
	 * @return
	 */
	public String getNameErrorAlarm(List<MonthlyAttendanceItemNameDto> attendanceItemNames ,int type,String nameErrorAlarm){
		if(!CollectionUtil.isEmpty(attendanceItemNames)) {
			
			for(int i=0; i< attendanceItemNames.size(); i++) {
				String beforeOperator = "";
				String operator = (i == (attendanceItemNames.size() - 1)) ? "" : type == 1 ? " - " : " + ";
				
				if (!"".equals(nameErrorAlarm) || type == 1) {
					beforeOperator = (i == 0) ? type == 1 ? " - " : " + " : "";
				}
                nameErrorAlarm += beforeOperator + attendanceItemNames.get(i).getAttendanceItemName() + operator;
			}
		}		
		return nameErrorAlarm;
	}
	/**
	 * アラームのチェック条件の時間はStringになる
	 * @param value
	 * @return
	 */
	public String timeToString(int value ){
		if(value%60<10){
			return  String.valueOf(value/60)+":0"+  String.valueOf(value%60);
		}
		return String.valueOf(value/60)+":"+  String.valueOf(value%60);
	}
}
