package nts.uk.screen.com.app.find.equipment.data;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationAdapter;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationImport;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationQueryDtoImport;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentPerformInputFormatSetting;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentUsageRecordItemSetting;
import nts.uk.ctx.office.dom.equipment.achievement.repo.EquipmentFormatSettingRepository;
import nts.uk.ctx.office.dom.equipment.achievement.repo.EquipmentRecordItemSettingRepository;
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassification;
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassificationRepository;
import nts.uk.ctx.office.dom.equipment.data.EquipmentData;
import nts.uk.ctx.office.dom.equipment.data.EquipmentDataRepository;
import nts.uk.ctx.office.dom.equipment.information.EquipmentInformation;
import nts.uk.ctx.office.dom.equipment.information.EquipmentInformationRepository;
import nts.uk.screen.com.app.find.equipment.achievement.EquipmentPerformInputFormatSettingDto;
import nts.uk.screen.com.app.find.equipment.achievement.EquipmentUsageRecordItemSettingDto;
import nts.uk.screen.com.app.find.equipment.classification.EquipmentClassificationDto;
import nts.uk.screen.com.app.find.equipment.information.EquipmentInformationDto;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EquipmentDataScreenQuery {

	@Inject
	private EquipmentClassificationRepository equipmentClassificationRepository;

	@Inject
	private EquipmentInformationRepository equipmentInformationRepository;

	@Inject
	private EquipmentDataRepository equipmentDataRepository;

	@Inject
	private EquipmentRecordItemSettingRepository equipmentRecordItemSettingRepository;

	@Inject
	private EquipmentFormatSettingRepository equipmentFormatSettingRepository;

	@Inject
	private EmployeeInformationAdapter employeeInformationAdapter;

	/**
	 * UKDesign.UniversalK.オフィス.OEW_設備管理.OEW001_設備利用の入力.Ａ：設備利用実績の一覧.メニュー別OCD.Ａ1：設備分類と設備情報を取得する
	 * 
	 * @param equipmentClsCode 設備利用の入力の初期選択．設備分類コード
	 * @return
	 */
	public EquipmentInitInfoDto initEquipmentInfo(Optional<String> equipmentClsCode) {
		// 1.初期の設備分類を取得する(ログイン契約コード、optional<設備利用の入力の初期選択．設備分類コード>)
		Optional<EquipmentClassification> optEquipmentCls = this.equipmentClassificationRepository
				.getByOptionalCode(AppContexts.user().contractCode(), equipmentClsCode);
		// 2.設備分類<List> IS NULL
		if (!optEquipmentCls.isPresent()) {
			throw new BusinessException("Msg_2234");
		}
		EquipmentClassificationDto eqClsDto = new EquipmentClassificationDto(optEquipmentCls.get().getCode().v(),
				optEquipmentCls.get().getName().v());
		// 3.分類コードの有効の設備情報を取得する(ログイン会社ID、設備分類．設備分類コード、システム日付)
		List<EquipmentInformation> equipmentInfoList = this.equipmentInformationRepository.findByClsCodeAndDate(
				AppContexts.user().companyId(), optEquipmentCls.get().getCode().v(), GeneralDate.today());
		return new EquipmentInitInfoDto(eqClsDto,
				equipmentInfoList.stream().map(EquipmentInformationDto::fromDomain).collect(Collectors.toList()));
	}

	/**
	 * UKDesign.UniversalK.オフィス.OEW_設備管理.OEW001_設備利用の入力.Ａ：設備利用実績の一覧.メニュー別OCD.Ａ2：「設備利用実績の項目設定」を取得する
	 * 
	 * @return
	 */
	public EquipmentInitSettingDto initEquipmentSetting() {
		String cid = AppContexts.user().companyId();
		// 1.get(ログイン会社ID)
		List<EquipmentUsageRecordItemSetting> itemSettings = this.equipmentRecordItemSettingRepository.findByCid(cid);
		// 2.get(ログイン会社ID)
		Optional<EquipmentPerformInputFormatSetting> formatSetting = this.equipmentFormatSettingRepository.get(cid);
		return new EquipmentInitSettingDto(
				itemSettings.stream().map(EquipmentUsageRecordItemSettingDto::fromDomain).collect(Collectors.toList()),
				formatSetting.map(EquipmentPerformInputFormatSettingDto::fromDomain).orElse(null));
	}

	/**
	 * UKDesign.UniversalK.オフィス.OEW_設備管理.OEW001_設備利用の入力.Ａ：設備利用実績の一覧.メニュー別OCD.Ａ3：「実績の抽出処理」を実行する
	 * 
	 * @param equipmentClsCode 設備分類コード
	 * @param equipmentCode    設備コード
	 * @param ym               年月
	 * @return
	 */
	public EquipmentDataResultDto getResultHistory(String equipmentClsCode, String equipmentCode, YearMonth ym) {
		// $対象期間 = 期間# 1ヶ月の期間を作る(INPUT「年月」)
		DatePeriod period = DatePeriod.daysFirstToLastIn(ym);
		// 1.get(ログイン会社ID、$対象期間、設備コード、設備分類コード)
		List<EquipmentData> equipmentDatas = this.equipmentDataRepository
				.findByEquipmentClsCodeAndEquipmentCodeAndPeriod(AppContexts.user().companyId(), equipmentClsCode,
						equipmentCode, period);
		// 社員名称リスト＝設備利用実績データ<List>：
		// map $.社員ID
		// distinct
		List<String> sids = equipmentDatas.stream().map(EquipmentData::getSid).distinct().collect(Collectors.toList());
		// 社員情報<List>
		EmployeeInformationQueryDtoImport param = new EmployeeInformationQueryDtoImport(sids, GeneralDate.today(),
				false, false, false, false, false, false);
		List<EmployeeInformationImport> employeeInfos = this.employeeInformationAdapter.getEmployeeInfo(param);
		return new EquipmentDataResultDto(
				equipmentDatas.stream().map(EquipmentDataDto::fromDomain).collect(Collectors.toList()), employeeInfos);
	}

	/**
	 * UKDesign.UniversalK.オフィス.OEW_設備管理.OEW001_設備利用の入力.Ａ：設備利用実績の一覧.メニュー別OCD.Ａ4C1：設備分類の設備を取得する
	 * 
	 * @param equipmentClsCode 設備分類コード
	 * @param baseDate         基準日
	 * @param isInput          入力する
	 * @return
	 */
	public List<EquipmentInformationDto> getEquipmentInfoList(String equipmentClsCode, GeneralDate baseDate,
			boolean isInput) {
		// 1. get(ログイン会社ID、設備分類コード)
		List<EquipmentInformation> equipmentInfos = this.equipmentInformationRepository
				.findByCidAndClsCode(AppContexts.user().companyId(), equipmentClsCode);
		// 2. [入力する＝する]:有効期限内か(年月日)
		// 「入力する」の場合は、設備情報<List>で基準日に有効な設備のみを返す
		// 「入力しない」の場合は、設備情報<List>で基準日に関係なく全部の設備を返す
		if (isInput) {
			equipmentInfos = equipmentInfos.stream().filter(data -> data.isValid(baseDate))
					.collect(Collectors.toList());
		}
		return equipmentInfos.stream().map(EquipmentInformationDto::fromDomain).collect(Collectors.toList());
	}
}
