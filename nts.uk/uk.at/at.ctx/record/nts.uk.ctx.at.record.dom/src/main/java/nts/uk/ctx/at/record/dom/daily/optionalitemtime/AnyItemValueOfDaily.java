package nts.uk.ctx.at.record.dom.daily.optionalitemtime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.record.dom.optitem.OptionalItem;
import nts.uk.ctx.at.record.dom.optitem.applicable.EmpCondition;
import nts.uk.ctx.at.record.dom.optitem.calculation.CalcResultOfAnyItem;
import nts.uk.ctx.at.record.dom.optitem.calculation.Formula;

/** 日別実績の任意項目*/
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AnyItemValueOfDaily {
	
	private String employeeId;
	
	private GeneralDate ymd;

	/** 任意項目値: 任意項目値 */
	private List<AnyItemValue> items;
	
	
	
    /**
     * 任意項目の計算
     * @return
     */
    public static AnyItemValueOfDaily caluculationAnyItem(String companyId,
    											   		  String employeeId,
    											   		  GeneralDate ymd,
    											   		  List<OptionalItem> optionalItemList,
    											   		  List<Formula> formulaList,
    											   		  List<EmpCondition> empConditionList,
    											   		  Optional<DailyRecordToAttendanceItemConverter> dailyRecordDto
												   		  ) {
    	
               
        //任意項目分ループ
        List<CalcResultOfAnyItem> anyItemList = new ArrayList<>();
        
        for(OptionalItem optionalItem : optionalItemList) {
        	
        	Optional<EmpCondition> empCondition = Optional.empty();
        	List<EmpCondition> findResult = empConditionList.stream().filter(t -> t.getOptItemNo()==optionalItem.getOptionalItemNo()).collect(Collectors.toList());
        	if(!findResult.isEmpty()) {
        		empCondition = Optional.of(findResult.get(0));
        	}
        	
        	//利用条件の判定
        	if(optionalItem.checkTermsOfUse(empCondition)) {
        		List<Formula> test = formulaList.stream().filter(t -> t.getOptionalItemNo()==optionalItem.getOptionalItemNo()).collect(Collectors.toList());
        		//計算処理
                anyItemList.add(optionalItem.caluculationFormula(companyId, optionalItem, test, dailyRecordDto/*,Optional.empty()　月次用なので日別から呼ぶ場合は何も無し*/));
        	}
        }
        
        /*パラメータ：任意項目の計算結果から任意項目値クラスへ変換*/
        List<AnyItemValue> transAnyItem = new ArrayList<>();
        for(CalcResultOfAnyItem calcResultOfAnyItem:anyItemList) {
        	transAnyItem.add(new AnyItemValue(new AnyItemNo(Integer.parseInt(calcResultOfAnyItem.getOptionalItemNo().v())),
        									  Optional.of(new AnyItemTimes(calcResultOfAnyItem.getCount().get())),
        									  Optional.of(new AnyItemAmount(BigDecimal.valueOf(calcResultOfAnyItem.getMoney().get()))),
        									  Optional.of(new AnyItemTime(calcResultOfAnyItem.getTime().get()))));       	
        }
                
        return new AnyItemValueOfDaily(employeeId,ymd,transAnyItem);
    }
    
}
