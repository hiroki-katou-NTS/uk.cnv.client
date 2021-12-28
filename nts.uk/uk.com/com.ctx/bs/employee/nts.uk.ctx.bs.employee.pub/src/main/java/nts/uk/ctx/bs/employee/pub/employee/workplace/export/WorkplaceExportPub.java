package nts.uk.ctx.bs.employee.pub.employee.workplace.export;

import java.util.List;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.bs.employee.pub.workplace.AffWorkplaceHistoryItemExport3;
import nts.uk.ctx.bs.employee.pub.workplace.master.WorkplaceInforExport;

/**
 * 
 * @author sonnh1
 *
 */
public interface WorkplaceExportPub {
	/**
	 * 職場IDから職場の階層コードを取得する
	 * 
	 * tuong tu RQ560
	 * 
	 * @param companyId
	 * @param listWkpId
	 * @param baseDate
	 */
	public List<WorkplaceExportPubDto> getAllWkpConfig(String companyId, List<String> listWkpId, GeneralDate baseDate);

	/**
	 * 過去の職場の階層コードを取得する
	 * 
	 * tuong tu RQ561
	 * 
	 * @param companyId
	 * @param listWkpId
	 * @param histId
	 */
	public List<WorkplaceExportPubDto> getPastWkpInfo(String companyId, List<String> listWkpId, String histId);
	
	//RQ560_HOATT_TAMTHOI
	public List<WkpExport> getWkpConfigRQ560(String companyId, List<String> listWkpId, GeneralDate baseDate);
	
	//RQ560_HieuTT
	public List<WorkplaceInforExport> getWkpRQ560(String companyId, List<String> listWkpId, GeneralDate baseDate);
	/**
	 * @name 全ての職場の所属社員を取得するPublish
	 * @param companyId 会社ID	会社ID
	 * @param baseDate 	基準日	年月日
	 * @return 	所属職場リスト	List＜所属職場履歴項目＞
	 */
	public List<AffWorkplaceHistoryItemExport3> getByCID(String companyId, GeneralDate baseDate);

	/**
	 * @name 職場に所属する社員Publish
	 * @param workPlaceIds 職場リスト List<職場ID>		
	 * @param baseDate 基準日	
	 * @return 	所属職場リスト	List＜所属職場履歴項目＞
	 */
	public List<AffWorkplaceHistoryItemExport3> getByListId(List<String> workPlaceIds, GeneralDate baseDate);

}
