package nts.uk.ctx.exio.dom.input.importableitem;

import java.util.Optional;

import nts.uk.ctx.exio.dom.input.importableitem.group.ImportingGroupId;

public interface ImportableItemsRepository {
	
	Optional<ImportableItem> find(ImportingGroupId groupId, int itemNo);
}
