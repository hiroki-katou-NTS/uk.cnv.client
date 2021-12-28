package nts.uk.ctx.at.record.dom.dailyprocess.calc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.actualworkinghours.AttendanceTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.actualworkinghours.repository.AttendanceTimeRepository;
import nts.uk.ctx.at.record.dom.daily.attendanceleavinggate.AttendanceLeavingGateOfDaily;
import nts.uk.ctx.at.record.dom.daily.attendanceleavinggate.PCLogOnInfoOfDaily;
import nts.uk.ctx.at.record.dom.daily.attendanceleavinggate.repo.AttendanceLeavingGateOfDailyRepo;
import nts.uk.ctx.at.record.dom.daily.attendanceleavinggate.repo.PCLogOnInfoOfDailyRepo;
import nts.uk.ctx.at.record.dom.daily.optionalitemtime.AnyItemValueOfDaily;
import nts.uk.ctx.at.record.dom.daily.optionalitemtime.AnyItemValueOfDailyRepo;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeOfDaily;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeOfDailyRepo;
import nts.uk.ctx.at.record.dom.editstate.EditStateOfDailyPerformance;
import nts.uk.ctx.at.record.dom.editstate.repository.EditStateOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.dom.workinformation.repository.WorkInformationRepository;
import nts.uk.ctx.at.record.dom.worktime.TimeLeavingOfDailyPerformance;
import nts.uk.ctx.at.record.dom.worktime.repository.TimeLeavingOfDailyPerformanceRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.CalculationState;

/**
 * 日別実績の勤怠時間と任意項目を同時更新し、ストアドを実行するためのサービス
 * @author keisuke_hoshina
 *
 */
@Stateless
public class AdTimeAndAnyItemAdUpServiceImpl implements AdTimeAndAnyItemAdUpService {

	/*日別実績の勤怠時間*/
	@Inject
	private AttendanceTimeRepository attendanceTimeRepository;
	
	/*任意項目リポジトリ*/
	@Inject
	private AnyItemValueOfDailyRepo anyItemValueOfDailyRepo;
	
	/*日別実績の勤務情報*/
	@Inject
	private WorkInformationRepository workInfo;
	/*日別実績のPCログオンログオフ*/
	@Inject
	private PCLogOnInfoOfDailyRepo pcLogon;
	/*日別実績の入退門*/
	@Inject
	private AttendanceLeavingGateOfDailyRepo attendanceGate;
	/*日別実績の出退勤*/
	@Inject
	private TimeLeavingOfDailyPerformanceRepository timeLeave;
	/*日別実績の応援作業別勤怠時間*/
	@Inject
	private OuenWorkTimeOfDailyRepo ouenWorkTimeOfDailyRepo;
	/*日別実績の編集状態*/
	@Inject
	private EditStateOfDailyPerformanceRepository editState;
	
	@Override
	public void addAndUpdate(String empId ,GeneralDate ymd,
							Optional<AttendanceTimeOfDailyPerformance> attendanceTime, Optional<AnyItemValueOfDaily> anyItem, Optional<OuenWorkTimeOfDaily> ouenTime) {
		workInfo.find(empId, ymd).ifPresent(wi -> {
			Optional<PCLogOnInfoOfDaily> pc = pcLogon.find(empId, ymd);
			Optional<AttendanceLeavingGateOfDaily> al = attendanceGate.find(empId, ymd);
			Optional<TimeLeavingOfDailyPerformance> tl = timeLeave.findByKey(empId, ymd);
			wi.changeCalcState(CalculationState.Calculated);
			IntegrationOfDaily daily = new IntegrationOfDaily(
					empId,
					ymd,
					wi.getWorkInformation(), //workInformation
					null, //calAttr
					null, //affiliationInfor
					pc.isPresent()?Optional.of(pc.get().getTimeZone()):Optional.empty(),//pcLogOnInfo
					new ArrayList<>(),//employeeError
					Optional.empty(),//outingTime
					new BreakTimeOfDailyAttd(),//breakTime
					attendanceTime.isPresent()?Optional.of(attendanceTime.get().getTime()):Optional.empty(),//attendanceTimeOfDailyPerformance
					tl.isPresent()?Optional.of(tl.get().getAttendance()):Optional.empty(),//attendanceLeave
					Optional.empty(), //shortTime
					Optional.empty(), //specDateAttr
					al.isPresent()?Optional.of(al.get().getTimeZone()):Optional.empty(),//attendanceLeavingGate
					anyItem.isPresent()?Optional.of(anyItem.get().getAnyItem()):Optional.empty(), //anyItemValue
					new ArrayList<>(),//editState
					Optional.empty(), //tempTime
					new ArrayList<>(),//remarks
					ouenTime.isPresent() ? ouenTime.get().getOuenTimes() : new ArrayList<>(),//ouenTime
					new ArrayList<>(),//ouenTimeSheet
					Optional.empty());//snapshot
			addAndUpdate(daily);
		});
	}
	
	@Override
	public List<IntegrationOfDaily> addAndUpdate(List<IntegrationOfDaily> daily) {
		return saveOnly(daily);
	}
	
	@Override
	public IntegrationOfDaily addAndUpdate(IntegrationOfDaily daily) {
		return addAndUpdate(Arrays.asList(daily)).get(0);
	}

	@Override
	public List<IntegrationOfDaily> saveOnly(List<IntegrationOfDaily> daily) {
		daily.stream().forEach(d -> {
			//勤怠時間更新
			d.getAttendanceTimeOfDailyPerformance().ifPresent(at -> {
				attendanceTimeRepository
						.update(new AttendanceTimeOfDailyPerformance(d.getEmployeeId(), d.getYmd(), at));
			});
			//任意項目更新
			d.getAnyItemValue().ifPresent(ai -> {
				anyItemValueOfDailyRepo.persistAndUpdate(new AnyItemValueOfDaily(d.getEmployeeId(), d.getYmd(), ai));
			});
			//応援作業別勤怠時間更新
			ouenWorkTimeOfDailyRepo.remove(d.getEmployeeId(), d.getYmd());
			if(!d.getOuenTime().isEmpty()) {
				ouenWorkTimeOfDailyRepo.insert(OuenWorkTimeOfDaily.create(d.getEmployeeId(), d.getYmd(), d.getOuenTime()));
			}
			// 編集状態更新
			List<EditStateOfDailyPerformance> editStateList = new ArrayList<>();
			for (EditStateOfDailyAttd editState : d.getEditState()){
				editStateList.add(new EditStateOfDailyPerformance(d.getEmployeeId(), d.getYmd(), editState));
			}
			this.editState.updateByKey(editStateList);
			this.editState.deleteExclude(d.getEmployeeId(), d.getYmd(), editStateList);
		});
		return daily;
	}

}
