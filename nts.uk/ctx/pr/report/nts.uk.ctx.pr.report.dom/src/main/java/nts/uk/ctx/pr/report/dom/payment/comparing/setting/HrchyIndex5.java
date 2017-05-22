package nts.uk.ctx.pr.report.dom.payment.comparing.setting;

import lombok.AllArgsConstructor;

/**
 * 階層インデックス5 チェックのついている階層の数値を保持
 */
@AllArgsConstructor
public enum HrchyIndex5 {

	/**
	 * 0.設定なし
	 */
	CLASS0(0),
	/**
	 * 5..9.設定
	 */
	CLASS5(5), CLASS6(6), CLASS7(7), CLASS8(8), CLASS9(9);

	public final int value;
}