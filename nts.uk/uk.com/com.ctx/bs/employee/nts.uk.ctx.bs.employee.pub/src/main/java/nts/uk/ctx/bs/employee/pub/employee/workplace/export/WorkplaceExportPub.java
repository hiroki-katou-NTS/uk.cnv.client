package nts.uk.ctx.bs.employee.pub.employee.workplace.export;

import java.util.List;

import nts.arc.time.GeneralDate;
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
}
