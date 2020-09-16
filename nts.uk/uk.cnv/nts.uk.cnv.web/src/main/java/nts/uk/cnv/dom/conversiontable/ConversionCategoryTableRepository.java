package nts.uk.cnv.dom.conversiontable;

import java.util.List;

public interface ConversionCategoryTableRepository {
	List<ConversionCategoryTable> get(String category);

	void delete(String category);

	void regist(ConversionCategoryTable conversionCategory);
}
