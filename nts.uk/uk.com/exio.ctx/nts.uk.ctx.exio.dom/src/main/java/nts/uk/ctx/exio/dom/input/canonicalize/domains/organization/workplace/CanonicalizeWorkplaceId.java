package nts.uk.ctx.exio.dom.input.canonicalize.domains.organization.workplace;

import static java.util.stream.Collectors.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Value;
import lombok.val;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.exio.dom.input.canonicalize.CanonicalItem;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.organization.workplace.WorkplaceCanonicalization.Items;

/**
 * 職場IDを正準化する
 * 既存履歴を含む直前の履歴を参照し、同じ職場コードであれば同じ職場IDを採用する
 */
public class CanonicalizeWorkplaceId {

	public static List<RecordWithPeriod> canonicalize(
			List<RecordWithPeriod> records,
			ExistingWorkplaceHistory existingHistory) {
		
		if (records.isEmpty()) {
			return Collections.emptyList();
		}
		
		CodeToIdMap idMap = existingHistory.getLastIdMap();
		
		List<HistoryItem> sortedHistoryItems = records.stream()
				.collect(Collectors.groupingBy(r -> r.period))
				.entrySet()
				.stream()
				.map(e -> new HistoryItem(e.getKey(), e.getValue()))
				.sorted(Comparator.comparing(h -> h.period.start()))
				.collect(toList());
		
		CodeToIdMap currentIdMap = idMap;
		for (val historyItem : sortedHistoryItems) {
			currentIdMap = historyItem.canonicalize(currentIdMap);
		}
		
		return sortedHistoryItems.stream()
				.flatMap(h -> h.records.stream())
				.collect(toList());
	}
	
	@Value
	private static class HistoryItem {
		
		DatePeriod period;
		
		List<RecordWithPeriod> records;
		
		/**
		 * 前の履歴のIdMapを参照しつつ職場IDを生成し、次の履歴のために新しいIdMapを返す
		 * @param idMapPrevHistory
		 * @return
		 */
		CodeToIdMap canonicalize(CodeToIdMap idMapPrevHistory) {
			
			List<RecordWithPeriod> newRecords = records.stream()
					.map(r -> canonicalize(r, idMapPrevHistory))
					.collect(toList());
			
			records.clear();
			records.addAll(newRecords);
			
			return createNewIdMap(records);
		}

		private static RecordWithPeriod canonicalize(
				RecordWithPeriod record,
				CodeToIdMap idMapPrevHistory) {
			
			val newRecord = record.canonicalize(interm -> {
				
				String code = interm.getItemByNo(Items.職場コード).get().getString();
				
				String id;
				if (idMapPrevHistory.containsCode(code)) {
					id = idMapPrevHistory.getId(code);
				} else {
					id = IdentifierUtil.randomUniqueId();
				}

				return interm.addCanonicalized(CanonicalItem.of(Items.職場ID, id));
			});
			
			return newRecord;
		}

		private static CodeToIdMap createNewIdMap(List<RecordWithPeriod> source) {
			
			CodeToIdMap newIdMap = new CodeToIdMap();
			
			for (val record : source) {
				String code = record.interm.getItemByNo(Items.職場コード).get().getString();
				String id = record.interm.getItemByNo(Items.職場ID).get().getString();
				newIdMap.put(code, id);
			}
			
			return newIdMap;
		}
	}
}
