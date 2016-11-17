package nts.uk.ctx.pr.proto.dom.layout.category;

import java.util.List;

import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.layer.dom.DomainObject;
import nts.arc.time.YearMonth;
import nts.uk.ctx.core.dom.company.CompanyCode;
import nts.uk.ctx.pr.proto.dom.enums.CategoryAtr;
import nts.uk.ctx.pr.proto.dom.layout.LayoutAtr;
import nts.uk.ctx.pr.proto.dom.layout.LayoutCode;
import nts.uk.ctx.pr.proto.dom.layout.LayoutMaster;
import nts.uk.ctx.pr.proto.dom.layout.LayoutName;
import nts.uk.ctx.pr.proto.dom.layout.line.LayoutMasterLine;

/**
 * LayoutMasterCategory valueObject
 * 
 * 
 *
 */
public class LayoutMasterCategory extends AggregateRoot {
	/** code */
	@Getter
	private CompanyCode companyCode;

	/** 開始年月 */
	@Getter
	private YearMonth startYM;

	/** 明細書コード */
	@Getter
	private LayoutCode stmtCode;
	
	@Getter
	private CategoryAtr ctAtr;

	/** 終了年月 */
	@Getter
	private YearMonth endYM;


	/** 明細書名 */
	@Getter
	private CategoryPosition ctgPos;
	
	
	
	@Getter
	private List<LayoutMasterLine> layoutMasterLines;

	public LayoutMasterCategory(CompanyCode companyCode, YearMonth startYM, LayoutCode stmtCode,CategoryAtr ctgatr, YearMonth endYM,
			 CategoryPosition ctgPos ) {
		super();
		this.companyCode = companyCode;
		this.startYM = startYM;
		this.stmtCode = stmtCode;
		this.ctAtr = ctgatr;
		this.endYM = endYM;
		this.ctgPos = ctgPos;
	

	}

	/**
	 * create From Java Type
	 * 
	 * @return LayoutMasterCategory
	 */

	public static LayoutMasterCategory createFromJavaType(String companyCode, int startYM,
			String stmtCode,int ctgAtr, int endYM,int ctgPos ) {
		
		return new LayoutMasterCategory(new CompanyCode(companyCode), new YearMonth(startYM), new LayoutCode(stmtCode),EnumAdaptor.valueOf(ctgAtr, CategoryAtr.class),
				new YearMonth(endYM), new CategoryPosition(ctgPos));

	}
}
