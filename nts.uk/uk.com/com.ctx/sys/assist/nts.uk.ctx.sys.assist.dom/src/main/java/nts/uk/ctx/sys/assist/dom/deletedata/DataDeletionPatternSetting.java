package nts.uk.ctx.sys.assist.dom.deletedata;

import java.util.List;
import java.util.Optional;

import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.sys.assist.dom.storage.ContractCode;
import nts.uk.ctx.sys.assist.dom.storage.Explanation;
import nts.uk.ctx.sys.assist.dom.storage.FileCompressionPassword;
import nts.uk.ctx.sys.assist.dom.storage.PatternClassification;
import nts.uk.ctx.sys.assist.dom.storage.PatternCode;
import nts.uk.ctx.sys.assist.dom.storage.PatternName;
import nts.uk.ctx.sys.assist.dom.storage.TargetMonth;
import nts.uk.ctx.sys.assist.dom.storage.TargetYear;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.補助機能.データ削除.削除パターン設定.データ削除のパターン設定
 */
@Getter
public class DataDeletionPatternSetting extends AggregateRoot {
	
	/**
	 * パスワード有無
	 */
	private NotUseAtr withoutPassword;
	
	/**
	 * パターン区分
	 */
	private PatternClassification patternClassification;
	
	/**
	 * 削除パターンパスワード
	 */
	private Optional<FileCompressionPassword> patternCompressionPwd;
	
	/**
	 * 月次参照年
	 */
	private Optional<TargetYear> monthlyReferYear;
	
	/**
	 * 日次参照年
	 */
	private Optional<TargetYear> dailyReferYear;
	
	/**
	 * 年次参照年
	 */
	private Optional<TargetYear> annualReferYear;
	
	/**
	 * 日次参照月
	 */
	private Optional<TargetMonth> dailyReferMonth;
	
	/**
	 * 月次参照月
	 */
	private Optional<TargetMonth> monthlyReferMonth;
	
	/**
	 * 契約コード
	 */
	private ContractCode contractCode;
	
	/**
	 * パターンコード
	 */
	private PatternCode patternCode;
	
	/**
	 * パターン名
	 */
	private PatternName patternName;
	
	/**
	 * データ削除の選択カテゴリ
	 */
	private List<DataDeletionSelectionCategory> categories;
	
	/**
	 * 削除パターン補足説明
	 */
	private Optional<Explanation> patternSuppleExplanation;
	
	public static DataDeletionPatternSetting createFromMemento(MementoGetter memento) {
		DataDeletionPatternSetting domain = new DataDeletionPatternSetting();
		domain.getMemento(memento);
		return domain;
	}
	
	public void setMemento(MementoSetter memento) {
		memento.setAnnualReferYear(annualReferYear.map(data -> data.value).orElse(null));
		memento.setContractCode(contractCode.v());
		memento.setDailyReferMonth(dailyReferMonth.map(data -> data.value).orElse(null));
		memento.setDailyReferYear(dailyReferYear.map(data -> data.value).orElse(null));
		memento.setPatternCompressionPwd(patternCompressionPwd.map(FileCompressionPassword::v).orElse(""));
		memento.setMonthlyReferMonth(monthlyReferMonth.map(data -> data.value).orElse(null));
		memento.setMonthlyReferYear(monthlyReferYear.map(data -> data.value).orElse(null));
		memento.setPatternClassification(patternClassification.value);
		memento.setPatternCode(patternCode.v());
		memento.setPatternName(patternName.v());
		memento.setPatternSuppleExplanation(patternSuppleExplanation.map(Explanation::v).orElse(""));
		memento.setWithoutPassword(withoutPassword.value);
		memento.setCategories(categories);
	}
	
	public void getMemento(MementoGetter memento) {
		this.annualReferYear = memento.getAnnualReferYear() == null
								? Optional.empty()
								: Optional.of(EnumAdaptor.valueOf(memento.getAnnualReferYear(), TargetYear.class));
		this.contractCode = new ContractCode(memento.getContractCode());
		this.dailyReferMonth = memento.getDailyReferMonth() == null
								? Optional.empty()
								: Optional.of(EnumAdaptor.valueOf(memento.getDailyReferMonth(), TargetMonth.class));
		this.dailyReferYear = memento.getDailyReferYear() == null
								? Optional.empty()
								: Optional.of(EnumAdaptor.valueOf(memento.getDailyReferYear(), TargetYear.class));
		this.monthlyReferMonth = memento.getMonthlyReferMonth() == null
								? Optional.empty()
								: Optional.of(EnumAdaptor.valueOf(memento.getMonthlyReferMonth(), TargetMonth.class));
		this.monthlyReferYear = memento.getMonthlyReferYear() == null
								? Optional.empty()
								: Optional.of(EnumAdaptor.valueOf(memento.getMonthlyReferYear(), TargetYear.class));
		this.patternCompressionPwd = memento.getPatternCompressionPwd() == null 
								? Optional.empty()
								: Optional.of(new FileCompressionPassword(memento.getPatternCompressionPwd()));
		this.patternClassification = EnumAdaptor.valueOf(memento.getPatternClassification(), PatternClassification.class);
		this.patternCode = new PatternCode(memento.getPatternCode());
		this.patternName = new PatternName(memento.getPatternName());
		this.patternSuppleExplanation = memento.getPatternSuppleExplanation() == null 
								? Optional.empty()
								: Optional.of(new Explanation(memento.getPatternSuppleExplanation()));
		this.withoutPassword = EnumAdaptor.valueOf(memento.getWithoutPassword(), NotUseAtr.class);
		this.categories = memento.getCategories();
	}
	
	public static interface MementoGetter {
		String getPatternCode();
		String getPatternName();
		int getPatternClassification();
		String getContractCode();
		int getWithoutPassword();
		String getPatternCompressionPwd();
		Integer getDailyReferYear();
		Integer getDailyReferMonth();
		Integer getMonthlyReferYear();
		Integer getMonthlyReferMonth();
		Integer getAnnualReferYear();
		String getPatternSuppleExplanation();
		List<DataDeletionSelectionCategory> getCategories();
	}
	
	public static interface MementoSetter {
		void setPatternCode(String patternCode);
		void setPatternName(String patternName);
		void setPatternClassification(int patternClassification);
		void setContractCode(String contractCode);
		void setWithoutPassword(int withoutPassword);
		void setPatternCompressionPwd(String fileCompressionPassword);
		void setDailyReferYear(Integer dailyReferYear);
		void setDailyReferMonth(Integer dailyReferMonth);
		void setMonthlyReferYear(Integer monthlyReferYear);
		void setMonthlyReferMonth(Integer monthlyReferMonth);
		void setAnnualReferYear(Integer annualReferYear);
		void setPatternSuppleExplanation(String patternSuppleExplanation);
		void setCategories(List<DataDeletionSelectionCategory> categories);
	}
}
