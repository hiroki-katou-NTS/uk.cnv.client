package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory.EmployeeUnitPriceHistoryItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory.UnitPrice;

/**
 * 人件費計算設定
 *
 * @author Doan Duy Hung
 */
@Value
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class PersonCostCalculation extends AggregateRoot {
//    private String companyID;
//    private List<PremiumSetting> premiumSettings;
//    private UnitPrice unitPrice;
//
//    private String historyID;
//
//    private GeneralDate startDate;
//
//    private GeneralDate endDate;
//    private Memo memo;
//
//    public PersonCostCalculation(String companyID, String historyID, GeneralDate startDate, GeneralDate endDate,
//                                 UnitPrice unitPrice, Memo memo, List<PremiumSetting> premiumSettings) {
//        super();
//        this.companyID = companyID;
//        this.historyID = historyID;
//        this.startDate = startDate;
//        this.endDate = endDate;
//        this.unitPrice = unitPrice;
//        this.memo = memo;
//        this.premiumSettings = premiumSettings;
//    }

    //1: 丸め設定 : 人件費丸め設定
    private PersonCostRoundingSetting roundingSetting;
    // 2: 会社ID
    private String companyID;
    // 3 : 備考
    private Remarks remark;
    // 4: 割増設定 : List<割増設定>`
    private List<PremiumSetting> premiumSettings;
    // 5: 単価 : Optional<単価>
    private Optional<UnitPrice> unitPrice;
    // 6: 単価の設定方法 :単価の設定方法
    private HowToSetUnitPrice howToSetUnitPrice;
    // 7: 就業時間単価: 単価
    private UnitPrice workingHoursUnitPrice;
    // 8: 履歴ID
    private String historyID;

    // 仮計算する
    public List<?> tentativeCalculation(int unitPrice, Map<Object, Object> workingHours) {
        return null;
    }
    

	/**
	 * 社員時間単価NOを取得する
	 * @param no 割増時間項目NO
	 * @return 「単価の設定方法」を判断して取得した「社員時間単価NO」
	 */
	public Optional<UnitPrice> getUnitPriceAsJudged(ExtraTimeItemNo no) {
		if(this.howToSetUnitPrice.isPremiumRate()) {
			return this.unitPrice;
		}
		return this.premiumSettings.stream()
				.filter(p -> p.getID().equals(no))
				.map(p -> p.getUnitPrice())
				.findFirst();
	}

	/**
	 * 割増時間合計に含めるか
	 * @param no 割増時間項目NO
	 * @return 割増時間合計に含める
	 */
	public boolean isIncludeTotalTime(ExtraTimeItemNo no) {
		Optional<PremiumSetting> set = this.premiumSettings.stream()
				.filter(p -> p.getID().equals(no))
				.findFirst();
		return set.map(s -> s.isIncludeTotal()).orElse(false);
	}

	/**
	 * 割増時間の単価を取得する
	 * @param no 割増時間項目NO
	 * @param employeeUnitPrice  社員単価履歴項目
	 * @return 割増時間の単価
	 */
	public WorkingHoursUnitPrice getPremiumUnitPrice(ExtraTimeItemNo no, EmployeeUnitPriceHistoryItem employeeUnitPrice) {
		Optional<UnitPrice> unitPriceNo = getUnitPriceAsJudged(no);
		if(!unitPriceNo.isPresent()) {
			return WorkingHoursUnitPrice.ZERO;
		}
		return employeeUnitPrice.getWorkingHoursUnitPrice(unitPriceNo.get()).orElse(WorkingHoursUnitPrice.ZERO);
	}

	/**
	 * 就業時間金額の単価を取得する
	 * @param employeeUnitPrice 社員単価履歴項目
	 * @return 就業時間金額の単価
	 */
	public WorkingHoursUnitPrice getWorkTimeUnitPrice(EmployeeUnitPriceHistoryItem employeeUnitPrice) {
		return employeeUnitPrice.getWorkingHoursUnitPrice(this.workingHoursUnitPrice).orElse(WorkingHoursUnitPrice.ZERO);
	}

	/**
	 * 割増設定を取得する
	 * @param no 割増時間項目NO
	 * @return 割増設定
	 */
	public Optional<PremiumSetting> getPremiumSetting(ExtraTimeItemNo no) {
		return this.premiumSettings.stream().filter(p -> p.getID().equals(no)).findFirst();
	}
}