package nts.uk.ctx.at.shared.dom.workrule.shiftmaster;

import java.util.List;
import java.util.Optional;

import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.dto.ShiftMasterDto;

public interface ShiftMasterRepository {

	public List<ShiftMasterDto> getAllByCid(String companyId);

	public Optional<ShiftMaster> getByShiftMaterCd(String companyId, String shiftMaterCode);

	public Optional<ShiftMaster> getByWorkTypeAndWorkTime(String companyId, String workTypeCd, String workTimeCd);

	public boolean checkExists(String companyId, String workTypeCd, String workTimeCd);
	
	public boolean checkExistsByCd(String companyId, String shiftMaterCode);
	
	public boolean checkExistByImportCd(String companyId, ShiftMasterImportCode importCd);

	public void insert(ShiftMaster shiftMater);

	public void update(ShiftMaster shiftMater);

	public void delete(String companyId, String shiftMaterCode);
	
	public List<ShiftMasterDto> getByListShiftMaterCd(String companyId, List<String> listShiftMaterCode);

	List<ShiftMasterDto> getAllDtoByCid(String companyId);

	/** [9] *get(会社ID, List<勤務情報>) **/
	List<ShiftMaster> get(String companyID , List<WorkInformation> lstWorkInformation);

	/** [7] *get(会社ID, List<シフトマスタコード>) **/
	List<ShiftMaster> getByListShiftMaterCd2(String companyId, List<String> shiftMaterCodes);
	
	List<ShiftMaster> getByListImportCodes(String companyId, List<String> importCodes);
	
	/**
	 * exists (会社ID, シフトマスタ取り込みコード)
	 * @param companyId 会社ID
	 * @param importCode シフトマスタ取り込みコード
	 * @return
	 */
	boolean exists (String companyId, ShiftMasterImportCode importCode);

	/**
	 * get (会社ID, シフトマスタ取り込みコード)
	 * @param companyId 会社ID
	 * @param importCode シフトマスタ取り込みコード
	 * @return
	 */
	Optional<ShiftMaster> getShiftMaster(String companyId, ShiftMasterImportCode importCode);
}
