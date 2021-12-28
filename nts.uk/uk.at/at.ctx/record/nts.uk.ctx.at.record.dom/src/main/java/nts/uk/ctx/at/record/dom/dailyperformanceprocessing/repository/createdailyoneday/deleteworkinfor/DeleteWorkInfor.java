package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyoneday.deleteworkinfor;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.actualworkinghours.repository.AttendanceTimeRepository;
import nts.uk.ctx.at.record.dom.breakorgoout.repository.BreakTimeOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.dom.breakorgoout.repository.OutingTimeOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.dom.daily.attendanceleavinggate.repo.AttendanceLeavingGateOfDailyRepo;
import nts.uk.ctx.at.record.dom.daily.attendanceleavinggate.repo.PCLogOnInfoOfDailyRepo;
import nts.uk.ctx.at.record.dom.daily.optionalitemtime.AnyItemValueOfDailyRepo;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeOfDailyRepo;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeSheetOfDailyRepo;
import nts.uk.ctx.at.record.dom.daily.remarks.RemarksOfDailyPerformRepo;
import nts.uk.ctx.at.record.dom.daily.timegroup.TaskTimeGroupRepository;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyoneday.deleteworkinfor.sysdomain.DeleteSystemDomain;
import nts.uk.ctx.at.record.dom.editstate.repository.EditStateOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.dom.shorttimework.repo.ShortTimeOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.EmployeeDailyPerErrorRepository;
import nts.uk.ctx.at.record.dom.worktime.repository.TemporaryTimeOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.dom.worktime.repository.TimeLeavingOfDailyPerformanceRepository;

/**
 * 日別実績の前データを削除する
 * @author tutk
 *
 */
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Stateless
public class DeleteWorkInfor {
	@Inject
	private DeleteSystemDomain deleteWorkinfor;
	
	@Inject
	private PCLogOnInfoOfDailyRepo pcLogOnInfoOfDailyRepo;
	
	@Inject
	private EmployeeDailyPerErrorRepository employeeDailyPerErrorRepo;
	
	@Inject
	private OutingTimeOfDailyPerformanceRepository outingTime;
	
	@Inject
	private BreakTimeOfDailyPerformanceRepository breakTimeOfDaily;
	
	@Inject
	private AttendanceTimeRepository attendanceTime;
	
	@Inject
	private TimeLeavingOfDailyPerformanceRepository timeLeaningOfDaily;
	
	@Inject
	private ShortTimeOfDailyPerformanceRepository shortTimeOfDaily;
	
	@Inject
	private AttendanceLeavingGateOfDailyRepo attendanceLeaving;
	
	@Inject
	private AnyItemValueOfDailyRepo anyItemValue;
	
	@Inject
	private EditStateOfDailyPerformanceRepository editState;
	
	@Inject
	private TemporaryTimeOfDailyPerformanceRepository temporary;
	
	@Inject
	private RemarksOfDailyPerformRepo remarks;
	
	@Inject
	private OuenWorkTimeOfDailyRepo ouen;
	
	@Inject
	private OuenWorkTimeSheetOfDailyRepo ouenSheet;
	
	@Inject
	private TaskTimeGroupRepository taskTimeGroupRepository;
	
	public void deleteWorkInfor(String companyId,String employeeId,GeneralDate ymd) {
		
		//「情報」系のドメイン削除する
		deleteWorkinfor.delete(companyId, employeeId, ymd);
		//「日別実績のPCログオン情報」を削除する
		pcLogOnInfoOfDailyRepo.removeByKey(employeeId, ymd);
		//「社員の日別実績エラー一覧」を削除する
		employeeDailyPerErrorRepo.removeParam(employeeId, ymd);
		//「日別実績の外出時間帯」を削除する
		outingTime.delete(employeeId, ymd);
		//「日別実績の休憩時間帯」を削除する
		breakTimeOfDaily.delete(employeeId, ymd);
		//「日別実績の勤怠時間」を削除する
		attendanceTime.deleteByEmployeeIdAndDate(employeeId, ymd);
		//「日別実績の作業別勤怠時間」を削除する - đã xóa theo EA
		
		//「日別実績の出退勤」を削除する
		timeLeaningOfDaily.delete(employeeId, ymd);
		//「日別実績の短時間勤務時間帯」を削除する
		shortTimeOfDaily.deleteByEmployeeIdAndDate(employeeId, ymd);
		//「日別実績の入退門」を削除する
		attendanceLeaving.removeByKey(employeeId, ymd);
		//「日別実績の任意項目」を削除する
		anyItemValue.deleteAnyItemValueOfDaily(employeeId, ymd);
		//「日別実績の編集状態」を削除する
		editState.delete(employeeId, ymd);
		//「日別実績の臨時出退勤」を削除する
		temporary.delete(employeeId, ymd);
		//「日別実績の備考」を削除する
		remarks.remove(employeeId, ymd);
		
		// 「日別実績の応援作業時間」を削除する
		ouen.remove(employeeId, ymd);
		
		// 「日別実績の応援作業時間帯」を削除する
		ouenSheet.remove(employeeId, ymd);
		
		//「日別実績の作業時間帯グループ」を削除する
		taskTimeGroupRepository.delete(employeeId, ymd);
	}

}
