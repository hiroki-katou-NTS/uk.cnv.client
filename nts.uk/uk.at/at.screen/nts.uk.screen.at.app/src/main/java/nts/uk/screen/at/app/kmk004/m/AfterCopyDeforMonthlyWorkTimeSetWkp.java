package nts.uk.screen.at.app.kmk004.m;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.monunit.MonthlyWorkTimeSet.LaborWorkTypeAttr;
import nts.uk.screen.at.app.query.kmk004.common.WorkplaceList;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KMK_計算マスタ.KMK004_法定労働時間の登録（New）.M：職場別法定労働時間の登録（変形労働）.メニュー別OCD.職場別月単位労働時間（変形労働）を複写した時
 * 
 * @author tutt
 *
 */
@Stateless
public class AfterCopyDeforMonthlyWorkTimeSetWkp {

	@Inject
	private WorkplaceList workplaceList;
	
	public List<String> afterCopyDeforMonthlyWorkTimeSetWkp() {
		
		// 職場リストを表示する
		return this.workplaceList.get(LaborWorkTypeAttr.DEFOR_LABOR).stream().map(x -> x.workplaceId)
				.collect(Collectors.toList());
	}
}
