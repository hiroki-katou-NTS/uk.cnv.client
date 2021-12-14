package nts.uk.screen.at.app.ksu001.start;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.screen.at.app.ksu001.getinfoofInitstartup.DataScreenQueryGetInforDto;

/**
 * @author laitv
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DataBasicDto {
	public GeneralDate startDate; // ・期間 A3_1_2
	public GeneralDate endDate;   // ・期間 A3_1_4
	public  int unit; //WORKPLACE(0), //WORKPLACE_GROUP(1);
	public  String workplaceId;
	public  String workplaceGroupId;
	public String designation ; //  Aa1_2_2
	public String targetOrganizationName ;   // A2_2	 表示名 
	public String code;
	public GeneralDate scheduleModifyStartDate;  // 修正可能開始日
	public Boolean usePublicAtr; // 公開を利用するか
	public Boolean useWorkAvailabilityAtr; // 勤務希望を利用するか
	
	public DataBasicDto(DataScreenQueryGetInforDto resultStep1) {
		this.startDate = resultStep1.startDate;
		this.endDate = resultStep1.endDate;
		this.unit = resultStep1.targetOrgIdenInfor.unit;
		this.workplaceId = resultStep1.targetOrgIdenInfor.workplaceId;
		this.workplaceGroupId = resultStep1.targetOrgIdenInfor.workplaceGroupId;
		this.designation = resultStep1.displayInforOrganization.getDesignation();
		this.targetOrganizationName = resultStep1.displayInforOrganization.getDisplayName();
		this.code = resultStep1.displayInforOrganization.getCode();
		this.scheduleModifyStartDate = resultStep1.scheduleModifyStartDate;
		this.usePublicAtr = resultStep1.usePublicAtr;
		this.useWorkAvailabilityAtr = resultStep1.useWorkAvailabilityAtr;
		
	}
}
