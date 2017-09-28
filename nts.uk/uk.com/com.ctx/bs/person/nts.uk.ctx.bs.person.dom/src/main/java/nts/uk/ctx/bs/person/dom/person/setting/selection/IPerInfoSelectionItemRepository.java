package nts.uk.ctx.bs.person.dom.person.setting.selection;

import java.util.List;
import java.util.Optional;

public interface IPerInfoSelectionItemRepository {
	
	void add(PerInfoSelectionItem perInfoSelectionItem);
	
	void update(PerInfoSelectionItem perInfoSelectionItem);
	
	void remove(String selectionItemId);
	
	List<PerInfoSelectionItem> getAllPerInfoSelectionItem(String contractCd);
	
	Optional<PerInfoSelectionItem> getPerInfoSelectionItem(String selectionItemId);
	
	boolean checkExist(String selectionItemId);

	Optional<PerInfoSelectionItem>  checkItemName(String selectionItemName);// check trung name!

	Optional<PerInfoSelectionItem> checkItemClassification(String selectionItemClassification);// check selectionItemClassification
	
}
