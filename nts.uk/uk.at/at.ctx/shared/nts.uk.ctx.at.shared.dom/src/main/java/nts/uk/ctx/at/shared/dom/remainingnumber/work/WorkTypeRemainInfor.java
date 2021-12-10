package nts.uk.ctx.at.shared.dom.remainingnumber.work;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.CreateAtr;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHolidayCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeClassification;

/**
 * 勤務種類別残数情報
 * 
 * @author do_dt
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class WorkTypeRemainInfor {
	/** 勤務種類コード */
	private String workTypeCode;
	/** * 勤務種類の分類 */
	private WorkTypeClassification workTypeClass;
	/** 作成元区分 */
	private CreateAtr createData;
	/** 発生使用明細 */
	private List<OccurrenceUseDetail> occurrenceDetailData = new ArrayList<>();
	/** 特休使用明細 */
	private List<SpecialHolidayUseDetail> speHolidayDetailData;
	/** 子の看護介護使用明細 */
	private List<CareUseDetail> childCareDetailData = new ArrayList<>();
	
	
	public WorkTypeRemainInfor clone(){
		WorkTypeRemainInfor clone = new WorkTypeRemainInfor();
		
		clone.setWorkTypeCode(this.workTypeCode);
		clone.setCreateData(this.createData);
		clone.setOccurrenceDetailData(this.occurrenceDetailData.stream().map(c -> c.clone()).collect(Collectors.toList()));
		clone.setSpeHolidayDetailData(this.speHolidayDetailData.stream().map(c -> c.clone()).collect(Collectors.toList()));
		clone.setChildCareDetailData(this.childCareDetailData.stream().map(c -> c.clone()).collect(Collectors.toList()));
		return clone;
	}
	
	public Optional<OccurrenceUseDetail> getOccurrence(WorkTypeClassification wkType) {
		
		return occurrenceDetailData.stream().filter(od ->od.getWorkTypeAtr().equals(wkType) && od.isUseAtr()).findFirst();
	}
	
	public Optional<SpecialHolidayUseDetail> getSpecialHolidayUseDetail(SpecialHolidayCode code){
		return speHolidayDetailData.stream().filter(c -> c.getSpecialHolidayCode() == (code.v())).findFirst();
	}
	
	public Optional<CareUseDetail> getCareUseDetail(CareType careType){
		return childCareDetailData.stream().filter(c -> c.getCareType().equals(careType)).findFirst();
	}
}
