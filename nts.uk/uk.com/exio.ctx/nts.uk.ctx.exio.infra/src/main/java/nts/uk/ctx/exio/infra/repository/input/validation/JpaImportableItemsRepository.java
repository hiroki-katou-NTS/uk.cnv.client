package nts.uk.ctx.exio.infra.repository.input.validation;

import java.util.Optional;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.uk.ctx.exio.dom.input.importableitem.ImportableItem;
import nts.uk.ctx.exio.dom.input.importableitem.ImportableItemsRepository;
import nts.uk.ctx.exio.dom.input.importableitem.group.ImportingGroupId;
import nts.uk.ctx.exio.infra.entity.input.validation.XimctImportableItem;

public class JpaImportableItemsRepository extends JpaRepository implements ImportableItemsRepository{

	@Override
	public Optional<ImportableItem> find(ImportingGroupId groupId, int itemNo) {
		
		String sql = "select * from XIMCT_IMPORTABLE_ITEM"
						+ " where GROUP_ID = @group"
						+ " and ITEM_NO = @item";
		
		return new NtsStatement(sql, this.jdbcProxy())
				.paramInt("group", groupId.value)
				.paramInt("item", itemNo)
				.getSingle(rec -> XimctImportableItem.MAPPER.toEntity(rec).toDomain());
	}

}
