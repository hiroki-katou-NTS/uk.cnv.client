package nts.uk.ctx.at.record.dom.daily.optionalitemtime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.attendanceitem.StoredProcdureProcessing.DailyStoredProcessResult;
import nts.uk.ctx.at.shared.dom.adapter.employment.BsEmploymentHistoryImport;
import nts.uk.ctx.at.shared.dom.scherec.anyitem.AnyItemNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.optionalitemvalue.AnyItemAmount;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.optionalitemvalue.AnyItemTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.optionalitemvalue.AnyItemTimes;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.optionalitemvalue.AnyItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.optionalitemvalue.AnyItemValueOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItem;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItemNo;
import nts.uk.ctx.at.shared.dom.scherec.optitem.PerformanceAtr;
import nts.uk.ctx.at.shared.dom.scherec.optitem.TermsOfUseForOptItem;
import nts.uk.ctx.at.shared.dom.scherec.optitem.applicable.EmpCondition;
import nts.uk.ctx.at.shared.dom.scherec.optitem.calculation.CalcResultOfAnyItem;
import nts.uk.ctx.at.shared.dom.scherec.optitem.calculation.Formula;
import nts.uk.ctx.at.shared.dom.scherec.optitem.calculation.disporder.FormulaDispOrder;

/** 日別実績の任意項目*/
@Getter
@NoArgsConstructor
public class AnyItemValueOfDaily {
	//社員ID
	private String employeeId;
	//年月日
	private GeneralDate ymd;

	//任意項目
	private AnyItemValueOfDailyAttd anyItem;
	
	public AnyItemValueOfDaily(String employeeId, GeneralDate ymd, List<AnyItemValue> items) {
		super();
		this.employeeId = employeeId;
		this.ymd = ymd;
		this.anyItem = new AnyItemValueOfDailyAttd(items);
	}
	
