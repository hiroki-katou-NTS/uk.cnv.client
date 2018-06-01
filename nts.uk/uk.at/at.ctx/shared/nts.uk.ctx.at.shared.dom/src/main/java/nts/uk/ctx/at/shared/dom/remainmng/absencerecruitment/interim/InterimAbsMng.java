package nts.uk.ctx.at.shared.dom.remainmng.absencerecruitment.interim;


import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.at.shared.dom.remainmng.interimremain.primitive.RequiredDay;
import nts.uk.ctx.at.shared.dom.remainmng.interimremain.primitive.UnOffsetDay;
/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.残数管理.暫定残数管理
 * 暫定振休管理データ
 * @author do_dt
 *
 */
@Getter
@AllArgsConstructor
public class InterimAbsMng extends AggregateRoot{
	/**	暫定振休管理データID */
	private String absenceMngId;
	/**	必要日数 */
	private RequiredDay requeiredDays;
	/**	未相殺日数 */
	private UnOffsetDay unOffsetDays;
}
