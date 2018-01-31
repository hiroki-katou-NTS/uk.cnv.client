package nts.uk.ctx.at.schedule.dom.schedule.algorithm;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;

import nts.uk.ctx.at.shared.dom.worktime.common.TimezoneOfFixedRestTimeSet;
import nts.uk.ctx.at.shared.dom.worktime.difftimeset.DiffTimeRestTimezone;
import nts.uk.ctx.at.shared.dom.worktime.difftimeset.DiffTimeWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.difftimeset.DiffTimeWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixRestTimezoneSet;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeDailyAtr;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.DailyWork;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;

public class CreScheWithBusinessDayCalService {
	@Inject
	WorkTypeRepository workTypeRepository;
	
	@Inject
	WorkTimeSettingRepository workTimeSettingRepository;
	
	@Inject
	FixedWorkSettingRepository fixedWorkSettingRepository;
	
	@Inject
	FlowWorkSettingRepository flowWorkSettingRepository;
	
	@Inject
	DiffTimeWorkSettingRepository diffTimeWorkSettingRepository;
	
	/**
	 * 休憩予定時間帯を取得する
	 * 
	 * @param companyId
	 * @param workTypeCode
	 * @param workTimeCode
	 */
	public void ScheduleBreakTime(String companyId, String workTypeCode, String workTimeCode) {
		// 入力パラメータ「就業時間帯コード」をチェック
		if(!Strings.isBlank(workTimeCode) || !"000".equals(workTimeCode)) {
			// ドメインモデル「勤務種類」を取得する
			Optional<WorkType> workType = workTypeRepository.findByPK(companyId, workTypeCode);
			
			// 取得したドメインモデルの「勤務種類. 1日の勤務. 勤務種類の分類」を判断
			if(workType.isPresent()) {
				// ドメインモデル「就業時間帯の設定」を取得する
				Optional<WorkTimeSetting> workTimeSetting = workTimeSettingRepository.findByCode(companyId, workTimeCode);
				
				// 休日出勤 or 休日出勤 以外
				if(workType.get().getDailyWork().isHolidayWork()) {
					if(workTimeSetting.isPresent()) {
						// 取得したドメインモデルの「就業時間帯勤務区分. 勤務形態区分」を判断
						if(workTimeSetting.get().getWorkTimeDivision().getWorkTimeDailyAtr() == WorkTimeDailyAtr.FLEX_WORK) {
							// 今回対象外
							// TODO: 
						} else {
							determineSetWorkingHours(workTimeSetting.get(), companyId, workTimeCode, true, workType.get().getDailyWork());
						}
					}
				} else {
					// 就業時間帯の設定
					// 取得したドメインモデルの「就業時間帯勤務区分. 勤務形態区分」を判断
					if(workTimeSetting.isPresent()) {
						if(workTimeSetting.get().getWorkTimeDivision().getWorkTimeDailyAtr() == WorkTimeDailyAtr.FLEX_WORK) {
							// ［フレックス勤務用］
							// TODO: 
						} else {
							determineSetWorkingHours(workTimeSetting.get(), companyId, workTimeCode, false, workType.get().getDailyWork());
						}
					}
				}
			}
		}
	}	
	
	/**
	 * 「就業時間帯勤務区分. 就業時間帯の設定方法」を判断
	 * 
	 * @param workTimeSetting
	 * @param companyId
	 * @param workTimeCode
	 */
	public void determineSetWorkingHours(WorkTimeSetting workTimeSetting, String companyId, String workTimeCode, boolean isHoliday, DailyWork dailyWork) {
		// 「就業時間帯勤務区分. 就業時間帯の設定方法」を判断
		switch(workTimeSetting.getWorkTimeDivision().getWorkTimeMethodSet()) {
			// ［固定勤務設定］
			case FIXED_WORK:
				// 固定勤務設定. 休日勤務時間帯. 休憩時間帯
				Optional<FixedWorkSetting> fixedWorkSetting = fixedWorkSettingRepository.findByKey(companyId, workTimeCode);
				
				if(fixedWorkSetting.isPresent()) {
					if(isHoliday) {
						getFixRestTimezoneSet(fixedWorkSetting.get());
					} else {
						getLstFixRestTimezoneSet(fixedWorkSetting.get(), dailyWork);
					}			
				}
				
		    	break;
	    	// ［流動勤務設定］
			case FLOW_WORK:
				// 流動勤務設定. 休日勤務時間帯. 休憩時間帯. 固定休憩時間帯
				Optional<FlowWorkSetting> flowWorkSetting = flowWorkSettingRepository.find(companyId, workTimeCode);
				
				if(flowWorkSetting.isPresent()) {
					if(isHoliday) {
						getTimezoneOfFixedRestTimeSet(flowWorkSetting.get());
					} else {
						getFixedRestTimezone(flowWorkSetting.get(), dailyWork);
					}
				}
				
		    	break;
	    	// ［時差勤務設定］
			case DIFFTIME_WORK:
				// 時差勤務設定. 休日勤務時間帯. 休憩時間帯
				Optional<DiffTimeWorkSetting> diffTimeWorkSetting = diffTimeWorkSettingRepository.find(companyId, workTimeCode);
				
				if(diffTimeWorkSetting.isPresent()) {
					if(isHoliday) {
						getDiffTimeRestTimezone(diffTimeWorkSetting.get());
					} else {
						getLstDiffTimeRestTimezone(diffTimeWorkSetting.get(), dailyWork);
					}					
				}
				
		    	break;
		}
	}
	
