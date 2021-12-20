package nts.uk.file.at.infra.worktime;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.aspose.cells.Cells;

import nts.uk.ctx.at.shared.app.find.ot.frame.OvertimeWorkFrameFindDto;
import nts.uk.ctx.at.shared.app.find.ot.frame.OvertimeWorkFrameFinder;
import nts.uk.ctx.at.shared.app.find.workdayoff.frame.WorkdayoffFrameFindDto;
import nts.uk.ctx.at.shared.app.find.workdayoff.frame.WorkdayoffFrameFinder;
import nts.uk.ctx.at.shared.app.find.worktime.common.dto.DeductionTimeDto;
import nts.uk.ctx.at.shared.app.find.worktime.common.dto.EmTimeZoneSetDto;
import nts.uk.ctx.at.shared.app.find.worktime.common.dto.FixedWorkCalcSettingDto;
import nts.uk.ctx.at.shared.app.find.worktime.common.dto.FlowRestSettingDto;
import nts.uk.ctx.at.shared.app.find.worktime.common.dto.HDWorkTimeSheetSettingDto;
import nts.uk.ctx.at.shared.app.find.worktime.common.dto.OtherEmTimezoneLateEarlySetDto;
import nts.uk.ctx.at.shared.app.find.worktime.common.dto.OverTimeOfTimeZoneSetDto;
import nts.uk.ctx.at.shared.app.find.worktime.common.dto.PrioritySettingDto;
import nts.uk.ctx.at.shared.app.find.worktime.common.dto.RoundingSetDto;
import nts.uk.ctx.at.shared.app.find.worktime.common.dto.StampReflectTimezoneDto;
import nts.uk.ctx.at.shared.app.find.worktime.common.dto.WorkTimezoneMedicalSetDto;
import nts.uk.ctx.at.shared.app.find.worktime.common.dto.WorkTimezoneOtherSubHolTimeSetDto;
import nts.uk.ctx.at.shared.app.find.worktime.dto.WorkTimeSettingInfoDto;
import nts.uk.ctx.at.shared.app.find.worktime.fixedset.dto.FixHalfDayWorkTimezoneDto;
import nts.uk.ctx.at.shared.app.find.worktime.flexset.dto.FlexHalfDayWorkTimeDto;
import nts.uk.ctx.at.shared.app.find.worktime.flowset.dto.FlOTTimezoneDto;
import nts.uk.ctx.at.shared.app.find.worktime.flowset.dto.FlWorkHdTimeZoneDto;
import nts.uk.ctx.at.shared.app.find.worktime.predset.dto.TimezoneDto;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.timerounding.Unit;
import nts.uk.ctx.at.shared.dom.common.usecls.ApplyAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.primitives.BonusPaySettingCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.repository.BPSettingRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.setting.BonusPaySetting;
import nts.uk.ctx.at.shared.dom.worktime.common.AmPmAtr;
import nts.uk.ctx.at.shared.dom.worktime.common.CompensatoryOccurrenceDivision;
import nts.uk.ctx.at.shared.dom.worktime.common.FontRearSection;
import nts.uk.ctx.at.shared.dom.worktime.common.GoLeavingWorkAtr;
import nts.uk.ctx.at.shared.dom.worktime.common.LateEarlyAtr;
import nts.uk.ctx.at.shared.dom.worktime.common.LegalOTSetting;
import nts.uk.ctx.at.shared.dom.worktime.common.MultiStampTimePiorityAtr;
import nts.uk.ctx.at.shared.dom.worktime.common.StampPiorityAtr;
import nts.uk.ctx.at.shared.dom.worktime.common.Superiority;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkSystemAtr;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FixedChangeAtr;
import nts.uk.ctx.at.shared.dom.worktime.flowset.PrePlanWorkTimeCalcMethod;
import nts.uk.ctx.at.shared.dom.worktime.worktimedisplay.DisplayMode;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeDailyAtr;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeMethodSet;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.TimeWithDayAttr;

@Stateless
public class WorkTimeReportService {
    private final int NORMAL = 0;
    private final int FLOW = 1;
    private final int FLEX = 2;
    
    @Inject
    private WorkdayoffFrameFinder finder;
    
    @Inject
    private BPSettingRepository bpSettingRepository;
    
    @Inject
    private OvertimeWorkFrameFinder overTimeFinder;
    
    /**
     * 勤務形態 通常
     * @param data
     * @param cells
     * @param startIndex
     */
    public void insertDataOneLineNormal(WorkTimeSettingInfoDto data, Cells cells, int startIndex) {
        Integer displayMode = data.getDisplayMode().displayMode;
        
        // 1        タブグ:                所定
        this.printDataPrescribed(data, cells, startIndex, NORMAL);
        
        // 2        タブグ:                勤務時間帯
        
        Optional<FixHalfDayWorkTimezoneDto> fixHalfDayWorkOneDayOpt = data.getFixedWorkSetting().getLstHalfDayWorkTimezone()
                .stream().filter(x -> x.getDayAtr().equals(AmPmAtr.ONE_DAY.value)).findFirst();
        Optional<FixHalfDayWorkTimezoneDto> fixHalfDayWorkMorningOpt = data.getFixedWorkSetting().getLstHalfDayWorkTimezone()
                .stream().filter(x -> x.getDayAtr().equals(AmPmAtr.AM.value)).findFirst();
        Optional<FixHalfDayWorkTimezoneDto> fixHalfDayWorkAfternoonOpt = data.getFixedWorkSetting().getLstHalfDayWorkTimezone()
                .stream().filter(x -> x.getDayAtr().equals(AmPmAtr.PM.value)).findFirst();
        
        if (fixHalfDayWorkOneDayOpt.isPresent()) {
            List<EmTimeZoneSetDto> lstWorkingTimezone = fixHalfDayWorkOneDayOpt.get().getWorkTimezone().getLstWorkingTimezone();
            Collections.sort(lstWorkingTimezone, new Comparator<EmTimeZoneSetDto>() {
                public int compare(EmTimeZoneSetDto o1, EmTimeZoneSetDto o2) {
                    return o1.getEmploymentTimeFrameNo().compareTo(o2.getEmploymentTimeFrameNo());
                };
            });
            
            for (int i = 0; i < lstWorkingTimezone.size(); i++) {
                /*
                 * R4_86
                 * 1日勤務用.開始時間
                 */
                Integer startTime = lstWorkingTimezone.get(i).getTimezone().getStart();
                cells.get("Z" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(startTime));
                
                /*
                 * R4_87
                 * 1日勤務用.終了時間
                 */
                Integer endTime = lstWorkingTimezone.get(i).getTimezone().getEnd();
                cells.get("AA" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(endTime));
                
                /*
                 * R4_88
                 * 1日勤務用.丸め
                 */
                Integer unit = lstWorkingTimezone.get(i).getTimezone().getRounding().getRoundingTime();
                String unitString = Unit.valueOf(unit).nameId;
                cells.get("AB" + ((startIndex + 1) + i)).setValue(unitString);
                
                /*
                 * R4_89
                 * 1日勤務用.端数L
                 */
                Integer rounding = lstWorkingTimezone.get(i).getTimezone().getRounding().getRounding();
                String roundingString = getRoundingEnum(rounding);
                cells.get("AC" + ((startIndex + 1) + i)).setValue(roundingString);
            }
        }
        
        // 3        タブグ:                残業時間帯
        
        List<WorkdayoffFrameFindDto> otFrameFind = this.finder.findAllUsed();
        List<OvertimeWorkFrameFindDto> overTimeLst = this.overTimeFinder.findAllUsed();
        
        Boolean useHalfDayShiftOverTime = data.getFixedWorkSetting().getUseHalfDayShift().isOverTime();
        Integer legalOTSetting = data.getFixedWorkSetting().getLegalOTSetting();
        
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            /*
             * R4_261
             * 半日勤務を設定する
             */
            cells.get("AD" + (startIndex + 1)).setValue(getUseAtrByBoolean(useHalfDayShiftOverTime));
            
            /*
             * R4_98
             * 法定内残業自動計算
             */
            cells.get("AE" + (startIndex + 1)).setValue(getUseAtrByInteger(legalOTSetting));
            
            // One day
            if (fixHalfDayWorkOneDayOpt.isPresent()) {
                List<OverTimeOfTimeZoneSetDto> lstOTTimezone = fixHalfDayWorkOneDayOpt.get().getWorkTimezone().getLstOTTimezone();
                Collections.sort(lstOTTimezone, new Comparator<OverTimeOfTimeZoneSetDto>() {
                    public int compare(OverTimeOfTimeZoneSetDto o1, OverTimeOfTimeZoneSetDto o2) {
                        return o1.getWorkTimezoneNo().compareTo(o2.getWorkTimezoneNo());
                    };
                });
                
                for (int i = 0; i < lstOTTimezone.size(); i++) {
                    /*
                     * R4_99
                     * 1日勤務用.開始時間
                     */
                    cells.get("AF" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(lstOTTimezone.get(i).getTimezone().getStart()));
                    
                    /*
                     * R4_100
                     * 1日勤務用.終了時間
                     */
                    cells.get("AG" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(lstOTTimezone.get(i).getTimezone().getEnd()));
                    
                    /*
                     * R4_101
                     * 1日勤務用.丸め
                     */
                    cells.get("AH" + ((startIndex + 1) + i))
                    .setValue(Unit.valueOf(lstOTTimezone.get(i).getTimezone().getRounding().getRoundingTime()).nameId);
                    
                    /*
                     * R4_102
                     * 1日勤務用.端数
                     */
                    cells.get("AI" + ((startIndex + 1) + i))
                    .setValue(getRoundingEnum(lstOTTimezone.get(i).getTimezone().getRounding().getRounding()));
                    
                    Integer otFrameNo = fixHalfDayWorkOneDayOpt.get().getWorkTimezone().getLstOTTimezone().get(i).getOtFrameNo();
                    Optional<OvertimeWorkFrameFindDto> otFrameOpt = overTimeLst.stream().filter(x -> x.getOvertimeWorkFrNo() == otFrameNo).findFirst();
                    
                    /*
                     * R4_103
                     * 1日勤務用.残業枠
                     */
                    cells.get("AJ" + ((startIndex + 1) + i)).setValue(otFrameOpt.isPresent() ? otFrameOpt.get().getOvertimeWorkFrName() : "");
                    
                    /*
                     * R4_104
                     * 1日勤務用.早出
                     */
                    boolean isEarlyOTUse = fixHalfDayWorkOneDayOpt.get().getWorkTimezone().getLstOTTimezone().get(i).isEarlyOTUse();
                    cells.get("AK" + ((startIndex + 1) + i)).setValue(isEarlyOTUse ?  "○" : "-");
                    
                    Integer legalOTframeNo = fixHalfDayWorkOneDayOpt.get().getWorkTimezone().getLstOTTimezone().get(i).getLegalOTframeNo();
                    Optional<OvertimeWorkFrameFindDto> legalOTframeOpt = overTimeLst.stream().filter(x -> x.getOvertimeWorkFrNo() == legalOTframeNo).findFirst();
                    
                    if (legalOTSetting.equals(LegalOTSetting.LEGAL_INTERNAL_TIME.value)) {
                        /*
                         * R4_105
                         * 1日勤務用.法定内残業枠
                         */
                        cells.get("AL" + ((startIndex + 1) + i)).setValue(legalOTframeOpt.isPresent() ? legalOTframeOpt.get().getOvertimeWorkFrName() : "");
                        
                        /*
                         * R4_106
                         * 1日勤務用.積残順序
                         */
                        Integer settlementOrder = fixHalfDayWorkOneDayOpt.get().getWorkTimezone().getLstOTTimezone().get(i).getSettlementOrder();
                        cells.get("AM" + ((startIndex + 1) + i)).setValue(settlementOrder != null ? getSetlementEnum(settlementOrder) : "");
                    }
                }
            }
            
            if (useHalfDayShiftOverTime) {
                
                // Morning
                if (fixHalfDayWorkMorningOpt.isPresent()) {
                    List<OverTimeOfTimeZoneSetDto> lstOTTimezone = fixHalfDayWorkMorningOpt.get().getWorkTimezone().getLstOTTimezone();
                    Collections.sort(lstOTTimezone, new Comparator<OverTimeOfTimeZoneSetDto>() {
                        public int compare(OverTimeOfTimeZoneSetDto o1, OverTimeOfTimeZoneSetDto o2) {
                            return o1.getWorkTimezoneNo().compareTo(o2.getWorkTimezoneNo());
                        };
                    });
                    
                    for (int i = 0; i < lstOTTimezone.size(); i++) {
                        /*
                         * R4_107
                         * 午前勤務用.開始時間
                         */
                        cells.get("AN" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(lstOTTimezone.get(i).getTimezone().getStart()));
                        
                        /*
                         * R4_108
                         * 午前勤務用.終了時間
                         */
                        cells.get("AO" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(lstOTTimezone.get(i).getTimezone().getEnd()));
                        
                        /*
                         * R4_109
                         * 午前勤務用.丸め
                         */
                        cells.get("AP" + ((startIndex + 1) + i))
                        .setValue(Unit.valueOf(lstOTTimezone.get(i).getTimezone().getRounding().getRoundingTime()).nameId);
                        
                        /*
                         * R4_110
                         * 午前勤務用.端数
                         */
                        cells.get("AQ" + ((startIndex + 1) + i))
                        .setValue(getRoundingEnum(lstOTTimezone.get(i).getTimezone().getRounding().getRounding()));
                        
                        Integer otFrameNo = fixHalfDayWorkMorningOpt.get().getWorkTimezone().getLstOTTimezone().get(i).getOtFrameNo();
                        Optional<OvertimeWorkFrameFindDto> otFrameOpt = overTimeLst.stream().filter(x -> x.getOvertimeWorkFrNo() == otFrameNo).findFirst();
                        
                        /*
                         * R4_111
                         * 午前勤務用.残業枠
                         */
                        cells.get("AR" + ((startIndex + 1) + i)).setValue(otFrameOpt.isPresent() ? otFrameOpt.get().getOvertimeWorkFrName() : "");
                        
                        /*
                         * R4_112
                         * 午前勤務用.早出
                         */
                        boolean isEarlyOTUse = fixHalfDayWorkMorningOpt.get().getWorkTimezone().getLstOTTimezone().get(i).isEarlyOTUse();
                        cells.get("AS" + ((startIndex + 1) + i)).setValue(isEarlyOTUse ?  "○" : "-");
                        
                        Integer legalOTframeNo = fixHalfDayWorkMorningOpt.get().getWorkTimezone().getLstOTTimezone().get(i).getLegalOTframeNo();
                        Optional<OvertimeWorkFrameFindDto> legalOTframeOpt = overTimeLst.stream().filter(x -> x.getOvertimeWorkFrNo() == legalOTframeNo).findFirst();
                        
                        if (legalOTSetting.equals(LegalOTSetting.LEGAL_INTERNAL_TIME.value)) {
                            /*
                             * R4_113
                             * 午前勤務用.法定内残業枠
                             */
                            cells.get("AT" + ((startIndex + 1) + i)).setValue(legalOTframeOpt.isPresent() ? legalOTframeOpt.get().getOvertimeWorkFrName() : "");
                            
                            /*
                             * R4_114
                             * 午前勤務用.積残順序
                             */
                            Integer settlementOrder = fixHalfDayWorkMorningOpt.get().getWorkTimezone().getLstOTTimezone().get(i).getSettlementOrder();
                            cells.get("AU" + ((startIndex + 1) + i)).setValue(settlementOrder != null ? getSetlementEnum(settlementOrder) : "");
                        }
                    }
                }
                
                // Afternoon
                if (fixHalfDayWorkAfternoonOpt.isPresent()) {
                    List<OverTimeOfTimeZoneSetDto> lstOTTimezone = fixHalfDayWorkAfternoonOpt.get().getWorkTimezone().getLstOTTimezone();
                    Collections.sort(lstOTTimezone, new Comparator<OverTimeOfTimeZoneSetDto>() {
                        public int compare(OverTimeOfTimeZoneSetDto o1, OverTimeOfTimeZoneSetDto o2) {
                            return o1.getWorkTimezoneNo().compareTo(o2.getWorkTimezoneNo());
                        };
                    });
                    
                    for (int i = 0; i < lstOTTimezone.size(); i++) {
                        /*
                         * R4_115
                         * 午後勤務用.開始時間
                         */
                        cells.get("AV" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(lstOTTimezone.get(i).getTimezone().getStart()));
                        
                        /*
                         * R4_116
                         * 午後勤務用.終了時間
                         */
                        cells.get("AW" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(lstOTTimezone.get(i).getTimezone().getEnd()));
                        
                        /*
                         * R4_117
                         * 午後勤務用.丸め
                         */
                        cells.get("AX" + ((startIndex + 1) + i))
                        .setValue(Unit.valueOf(lstOTTimezone.get(i).getTimezone().getRounding().getRoundingTime()).nameId);
                        
                        /*
                         * R4_118
                         * 午後勤務用.端数
                         */
                        cells.get("AY" + ((startIndex + 1) + i))
                        .setValue(getRoundingEnum(lstOTTimezone.get(i).getTimezone().getRounding().getRounding()));
                        
                        Integer otFrameNo = fixHalfDayWorkAfternoonOpt.get().getWorkTimezone().getLstOTTimezone().get(i).getOtFrameNo();
                        Optional<OvertimeWorkFrameFindDto> otFrameOpt = overTimeLst.stream().filter(x -> x.getOvertimeWorkFrNo() == otFrameNo).findFirst();
                        
                        /*
                         * R4_119
                         * 午後勤務用.残業枠
                         */
                        cells.get("AZ" + ((startIndex + 1) + i)).setValue(otFrameOpt.isPresent() ? otFrameOpt.get().getOvertimeWorkFrName() : "");
                        
                        /*
                         * R4_120
                         * 午後勤務用.早出
                         */
                        boolean isEarlyOTUse = fixHalfDayWorkAfternoonOpt.get().getWorkTimezone().getLstOTTimezone().get(i).isEarlyOTUse();
                        cells.get("BA" + ((startIndex + 1) + i)).setValue(isEarlyOTUse ?  "○" : "-");
                        
                        Integer legalOTframeNo = fixHalfDayWorkAfternoonOpt.get().getWorkTimezone().getLstOTTimezone().get(i).getLegalOTframeNo();
                        Optional<OvertimeWorkFrameFindDto> legalOTframeOpt = overTimeLst.stream().filter(x -> x.getOvertimeWorkFrNo() == legalOTframeNo).findFirst();
                        
                        if (legalOTSetting.equals(LegalOTSetting.LEGAL_INTERNAL_TIME.value)) {
                            /*
                             * R4_121
                             * 午後勤務用.法定内残業枠
                             */
                            cells.get("BB" + ((startIndex + 1) + i)).setValue(legalOTframeOpt.isPresent() ? legalOTframeOpt.get().getOvertimeWorkFrName() : "");
                            
                            /*
                             * R4_122
                             * 午後勤務用.積残順序
                             */
                            Integer settlementOrder = fixHalfDayWorkAfternoonOpt.get().getWorkTimezone().getLstOTTimezone().get(i).getSettlementOrder();
                            cells.get("BC" + ((startIndex + 1) + i)).setValue(settlementOrder != null ? getSetlementEnum(settlementOrder) : "");
                        }
                    }
                }
            } 
        } else {
            if (fixHalfDayWorkOneDayOpt.isPresent()) {
                List<OverTimeOfTimeZoneSetDto> lstOTTimezone = fixHalfDayWorkOneDayOpt.get().getWorkTimezone().getLstOTTimezone();
                Collections.sort(lstOTTimezone, new Comparator<OverTimeOfTimeZoneSetDto>() {
                    public int compare(OverTimeOfTimeZoneSetDto o1, OverTimeOfTimeZoneSetDto o2) {
                        return o1.getOtFrameNo().compareTo(o2.getOtFrameNo());
                    };
                });
                
                for (int i = 0; i < lstOTTimezone.size(); i++) {
                    /*
                     * R4_99
                     * 1日勤務用.開始時間
                     */
                    cells.get("AF" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(lstOTTimezone.get(i).getTimezone().getStart()));
                    
                    /*
                     * R4_100
                     * 1日勤務用.終了時間
                     */
                    cells.get("AG" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(lstOTTimezone.get(i).getTimezone().getEnd()));
                    
                    /*
                     * R4_101
                     * 1日勤務用.丸め
                     */
                    cells.get("AH" + ((startIndex + 1) + i))
                    .setValue(Unit.valueOf(lstOTTimezone.get(i).getTimezone().getRounding().getRoundingTime()).nameId);
                    
                    /*
                     * R4_102
                     * 1日勤務用.端数
                     */
                    cells.get("AI" + ((startIndex + 1) + i))
                    .setValue(getRoundingEnum(lstOTTimezone.get(i).getTimezone().getRounding().getRounding()));
                    
                    Integer otFrameNo = fixHalfDayWorkOneDayOpt.get().getWorkTimezone().getLstOTTimezone().get(i).getOtFrameNo();
                    Optional<OvertimeWorkFrameFindDto> otFrameOpt = overTimeLst.stream().filter(x -> x.getOvertimeWorkFrNo() == otFrameNo).findFirst();
                    
                    /*
                     * R4_103
                     * 1日勤務用.残業枠
                     */
                    cells.get("AJ" + ((startIndex + 1) + i)).setValue(otFrameOpt.isPresent() ? otFrameOpt.get().getOvertimeWorkFrName() : "");
                    
                    /*
                     * R4_104
                     * 1日勤務用.早出
                     */
                    boolean isEarlyOTUse = fixHalfDayWorkOneDayOpt.get().getWorkTimezone().getLstOTTimezone().get(i).isEarlyOTUse();
                    cells.get("AK" + ((startIndex + 1) + i)).setValue(isEarlyOTUse ?  "○" : "-");
                }
            }
        }
        
        // 4        タブグ:                打刻時間帯
        
        List<PrioritySettingDto> prioritySets = data.getFixedWorkSetting().getCommonSetting().getStampSet().getPrioritySets();
        Optional<PrioritySettingDto> prioritySetGoingWorkOpt = prioritySets.stream()
                .filter(x -> x.getStampAtr().equals(StampPiorityAtr.GOING_WORK.value)).findFirst();
        Optional<PrioritySettingDto> prioritySetLeaveWorkOpt = prioritySets.stream()
                .filter(x -> x.getStampAtr().equals(StampPiorityAtr.LEAVE_WORK.value)).findFirst();
        Optional<PrioritySettingDto> prioritySetEnteringOpt = prioritySets.stream()
                .filter(x -> x.getStampAtr().equals(StampPiorityAtr.ENTERING.value)).findFirst();
        Optional<PrioritySettingDto> prioritySetExitOpt = prioritySets.stream()
                .filter(x -> x.getStampAtr().equals(StampPiorityAtr.EXIT.value)).findFirst();
        Optional<PrioritySettingDto> prioritySetPCLoginOpt = prioritySets.stream()
                .filter(x -> x.getStampAtr().equals(StampPiorityAtr.PCLOGIN.value)).findFirst();
        Optional<PrioritySettingDto> prioritySetPCLogoutOpt = prioritySets.stream()
                .filter(x -> x.getStampAtr().equals(StampPiorityAtr.PC_LOGOUT.value)).findFirst();
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            
            /*
             * R4_123
             * 優先設定.出勤
             */
            if (prioritySetGoingWorkOpt.isPresent()) {
                Integer priorityAtr = prioritySetGoingWorkOpt.get().getPriorityAtr();
                cells.get("BD" + (startIndex + 1)).setValue(priorityAtr.equals(MultiStampTimePiorityAtr.BEFORE_PIORITY.value) ? "前優先" : "後優先");
            }
            
