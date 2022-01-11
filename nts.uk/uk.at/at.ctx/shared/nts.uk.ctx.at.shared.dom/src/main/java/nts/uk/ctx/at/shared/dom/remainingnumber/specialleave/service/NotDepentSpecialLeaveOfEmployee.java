package nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.service;

import java.util.List;
import java.util.Map;


public interface NotDepentSpecialLeaveOfEmployee {
	/**
	 * RequestList501: 社員に依存しない特別休暇情報を取得する
	 * @param param
	 * @return
	 */
	InforSpecialLeaveOfEmployee getNotDepentInfoSpecialLeave(NotDepentSpecialLeaveOfEmployeeInputExtend param);

	/**
	 * đối ứng cps003
	 * RequestList501: 社員に依存しない特別休暇情報を取得する
	 * @param param
	 * @return
	 */
	Map<String, InforSpecialLeaveOfEmployee> getNotDepentInfoSpecialLeave(List<NotDepentSpecialLeaveOfEmployeeInputExtend> param);

}
