package nts.uk.ctx.sys.assist.dom.storage;

import java.util.List;
import java.util.Optional;

import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.補助機能.データ保存.パターン設定.データ保存のパターン設定
 */
@Getter
public class DataStoragePatternSetting extends AggregateRoot {

	/**
	 * パスワード有無
	 */
	private NotUseAtr withoutPassword;

	/**
	 * パターンコード
	 */
	private PatternCode patternCode;

	/**
	 * パターン区分
	 */
	private PatternClassification patternClassification;

	/**
	 * パターン名
	 */
	private PatternName patternName;

	/**
	 * パターン圧縮パスワード
	 */
	private Optional<FileCompressionPassword> patternCompressionPwd;

	/**
	 * パターン補足説明
	 */
	private Optional<String> patternSuppleExplanation;

	/**
	 * 契約コード
	 */
	private ContractCode contractCode;

	/**
	 * 年次参照月
	 */
	private Optional<TargetYear> annualReferYear;

	/**
	 * 日次参照年
	 */
	private Optional<TargetYear> dailyReferYear;

	/**
	 * 日次参照月
	 */
	private Optional<TargetMonth> dailyReferMonth;

	/**
	 * 月次参照年
	 */
	private Optional<TargetYear> monthlyReferYear;

	/**
	 * 月次参照月
	 */
	private Optional<TargetMonth> monthlyReferMonth;

	/**
	 * 調査用保存の識別
	 */
	private SurveySettingCategory idenSurveyArch;
	
	/**
	 * データ保存の選択カテゴリ
	 */
	private List<DataStorageSelectionCategory> categories;

	public static DataStoragePatternSetting createFromMemento(MementoGetter memento) {
		DataStoragePatternSetting domain = new DataStoragePatternSetting();
		domain.getMemento(memento);
		return domain;
	}

	public void getMemento(MementoGetter memento) {
		this.annualReferYear = memento.getAnnualReferYear() != null 
							   ? Optional.of(EnumAdaptor.valueOf(memento.getAnnualReferYear(), TargetYear.class))
							   : Optional.empty();
		this.contractCode = new ContractCode(memento.getContractCode());
		this.dailyReferMonth = memento.getDailyReferMonth() != null 
				   ? Optional.of(EnumAdaptor.valueOf(memento.getDailyReferMonth(), TargetMonth.class))
				   : Optional.empty();
		this.dailyReferYear = memento.getDailyReferYear() != null 
				   ? Optional.of(EnumAdaptor.valueOf(memento.getDailyReferYear(), TargetYear.class))
				   : Optional.empty();
		this.idenSurveyArch = EnumAdaptor.valueOf(memento.getIdenSurveyArch(), SurveySettingCategory.class);
		this.monthlyReferMonth = memento.getMonthlyReferMonth() != null 
				   ? Optional.of(EnumAdaptor.valueOf(memento.getMonthlyReferMonth(), TargetMonth.class))
				   : Optional.empty();
		this.monthlyReferYear = memento.getMonthlyReferYear() != null 
				   ? Optional.of(EnumAdaptor.valueOf(memento.getMonthlyReferYear(), TargetYear.class))
				   : Optional.empty();
		this.patternClassification = EnumAdaptor.valueOf(memento.getPatternClassification(),
				PatternClassification.class);
		this.patternCode = new PatternCode(memento.getPatternCode());
		this.patternCompressionPwd = Optional
				.ofNullable(new FileCompressionPassword(memento.getPatternCompressionPwd()));
		this.patternName = new PatternName(memento.getPatternName());
		this.patternSuppleExplanation = Optional.ofNullable(memento.getPatternSuppleExplanation());
		this.withoutPassword = EnumAdaptor.valueOf(memento.getWithoutPassword(), NotUseAtr.class);
		this.categories = memento.getCategories();
	}

	public void setMemento(MementoSetter memento) {
		memento.setAnnualReferYear(annualReferYear.map(v -> v.value).orElse(null));
		memento.setContractCode(contractCode.v());
		memento.setDailyReferMonth(dailyReferMonth.map(v -> v.value).orElse(null));
		memento.setDailyReferYear(dailyReferYear.map(v -> v.value).orElse(null));
		memento.setIdenSurveyArch(idenSurveyArch.value);
		memento.setMonthlyReferMonth(monthlyReferMonth.map(v -> v.value).orElse(null));
		memento.setMonthlyReferYear(monthlyReferYear.map(v -> v.value).orElse(null));
		memento.setPatternClassification(patternClassification.value);
		memento.setPatternCode(patternCode.v());
		memento.setPatternCompressionPwd(patternCompressionPwd.map(FileCompressionPassword::v).orElse(null));
		memento.setPatternName(patternName.v());
		memento.setPatternSuppleExplanation(patternSuppleExplanation.orElse(null));
		memento.setWithoutPassword(withoutPassword.value);
		memento.setCategories(this.categories);
	}

	public static interface MementoSetter {
		void setWithoutPassword(int withoutPassword);

		void setPatternCode(String patternCode);

		void setPatternClassification(int patternClassification);

		void setPatternName(String patternName);

		void setPatternCompressionPwd(String patternCompressionPwd);

		void setPatternSuppleExplanation(String patternSuppleExplanation);

		void setContractCode(String contractCode);

		void setAnnualReferYear(Integer annualReferYear);

		void setDailyReferYear(Integer dailyReferYear);

		void setDailyReferMonth(Integer dailyReferMonth);

		void setMonthlyReferYear(Integer monthlyReferYear);

		void setMonthlyReferMonth(Integer monthlyReferMonth);

		void setIdenSurveyArch(int idenSurveyArch);
		
		void setCategories(List<DataStorageSelectionCategory> categories);
	}

	public static interface MementoGetter {
		int getWithoutPassword();

		String getPatternCode();

		int getPatternClassification();

		String getPatternName();

		String getPatternCompressionPwd();

		String getPatternSuppleExplanation();

		String getContractCode();

		Integer getAnnualReferYear();

		Integer getDailyReferYear();

		Integer getDailyReferMonth();

		Integer getMonthlyReferYear();

		Integer getMonthlyReferMonth();

		int getIdenSurveyArch();
		
		List<DataStorageSelectionCategory> getCategories();
	}
}