            /*
             * R4_124
             * 優先設定.退勤
             */
            if (prioritySetLeaveWorkOpt.isPresent()) {
                Integer priorityAtr = prioritySetLeaveWorkOpt.get().getPriorityAtr();
                cells.get("BE" + (startIndex + 1)).setValue(priorityAtr.equals(MultiStampTimePiorityAtr.BEFORE_PIORITY.value) ? "前優先" : "後優先");
            }
        }
        
        List<RoundingSetDto> roundingSets = data.getFixedWorkSetting().getCommonSetting().getStampSet().getRoundingTime().getRoundingSets();
        Optional<RoundingSetDto> roundingAttendanceSetOpt = roundingSets.stream().filter(x -> x.getSection() == Superiority.ATTENDANCE.value).findFirst();
        Optional<RoundingSetDto> roundingOfficeSetOpt = roundingSets.stream().filter(x -> x.getSection() == Superiority.OFFICE_WORK.value).findFirst();
        Optional<RoundingSetDto> roundingGoOutSetOpt = roundingSets.stream().filter(x -> x.getSection() == Superiority.GO_OUT.value).findFirst();
        Optional<RoundingSetDto> roundingTurnBackSetOpt = roundingSets.stream().filter(x -> x.getSection() == Superiority.TURN_BACK.value).findFirst();

        if (roundingAttendanceSetOpt.isPresent()) {
            /*
             * R4_125
             * 打刻丸め.出勤
             */
            Integer roundingTimeUnit = roundingAttendanceSetOpt.get().getRoundingSet().getRoundingTimeUnit();
            cells.get("BF" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(roundingTimeUnit));
            
            /*
             * R4_126
             * 打刻丸め.出勤前後設定
             */
            Integer fontRearSection = roundingAttendanceSetOpt.get().getRoundingSet().getFontRearSection();
            cells.get("BG" + (startIndex + 1)).setValue(FontRearSection.valueOf(fontRearSection).description);
        }
        
        if (roundingOfficeSetOpt.isPresent()) {
            /*
             * R4_127
             * 打刻丸め.退勤
             */
            Integer roundingTimeUnit = roundingOfficeSetOpt.get().getRoundingSet().getRoundingTimeUnit();
            cells.get("BH" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(roundingTimeUnit));
            
            /*
             * R4_128
             * 打刻丸め.退勤前後設定
             */
            Integer fontRearSection = roundingOfficeSetOpt.get().getRoundingSet().getFontRearSection();
            cells.get("BI" + (startIndex + 1)).setValue(FontRearSection.valueOf(fontRearSection).description);
        }
        
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            List<StampReflectTimezoneDto> lstStampReflectTimezone = data.getFixedWorkSetting().getLstStampReflectTimezone();
            Optional<StampReflectTimezoneDto> lst1stGoWorkOpt = lstStampReflectTimezone.stream()
                .filter(x -> (x.getWorkNo() == 1 && x.getClassification().equals(GoLeavingWorkAtr.GO_WORK.value)))
                .findFirst();
            
            Optional<StampReflectTimezoneDto> lst2ndGoWorkOpt = lstStampReflectTimezone.stream()
                    .filter(x -> (x.getWorkNo() == 2 && x.getClassification().equals(GoLeavingWorkAtr.GO_WORK.value)))
                    .findFirst();
            
            Optional<StampReflectTimezoneDto> lst1stLeavingWorkOpt = lstStampReflectTimezone.stream()
                .filter(x -> (x.getWorkNo() == 1 && x.getClassification().equals(GoLeavingWorkAtr.LEAVING_WORK.value)))
                .findFirst();
            
            Optional<StampReflectTimezoneDto> lst2ndLeavingWorkOpt = lstStampReflectTimezone.stream()
                    .filter(x -> (x.getWorkNo() == 2 && x.getClassification().equals(GoLeavingWorkAtr.LEAVING_WORK.value)))
                    .findFirst();
            
            if (lst1stGoWorkOpt.isPresent()) {
                /*
                 * R4_129
                 * 出勤反映時間帯1回目.開始時刻
                 */
                Integer startTime = lst1stGoWorkOpt.get().getStartTime();
                cells.get("BJ" + (startIndex + 1)).setValue(startTime != null ? getInDayTimeWithFormat(startTime) : "");
                
                /*
                 * R4_130
                 * 出勤反映時間帯1回目.終了時刻
                 */
                Integer endTime = lst1stGoWorkOpt.get().getEndTime();
                cells.get("BK" + (startIndex + 1)).setValue(endTime != null ? getInDayTimeWithFormat(endTime) : "");
            }
            
            if (lst2ndGoWorkOpt.isPresent()) {
                /*
                 * R4_131
                 * 出勤反映時間帯2回目.開始時刻
                 */
                Integer startTime = lst2ndGoWorkOpt.get().getStartTime();
                cells.get("BL" + (startIndex + 1)).setValue(startTime != null ? getInDayTimeWithFormat(startTime) : "");
                
                /*
                 * R4_132
                 * 出勤反映時間帯2回目.終了時刻
                 */
                Integer endTime = lst2ndGoWorkOpt.get().getEndTime();
                cells.get("BM" + (startIndex + 1)).setValue(endTime != null ? getInDayTimeWithFormat(endTime) : "");
            }
            
            if (lst1stLeavingWorkOpt.isPresent()) {
                /*
                 * R4_133
                 * 退勤反映時間帯1回目.開始時刻
                 */
                Integer startTime = lst1stLeavingWorkOpt.get().getStartTime();
                cells.get("BN" + (startIndex + 1)).setValue(startTime != null ? getInDayTimeWithFormat(startTime) : "");
                
                /*
                 * R4_134
                 * 退勤反映時間帯1回目.終了時刻
                 */
                Integer endTime = lst1stLeavingWorkOpt.get().getEndTime();
                cells.get("BO" + (startIndex + 1)).setValue(endTime != null ? getInDayTimeWithFormat(endTime) : "");
            }
            
            if (lst2ndLeavingWorkOpt.isPresent()) {
                /*
                 * R4_135
                 * 退勤反映時間帯2回目.開始時刻
                 */
                Integer startTime = lst2ndLeavingWorkOpt.get().getStartTime();
                cells.get("BP" + (startIndex + 1)).setValue(startTime != null ? getInDayTimeWithFormat(startTime) : "");
                
                /*
                 * R4_136
                 * 退勤反映時間帯2回目.終了時刻
                 */
                Integer endTime = lst2ndLeavingWorkOpt.get().getEndTime();
                cells.get("BQ" + (startIndex + 1)).setValue(endTime != null ? getInDayTimeWithFormat(endTime) : "");
            }
            
            /*
             * R4_137
             * 優先設定.入門
             */
            if (prioritySetEnteringOpt.isPresent()) {
                cells.get("BR" + (startIndex + 1)).setValue(getPrioritySetAtr(prioritySetEnteringOpt.get().getPriorityAtr()));
            }
            
            /*
             * R4_138
             * 優先設定.退門
             */
            if (prioritySetExitOpt.isPresent()) {
                cells.get("BS" + (startIndex + 1)).setValue(getPrioritySetAtr(prioritySetExitOpt.get().getPriorityAtr()));
            }
            
            /*
             * R4_139
             * 優先設定.PCログオン
             */
            if (prioritySetPCLoginOpt.isPresent()) {
                cells.get("BT" + (startIndex + 1)).setValue(getPrioritySetAtr(prioritySetPCLoginOpt.get().getPriorityAtr()));
            }
            
            /*
             * R4_140
             * 優先設定.PCログオフ
             */
            if (prioritySetPCLogoutOpt.isPresent()) {
                cells.get("BU" + (startIndex + 1)).setValue(getPrioritySetAtr(prioritySetPCLogoutOpt.get().getPriorityAtr()));
            }
            
            if (roundingGoOutSetOpt.isPresent()) {
                /*
                 * R4_141
                 * 打刻丸め.外出
                 */
                Integer roundingTimeUnit = roundingGoOutSetOpt.get().getRoundingSet().getRoundingTimeUnit();
                cells.get("BV" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(roundingTimeUnit));
                
                /*
                 * R4_142
                 * 打刻丸め.外出前後ずらし
                 */
                Integer fontRearSection = roundingGoOutSetOpt.get().getRoundingSet().getFontRearSection();
                cells.get("BW" + (startIndex + 1)).setValue(fontRearSection == 0 ? "前にずらす" : "後ろにずらす");
            }
            
            if (roundingTurnBackSetOpt.isPresent()) {
                /*
                 * R4_143
                 * 打刻丸め.戻り
                 */
                Integer roundingTimeUnit = roundingTurnBackSetOpt.get().getRoundingSet().getRoundingTimeUnit();
                cells.get("BX" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(roundingTimeUnit));
                
                /*
                 * R4_144
                 * 打刻丸め.戻り前後ずらし
                 */
                Integer fontRearSection = roundingTurnBackSetOpt.get().getRoundingSet().getFontRearSection();
                cells.get("BY" + (startIndex + 1)).setValue(fontRearSection == 0 ? "前にずらす" : "後ろにずらす");
            }
            
            /*
             * R4_264
             * 計算設定.出勤を1分後から計算する
             */
            Integer attendanceMinuteLaterCalculate = data.getFixedWorkSetting().getCommonSetting().getStampSet().getRoundingTime().getAttendanceMinuteLaterCalculate();
            cells.get("BZ" + (startIndex + 1)).setValue((attendanceMinuteLaterCalculate == 0) ? "-" : "○");
            
            /*
             * R4_265
             * 計算設定.退勤を1分前まで計算する
             */
            Integer leaveWorkMinuteAgoCalculate = data.getFixedWorkSetting().getCommonSetting().getStampSet().getRoundingTime().getLeaveWorkMinuteAgoCalculate();
            cells.get("CA" + (startIndex + 1)).setValue(leaveWorkMinuteAgoCalculate == 0 ? "-" : "○");
        }
        
        // 5        タブグ:                休憩時間帯
        
        Boolean useHalfDayShiftBreakTime = data.getFixedWorkSetting().getUseHalfDayShift().isBreakTime();
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            /*
             * R4_262
             * 半日勤務を設定する
             */
            cells.get("CB" + (startIndex + 1)).setValue(getUseAtrByBoolean(useHalfDayShiftBreakTime));
        }
        
        List<FixHalfDayWorkTimezoneDto> lstHalfDayWorkTimezone = data.getFixedWorkSetting().getLstHalfDayWorkTimezone();
        Optional<FixHalfDayWorkTimezoneDto> halfDayWorkTimezoneOneDayOpt = lstHalfDayWorkTimezone.stream()
                .filter(x -> x.getDayAtr().equals(AmPmAtr.ONE_DAY.value)).findFirst();
        Optional<FixHalfDayWorkTimezoneDto> halfDayWorkTimezoneMorningOpt = lstHalfDayWorkTimezone.stream()
                .filter(x -> x.getDayAtr().equals(AmPmAtr.AM.value)).findFirst();
        Optional<FixHalfDayWorkTimezoneDto> halfDayWorkTimezoneAfternoonOpt = lstHalfDayWorkTimezone.stream()
                .filter(x -> x.getDayAtr().equals(AmPmAtr.PM.value)).findFirst();
        
        if (halfDayWorkTimezoneOneDayOpt.isPresent()) {
            List<DeductionTimeDto> deductionTimes = halfDayWorkTimezoneOneDayOpt.get().getRestTimezone().getTimezones();
            for (int i = 0; i < deductionTimes.size(); i++) {
                /*
                 * R4_145
                 * 1日勤務用.開始時間
                 */
                cells.get("CC" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(deductionTimes.get(i).getStart()));
                
                /*
                 * R4_146
                 * 1日勤務用.終了時間
                 */
                cells.get("CD" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(deductionTimes.get(i).getEnd()));
            }
        }
        
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            if (useHalfDayShiftBreakTime) {
                if (halfDayWorkTimezoneMorningOpt.isPresent()) {
                    List<DeductionTimeDto> deductionTimes = halfDayWorkTimezoneMorningOpt.get().getRestTimezone().getTimezones();
                    for (int i = 0; i < deductionTimes.size(); i++) {
                        /*
                         * R4_147
                         * 午前勤務用.開始時間
                         */
                        cells.get("CE" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(deductionTimes.get(i).getStart()));
                        
                        /*
                         * R4_148
                         * 午前勤務用.終了時間
                         */
                        cells.get("CF" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(deductionTimes.get(i).getEnd()));
                    }
                }
                
                if (halfDayWorkTimezoneAfternoonOpt.isPresent()) {
                    List<DeductionTimeDto> deductionTimes = halfDayWorkTimezoneAfternoonOpt.get().getRestTimezone().getTimezones();
                    for (int i = 0; i < deductionTimes.size(); i++) {
                        /*
                         * R4_149
                         * 午後勤務用.開始時間
                         */
                        cells.get("CG" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(deductionTimes.get(i).getStart()));
                        
                        /*
                         * R4_150
                         * 午後勤務用.終了時間
                         */
                        cells.get("CH" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(deductionTimes.get(i).getEnd()));
                    }
                }
            }
            
            /*
             * R4_152
             * 休憩計算設定.休憩中に退勤した場合の休憩時間の計算方法
             */
            Integer calculateMethod = data.getFixedWorkSetting().getCommonRestSet().getCalculateMethod();
            cells.get("CI" + (startIndex + 1)).setValue(getCalculatedMethodAtr(calculateMethod));
        }
        
        // 6        タブグ:                休出時間帯
        
        List<HDWorkTimeSheetSettingDto> lstWorkTimezone = data.getFixedWorkSetting().getOffdayWorkTimezone().getLstWorkTimezone();
        Collections.sort(lstWorkTimezone, new Comparator<HDWorkTimeSheetSettingDto>() {
            public int compare(HDWorkTimeSheetSettingDto o1, HDWorkTimeSheetSettingDto o2) {
                return o1.getWorkTimeNo().compareTo(o2.getWorkTimeNo());
            };
        });
        
        for (int i = 0; i < lstWorkTimezone.size(); i++) {
            final int index = i;
            
            /*
             * R4_153
             * 休出時間帯.開始時間
             */
            cells.get("CJ" + (startIndex + lstWorkTimezone.get(i).getWorkTimeNo()))
                .setValue(getInDayTimeWithFormat(lstWorkTimezone.get(i).getTimezone().getStart()));
            
            /*
             * R4_154
             * 休出時間帯.終了時間
             */
            cells.get("CK" + (startIndex + lstWorkTimezone.get(i).getWorkTimeNo()))
            .setValue(getInDayTimeWithFormat(lstWorkTimezone.get(i).getTimezone().getEnd()));
            
            Optional<WorkdayoffFrameFindDto> workDayOffFrameInLegalBreak = otFrameFind.stream()
                .filter(x -> x.getWorkdayoffFrNo() == lstWorkTimezone.get(index).getInLegalBreakFrameNo().intValue())
                .findFirst();
            Optional<WorkdayoffFrameFindDto> workDayOffFrameOutLegalBreak = otFrameFind.stream()
                    .filter(x -> x.getWorkdayoffFrNo() == lstWorkTimezone.get(index).getOutLegalBreakFrameNo().intValue())
                    .findFirst();
            Optional<WorkdayoffFrameFindDto> workDayOffFrameoutLegalPubHD = otFrameFind.stream()
                    .filter(x -> x.getWorkdayoffFrNo() == lstWorkTimezone.get(index).getOutLegalPubHDFrameNo().intValue())
                    .findFirst();
            
            /*
             * R4_155
             * 休出時間帯.法定内休出枠
             */
            if (workDayOffFrameInLegalBreak.isPresent()) {
                cells.get("CL" + (startIndex + lstWorkTimezone.get(i).getWorkTimeNo()))
                    .setValue(workDayOffFrameInLegalBreak.get().getWorkdayoffFrName());
            }
            
            /*
             * R4_156
             * 休出時間帯.法定外休出枠
             */
            if (workDayOffFrameOutLegalBreak.isPresent()) {
                cells.get("CM" + (startIndex + lstWorkTimezone.get(i).getWorkTimeNo()))
                    .setValue(workDayOffFrameOutLegalBreak.get().getWorkdayoffFrName());
            }
            
            /*
             * R4_157
             * 休出時間帯.法定外休出枠（祝日）
             */
            if (workDayOffFrameoutLegalPubHD.isPresent()) {
                cells.get("CN" + (startIndex + lstWorkTimezone.get(i).getWorkTimeNo()))
                    .setValue(workDayOffFrameoutLegalPubHD.get().getWorkdayoffFrName());
            }
            
            /*
             * R4_158
             * 休出時間帯.丸め
             */
            cells.get("CO" + (startIndex + lstWorkTimezone.get(i).getWorkTimeNo()))
            .setValue(getRoundingTimeUnitEnum(lstWorkTimezone.get(i).getTimezone().getRounding().getRoundingTime()));
            
            /*
             * R4_159
             * 休出時間帯.端数処理
             */
            cells.get("CP" + (startIndex + lstWorkTimezone.get(i).getWorkTimeNo()))
            .setValue(getRoundingEnum(lstWorkTimezone.get(i).getTimezone().getRounding().getRounding()));
        }
        
        // 7        タブグ:                休出休憩
        
        List<DeductionTimeDto> timeZones = data.getFixedWorkSetting().getOffdayWorkTimezone().getRestTimezone().getTimezones();
        
        for (int i = 0; i < timeZones.size(); i++) {
            /*
             * R4_160
             * 休出休憩.開始時間
             */
            cells.get("CQ" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(timeZones.get(i).getStart()));
            
            /*
             * R4_161
             * 休出休憩.終了時間
             */
            cells.get("CR" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(timeZones.get(i).getEnd()));
        }
        
        // 8        タブグ:                外出
        
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            /*
             * R4_162
             * 外出丸め設定.同じ枠内での丸め設定
             */
            Integer setSameFrameRounding = data.getFixedWorkSetting().getCommonSetting().getGoOutSet().getTotalRoundingSet().getSetSameFrameRounding();
            cells.get("CS" + (startIndex + 1)).setValue(getFrameRoundingAtr(setSameFrameRounding));
            
            /*
             * R4_163
             * 外出丸め設定.枠を跨る場合の丸め設定
             */
            Integer frameStraddRoundingSet = data.getFixedWorkSetting().getCommonSetting().getGoOutSet().getTotalRoundingSet().getFrameStraddRoundingSet();
            cells.get("CT" + (startIndex + 1)).setValue(getFrameRoundingAtr(frameStraddRoundingSet));
            
            /*
             * R4_164
             * 私用・組合外出時間.就業時間帯
             */
            Integer roundingMethodAppro = data.getFixedWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getWorkTimezone().getPrivateUnionGoOut().getApproTimeRoundingSetting().getRoundingMethod();
            cells.get("CU" + (startIndex + 1)).setValue("就業" + getApproTimeRoundingAtr(roundingMethodAppro));
            
            /*
             * R4_165
             * 私用・組合外出時間.丸め設定
             */
            Integer unitAppro = data.getFixedWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getWorkTimezone().getPrivateUnionGoOut()
                    .getApproTimeRoundingSetting().getRoundingSetting().getRoundingTime();
            cells.get("CV" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitAppro));
            
            /*
             * R4_166
             * 私用・組合外出時間.丸め設定端数
             */
            Integer roundingAppro = data.getFixedWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getWorkTimezone().getPrivateUnionGoOut()
                    .getApproTimeRoundingSetting().getRoundingSetting().getRounding();
            cells.get("CW" + (startIndex + 1)).setValue(getRoundingEnum(roundingAppro));
            
            /*
             * R4_167
             * 私用・組合外出時間.残業時間帯
             */
            Integer roundingMethodApproOt = data.getFixedWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getOttimezone().getPrivateUnionGoOut().getApproTimeRoundingSetting().getRoundingMethod();
            cells.get("CX" + (startIndex + 1)).setValue("残業" + getApproTimeRoundingAtr(roundingMethodApproOt));
            /*
             * R4_168
             * 私用・組合外出時間.丸め設定
             */
            Integer unitApproOt = data.getFixedWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getOttimezone().getPrivateUnionGoOut()
                    .getApproTimeRoundingSetting().getRoundingSetting().getRoundingTime();
            cells.get("CY" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitApproOt));
            
            /*
             * R4_169
             * 私用・組合外出時間.丸め設定端数
             */
            Integer roundingApproOt = data.getFixedWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getOttimezone().getPrivateUnionGoOut()
                    .getApproTimeRoundingSetting().getRoundingSetting().getRounding();
            cells.get("CZ" + (startIndex + 1)).setValue(getRoundingEnum(roundingApproOt));
            
            /*
             * R4_170
             * 私用・組合外出時間.休出時間帯
             */
            Integer roundingMethodApproHol = data.getFixedWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getPubHolWorkTimezone().getPrivateUnionGoOut().getApproTimeRoundingSetting().getRoundingMethod();
            cells.get("DA" + (startIndex + 1)).setValue(getApproTimeRoundingAtr(roundingMethodApproHol));
            
            /*
             * R4_171
             * 私用・組合外出時間.丸め設定
             */
            Integer unitApproHol = data.getFixedWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getPubHolWorkTimezone().getPrivateUnionGoOut()
                    .getApproTimeRoundingSetting().getRoundingSetting().getRoundingTime();
            cells.get("DB" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitApproHol));
            
            /*
             * R4_172
             * 私用・組合外出時間.丸め設定端数
             */
            Integer roundingApproHol = data.getFixedWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getPubHolWorkTimezone().getPrivateUnionGoOut()
                    .getApproTimeRoundingSetting().getRoundingSetting().getRounding();
            cells.get("DC" + (startIndex + 1)).setValue(getRoundingEnum(roundingApproHol));
            
            /*
             * R4_173
             * 私用・組合外出控除時間.就業時間帯
             */
            Integer roundingMethodDeduct = data.getFixedWorkSetting().getCommonSetting().getGoOutSet().getDiffTimezoneSetting()
                    .getWorkTimezone().getPrivateUnionGoOut().getDeductTimeRoundingSetting().getRoundingMethod();
            cells.get("DD" + (startIndex + 1)).setValue(getApproTimeRoundingAtr(roundingMethodDeduct));
            
            /*
             * R4_174
             * 私用・組合外出控除時間.丸め設定
             */
            Integer unitDeduct = data.getFixedWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getWorkTimezone().getPrivateUnionGoOut()
                    .getDeductTimeRoundingSetting().getRoundingSetting().getRoundingTime();
            cells.get("DE" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitDeduct));
            
            /*
             * R4_175
             * 私用・組合外出控除時間.丸め設定端数
             */
            Integer roundingDeduct = data.getFixedWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getWorkTimezone().getPrivateUnionGoOut()
                    .getDeductTimeRoundingSetting().getRoundingSetting().getRounding();
            cells.get("DF" + (startIndex + 1)).setValue(getRoundingEnum(roundingDeduct));
            
            /*
             * R4_176
             * 私用・組合外出控除時間.残業時間帯
             */
            Integer roundingMethodDeductOt = data.getFixedWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getOttimezone().getPrivateUnionGoOut()
                    .getDeductTimeRoundingSetting().getRoundingMethod();
            cells.get("DG" + (startIndex + 1)).setValue(getApproTimeRoundingAtr(roundingMethodDeductOt));
            
            /*
             * R4_177
             * 私用・組合外出控除時間.丸め設定
             */
            Integer unitDeductOt = data.getFixedWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getOttimezone().getPrivateUnionGoOut()
                    .getDeductTimeRoundingSetting().getRoundingSetting().getRoundingTime();
            cells.get("DH" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitDeductOt));
            
            /*
             * R4_178
             * 私用・組合外出控除時間.丸め設定端数
             */
            Integer roundingDeductOt = data.getFixedWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getOttimezone().getPrivateUnionGoOut()
                    .getDeductTimeRoundingSetting().getRoundingSetting().getRounding();
            cells.get("DI" + (startIndex + 1)).setValue(getRoundingEnum(roundingDeductOt));
            
            /*
             * R4_179
             * 私用・組合外出控除時間.休出時間帯
             */
            Integer roundingMethodDeductHol = data.getFixedWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getPubHolWorkTimezone().getPrivateUnionGoOut()
                    .getDeductTimeRoundingSetting().getRoundingMethod();
            cells.get("DJ" + (startIndex + 1)).setValue(getApproTimeRoundingAtr(roundingMethodDeductHol));
            
            /*
             * R4_180
             * 私用・組合外出控除時間.丸め設定
             */
            Integer unitDeductHol = data.getFixedWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getPubHolWorkTimezone().getPrivateUnionGoOut()
                    .getDeductTimeRoundingSetting().getRoundingSetting().getRoundingTime();
            cells.get("DK" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitDeductHol));
            
            /*
             * R4_181
             * 私用・組合外出控除時間.丸め設定端数
             */
            Integer roundingDeductHol = data.getFixedWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getPubHolWorkTimezone().getPrivateUnionGoOut()
                    .getDeductTimeRoundingSetting().getRoundingSetting().getRounding();
            cells.get("DL" + (startIndex + 1)).setValue(getRoundingEnum(roundingDeductHol));
            
            /*
             * R4_182
             * 公用・有償外出時間.就業時間帯
             */
            Integer roundingMethodOfficalAppro = data.getFixedWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getWorkTimezone().getOfficalUseCompenGoOut()
                    .getApproTimeRoundingSetting().getRoundingMethod();
            cells.get("DM" + (startIndex + 1)).setValue(getApproTimeRoundingAtr(roundingMethodOfficalAppro));
            
            /*
             * R4_183
             * 公用・有償外出時間.丸め設定
             */
            Integer unitOfficalAppro = data.getFixedWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getWorkTimezone().getOfficalUseCompenGoOut()
                    .getApproTimeRoundingSetting().getRoundingSetting().getRoundingTime();
            cells.get("DN" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitOfficalAppro));
            
            /*
             * R4_184
             * 公用・有償外出時間.丸め設定端数
             */
            Integer roundingOfficalAppro = data.getFixedWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getWorkTimezone().getOfficalUseCompenGoOut()
                    .getApproTimeRoundingSetting().getRoundingSetting().getRounding();
            cells.get("DO" + (startIndex + 1)).setValue(getRoundingEnum(roundingOfficalAppro));
            
            /*
             * R4_185
             * 公用・有償外出時間.残業時間帯
             */
            Integer roundingMethodOfficlaOt = data.getFixedWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getOttimezone().getOfficalUseCompenGoOut()
                    .getApproTimeRoundingSetting().getRoundingMethod();
            cells.get("DP" + (startIndex + 1)).setValue(getApproTimeRoundingAtr(roundingMethodOfficlaOt));
            
            /*
             * R4_186
             * 公用・有償外出時間.丸め設定
             */
            Integer unitOfficalOt = data.getFixedWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getOttimezone().getOfficalUseCompenGoOut()
                    .getApproTimeRoundingSetting().getRoundingSetting().getRoundingTime();
            cells.get("DQ" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitOfficalOt));
            
            /*
             * R4_187
             * 公用・有償外出時間.丸め設定端数
             */
            Integer roundingOfficalOt = data.getFixedWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getOttimezone().getOfficalUseCompenGoOut()
                    .getApproTimeRoundingSetting().getRoundingSetting().getRounding();
            cells.get("DR" + (startIndex + 1)).setValue(getRoundingEnum(roundingOfficalOt));
            
            /*
             * R4_188
             * 公用・有償外出時間.休出時間帯
             */
            Integer roundingMethodOfficalHol = data.getFixedWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getPubHolWorkTimezone().getOfficalUseCompenGoOut()
                    .getApproTimeRoundingSetting().getRoundingMethod();
            cells.get("DS" + (startIndex + 1)).setValue(getApproTimeRoundingAtr(roundingMethodOfficalHol));
            
            /*
             * R4_189
             * 公用・有償外出時間.丸め設定
             */
            Integer unitOfficalHol = data.getFixedWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getPubHolWorkTimezone().getOfficalUseCompenGoOut()
                    .getApproTimeRoundingSetting().getRoundingSetting().getRoundingTime();
            cells.get("DT" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitOfficalHol));
            
            /*
             * R4_190
             * 公用・有償外出時間.丸め設定端数
             */
            Integer roundingOfficalHol = data.getFixedWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getPubHolWorkTimezone().getOfficalUseCompenGoOut()
                    .getApproTimeRoundingSetting().getRoundingSetting().getRounding();
            cells.get("DU" + (startIndex + 1)).setValue(getRoundingEnum(roundingOfficalHol));
        }
        
        // 9        タブグ:                遅刻早退
        
        List<OtherEmTimezoneLateEarlySetDto> otherClassSets = data.getFixedWorkSetting().getCommonSetting().getLateEarlySet().getOtherClassSets();
        Optional<OtherEmTimezoneLateEarlySetDto> otherLate = otherClassSets.stream()
                .filter(x -> x.getLateEarlyAtr().equals(LateEarlyAtr.LATE.value)).findFirst();
        Optional<OtherEmTimezoneLateEarlySetDto> otherEarly = otherClassSets.stream()
                .filter(x -> x.getLateEarlyAtr().equals(LateEarlyAtr.EARLY.value)).findFirst();
        
        if (otherLate.isPresent()) {
            /*
             * R4_191
             * 遅刻早退時間丸め.遅刻丸め
             */
            Integer unitRecordLate = otherLate.get().getRecordTimeRoundingSet().getRoundingTime();
            cells.get("DV" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitRecordLate));
            
            /*
             * R4_192
             * 遅刻早退時間丸め.遅刻端数
             */
            Integer roundingRecordLate = otherLate.get().getRecordTimeRoundingSet().getRounding();
            cells.get("DW" + (startIndex + 1)).setValue(getRoundingEnum(roundingRecordLate));
        }
        
        if (otherEarly.isPresent()) {
            /*
             * R4_193
             * 遅刻早退時間丸め.早退丸め
             */
            Integer unitRecordEarly = otherEarly.get().getRecordTimeRoundingSet().getRoundingTime();
            cells.get("DX" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitRecordEarly));
            
            /*
             * R4_194
             * 遅刻早退時間丸め.早退端数
             */
            Integer roundingRecordEarly = otherEarly.get().getRecordTimeRoundingSet().getRounding();
            cells.get("DY" + (startIndex + 1)).setValue(getRoundingEnum(roundingRecordEarly));
        }
        
        if (otherLate.isPresent()) {
            /*
             * R4_195
             * 遅刻早退控除時間丸め.遅刻丸め
             */
            Integer unitDelLate = otherLate.get().getDelTimeRoundingSet().getRoundingTime();
            cells.get("DZ" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitDelLate));
            
            /*
             * R4_196
             * 遅刻早退控除時間丸め.遅刻端数
             */
            Integer roundingDelLate = otherLate.get().getDelTimeRoundingSet().getRounding();
            cells.get("EA" + (startIndex + 1)).setValue(getRoundingEnum(roundingDelLate));
        }
        
        if (otherEarly.isPresent()) {
            /*
             * R4_197
             * 遅刻早退控除時間丸め.早退丸め
             */
            Integer unitDelEarly = otherEarly.get().getDelTimeRoundingSet().getRoundingTime();
            cells.get("EB" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitDelEarly));
            
            /*
             * R4_198
             * 遅刻早退控除時間丸め.早退端数
             */
            Integer roundingDelEarly = otherEarly.get().getDelTimeRoundingSet().getRounding();
            cells.get("EC" + (startIndex + 1)).setValue(getRoundingEnum(roundingDelEarly));
        }
        
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            /*
             * R4_199
             * 遅刻早退詳細設定.控除時間.遅刻早退時間を就業時間から控除する
             */
            boolean isDelFromEmTime = data.getFixedWorkSetting().getCommonSetting().getLateEarlySet().getCommonSet().isDelFromEmTime();
            cells.get("ED" + (startIndex + 1)).setValue(isDelFromEmTime ?  "○" : "-");
            
            if (otherLate.isPresent()) {
                /*
                 * R4_202
                 * 遅刻早退詳細設定.猶予時間.遅刻猶予時間
                 */
                Integer graceTime = otherLate.get().getGraceTimeSet().getGraceTime();
                cells.get("EE" + (startIndex + 1)).setValue(getInDayTimeWithFormat(graceTime));
                
                /*
                 * R4_203
                 * 遅刻早退詳細設定.猶予時間.遅刻猶予時間を就業時間に含める
                 */
                boolean includeWorkingHour = otherLate.get().getGraceTimeSet().isIncludeWorkingHour();
                cells.get("EF" + (startIndex + 1)).setValue(includeWorkingHour ? "○" : "-");
            }
            
            if (otherEarly.isPresent()) {
                /*
                 * R4_204
                 * 遅刻早退詳細設定.猶予時間.早退猶予時間
                 */
                Integer graceTime = otherEarly.get().getGraceTimeSet().getGraceTime();
                cells.get("EG" + (startIndex + 1)).setValue(getInDayTimeWithFormat(graceTime));
                
                /*
                 * R4_205
                 * 遅刻早退詳細設定.猶予時間.早退猶予時間を就業時間に含める
                 */
                boolean includeWorkingHour = otherEarly.get().getGraceTimeSet().isIncludeWorkingHour();
                cells.get("EH" + (startIndex + 1)).setValue(includeWorkingHour ? "○" : "-");
            }
        }
        
        // 10       タブグ:                加給
        
        /*
         * R4_206
         * コード
         */
        String raisingSalarySetCode = data.getFixedWorkSetting().getCommonSetting().getRaisingSalarySet();
        cells.get("EI" + (startIndex + 1)).setValue(raisingSalarySetCode != null ? raisingSalarySetCode : "");
        
        /*
         * R4_207
         * 名称
         */
        if (raisingSalarySetCode != null) {
            Optional<BonusPaySetting> bonusPaySettingOpt = this.bpSettingRepository
                    .getBonusPaySetting(AppContexts.user().companyId(), new BonusPaySettingCode(raisingSalarySetCode));
            if (bonusPaySettingOpt.isPresent()) {
                String raisingSalaryName = bonusPaySettingOpt.get().getName().v();
                cells.get("EJ" + (startIndex + 1)).setValue(raisingSalaryName);
            }
        }
        
        // 11       タブグ:                代休
        
        List<WorkTimezoneOtherSubHolTimeSetDto> subHolTimeSet = data.getFixedWorkSetting().getCommonSetting().getSubHolTimeSet();
        Optional<WorkTimezoneOtherSubHolTimeSetDto> subHolTimeWorkDayOffSet = subHolTimeSet.stream()
                .filter(x -> x.getOriginAtr().equals(CompensatoryOccurrenceDivision.WorkDayOffTime.value)).findFirst();
        Optional<WorkTimezoneOtherSubHolTimeSetDto> subHolTimeOverTimeOffSet = subHolTimeSet.stream()
                .filter(x -> x.getOriginAtr().equals(CompensatoryOccurrenceDivision.FromOverTime.value)).findFirst();
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            
            if (subHolTimeWorkDayOffSet.isPresent()) {
                /*
                 * R4_47
                 * 代休発生に必要な時間.休日出勤
                 */
                boolean useDivision = subHolTimeWorkDayOffSet.get().getSubHolTimeSet().isUseDivision();
                cells.get("EK" + (startIndex + 1)).setValue(useDivision ? "○" : "-");
                
                /*
                 * R4_208
                 * 代休発生に必要な時間.時間
                 */
                Integer subHolTransferSetAtr = subHolTimeWorkDayOffSet.get().getSubHolTimeSet().getSubHolTransferSetAtr();
                cells.get("EL" + (startIndex + 1)).setValue(subHolTransferSetAtr == 0 ? "指定時間" : "一定時間");
            }
        }
        
        if (subHolTimeWorkDayOffSet.isPresent()) {
            /*
             * R4_209
             * 代休発生に必要な時間.１日
             */
            Integer oneDayTime = subHolTimeWorkDayOffSet.get().getSubHolTimeSet().getDesignatedTime().getOneDayTime();
            cells.get("EM" + (startIndex + 1)).setValue(getInDayTimeWithFormat(oneDayTime));
            
            /*
             * R4_210
             * 代休発生に必要な時間.半日
             */
            Integer halfDayTime = subHolTimeWorkDayOffSet.get().getSubHolTimeSet().getDesignatedTime().getHalfDayTime();
            cells.get("EN" + (startIndex + 1)).setValue(getInDayTimeWithFormat(halfDayTime));
        }
        
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            if (subHolTimeWorkDayOffSet.isPresent()) {
                /*
                 * R4_211
                 * 代休発生に必要な時間.一定時間
                 */
                Integer certainTime = subHolTimeWorkDayOffSet.get().getSubHolTimeSet().getCertainTime();
                cells.get("EO" + (startIndex + 1)).setValue(getInDayTimeWithFormat(certainTime));
            }
            
            if (subHolTimeOverTimeOffSet.isPresent()) {
                /*
                 * R4_48
                 * 代休発生に必要な時間.残業
                 */
                boolean useDivision = subHolTimeOverTimeOffSet.get().getSubHolTimeSet().isUseDivision();
                cells.get("EP" + (startIndex + 1)).setValue(useDivision ? "○" : "-");
                
                /*
                 * R4_212
                 * 代休発生に必要な時間.時間
                 */
                Integer subHolTransferSetAtr = subHolTimeOverTimeOffSet.get().getSubHolTimeSet().getSubHolTransferSetAtr();
                cells.get("EQ" + (startIndex + 1)).setValue(subHolTransferSetAtr == 0 ? "指定時間" : "一定時間");
                
                /*
                 * R4_213
                 * 代休発生に必要な時間.１日
                 */
                Integer oneDayTime = subHolTimeOverTimeOffSet.get().getSubHolTimeSet().getDesignatedTime().getOneDayTime();
                cells.get("ER" + (startIndex + 1)).setValue(getInDayTimeWithFormat(oneDayTime));
                
                /*
                 * R4_214
                 * 代休発生に必要な時間.半日
                 */
                Integer halfDayTime = subHolTimeOverTimeOffSet.get().getSubHolTimeSet().getDesignatedTime().getHalfDayTime();
                cells.get("ES" + (startIndex + 1)).setValue(getInDayTimeWithFormat(halfDayTime));
                
                /*
                 * R4_215
                 * 代休発生に必要な時間.一定時間
                 */
                Integer certainTime = subHolTimeOverTimeOffSet.get().getSubHolTimeSet().getCertainTime();
                cells.get("ET" + (startIndex + 1)).setValue(getInDayTimeWithFormat(certainTime));
            }
        }
        
        // 12       タブグ:                深夜
        
        /*
         * R4_216
         * 深夜.深夜時間丸め
         */
        Integer unit = data.getFixedWorkSetting().getCommonSetting().getLateNightTimeSet().getRoundingSetting().getRoundingTime();
        cells.get("EU" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unit));
        
        /*
         * R4_217
         * 深夜.深夜時間端数
         */
        Integer rounding = data.getFixedWorkSetting().getCommonSetting().getLateNightTimeSet().getRoundingSetting().getRounding();
        cells.get("EV" + (startIndex + 1)).setValue(getRoundingEnum(rounding));
        
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            // 13       タブグ:                臨時
            
            /*
             * R4_218
             * 臨時.臨時丸め
             */
            Integer unitExtraord = data.getFixedWorkSetting().getCommonSetting().getExtraordTimeSet().getTimeRoundingSet().getRoundingTime();
            cells.get("EW" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitExtraord));
            
            /*
             * R4_219
             * 臨時.臨時端数
             */
            Integer roundingExtraord = data.getFixedWorkSetting().getCommonSetting().getExtraordTimeSet().getTimeRoundingSet().getRounding();
            cells.get("EX" + (startIndex + 1)).setValue(getRoundingEnum(roundingExtraord));
            
            // 14       タブグ:                育児
            
            /*
             * R4_220
             * 育児.育児時間帯に勤務した場合の扱い
             */
            boolean childCareWorkUse = data.getFixedWorkSetting().getCommonSetting().getShortTimeWorkSet().isChildCareWorkUse();
            cells.get("EY" + (startIndex + 1)).setValue(childCareWorkUse ? "育児時間を減算する" : "育児時間を減算しない");
            
            /*
             * R4_221
             * 育児.介護時間帯に勤務した場合の扱い
             */
            boolean nursTimezoneWorkUse = data.getFixedWorkSetting().getCommonSetting().getShortTimeWorkSet().isNursTimezoneWorkUse();
            cells.get("EZ" + (startIndex + 1)).setValue(nursTimezoneWorkUse ? "介護時間を減算する" : "介護時間を減算しない");
            
            // 15       タブグ:                医療
            
            List<WorkTimezoneMedicalSetDto> medicalSets = data.getFixedWorkSetting().getCommonSetting().getMedicalSet();
            Optional<WorkTimezoneMedicalSetDto> medicalDaySet = medicalSets.stream()
                    .filter(x -> x.getWorkSystemAtr().equals(WorkSystemAtr.DAY_SHIFT.value)).findFirst();
            Optional<WorkTimezoneMedicalSetDto> medicalNightSet = medicalSets.stream()
                    .filter(x -> x.getWorkSystemAtr().equals(WorkSystemAtr.NIGHT_SHIFT.value)).findFirst();
            
            if (medicalDaySet.isPresent()) {
                /*
                 * R4_222
                 * 医療.日勤申し送り時間
                 */
                Integer applicationTime = medicalDaySet.get().getApplicationTime();
                cells.get("FA" + (startIndex + 1)).setValue(getInDayTimeWithFormat(applicationTime));
            }
            
            if (medicalNightSet.isPresent()) {
                /*
                 * R4_223
                 * 医療.夜勤申し送り時間
                 */
                Integer applicationTime = medicalNightSet.get().getApplicationTime();
                cells.get("FB" + (startIndex + 1)).setValue(getInDayTimeWithFormat(applicationTime));
            }
            
            if (medicalDaySet.isPresent()) {
                /*
                 * R4_224
                 * 医療.日勤勤務時間.丸め
                 */
                Integer unitDay = medicalDaySet.get().getRoundingSet().getRoundingTime();
                cells.get("FC" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitDay));
                
                /*
                 * R4_225
                 * 医療.日勤勤務時間.端数
                 */
                Integer roundingDay = medicalDaySet.get().getRoundingSet().getRounding();
                cells.get("FD" + (startIndex + 1)).setValue(getRoundingEnum(roundingDay));
            }
            
            if (medicalNightSet.isPresent()) {
                /*
                 * R4_226
                 * 医療.夜勤勤務時間.丸め
                 */
                Integer unitNight = medicalNightSet.get().getRoundingSet().getRoundingTime();
                cells.get("FE" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitNight));
                
                /*
                 * R4_227
                 * 医療.夜勤勤務時間.端数
                 */
                Integer roundingNight = medicalNightSet.get().getRoundingSet().getRounding();
                cells.get("FF" + (startIndex + 1)).setValue(getRoundingEnum(roundingNight));
            }
            
            // 16       タブグ:                0時跨ぎ
            
            /*
             * R4_228
             * ０時跨ぎ.0時跨ぎ計算
             */
            boolean zeroHStraddCalculateSet = data.getFixedWorkSetting().getCommonSetting().isZeroHStraddCalculateSet();
            cells.get("FG" + (startIndex + 1)).setValue(getUseAtrByBoolean(zeroHStraddCalculateSet));
        }
        
        // 17       タブグ:                その地
        
        boolean ootsuka = AppContexts.optionLicense().customize().ootsuka();
        if (ootsuka) {
            /*
             * R4_229
             * その他.勤務種類が休暇の場合に就業時間を計算するか
             */
            Integer isCalculate = data.getFixedWorkSetting().getCommonSetting().getHolidayCalculation().getIsCalculate();
            cells.get("FH" + (startIndex + 1)).setValue(getUseAtrByInteger(isCalculate));
            
            FixedWorkCalcSettingDto calculationSetting = data.getFixedWorkSetting().getCalculationSetting();
            if (calculationSetting != null) {
                /*
                 * R4_57
                 * その他.休暇加算時間を加算する場合に就業時間として加算するか.しない
                 */
                Integer calcMethod = calculationSetting.getExceededPredAddVacationCalc().getCalcMethod();
                cells.get("FI" + (startIndex + 1)).setValue(getUseAtrByInteger(calcMethod));
                
                /*
                 * R4_231
                 * その他.休暇加算時間を加算する場合に就業時間として加算するか.残業枠
                 */
                Integer otFrameNo = calculationSetting.getExceededPredAddVacationCalc().getOtFrameNo();
                Optional<WorkdayoffFrameFindDto> otFrame = otFrameFind.stream().filter(x -> x.getWorkdayoffFrNo() == otFrameNo).findFirst();
                if (otFrame.isPresent()) {
                    cells.get("FJ" + (startIndex + 1)).setValue(otFrame.get().getWorkdayoffFrName());
                }
                
                /*
                 * R4_58
                 * その他.休憩未取得時に就業時間として計算するか
                 */
                Integer calcMethodOt = calculationSetting.getOverTimeCalcNoBreak().getCalcMethod();
                cells.get("FK" + (startIndex + 1)).setValue(getUseAtrByInteger(calcMethodOt));
                
                /*
                 * R4_232
                 * その他.休憩未取得時に就業時間として計算するか.法定内残業枠
                 */
                Integer inLawOT = calculationSetting.getOverTimeCalcNoBreak().getInLawOT();
                Optional<WorkdayoffFrameFindDto> otFrameInLaw = otFrameFind.stream().filter(x -> x.getWorkdayoffFrNo() == inLawOT).findFirst();
                if (otFrameInLaw.isPresent()) {
                    cells.get("FL" + (startIndex + 1)).setValue(otFrameInLaw.get().getWorkdayoffFrName());
                }
                
                /*
                 * R4_233
                 * その他.休憩未取得時に就業時間として計算するか.法定外残業枠
                 */
                Integer notInLawOT = calculationSetting.getOverTimeCalcNoBreak().getInLawOT();
                Optional<WorkdayoffFrameFindDto> otFrameNotInLaw = otFrameFind.stream().filter(x -> x.getWorkdayoffFrNo() == notInLawOT).findFirst();
                if (otFrameNotInLaw.isPresent()) {
                    cells.get("FM" + (startIndex + 1)).setValue(otFrameNotInLaw.get().getWorkdayoffFrName());
                }
            }
        } else {
        	cells.deleteColumns(163, 6, false);
        }
    }
    
    /**
     * 勤務形態 流動
     * @param data
     * @param cell
     * @param startIndex
     */
    public void insertDataOneLineFlow(WorkTimeSettingInfoDto data, Cells cells, int startIndex) {
        Integer displayMode = data.getDisplayMode().displayMode;
        
        // 1        タブグ:                所定
        
        this.printDataPrescribed(data, cells, startIndex, FLOW);
        
        // 2        タブグ:                勤務時間帯
        
        /*
         * R5_79
         * 勤務時間帯.就業時間帯丸め.丸め
         */
        Integer unit = data.getFlowWorkSetting().getHalfDayWorkTimezone().getWorkTimeZone().getWorkTimeRounding().getRoundingTime();
        cells.get("Y" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unit));
        
        /*
         * R5_80
         * 勤務時間帯.就業時間帯丸め.端数
         */
        Integer rounding = data.getFlowWorkSetting().getHalfDayWorkTimezone().getWorkTimeZone().getWorkTimeRounding().getRounding();
        cells.get("Z" + (startIndex + 1)).setValue(getRoundingEnum(rounding));
        
        /*
         * R5_81
         * 勤務時間帯.予定開始時刻より前に出勤した場合の計算方法
         */
        Integer calcStartTimeSet = data.getFlowWorkSetting().getFlowSetting().getCalculateSetting().getCalcStartTimeSet();
        cells.get("AA" + (startIndex + 1)).setValue(PrePlanWorkTimeCalcMethod.valueOf(calcStartTimeSet).description);
        
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            /*
             * R5_82
             * 勤務時間帯.所定時間に変更（予定開始・終了時刻の変更）があった場合、所定時間が終わり次第、残業を求める
             */
            Integer fixedChangeAtr = data.getFlowWorkSetting().getFlowSetting().getOvertimeSetting().getFixedChangeAtr();
            cells.get("AB" + (startIndex + 1)).setValue(FixedChangeAtr.valueOf(fixedChangeAtr).description);
        }
        
        // 3        タブグ:                残業時間帯
        
        List<WorkdayoffFrameFindDto> otFrameFind = this.finder.findAllUsed();
        List<OvertimeWorkFrameFindDto> overTimeLst = this.overTimeFinder.findAllUsed();
        List<FlOTTimezoneDto> lstOTTimezone = data.getFlowWorkSetting().getHalfDayWorkTimezone().getWorkTimeZone().getLstOTTimezone();
        for (int i = 0; i < lstOTTimezone.size(); i++) {
            /*
             * R5_83
             * 残業時間帯.経過時間
             */
            Integer elapsedTime = lstOTTimezone.get(i).getFlowTimeSetting().getElapsedTime();
            cells.get("AC" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(elapsedTime));
            
            /*
             * R5_84
             * 残業時間帯.丸め
             */
            Integer unitOtTime = lstOTTimezone.get(i).getFlowTimeSetting().getRounding().getRoundingTime();
            cells.get("AD" + ((startIndex + 1) + i)).setValue(getRoundingTimeUnitEnum(unitOtTime));
            
            /*
             * R5_85
             * 残業時間帯.端数
             */
            Integer roundingOtTime = lstOTTimezone.get(i).getFlowTimeSetting().getRounding().getRounding();
            cells.get("AE" + ((startIndex + 1) + i)).setValue(getRoundingEnum(roundingOtTime));
            
            /*
             * R5_86
             * 残業時間帯.残業枠
             */
            BigDecimal otFrameNo = lstOTTimezone.get(i).getOtFrameNo();
            Optional<OvertimeWorkFrameFindDto> otFrameOpt = overTimeLst.stream()
                    .filter(x -> x.getOvertimeWorkFrNo() == otFrameNo.intValue()).findFirst();
            if (otFrameOpt.isPresent()) {
                cells.get("AF" + ((startIndex + 1) + i)).setValue(otFrameOpt.get().getOvertimeWorkFrName());
            }
            
            if (displayMode.equals(DisplayMode.DETAIL.value)) {
                /*
                 * R5_87
                 * 残業時間帯.法定内残業枠
                 */
                Integer settlementOrder = lstOTTimezone.get(i).getSettlementOrder();
                BigDecimal inLegalOTFrameNo = lstOTTimezone.get(i).getInLegalOTFrameNo();
                Optional<OvertimeWorkFrameFindDto> legalOtFrame = overTimeLst.stream()
                        .filter(x -> x.getOvertimeWorkFrNo() == inLegalOTFrameNo.intValue()).findFirst();
                if (legalOtFrame.isPresent()) {
                    cells.get("AG" +((startIndex + 1) + i)).setValue(legalOtFrame.get().getOvertimeWorkFrName());
                }
                
                /*
                 * R5_88
                 * 残業時間帯.積残順序
                 */
                cells.get("AH" + ((startIndex + 1) + i)).setValue(getSetlementEnum(settlementOrder));
            }
        }
        
        // 4        タブグ:                打刻時間帯
        
        List<PrioritySettingDto> prioritySets = data.getFlowWorkSetting().getCommonSetting().getStampSet().getPrioritySets();
        Optional<PrioritySettingDto> prioritySetGoingWorkOpt = prioritySets.stream()
                .filter(x -> x.getStampAtr().equals(StampPiorityAtr.GOING_WORK.value)).findFirst();
        Optional<PrioritySettingDto> prioritySetLeaveWorkOpt = prioritySets.stream()
                .filter(x -> x.getStampAtr().equals(StampPiorityAtr.LEAVE_WORK.value)).findFirst();
        Optional<PrioritySettingDto> prioritySetEnteringOpt = prioritySets.stream()
                .filter(x -> x.getStampAtr().equals(StampPiorityAtr.ENTERING.value)).findFirst();
        Optional<PrioritySettingDto> prioritySetExitOpt = prioritySets.stream()
                .filter(x -> x.getStampAtr().equals(StampPiorityAtr.EXIT.value)).findFirst();
        Optional<PrioritySettingDto> prioritySetPCLoginOpt = prioritySets.stream()
                .filter(x -> x.getStampAtr().equals(StampPiorityAtr.PCLOGIN.value)).findFirst();
        Optional<PrioritySettingDto> prioritySetPCLogoutOpt = prioritySets.stream()
                .filter(x -> x.getStampAtr().equals(StampPiorityAtr.PC_LOGOUT.value)).findFirst();
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            if (prioritySetGoingWorkOpt.isPresent()) {
                /*
                 * R5_89
                 * 打刻時間帯.優先設定.出勤
                 */
                Integer priorityAtr = prioritySetGoingWorkOpt.get().getPriorityAtr();
                cells.get("AI" + (startIndex + 1)).setValue(MultiStampTimePiorityAtr.valueOf(priorityAtr).description);
            }
            
            if (prioritySetLeaveWorkOpt.isPresent()) {
                /*
                 * R5_90
                 * 打刻時間帯.優先設定.退勤
                 */
                Integer priorityAtr = prioritySetLeaveWorkOpt.get().getPriorityAtr();
                cells.get("AJ" + (startIndex + 1)).setValue(MultiStampTimePiorityAtr.valueOf(priorityAtr).description);
            }
        }
        
        List<RoundingSetDto> roundingSets = data.getFlowWorkSetting().getCommonSetting().getStampSet().getRoundingTime().getRoundingSets();
        Optional<RoundingSetDto> roundingAttendanceSetOpt = roundingSets.stream().filter(x -> x.getSection() == Superiority.ATTENDANCE.value).findFirst();
        Optional<RoundingSetDto> roundingOfficeSetOpt = roundingSets.stream().filter(x -> x.getSection() == Superiority.OFFICE_WORK.value).findFirst();
        Optional<RoundingSetDto> roundingGoOutSetOpt = roundingSets.stream().filter(x -> x.getSection() == Superiority.GO_OUT.value).findFirst();
        Optional<RoundingSetDto> roundingTurnBackSetOpt = roundingSets.stream().filter(x -> x.getSection() == Superiority.TURN_BACK.value).findFirst();
        
        if (roundingAttendanceSetOpt.isPresent()) {
            /*
             * R5_91
             * 打刻時間帯.打刻丸め.出勤
             */
            Integer roundingTimeUnit = roundingAttendanceSetOpt.get().getRoundingSet().getRoundingTimeUnit();
            cells.get("AK" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(roundingTimeUnit));
            
            /*
             * R5_92
             * 打刻時間帯.打刻丸め.出勤前後設定
             */
            Integer fontRearSection = roundingAttendanceSetOpt.get().getRoundingSet().getFontRearSection();
            cells.get("AL" + (startIndex + 1)).setValue(FontRearSection.valueOf(fontRearSection).description);
        }
        
        if (roundingOfficeSetOpt.isPresent()) {
            /*
             * R5_93
             * 打刻時間帯.打刻丸め.退勤
             */
            Integer roundingTimeUnit = roundingOfficeSetOpt.get().getRoundingSet().getRoundingTimeUnit();
            cells.get("AM" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(roundingTimeUnit));
            
            /*
             * R5_94
             * 打刻時間帯.打刻丸め.退勤前後設定
             */
            Integer fontRearSection = roundingOfficeSetOpt.get().getRoundingSet().getFontRearSection();
            cells.get("AN" + (startIndex + 1)).setValue(FontRearSection.valueOf(fontRearSection).description);
        }
        
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            List<StampReflectTimezoneDto> stampReflectTimezones = data.getFlowWorkSetting().getStampReflectTimezone().getStampReflectTimezones();
            Optional<StampReflectTimezoneDto> lst1stGoWorkOpt = stampReflectTimezones.stream()
                    .filter(x -> (x.getWorkNo() == 1 && x.getClassification().equals(GoLeavingWorkAtr.GO_WORK.value)))
                    .findFirst();
                
                Optional<StampReflectTimezoneDto> lst1stLeavingWorkOpt = stampReflectTimezones.stream()
                    .filter(x -> (x.getWorkNo() == 1 && x.getClassification().equals(GoLeavingWorkAtr.LEAVING_WORK.value)))
                    .findFirst();
                
                if (lst1stGoWorkOpt.isPresent()) {
                    /*
                     * R5_95
                     * 打刻詳細設定.出勤反映時間帯.開始時刻
                     */
                    Integer startTime = lst1stGoWorkOpt.get().getStartTime();
                    cells.get("AO" + (startIndex + 1)).setValue(startTime != null ? getInDayTimeWithFormat(startTime) : "");
                    
                    /*
                     * R5_96
                     * 打刻詳細設定.出勤反映時間帯.終了時刻
                     */
                    Integer endTime = lst1stGoWorkOpt.get().getEndTime();
                    cells.get("AP" + (startIndex + 1)).setValue(endTime != null ? getInDayTimeWithFormat(endTime) : "");
                }
                
                /*
                 * R5_97
                 * 打刻詳細設定.出勤反映時間帯.2勤務目の開始（1勤務目の退勤から分けられる時間）
                 */
                Integer twoTimesWorkReflectBasicTime = data.getFlowWorkSetting().getStampReflectTimezone().getTwoTimesWorkReflectBasicTime();
                cells.get("AQ" + (startIndex + 1)).setValue(getInDayTimeWithFormat(twoTimesWorkReflectBasicTime));
                
                if (lst1stLeavingWorkOpt.isPresent()) {
                    /*
                     * R5_98
                     * 打刻詳細設定.退勤反映時間帯.開始時刻
                     */
                    Integer startTime = lst1stLeavingWorkOpt.get().getStartTime();
                    cells.get("AR" + (startIndex + 1)).setValue(startTime != null ? getInDayTimeWithFormat(startTime) : "");
                    
                    /*
                     * R5_99
                     * 打刻詳細設定.退勤反映時間帯.終了時刻
                     */
                    Integer endTime = lst1stLeavingWorkOpt.get().getEndTime();
                    cells.get("AS" + (startIndex + 1)).setValue(endTime != null ? getInDayTimeWithFormat(endTime) : "");
                }
                
                /*
                 * R5_100
                 * 打刻詳細設定.優先設定.入門
                 */
                if (prioritySetEnteringOpt.isPresent()) {
                    Integer priorityAtr = prioritySetEnteringOpt.get().getPriorityAtr();
                    cells.get("AT" + (startIndex + 1)).setValue(getPrioritySetAtr(priorityAtr));
                }
                
                /*
                 * R5_101
                 * 打刻詳細設定.優先設定.退門
                 */
                if (prioritySetExitOpt.isPresent()) {
                    Integer priorityAtr = prioritySetExitOpt.get().getPriorityAtr();
                    cells.get("AU" + (startIndex + 1)).setValue(getPrioritySetAtr(priorityAtr));
                }
                
                /*
                 * R5_102
                 * 打刻詳細設定.優先設定.PCログオン
                 */
                if (prioritySetPCLoginOpt.isPresent()) {
                    Integer priorityAtr = prioritySetPCLoginOpt.get().getPriorityAtr();
                    cells.get("AV" + (startIndex + 1)).setValue(getPrioritySetAtr(priorityAtr));
                }
                
                /*
                 * R5_103
                 * 打刻詳細設定.優先設定.PCログオフ
                 */
                if (prioritySetPCLogoutOpt.isPresent()) {
                    Integer priorityAtr = prioritySetPCLogoutOpt.get().getPriorityAtr();
                    cells.get("AW" + (startIndex + 1)).setValue(getPrioritySetAtr(priorityAtr));
                }
                
                if (roundingGoOutSetOpt.isPresent()) {
                    /*
                     * R5_104
                     * 打刻詳細設定.打刻丸め.外出
                     */
                    Integer unitGoOut = roundingGoOutSetOpt.get().getRoundingSet().getRoundingTimeUnit();
                    cells.get("AX" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitGoOut));
                    
                    /*
                     * R5_105
                     * 打刻詳細設定.打刻丸め.外出前後設定
                     */
                    Integer fontRearSection = roundingGoOutSetOpt.get().getRoundingSet().getFontRearSection();
                    cells.get("AY" + (startIndex + 1)).setValue(fontRearSection == 0 ? "前にずらす" : "後ろにずらす");
                }
                
                if (roundingTurnBackSetOpt.isPresent()) {
                    /*
                     * R5_106
                     * 打刻詳細設定.打刻丸め.戻り
                     */
                    Integer unitReturnBack = roundingTurnBackSetOpt.get().getRoundingSet().getRoundingTimeUnit();
                    cells.get("AZ" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitReturnBack));
                    
                    /*
                     * R5_107
                     * 打刻詳細設定.打刻丸め.戻り前後設定
                     */
                    Integer fontRearSection = roundingTurnBackSetOpt.get().getRoundingSet().getFontRearSection();
                    cells.get("BA" + (startIndex + 1)).setValue(fontRearSection == 0 ? "前にずらす" : "後ろにずらす");
                }
                
                /*
                 * R5_264
                 * 打刻詳細設定.計算設定.時間丁度の打刻は遅刻とする
                 */
                Integer attendanceMinuteLaterCalculate = data.getFlowWorkSetting().getCommonSetting()
                        .getStampSet().getRoundingTime().getAttendanceMinuteLaterCalculate();
                cells.get("BB" + (startIndex + 1)).setValue(attendanceMinuteLaterCalculate == 0 ? "-" : "○");
                
                /*
                 * R5_265
                 * 打刻詳細設定.計算設定.時間丁度の打刻は早退とする
                 */
                Integer leaveWorkMinuteAgoCalculate = data.getFlowWorkSetting().getCommonSetting()
                        .getStampSet().getRoundingTime().getLeaveWorkMinuteAgoCalculate();
                cells.get("BC" + (startIndex + 1)).setValue(leaveWorkMinuteAgoCalculate == 0 ? "-" : "○");
        }
        
        // 5        タブグ:                休憩時間帯
        
        /*
         * R5_108
         * 休憩時間帯.休憩時間の固定
         */
        boolean fixRestTime = data.getFlowWorkSetting().getHalfDayWorkTimezone().getRestTimezone().isFixRestTime();
        cells.get("BD" + (startIndex + 1)).setValue(getUseAtrByBoolean(fixRestTime));
        
        List<DeductionTimeDto> timezones = data.getFlowWorkSetting().getHalfDayWorkTimezone().getRestTimezone().getFixedRestTimezone().getTimezones();
        
        for (int i = 0; i < timezones.size(); i++) {
            if (fixRestTime) {
                /*
                 * R5_109
                 * 休憩時間帯.休憩時間の固定する.開始時間
                 */
                cells.get("BE" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(timezones.get(i).getStart()));
                
                /*
                 * R5_110
                 * 休憩時間帯.休憩時間の固定する.終了時間
                 */
                cells.get("BF" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(timezones.get(i).getEnd()));
            }
        }
        
        List<FlowRestSettingDto> flowRestSets = data.getFlowWorkSetting().getHalfDayWorkTimezone().getRestTimezone().getFlowRestTimezone().getFlowRestSets();
        
        if (!fixRestTime) {
            for (int i = 0; i < flowRestSets.size(); i++) {
                /*
                 * R5_111
                 * 休憩時間帯.休憩時間の固定しない.経過時間
                 */
                Integer flowPassageTime = flowRestSets.get(i).getFlowPassageTime();
                cells.get("BG" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(flowPassageTime));
                
                /*
                 * R5_112
                 * 休憩時間帯.休憩時間の固定しない.休憩時間
                 */
                Integer flowRestTime = flowRestSets.get(i).getFlowRestTime();
                cells.get("BH" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(flowRestTime));
            }
            
            /*
             * R5_113
             * 休憩時間帯.休憩時間の固定しない.休憩時間
             */
            boolean useHereAfterRestSet = data.getFlowWorkSetting().getHalfDayWorkTimezone().getRestTimezone().getFlowRestTimezone().isUseHereAfterRestSet();
            cells.get("BH" + (startIndex + 7)).setValue(useHereAfterRestSet ? "○" : "-");
            
            FlowRestSettingDto hereAfterRestSet = data.getFlowWorkSetting().getHalfDayWorkTimezone().getRestTimezone().getFlowRestTimezone().getHereAfterRestSet();
            /*
             * R5_114
             * 休憩時間帯.休憩時間の固定しない.経過時間
             */
            Integer flowPassageTime = hereAfterRestSet.getFlowPassageTime();
            cells.get("BG" + (startIndex + 9)).setValue(getInDayTimeWithFormat(flowPassageTime));
            
            /*
             * R5_115
             * 休憩時間帯.休憩時間の固定しない.休憩時間
             */
            Integer flowRestTime = hereAfterRestSet.getFlowRestTime();
            cells.get("BH" + (startIndex + 9)).setValue(getInDayTimeWithFormat(flowRestTime));
        }
        
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            /*
             * R5_116
             * 休憩時間帯.休憩計算設定.休憩中に退勤した場合の休憩時間の計算方法
             */
            Integer calculateMethod = data.getFlowWorkSetting().getRestSetting().getCommonRestSetting().getCalculateMethod();
            cells.get("BI" + (startIndex + 1)).setValue(getCalculatedMethodAtr(calculateMethod));
            
            /*
             * R5_117
             * 休憩時間帯.休憩計算設定.流動勤務の2勤務目設定
             */
            boolean usePluralWorkRestTime = data.getFlowWorkSetting().getRestSetting().getFlowRestSetting().isUsePluralWorkRestTime();
            cells.get("BJ" + (startIndex + 1)).setValue(usePluralWorkRestTime ? "扱う" : "扱わない");
            
            /*
             * R5_118
             * 休憩時間帯.休憩計算設定.丸め
             */
            Integer unitFlowBreakMul = data.getFlowWorkSetting().getRestSetting().getFlowRestSetting().getRoundingBreakMultipleWork().getRoundingTime();
            cells.get("BK" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitFlowBreakMul));
            
            /*
             * R5_119
             * 休憩時間帯.休憩計算設定.端数
             */
            Integer roundingFlowBreakMul = data.getFlowWorkSetting().getRestSetting().getFlowRestSetting().getRoundingBreakMultipleWork().getRounding();
            cells.get("BL" + (startIndex + 1)).setValue(getRoundingEnum(roundingFlowBreakMul));
            
            if (fixRestTime) {
                /*
                 * R5_120
                 * 休憩時間帯.固定休憩設定（休憩時間の固定する）.実績での休憩計算方法
                 */
                Integer calculateMethodFlowRest = data.getFlowWorkSetting().getRestSetting().getFlowRestSetting().getFlowFixedRestSetting().getCalculateMethod();
                cells.get("BM" + (startIndex + 1)).setValue(getCalculateMethodFlowRest(calculateMethodFlowRest));
                
                /*
                 * R5_123
                 * 休憩時間帯.固定休憩設定（休憩時間の固定する）.私用外出を休憩として扱う
                 */
                boolean usePrivateGoOutRest = data.getFlowWorkSetting().getRestSetting().getFlowRestSetting().getFlowFixedRestSetting().getCalculateFromStamp().isUsePrivateGoOutRest();
                cells.get("BN" + (startIndex + 1)).setValue(usePrivateGoOutRest ? "扱う" : "扱わない");
                
                /*
                 * R5_124
                 * 休憩時間帯.固定休憩設定（休憩時間の固定する）.組合外出を休憩として扱う
                 */
                boolean useAssoGoOutRest = data.getFlowWorkSetting().getRestSetting().getFlowRestSetting().getFlowFixedRestSetting().getCalculateFromStamp().isUseAssoGoOutRest();
                cells.get("BO" + (startIndex + 1)).setValue(useAssoGoOutRest ? "扱う" : "扱わない");
            } else {
                /*
                 * R5_126
                 * 休憩時間帯.流動休憩設定（休憩時間の固定しない）.外出を休憩として扱う
                 */
                Boolean useStamp = data.getFlowWorkSetting().getRestSetting().getFlowRestSetting().getFlowRestSetting().getUseStamp();
                if (useStamp != null) {
                    cells.get("BP" + (startIndex + 1)).setValue(useStamp ? "○" : "-");
                }
                
                /*
                 * R5_127
                 * 休憩時間帯.流動休憩設定（休憩時間の固定しない）.外出の計上方法
                 */
                Integer useStampCalcMethod = data.getFlowWorkSetting().getRestSetting().getFlowRestSetting().getFlowRestSetting().getUseStampCalcMethod();
                cells.get("BQ" + (startIndex + 1)).setValue(useStampCalcMethod == 0 ? "休憩として計上する" : "外出として計上する");
                
                /*
                 * R5_128
                 * 休憩時間帯.流動休憩設定（休憩時間の固定しない）.優先設定
                 */
                Integer timeManagerSetAtr = data.getFlowWorkSetting().getRestSetting().getFlowRestSetting().getFlowRestSetting().getTimeManagerSetAtr();
                cells.get("BR" + (startIndex + 1)).setValue(timeManagerSetAtr == 0 ? "休憩中の外出は外出を優先する" : "休憩中の外出は休憩を優先し、外出としては計上されない");
            }
        }
        
        // 6        タブグ:                休出時間帯
        
        List<FlWorkHdTimeZoneDto> lstWorkTimezone = data.getFlowWorkSetting().getOffdayWorkTimezone().getLstWorkTimezone();
        Collections.sort(lstWorkTimezone, new Comparator<FlWorkHdTimeZoneDto>() {
            public int compare(FlWorkHdTimeZoneDto o1, FlWorkHdTimeZoneDto o2) {
                return o1.getWorktimeNo().compareTo(o2.getWorktimeNo());
            };
        });
        
        for (int i = 0; i < lstWorkTimezone.size(); i++) {
            final int index = i;
            
            /*
             * R5_129
             * 休出時間帯.経過時間
             */
            Integer elapsedTime = lstWorkTimezone.get(i).getFlowTimeSetting().getElapsedTime();
            cells.get("BS" + (startIndex + lstWorkTimezone.get(i).getWorktimeNo())).setValue(getInDayTimeWithFormat(elapsedTime));
            
            Optional<WorkdayoffFrameFindDto> workDayOffFrameInLegalBreak = otFrameFind.stream()
                    .filter(x -> x.getWorkdayoffFrNo() == lstWorkTimezone.get(index).getInLegalBreakFrameNo().intValue())
                    .findFirst();
            Optional<WorkdayoffFrameFindDto> workDayOffFrameOutLegalBreak = otFrameFind.stream()
                    .filter(x -> x.getWorkdayoffFrNo() == lstWorkTimezone.get(index).getOutLegalBreakFrameNo().intValue())
                    .findFirst();
            Optional<WorkdayoffFrameFindDto> workDayOffFrameoutLegalPubHD = otFrameFind.stream()
                    .filter(x -> x.getWorkdayoffFrNo() == lstWorkTimezone.get(index).getOutLegalPubHolFrameNo().intValue())
                    .findFirst();
            
            /*
             * R5_130
             * 休出時間帯.法定内休出枠
             */
            if (workDayOffFrameInLegalBreak.isPresent()) {
                cells.get("BT" + (startIndex + lstWorkTimezone.get(i).getWorktimeNo()))
                .setValue(workDayOffFrameInLegalBreak.get().getWorkdayoffFrName());
            }
            
            /*
             * R5_131
             * 休出時間帯.法定外休出枠
             */
            if (workDayOffFrameOutLegalBreak.isPresent()) {
                cells.get("BU" + (startIndex + lstWorkTimezone.get(i).getWorktimeNo()))
                .setValue(workDayOffFrameOutLegalBreak.get().getWorkdayoffFrName());
            }
            
            /*
             * R5_132
             * 休出時間帯.法定外休出枠（祝日）
             */
            if (workDayOffFrameoutLegalPubHD.isPresent()) {
                cells.get("BV" + (startIndex + lstWorkTimezone.get(i).getWorktimeNo()))
                .setValue(workDayOffFrameoutLegalPubHD.get().getWorkdayoffFrName());
            }
            
            /*
             * R5_133
             * 休出時間帯.丸め
             */
            Integer unitFlowSet = lstWorkTimezone.get(i).getFlowTimeSetting().getRounding().getRoundingTime();
            cells.get("BW" + (startIndex + lstWorkTimezone.get(i).getWorktimeNo())).setValue(getRoundingTimeUnitEnum(unitFlowSet));
            
            /*
             * R5_134
             * 休出時間帯.端数
             */
            Integer roundingFlowSet = lstWorkTimezone.get(i).getFlowTimeSetting().getRounding().getRounding();
            cells.get("BX" + (startIndex + lstWorkTimezone.get(i).getWorktimeNo())).setValue(getRoundingEnum(roundingFlowSet));
        }
        
        // 7        タブグ:                休出休憩
        
        boolean fixRestTimeOffDay = data.getFlowWorkSetting().getOffdayWorkTimezone().getRestTimeZone().isFixRestTime();
        
        /*
         * R5_135
         * 休出休憩.休憩時間の固定
         */
        cells.get("BY" + (startIndex + 1)).setValue(getUseAtrByBoolean(fixRestTimeOffDay));
        
        if (fixRestTimeOffDay) {
            List<DeductionTimeDto> timezonesRest = data.getFlowWorkSetting().getOffdayWorkTimezone().getRestTimeZone().getFixedRestTimezone().getTimezones();
            
            for (int i = 0; i < timezonesRest.size(); i++) {
                /*
                 * R5_136
                 * 休出休憩.固定する.開始時間
                 */
                cells.get("BZ" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(timezonesRest.get(i).getStart()));
                
                /*
                 * R5_137
                 * 休出休憩.固定する.終了時間
                 */
                cells.get("CA" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(timezonesRest.get(i).getEnd()));
            }
        } else {
            List<FlowRestSettingDto> flowRestSetsOffDay = data.getFlowWorkSetting().getOffdayWorkTimezone().getRestTimeZone().getFlowRestTimezone().getFlowRestSets();
            for (int i = 0; i < flowRestSetsOffDay.size(); i++) {
                /*
                 * R5_138
                 * 休出休憩.固定しない.経過時間
                 */
                Integer flowPassageTime = flowRestSetsOffDay.get(i).getFlowPassageTime();
                cells.get("CB" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(flowPassageTime));
                
                /*
                 * R5_139
                 * 休出休憩.固定しない.休憩時間
                 */
                Integer flowRestTime = flowRestSetsOffDay.get(i).getFlowRestTime();
                cells.get("CC" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(flowRestTime));
            }
            
            /*
             * R5_140
             * 休出休憩.固定しない.以降は下記の時間で繰り返す
             */
            boolean useHereAfterRestSet = data.getFlowWorkSetting().getOffdayWorkTimezone().getRestTimeZone().getFlowRestTimezone().isUseHereAfterRestSet();
            cells.get("CC" + (startIndex + 7)).setValue(useHereAfterRestSet ? "○" : "-");
            
            /*
             * R5_141
             * 休出休憩.固定しない.経過時間
             */
            Integer flowPassageTime = data.getFlowWorkSetting().getOffdayWorkTimezone().getRestTimeZone().getFlowRestTimezone().getHereAfterRestSet().getFlowPassageTime();
            cells.get("CB" + (startIndex + 9)).setValue(getInDayTimeWithFormat(flowPassageTime));
            
            /*
             * R5_142
             * 休出休憩.固定しない.休憩時間
             */
            Integer flowRestTime = data.getFlowWorkSetting().getOffdayWorkTimezone().getRestTimeZone().getFlowRestTimezone().getHereAfterRestSet().getFlowRestTime();
            cells.get("CC" + (startIndex + 9)).setValue(getInDayTimeWithFormat(flowRestTime));
        }
        
        // 8        タブグ:                外出
        
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            /*
             * R5_143
             * 外出.外出丸め設定.同じ枠内での丸め設定
             */
            Integer setSameFrameRounding = data.getFlowWorkSetting().getCommonSetting().getGoOutSet().getTotalRoundingSet().getSetSameFrameRounding();
            cells.get("CD" + (startIndex + 1)).setValue(getFrameRoundingAtr(setSameFrameRounding));
            
            /*
             * R5_144
             * 外出.外出丸め設定.枠を跨る場合の丸め設定
             */
            Integer frameStraddRoundingSet = data.getFlowWorkSetting().getCommonSetting().getGoOutSet().getTotalRoundingSet().getFrameStraddRoundingSet();
            cells.get("CE" + (startIndex + 1)).setValue(getFrameRoundingAtr(frameStraddRoundingSet));
            
            /*
             * R5_145
             * 外出.私用・組合外出時間.就業時間帯
             */
            Integer roundingMethodWorkPrivateAppro = data.getFlowWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getWorkTimezone().getPrivateUnionGoOut()
                    .getApproTimeRoundingSetting().getRoundingMethod();
            cells.get("CF" + (startIndex + 1)).setValue(getApproTimeRoundingAtr(roundingMethodWorkPrivateAppro));
            
            /*
             * R5_146
             * 外出.私用・組合外出時間.丸め設定
             */
            Integer unitWorkPrivateAppro = data.getFlowWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getWorkTimezone().getPrivateUnionGoOut()
                    .getApproTimeRoundingSetting().getRoundingSetting().getRoundingTime();
            cells.get("CG" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitWorkPrivateAppro));
            
            /*
             * R5_147
             * 外出.私用・組合外出時間.丸め設定端数
             */
            Integer roundingWorkPrivateAppro = data.getFlowWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getWorkTimezone().getPrivateUnionGoOut()
                    .getApproTimeRoundingSetting().getRoundingSetting().getRounding();
            cells.get("CH" + (startIndex + 1)).setValue(getRoundingEnum(roundingWorkPrivateAppro));
            
            /*
             * R5_148
             * 外出.私用・組合外出時間.残業時間帯
             */
            Integer roundingMethodOtPrivate = data.getFlowWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getOttimezone().getPrivateUnionGoOut()
                    .getApproTimeRoundingSetting().getRoundingMethod();
            cells.get("CI" + (startIndex + 1)).setValue(getApproTimeRoundingAtr(roundingMethodOtPrivate));
            
            /*
             * R5_149
             * 外出.私用・組合外出時間.丸め設定
             */
            Integer unitOtPrivateAppro = data.getFlowWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getOttimezone().getPrivateUnionGoOut()
                    .getApproTimeRoundingSetting().getRoundingSetting().getRoundingTime();
            cells.get("CJ" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitOtPrivateAppro));
            
            /*
             * R5_150
             * 外出.私用・組合外出時間.丸め設定端数
             */
            Integer roundingOtPrivateAppro = data.getFlowWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getOttimezone().getPrivateUnionGoOut()
                    .getApproTimeRoundingSetting().getRoundingSetting().getRounding();
            cells.get("CK" + (startIndex + 1)).setValue(getRoundingEnum(roundingOtPrivateAppro));
            
            /*
             * R5_151
             * 外出.私用・組合外出時間.休出時間帯
             */
            Integer roundingMethodHdPrivate = data.getFlowWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getPubHolWorkTimezone().getPrivateUnionGoOut()
                    .getApproTimeRoundingSetting().getRoundingMethod();
            cells.get("CL" + (startIndex + 1)).setValue(getApproTimeRoundingAtr(roundingMethodHdPrivate));
            
            /*
             * R5_152
             * 外出.私用・組合外出時間.丸め設定
             */
            Integer unitHdPrivate = data.getFlowWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getPubHolWorkTimezone().getPrivateUnionGoOut()
                    .getApproTimeRoundingSetting().getRoundingSetting().getRoundingTime();
            cells.get("CM" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitHdPrivate));
            
            /*
             * R5_153
             * 外出.私用・組合外出時間.丸め設定端数
             */
            Integer roundingHdPrivate = data.getFlowWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getPubHolWorkTimezone().getPrivateUnionGoOut()
                    .getApproTimeRoundingSetting().getRoundingSetting().getRounding();
            cells.get("CN" + (startIndex + 1)).setValue(getRoundingEnum(roundingHdPrivate));
            
            /*
             * R5_154
             * 外出.私用・組合外出控除時間.就業時間帯
             */
            Integer roundingMethodWorkPrivateDeduct = data.getFlowWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getWorkTimezone().getPrivateUnionGoOut()
                    .getDeductTimeRoundingSetting().getRoundingMethod();
            cells.get("CO" + (startIndex + 1)).setValue(getApproTimeRoundingAtr(roundingMethodWorkPrivateDeduct));
            
            /*
             * R5_155
             * 外出.私用・組合外出控除時間.丸め設定
             */
            Integer unitWorkPrivateDeduct = data.getFlowWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getWorkTimezone().getPrivateUnionGoOut()
                    .getDeductTimeRoundingSetting().getRoundingSetting().getRoundingTime();
            cells.get("CP" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitWorkPrivateDeduct));
            
            /*
             * R5_156
             * 外出.私用・組合外出控除時間.丸め設定端数
             */
            Integer roundingWorkPrivateDeduct = data.getFlowWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getWorkTimezone().getPrivateUnionGoOut()
                    .getDeductTimeRoundingSetting().getRoundingSetting().getRounding();
            cells.get("CQ" + (startIndex + 1)).setValue(getRoundingEnum(roundingWorkPrivateDeduct));
            
            /*
             * R5_157
             * 外出.私用・組合外出控除時間.残業時間帯
             */
            Integer roundingMethodOtPrivateDeduct = data.getFlowWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getOttimezone().getPrivateUnionGoOut()
                    .getDeductTimeRoundingSetting().getRoundingMethod();
            cells.get("CR" + (startIndex + 1)).setValue(getApproTimeRoundingAtr(roundingMethodOtPrivateDeduct));
            
            /*
             * R5_158
             * 外出.私用・組合外出控除時間.丸め設定
             */
            Integer unitOtPrivateDeduct = data.getFlowWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getOttimezone().getPrivateUnionGoOut()
                    .getDeductTimeRoundingSetting().getRoundingSetting().getRoundingTime();
            cells.get("CS" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitOtPrivateDeduct));
            
            /*
             * R5_159
             * 外出.私用・組合外出控除時間.丸め設定端数
             */
            Integer roundingOtPrivateDeduct = data.getFlowWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getOttimezone().getPrivateUnionGoOut()
                    .getDeductTimeRoundingSetting().getRoundingSetting().getRounding();
            cells.get("CT" + (startIndex + 1)).setValue(getRoundingEnum(roundingOtPrivateDeduct));
            
            /*
             * R5_160
             * 外出.私用・組合外出控除時間.休出時間帯
             */
            Integer roundingMethodHdPriateDeduct = data.getFlowWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getPubHolWorkTimezone().getPrivateUnionGoOut()
                    .getDeductTimeRoundingSetting().getRoundingMethod();
            cells.get("CU" + (startIndex + 1)).setValue(getApproTimeRoundingAtr(roundingMethodHdPriateDeduct));
            
            /*
             * R5_161
             * 外出.私用・組合外出控除時間.丸め設定
             */
            Integer unitHdPrivateDeduct = data.getFlowWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getPubHolWorkTimezone().getPrivateUnionGoOut()
                    .getDeductTimeRoundingSetting().getRoundingSetting().getRoundingTime();
            cells.get("CV" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitHdPrivateDeduct));
            
            /*
             * R5_162
             * 外出.私用・組合外出控除時間.丸め設定端数
             */
            Integer roundingHdPrivateDeduct = data.getFlowWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getPubHolWorkTimezone().getPrivateUnionGoOut()
                    .getDeductTimeRoundingSetting().getRoundingSetting().getRounding();
            cells.get("CW" + (startIndex + 1)).setValue(getRoundingEnum(roundingHdPrivateDeduct));
            
            /*
             * R5_163
             * 外出.公用・有償外出時間.就業時間帯
             */
            Integer roundingMethodWorkOfficalAppro = data.getFlowWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getWorkTimezone().getOfficalUseCompenGoOut()
                    .getApproTimeRoundingSetting().getRoundingMethod();
            cells.get("CX" + (startIndex + 1)).setValue(getApproTimeRoundingAtr(roundingMethodWorkOfficalAppro));
            
            /*
             * R5_164
             * 外出.公用・有償外出時間.丸め設定
             */
            Integer unitWorkOfficalAppro = data.getFlowWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getWorkTimezone().getOfficalUseCompenGoOut()
                    .getApproTimeRoundingSetting().getRoundingSetting().getRoundingTime();
            cells.get("CY" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitWorkOfficalAppro));
            
            /*
             * R5_165
             * 外出.公用・有償外出時間.丸め設定端数
             */
            Integer roundingWorkOfficalAppro = data.getFlowWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getWorkTimezone().getOfficalUseCompenGoOut()
                    .getApproTimeRoundingSetting().getRoundingSetting().getRounding();
            cells.get("CZ" + (startIndex + 1)).setValue(getRoundingEnum(roundingWorkOfficalAppro));
            
            /*
             * R5_166
             * 外出.公用・有償外出時間.残業時間帯
             */
            Integer roundingMethodOtOfficalAppro = data.getFlowWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getOttimezone().getOfficalUseCompenGoOut()
                    .getApproTimeRoundingSetting().getRoundingMethod();
            cells.get("DA" + (startIndex + 1)).setValue(getApproTimeRoundingAtr(roundingMethodOtOfficalAppro));
            
            /*
             * R5_167
             * 外出.公用・有償外出時間.丸め設定
             */
            Integer unitOtOfficalAppro = data.getFlowWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getOttimezone().getOfficalUseCompenGoOut()
                    .getApproTimeRoundingSetting().getRoundingSetting().getRoundingTime();
            cells.get("DB" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitOtOfficalAppro));
            
            /*
             * R5_168
             * 外出.公用・有償外出時間.丸め設定端数
             */
            Integer roundingOtOfficalAppro = data.getFlowWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getOttimezone().getOfficalUseCompenGoOut()
                    .getApproTimeRoundingSetting().getRoundingSetting().getRounding();
            cells.get("DC" + (startIndex + 1)).setValue(getRoundingEnum(roundingOtOfficalAppro));
            
            /*
             * R5_169
             * 外出.公用・有償外出時間.休出時間帯
             */
            Integer roundingMethodHdOfficalAppro = data.getFlowWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getPubHolWorkTimezone().getOfficalUseCompenGoOut()
                    .getApproTimeRoundingSetting().getRoundingMethod();
            cells.get("DD" + (startIndex + 1)).setValue(getApproTimeRoundingAtr(roundingMethodHdOfficalAppro));
            
            /*
             * R5_170
             * 外出.公用・有償外出時間.丸め設定
             */
            Integer unitHdOfficalAppro = data.getFlowWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getPubHolWorkTimezone().getOfficalUseCompenGoOut()
                    .getApproTimeRoundingSetting().getRoundingSetting().getRoundingTime();
            cells.get("DE" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitHdOfficalAppro));
            
            /*
             * R5_171
             * 外出.公用・有償外出時間.丸め設定端数
             */
            Integer roundingHdOfficalAppro = data.getFlowWorkSetting().getCommonSetting().getGoOutSet()
                    .getDiffTimezoneSetting().getPubHolWorkTimezone().getOfficalUseCompenGoOut()
                    .getApproTimeRoundingSetting().getRoundingSetting().getRounding();
            cells.get("DF" + (startIndex + 1)).setValue(getRoundingEnum(roundingHdOfficalAppro));
        }
        
        // 9        タブグ:                遅刻早退
        
        List<OtherEmTimezoneLateEarlySetDto> otherClassSets = data.getFlowWorkSetting().getCommonSetting().getLateEarlySet().getOtherClassSets();
        Optional<OtherEmTimezoneLateEarlySetDto> otherLate = otherClassSets.stream()
                .filter(x -> x.getLateEarlyAtr().equals(LateEarlyAtr.LATE.value)).findFirst();
        Optional<OtherEmTimezoneLateEarlySetDto> otherEarly = otherClassSets.stream()
                .filter(x -> x.getLateEarlyAtr().equals(LateEarlyAtr.EARLY.value)).findFirst();
        
        if (otherLate.isPresent()) {
            /*
             * R5_172
             * 遅刻早退.遅刻早退時間丸め.遅刻丸め
             */
            Integer unitRecordLate = otherLate.get().getRecordTimeRoundingSet().getRoundingTime();
            cells.get("DG" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitRecordLate));
            
            /*
             * R5_173
             * 遅刻早退.遅刻早退時間丸め.遅刻端数
             */
            Integer roundingRecordlate = otherLate.get().getRecordTimeRoundingSet().getRounding();
            cells.get("DH" + (startIndex + 1)).setValue(getRoundingEnum(roundingRecordlate));
        }
        
        if (otherEarly.isPresent()) {
            /*
             * R5_174
             * 遅刻早退.遅刻早退時間丸め.早退丸め
             */
            Integer unitRecordEarly = otherEarly.get().getRecordTimeRoundingSet().getRoundingTime();
            cells.get("DI" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitRecordEarly));
            
            /*
             * R5_175
             * 遅刻早退.遅刻早退時間丸め.早退端数
             */
            Integer roundingRecordEarly = otherEarly.get().getRecordTimeRoundingSet().getRounding();
            cells.get("DJ" + (startIndex + 1)).setValue(getRoundingEnum(roundingRecordEarly));
        }
        
        if (otherLate.isPresent()) {
            /*
             * R5_176
             * 遅刻早退.遅刻早退控除時間丸め.遅刻丸め
             */
            Integer unitDelLate = otherLate.get().getDelTimeRoundingSet().getRoundingTime();
            cells.get("DK" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitDelLate));
            
            /*
             * R5_177
             * 遅刻早退.遅刻早退控除時間丸め.遅刻端数
             */
            Integer roundingDelLate = otherLate.get().getDelTimeRoundingSet().getRounding();
            cells.get("DL" + (startIndex + 1)).setValue(getRoundingEnum(roundingDelLate));
        }
        
        if (otherEarly.isPresent()) {
            /*
             * R5_178
             * 遅刻早退.遅刻早退控除時間丸め.早退丸め
             */
            Integer unitDelEarly = otherEarly.get().getDelTimeRoundingSet().getRoundingTime();
            cells.get("DM" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitDelEarly));
            
            /*
             * R5_179
             * 遅刻早退.遅刻早退控除時間丸め.早退端数
             */
            Integer roundingDelEarly = otherEarly.get().getDelTimeRoundingSet().getRounding();
            cells.get("DN" + (startIndex + 1)).setValue(getRoundingEnum(roundingDelEarly));
        }
        
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            if (otherLate.isPresent()) {
                /*
                 * R5_182
                 * 遅刻早退詳細設定.猶予時間.遅刻猶予時間
                 */
                Integer graceTime = otherLate.get().getGraceTimeSet().getGraceTime();
                cells.get("DO" + (startIndex + 1)).setValue(getInDayTimeWithFormat(graceTime));
                
                /*
                 * R5_183
                 * 遅刻早退詳細設定.猶予時間.遅刻猶予時間を就業時間に含める
                 */
                boolean includeWorkHour = otherLate.get().getGraceTimeSet().isIncludeWorkingHour();
                cells.get("DP" + (startIndex + 1)).setValue(includeWorkHour ? "○" : "-");
            }
            
            if (otherEarly.isPresent()) {
                /*
                 * R5_184
                 * 遅刻早退詳細設定.猶予時間.遅刻猶予時間
                 */
                Integer graceTime = otherEarly.get().getGraceTimeSet().getGraceTime();
                cells.get("DQ" + (startIndex + 1)).setValue(getInDayTimeWithFormat(graceTime));
                
                /*
                 * R5_185
                 * 遅刻早退詳細設定.猶予時間.遅刻猶予時間を就業時間に含める
                 */
                boolean includeWorkHour = otherEarly.get().getGraceTimeSet().isIncludeWorkingHour();
                cells.get("DR" + (startIndex + 1)).setValue(includeWorkHour ? "○" : "-");
            }
        }
        
        // 10       タブグ:                加給
        
        /*
         * R5_186
         * 加給.コード
         */
        String raisingSalarySetCode = data.getFlowWorkSetting().getCommonSetting().getRaisingSalarySet();
        cells.get("DS" + (startIndex + 1)).setValue(raisingSalarySetCode != null ? raisingSalarySetCode : "");
        
        /*
         * R5_187
         * 名称
         */
        if (raisingSalarySetCode != null) {
            Optional<BonusPaySetting> bonusPaySettingOpt = this.bpSettingRepository
                    .getBonusPaySetting(AppContexts.user().companyId(), new BonusPaySettingCode(raisingSalarySetCode));
            if (bonusPaySettingOpt.isPresent()) {
                String raisingSalaryName = bonusPaySettingOpt.get().getName().v();
                cells.get("DT" + (startIndex + 1)).setValue(raisingSalaryName);
            }
        }
        
        // 11       タブグ:                代休
        
        List<WorkTimezoneOtherSubHolTimeSetDto> subHolTimeSet = data.getFlowWorkSetting().getCommonSetting().getSubHolTimeSet();
        Optional<WorkTimezoneOtherSubHolTimeSetDto> subHolTimeWorkDayOffSet = subHolTimeSet.stream()
                .filter(x -> x.getOriginAtr().equals(CompensatoryOccurrenceDivision.WorkDayOffTime.value)).findFirst();
        Optional<WorkTimezoneOtherSubHolTimeSetDto> subHolTimeOverTimeOffSet = subHolTimeSet.stream()
                .filter(x -> x.getOriginAtr().equals(CompensatoryOccurrenceDivision.FromOverTime.value)).findFirst();
        
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            if (subHolTimeWorkDayOffSet.isPresent()) {
                /*
                 * R5_41
                 * 代休.代休発生に必要な時間.休日出勤
                 */
                boolean useDivision = subHolTimeWorkDayOffSet.get().getSubHolTimeSet().isUseDivision();
                cells.get("DU" + (startIndex + 1)).setValue(useDivision ? "○" : "-");
                
                /*
                 * R5_189
                 * 代休.代休発生に必要な時間.時間
                 */
                Integer subHolTransferSetAtr = subHolTimeWorkDayOffSet.get().getSubHolTimeSet().getSubHolTransferSetAtr();
                cells.get("DV" + (startIndex + 1)).setValue(subHolTransferSetAtr == 0 ? "指定時間" : "一定時間");
            }
        }
        
        if (subHolTimeWorkDayOffSet.isPresent()) {
            /*
             * R5_190
             * 代休.代休発生に必要な時間.１日
             */
            Integer oneDayTime = subHolTimeWorkDayOffSet.get().getSubHolTimeSet().getDesignatedTime().getOneDayTime();
            cells.get("DW" + (startIndex + 1)).setValue(getInDayTimeWithFormat(oneDayTime));
            
            /*
             * R5_191
             * 代休.代休発生に必要な時間.半日
             */
            Integer halfDayTime = subHolTimeWorkDayOffSet.get().getSubHolTimeSet().getDesignatedTime().getHalfDayTime();
            cells.get("DX" + (startIndex + 1)).setValue(getInDayTimeWithFormat(halfDayTime));
            
            if (displayMode.equals(DisplayMode.DETAIL.value)) {
                /*
                 * R5_192
                 * 代休.代休発生に必要な時間.一定時間
                 */
                Integer certainTime = subHolTimeWorkDayOffSet.get().getSubHolTimeSet().getCertainTime();
                cells.get("DY" + (startIndex + 1)).setValue(getInDayTimeWithFormat(certainTime));
            }
        }
        
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            if (subHolTimeOverTimeOffSet.isPresent()) {
                /*
                 * R5_42
                 * 代休.代休発生に必要な時間.残業
                 */
                boolean useDivision = subHolTimeWorkDayOffSet.get().getSubHolTimeSet().isUseDivision();
                cells.get("DZ" + (startIndex + 1)).setValue(useDivision ? "○" : "-");
                
                /*
                 * R5_194
                 * 代休.代休発生に必要な時間.時間
                 */
                Integer subHolTransferSetAtr = subHolTimeOverTimeOffSet.get().getSubHolTimeSet().getSubHolTransferSetAtr();
                cells.get("EA" + (startIndex + 1)).setValue(subHolTransferSetAtr == 0 ? "指定時間" : "一定時間");
                
                /*
                 * R5_195
                 * 代休.代休発生に必要な時間.１日
                 */
                Integer oneDayTime = subHolTimeOverTimeOffSet.get().getSubHolTimeSet().getDesignatedTime().getOneDayTime();
                cells.get("EB" + (startIndex + 1)).setValue(getInDayTimeWithFormat(oneDayTime));
                
                /*
                 * R5_196
                 * 代休.代休発生に必要な時間.半日
                 */
                Integer halfDayTime = subHolTimeOverTimeOffSet.get().getSubHolTimeSet().getDesignatedTime().getHalfDayTime();
                cells.get("EC" + (startIndex + 1)).setValue(getInDayTimeWithFormat(halfDayTime));
                
                /*
                 * R5_197
                 * 代休.代休発生に必要な時間.一定時間
                 */
                Integer certainTime = subHolTimeOverTimeOffSet.get().getSubHolTimeSet().getCertainTime();
                cells.get("ED" + (startIndex + 1)).setValue(getInDayTimeWithFormat(certainTime));
            }
        }
        
        // 12       タブグ:                深夜
        
        /*
         * R5_198
         * 深夜残業.深夜時間丸め
         */
        Integer unitLateNight = data.getFlowWorkSetting().getCommonSetting().getLateNightTimeSet().getRoundingSetting().getRoundingTime();
        cells.get("EE" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitLateNight));
        
        /*
         * R5_199
         * 深夜残業.深夜時間端数
         */
        Integer roundingLateNight = data.getFlowWorkSetting().getCommonSetting().getLateNightTimeSet().getRoundingSetting().getRounding();
        cells.get("EF" + (startIndex + 1)).setValue(getRoundingEnum(roundingLateNight));
        
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            // 13       タブグ:                臨時
            
            /*
             * R5_200
             * 臨時.臨時丸め
             */
            Integer unitExtrao = data.getFlowWorkSetting().getCommonSetting().getExtraordTimeSet().getTimeRoundingSet().getRoundingTime();
            cells.get("EG" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitExtrao));
            
            /*
             * R5_201
             * 臨時.臨時端数
             */
            Integer roundingExtrao = data.getFlowWorkSetting().getCommonSetting().getExtraordTimeSet().getTimeRoundingSet().getRounding();
            cells.get("EH" + (startIndex + 1)).setValue(getRoundingEnum(roundingExtrao));
            
            // 14       タブグ:                育児
            
            /*
             * R5_202
             * 育児.育児時間帯に勤務した場合の扱い
             */
            boolean childCareWorkUse = data.getFlowWorkSetting().getCommonSetting().getShortTimeWorkSet().isChildCareWorkUse();
            cells.get("EI" + (startIndex + 1)).setValue(childCareWorkUse ? "育児時間を減算する" : "育児時間を減算しない");
            
            /*
             * R5_203
             * 育児.介護時間帯に勤務した場合の扱い
             */
            boolean nursTimezoneWorkUse = data.getFlowWorkSetting().getCommonSetting().getShortTimeWorkSet().isNursTimezoneWorkUse();
            cells.get("EJ" + (startIndex + 1)).setValue(nursTimezoneWorkUse ? "介護時間を減算する" : "介護時間を減算しない");
            
            // 15       タブグ:                医療
            
            List<WorkTimezoneMedicalSetDto> medicalSet = data.getFlowWorkSetting().getCommonSetting().getMedicalSet();
            Optional<WorkTimezoneMedicalSetDto> medicalSetDay = medicalSet.stream()
                    .filter(x -> x.getWorkSystemAtr().equals(WorkSystemAtr.DAY_SHIFT.value)).findFirst();
            Optional<WorkTimezoneMedicalSetDto> medicalSetNight = medicalSet.stream()
                    .filter(x -> x.getWorkSystemAtr().equals(WorkSystemAtr.NIGHT_SHIFT.value)).findFirst();
            /*
             * R5_204
             * 医療.日勤申し送り時間
             */
            if (medicalSetDay.isPresent()) {
                Integer applicationTime = medicalSetDay.get().getApplicationTime();
                cells.get("EK" + (startIndex + 1)).setValue(getInDayTimeWithFormat(applicationTime));
            }
            
            /*
             * R5_205
             * 医療.夜勤申し送り時間
             */
            if (medicalSetNight.isPresent()) {
                Integer applicationTime = medicalSetNight.get().getApplicationTime();
                cells.get("EL" + (startIndex + 1)).setValue(getInDayTimeWithFormat(applicationTime));
            }
            
            if (medicalSetDay.isPresent()) {
                /*
                 * R5_206
                 * 医療.日勤勤務時間.丸め
                 */
                Integer unitDay = medicalSetDay.get().getRoundingSet().getRoundingTime();
                cells.get("EM" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitDay));
                
                /*
                 * R5_207
                 * 医療.日勤勤務時間.端数
                 */
                Integer roundingDay = medicalSetDay.get().getRoundingSet().getRounding();
                cells.get("EN" + (startIndex + 1)).setValue(getRoundingEnum(roundingDay));
            }
            
            if (medicalSetNight.isPresent()) {
                /*
                 * R5_208
                 * 医療.夜勤勤務時間.丸め
                 */
                Integer unitNight = medicalSetNight.get().getRoundingSet().getRoundingTime();
                cells.get("EO" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitNight));
                
                /*
                 * R5_209
                 * 医療.夜勤勤務時間.端数
                 */
                Integer roundingNight = medicalSetNight.get().getRoundingSet().getRounding();
                cells.get("EP" + (startIndex + 1)).setValue(getRoundingEnum(roundingNight));
            }
            
            // 16       タブグ:                0時跨ぎ
            
            /*
             * R5_210
             * ０時跨ぎ.0時跨ぎ計算
             */
            if (displayMode.equals(DisplayMode.DETAIL.value)) {
                boolean zeroHStraddCalculateSet = data.getFlowWorkSetting().getCommonSetting().isZeroHStraddCalculateSet();
                cells.get("EQ" + (startIndex + 1)).setValue(getUseAtrByBoolean(zeroHStraddCalculateSet));
            }
        }
        
        // 17       タブグ:                その地
        
        boolean ootsuka = AppContexts.optionLicense().customize().ootsuka();
        if (ootsuka) {
            /*
             * R5_211
             * その他.勤務種類が休暇の場合に就業時間を計算するか
             */
            Integer isCalculate = data.getFlowWorkSetting().getCommonSetting().getHolidayCalculation().getIsCalculate();
            cells.get("ER" + (startIndex + 1)).setValue(getUseAtrByInteger(isCalculate));
        } else {
        	cells.deleteColumn(147);
        }
    }
    
    /**
     * 勤務形態 フレックス
     * @param data
     * @param cell
     * @param startIndex
     */
    public void insertDataOneLineFlex(WorkTimeSettingInfoDto data, Cells cells, int startIndex) {
        Integer displayMode = data.getDisplayMode().displayMode;
        
        // 1        タブグ:                所定
        
        this.printDataPrescribed(data, cells, startIndex, FLEX);
        
        // 2        タブグ:                勤務時間帯
        
        boolean workingTimes = data.getFlexWorkSetting().getUseHalfDayShift().isWorkingTimes();
        
        /*
         * R6_260
         * フレキシブルタイム.半日勤務を設定する
         */
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            cells.get("AA" + (startIndex + 1)).setValue(getUseAtrByBoolean(workingTimes));
        }
        
        List<FlexHalfDayWorkTimeDto> lstHalfDayWorkTimezone = data.getFlexWorkSetting().getLstHalfDayWorkTimezone();
        Optional<FlexHalfDayWorkTimeDto> lstHalfDayWorkTimezoneOneDay = lstHalfDayWorkTimezone.stream()
                .filter(x -> x.getAmpmAtr().equals(AmPmAtr.ONE_DAY.value)).findFirst();
        Optional<FlexHalfDayWorkTimeDto> lstHalfDayWorkTimezoneMorning = lstHalfDayWorkTimezone.stream()
                .filter(x -> x.getAmpmAtr().equals(AmPmAtr.AM.value)).findFirst();
        Optional<FlexHalfDayWorkTimeDto> lstHalfDayWorkTimezoneAfternoon = lstHalfDayWorkTimezone.stream()
                .filter(x -> x.getAmpmAtr().equals(AmPmAtr.PM.value)).findFirst();
        
        if (lstHalfDayWorkTimezoneOneDay.isPresent()) {
            List<EmTimeZoneSetDto> lstWorkingTimezone = lstHalfDayWorkTimezoneOneDay.get().getWorkTimezone().getLstWorkingTimezone();
            Collections.sort(lstWorkingTimezone, new Comparator<EmTimeZoneSetDto>() {
                public int compare(EmTimeZoneSetDto o1, EmTimeZoneSetDto o2) {
                    return o1.getEmploymentTimeFrameNo().compareTo(o2.getEmploymentTimeFrameNo());
                };
            });
            
            for (int i = 0; i < lstWorkingTimezone.size(); i++) {
                /*
                 * R6_88
                 * フレキシブルタイム.1日勤務用.開始時間
                 */
                cells.get("AB" + (startIndex + lstWorkingTimezone.get(i).getEmploymentTimeFrameNo()))
                    .setValue(getInDayTimeWithFormat(lstWorkingTimezone.get(i).getTimezone().getStart()));
                    
                /*
                 * R6_89
                 * フレキシブルタイム.1日勤務用.終了時間
                 */
                cells.get("AC" + (startIndex + lstWorkingTimezone.get(i).getEmploymentTimeFrameNo()))
                    .setValue(getInDayTimeWithFormat(lstWorkingTimezone.get(i).getTimezone().getEnd()));
                
                /*
                 * R6_90
                 * フレキシブルタイム.1日勤務用.丸め
                 */
                cells.get("AD" + (startIndex + lstWorkingTimezone.get(i).getEmploymentTimeFrameNo()))
                    .setValue(getRoundingTimeUnitEnum(lstWorkingTimezone.get(i).getTimezone().getRounding().getRoundingTime()));
                
                /*
                 * R6_91
                 * フレキシブルタイム.1日勤務用.端数
                 */
                cells.get("AE" + (startIndex + lstWorkingTimezone.get(i).getEmploymentTimeFrameNo()))
                    .setValue(getRoundingEnum(lstWorkingTimezone.get(i).getTimezone().getRounding().getRounding()));
            }
        }
        
        if (displayMode.equals(DisplayMode.DETAIL.value) && workingTimes) {
            if (lstHalfDayWorkTimezoneMorning.isPresent()) {
                List<EmTimeZoneSetDto> lstWorkingTimezone = lstHalfDayWorkTimezoneMorning.get().getWorkTimezone().getLstWorkingTimezone();
                Collections.sort(lstWorkingTimezone, new Comparator<EmTimeZoneSetDto>() {
                    public int compare(EmTimeZoneSetDto o1, EmTimeZoneSetDto o2) {
                        return o1.getEmploymentTimeFrameNo().compareTo(o2.getEmploymentTimeFrameNo());
                    };
                });
                
                for (int i = 0; i < lstWorkingTimezone.size(); i++) {
                    /*
                     * R6_92
                     * フレキシブルタイム.午前勤務用.開始時間
                     */
                    cells.get("AF" + (startIndex + lstWorkingTimezone.get(i).getEmploymentTimeFrameNo()))
                        .setValue(getInDayTimeWithFormat(lstWorkingTimezone.get(i).getTimezone().getStart()));
                    
                    /*
                     * R6_93
                     * フレキシブルタイム.午前勤務用.終了時間
                     */
                    cells.get("AG" + (startIndex + lstWorkingTimezone.get(i).getEmploymentTimeFrameNo()))
                        .setValue(getInDayTimeWithFormat(lstWorkingTimezone.get(i).getTimezone().getEnd()));
                    
                    /*
                     * R6_94
                     * フレキシブルタイム.午前勤務用.丸め
                     */
                    cells.get("AH" + (startIndex + lstWorkingTimezone.get(i).getEmploymentTimeFrameNo()))
                        .setValue(getRoundingTimeUnitEnum(lstWorkingTimezone.get(i).getTimezone().getRounding().getRoundingTime()));
                    
                    /*
                     * R6_95
                     * フレキシブルタイム.午前勤務用.端数
                     */
                    cells.get("AI" + (startIndex + lstWorkingTimezone.get(i).getEmploymentTimeFrameNo()))
                        .setValue(getRoundingEnum(lstWorkingTimezone.get(i).getTimezone().getRounding().getRounding()));
                }
            }
            
            if (lstHalfDayWorkTimezoneAfternoon.isPresent()) {
                List<EmTimeZoneSetDto> lstWorkingTimezone = lstHalfDayWorkTimezoneAfternoon.get().getWorkTimezone().getLstWorkingTimezone();
                Collections.sort(lstWorkingTimezone, new Comparator<EmTimeZoneSetDto>() {
                    public int compare(EmTimeZoneSetDto o1, EmTimeZoneSetDto o2) {
                        return o1.getEmploymentTimeFrameNo().compareTo(o2.getEmploymentTimeFrameNo());
                    };
                });
                
                for (int i = 0; i < lstWorkingTimezone.size(); i++) {
                    /*
                     * R6_96
                     * フレキシブルタイム.午後勤務用.開始時間
                     */
                    cells.get("AJ" + (startIndex + lstWorkingTimezone.get(i).getEmploymentTimeFrameNo()))
                        .setValue(getInDayTimeWithFormat(lstWorkingTimezone.get(i).getTimezone().getStart()));
                    
                    /*
                     * R6_97
                     * フレキシブルタイム.午後勤務用.終了時間
                     */
                    cells.get("AK" + (startIndex + lstWorkingTimezone.get(i).getEmploymentTimeFrameNo()))
                        .setValue(getInDayTimeWithFormat(lstWorkingTimezone.get(i).getTimezone().getEnd()));
                    
                    /*
                     * R6_98
                     * フレキシブルタイム.午後勤務用.丸め
                     */
                    cells.get("AL" + (startIndex + lstWorkingTimezone.get(i).getEmploymentTimeFrameNo()))
                        .setValue(getRoundingTimeUnitEnum(lstWorkingTimezone.get(i).getTimezone().getRounding().getRoundingTime()));
                    
                    /*
                     * R6_99
                     * フレキシブルタイム.午後勤務用.端数
                     */
                    cells.get("AM" + (startIndex + lstWorkingTimezone.get(i).getEmploymentTimeFrameNo()))
                        .setValue(getRoundingEnum(lstWorkingTimezone.get(i).getTimezone().getRounding().getRounding()));
                }
            }
        }
        
        // 3        タブグ:                残業時間帯
        
        boolean overTime = data.getFlexWorkSetting().getUseHalfDayShift().isOverTime();
        
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            /*
             * R6_261
             * 残業時間帯.半日勤務を設定する
             */
            cells.get("AN" + (startIndex + 1)).setValue(getUseAtrByBoolean(overTime));
        }
        
        List<WorkdayoffFrameFindDto> otFrameFind = this.finder.findAllUsed();
        List<OvertimeWorkFrameFindDto> overTimeLst = this.overTimeFinder.findAllUsed();
        
        if (lstHalfDayWorkTimezoneOneDay.isPresent()) {
            List<OverTimeOfTimeZoneSetDto> lstOTTimezone = lstHalfDayWorkTimezoneOneDay.get().getWorkTimezone().getLstOTTimezone();
            
            for (int i = 0; i < lstOTTimezone.size(); i++) {
                /*
                 * R6_100
                 * 残業時間帯.1日勤務用.開始時間
                 */
                cells.get("AO" + (startIndex + lstOTTimezone.get(i).getWorkTimezoneNo()))
                    .setValue(getInDayTimeWithFormat(lstOTTimezone.get(i).getTimezone().getStart()));
                
                /*
                 * R6_101
                 * 残業時間帯.1日勤務用.終了時間
                 */
                cells.get("AP" + (startIndex + lstOTTimezone.get(i).getWorkTimezoneNo()))
                    .setValue(getInDayTimeWithFormat(lstOTTimezone.get(i).getTimezone().getEnd()));
                
                /*
                 * R6_102
                 * 残業時間帯.1日勤務用.丸め
                 */
                cells.get("AQ" + (startIndex + lstOTTimezone.get(i).getWorkTimezoneNo()))
                    .setValue(getRoundingTimeUnitEnum(lstOTTimezone.get(i).getTimezone().getRounding().getRoundingTime()));
                
                /*
                 * R6_103
                 * 残業時間帯.1日勤務用.端数
                 */
                cells.get("AR" + (startIndex + lstOTTimezone.get(i).getWorkTimezoneNo()))
                    .setValue(getRoundingEnum(lstOTTimezone.get(i).getTimezone().getRounding().getRounding()));
                
                Integer otFrameNo = lstOTTimezone.get(i).getOtFrameNo();
                Optional<OvertimeWorkFrameFindDto> otFrameOpt = overTimeLst.stream()
                        .filter(x -> x.getOvertimeWorkFrNo() == otFrameNo).findFirst();
                if (otFrameOpt.isPresent()) {
                    /*
                     * R6_104
                     * 残業時間帯.1日勤務用.残業枠
                     */
                    cells.get("AS" + (startIndex + lstOTTimezone.get(i).getWorkTimezoneNo())).setValue(otFrameOpt.get().getOvertimeWorkFrName());
                }
                
                /*
                 * R6_105
                 * 残業時間帯.1日勤務用.早出
                 */
                boolean earlyOTUse = lstOTTimezone.get(i).isEarlyOTUse();
                cells.get("AT" + (startIndex + lstOTTimezone.get(i).getWorkTimezoneNo())).setValue(earlyOTUse ? "○" : "-");
            }
        }
        
        if (displayMode.equals(DisplayMode.DETAIL.value) && overTime) {
            if (lstHalfDayWorkTimezoneMorning.isPresent()) {
                List<OverTimeOfTimeZoneSetDto> lstOTTimezone = lstHalfDayWorkTimezoneMorning.get().getWorkTimezone().getLstOTTimezone();
                
                for (int i = 0; i < lstOTTimezone.size(); i++) {
                    /*
                     * R6_106
                     * 残業時間帯.午前勤務用.開始時間
                     */
                    cells.get("AU" + (startIndex + lstOTTimezone.get(i).getWorkTimezoneNo()))
                        .setValue(getInDayTimeWithFormat(lstOTTimezone.get(i).getTimezone().getStart()));
                    
                    /*
                     * R6_107
                     * 残業時間帯.午前勤務用.終了時間
                     */
                    cells.get("AV" + (startIndex + lstOTTimezone.get(i).getWorkTimezoneNo()))
                        .setValue(getInDayTimeWithFormat(lstOTTimezone.get(i).getTimezone().getEnd()));
                    
                    /*
                     * R6_108
                     * 残業時間帯.午前勤務用.丸め
                     */
                    cells.get("AW" + (startIndex + lstOTTimezone.get(i).getWorkTimezoneNo()))
                        .setValue(getRoundingTimeUnitEnum(lstOTTimezone.get(i).getTimezone().getRounding().getRoundingTime()));
                    
                    /*
                     * R6_109
                     * 残業時間帯.午前勤務用.端数
                     */
                    cells.get("AX" + (startIndex + lstOTTimezone.get(i).getWorkTimezoneNo()))
                        .setValue(getRoundingEnum(lstOTTimezone.get(i).getTimezone().getRounding().getRounding()));
                    
                    Integer otFrameNo = lstOTTimezone.get(i).getOtFrameNo();
                    Optional<OvertimeWorkFrameFindDto> otFrameOpt = overTimeLst.stream()
                            .filter(x -> x.getOvertimeWorkFrNo() == otFrameNo).findFirst();
                    if (otFrameOpt.isPresent()) {
                        /*
                         * R6_110
                         * 残業時間帯.午前勤務用.残業枠
                         */
                        cells.get("AY" + (startIndex + lstOTTimezone.get(i).getWorkTimezoneNo())).setValue(otFrameOpt.get().getOvertimeWorkFrName());
                    }
                    
                    /*
                     * R6_111
                     * 残業時間帯.午前勤務用.早出
                     */
                    boolean earlyOTUse = lstOTTimezone.get(i).isEarlyOTUse();
                    cells.get("AZ" + (startIndex + lstOTTimezone.get(i).getWorkTimezoneNo())).setValue(earlyOTUse ? "○" : "-");
                }
            }
            
            if (lstHalfDayWorkTimezoneAfternoon.isPresent()) {
                List<OverTimeOfTimeZoneSetDto> lstOTTimezone = lstHalfDayWorkTimezoneAfternoon.get().getWorkTimezone().getLstOTTimezone();
                
                for (int i = 0; i < lstOTTimezone.size(); i++) {
                    /*
                     * R6_112
                     * 残業時間帯.午後勤務用.開始時間
                     */
                    cells.get("BA" + (startIndex + lstOTTimezone.get(i).getWorkTimezoneNo()))
                        .setValue(getInDayTimeWithFormat(lstOTTimezone.get(i).getTimezone().getStart()));
                    
                    /*
                     * R6_113
                     * 残業時間帯.午後勤務用.終了時間
                     */
                    cells.get("BB" + (startIndex + lstOTTimezone.get(i).getWorkTimezoneNo()))
                        .setValue(getInDayTimeWithFormat(lstOTTimezone.get(i).getTimezone().getEnd()));
                    
                    /*
                     * R6_114
                     * 残業時間帯.午後勤務用.丸め
                     */
                    cells.get("BC" + (startIndex + lstOTTimezone.get(i).getWorkTimezoneNo()))
                        .setValue(getRoundingTimeUnitEnum(lstOTTimezone.get(i).getTimezone().getRounding().getRoundingTime()));
                    
                    /*
                     * R6_115
                     * 残業時間帯.午後勤務用.端数
                     */
                    cells.get("BD" + (startIndex + lstOTTimezone.get(i).getWorkTimezoneNo()))
                        .setValue(getRoundingEnum(lstOTTimezone.get(i).getTimezone().getRounding().getRounding()));
                    
                    Integer otFrameNo = lstOTTimezone.get(i).getOtFrameNo();
                    Optional<OvertimeWorkFrameFindDto> otFrameOpt = overTimeLst.stream()
                            .filter(x -> x.getOvertimeWorkFrNo() == otFrameNo).findFirst();
                    if (otFrameOpt.isPresent()) {
                        /*
                         * R6_116
                         * 残業時間帯.午後勤務用.残業枠
                         */
                        cells.get("BE" + (startIndex + lstOTTimezone.get(i).getWorkTimezoneNo())).setValue(otFrameOpt.get().getOvertimeWorkFrName());
                    }
                    
                    /*
                     * R6_117
                     * 残業時間帯.午後勤務用.早出
                     */
                    boolean earlyOTUse = lstOTTimezone.get(i).isEarlyOTUse();
                    cells.get("BF" + (startIndex + lstOTTimezone.get(i).getWorkTimezoneNo())).setValue(earlyOTUse ? "○" : "-");
                }
            }
        }
        
        // 4        タブグ:                打刻時間帯
        
        List<PrioritySettingDto> prioritySets = data.getFlexWorkSetting().getCommonSetting().getStampSet().getPrioritySets();
        Optional<PrioritySettingDto> prioritySetGoingWorkOpt = prioritySets.stream()
                .filter(x -> x.getStampAtr().equals(StampPiorityAtr.GOING_WORK.value)).findFirst();
        Optional<PrioritySettingDto> prioritySetLeaveWorkOpt = prioritySets.stream()
                .filter(x -> x.getStampAtr().equals(StampPiorityAtr.LEAVE_WORK.value)).findFirst();
        Optional<PrioritySettingDto> prioritySetEnteringOpt = prioritySets.stream()
                .filter(x -> x.getStampAtr().equals(StampPiorityAtr.ENTERING.value)).findFirst();
        Optional<PrioritySettingDto> prioritySetExitOpt = prioritySets.stream()
                .filter(x -> x.getStampAtr().equals(StampPiorityAtr.EXIT.value)).findFirst();
        Optional<PrioritySettingDto> prioritySetPCLoginOpt = prioritySets.stream()
                .filter(x -> x.getStampAtr().equals(StampPiorityAtr.PCLOGIN.value)).findFirst();
        Optional<PrioritySettingDto> prioritySetPCLogoutOpt = prioritySets.stream()
                .filter(x -> x.getStampAtr().equals(StampPiorityAtr.PC_LOGOUT.value)).findFirst();
        
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            /*
             * R6_118
             * 打刻時間帯.優先設定.出勤
             */
            if (prioritySetGoingWorkOpt.isPresent()) {
                Integer priorityAtr = prioritySetGoingWorkOpt.get().getPriorityAtr();
                cells.get("BG" + (startIndex + 1)).setValue(priorityAtr.equals(MultiStampTimePiorityAtr.BEFORE_PIORITY.value) ? "前優先" : "後優先");
            }
            
            /*
             * R6_119
             * 打刻時間帯.優先設定.退勤
             */
            if (prioritySetLeaveWorkOpt.isPresent()) {
                Integer priorityAtr = prioritySetLeaveWorkOpt.get().getPriorityAtr();
                cells.get("BH" + (startIndex + 1)).setValue(priorityAtr.equals(MultiStampTimePiorityAtr.BEFORE_PIORITY.value) ? "前優先" : "後優先");
            }
        }
        
        List<RoundingSetDto> roundingSets = data.getFlexWorkSetting().getCommonSetting().getStampSet().getRoundingTime().getRoundingSets();
        Optional<RoundingSetDto> roundingAttendanceSetOpt = roundingSets.stream().filter(x -> x.getSection() == Superiority.ATTENDANCE.value).findFirst();
        Optional<RoundingSetDto> roundingOfficeSetOpt = roundingSets.stream().filter(x -> x.getSection() == Superiority.OFFICE_WORK.value).findFirst();
        Optional<RoundingSetDto> roundingGoOutSetOpt = roundingSets.stream().filter(x -> x.getSection() == Superiority.GO_OUT.value).findFirst();
        Optional<RoundingSetDto> roundingTurnBackSetOpt = roundingSets.stream().filter(x -> x.getSection() == Superiority.TURN_BACK.value).findFirst();
        
        if (roundingAttendanceSetOpt.isPresent()) {
            /*
             * R6_120
             * 打刻時間帯.打刻丸め.出勤
             */
            cells.get("BI" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(roundingAttendanceSetOpt.get().getRoundingSet().getRoundingTimeUnit()));
            
            /*
             * R6_121
             * 打刻時間帯.打刻丸め.出勤前後設定
             */
            cells.get("BJ" + (startIndex + 1)).setValue(FontRearSection.valueOf(roundingAttendanceSetOpt.get().getRoundingSet().getFontRearSection()).description);
        }
        
        if (roundingOfficeSetOpt.isPresent()) {
            /*
             * R6_122
             * 打刻時間帯.打刻丸め.退勤
             */
            Integer roundingTimeUnit = roundingOfficeSetOpt.get().getRoundingSet().getRoundingTimeUnit();
            cells.get("BK" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(roundingTimeUnit));
            
            /*
             * R6_123
             * 打刻時間帯.打刻丸め.退勤前後設定
             */
            Integer fontRearSection = roundingOfficeSetOpt.get().getRoundingSet().getFontRearSection();
            cells.get("BL" + (startIndex + 1)).setValue(FontRearSection.valueOf(fontRearSection).description);
        }
        
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            List<StampReflectTimezoneDto> lstStampReflectTimezone = data.getFlexWorkSetting().getLstStampReflectTimezone();
            Optional<StampReflectTimezoneDto> lst1stGoWorkOpt = lstStampReflectTimezone.stream()
                .filter(x -> (x.getWorkNo() == 1 && x.getClassification().equals(GoLeavingWorkAtr.GO_WORK.value)))
                .findFirst();
            
            Optional<StampReflectTimezoneDto> lst1stLeavingWorkOpt = lstStampReflectTimezone.stream()
                .filter(x -> (x.getWorkNo() == 1 && x.getClassification().equals(GoLeavingWorkAtr.LEAVING_WORK.value)))
                .findFirst();
            
            if (lst1stGoWorkOpt.isPresent()) {
                /*
                 * R6_124
                 * 打刻詳細設定.出勤反映時間帯.開始時刻
                 */
                Integer startTime = lst1stGoWorkOpt.get().getStartTime();
                cells.get("BM" + (startIndex + 1)).setValue(startTime != null ? getInDayTimeWithFormat(startTime) : "");
                
                /*
                 * R6_125
                 * 打刻詳細設定.出勤反映時間帯.終了時刻
                 */
                Integer endTime = lst1stGoWorkOpt.get().getEndTime();
                cells.get("BN" + (startIndex + 1)).setValue(endTime != null ? getInDayTimeWithFormat(endTime) : "");
            }
            
            if (lst1stLeavingWorkOpt.isPresent()) {
                /*
                 * R6_127
                 * 打刻詳細設定.退勤反映時間帯.開始時刻
                 */
                Integer startTime = lst1stLeavingWorkOpt.get().getStartTime();
                cells.get("BO" + (startIndex + 1)).setValue(startTime != null ? getInDayTimeWithFormat(startTime) : "");
                
                /*
                 * R6_128
                 * 打刻詳細設定.退勤反映時間帯.終了時刻
                 */
                Integer endTime = lst1stLeavingWorkOpt.get().getEndTime();
                cells.get("BP" + (startIndex + 1)).setValue(endTime != null ? getInDayTimeWithFormat(endTime) : "");
            }
            
            /*
             * R6_129
             * 打刻詳細設定.優先設定.入門
             */
            if (prioritySetEnteringOpt.isPresent()) {
                cells.get("BQ" + (startIndex + 1)).setValue(getPrioritySetAtr(prioritySetEnteringOpt.get().getPriorityAtr()));
            }
            
            /*
             * R6_130
             * 打刻詳細設定.優先設定.退門
             */
            if (prioritySetExitOpt.isPresent()) {
                cells.get("BR" + (startIndex + 1)).setValue(getPrioritySetAtr(prioritySetExitOpt.get().getPriorityAtr()));
            }
            
            /*
             * R6_131
             * 打刻詳細設定.優先設定.PCログオン
             */
            if (prioritySetPCLoginOpt.isPresent()) {
                cells.get("BS" + (startIndex + 1)).setValue(getPrioritySetAtr(prioritySetPCLoginOpt.get().getPriorityAtr()));
            }
            
            /*
             * R6_132
             * 打刻詳細設定.優先設定.PCログオフ
             */
            if (prioritySetPCLogoutOpt.isPresent()) {
                cells.get("BT" + (startIndex + 1)).setValue(getPrioritySetAtr(prioritySetPCLogoutOpt.get().getPriorityAtr()));
            }
            
            if (roundingGoOutSetOpt.isPresent()) {
                /*
                 * R6_133
                 * 打刻詳細設定.打刻丸め.外出
                 */
                Integer roundingTimeUnit = roundingGoOutSetOpt.get().getRoundingSet().getRoundingTimeUnit();
                cells.get("BU" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(roundingTimeUnit));
                
                /*
                 * R6_134
                 * 打刻詳細設定.打刻丸め.外出前後設定
                 */
                Integer fontRearSection = roundingGoOutSetOpt.get().getRoundingSet().getFontRearSection();
                cells.get("BV" + (startIndex + 1)).setValue(fontRearSection == 0 ? "前にずらす" : "後ろにずらす");
            }
            
            if (roundingTurnBackSetOpt.isPresent()) {
                /*
                 * R6_135
                 * 打刻詳細設定.打刻丸め.戻り
                 */
                Integer roundingTimeUnit = roundingTurnBackSetOpt.get().getRoundingSet().getRoundingTimeUnit();
                cells.get("BW" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(roundingTimeUnit));
                
                /*
                 * R6_136
                 * 打刻詳細設定.打刻丸め.戻り前後設定
                 */
                Integer fontRearSection = roundingTurnBackSetOpt.get().getRoundingSet().getFontRearSection();
                cells.get("BX" + (startIndex + 1)).setValue(fontRearSection == 0 ? "前にずらす" : "後ろにずらす");
            }
            
            /*
             * R6_264
             * 打刻詳細設定.計算設定.退勤を1分前まで計算する
             */
            Integer attendanceMinuteLaterCalculate = data.getFlexWorkSetting().getCommonSetting().getStampSet().getRoundingTime().getAttendanceMinuteLaterCalculate();
            cells.get("BY" + (startIndex + 1)).setValue((attendanceMinuteLaterCalculate == 0) ? "-" : "○");
            
            /*
             * R6_265
             * 打刻詳細設定.計算設定.退勤を1分前まで計算する
             */
            Integer leaveWorkMinuteAgoCalculate = data.getFlexWorkSetting().getCommonSetting().getStampSet().getRoundingTime().getLeaveWorkMinuteAgoCalculate();
            cells.get("BZ" + (startIndex + 1)).setValue(leaveWorkMinuteAgoCalculate == 0 ? "-" : "○");
        }
        
        // 5        タブグ:                休憩時間帯
        
        boolean breakTime = data.getFlexWorkSetting().getUseHalfDayShift().isBreakTime();
        boolean fixRestTimeOneDay = false;
        
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            /*
             * R6_262
             * 休憩時間帯.半日勤務を設定する
             */
            cells.get("CA" + (startIndex + 1)).setValue(getUseAtrByBoolean(breakTime));
        }
        
        if (lstHalfDayWorkTimezoneOneDay.isPresent()) {
            fixRestTimeOneDay = lstHalfDayWorkTimezoneOneDay.get().getRestTimezone().isFixRestTime();
            
            /*
             * R6_137
             * 休憩時間帯.休憩時間の固定
             */
            cells.get("CB" + (startIndex + 1)).setValue(getUseAtrByBoolean(fixRestTimeOneDay));
            
            if (fixRestTimeOneDay) {
                List<DeductionTimeDto> timezones = lstHalfDayWorkTimezoneOneDay.get().getRestTimezone().getFixedRestTimezone().getTimezones();
                for (int i = 0; i < timezones.size(); i++) {
                    /*
                     * R6_138
                     * 休憩時間帯.1日勤務用（固定する）.開始時間
                     */
                    cells.get("CC" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(timezones.get(i).getStart()));
                    
                    /*
                     * R6_139
                     * 休憩時間帯.1日勤務用（固定する）.終了時間
                     */
                    cells.get("CD" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(timezones.get(i).getEnd()));
                }
                
            }
        }
        
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            if (lstHalfDayWorkTimezoneMorning.isPresent()) {
                boolean fixRestTime = lstHalfDayWorkTimezoneMorning.get().getRestTimezone().isFixRestTime();
                
                if (fixRestTime) {
                    List<DeductionTimeDto> timezones = lstHalfDayWorkTimezoneMorning.get().getRestTimezone().getFixedRestTimezone().getTimezones();
                    for (int i = 0; i < timezones.size(); i++) {
                        /*
                         * R6_140
                         * 休憩時間帯.午前勤務用.開始時間
                         */
                        cells.get("CE" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(timezones.get(i).getStart()));
                        
                        /*
                         * R6_141
                         * 休憩時間帯.午前勤務用.終了時間
                         */
                        cells.get("CF" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(timezones.get(i).getEnd()));
                    }
                }
            }
            
            if (lstHalfDayWorkTimezoneAfternoon.isPresent()) {
                boolean fixRestTime = lstHalfDayWorkTimezoneAfternoon.get().getRestTimezone().isFixRestTime();
                
                if (fixRestTime) {
                    List<DeductionTimeDto> timezones = lstHalfDayWorkTimezoneAfternoon.get().getRestTimezone().getFixedRestTimezone().getTimezones();
                    for (int i = 0; i < timezones.size(); i++) {
                        /*
                         * R6_142
                         * 休憩時間帯.午後勤務用.開始時間
                         */
                        cells.get("CG" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(timezones.get(i).getStart()));
                        
                        /*
                         * R6_143
                         * 休憩時間帯.午後勤務用.終了時間
                         */
                        cells.get("CH" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(timezones.get(i).getEnd()));
                    }
                }
            }
        }
        
        if (lstHalfDayWorkTimezoneOneDay.isPresent()) {
            boolean fixRestTime = lstHalfDayWorkTimezoneOneDay.get().getRestTimezone().isFixRestTime();
            
            if (!fixRestTime) {
                List<FlowRestSettingDto> flowRestSets = lstHalfDayWorkTimezoneOneDay.get().getRestTimezone().getFlowRestTimezone().getFlowRestSets();
                for (int i = 0; i < flowRestSets.size(); i++) {
                    /*
                     * R6_144
                     * 休憩時間帯.1日勤務用（固定しない）.経過時間
                     */
                    cells.get("CI" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(flowRestSets.get(i).getFlowPassageTime()));
                    
                    /*
                     * R6_145
                     * 休憩時間帯.1日勤務用（固定しない）.休憩時間
                     */
                    cells.get("CJ" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(flowRestSets.get(i).getFlowRestTime()));
                }
                
                /*
                 * R6_146
                 * 休憩時間帯.1日勤務用（固定しない）.以降は下記の時間で繰り返す
                 */
                cells.get("CJ" + (startIndex + 7))
                .setValue(lstHalfDayWorkTimezoneOneDay.get().getRestTimezone().getFlowRestTimezone().isUseHereAfterRestSet() ? "○" : "-");
                
                
                /*
                 * R6_147
                 * 休憩時間帯.1日勤務用（固定しない）.経過時間
                 */
                cells.get("CI" + (startIndex + 9))
                .setValue(getInDayTimeWithFormat(lstHalfDayWorkTimezoneOneDay.get().getRestTimezone().getFlowRestTimezone().getHereAfterRestSet().getFlowPassageTime()));
                
                /*
                 * R6_148
                 * 休憩時間帯.1日勤務用（固定しない）.休憩時間
                 */
                cells.get("CJ" + (startIndex + 9))
                .setValue(getInDayTimeWithFormat(lstHalfDayWorkTimezoneOneDay.get().getRestTimezone().getFlowRestTimezone().getHereAfterRestSet().getFlowRestTime()));
            }
        }
        
        if (displayMode.equals(DisplayMode.DETAIL.value) && breakTime && lstHalfDayWorkTimezoneMorning.isPresent()) {
            boolean fixRestTime = lstHalfDayWorkTimezoneMorning.get().getRestTimezone().isFixRestTime();
            
            if (!fixRestTime) {
                List<FlowRestSettingDto> flowRestSets = lstHalfDayWorkTimezoneMorning.get().getRestTimezone().getFlowRestTimezone().getFlowRestSets();
                for (int i = 0; i < flowRestSets.size(); i++) {
                    /*
                     * R6_149
                     * 休憩時間帯.午前勤務用.経過時間
                     */
                    cells.get("CK" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(flowRestSets.get(i).getFlowPassageTime()));
                    
                    /*
                     * R6_150
                     * 休憩時間帯.午前勤務用.休憩時間
                     */
                    cells.get("CL" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(flowRestSets.get(i).getFlowRestTime()));
                }
                
                /*
                 * R6_151
                 * 休憩時間帯.午前勤務用.以降は下記の時間で繰り返す
                 */
                cells.get("CL" + (startIndex + 7))
                .setValue(lstHalfDayWorkTimezoneMorning.get().getRestTimezone().getFlowRestTimezone().isUseHereAfterRestSet() ? "○" : "-");
                
                
                /*
                 * R6_152
                 * 休憩時間帯.午前勤務用.経過時間
                 */
                cells.get("CK" + (startIndex + 9))
                .setValue(getInDayTimeWithFormat(lstHalfDayWorkTimezoneMorning.get().getRestTimezone().getFlowRestTimezone().getHereAfterRestSet().getFlowPassageTime()));
                
                /*
                 * R6_153
                 * 休憩時間帯.午前勤務用.休憩時間
                 */
                cells.get("CL" + (startIndex + 9))
                .setValue(getInDayTimeWithFormat(lstHalfDayWorkTimezoneMorning.get().getRestTimezone().getFlowRestTimezone().getHereAfterRestSet().getFlowRestTime()));
            }
        }
        
        if (displayMode.equals(DisplayMode.DETAIL.value) && breakTime && lstHalfDayWorkTimezoneAfternoon.isPresent()) {
            boolean fixRestTime = lstHalfDayWorkTimezoneAfternoon.get().getRestTimezone().isFixRestTime();
            
            if (!fixRestTime) {
                List<FlowRestSettingDto> flowRestSets = lstHalfDayWorkTimezoneAfternoon.get().getRestTimezone().getFlowRestTimezone().getFlowRestSets();
                for (int i = 0; i < flowRestSets.size(); i++) {
                    /*
                     * R6_154
                     * 休憩時間帯.午前勤務用.経過時間
                     */
                    cells.get("CM" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(flowRestSets.get(i).getFlowPassageTime()));
                    
                    /*
                     * R6_155
                     * 休憩時間帯.午前勤務用.休憩時間
                     */
                    cells.get("CN" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(flowRestSets.get(i).getFlowRestTime()));
                }
                
                /*
                 * R6_156
                 * 休憩時間帯.午前勤務用.以降は下記の時間で繰り返す
                 */
                cells.get("CN" + (startIndex + 7))
                .setValue(lstHalfDayWorkTimezoneAfternoon.get().getRestTimezone().getFlowRestTimezone().isUseHereAfterRestSet() ? "○" : "-");
                
                
                /*
                 * R6_157
                 * 休憩時間帯.午前勤務用.経過時間
                 */
                cells.get("CM" + (startIndex + 9))
                .setValue(getInDayTimeWithFormat(lstHalfDayWorkTimezoneAfternoon.get().getRestTimezone().getFlowRestTimezone().getHereAfterRestSet().getFlowPassageTime()));
                
                /*
                 * R6_158
                 * 休憩時間帯.午前勤務用.休憩時間
                 */
                cells.get("CN" + (startIndex + 9))
                .setValue(getInDayTimeWithFormat(lstHalfDayWorkTimezoneAfternoon.get().getRestTimezone().getFlowRestTimezone().getHereAfterRestSet().getFlowRestTime()));
            }
        }
        
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            /*
             * R6_159
             * 休憩計算設定.休憩中に退勤した場合の休憩時間の計算方法
             */
            Integer calculateMethod = data.getFlexWorkSetting().getRestSetting().getCommonRestSetting().getCalculateMethod();
            cells.get("CO" + (startIndex + 1)).setValue(getCalculatedMethodAtr(calculateMethod));
            
            if (fixRestTimeOneDay) {
                /*
                 * R6_32
                 * 固定休憩設定（休憩時間の固定する）.実績での休憩計算方法
                 */
                Integer calculateMethodFlowRest = data.getFlexWorkSetting().getRestSetting().getFlowRestSetting().getFlowFixedRestSetting().getCalculateMethod();
                cells.get("CP" + (startIndex + 1)).setValue(getCalculateMethodFlowRest(calculateMethodFlowRest));
                
                /*
                 * R6_163
                 * 固定休憩設定（休憩時間の固定する）.私用外出を休憩として扱う
                 */
                boolean usePrivateGoOutRest = data.getFlexWorkSetting().getRestSetting().getFlowRestSetting().getFlowFixedRestSetting().getCalculateFromStamp().isUsePrivateGoOutRest();
                cells.get("CQ" + (startIndex + 1)).setValue(usePrivateGoOutRest ? "○" : "-");
                
                /*
                 * R6_164
                 * 固定休憩設定（休憩時間の固定する）.組合外出を休憩として扱う
                 */
                boolean useAssoGoOutRest = data.getFlexWorkSetting().getRestSetting().getFlowRestSetting().getFlowFixedRestSetting().getCalculateFromStamp().isUseAssoGoOutRest();
                cells.get("CR" + (startIndex + 1)).setValue(useAssoGoOutRest ? "○" : "-");
            } else {
                /*
                 * R6_166
                 * 流動休憩設定（休憩時間の固定しない）.流動休憩設定 外出を休憩として扱う
                 */
                Boolean useStamp = data.getFlexWorkSetting().getRestSetting().getFlowRestSetting().getFlowRestSetting().getUseStamp();
                if (useStamp != null) {
                    cells.get("CS" + (startIndex + 1)).setValue(useStamp ? "○" : "-");
                }
                
                /*
                 * R6_167
                 * 流動休憩設定（休憩時間の固定しない）.外出の計上方法
                 */
                Integer useStampCalcMethod = data.getFlexWorkSetting().getRestSetting().getFlowRestSetting().getFlowRestSetting().getUseStampCalcMethod();
                cells.get("CT" + (startIndex + 1)).setValue(useStampCalcMethod == 0 ? "休憩として計上する" : "外出として計上する");
                
                /*
                 * R6_168
                 * 流動休憩設定（休憩時間の固定しない）.時刻管理設定区分
                 */
                Integer timeManagerSetAtr = data.getFlexWorkSetting().getRestSetting().getFlowRestSetting().getFlowRestSetting().getTimeManagerSetAtr();
                cells.get("CU" + (startIndex + 1)).setValue(timeManagerSetAtr == 0 ? "休憩中の外出は外出を優先する" : "休憩中の外出は休憩を優先し、外出としては計上されない");
            }
        }
        
        // 6        タブグ:                休出時間帯
        
        List<HDWorkTimeSheetSettingDto> lstWorkTimezone = data.getFlexWorkSetting().getOffdayWorkTime().getLstWorkTimezone();
        Collections.sort(lstWorkTimezone, new Comparator<HDWorkTimeSheetSettingDto>() {
            public int compare(HDWorkTimeSheetSettingDto o1, HDWorkTimeSheetSettingDto o2) {
                return o1.getWorkTimeNo().compareTo(o2.getWorkTimeNo());
            };
        });
        
        for (int i = 0; i < lstWorkTimezone.size(); i++) {
            final int index = i;
            
            /*
             * R6_169
             * 休出時間帯.開始時間
             */
            cells.get("CV" + (startIndex + lstWorkTimezone.get(i).getWorkTimeNo())).setValue(getInDayTimeWithFormat(lstWorkTimezone.get(i).getTimezone().getStart()));
            
            /*
             * R6_170
             * 休出時間帯.終了時間
             */
            cells.get("CW" + (startIndex + lstWorkTimezone.get(i).getWorkTimeNo())).setValue(getInDayTimeWithFormat(lstWorkTimezone.get(i).getTimezone().getEnd()));
            
                Optional<WorkdayoffFrameFindDto> workDayOffFrameInLegalBreak = otFrameFind.stream()
                        .filter(x -> x.getWorkdayoffFrNo() == lstWorkTimezone.get(index).getInLegalBreakFrameNo().intValue())
                        .findFirst();
                Optional<WorkdayoffFrameFindDto> workDayOffFrameOutLegalBreak = otFrameFind.stream()
                        .filter(x -> x.getWorkdayoffFrNo() == lstWorkTimezone.get(index).getOutLegalBreakFrameNo().intValue())
                        .findFirst();
                Optional<WorkdayoffFrameFindDto> workDayOffFrameoutLegalPubHD = otFrameFind.stream()
                        .filter(x -> x.getWorkdayoffFrNo() == lstWorkTimezone.get(index).getOutLegalPubHDFrameNo().intValue())
                        .findFirst();
                
                /*
                 * R6_171
                 * 休出時間帯.法定内休出枠
                 */
                if (workDayOffFrameInLegalBreak.isPresent()) {
                    cells.get("CX" + (startIndex + lstWorkTimezone.get(i).getWorkTimeNo()))
                        .setValue(workDayOffFrameInLegalBreak.get().getWorkdayoffFrName());
                }
                
                /*
                 * R6_172
                 * 法定外休出枠.法定外休出枠
                 */
                if (workDayOffFrameOutLegalBreak.isPresent()) {
                    cells.get("CY" + (startIndex + lstWorkTimezone.get(i).getWorkTimeNo()))
                        .setValue(workDayOffFrameOutLegalBreak.get().getWorkdayoffFrName());
                }
                
                /*
                 * R6_173
                 * 法定外休出枠.法定外休出枠（祝日）
                 */
                if (workDayOffFrameoutLegalPubHD.isPresent()) {
                    cells.get("CZ" + (startIndex + lstWorkTimezone.get(i).getWorkTimeNo()))
                        .setValue(workDayOffFrameoutLegalPubHD.get().getWorkdayoffFrName());
                }
                
                
            /*
             * R6_174
             * 法定外休出枠.丸め
             */
            Integer unit = lstWorkTimezone.get(i).getTimezone().getRounding().getRoundingTime();
            cells.get("DA" + (startIndex + lstWorkTimezone.get(i).getWorkTimeNo())).setValue(getRoundingTimeUnitEnum(unit));
            
            /*
             * R6_175
             * 法定外休出枠.端数
             */
            Integer rounding = lstWorkTimezone.get(i).getTimezone().getRounding().getRounding();
            cells.get("DB" + (startIndex + lstWorkTimezone.get(i).getWorkTimeNo())).setValue(getRoundingEnum(rounding));
        }
        
        // 7        タブグ:                休出休憩
        
        boolean fixRestTime = data.getFlexWorkSetting().getOffdayWorkTime().getRestTimezone().isFixRestTime();
        
        /*
         * R6_176
         * 休出休憩.休憩時間の固定
         */
        cells.get("DC" + (startIndex + 1)).setValue(getUseAtrByBoolean(fixRestTime));
        
        if (fixRestTime) {
            List<DeductionTimeDto> timezones = data.getFlexWorkSetting().getOffdayWorkTime().getRestTimezone().getFixedRestTimezone().getTimezones();
            for (int i = 0; i < timezones.size(); i++) {
                /*
                 * R6_177
                 * 休出休憩.固定する.開始時間
                 */
                cells.get("DD" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(timezones.get(i).getStart()));
                
                /*
                 * R6_178
                 * 休出休憩.固定する.終了時間
                 */
                cells.get("DE" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(timezones.get(i).getEnd()));
            }
        } else {
            List<FlowRestSettingDto> flowRestSets = data.getFlexWorkSetting().getOffdayWorkTime().getRestTimezone().getFlowRestTimezone().getFlowRestSets();
            for (int i = 0; i < flowRestSets.size(); i++) {
                /*
                 * R6_179
                 * 休出休憩.固定しない.経過時間
                 */
                cells.get("DF" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(flowRestSets.get(i).getFlowPassageTime()));
                
                /*
                 * R6_180
                 * 休出休憩.固定しない.休憩時間
                 */
                cells.get("DG" + ((startIndex + 1) + i)).setValue(getInDayTimeWithFormat(flowRestSets.get(i).getFlowRestTime()));
            }
            
            /*
             * R6_181
             * 休出休憩.固定しない.以降は下記の時間で繰り返す
             */
            boolean useHereAfterRestSet = data.getFlexWorkSetting().getOffdayWorkTime().getRestTimezone().getFlowRestTimezone().isUseHereAfterRestSet();
            cells.get("DG" + (startIndex + 7)).setValue(useHereAfterRestSet ? "○" : "-");
            
            /*
             * R6_182
             * 休出休憩.固定しない.経過時間
             */
            Integer flowPassageTime = data.getFlexWorkSetting().getOffdayWorkTime().getRestTimezone().getFlowRestTimezone().getHereAfterRestSet().getFlowPassageTime();
            cells.get("DF" + (startIndex + 9)).setValue(getInDayTimeWithFormat(flowPassageTime));
            
            /*
             * R6_183
             * 休出休憩.固定しない.休憩時間
             */
            Integer flowRestTime = data.getFlexWorkSetting().getOffdayWorkTime().getRestTimezone().getFlowRestTimezone().getHereAfterRestSet().getFlowRestTime();
            cells.get("DG" + (startIndex + 9)).setValue(getInDayTimeWithFormat(flowRestTime));
        }
        
        // 8        タブグ:                外出
        
        /*
         * R6_184
         * 外出.外出丸め設定.同じ枠内での丸め設定
         */
        Integer setSameFrameRounding = data.getFlexWorkSetting().getCommonSetting().getGoOutSet()
                .getTotalRoundingSet().getSetSameFrameRounding();
        cells.get("DH" + (startIndex + 1)).setValue(getFrameRoundingAtr(setSameFrameRounding));
        
        /*
         * R6_185
         * 外出.外出丸め設定.枠を跨る場合の丸め設定
         */
        Integer frameStraddRoundingSet = data.getFlexWorkSetting().getCommonSetting().getGoOutSet()
                .getTotalRoundingSet().getFrameStraddRoundingSet();
        cells.get("DI" + (startIndex + 1)).setValue(getFrameRoundingAtr(frameStraddRoundingSet));
        
        /*
         * R6_186
         * 外出.私用・組合外出時間.就業時間帯
         */
        Integer roundingMethodWorkPriAppro = data.getFlexWorkSetting().getCommonSetting().getGoOutSet()
                .getDiffTimezoneSetting().getWorkTimezone().getPrivateUnionGoOut()
                .getApproTimeRoundingSetting().getRoundingMethod();
        cells.get("DJ" + (startIndex + 1)).setValue(getApproTimeRoundingAtr(roundingMethodWorkPriAppro));
        
        /*
         * R6_187
         * 外出.私用・組合外出時間.丸め設定
         */
        Integer unitWorkPriAppro = data.getFlexWorkSetting().getCommonSetting().getGoOutSet()
                .getDiffTimezoneSetting().getWorkTimezone().getPrivateUnionGoOut()
                .getApproTimeRoundingSetting().getRoundingSetting().getRoundingTime();
        cells.get("DK" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitWorkPriAppro));
        
        /*
         * R6_188
         * 外出.私用・組合外出時間.丸め設定端数
         */
        Integer roundingWorkPriAppro = data.getFlexWorkSetting().getCommonSetting().getGoOutSet()
                .getDiffTimezoneSetting().getWorkTimezone().getPrivateUnionGoOut()
                .getApproTimeRoundingSetting().getRoundingSetting().getRounding();
        cells.get("DL" + (startIndex + 1)).setValue(getRoundingEnum(roundingWorkPriAppro));
        
        /*
         * R6_189
         * 外出.私用・組合外出時間.残業時間帯
         */
        Integer roundingMethodOtPriAppro = data.getFlexWorkSetting().getCommonSetting().getGoOutSet()
                .getDiffTimezoneSetting().getOttimezone().getPrivateUnionGoOut()
                .getApproTimeRoundingSetting().getRoundingMethod();
        cells.get("DM" + (startIndex + 1)).setValue(getApproTimeRoundingAtr(roundingMethodOtPriAppro));
        
        /*
         * R6_190
         * 外出.私用・組合外出時間.丸め設定
         */
        Integer unitOtPriAppro = data.getFlexWorkSetting().getCommonSetting().getGoOutSet()
                .getDiffTimezoneSetting().getOttimezone().getPrivateUnionGoOut()
                .getApproTimeRoundingSetting().getRoundingSetting().getRoundingTime();
        cells.get("DN" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitOtPriAppro));
        
        /*
         * R6_191
         * 外出.私用・組合外出時間.丸め設定端数
         */
        Integer roundingOtPriAppro = data.getFlexWorkSetting().getCommonSetting().getGoOutSet()
                .getDiffTimezoneSetting().getOttimezone().getPrivateUnionGoOut()
                .getApproTimeRoundingSetting().getRoundingSetting().getRounding();
        cells.get("DO" + (startIndex + 1)).setValue(getRoundingEnum(roundingOtPriAppro));
        
        /*
         * R6_192
         * 外出.私用・組合外出時間.休出時間帯
         */
        Integer roundingMethodHdPriAppro = data.getFlexWorkSetting().getCommonSetting().getGoOutSet()
                .getDiffTimezoneSetting().getPubHolWorkTimezone().getPrivateUnionGoOut()
                .getApproTimeRoundingSetting().getRoundingMethod();
        cells.get("DP" + (startIndex + 1)).setValue(getApproTimeRoundingAtr(roundingMethodHdPriAppro));
        
        /*
         * R6_193
         * 外出.私用・組合外出時間.丸め設定
         */
        Integer unitHdPriAppro = data.getFlexWorkSetting().getCommonSetting().getGoOutSet()
                .getDiffTimezoneSetting().getPubHolWorkTimezone().getPrivateUnionGoOut()
                .getApproTimeRoundingSetting().getRoundingSetting().getRoundingTime();
        cells.get("DQ" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitHdPriAppro));
        
        /*
         * R6_194
         * 外出.私用・組合外出時間.丸め設定端数
         */
        Integer roundingHdPriAppro = data.getFlexWorkSetting().getCommonSetting().getGoOutSet()
                .getDiffTimezoneSetting().getPubHolWorkTimezone().getPrivateUnionGoOut()
                .getApproTimeRoundingSetting().getRoundingSetting().getRounding();
        cells.get("DR" + (startIndex + 1)).setValue(getRoundingEnum(roundingHdPriAppro));
        
        /*
         * R6_195
         * 外出.私用・組合外出控除時間.就業時間帯
         */
        Integer roundingMethodWorkPriDeduct = data.getFlexWorkSetting().getCommonSetting().getGoOutSet()
                .getDiffTimezoneSetting().getWorkTimezone().getPrivateUnionGoOut()
                .getDeductTimeRoundingSetting().getRoundingMethod();
        cells.get("DS" + (startIndex + 1)).setValue(getApproTimeRoundingAtr(roundingMethodWorkPriDeduct));
        
        /*
         * R6_196
         * 外出.私用・組合外出控除時間.丸め設定
         */
        Integer unitWorkPriDeduct = data.getFlexWorkSetting().getCommonSetting().getGoOutSet()
                .getDiffTimezoneSetting().getWorkTimezone().getPrivateUnionGoOut()
                .getDeductTimeRoundingSetting().getRoundingSetting().getRoundingTime();
        cells.get("DT" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitWorkPriDeduct));
        
        /*
         * R6_197
         * 外出.私用・組合外出控除時間.丸め設定端数
         */
        Integer roundingWorkPriDeduct = data.getFlexWorkSetting().getCommonSetting().getGoOutSet()
                .getDiffTimezoneSetting().getWorkTimezone().getPrivateUnionGoOut()
                .getDeductTimeRoundingSetting().getRoundingSetting().getRounding();
        cells.get("DU" + (startIndex + 1)).setValue(getRoundingEnum(roundingWorkPriDeduct));
        
        /*
         * R6_198
         * 外出.私用・組合外出控除時間.残業時間帯
         */
        Integer roundingMethodOtPriDeduct = data.getFlexWorkSetting().getCommonSetting().getGoOutSet()
                .getDiffTimezoneSetting().getOttimezone().getPrivateUnionGoOut()
                .getDeductTimeRoundingSetting().getRoundingMethod();
        cells.get("DV" + (startIndex + 1)).setValue(getApproTimeRoundingAtr(roundingMethodOtPriDeduct));
        
        /*
         * R6_199
         * 外出.私用・組合外出控除時間.丸め設定
         */
        Integer unitOtPriDeduct = data.getFlexWorkSetting().getCommonSetting().getGoOutSet()
                .getDiffTimezoneSetting().getOttimezone().getPrivateUnionGoOut()
                .getDeductTimeRoundingSetting().getRoundingSetting().getRoundingTime();
        cells.get("DW" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitOtPriDeduct));
        
        /*
         * R6_200
         * 外出.私用・組合外出控除時間.丸め設定端数
         */
        Integer roundingOtPriDeduct = data.getFlexWorkSetting().getCommonSetting().getGoOutSet()
                .getDiffTimezoneSetting().getOttimezone().getPrivateUnionGoOut()
                .getDeductTimeRoundingSetting().getRoundingSetting().getRounding();
        cells.get("DX" + (startIndex + 1)).setValue(getRoundingEnum(roundingOtPriDeduct));
        
        /*
         * R6_201
         * 外出.私用・組合外出控除時間.休出時間帯
         */
        Integer roundingMethodHdPriDeduct = data.getFlexWorkSetting().getCommonSetting().getGoOutSet()
                .getDiffTimezoneSetting().getPubHolWorkTimezone().getPrivateUnionGoOut()
                .getDeductTimeRoundingSetting().getRoundingMethod();
        cells.get("DY" + (startIndex + 1)).setValue(getApproTimeRoundingAtr(roundingMethodHdPriDeduct));
        
        /*
         * R6_202
         * 外出.私用・組合外出控除時間.丸め設定
         */
        Integer unitHdPriDeduct = data.getFlexWorkSetting().getCommonSetting().getGoOutSet()
                .getDiffTimezoneSetting().getPubHolWorkTimezone().getPrivateUnionGoOut()
                .getDeductTimeRoundingSetting().getRoundingSetting().getRoundingTime();
        cells.get("DZ" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitHdPriDeduct));
        
        /*
         * R6_203
         * 外出.私用・組合外出控除時間.丸め設定端数
         */
        Integer roundingHdPriDeduct = data.getFlexWorkSetting().getCommonSetting().getGoOutSet()
                .getDiffTimezoneSetting().getPubHolWorkTimezone().getPrivateUnionGoOut()
                .getDeductTimeRoundingSetting().getRoundingSetting().getRounding();
        cells.get("EA" + (startIndex + 1)).setValue(getRoundingEnum(roundingHdPriDeduct));
        
        /*
         * R6_204
         * 外出.公用・有償外出時間.就業時間帯
         */
        Integer roundingMethodWorkOfficalAppro = data.getFlexWorkSetting().getCommonSetting().getGoOutSet()
                .getDiffTimezoneSetting().getWorkTimezone().getOfficalUseCompenGoOut()
                .getApproTimeRoundingSetting().getRoundingMethod();
        cells.get("EB" + (startIndex + 1)).setValue(getApproTimeRoundingAtr(roundingMethodWorkOfficalAppro));
        
        /*
         * R6_205
         * 外出.公用・有償外出時間.丸め設定
         */
        Integer unitWOrkOfficalApro = data.getFlexWorkSetting().getCommonSetting().getGoOutSet()
                .getDiffTimezoneSetting().getWorkTimezone().getOfficalUseCompenGoOut()
                .getApproTimeRoundingSetting().getRoundingSetting().getRoundingTime();
        cells.get("EC" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitWOrkOfficalApro));
        
        /*
         * R6_206
         * 外出.公用・有償外出時間.丸め設定端数
         */
        Integer roundingWorkOfficalAppro = data.getFlexWorkSetting().getCommonSetting().getGoOutSet()
                .getDiffTimezoneSetting().getWorkTimezone().getOfficalUseCompenGoOut()
                .getApproTimeRoundingSetting().getRoundingSetting().getRounding();
        cells.get("ED" + (startIndex + 1)).setValue(getRoundingEnum(roundingWorkOfficalAppro));
        
        /*
         * R6_207
         * 外出.公用・有償外出時間.残業時間帯
         */
        Integer roundingMethodOtOfficalAppro = data.getFlexWorkSetting().getCommonSetting().getGoOutSet()
                .getDiffTimezoneSetting().getOttimezone().getOfficalUseCompenGoOut()
                .getApproTimeRoundingSetting().getRoundingMethod();
        cells.get("EE" + (startIndex + 1)).setValue(getApproTimeRoundingAtr(roundingMethodOtOfficalAppro));
        
        /*
         * R6_208
         * 外出.公用・有償外出時間.丸め設定
         */
        Integer unitOtOfficalAppro = data.getFlexWorkSetting().getCommonSetting().getGoOutSet()
                .getDiffTimezoneSetting().getOttimezone().getOfficalUseCompenGoOut()
                .getApproTimeRoundingSetting().getRoundingSetting().getRoundingTime();
        cells.get("EF" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitOtOfficalAppro));
        
        /*
         * R6_209
         * 外出.公用・有償外出時間.丸め設定端数
         */
        Integer roundingOtOfficalAppro = data.getFlexWorkSetting().getCommonSetting().getGoOutSet()
                .getDiffTimezoneSetting().getOttimezone().getOfficalUseCompenGoOut()
                .getApproTimeRoundingSetting().getRoundingSetting().getRounding();
        cells.get("EG" + (startIndex + 1)).setValue(getRoundingEnum(roundingOtOfficalAppro));
        
        /*
         * R6_210
         * 外出.公用・有償外出時間.休出時間帯
         */
        Integer roundingMethodHdOfficalAppro = data.getFlexWorkSetting().getCommonSetting().getGoOutSet()
                .getDiffTimezoneSetting().getPubHolWorkTimezone().getOfficalUseCompenGoOut()
                .getApproTimeRoundingSetting().getRoundingMethod();
        cells.get("EH" + (startIndex + 1)).setValue(getApproTimeRoundingAtr(roundingMethodHdOfficalAppro));
        
        /*
         * R6_211
         * 外出.公用・有償外出時間.丸め設定
         */
        Integer unitHdOfficalAppro = data.getFlexWorkSetting().getCommonSetting().getGoOutSet()
                .getDiffTimezoneSetting().getPubHolWorkTimezone().getOfficalUseCompenGoOut()
                .getApproTimeRoundingSetting().getRoundingSetting().getRoundingTime();
        cells.get("EI" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitHdOfficalAppro));
        
        /*
         * R6_212
         * 外出.公用・有償外出時間.丸め設定端数
         */
        Integer roudingHdOfficalAppro = data.getFlexWorkSetting().getCommonSetting().getGoOutSet()
                .getDiffTimezoneSetting().getPubHolWorkTimezone().getOfficalUseCompenGoOut()
                .getApproTimeRoundingSetting().getRoundingSetting().getRounding();
        cells.get("EJ" + (startIndex + 1)).setValue(getRoundingEnum(roudingHdOfficalAppro));
        
        // 9        タブグ:                遅刻早退
        
        List<OtherEmTimezoneLateEarlySetDto> otherClassSets = data.getFlexWorkSetting().getCommonSetting().getLateEarlySet().getOtherClassSets();
        Optional<OtherEmTimezoneLateEarlySetDto> otherLate = otherClassSets.stream()
                .filter(x -> x.getLateEarlyAtr().equals(LateEarlyAtr.LATE.value)).findFirst();
        Optional<OtherEmTimezoneLateEarlySetDto> otherEarly = otherClassSets.stream()
                .filter(x -> x.getLateEarlyAtr().equals(LateEarlyAtr.EARLY.value)).findFirst();
        
        if (otherLate.isPresent()) {
            /*
             * R6_213
             * 遅刻早退.遅刻早退時間丸め.遅刻丸め
             */
            Integer unitRecord = otherLate.get().getRecordTimeRoundingSet().getRoundingTime();
            cells.get("EK" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitRecord));
            
            /*
             * R6_214
             * 遅刻早退.遅刻早退時間丸め.遅刻端数
             */
            Integer roundingRecord = otherLate.get().getRecordTimeRoundingSet().getRounding();
            cells.get("EL" + (startIndex + 1)).setValue(getRoundingEnum(roundingRecord));
        }
        
        if (otherEarly.isPresent()) {
            /*
             * R6_215
             * 遅刻早退.遅刻早退時間丸め.早退丸め
             */
            Integer unitRecord = otherEarly.get().getRecordTimeRoundingSet().getRoundingTime();
            cells.get("EM" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitRecord));
            
            /*
             * R6_216
             * 遅刻早退.遅刻早退時間丸め.早退端数
             */
            Integer roundingRecord = otherEarly.get().getRecordTimeRoundingSet().getRounding();
            cells.get("EN" + (startIndex + 1)).setValue(getRoundingEnum(roundingRecord));
        }
        
        if (otherLate.isPresent()) {
            /*
             * R6_217
             * 遅刻早退.遅刻早退控除時間丸め.遅刻丸め
             */
            Integer unitDel = otherLate.get().getDelTimeRoundingSet().getRoundingTime();
            cells.get("EO" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitDel));
            
            /*
             * R6_218
             * 遅刻早退.遅刻早退控除時間丸め.遅刻端数
             */
            Integer roundingDel = otherLate.get().getDelTimeRoundingSet().getRounding();
            cells.get("EP" + (startIndex + 1)).setValue(getRoundingEnum(roundingDel));
        }
        
        if (otherEarly.isPresent()) {
            /*
             * R6_219
             * 遅刻早退.遅刻早退控除時間丸め.早退丸め
             */
            Integer unitDel = otherEarly.get().getDelTimeRoundingSet().getRoundingTime();
            cells.get("EQ" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitDel));
            
            /*
             * R6_220
             * 遅刻早退.遅刻早退控除時間丸め.早退端数
             */
            Integer roundingDel = otherEarly.get().getDelTimeRoundingSet().getRounding();
            cells.get("ER" + (startIndex + 1)).setValue(getRoundingEnum(roundingDel));
        }
        
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            /*
             * R6_221
             * 遅刻早退詳細設定.控除時間.遅刻早退時間を就業時間から控除する
             */
            boolean delFromEmTime = data.getFlexWorkSetting().getCommonSetting().getLateEarlySet().getCommonSet().isDelFromEmTime();
            cells.get("ES" + (startIndex + 1)).setValue(delFromEmTime ? "○" : "-");
            
            if (otherLate.isPresent()) {
                /*
                 * R6_224
                 * 遅刻早退詳細設定.猶予時間.遅刻猶予時間
                 */
                Integer graceTime = otherLate.get().getGraceTimeSet().getGraceTime();
                cells.get("ET" + (startIndex + 1)).setValue(getInDayTimeWithFormat(graceTime));
                
                /*
                 * R6_225
                 * 遅刻早退詳細設定.猶予時間.遅刻猶予時間を就業時間に含める
                 */
                boolean includeWorkingHour = otherLate.get().getGraceTimeSet().isIncludeWorkingHour();
                cells.get("EU" + (startIndex + 1)).setValue(includeWorkingHour ? "○" : "-");
            }
            
            if (otherEarly.isPresent()) {
                /*
                 * R6_226
                 * 遅刻早退詳細設定.猶予時間.早退猶予時間
                 */
                Integer graceTime = otherEarly.get().getGraceTimeSet().getGraceTime();
                cells.get("EV" + (startIndex + 1)).setValue(getInDayTimeWithFormat(graceTime));
                
                /*
                 * R6_227
                 * 遅刻早退詳細設定.猶予時間.早退猶予時間を就業時間に含める
                 */
                boolean includeWorkingHour = otherEarly.get().getGraceTimeSet().isIncludeWorkingHour();
                cells.get("EW" + (startIndex + 1)).setValue(includeWorkingHour ? "○" : "-");
            }
        }
        
        // 10       タブグ:                加給
        
        /*
         * R6_228
         * コード
         */
        String raisingSalarySetCode = data.getFlexWorkSetting().getCommonSetting().getRaisingSalarySet();
        cells.get("EX" + (startIndex + 1)).setValue(raisingSalarySetCode != null ? raisingSalarySetCode : "");
        
        /*
         * R6_229
         * 名称
         */
        if (raisingSalarySetCode != null) {
            Optional<BonusPaySetting> bonusPaySettingOpt = this.bpSettingRepository
                    .getBonusPaySetting(AppContexts.user().companyId(), new BonusPaySettingCode(raisingSalarySetCode));
            if (bonusPaySettingOpt.isPresent()) {
                String raisingSalaryName = bonusPaySettingOpt.get().getName().v();
                cells.get("EY" + (startIndex + 1)).setValue(raisingSalaryName);
            }
        }
        
        // 11       タブグ:                代休
        
        List<WorkTimezoneOtherSubHolTimeSetDto> subHolTimeSet = data.getFlexWorkSetting().getCommonSetting().getSubHolTimeSet();
        Optional<WorkTimezoneOtherSubHolTimeSetDto> subHolTimeWorkDayOffSet = subHolTimeSet.stream()
                .filter(x -> x.getOriginAtr().equals(CompensatoryOccurrenceDivision.WorkDayOffTime.value)).findFirst();
        Optional<WorkTimezoneOtherSubHolTimeSetDto> subHolTimeOverTimeOffSet = subHolTimeSet.stream()
                .filter(x -> x.getOriginAtr().equals(CompensatoryOccurrenceDivision.FromOverTime.value)).findFirst();
        
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            if (subHolTimeWorkDayOffSet.isPresent()) {
                /*
                 * R6_230
                 * 代休.代休発生に必要な時間.休日出勤
                 */
                boolean useDivision = subHolTimeWorkDayOffSet.get().getSubHolTimeSet().isUseDivision();
                cells.get("EZ" + (startIndex + 1)).setValue(useDivision ? "○" : "-");
                
                /*
                 * R6_231
                 * 代休.代休発生に必要な時間.時間区分
                 */
                Integer subHolTransferSetAtr = subHolTimeWorkDayOffSet.get().getSubHolTimeSet().getSubHolTransferSetAtr();
                cells.get("FA" + (startIndex + 1)).setValue(subHolTransferSetAtr == 0 ? "指定時間" : "一定時間");
            }
        }
        
        if (subHolTimeWorkDayOffSet.isPresent()) {
            /*
             * R6_232
             * 代休.代休発生に必要な時間.１日
             */
            Integer oneDayTime = subHolTimeWorkDayOffSet.get().getSubHolTimeSet().getDesignatedTime().getOneDayTime();
            cells.get("FB" + (startIndex + 1)).setValue(getInDayTimeWithFormat(oneDayTime));
            
            /*
             * R6_233
             * 代休.代休発生に必要な時間.半日
             */
            Integer halfDayTime = subHolTimeWorkDayOffSet.get().getSubHolTimeSet().getDesignatedTime().getHalfDayTime();
            cells.get("FC" + (startIndex + 1)).setValue(getInDayTimeWithFormat(halfDayTime));
        }
        
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            if (subHolTimeWorkDayOffSet.isPresent()) {
                /*
                 * R6_234
                 * 代休.代休発生に必要な時間.一定時間
                 */
                Integer certainTime = subHolTimeWorkDayOffSet.get().getSubHolTimeSet().getCertainTime();
                cells.get("FD" + (startIndex + 1)).setValue(getInDayTimeWithFormat(certainTime));
            }
            
            if (subHolTimeOverTimeOffSet.isPresent()) {
                /*
                 * R6_235
                 * 代休.代休発生に必要な時間.残業
                 */
                boolean useDivision = subHolTimeOverTimeOffSet.get().getSubHolTimeSet().isUseDivision();
                cells.get("FE" + (startIndex + 1)).setValue(useDivision ? "○" : "-");
                
                /*
                 * R6_236
                 * 代休.代休発生に必要な時間.時間
                 */
                Integer subHolTransferSetAtr = subHolTimeOverTimeOffSet.get().getSubHolTimeSet().getSubHolTransferSetAtr();
                cells.get("FF" + (startIndex + 1)).setValue(subHolTransferSetAtr == 0 ? "指定時間" : "一定時間");
                
                /*
                 * R6_237
                 * 代休.代休発生に必要な時間.１日
                 */
                Integer oneDayTime = subHolTimeOverTimeOffSet.get().getSubHolTimeSet().getDesignatedTime().getOneDayTime();
                cells.get("FG" + (startIndex + 1)).setValue(getInDayTimeWithFormat(oneDayTime));
                
                /*
                 * R6_238
                 * 代休.代休発生に必要な時間.半日
                 */
                Integer halfDayTime = subHolTimeOverTimeOffSet.get().getSubHolTimeSet().getDesignatedTime().getHalfDayTime();
                cells.get("FH" + (startIndex + 1)).setValue(getInDayTimeWithFormat(halfDayTime));
                
                /*
                 * R6_239
                 * 代休.代休発生に必要な時間.一定時間
                 */
                Integer certainTime = subHolTimeOverTimeOffSet.get().getSubHolTimeSet().getCertainTime();
                cells.get("FI" + (startIndex + 1)).setValue(getInDayTimeWithFormat(certainTime));
            }
        }
        
        // 12       タブグ:                深夜
        
        /*
         * R6_240
         * 深夜.深夜時間丸め
         */
        Integer unit = data.getFlexWorkSetting().getCommonSetting().getLateNightTimeSet().getRoundingSetting().getRoundingTime();
        cells.get("FJ" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unit));
        
        /*
         * R6_241
         * 深夜.深夜時間端数
         */
        Integer rounding = data.getFlexWorkSetting().getCommonSetting().getLateNightTimeSet().getRoundingSetting().getRounding();
        cells.get("FK" + (startIndex + 1)).setValue(getRoundingEnum(rounding));
        
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            // 13       タブグ:                臨時
            
            /*
             * R6_242
             * 臨時.臨時丸め
             */
            Integer unitExtrao = data.getFlexWorkSetting().getCommonSetting().getExtraordTimeSet().getTimeRoundingSet().getRoundingTime();
            cells.get("FL" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(unitExtrao));
            
            /*
             * R6_243
             * 臨時.臨時端数
             */
            Integer roundingExtrao = data.getFlexWorkSetting().getCommonSetting().getExtraordTimeSet().getTimeRoundingSet().getRounding();
            cells.get("FM" + (startIndex + 1)).setValue(getRoundingEnum(roundingExtrao));
            
            // 14       タブグ:                育児
            
            /*
             * R6_244
             * 育児.育児時間帯に勤務した場合の扱い
             */
            boolean childCareWorkUse = data.getFlexWorkSetting().getCommonSetting().getShortTimeWorkSet().isChildCareWorkUse();
            cells.get("FN" + (startIndex + 1)).setValue(childCareWorkUse ? "育児時間を減算する" : "育児時間を減算しない");
            
            /*
             * R6_245
             * 育児.介護時間帯に勤務した場合の扱い
             */
            boolean nursTimezoneWorkUse = data.getFlexWorkSetting().getCommonSetting().getShortTimeWorkSet().isNursTimezoneWorkUse();
            cells.get("FO" + (startIndex + 1)).setValue(nursTimezoneWorkUse ? "介護時間を減算する" : "介護時間を減算しない");
            
            // 15       タブグ:                医療
            
            List<WorkTimezoneMedicalSetDto> medicalSet = data.getFlexWorkSetting().getCommonSetting().getMedicalSet();
            Optional<WorkTimezoneMedicalSetDto> medicalDay = medicalSet.stream()
                    .filter(x -> x.getWorkSystemAtr().equals(WorkSystemAtr.DAY_SHIFT.value)).findFirst();
            Optional<WorkTimezoneMedicalSetDto> medicalNight = medicalSet.stream()
                    .filter(x -> x.getWorkSystemAtr().equals(WorkSystemAtr.NIGHT_SHIFT.value)).findFirst();
            
            /*
             * R6_246
             * 医療.日勤申し送り時間
             */
            if (medicalDay.isPresent()) {
                cells.get("FP" + (startIndex + 1)).setValue(getInDayTimeWithFormat(medicalDay.get().getApplicationTime()));
            }
            
            /*
             * R6_247
             * 医療.夜勤申し送り時間
             */
            if (medicalNight.isPresent()) {
                cells.get("FQ" + (startIndex + 1)).setValue(getInDayTimeWithFormat(medicalNight.get().getApplicationTime()));
            }
            
            if (medicalDay.isPresent()) {
                /*
                 * R6_248
                 * 医療.日勤勤務時間.丸め
                 */
                cells.get("FR" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(medicalDay.get().getRoundingSet().getRoundingTime()));
                
                /*
                 * R6_249
                 * 医療.日勤勤務時間.端数
                 */
                cells.get("FS" + (startIndex + 1)).setValue(getRoundingEnum(medicalDay.get().getRoundingSet().getRounding()));
            }
            
            if (medicalNight.isPresent()) {
                /*
                 * R6_250
                 * 医療.夜勤勤務時間.丸め
                 */
                cells.get("FT" + (startIndex + 1)).setValue(getRoundingTimeUnitEnum(medicalNight.get().getRoundingSet().getRoundingTime()));
                
                /*
                 * R6_251
                 * 医療.夜勤勤務時間.端数
                 */
                cells.get("FU" + (startIndex + 1)).setValue(getRoundingEnum(medicalNight.get().getRoundingSet().getRounding()));
            }
            
            // 16       タブグ:                0時跨ぎ
            
            /*
             * R6_252
             * ０時跨ぎ.0時跨ぎ計算
             */
            boolean zeroHStraddCalculateSet = data.getFlexWorkSetting().getCommonSetting().isZeroHStraddCalculateSet();
            cells.get("FV" + (startIndex + 1)).setValue(getUseAtrByBoolean(zeroHStraddCalculateSet));
        }
        
        // 17       タブグ:                その地
        
        boolean ootsuka = AppContexts.optionLicense().customize().ootsuka();
        if (ootsuka) {
            /*
             * R6_253
             * その他.勤務種類が休暇の場合に就業時間を計算するか
             */
            Integer isCalculate = data.getFlexWorkSetting().getCommonSetting().getHolidayCalculation().getIsCalculate();
            cells.get("FW" + (startIndex + 1)).setValue(getUseAtrByInteger(isCalculate));
        } else {
        	cells.deleteColumn(178);
        }
    }
    
    /**
     * タブグ: 所定
     * @param data
     * @param cells
     * @param startIndex
     */
    private void printDataPrescribed(WorkTimeSettingInfoDto data, Cells cells, int startIndex, int type) {
        Integer displayMode = data.getDisplayMode().displayMode;
        int columnIndex = 0;
        
        /*
         * R4_59
         * R5_51
         * R6_61
         * コード
         */
        String workTimeCode = data.getWorktimeSetting().worktimeCode;
        cells.get(startIndex, columnIndex).setValue(workTimeCode);
        columnIndex++;
        
        /*
         * R4_60
         * R5_52
         * R6_62
         * 名称
         */
        String workTimeName = data.getWorktimeSetting().workTimeDisplayName.workTimeName;
        cells.get(startIndex, columnIndex).setValue(workTimeName);
        columnIndex++;
        
        /*
         * R4_61
         * R5_53
         * R6_63
         * 略名
         */
        String workTimeAbName = data.getWorktimeSetting().workTimeDisplayName.workTimeAbName;
        cells.get(startIndex, columnIndex).setValue(workTimeAbName);
        columnIndex++;
        
        /*
         * R4_63
         * R5_55
         * R6_65
         * 廃止
         */
        String isAbolish = data.getWorktimeSetting().isAbolish ?  "○" : "-";
        cells.get(startIndex, columnIndex).setValue(isAbolish);
        columnIndex++;
        
        /*
         * R4_64
         * R5_56
         * R6_66
         * 勤務形態
         */
        String workStyle = StringUtils.EMPTY;
        if (data.getWorktimeSetting().workTimeDivision.workTimeDailyAtr == WorkTimeDailyAtr.REGULAR_WORK.value) {
            workStyle = data.getWorktimeSetting().workTimeDivision.workTimeDailyAtr == WorkTimeDailyAtr.REGULAR_WORK.value ? 
                    WorkTimeDailyAtr.REGULAR_WORK.description : "";
        } else if (data.getWorktimeSetting().workTimeDivision.workTimeDailyAtr == WorkTimeDailyAtr.FLEX_WORK.value) {
            workStyle = data.getWorktimeSetting().workTimeDivision.workTimeDailyAtr == WorkTimeDailyAtr.FLEX_WORK.value ? 
                    WorkTimeDailyAtr.FLEX_WORK.description : "";
        }
        cells.get(startIndex, columnIndex).setValue(workStyle);
        columnIndex++;
        
        /*
         * R4_65
         * R5_57
         * 設定方法
         */
        if (type != FLEX) {
            String settingMethod = StringUtils.EMPTY;
            if (data.getWorktimeSetting().workTimeDivision.workTimeMethodSet == WorkTimeMethodSet.FIXED_WORK.value) {
                settingMethod = data.getWorktimeSetting().workTimeDivision.workTimeMethodSet == WorkTimeMethodSet.FIXED_WORK.value ? 
                        WorkTimeMethodSet.FIXED_WORK.description : "";
            } else if (data.getWorktimeSetting().workTimeDivision.workTimeMethodSet == WorkTimeMethodSet.FLOW_WORK.value) {
                settingMethod = data.getWorktimeSetting().workTimeDivision.workTimeMethodSet == WorkTimeMethodSet.FLOW_WORK.value ? 
                        WorkTimeMethodSet.FLOW_WORK.description : "";
            }
            cells.get(startIndex, columnIndex).setValue(settingMethod);
            columnIndex++;
        }
        
        /*
         * R4_67
         * R5_58
         * R6_67
         * モード
         */
        String displayModeString = displayMode.equals(DisplayMode.SIMPLE.value) ? DisplayMode.SIMPLE.description : DisplayMode.DETAIL.description;
        cells.get(startIndex, columnIndex).setValue(displayModeString);
        columnIndex++;
        
        /*
         * R4_68
         * R5_60
         * R6_69
         * 備考
         */
        String note = data.getWorktimeSetting().note;
        cells.get(startIndex, columnIndex).setValue(note);
        columnIndex++;
        
        /*
         * R4_69
         * R5_61
         * R6_70
         * コメント
         */
        String memo = data.getWorktimeSetting().memo;
        cells.get(startIndex, columnIndex).setValue(memo);
        columnIndex++;
        
        /*
         * R4_70
         * R5_62
         * R6_71
         * 所定設定.1日の始まりの時刻
         */
        int timeOfDayStart = data.getPredseting().startDateClock;
        cells.get(startIndex, columnIndex).setValue(getInDayTimeWithFormat(timeOfDayStart));
        columnIndex++;
        
        /*
         * R4_71
         * R5_63
         * R6_72
         * 1日の範囲時間.時間
         */
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            int rangeTimeOfDay = new AttendanceTime(data.getPredseting().getRangeTimeDay()).hour();
            cells.get(startIndex, columnIndex).setValue(rangeTimeOfDay);
        }
        columnIndex++;
        
        Optional<TimezoneDto> time = data.getPredseting().getPrescribedTimezoneSetting().getLstTimezone().stream()
                .filter(x -> x.workNo == 1).findFirst();
        if (time.isPresent()) {
            /*
             * R4_73
             * R5_65
             * R6_74
             * 就業時刻1回目.始業時刻
             */
            Integer startTimeInt = time.get().start;
            cells.get(startIndex, columnIndex).setValue(startTimeInt != null ? 
                    getInDayTimeWithFormat(startTimeInt) : "");
            columnIndex++;
            
            /*
             * R4_74
             * R5_66
             * R6_75
             * 就業時刻1回目.終業時刻
             */
            Integer endTimeInt = time.get().end;
            cells.get(startIndex, columnIndex).setValue(endTimeInt != null ? 
                    getInDayTimeWithFormat(endTimeInt) : "");
            columnIndex++;
        } else {
            columnIndex += 2;
        }
        
        /*
         * R4_75
         * 就業時刻1回目.残業を含めた所定時間帯を設定する
         */
        if (type == NORMAL) {
            if (displayMode.equals(DisplayMode.DETAIL.value)) {
                String predetermine = data.getPredseting().isPredetermine() ? "○" : "-";
                cells.get(startIndex, columnIndex).setValue(predetermine);
            }
            columnIndex++;
        }
        
        // 就業時刻2回目
        if (type != FLEX) {
            if (displayMode.equals(DisplayMode.DETAIL.value)) {
                Optional<TimezoneDto> time2 = data.getPredseting().getPrescribedTimezoneSetting().getLstTimezone().stream()
                        .filter(x -> x.workNo == 2).findFirst();
                if (time2.isPresent()) {
                    /*
                     * R4_4
                     * R5_4
                     * 就業時刻2回目.2回目を使用する
                     */
                    String useTime2 = time2.get().isUseAtr() ? "○" : "-";
                    cells.get(startIndex, columnIndex).setValue(useTime2);
                    columnIndex++;
                    
                    if (time2.get().isUseAtr()) {
                    	/*
                    	 * R4_76
                    	 * R5_69
                    	 * 就業時刻2回目.始業時刻
                    	 */
                    	Integer startTime2 = time2.get().getStart();
                    	cells.get(startIndex, columnIndex).setValue(startTime2 != null ? 
                    			getInDayTimeWithFormat(startTime2) : "");
                    	columnIndex++;
                    	
                    	/*
                    	 * R4_77
                    	 * R5_70
                    	 * 就業時刻2回目.終業時刻
                    	 */
                    	Integer endTime2 = time2.get().getEnd();
                    	cells.get(startIndex, columnIndex).setValue(endTime2 != null ? 
                    			getInDayTimeWithFormat(endTime2) : "");
                    	columnIndex++;
                    } else {
                    	columnIndex += 2;
                    }
                } else {
                    columnIndex += 3;
                }
            } else {
                columnIndex += 3;
            }
        }
        
        // コアタイム時間帯
        if (type == FLEX) {
            /*
             * R6_4
             * 利用
             */
            Integer coreTimeZoneUsage = data.getFlexWorkSetting().getCoreTimeSetting().getTimesheet();
            String useCoreTimeZone = coreTimeZoneUsage.equals(ApplyAtr.USE.value) ? ApplyAtr.USE.nameId : ApplyAtr.NOT_USE.nameId;
            cells.get(startIndex, columnIndex).setValue(useCoreTimeZone);
            columnIndex++;
            
            if (coreTimeZoneUsage.equals(ApplyAtr.USE.value)) {
                /*
                 * R6_77
                 * 始業時刻
                 */
                Integer coreTimeStart = data.getFlexWorkSetting().getCoreTimeSetting().getCoreTimeSheet().getStartTime();
                cells.get(startIndex, columnIndex).setValue(coreTimeStart != null ? getInDayTimeWithFormat(coreTimeStart) : "");
                columnIndex++;
                
                /*
                 * R6_78
                 * 終業時刻
                 */
                Integer coreTimeEnd = data.getFlexWorkSetting().getCoreTimeSetting().getCoreTimeSheet().getEndTime();
                cells.get(startIndex, columnIndex).setValue(coreTimeEnd != null ? getInDayTimeWithFormat(coreTimeEnd) : "");
                columnIndex++;
                
                /*
                 * R6_258
                 * コアタイム内と外の外出時間を分けて集計する
                 */
                Integer aggregateTime = data.getFlexWorkSetting().getCoreTimeSetting().getGoOutCalc().getEspecialCalc();
                cells.get(startIndex, columnIndex).setValue(aggregateTime == 1 ? "○" : "-");
                columnIndex++;
                
                /*
                 * R6_259
                 * コアタイム内の外出時間を就業時間から控除する
                 */
                Integer deductTime = data.getFlexWorkSetting().getCoreTimeSetting().getGoOutCalc().getRemoveFromWorkTime();
                cells.get(startIndex, columnIndex).setValue(deductTime == 1 ? "○" : "-");
                columnIndex++;
            } else {
                columnIndex += 4;
            }
           
            if (coreTimeZoneUsage.equals(ApplyAtr.NOT_USE.value)) {
                /*
                 * R6_79
                 * 最低勤務時間
                 */
                Integer minWorkTime = data.getFlexWorkSetting().getCoreTimeSetting().getMinWorkTime();
                cells.get(startIndex, columnIndex).setValue(getInDayTimeWithFormat(minWorkTime));
            }
            columnIndex++;
        }
        
        /*
         * R4_78
         * R5_71
         * R6_80
         * 半日勤務.前半終了時刻
         */
        Integer firstHalfEnd = data.getPredseting().getPrescribedTimezoneSetting().getMorningEndTime();
        String firstHalfEndString = firstHalfEnd != null ? getInDayTimeWithFormat(firstHalfEnd) : "";
        cells.get(startIndex, columnIndex).setValue(firstHalfEndString);
        columnIndex++;
        
        /*
         * R4_79
         * R5_72
         * R6_81
         * 半日勤務.後半開始時刻
         */
        Integer secondHalfStart = data.getPredseting().getPrescribedTimezoneSetting().getAfternoonStartTime();
        String secondHalfStartString = secondHalfStart != null ? getInDayTimeWithFormat(secondHalfStart) : "";
        cells.get(startIndex, columnIndex).setValue(secondHalfStartString);
        columnIndex++;
        
        /*
         * R4_80
         * R5_73
         * R6_82
         * 所定時間.1日
         */
        Integer oneDayPredTime = data.getPredseting().getPredTime().getPredTime().getOneDay();
        cells.get(startIndex, columnIndex).setValue(oneDayPredTime != null ? getInDayTimeWithFormat(oneDayPredTime) : "");
        columnIndex++;
        
        /*
         * R4_81
         * R5_74
         * R6_83
         * 所定時間.午前
         */
        Integer morningPredTime = data.getPredseting().getPredTime().getPredTime().getMorning();
        cells.get(startIndex, columnIndex).setValue(morningPredTime != null ? getInDayTimeWithFormat(morningPredTime) : "");
        columnIndex++;
        
        /*
         * R4_82
         * R5_75
         * R6_84
         * 所定時間.午後
         */
        Integer afternoonPredTime = data.getPredseting().getPredTime().getPredTime().getAfternoon();
        cells.get(startIndex, columnIndex).setValue(afternoonPredTime != null ? getInDayTimeWithFormat(afternoonPredTime) : "");
        columnIndex++;
        
        if (displayMode.equals(DisplayMode.DETAIL.value)) {
            /*
             * R4_83
             * R5_76
             * R6_85
             * 休暇取得時加算時間.1日
             */
            Integer oneDayAddTime = data.getPredseting().getPredTime().getAddTime().getOneDay();
            cells.get(startIndex, columnIndex).setValue(oneDayAddTime != null ? getInDayTimeWithFormat(oneDayAddTime) : "");
            columnIndex++;
            
            /*
             * R4_84
             * R5_77
             * R6_86
             * 休暇取得時加算時間.午前
             */
            Integer morningAddTime = data.getPredseting().getPredTime().getAddTime().getMorning();
            cells.get(startIndex, columnIndex).setValue(morningAddTime != null ? getInDayTimeWithFormat(morningAddTime) : "");
            columnIndex++;
            
            /*
             * R4_85
             * R5_78
             * R6_87
             * 休暇取得時加算時間.午後
             */
            Integer afternoonAddTime = data.getPredseting().getPredTime().getAddTime().getAfternoon();
            cells.get(startIndex, columnIndex).setValue(afternoonAddTime != null ? getInDayTimeWithFormat(afternoonAddTime) : "");
            columnIndex++;
        }
    }
    
    /**
     * @param time
     * @return
     */
    private static String getInDayTimeWithFormat(Integer time) {
        if (time == null) {
            return "";
        }
        return new TimeWithDayAttr(time).getRawTimeWithFormat();
    }
    
    /**
     * Get Rounding Text by value
     * 切り捨て = 0
     * 切り上げ = 1
     * @param rounding
     * @return
     */
    private static String getRoundingEnum(Integer rounding) {
        if (rounding == null) {
            return "";
        }
        if (rounding == 0) {
            return "切り捨て";
        }
        if (rounding == 1) {
            return "切り上げ";
        }
        
        return "";
    }
    
    /**
     * 積残順序
     * @param settlementOrder
     * @return
     */
    private static String getSetlementEnum(Integer settlementOrder) {
        if (settlementOrder == null) {
            return "";
        }
        
        String[] settlementOrderAtr = {"１", "２", "３", "４", "５", "６", "７", "８", "９", "１０"};
        for (int i = 1; i <= settlementOrderAtr.length; i++) {
            if (settlementOrder == i) {
                return settlementOrderAtr[i - 1];
            }
        }
        
        return "";
    }
    
    /**
     * 時刻丸め単位
     * @param roundingTimeUnit
     * @return
     */
    private static String getRoundingTimeUnitEnum(Integer roundingTimeUnit) {
        if (roundingTimeUnit == null) {
            return "";
        }
        
        String[] roundingTimeUnitAtr = {"1分", "5分", "6分", "10分", "15分", "20分", "30分", "60分"};
        for (int i = 0; i < roundingTimeUnitAtr.length; i++) {
            if (roundingTimeUnit == i) {
                return roundingTimeUnitAtr[i];
            }
        }
        
        return "";
    }
    
    /**
     * 優先設定
     * @param priority
     * @return
     */
    private static String getPrioritySetAtr(Integer priority) {
        if (priority == null) {
            return "";
        }
        
        String[] priorityAtr = {"最初", "最後"};
        for (int i = 0; i < priorityAtr.length; i++) {
            if (priority == i) {
                return priorityAtr[i];
            }
        }
        
        return "";
    }
    
    /**
     * 固定勤務の休憩設定.休憩時間中に退勤した場合の計算方法
     * @param calculatedMethod
     * @return
     */
    private static String getCalculatedMethodAtr(Integer calculatedMethod) {
        if (calculatedMethod == null) {
            return "";
        }
        
        String[] calculatedMethodAtr = { "退勤以降も含め休憩時間を全て計上する", "休憩を計算しない", "退勤までの休憩時間を計算する(丸めを適用する)"};
        for (int i = 0; i < calculatedMethodAtr.length; i++) {
            if (calculatedMethod == i) {
                return calculatedMethodAtr[i];
            }
        }
        
        return "";
    }
    
    /**
     * 外出丸め設定.枠を跨る場合の丸め設定
     * @param frameRoundingAtr
     * @return
     */
    private static String getFrameRoundingAtr(Integer frameRoundingAtr) {
        if (frameRoundingAtr == null) {
            return "";
        }
        
        String[] frameRoundings = {"合算した後に丸める", "休憩枠毎に丸める"};
        for (int i = 0; i < frameRoundings.length; i++) {
            if (frameRoundingAtr == i) {
                return frameRoundings[i];
            }
        }
        
        return "";
    }
    
    /**
     * 私用・組合外出時間.就業時間帯
     * @param approTimeRoundingAtr
     * @return
     */
    private static String getApproTimeRoundingAtr(Integer approTimeRoundingAtr) {
        if (approTimeRoundingAtr == null) {
            return "";
        }
        
        String[] approTimeRoundings = {"時間帯の丸めを逆に適用する", "丸めを設定する"};
        for (int i = 0; i < approTimeRoundings.length; i++) {
            if (approTimeRoundingAtr == i) {
                return approTimeRoundings[i];
            }
        }
        
        return "";
    }
    
    /**
     * するしない区分
     * @param useAtr
     * @return
     */
    private static String getUseAtrByInteger(Integer useAtr) {
        if (useAtr == null) {
            return "";
        }
        
        String[] uses = {"しない", "する"};
        for (int i = 0; i < uses.length; i++) {
            if (useAtr == i) {
                return uses[i];
            }
        }
        
        return "";
    }
    
    /**
     * するしない区分
     * @param useAtr
     * @return
     */
    private static String getUseAtrByBoolean(Boolean useAtr) {
        if (useAtr == null) {
            return "";
        }
        
        String[] uses = {"しない", "する"};
        if (useAtr) {
            return uses[1];
        } else {
            return uses[0];
        }
    }
    
    /**
     * 実績での休憩計算方法
     * @param calculateMethod
     * @return
     */
    private static String getCalculateMethodFlowRest(Integer calculateMethod) {
        if (calculateMethod == null) {
            return "";
        }
        
        String[] calculateMethods = {"マスタを参照する", "参照せずに打刻する"};
        for (int i = 0; i < calculateMethods.length; i++) {
            if (calculateMethod == i) {
                return calculateMethods[i];
            }
        }
        
        return "";
    }
}
