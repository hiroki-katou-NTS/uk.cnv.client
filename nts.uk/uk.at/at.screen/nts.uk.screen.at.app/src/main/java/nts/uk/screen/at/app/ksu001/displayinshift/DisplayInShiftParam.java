package nts.uk.screen.at.app.ksu001.displayinshift;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.DateInMonth;
import nts.uk.screen.at.app.ksu001.start.ShiftPaletteWantGet;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class DisplayInShiftParam {
	public List<String> listSid;             // ・社員IDリスト：List<社員ID>
	public GeneralDate startDate;            // ・期間
	public GeneralDate endDate;    	         // ・期間
	public String workplaceId;     	         // ・対象組織：対象組織識別情報
	public String workplaceGroupId; 	     // ・対象組織：対象組織識別情報
	/**
	 *  ・新たに取得する必要のないシフト一覧：List<シフトマスタ> | 
	 *  danh sách shiftmaster không cần lấy mới. 
	 *  trong lần đầu đăng nhập thì list này sẽ empty
	 */
	public List<ShiftMasterMapWithWorkStyle> listShiftMasterNotNeedGetNew; 
	// thông tin của page cần lấy data
	public ShiftPaletteWantGet shiftPaletteWantGet; // ・取得したいシフトパレット：Optional<単位, ページ>  単位
	public boolean getActualData;            // ・実績も取得するか：boolean : có lấy data thực tế không
	public int unit;					     // workPlace | workPlaceGroup
	
	// ・集計したい個人計：Optional<個人計カテゴリ>
	public Integer personalCounterOp;
	
	// ・集計したい職場計：Optional<職場計カテゴリ>
	
	public Integer workplaceCounterOp;
	
	// 締め日
	public DateInMonth day;
	
}
