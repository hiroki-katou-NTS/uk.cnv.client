package nts.uk.ctx.exio.dom.input.importableitem;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainAggregate;
import nts.uk.ctx.exio.dom.input.DataItem;

/**
 * 受入可能項目
 */
@Getter
@AllArgsConstructor
public class ImportableItem implements DomainAggregate{

	private int groupId;
	private int itemNo;
	private ItemType itemType;
	private boolean required;
	private Optional<DomainConstraint> domainConstraint;
	
	public boolean validate(DataItem dataItem) {
		if(required && dataItem.getValue() == null) {
			return false;
		}
		
		return domainConstraint.map(constraint -> constraint.validate(dataItem))
				//↓はそもそも制限が無いという意図.
				.orElse(true);
	}
}
