package nts.uk.ctx.at.shared.app.find.remainingnumber.empinfo.basicinfo;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.SpecialLeaveCode;
import nts.uk.shr.pereg.app.ComboBoxObject;
import nts.uk.shr.pereg.app.find.PeregFinder;
import nts.uk.shr.pereg.app.find.PeregQuery;
import nts.uk.shr.pereg.app.find.PeregQueryByListEmp;
import nts.uk.shr.pereg.app.find.dto.DataClassification;
import nts.uk.shr.pereg.app.find.dto.GridPeregDomainBySidDto;
import nts.uk.shr.pereg.app.find.dto.GridPeregDomainDto;
import nts.uk.shr.pereg.app.find.dto.PeregDomainDto;

@Stateless
public class Specialleave12InformationFinder implements PeregFinder<Specialleave12informationDto>{

	@Inject 
	private SpecialleaveInformationFinder specialleaveInformationFinder;
	
	@Override
	public String targetCategoryCode() {
		return "CS00050";
	}

	@Override
	public Class<Specialleave12informationDto> dtoClass() {
		return Specialleave12informationDto.class;
	}

	@Override
	public DataClassification dataType() {
		return DataClassification.EMPLOYEE;
	}

	@Override
	public PeregDomainDto getSingleData(PeregQuery query) {
		// 社員ID　＝　社員ID　 and 特別休暇コード＝12
		return specialleaveInformationFinder.getSingleData(query, SpecialLeaveCode.CS00050.value);
	}
	
	@Override
	public List<PeregDomainDto> getListData(PeregQuery query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ComboBoxObject> getListFirstItems(PeregQuery query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GridPeregDomainDto> getAllData(PeregQueryByListEmp query) {
		// 社員ID　＝　社員ID　 and 特別休暇コード＝12
		return specialleaveInformationFinder.getAllData(query, SpecialLeaveCode.CS00050.value);
	}

	@Override
	public List<GridPeregDomainBySidDto> getListData(PeregQueryByListEmp query) {
		return specialleaveInformationFinder.getListData(query, SpecialLeaveCode.CS00050.value);
	}
}
