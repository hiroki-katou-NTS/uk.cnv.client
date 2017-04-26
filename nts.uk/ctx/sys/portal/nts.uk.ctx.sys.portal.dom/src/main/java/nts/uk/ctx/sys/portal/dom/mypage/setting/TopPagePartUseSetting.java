package nts.uk.ctx.sys.portal.dom.mypage.setting;

import lombok.Getter;
import nts.arc.layer.dom.DomainObject;
import nts.uk.ctx.sys.portal.dom.enums.TopPagePartType;
import nts.uk.ctx.sys.portal.dom.enums.UseDivision;
import nts.uk.ctx.sys.portal.dom.primitive.CompanyId;
import nts.uk.ctx.sys.portal.dom.toppagepart.primitive.ToppagePartCode;
import nts.uk.ctx.sys.portal.dom.toppagepart.primitive.ToppagePartName;

/**
 * The Class TopPagePartSettingItem.
 */
@Getter
public class TopPagePartUseSetting extends DomainObject{

	/** The company id. */
	private CompanyId companyId;

	/** The top page part code. */
	private ToppagePartCode topPagePartCode;

	/** The top page part name. */
	private ToppagePartName topPagePartName;

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
	public TopPagePartUseSetting(CompanyId companyId, ToppagePartCode topPagePartCode, ToppagePartName topPagePartName,
			UseDivision useDivision, TopPagePartType topPagePartType) {
		super();
		this.companyId = companyId;
		this.topPagePartCode = topPagePartCode;
		this.topPagePartName = topPagePartName;
		this.useDivision = useDivision;
		this.topPagePartType = topPagePartType;
	}
}
