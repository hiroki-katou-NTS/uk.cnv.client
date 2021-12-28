package nts.uk.ctx.exio.app.find.exo.item;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Value;
import nts.uk.ctx.exio.dom.exo.categoryitemdata.CtgItemData;
import nts.uk.ctx.exio.dom.exo.outputitem.StandardOutputItem;
import nts.uk.ctx.exio.dom.exo.outputitemorder.StandardOutputItemOrder;

/**
 * 出力項目(定型)
 */
@Value
public class StdOutItemDto {

	/**
	 * 会社ID
	 */
	private String cid;

	/**
	 * 出力項目コード
	 */
	private String outItemCd;

	/**
	 * 条件設定コード
	 */
	private String condSetCd;

	/**
	 * 出力項目名
	 */
	private String outItemName;

	/**
	 * 項目型
	 */
	private int itemType;

	/**
	 * カテゴリ項目
	 */
	private List<CategoryItemDto> categoryItems;
	
	private int displayOrder;

	public static StdOutItemDto fromDomain(List<StandardOutputItemOrder> listStdOutputItemOrder, StandardOutputItem domain) {
	    Optional<StandardOutputItemOrder> stdOutputItemOrder = listStdOutputItemOrder.stream().
	            filter(x -> x.getConditionSettingCode().v() == domain.getConditionSettingCode().v() 
	            && x.getOutputItemCode().v() == domain.getOutputItemCode().v()).findFirst();
		return new StdOutItemDto(
		        domain.getCid(), 
		        domain.getOutputItemCode().v(), 
		        domain.getConditionSettingCode().v(),
				domain.getOutputItemName().v(), domain.getItemType().value,
				stdOutputItemOrder.isPresent() ? stdOutputItemOrder.get().getDisplayOrder() : 0,
				domain.getCategoryItems().stream().map(item -> {
					return CategoryItemDto.fromDomain(item);
				}).collect(Collectors.toList()));
	}
	
	public static StdOutItemDto fromDomain(StandardOutputItem domain, List<CtgItemData> ctgItemDataDomain) {
		return new StdOutItemDto(domain.getCid(), domain.getOutputItemCode().v(), domain.getConditionSettingCode().v(),
				domain.getOutputItemName().v(), domain.getItemType().value, 0,
				domain.getCategoryItems().stream().map(item -> {
					String categoryItemName = "";
					Optional<CtgItemData> ctgItemData = ctgItemDataDomain.stream()
							.filter(x -> x.getCategoryId().v().intValue() == item.getCategoryId().v().intValue()
									&& x.getItemNo().v().equals(item.getItemNo().v()))
							.findFirst();
					if (ctgItemData.isPresent()) {
						categoryItemName = ctgItemData.get().getItemName().v();
					}
					return CategoryItemDto.fromDomain(item, categoryItemName);
				}).collect(Collectors.toList()));
	}
	
	public StdOutItemDto(String cid, String outItemCd, String condSetCd, String outItemName, int itemType, int displayOrder,
            List<CategoryItemDto> categoryItems ) {
        super();
        this.cid = cid;
        this.outItemCd = outItemCd;
        this.condSetCd = condSetCd;
        this.outItemName = outItemName;
        this.itemType = itemType;
        this.displayOrder = displayOrder;
        this.categoryItems = categoryItems;
        
    }


}
