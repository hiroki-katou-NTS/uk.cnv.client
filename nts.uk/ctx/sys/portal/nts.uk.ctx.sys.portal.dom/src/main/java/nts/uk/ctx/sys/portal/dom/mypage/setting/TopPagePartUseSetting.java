package nts.uk.ctx.sys.portal.dom.mypage.setting;

import lombok.EqualsAndHashCode;
import lombok.Value;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.sys.portal.dom.enums.TopPagePartType;
import nts.uk.ctx.sys.portal.dom.enums.UseDivision;
import nts.uk.ctx.sys.portal.dom.toppagepart.TopPagePartCode;
import nts.uk.ctx.sys.portal.dom.toppagepart.TopPagePartName;

/**
 * The Class TopPagePartSettingItem.
 */
@Value
@EqualsAndHashCode(callSuper = false)
public class TopPagePartUseSetting extends AggregateRoot {

	/** The company id. */
	private String companyId;

	/** The top page part code. */
	private TopPagePartCode topPagePartCode;

	/** The top page part name. */
	private TopPagePartName topPagePartName;

	/** The use division. */
	private UseDivision useDivision;

	/** The part type. */
	private TopPagePartType topPagePartType;

	/**
	 * Instantiates a new top page part use setting.
	 *
	 * @param companyId
	 *            the company id
	 * @param topPagePartCode
	 *            the top page part code
	 * @param topPagePartName
	 *            the top page part name
	 * @param useDivision
	 *            the use division
	 * @param topPagePartType
	 *            the top page part type
	 */
	public TopPagePartUseSetting(String companyId, TopPagePartCode topPagePartCode, TopPagePartName topPagePartName,
			UseDivision useDivision, TopPagePartType topPagePartType) {
		super();
		this.companyId = companyId;
		this.topPagePartCode = topPagePartCode;
		this.topPagePartName = topPagePartName;
		this.useDivision = useDivision;
		this.topPagePartType = topPagePartType;
	}

	public static TopPagePartUseSetting createFromJavaType(String companyId, String topPagePartCode,
			String topPagePartName, Integer useDivision, Integer topPagePartType) {
		return new TopPagePartUseSetting(companyId, new TopPagePartCode(topPagePartCode),
				new TopPagePartName(topPagePartName), UseDivision.valueOf(useDivision),
				TopPagePartType.valueOf(topPagePartType));
	}
}