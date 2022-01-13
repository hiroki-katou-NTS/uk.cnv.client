package nts.uk.screen.at.app.kdw006.k;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * ScreenQuery: 作業補足情報の選択項目を取得する
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW006_前準備.K：任意作業コード設定.メニュー別OCD.作業補足情報の選択項目を取得する
 * @author chungnt
 *
 */

@Stateless
public class WorkInfomations {
	
	@Inject
	private GetManHourRecordItemSpecifiedIDList getManHourRecordItemSpecifiedIDList;

	public List<GetManHourRecordItemSpecifiedIDListDto> get(GetManHourRecordItemSpecifiedIDListParam param) {
		
		return getManHourRecordItemSpecifiedIDList.get(param);
	}
	
}
