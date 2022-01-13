package nts.uk.ctx.sys.log.app.find.reference;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.gul.text.StringUtil;
import nts.uk.ctx.sys.log.dom.reference.LogOutputItemRepository;

/*
 * author : hiep.th
 */

@Stateless
public class LogOuputItemFinder {

	@Inject
	private LogOutputItemRepository logOutputItemRepository;

	public List<LogOutputItemDto> getLogOutputItemByRecordType(String recordType) {
		if (StringUtil.isNullOrEmpty(recordType, true)) {
			return Collections.emptyList();
		}
		return this.logOutputItemRepository.getByRecordType(Integer.valueOf(recordType)).stream()
				.map(s -> LogOutputItemDto.fromDomain(s)).collect(Collectors.toList());
	}

	public List<LogOutputItemDto> getLogOutputItemByItemNosAndRecordType(List<String> itemNos, int recordType) {
		List<LogOutputItemDto> listtemp = this.logOutputItemRepository.getByItemNosAndRecordType(itemNos, recordType)
				.stream().map(s -> LogOutputItemDto.fromDomain(s)).collect(Collectors.toList());
		// re-sort list
		Comparator<LogOutputItemDto> sortByRecordType = (p, o) -> Integer.compare(p.getRecordType(), o.getRecordType());
		Comparator<LogOutputItemDto> sortBySortOrder = (p, o) -> Integer.compare(p.getSortOrder(), o.getSortOrder());
		return listtemp.stream().sorted(sortByRecordType.thenComparing(sortBySortOrder)).collect(Collectors.toList());
	}
	
	public List<LogOutputItemDto> getLogOutputItemByItemNosAndRecordTypeAll(List<String> itemNos, int recordType) {
		List<LogOutputItemDto> listtemp = this.logOutputItemRepository.getByItemNosAndRecordType(itemNos, recordType)
				.stream().map(s -> LogOutputItemDto.fromDomainAll(s)).collect(Collectors.toList());
		// re-sort list
		Comparator<LogOutputItemDto> sortByRecordType = (p, o) -> Integer.compare(p.getRecordType(), o.getRecordType());
		Comparator<LogOutputItemDto> sortBySortOrder = (p, o) -> Integer.compare(p.getSortOrder(), o.getSortOrder());
		return listtemp.stream().sorted(sortByRecordType.thenComparing(sortBySortOrder)).collect(Collectors.toList());
	}
}
