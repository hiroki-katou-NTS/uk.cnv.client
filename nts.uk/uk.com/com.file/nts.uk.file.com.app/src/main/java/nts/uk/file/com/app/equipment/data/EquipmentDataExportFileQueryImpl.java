package nts.uk.file.com.app.equipment.data;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.layer.app.file.export.ExportServiceContext;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.bs.company.dom.company.Company;
import nts.uk.ctx.bs.company.dom.company.CompanyRepository;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentFormSetting;
import nts.uk.ctx.office.dom.equipment.achievement.repo.EquipmentFormSettingRepository;
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassification;
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassificationRepository;
import nts.uk.ctx.office.dom.equipment.data.EquipmentData;
import nts.uk.ctx.office.dom.equipment.data.EquipmentDataRepository;
import nts.uk.ctx.office.dom.equipment.information.EquipmentInformation;
import nts.uk.ctx.office.dom.equipment.information.EquipmentInformationRepository;
import nts.uk.file.com.app.equipment.data.dto.EquipmentUsageRecordItemSettingDto;
import nts.uk.query.model.employee.EmployeeInformation;
import nts.uk.query.model.employee.EmployeeInformationQuery;
import nts.uk.query.model.employee.EmployeeInformationRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class EquipmentDataExportFileQueryImpl implements EquipmentDataExportFileQuery {

	@Inject
	private EquipmentDataExportGenerator generator;

	@Inject
	private EquipmentDataRepository equipmentDataRepository;

	@Inject
	private EquipmentInformationRepository equipmentInformationRepository;

	@Inject
	private EquipmentClassificationRepository equipmentClassificationRepository;

	@Inject
	private EquipmentFormSettingRepository equipmentFormSettingRepository;

	@Inject
	private EmployeeInformationRepository employeeInformationRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Override
	public void handle(ExportServiceContext<EquipmentDataQuery> context, EquipmentDataQuery query) {
		EquipmentDataExportDataSource dataSource = this.getExportData(query);
		this.generator.generate(context.getGeneratorContext(), dataSource);
	}

	/**
	 * UKDesign.UniversalK.オフィス.OEW_設備管理.OEW001_設備利用の入力.Ｃ：設備利用の出力ダイアログ.メニュー別OCD.Ｃ：設備利用実績一覧を作成する.Ｃ：設備利用実績一覧を作成する
	 */
	@Override
	public EquipmentDataExportDataSource getExportData(EquipmentDataQuery query) {
		// Init input
		Optional<String> optEquipmentClsCode = Optional.ofNullable(query.getEquipmentClsCode());
		Optional<String> optEquipmentCode = Optional.ofNullable(query.getEquipmentCode());
		YearMonth ym = YearMonth.of(query.getYm());
		String cid = AppContexts.user().companyId();
		EquipmentDataReportType reportType = EnumAdaptor.valueOf(query.getPrintType(), EquipmentDataReportType.class);

		// $対象期間 = 期間# 1ヶ月の期間を作る(INPUT「年月」)
		DatePeriod period = DatePeriod.daysFirstToLastIn(ym);

		// 1.期間、設備コード、設備分類コードから実績データを取得する(ログイン会社ID、$対象期間、Optional<設備コード>、Optional<設備分類コード>)
		List<EquipmentData> equipmentDatas = this.equipmentDataRepository.findByPeriodAndOptionalInput(
				AppContexts.user().companyId(), period, optEquipmentCode, optEquipmentClsCode);
		// 2.[設備利用実績データ<List> IS NULL]
		if (equipmentDatas.isEmpty()) {
			throw new BusinessException("Msg_2240");
		}
		// 3.<call>(ログイン会社ID)
		Optional<Company> optCompanyInfo = this.companyRepository.getComanyNotAbolitionByCid(cid);
		// 4.get(ログイン会社ID)
		Optional<EquipmentFormSetting> optEquipmentFormSetting = this.equipmentFormSettingRepository.findByCid(cid);
		// 設備名称リスト＝設備利用実績データ<List>：
		// map $.設備コード
		// distinct
		List<String> codes = equipmentDatas.stream().map(data -> data.getEquipmentCode().v()).distinct()
				.collect(Collectors.toList());
		// 5.get(設備コード)
		List<EquipmentInformation> equipmentInfos = this.equipmentInformationRepository.findByCidAndCodes(cid, codes);
		// 設備分類名称リスト＝設備利用実績データ<List>：
		// map $.設備分類コード
		// distinct
		List<String> clsCodes = equipmentDatas.stream().map(data -> data.getEquipmentClassificationCode().v())
				.distinct().collect(Collectors.toList());
		// 6.get(設備分類コード)
		List<EquipmentClassification> equipmentClassifications = this.equipmentClassificationRepository
				.getFromClsCodeList(AppContexts.user().contractCode(), clsCodes);
		// 社員名称リスト＝設備利用実績データ<List>：
		// map $.社員ID
		// distinct
		List<String> sids = equipmentDatas.stream().map(EquipmentData::getSid).distinct().collect(Collectors.toList());
		// 7.<call>(社員ID)
		EmployeeInformationQuery param = EmployeeInformationQuery.builder().employeeIds(sids)
				.referenceDate(GeneralDate.today()).toGetClassification(false).toGetDepartment(false)
				.toGetEmployment(false).toGetEmploymentCls(false).toGetPosition(false).toGetWorkplace(false).build();
		List<EmployeeInformation> employees = this.employeeInformationRepository.find(param);
		return EquipmentDataExportDataSource.builder().employees(employees)
				.equipmentClassifications(equipmentClassifications).equipmentDatas(equipmentDatas)
				.equipmentInfos(equipmentInfos).formSetting(optEquipmentFormSetting).companyInfo(optCompanyInfo)
				.itemSettings(query.getItemSettings().stream().map(EquipmentUsageRecordItemSettingDto::toDomain)
						.collect(Collectors.toList()))
				.optEquipmentClsCode(optEquipmentClsCode).optEquipmentInfoCode(optEquipmentCode)
				.formatSetting(query.getFormatSetting().toDomain()).reportType(reportType).yearMonth(ym).build();
	}
}
