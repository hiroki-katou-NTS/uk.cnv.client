package nts.uk.screen.at.app.ksu001.displayinworkinformation;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.DateInMonth;
import nts.uk.screen.at.app.ksu001.getinfoofInitstartup.TargetOrgIdenInforDto;
/**
 * 
 * @author hoangnd
 *
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DisplayInWorkInfoParam_New {
	public List<String> listSid;             // ・社員IDリスト：List<社員ID>
	public GeneralDate startDate;            // ・期間
	public GeneralDate endDate;    	         // ・期間
	public boolean getActualData;            // ・実績も取得するか：boolean : có lấy data thực tế không A4_7
	
	
//	・締め日：日付
	public DateInMonth day;
	
//	・対象組織：対象組織識別情報s
	public TargetOrgIdenInforDto targetOrgIdenInforDto;
	
	
//	・集計したい個人計：Optional<個人計カテゴリ>
	public Integer personalCounterOp;
	
//	・集計したい職場計：Optional<職場計カテゴリ>
	
	public Integer workplaceCounterOp;

}