	/**
	 * getFixRestTimezoneSet
	 * @param fixedWorkSetting
	 * @return
	 */
	public FixRestTimezoneSet getFixRestTimezoneSet(FixedWorkSetting fixedWorkSetting) {
		return fixedWorkSetting.getOffdayWorkTimezone().getRestTimezone();
	}
	
	/**
	 * getTimezoneOfFixedRestTimeSet
	 * @param flowWorkSetting
	 * @return
	 */
	public TimezoneOfFixedRestTimeSet getTimezoneOfFixedRestTimeSet(FlowWorkSetting flowWorkSetting) {
		return flowWorkSetting.getOffdayWorkTimezone().getRestTimeZone().getFixedRestTimezone();
	}
	
	/**
	 * getDiffTimeRestTimezone
	 * @param diffTimeWorkSetting
	 * @return
	 */
	public DiffTimeRestTimezone getDiffTimeRestTimezone(DiffTimeWorkSetting diffTimeWorkSetting) {
		return diffTimeWorkSetting.getDayoffWorkTimezone().getRestTimezone();
	}
	
	/**
	 * getLstFixRestTimezoneSet
	 * @param fixedWorkSetting
	 * @return
	 */
	public List<FixRestTimezoneSet> getLstFixRestTimezoneSet(FixedWorkSetting fixedWorkSetting, DailyWork dailyWork) {
		if(fixedWorkSetting.getLstHalfDayWorkTimezone() == null) {
			return null;
		}
		
		List<FixRestTimezoneSet> lstFixRestTimezoneSet = null;
		
		for(int i = 0; i < fixedWorkSetting.getLstHalfDayWorkTimezone().size(); i++) {
			FixRestTimezoneSet item = fixedWorkSetting.getLstHalfDayWorkTimezone().get(i).getRestTimezone();
			lstFixRestTimezoneSet.add(item);
		}
		
		if(lstFixRestTimezoneSet.size() > 0) {
			makeBreakTimesCorrections(dailyWork);
		}
		
		return lstFixRestTimezoneSet;
	}
	
	/**
	 * getFixedRestTimezone
	 * @param flowWorkSetting
	 * @return
	 */
	public TimezoneOfFixedRestTimeSet getFixedRestTimezone(FlowWorkSetting flowWorkSetting, DailyWork dailyWork) {
		TimezoneOfFixedRestTimeSet item = flowWorkSetting.getHalfDayWorkTimezone().getRestTimezone().getFixedRestTimezone();
		
		if(item != null) {
			makeBreakTimesCorrections(dailyWork);
		}
		
		return item;
	}
	
	/**
	 * getLstDiffTimeRestTimezone
	 * @param diffTimeWorkSetting
	 * @return
	 */
	public List<DiffTimeRestTimezone> getLstDiffTimeRestTimezone(DiffTimeWorkSetting diffTimeWorkSetting, DailyWork dailyWork) {
		if(diffTimeWorkSetting.getHalfDayWorkTimezones() == null) {
			return null;
		}
		
		List<DiffTimeRestTimezone> lstDiffTimeRestTimezone = null;
		
		for(int i = 0; i < diffTimeWorkSetting.getHalfDayWorkTimezones().size(); i++) {
			DiffTimeRestTimezone item = diffTimeWorkSetting.getHalfDayWorkTimezones().get(i).getRestTimezone();
			lstDiffTimeRestTimezone.add(item);
		}
		
		if(lstDiffTimeRestTimezone.size() > 0) {
			makeBreakTimesCorrections(dailyWork);
		}
		
		return lstDiffTimeRestTimezone;
	}
	
	/**
	 * 午前出勤系、午後出勤系の場合に、休憩時間帯の補正を行う
	 * @param dailyWork
	 */
	public void makeBreakTimesCorrections(DailyWork dailyWork) {
		if(dailyWork.IsLeaveForMorning()) {
			
		} else if (dailyWork.IsLeaveForAfternoon()) {
			
		}
	}
}