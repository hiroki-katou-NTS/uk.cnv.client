package nts.uk.screen.com.ws.equipment.data;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationImport;
import nts.uk.screen.com.app.find.equipment.data.EquipmentDataResultDto;
import nts.uk.screen.com.app.find.equipment.data.EquipmentDataResultParam;
import nts.uk.screen.com.app.find.equipment.data.EquipmentDataScreenQuery;
import nts.uk.screen.com.app.find.equipment.data.EquipmentDataUsageInputScreenQuery;
import nts.uk.screen.com.app.find.equipment.data.EquipmentInfoParam;
import nts.uk.screen.com.app.find.equipment.data.EquipmentInitInfoDto;
import nts.uk.screen.com.app.find.equipment.data.EquipmentInitSettingDto;
import nts.uk.screen.com.app.find.equipment.data.InitialEquipmentUsageInput;
import nts.uk.screen.com.app.find.equipment.information.EquipmentInformationDto;

@Path("com/screen/oew001/")
@Produces("application/json")
public class Oew001WebService extends WebService {

	@Inject
	private EquipmentDataScreenQuery equipmentDataScreenQuery;

	@Inject
	private EquipmentDataUsageInputScreenQuery equipmentDataUsageInputScreenQuery;

	@POST
	@Path("initEquipmentInfo")
	public EquipmentInitInfoDto getInitEquipmentInfo(InitialEquipmentUsageInput param) {
		return this.equipmentDataScreenQuery.initEquipmentInfo(Optional.ofNullable(param.getEquipmentClsCode()));
	}

	@POST
	@Path("initEquipmentSetting")
	public EquipmentInitSettingDto getInitEquipmentSetting() {
		return this.equipmentDataScreenQuery.initEquipmentSetting();
	}

	@POST
	@Path("getResultHistory")
	public EquipmentDataResultDto getResultHistory(EquipmentDataResultParam param) {
		return this.equipmentDataScreenQuery.getResultHistory(param.getEquipmentClsCode(), param.getEquipmentCode(),
				YearMonth.of(param.getYm()));
	}

	@POST
	@Path("getEquipmentInfoList")
	public List<EquipmentInformationDto> getEquipmentInfoList(EquipmentInfoParam param) {
		return this.equipmentDataScreenQuery.getEquipmentInfoList(param.getEquipmentClsCode(), param.getBaseDate(),
				param.isInput());
	}
	
	@POST
	@Path("getEmployeeInfo/{sid}")
	public List<EmployeeInformationImport> getEmployeeInfo(@PathParam("sid") String sid) {
		return this.equipmentDataUsageInputScreenQuery.getEmployeeInfo(sid);
	}
}
