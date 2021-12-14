package nts.uk.ctx.at.record.dom.workrecord.remainingnumbermanagement;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.TimeDigestionParam;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHolidayRepository;
import nts.uk.ctx.at.shared.dom.worktype.DailyWork;
import nts.uk.ctx.at.shared.dom.worktype.Holiday;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;

/**
 * @author anhnm
 *
 */
@Stateless
public class RemainingNumberCheckImp implements RemainingNumberCheck {
    
    @Inject
    private WorkTypeRepository workTypeRepository;
    
    @Inject
    private DetermineCareNursingCheck determineCareNursingCheck;
    
    @Inject
    private SpecialHolidayRepository specialHolidayRepo;

    @Override
    public RemainNumberClassification determineCheckRemain(String cId, List<String> workTypeCodes,
            Optional<TimeDigestionParam> timeDigest) {
        // 「残数チェック区分」の初期値をセットする：全て「チェックしない」（false）にする
        RemainNumberClassification remainNumberClassification = new RemainNumberClassification();
        
        // 「INPUT.勤務種類コード(List)」をチェックする
        if (workTypeCodes.isEmpty()) {
            // 「INPUT.時間消化」をチェックする
            if (timeDigest.isPresent()) {
                // ①時間消化.時間年休　＞　０：残数チェック区分.年休チェック区分　＝　true
                if (timeDigest.get().getAnnualTime() > 0) {
                    remainNumberClassification.setChkAnnual(true);
                }
                // ②時間消化.時間代休　＞　０：残数チェック区分.代休チェック区分　＝　true
                if (timeDigest.get().getSubHolidayTime() > 0) {
                    remainNumberClassification.setChkSubHoliday(true);
                }
                // ③時間消化.時間60超休　＞　０：残数チェック区分.超休チェック区分　＝　true
                if (timeDigest.get().getOver60HTime() > 0) {
                    remainNumberClassification.setChkSuperBreak(true);
                }
                // ④時間消化.時間子の看護　＞　０：残数チェック区分.子の看護チェック区分　＝　true
                if (timeDigest.get().getChildCareTime() > 0) {
                    remainNumberClassification.setChkChildNursing(true);
                }
                // ⑤時間消化.時間介護　＞　０：残数チェック区分.介護チェック区分　＝　true
                if (timeDigest.get().getNursingTime() > 0) {
                    remainNumberClassification.setChkLongTermCare(true);
                }
                // ⑥残業・休出時間　＞　０：残数チェック区分.超休チェック区分　＝　true
                if (timeDigest.get().getOverHolidayTime() > 0) {
                    remainNumberClassification.setChkSuperBreak(true);
                }
            }
        } else {
            // ドメイン「勤務種類」を取得する
            List<WorkType> workTypeLst = workTypeRepository.findNotDeprecatedByListCode(cId, workTypeCodes);
            if (timeDigest.isPresent() && timeDigest.get().getOverHolidayTime() > 0) {
                remainNumberClassification.setChkSuperBreak(true);
            }
            
            // 取得した「勤務種類」ListをLoopする
            workTypeLst.forEach(workType -> {
                // ＠勤務種類からどんな休暇種類を含むか判断する
                Holiday holiday = workType.getDailyWork().determineHolidayByWorkType();
                
                
                // 介護看護がチェックするか判断する
                ChildCareNurseCheck childCareNurseCheck = determineCareNursingCheck.determineCareNursingCheck(
                        cId, 
                        workType.getWorkTypeSetList().stream().map(wts -> wts.getSumAbsenseNo()).collect(Collectors.toList()), 
                        workType.getWorkTypeSetList().stream().map(wts -> wts.getSumSpHodidayNo()).collect(Collectors.toList()));
                
                if (childCareNurseCheck.childCareCheck) {
                    remainNumberClassification.setChkChildNursing(true);
                }
                if (childCareNurseCheck.nursingCheck) {
                    remainNumberClassification.setChkLongTermCare(true);
                }
                
                // 時間消化休暇か判断する
                if (holiday.isTimeDigestVacation() && timeDigest.isPresent()) {
                    // 時間消化から各残数種類がチェックか判断する
                    // ①時間消化.時間年休　＞　０：残数チェック区分.年休チェック区分　＝　true
                    if (timeDigest.get().getAnnualTime() > 0) {
                        remainNumberClassification.setChkAnnual(true);
                    }
                    // ②時間消化.時間代休　＞　０：残数チェック区分.代休チェック区分　＝　true
                    if (timeDigest.get().getSubHolidayTime() > 0) {
                        remainNumberClassification.setChkSubHoliday(true);
                    }
                    // ③時間消化.時間60超休　＞　０：残数チェック区分.超休チェック区分　＝　true
                    if (timeDigest.get().getOver60HTime() > 0) {
                        remainNumberClassification.setChkSuperBreak(true);
                    }
                    // ④時間消化.時間子の看護　＞　０：残数チェック区分.子の看護チェック区分　＝　true
                    if (timeDigest.get().getChildCareTime() > 0) {
                        remainNumberClassification.setChkChildNursing(true);
                    }
                    // ⑤時間消化.時間介護　＞　０：残数チェック区分.介護チェック区分　＝　true
                    if (timeDigest.get().getNursingTime() > 0) {
                        remainNumberClassification.setChkLongTermCare(true);
                    }
                    // ⑥残業・休出時間　＞　０：残数チェック区分.超休チェック区分　＝　true
                    if (timeDigest.get().getOverHolidayTime() > 0) {
                        remainNumberClassification.setChkSuperBreak(true);
                    }
                } 
                
                if (holiday.isAnnualHoliday()) {
                    remainNumberClassification.setChkAnnual(true);
                }
                if (holiday.isSubstituteHoliday()) {
                    remainNumberClassification.setChkSubHoliday(true);
                }
                if (holiday.isPause()) {
                    remainNumberClassification.setChkPause(true);
                }
                if (checkSpecialHoliday(cId, 
                        workType.getWorkTypeSetList().stream().map(wts -> wts.getSumAbsenseNo()).collect(Collectors.toList()), 
                        workType.getWorkTypeSetList().stream().map(wts -> wts.getSumSpHodidayNo()).collect(Collectors.toList()),
                        workType.getDailyWork())) {
                    remainNumberClassification.setChkSpecial(true);
                }
                if (holiday.isYearlyReserved()) {
                    remainNumberClassification.setChkFundingAnnual(true);
                }
                if (holiday.isHoliday()) {
                    remainNumberClassification.setChkPublicHoliday(true);
                }
            });
        }

        return remainNumberClassification;
    }
    