	public AnyItemValueOfDaily(String employeeId, GeneralDate ymd, AnyItemValueOfDailyAttd anyItem) {
		super();
		this.employeeId = employeeId;
		this.ymd = ymd;
		this.anyItem = anyItem;
	}
	
	
    /**
     * 任意項目の計算
     * @param resultProcedure 
     * @return
     */
    public static AnyItemValueOfDaily caluculationAnyItem(String companyId,
    											   		  String employeeId,
    											   		  GeneralDate ymd,
    											   		  List<OptionalItem> optionalItemList,
    											   		  List<Formula> formulaList,
    											   		  List<FormulaDispOrder> formulaOrderList,
    											   		  List<EmpCondition> empConditionList,
    											   		  Optional<DailyRecordToAttendanceItemConverter> dailyRecordDto,
    											   		  Optional<BsEmploymentHistoryImport> bsEmploymentHistOpt, 
    											   		  Optional<DailyStoredProcessResult> resultProcedure) {
    	
    	Optional<AnyItemValueOfDailyAttd> dailyAnyItem = dailyRecordDto.get().anyItems();
        //任意項目分ループ
        List<CalcResultOfAnyItem> anyItemList = new ArrayList<>();
        
        for(OptionalItem optionalItem : optionalItemList) {
        	
        	
        	CalcResultOfAnyItem calcResult = new CalcResultOfAnyItem(optionalItem.getOptionalItemNo(),
        															 Optional.of(BigDecimal.ZERO), 
        															 Optional.of(BigDecimal.ZERO),
        															 Optional.of(BigDecimal.ZERO));
        	
        	//ストアドで計算する対象であるなら、ストアドから値取得
        	Optional<AnyItemValue> storedValue = Optional.empty();
        	if(resultProcedure.isPresent()
        	 &&resultProcedure.get().isInclude(optionalItem.getOptionalItemNo().toString())) {
        		storedValue = resultProcedure.get().getOptionalItemBySearchNo(optionalItem.getOptionalItemNo().toString());
        		calcResult = new CalcResultOfAnyItem(new OptionalItemNo(storedValue.get().getItemNo().v()),
        											 Optional.of(storedValue.get().getRowTimes()), 
        											 Optional.of(BigDecimal.valueOf(storedValue.get().getRowTime())),
        											Optional.of(BigDecimal.valueOf(storedValue.get().getRowAmount())));
        	}
        	//日別利用条件の判定
        	else if(decisionCondition(optionalItem,empConditionList,bsEmploymentHistOpt)) {
        		List<Formula> optFormulas = formulaList.stream().filter(t -> t.getOptionalItemNo().equals(optionalItem.getOptionalItemNo())).collect(Collectors.toList());
        		List<FormulaDispOrder> optOrders = formulaOrderList.stream().filter(t -> t.getOptionalItemNo().equals(optionalItem.getOptionalItemNo())).collect(Collectors.toList());
        		//計算処理
        		calcResult = optionalItem.caluculationFormula(companyId, optFormulas, optOrders, dailyRecordDto, Optional.empty(), PerformanceAtr.DAILY_PERFORMANCE);
        	}
        	anyItemList.add(calcResult);
        	
        	
        	
        	
            //計算した(calcResult)値を Converter内へ格納
        	if(dailyAnyItem.isPresent()) {
        		List<AnyItemValue> forcsItem = dailyAnyItem.get().getItems();
        		Optional<AnyItemValue> getAnyItem = storedValue.isPresent() ? 
        												storedValue : 
        												forcsItem.stream().filter(tc -> tc.getItemNo().v().equals(optionalItem.getOptionalItemNo().v())).findFirst();
    			//存在する(上書き)
        		if(getAnyItem.isPresent()) {
        			//更新する(一致する)任意項目NoががいぶDtoのリスト内にいるかチェック
        			 val numberList = forcsItem.stream().map(tc -> tc.getItemNo().v().intValue()).collect(Collectors.toList());
        			 //リスト内での位置取得
        			 int indexNumber = numberList.indexOf(calcResult.getOptionalItemNo().v().intValue());
        			 //更新
        			 forcsItem.set(indexNumber, new AnyItemValue(new AnyItemNo(calcResult.getOptionalItemNo().v()),
							 												   calcResult.getCount().map(v -> new AnyItemTimes(BigDecimal.valueOf(v.doubleValue()))),
							 												   calcResult.getMoney().map(v -> new AnyItemAmount(v.intValue())),
							 												   calcResult.getTime().map(v -> new AnyItemTime(v.intValue()))));
        		}
        		else {
        			//コンバーター内には存在しない任意項目Noの追加
        			forcsItem.add(new AnyItemValue(new AnyItemNo(calcResult.getOptionalItemNo().v()),
        														 calcResult.getCount().map(v -> new AnyItemTimes(BigDecimal.valueOf(v.doubleValue()))),
        														 calcResult.getMoney().map(v -> new AnyItemAmount(v.intValue())),
        														 calcResult.getTime().map(v -> new AnyItemTime(v.intValue()))));
        		}     
            	dailyAnyItem.get().setItems(forcsItem);
            	//
            	dailyRecordDto = Optional.of(dailyRecordDto.get().withAnyItems(dailyAnyItem.get()));
        	}
        }
        
        AnyItemValueOfDaily result = new AnyItemValueOfDaily(employeeId,ymd,new ArrayList<>());
        
        for(CalcResultOfAnyItem calcResultOfAnyItem:anyItemList) {
        	int itemNo = calcResultOfAnyItem.getOptionalItemNo().v();
        	result.getAnyItem().getItems().add(new AnyItemValue(new AnyItemNo(itemNo),
					  							   calcResultOfAnyItem.getCount().map(v -> new AnyItemTimes(BigDecimal.valueOf(v.doubleValue()))),
					  							   calcResultOfAnyItem.getMoney().map(v -> new AnyItemAmount(v.intValue())),
					  							   calcResultOfAnyItem.getTime().map(v -> new AnyItemTime(v.intValue()))));
    	}

        return result;
    }
    
    public static boolean decisionCondition(OptionalItem optionalItem,List<EmpCondition> empConditionList,Optional<BsEmploymentHistoryImport> bsEmploymentHistOpt) {
    	Optional<EmpCondition> empCondition = Optional.empty();
    	List<EmpCondition> findResult = empConditionList.stream().filter(t -> t.getOptItemNo().equals(optionalItem.getOptionalItemNo())).collect(Collectors.toList());
    	if(!findResult.isEmpty()) {
    		empCondition = Optional.of(findResult.get(0));
    	}
    	
    	return (optionalItem.checkTermsOfUseDaily(empCondition,bsEmploymentHistOpt) == TermsOfUseForOptItem.USE);
    }

    public Optional<AnyItemValue> getNo(int no) {
    	return this.anyItem.getItems().stream().filter(i -> i.getItemNo().v() == no).findFirst();
    }
}
