package nts.uk.shr.pereg.app.find;

import java.util.List;

import nts.uk.shr.pereg.app.find.dto.OptionalItemDataDto;

public interface PeregPerOptRepository {
	/**
	 * get optional data of person category type by record id 
	 * 
	 * @param recordId
	 * @return
	 */
	List<OptionalItemDataDto> getData(String recordId);
}
