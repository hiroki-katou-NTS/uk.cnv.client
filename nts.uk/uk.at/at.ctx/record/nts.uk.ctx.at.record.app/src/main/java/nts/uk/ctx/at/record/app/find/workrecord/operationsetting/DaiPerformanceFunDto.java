package nts.uk.ctx.at.record.app.find.workrecord.operationsetting;

import lombok.AllArgsConstructor;
import lombok.Value;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.DaiPerformanceFun;

/**
* 日別実績の修正の機能
*/
@AllArgsConstructor
@Value
public class DaiPerformanceFunDto
{
    
    /**
    * 会社ID
    */
    private String cid;
    
    /**
    * コメント
    */
    private String comment;
    
    /**
    * 36協定情報を表示する
    */
    private int disp36Atr;
    
    /**
    * フレックス勤務者のフレックス不足情報を表示する
    */
    private int flexDispAtr;
    
    /**
     * 抽出時にエラーがある場合はエラー参照ダイアログを表示する
     * 
     */
    private int checkErrRefDisp;
    
    
    public static DaiPerformanceFunDto fromDomain(DaiPerformanceFun domain)
    {
        return new DaiPerformanceFunDto(domain.getCid(), domain.getComment().toString(), 
						        		domain.getDisp36Atr(), 
						        		domain.getFlexDispAtr(), 
						        		domain.getCheckErrRefDisp());
    }
    
}
