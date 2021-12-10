package nts.uk.ctx.at.shared.dom.remainingnumber.work;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.CreateAtr;

/**
 * 振替時間情報
 * @author do_dt
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TranferTimeInfor {
	/**	作成元区分 */
	private CreateAtr createAtr;
	/**	振替時間 */
	private int tranferTime;
	/**	日数 */
	private Optional<Double> days;

}
