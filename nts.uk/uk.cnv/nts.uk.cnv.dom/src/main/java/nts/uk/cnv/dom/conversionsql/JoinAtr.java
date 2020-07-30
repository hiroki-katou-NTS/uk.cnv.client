package nts.uk.cnv.dom.conversionsql;

import lombok.Getter;

/**
 * 結合区分
 * @author ai_muto
 *
 */
public enum JoinAtr {
	/** 主テーブル **/
	Main(""),
	/** 内部結合 */
	InnerJoin("INNER JOIN"),
	/** 外部結合 */
	OuterJoin("LEFT OUTER JOIN");

	@Getter
	private final String sql;
	
	private JoinAtr( final String sql) {
		this.sql = sql;
	}
}
