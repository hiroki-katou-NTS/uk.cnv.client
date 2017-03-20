package nts.uk.ctx.pr.core.dom.itemmaster.itemdeductbd;

import java.math.BigDecimal;

import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.pr.core.dom.enums.DisplayAtr;
import nts.uk.ctx.pr.core.dom.itemmaster.ItemCode;
import nts.uk.ctx.pr.core.dom.itemmaster.UniteCode;
import nts.uk.ctx.pr.core.dom.itemmaster.itemsalary.AlRangeHigh;
import nts.uk.ctx.pr.core.dom.itemmaster.itemsalary.AlRangeLow;
import nts.uk.ctx.pr.core.dom.itemmaster.itemsalary.ErrRangeHigh;
import nts.uk.ctx.pr.core.dom.itemmaster.itemsalary.ErrRangeLow;
import nts.uk.ctx.pr.core.dom.itemmaster.itemsalary.RangeAtr;
import nts.uk.ctx.pr.core.dom.itemmaster.itemsalarybd.ItemBreakdownAbName;
import nts.uk.ctx.pr.core.dom.itemmaster.itemsalarybd.ItemBreakdownCd;
import nts.uk.ctx.pr.core.dom.itemmaster.itemsalarybd.ItemBreakdownName;

@Getter
public class ItemDeductBD extends AggregateRoot {

	private ItemCode itemCd;
	private ItemBreakdownCd itemBreakdownCd;
	private ItemBreakdownName itemBreakdownName;
	private ItemBreakdownAbName itemBreakdownAbName;
	private UniteCode uniteCd;
	private DisplayAtr zeroDispSet;
	private DisplayAtr itemDispAtr;
	private RangeAtr errRangeLowAtr;
	private ErrRangeLow errRangeLow;
	private RangeAtr errRangeHighAtr;
	private ErrRangeHigh errRangeHigh;
	private RangeAtr alRangeLowAtr;
	private AlRangeLow alRangeLow;
	private RangeAtr alRangeHighAtr;
	private AlRangeHigh alRangeHigh;

	public ItemDeductBD(ItemCode itemCd, ItemBreakdownCd itemBreakdownCd, ItemBreakdownName itemBreakdownName,
			ItemBreakdownAbName itemBreakdownAbName, UniteCode uniteCd, DisplayAtr zeroDispSet, DisplayAtr itemDispAtr,
			RangeAtr errRangeLowAtr, ErrRangeLow errRangeLow, RangeAtr errRangeHighAtr, ErrRangeHigh errRangeHigh,
			RangeAtr alRangeLowAtr, AlRangeLow alRangeLow, RangeAtr alRangeHighAtr, AlRangeHigh alRangeHigh) {
		super();
		this.itemCd = itemCd;
		this.itemBreakdownCd = itemBreakdownCd;
		this.itemBreakdownName = itemBreakdownName;
		this.itemBreakdownAbName = itemBreakdownAbName;
		this.uniteCd = uniteCd;
		this.zeroDispSet = zeroDispSet;
		this.itemDispAtr = itemDispAtr;
		this.errRangeLowAtr = errRangeLowAtr;
		this.errRangeLow = errRangeLow;
		this.errRangeHighAtr = errRangeHighAtr;
		this.errRangeHigh = errRangeHigh;
		this.alRangeLowAtr = alRangeLowAtr;
		this.alRangeLow = alRangeLow;
		this.alRangeHighAtr = alRangeHighAtr;
		this.alRangeHigh = alRangeHigh;
	}

	public static ItemDeductBD createFromJavaType(
			String itemCd, String itemBreakdownCd, String itemBreakdownName, String itemBreakdownAbName, String uniteCd,
			int zeroDispSet, int itemDispAtr, int errRangeLowAtr, BigDecimal errRangeLow, int errRangeHighAtr,
			BigDecimal errRangeHigh, int alRangeLowAtr, BigDecimal alRangeLow, int alRangeHighAtr,
			BigDecimal alRangeHigh
	) {
		return new ItemDeductBD(new ItemCode(itemCd), new ItemBreakdownCd(itemBreakdownCd),
				new ItemBreakdownName(itemBreakdownName), new ItemBreakdownAbName(itemBreakdownAbName),
				new UniteCode(uniteCd), EnumAdaptor.valueOf(zeroDispSet, DisplayAtr.class),
				EnumAdaptor.valueOf(itemDispAtr, DisplayAtr.class), EnumAdaptor.valueOf(errRangeLowAtr, RangeAtr.class),
				new ErrRangeLow(errRangeLow), EnumAdaptor.valueOf(errRangeHighAtr, RangeAtr.class),
				new ErrRangeHigh(errRangeHigh), EnumAdaptor.valueOf(alRangeLowAtr, RangeAtr.class),
				new AlRangeLow(alRangeLow), EnumAdaptor.valueOf(alRangeHighAtr, RangeAtr.class),
				new AlRangeHigh(alRangeHigh));
	}

}
