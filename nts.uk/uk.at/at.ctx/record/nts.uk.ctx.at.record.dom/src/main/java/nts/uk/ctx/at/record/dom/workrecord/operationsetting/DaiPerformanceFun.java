package nts.uk.ctx.at.record.dom.workrecord.operationsetting;

import lombok.AllArgsConstructor;
import lombok.Getter;
//import lombok.Setter;
//import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.dom.AggregateRoot;
//import nts.arc.time.GeneralDate;
//import nts.arc.time.GeneralDateTime;

/**
* 日別実績の修正の機能
*/
@AllArgsConstructor
@Getter
public class DaiPerformanceFun extends AggregateRoot
{
    
    /**
    * 会社ID
    */
    private String cid;
    
    /**
     * コメント Y0
     */
    private Comment comment;
    
    /**
    * 36協定情報を表示する Y
    */
    private int disp36Atr;

    /**
    * フレックス勤務者のフレックス不足情報を表示する Y
    */
    private int flexDispAtr;
    
    /**
     * 抽出時にエラーがある場合はエラー参照ダイアログを表示する
     * 
     */
    private int checkErrRefDisp;
     
//    /**
//    * 休出計算区分を変更する場合、休出深夜計算区分を変更する Y
//    */
//    private int breakCalcUpdAtr;
    
//    /**
//    * 休憩時刻を自動で設定する Y
//    */
//    private int breakTimeAutoAtr;
    
//    /**
//    * 早出計算区分を変更する場合、早出残業深夜計算区分を変更する Y
//    */
//    private int ealyCalcUpdAtr;
    
//    /**
//    * 残業計算区分を変更する場合、残業深夜区分を変更する Y
//    */
//    private int overtimeCalcUpdAtr;
    
//    /**
//    * 法定内残業計算区分を変更する場合、法定内深夜残業計算区分を変更する Y
//    */
//    private int lawOverCalcUpdAtr;
    
//    /**
//    * 自動で設定した内容は手修正にする Y
//    */
//    private int manualFixAutoSetAtr;
    
//	  /**
//	  * クリアした内容は手修正にする Y
//	  */
//	  private int clearManuAtr;
    
//	  /**
//	  * 1ヵ月の確認・承認が完了した場合、メッセージを表示する Y
//	  */
//	  private int monthChkMsgAtr;
    
    public static DaiPerformanceFun createFromJavaType(String cid, String comment, 
    		int isDisplayAgreementThirtySix, int isDisplayFlexWorker, int checkErrRefDisp) {
        DaiPerformanceFun  daiPerformanceFun = 
        		new DaiPerformanceFun(cid, 
        				new Comment(comment),
        				isDisplayAgreementThirtySix, 
        				isDisplayFlexWorker, 
        				checkErrRefDisp);
        return daiPerformanceFun;
    }
    
}
