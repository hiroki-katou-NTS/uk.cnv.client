/**
 * 
 */
package nts.uk.screen.at.app.ksu001.changeworkplace;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.screen.at.app.ksu001.displayinshift.ShiftMasterMapWithWorkStyle;

/**
 * @author laitv
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ChangeWorkPlaceParam {
	
	public String wkpId;                     // ・wkpid || wkpGrId
	public String startDate;            	 // ・期間
	public String endDate;    	        	 // ・期間
	public List<ShiftMasterMapWithWorkStyle> listShiftMasterNotNeedGetNew; // ・新たに取得する必要のないシフト一覧：List<シフトマスタ> // chưa có data ở localstorage thì se emplty
	public int shiftPalletUnit;              // Company | workPlace
	public int pageNumberCom;  
	public int pageNumberOrg;
	public boolean getActualData;            // ・実績も取得するか：boolean : có lấy data thực tế không
	public String viewMode;                  // 1 : shift, 2 : detail, 3 : working 
	public int unit;					     // workPlace | workPlaceGroup
	public String personTotalSelected; // 個人計選択 A11_1
	public String workplaceSelected;   // 職場計選択A12_1
	public int day; // 締め日
	public boolean isLastDay;// 締め日
	
}
