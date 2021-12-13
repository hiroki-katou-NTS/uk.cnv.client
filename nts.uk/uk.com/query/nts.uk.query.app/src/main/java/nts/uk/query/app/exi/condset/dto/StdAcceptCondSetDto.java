package nts.uk.query.app.exi.condset.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.exio.dom.exi.condset.StdAcceptCondSet;

/**
 * The class Standard acceptance condition setting dto.<br>
 * Dto 受入条件設定（定型）
 *
 * @author nws-minhnb
 */
@Data
@AllArgsConstructor
@Setter
@Getter
public class StdAcceptCondSetDto{

	/**
	 * システム種類
	 */
	private Integer systemType;

	/**
	 * 外部受入条件コード
	 */
	private String conditionSetCode;

	/**
	 * 外部受入条件名称
	 */
	private String conditionSetName;

	/**
	 * 既存データの削除
	 */
	private int deleteExistData;

	/**
	 * 受入モード
	 */
	private Integer acceptMode;

	/**
	 * 外部受入カテゴリID
	 */
	private Integer categoryId;


	/**
	 * 既存データの削除方法
	 */
	private Integer deleteExistDataMethod;


	/**
	 * Creates from domain.
	 *
	 * @param domain the domain 受入条件設定（定型）
	 * @return the dto 受入条件設定（定型）
	 */
	public static StdAcceptCondSetDto createFromDomain(StdAcceptCondSet domain) {
		if (domain == null) {
			return null;
		}
		StdAcceptCondSetDto dto = new StdAcceptCondSetDto(domain.getSystemType().isPresent() ? domain.getSystemType().get().value : null,
				domain.getConditionSetCode().v(),
				domain.getConditionSetName().v(),
				domain.getDeleteExistData().value,
				domain.getAcceptMode().isPresent() ? domain.getAcceptMode().get().value : null,
				domain.getCategoryId().isPresent() ? domain.getCategoryId().get() : null,
				domain.getDeleteExistDataMethod().isPresent() ? domain.getDeleteExistDataMethod().get().value : null);
		return dto;
	}
}
