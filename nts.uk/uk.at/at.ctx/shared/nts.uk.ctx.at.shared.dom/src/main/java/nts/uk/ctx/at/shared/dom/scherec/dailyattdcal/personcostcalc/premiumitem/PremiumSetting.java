package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.EqualsAndHashCode;
import lombok.Value;
import nts.arc.validate.Validatable;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory.UnitPrice;

/**
 * 割増設定
 * @author Doan Duy Hung
 *
 */
@Value
@EqualsAndHashCode(callSuper = false)
public class PremiumSetting implements Validatable{

//
//	private Integer displayNumber;
//
//	private PremiumRate rate;
//

//
//	private UseAttribute useAtr;
//
//	private List<Integer> attendanceItems;
//
//	public PremiumSetting(String companyID, String historyID, Integer displayNumber, PremiumRate rate, PremiumName name,
//						  UseAttribute useAtr, List<Integer> attendanceItems) {
//		super();
//		this.companyID = companyID;
//		this.historyID = historyID;
//		this.displayNumber = displayNumber;
//		this.rate = rate;
//		this.name = name;
//		this.useAtr = useAtr;
//		this.attendanceItems = attendanceItems;
//	}

	private String companyID;//

	private String historyID;//
	// 割増時間項目NO
	private ExtraTimeItemNo iD;
	// 人件費割増率
    private PremiumRate rate;
	// 単価
    private UnitPrice unitPrice;

    private List<String> attendanceItems;

	public List<Integer> getAttendanceItems() {
		if (attendanceItems == null || attendanceItems.size()<=0){
			return new ArrayList<>();
		}
		return attendanceItems.stream().map(j-> Integer.valueOf(j)).collect(Collectors.toList());
	}

	/** 割増時間合計に含めるか */
	private boolean includeTotal;

    public PremiumSetting(String companyID,String historyID,
						  ExtraTimeItemNo iD, PremiumRate rate,
						  UnitPrice unitPrice,
						  List<String> attendanceItems) {
		super();
		this.iD = iD;
		this.rate = rate;
		this.unitPrice = unitPrice;
		this.attendanceItems = attendanceItems;
		this.companyID = companyID;
		this.historyID = historyID;
		this.includeTotal = true; //ToDo UI実装時に要修正
	}

	@Override
	public void validate() {
		this.rate.validate();
	}
}
