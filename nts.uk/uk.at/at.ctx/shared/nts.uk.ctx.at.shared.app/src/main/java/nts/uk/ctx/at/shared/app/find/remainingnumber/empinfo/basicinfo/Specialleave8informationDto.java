package nts.uk.ctx.at.shared.app.find.remainingnumber.empinfo.basicinfo;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.basicinfo.SpecialLeaveBasicInfo;
import nts.uk.shr.pereg.app.PeregEmployeeId;
import nts.uk.shr.pereg.app.PeregItem;
import nts.uk.shr.pereg.app.find.dto.PeregDomainDto;

@Getter
@Setter
public class Specialleave8informationDto  extends PeregDomainDto {

	@PeregEmployeeId
	private String sID;
	
	//	特別休暇付与基準日
	@PeregItem("IS00344")
	private GeneralDate grantDate;
	
	//	特別休暇管理
	@PeregItem("IS00345")
	private Integer useAtr;
	
	//	付与設定
	@PeregItem("IS00346")
	private Integer appSet;
	
	//	付与日数
	@PeregItem("IS00347")
	private Integer grantDays;
	
	//	付与テーブル
	@PeregItem("IS00348")
	private String grantTable;
	
	//	次回付与日
	@PeregItem("IS00349")
	private String nextGrantDate;

	//	特別休暇残数
	@PeregItem("IS00350")
	private String spHDRemain;
	public static Specialleave8informationDto createFromDomain(SpecialLeaveBasicInfo domain){
		Specialleave8informationDto dto = new Specialleave8informationDto();
		dto.grantDate = domain.getGrantSetting().getGrantDate();
		dto.useAtr = domain.getUsed().value;
		dto.appSet = domain.getApplicationSet().value;
		if (domain.getGrantSetting().getGrantDays().isPresent()){
			dto.grantDays = domain.getGrantSetting().getGrantDays().get().v();
		}
		if (domain.getGrantSetting().getGrantTable().isPresent()){
			dto.grantTable = domain.getGrantSetting().getGrantTable().get().v();
		}
		dto.setRecordId(domain.getSID());
		return dto;
	}
}
