package nts.uk.ctx.at.function.dom.holidaysremaining;

import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.at.function.dom.monthlyworkschedule.ItemSelectionEnum;

import java.util.Optional;

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
	 * 出力する項目一覧  -帳表に出力する項目
	 */
	private ItemOutputForm listItemsOutput;

	/**
	 * 出力レイアウトID
	 * GUID
	 */
	private String layOutId;

	/**
	 * 項目選択区分
	 */
	private ItemSelectionEnum itemSelectionCategory;

	/**
	 *
	 */
	private Optional<String> employeeId;

	public HolidaysRemainingManagement(String companyID,  String code, String name, ItemOutputForm listItemsOutput,
									   String layOutId,ItemSelectionEnum itemSelectionCategory,Optional<String> employeeId) {
		super();
		this.name = new HolidayRemainingName(name);
		this.companyID = companyID;
		this.code = new HolidayRemainingCode(code);
		this.listItemsOutput = listItemsOutput;
		this.layOutId = layOutId;
		this.itemSelectionCategory = itemSelectionCategory;
		this.employeeId = employeeId;
	}

}
