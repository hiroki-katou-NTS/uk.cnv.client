package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainAggregate;
import nts.arc.time.GeneralDateTime;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;

/**
 * @author ThanhNX
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.勤務実績.打刻管理.打刻.打刻記録
 *         打刻記録
 */
@AllArgsConstructor
public class StampRecord implements DomainAggregate {
	
	/**
	 * 打刻記録ID // GUID
	 */
	@Getter
	private final String stampRecordId;
	
	/**
	 * 	契約コード
	 */
	@Getter
	private final ContractCode contractCode;
	/**
	 * 打刻カード番号
	 */
	@Getter
	private final StampNumber stampNumber;

	/**
	 * 打刻日時
	 */
	@Getter
	private final GeneralDateTime stampDateTime;
	
	/**
	 * 	表示する打刻区分
	 */
	@Getter
	private final StampTypeDisplay stampTypeDisplay;

	public String retriveKey() {
		return this.getStampNumber()+ this.getStampDateTime().toString();
	}
	
	/**
	 * 	[C-1] 新規作成
	 * @param contractCode
	 * @param stampNumber
	 * @param stampDateTime
	 * @param stampTypeDisplay
	 */
	public StampRecord (ContractCode contractCode, StampNumber stampNumber, GeneralDateTime stampDateTime,
			StampTypeDisplay stampTypeDisplay) {
		super();
		//	新しいGUIDを作成する
		this.stampRecordId = IdentifierUtil.randomUniqueId();
		this.contractCode = contractCode;
		this.stampNumber = stampNumber;
		this.stampDateTime = stampDateTime;
		this.stampTypeDisplay = stampTypeDisplay;
		
	}
}
