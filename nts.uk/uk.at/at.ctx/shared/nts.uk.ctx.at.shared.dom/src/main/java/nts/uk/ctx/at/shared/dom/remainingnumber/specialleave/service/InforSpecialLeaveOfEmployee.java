package nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.service;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * 社員の特別休暇情報を取得するのOuput値
 * @author do_dt
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InforSpecialLeaveOfEmployee {
	/**
	 * ・状態：付与あり or 付与なし
	 */
	private InforStatus status;
	/**
	 * 繰越上限日数 (OR 蓄積上限日数)：日数
	 */
	private Optional<Integer> upLimiDays;	
	/**
	 * 特別休暇情報一覧
	 */
	private List<SpecialHolidayInfor> speHolidayInfor;

}
