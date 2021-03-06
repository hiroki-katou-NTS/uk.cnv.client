package nts.uk.ctx.exio.dom.input.canonicalize.domaindata;

import java.util.HashMap;
import java.util.Map;

import nts.uk.ctx.exio.dom.input.canonicalize.existing.AnyRecordTo;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.StringifiedValue;

/**
 * 項目NOが割り振られているが、システム内部でのみ登場する項目たち
 * これが無いと、既存データのCHANGEテーブル書き込み時に項目Noから列名へ復元できない
 * Map<列名,項目NO> 
 */
public class SystemImportingItems {
	private static Map<String, Integer> map = new HashMap<String, Integer>();
	static {
		map.put("SID",101);
		map.put("HIST_ID",102);
	}

	public static StringifiedValue getStringifiedValue(AnyRecordTo record, String columnName, int keyIndex) {
		return record.getKey(getItemNo(columnName, keyIndex));
	}

	private static int getItemNo(String columnName, int keyIndex) {
		//システム内部でしか使わない項目も加味して項目Noに変更
		return map.containsKey(columnName)
				? SystemImportingItems.map.get(columnName)
				: keyIndex;
	}
}