    /**
     * 特別休暇をチェックするか
     * @param cId
     * @param absenceFrameNo
     * @param specialHolidayFrame
     * @param dailyWork
     * @return
     */
    private boolean checkSpecialHoliday(String cId, List<Integer> absenceFrameNo, List<Integer> specialHolidayFrame, DailyWork dailyWork){
    	
    	if(dailyWork.isAbsence()){
    		boolean checkAbsence = absenceFrameNo.stream().anyMatch(x ->{
        		List<Integer> absenceSpecialCd = specialHolidayRepo.findByAbsframeNo(cId, x);
        		if(!absenceSpecialCd.isEmpty()){
        			return true;
        		}
        		return false;
        	});
    		
    		if(checkAbsence){
    			return checkAbsence;
    		}
    		
    	}
    	
    	if(dailyWork.isSpecHoliday()){
    		boolean checkSpecialHoliday =  specialHolidayFrame.stream().anyMatch(x ->{
	    		List<Integer> holidaySpecialCd = specialHolidayRepo.findBySphdSpecLeave(cId, x);
		    	if(!holidaySpecialCd.isEmpty()){
		    		return true;
		    	}
		    	return false;
	    	});
	    	 
	    	 if(checkSpecialHoliday){
	    		 return checkSpecialHoliday;
	    	 }

    	}
    	return false;
    }

}
