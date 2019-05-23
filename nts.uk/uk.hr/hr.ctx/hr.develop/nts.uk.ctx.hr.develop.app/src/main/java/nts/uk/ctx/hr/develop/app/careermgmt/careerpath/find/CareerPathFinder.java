package nts.uk.ctx.hr.develop.app.careermgmt.careerpath.find;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.hr.develop.app.careermgmt.careerpath.dto.MasterCareer;
import nts.uk.ctx.hr.develop.app.careermgmt.careerpath.dto.Career;
import nts.uk.ctx.hr.develop.app.careermgmt.careerpath.dto.CareerPartDto;
import nts.uk.ctx.hr.develop.dom.careermgmt.careerclass.algorithm.CareerClassService;
import nts.uk.ctx.hr.develop.dom.careermgmt.careerpath.CareerPath;
import nts.uk.ctx.hr.develop.dom.careermgmt.careerpath.algorithm.careerpath.CareerPathService;
import nts.uk.ctx.hr.develop.dom.careermgmt.careerpath.algorithm.careerpathhistory.CareerPathHistService;
import nts.uk.ctx.hr.develop.dom.careermgmt.careertype.algorithm.CareerTypeService;
import nts.uk.ctx.hr.develop.dom.careermgmt.setting.algorithm.CarrierManagementOperationSetting;

@Stateless
public class CareerPathFinder {

	@Inject
	private CareerPathService careerPathService;
	
	@Inject
	private CarrierManagementOperationSetting carrierManagementOperationSetting;
	
	@Inject
	private CareerPathHistService careerPathHistService;
	
	@Inject
	private CareerTypeService careerTypeService;
	
	@Inject
	private CareerClassService careerClassService;
	
	//更新モードでキャリアパスを画面に表示する
	public CareerPartDto getCareerPath(String companyId, String hisId, GeneralDate referenceDate){
		Optional<CareerPath> careerPath = careerPathService.getCareerPath(companyId, hisId);
		if(careerPath.isPresent()) {
			int maxClassLevel = careerPath.get().getMaxClassLevel().v();
			//キャリアリスト
			List<Career> listCareer = careerPath.get().getCareerList().stream().map(c-> new Career(c.getCareerTypeItem(), c.getCareerLevel().v(), c.getCareerClassItem())).collect(Collectors.toList());
			//職務マスタリストの取得
			List<MasterCareer> listCareerType = careerTypeService.getCareerPath(companyId, referenceDate).stream().map(c-> new MasterCareer(c.getCareerTypeId(), c.getCareerTypeCode().v(), c.getCareerTypeName().v())).collect(Collectors.toList());
			//キャリアマスタリストの取得
			List<MasterCareer> listCareerClass = careerClassService.getCareerPath(companyId, referenceDate).stream().map(c-> new MasterCareer(c.getCareerClassId(), c.getCareerClassCode().v(), c.getCareerClassName().v())).collect(Collectors.toList());
			
			return new CareerPartDto(false, maxClassLevel, listCareerType, listCareerClass, listCareer);
		}
		//新規モードでキャリアパスを画面に表示する
		return this.getCareerPartMaster(companyId, hisId);
	}
	
	//新規モードでキャリアパスを画面に表示する
	private CareerPartDto getCareerPartMaster(String companyId, String hisId) {
		//最大階層レベルの取得
		int maxClassLevel = carrierManagementOperationSetting.getCareerPathMaxClassLevel(companyId);
		//開始日の取得
		GeneralDate startDate = careerPathHistService.getCareerPathStartDate(companyId, hisId);
		//職務マスタリストの取得
		List<MasterCareer> listCareerType = careerTypeService.getCareerPath(companyId, startDate).stream().map(c-> new MasterCareer(c.getCareerTypeId(), c.getCareerTypeCode().v(), c.getCareerTypeName().v())).collect(Collectors.toList());
		//キャリアマスタリストの取得
		List<MasterCareer> listCareerClass = careerClassService.getCareerPath(companyId, startDate).stream().map(c-> new MasterCareer(c.getCareerClassId(), c.getCareerClassCode().v(), c.getCareerClassName().v())).collect(Collectors.toList());
		
		return new CareerPartDto(false, maxClassLevel, listCareerType, listCareerClass, new ArrayList<>());
	}
}
