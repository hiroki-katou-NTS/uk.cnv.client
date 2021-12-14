package nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace;

import lombok.AllArgsConstructor;

/**
 * 検索参照範囲
 * @author dan_pv
 */
@AllArgsConstructor
public enum SearchReferenceRange {
	
	/** 参照可能範囲すべて */
	ALL_REFERENCE_RANGE(0),

	/** 所属と配下すべて */
	AFFILIATION_AND_ALL_SUBORDINATES(1),

	/** 所属のみ */
	AFFILIATION_ONLY(2),

	/** 参照範囲を考慮しない */
	DO_NOT_CONSIDER_REFERENCE_RANGE(3);

	/** The value. */
	public final int value;

}

