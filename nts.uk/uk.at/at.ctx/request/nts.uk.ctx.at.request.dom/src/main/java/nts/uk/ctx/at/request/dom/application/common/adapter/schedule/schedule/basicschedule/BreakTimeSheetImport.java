package nts.uk.ctx.at.request.dom.application.common.adapter.schedule.schedule.basicschedule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BreakTimeSheetImport {

  //休憩枠NO BreakFrameNo 
    private Integer breakFrameNo;
    
    //開始 - 勤怠打刻(実打刻付き)
    @Setter
    private Integer startTime;
    
    //終了 - 勤怠打刻(実打刻付き)
    @Setter
    private Integer endTime;
    
    /** 休憩時間: 勤怠時間 */
    private Integer breakTime;
}
