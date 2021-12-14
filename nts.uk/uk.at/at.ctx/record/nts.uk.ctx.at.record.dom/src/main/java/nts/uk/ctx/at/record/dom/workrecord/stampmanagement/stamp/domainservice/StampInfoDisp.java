package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import java.util.Optional;

import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainValue;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;

/**
 * VO : 表示する打刻情報
 * 
 * @author tutk
 *
 */
public class StampInfoDisp implements DomainValue {

	/**
	 * 打刻カード番号
	 */
	@Getter
	private final StampNumber stampNumber;

	/**
	 * 打刻日時
	 */
	@Getter
	private final GeneralDateTime stampDatetime;

	/**
	 * 打刻区分
	 */
	@Getter
	private final String stampAtr;

	/**
	 * 打刻
	 */
	@Getter
	private Optional<Stamp> stamp;

	/**
	 * [C-0] 打刻区分を作成する
	 * 
	 * @param stampNumber
	 *            打刻カード番号
	 * @param stampDatetime
	 *            打刻日時
	 * @param stampTypeDisplay
	 *            表示する打刻区分
	 * @param stamp
	 *            打刻リスト
	 */
	public StampInfoDisp(StampNumber stampNumber, GeneralDateTime stampDatetime, String stampTypeDisplay,
			Optional<Stamp> stamp) {
		super();
		this.stampNumber = stampNumber;
		this.stampDatetime = stampDatetime;
		this.stampAtr = stampTypeDisplay;
		this.stamp = stamp;
	}

}
