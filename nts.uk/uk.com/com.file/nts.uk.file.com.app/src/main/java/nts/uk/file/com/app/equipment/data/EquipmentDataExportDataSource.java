package nts.uk.file.com.app.equipment.data;

import java.util.List;
import java.util.Optional;

import lombok.Builder;
import lombok.Data;
import nts.arc.time.YearMonth;
import nts.uk.ctx.bs.company.dom.company.Company;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentFormSetting;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentPerformInputFormatSetting;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentUsageRecordItemSetting;
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassification;
import nts.uk.ctx.office.dom.equipment.data.EquipmentData;
import nts.uk.ctx.office.dom.equipment.information.EquipmentInformation;
import nts.uk.query.model.employee.EmployeeInformation;

@Data
@Builder
public class EquipmentDataExportDataSource {
	/**
	 * 設備利用実績データ<List>
	 */
	private List<EquipmentData> equipmentDatas;
	
	/**
	 * 会社情報
	 */
	private Optional<Company> companyInfo;
	
	/**
	 * 設備帳票設定
	 */
	private Optional<EquipmentFormSetting> formSetting;
	
	/**
	 * 設備情報<List>
	 */
	private List<EquipmentInformation> equipmentInfos;
	
	/**
	 * 設備分類<List>
	 */
	private List<EquipmentClassification> equipmentClassifications;
	
	/**
	 * 社員情報<List>
	 */
	private List<EmployeeInformation> employees;
	
	// export params
	private Optional<String> optEquipmentClsCode;
	private Optional<String> optEquipmentInfoCode;
	private EquipmentDataReportType reportType;
	private YearMonth yearMonth;
	private List<EquipmentUsageRecordItemSetting> itemSettings;
	private EquipmentPerformInputFormatSetting formatSetting;
}

