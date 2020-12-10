package nts.uk.screen.at.app.kmk004.i;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.monunit.MonthlyWorkTimeSet.LaborWorkTypeAttr;
import nts.uk.screen.at.app.query.kmk004.common.YearDto;
import nts.uk.screen.at.app.query.kmk004.common.YearListByEmployment;

/**
 * 
 * @author sonnlb
 *
 *         UKDesign.UniversalK.就業.KDW_日別実績.KMK_計算マスタ.KMK004_法定労働時間の登録（New）.I：雇用別法定労働時間の登録（フレックス勤務）.メニュー別OCD.雇用別年度（フレックス勤務）を選択する
 */
@Stateless
public class SelectFlexYearByEmployment {

	@Inject
	private YearListByEmployment yearListByEmployment;

	public List<YearDto> selectFlexYearByEmployment(String employmentCd) {

		return this.yearListByEmployment.get(employmentCd, LaborWorkTypeAttr.FLEX);
	}

}
