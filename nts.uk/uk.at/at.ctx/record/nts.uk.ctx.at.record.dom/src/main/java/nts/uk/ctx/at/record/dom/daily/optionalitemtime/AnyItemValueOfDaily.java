package nts.uk.ctx.at.record.dom.daily.optionalitemtime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.gul.text.StringUtil;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.record.dom.optitem.OptionalItem;
import nts.uk.ctx.at.record.dom.optitem.OptionalItemRepository;
import nts.uk.ctx.at.record.dom.optitem.applicable.EmpCondition;
import nts.uk.ctx.at.record.dom.optitem.calculation.CalcResultOfAnyItem;
import nts.uk.ctx.at.record.dom.optitem.calculation.Formula;
import nts.uk.ctx.at.shared.dom.adapter.employment.BsEmploymentHistoryImport;
import nts.uk.shr.com.context.AppContexts;

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
    											   		  Optional<DailyRecordToAttendanceItemConverter> dailyRecordDto,
    											   		  Optional<BsEmploymentHistoryImport> bsEmploymentHistOpt,
    											   		  Optional<AnyItemValueOfDaily> anyItemValueOfDaily) {
    	
               
        //任意項目分ループ
        List<CalcResultOfAnyItem> anyItemList = new ArrayList<>();
        
        for(OptionalItem optionalItem : optionalItemList) {
        	
        	Optional<EmpCondition> empCondition = Optional.empty();
        	List<EmpCondition> findResult = empConditionList.stream().filter(t -> t.getOptItemNo().equals(optionalItem.getOptionalItemNo())).collect(Collectors.toList());
        	if(!findResult.isEmpty()) {
        		empCondition = Optional.of(findResult.get(0));
        	}
        	
        	//利用条件の判定
        	if(optionalItem.checkTermsOfUse(empCondition,bsEmploymentHistOpt)) {
        		List<Formula> test = formulaList.stream().filter(t -> t.getOptionalItemNo().equals(optionalItem.getOptionalItemNo())).collect(Collectors.toList());
        		//計算処理
                anyItemList.add(optionalItem.caluculationFormula(companyId, optionalItem, test, dailyRecordDto/*,Optional.empty()　月次用なので日別から呼ぶ場合は何も無し*/));
        	}
        }
        
        AnyItemValueOfDaily result = new AnyItemValueOfDaily(employeeId,ymd,new ArrayList<>());
        
        if(anyItemValueOfDaily.isPresent()) {
        	
        	result = anyItemValueOfDaily.get();
        	
            //任意項目NOの昇順でソート
        	result.items.sort((c1, c2) -> c1.getItemNo().compareTo(c2.getItemNo()));
            
            /*パラメータ：任意項目の計算結果から任意項目値クラスへ変換(上で計算した項目のみ更新する)*/
            for(CalcResultOfAnyItem calcResultOfAnyItem:anyItemList) {
            	int itemNo = calcResultOfAnyItem.getOptionalItemNo().v();
            	result.items.set(itemNo - 1, new AnyItemValue(new AnyItemNo(itemNo),
    					  									  calcResultOfAnyItem.getCount().map(v -> new AnyItemTimes(BigDecimal.valueOf(v))),
    					  									  calcResultOfAnyItem.getMoney().map(v -> new AnyItemAmount(v)),
    					  									  calcResultOfAnyItem.getTime().map(v -> new AnyItemTime(v)))); 
            }
        }else {
        	for(CalcResultOfAnyItem calcResultOfAnyItem:anyItemList) {
            	int itemNo = calcResultOfAnyItem.getOptionalItemNo().v();
            	result.getItems().add(new AnyItemValue(new AnyItemNo(itemNo),
    					  							   calcResultOfAnyItem.getCount().map(v -> new AnyItemTimes(BigDecimal.valueOf(v))),
    					  							   calcResultOfAnyItem.getMoney().map(v -> new AnyItemAmount(v)),
    					  							   calcResultOfAnyItem.getTime().map(v -> new AnyItemTime(v))));
        	}
        }
        return result;
    }
    
    public void correctAnyType(OptionalItemRepository optionalMasterRepo){
    	List<Integer> itemIds = items.stream().map(i -> i.getItemNo().v()).collect(Collectors.toList());
		Map<Integer, OptionalItem> optionalMaster = optionalMasterRepo
				.findByListNos(AppContexts.user().companyId(), itemIds).stream()
				.collect(Collectors.toMap(c -> c.getOptionalItemNo().v(), c -> c));
		if(!optionalMaster.isEmpty()){
			items.stream().forEach(i -> {
				OptionalItem master = optionalMaster.get(i.getItemNo().v());
				if(master != null){
					switch (master.getOptionalItemAtr()) {
					case AMOUNT:
						i.markAsAmount();
						break;
					case NUMBER:
						i.markAsTimes();
						break;
					case TIME:
						i.markAsTime();
						break;
					default:
						break;
					}
				}
			});	
		}
    }
    
}
