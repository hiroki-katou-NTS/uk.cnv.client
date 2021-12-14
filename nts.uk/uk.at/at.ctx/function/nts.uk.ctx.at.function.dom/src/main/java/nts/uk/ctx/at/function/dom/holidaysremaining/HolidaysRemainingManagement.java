package nts.uk.ctx.at.function.dom.holidaysremaining;

import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;

/**
 * @author thanh.tq 休暇残数管理表の出力項目設定
 */
@Getter
public class HolidaysRemainingManagement extends AggregateRoot {
	/**
	 * 会社ID
	 */
	private String companyID;
	
	/**
	 * コード
	 */
	private HolidayRemainingCode code;
	/**
	 * 名称
	 */
	private HolidayRemainingName name;

	/**
	 * 出力する項目一覧
	 */
	private ItemOutputForm listItemsOutput;

	public HolidaysRemainingManagement(String companyID,  String code, String name, ItemOutputForm listItemsOutput) {
		super();
		this.name = new HolidayRemainingName(name);
		this.companyID = companyID;
		this.code = new HolidayRemainingCode(code);
		this.listItemsOutput = listItemsOutput;
	}
	
	

}
