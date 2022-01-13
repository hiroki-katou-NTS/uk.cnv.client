package nts.uk.ctx.sys.assist.app.find.autosetting.storage;

import java.util.List;

import lombok.Data;
import nts.uk.ctx.sys.assist.app.find.autosetting.AbstractCategoryDto;
import nts.uk.ctx.sys.assist.dom.storage.DataStoragePatternSetting;
import nts.uk.ctx.sys.assist.dom.storage.DataStorageSelectionCategory;

@Data
public class DataStoragePatternSettingDto<X extends AbstractCategoryDto> implements DataStoragePatternSetting.MementoSetter {
	
	/**
	 * パターンコード
	 */
	private String patternCode;

	/**
	 * パターン区分
	 */
	private int patternClassification;

	/**
	 * パターン名
	 */
	private String patternName;

	/**
	 * 契約コード
	 */
	private String contractCode;

	/**
	 *List<選択カテゴリ名称> 
	 */
	private List<X> selectCategories;

	/**
	 * 調査用保存可否
	 */
	private int idenSurveyArch;

	/**
	 * 日次対象年
	 */
	private Integer dailyReferYear;

	/**
	 * 日次対象月
	 */
	private Integer dailyReferMonth;

	/**
	 * 月次対象年
	 */
	private Integer monthlyReferYear;

	/**
	 * 月次対象月
	 */
	private Integer monthlyReferMonth;

	/**
	 * 年次対象年
	 */
	private Integer annualReferYear;

	/**
	 * 圧縮パスワード有無
	 */
	private int withoutPassword;

	/**
	 * パターン圧縮パスワード
	 */
	private String patternCompressionPwd;

	/**
	 * パターン補足説明
	 */
	private String patternSuppleExplanation;

	@Override
	public void setCategories(List<DataStorageSelectionCategory> categories) {
		//NOT USED
	}

	/**
	 * Creates from domain.
	 *
	 * @param domain the domain
	 * @return the Data storage pattern setting dto
	 */
	public static <X extends AbstractCategoryDto> DataStoragePatternSettingDto<X> createFromDomain(
			DataStoragePatternSetting domain) {
		if (domain == null) {
			return null;
		}
		DataStoragePatternSettingDto<X> dto = new DataStoragePatternSettingDto<>();
		domain.setMemento(dto);
		return dto;
	}

	@Override
	public void setWithoutPassword(boolean withoutPassword) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPatternClassification(boolean patternClassification) {
		// TODO Auto-generated method stub
		
	}
}
