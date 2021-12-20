package nts.uk.ctx.exio.dom.input.canonicalize.result;

import static java.util.stream.Collectors.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.exio.dom.input.DataItemList;

@SuppressWarnings("serial")
public class CanonicalItemList extends ArrayList<CanonicalItem> {

	public CanonicalItemList() {
		super();
	}
	
	public CanonicalItemList(Collection<CanonicalItem> items) {
		super(items);
	}
	
	public static CanonicalItemList of(DataItemList list) {
		return list.stream()
				.map(CanonicalItem::of)
				.collect(Collectors.collectingAndThen(toList(), CanonicalItemList::new));
	}
	
	public Optional<CanonicalItem> getItemByNo(int itemNo) {
		return stream()
				.filter(item -> item.getItemNo() == itemNo)
				.findFirst();
	}
	
	public CanonicalItemList addNull(int itemNo) {
		return addItem(CanonicalItem.nullValue(itemNo));
	}

	public CanonicalItemList add(int itemNo, String value) {
		return addItem(CanonicalItem.of(itemNo, value));
	}
	
	public CanonicalItemList add(int itemNo, long value) {
		return addItem(CanonicalItem.of(itemNo, value));
	}
	
	public CanonicalItemList add(int itemNo, BigDecimal value) {
		return addItem(CanonicalItem.of(itemNo, value));
	}
	
	public CanonicalItemList add(int itemNo, GeneralDate value) {
		return addItem(CanonicalItem.of(itemNo, value));
	}
	
	public CanonicalItemList add(int itemNo, GeneralDateTime value) {
		return addItem(CanonicalItem.of(itemNo, value));
	}
	
	public CanonicalItemList addItem(CanonicalItem item) {
		this.add(item);
		return this;
	}
}
