package nts.uk.ctx.bs.employee.pubimp.employee.workplace.export;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.bs.employee.dom.workplace.export.WorkplaceExport;
import nts.uk.ctx.bs.employee.dom.workplace.master.WorkplaceInformationRepository;
import nts.uk.ctx.bs.employee.pub.employee.workplace.export.WkpExport;
import nts.uk.ctx.bs.employee.pub.employee.workplace.export.WorkplaceExportPub;
import nts.uk.ctx.bs.employee.pub.employee.workplace.export.WorkplaceExportPubDto;
import nts.uk.ctx.bs.employee.pub.workplace.AffWorkplaceHistoryItemExport3;
import nts.uk.ctx.bs.employee.pub.workplace.master.WorkplaceInforExport;
import nts.uk.ctx.bs.employee.pub.workplace.master.WorkplacePub;

/**
 * 
 * @author sonnh1
 *
 */
@Stateless
public class WorkplaceExportPubImpl implements WorkplaceExportPub {

	@Inject
	private WorkplaceExport workplaceExport;

	@Inject
	private WorkplaceInformationRepository workplaceConfigInfoRepository;
	
	@Inject
	private WorkplacePub workplacePub;

	@Override
	public List<WorkplaceExportPubDto> getAllWkpConfig(String companyId, List<String> listWkpId, GeneralDate baseDate) {
		return this.workplaceExport.getAllWkpConfig(companyId, listWkpId, baseDate).stream()
				.map(x -> new WorkplaceExportPubDto(x.getWorkplaceId(), x.getHierarchyCd()))
				.collect(Collectors.toList());
	}

	@Override
	public List<WorkplaceExportPubDto> getPastWkpInfo(String companyId, List<String> listWkpId, String histId) {
		return this.workplaceExport.getPastWkpInfo(companyId, listWkpId, histId).stream()
				.map(x -> new WorkplaceExportPubDto(x.getWorkplaceId(), x.getHierarchyCd()))
				.collect(Collectors.toList());
	}

	@Override
	public List<WkpExport> getWkpConfigRQ560(String companyId, List<String> listWkpId, GeneralDate baseDate) {
		return this.workplaceExport.getWkpConfigRQ560(companyId, listWkpId, baseDate).stream()
				.map(x -> new WkpExport(x.getWorkplaceId(), x.getWorkplaceName())).collect(Collectors.toList());
	}

	@Override
	public List<WorkplaceInforExport> getWkpRQ560(String companyId, List<String> listWkpId, GeneralDate baseDate) {
		return this.workplaceConfigInfoRepository.findByBaseDateWkpIds2(companyId, listWkpId, baseDate).stream()
				.map(x -> new WorkplaceInforExport(x.getWorkplaceId(), x.getHierarchyCode().v(),
						x.getWorkplaceCode().v(), x.getWorkplaceName().v(), x.getWorkplaceDisplayName().v(),
						x.getWorkplaceGeneric().v(), x.getWorkplaceExternalCode().map(a -> a.v()).orElse("")))
				.collect(Collectors.toList());
	}

	@Override
	public List<AffWorkplaceHistoryItemExport3> getByCID(String companyId, GeneralDate baseDate) {
		//$職場情報一覧 = [No.559]運用している職場の情報をすべて取得する
		List<WorkplaceInforExport> list = workplacePub.getAllActiveWorkplaceInfor(companyId, baseDate);
		//	$職場リスト = $職場情報一覧：map $.職場ID
		List<String> workPlaceIds = list.stream().map(c->c.getWorkplaceId()).collect(Collectors.toList());
		//		return 職場（List）と基準日から所属職場履歴項目を取得する(職場リスト,基準日)
		return workplacePub.getWorkHisItemfromWkpIdsAndBaseDate(workPlaceIds, baseDate);
	}

	@Override
	public List<AffWorkplaceHistoryItemExport3> getByListId(List<String> workPlaceIds, GeneralDate baseDate) {
		//return 職場（List）と基準日から所属職場履歴項目を取得する(職場リスト,基準日)
		return workplacePub.getWorkHisItemfromWkpIdsAndBaseDate(workPlaceIds, baseDate);
	}
}
