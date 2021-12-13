package nts.uk.ctx.at.aggregation.dom.scheduletable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainAggregate;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.PersonalCounterCategory;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.WorkplaceCounterCategory;

/**
 * スケジュール表の出力設定
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.予実集計.スケジュール表.スケジュール表の出力設定
 * @author dan_pv
 *
 */
@AllArgsConstructor
@Getter
public class ScheduleTableOutputSetting implements DomainAggregate{
	
	/**
	 *  コード
	 */
	private final OutputSettingCode code;
	
	/**
	 * 	名称
	 */
	private OutputSettingName name;
	
	/**
	 * 出力項目
	 */
	private OutputItem outputItem;
	
	/**
	 * 職場計カテゴリ一覧
	 */
	private List<WorkplaceCounterCategory> workplaceCounterCategories;
	
	/**
	 * 個人計カテゴリ一覧
	 */
	private List<PersonalCounterCategory> personalCounterCategories;
	
	/**
	 * 作る
	 * @param code コード
	 * @param name 名称
	 * @param outputItem 出力項目
	 * @param workplaceCounterCategories 職場計カテゴリ一覧
	 * @param personalCounterCategories 個人計カテゴリ一覧
	 * @return
	 */
	public static ScheduleTableOutputSetting create(
			OutputSettingCode code,
			OutputSettingName name,
			OutputItem outputItem,
			List<WorkplaceCounterCategory> workplaceCounterCategories,
			List<PersonalCounterCategory> personalCounterCategories
			) {
		
		if ( workplaceCounterCategories.size() != new HashSet<>(workplaceCounterCategories).size()
				|| personalCounterCategories.size() != new HashSet<>(personalCounterCategories).size()) {
			throw new RuntimeException("List is duplicated");
		}
		
		return new ScheduleTableOutputSetting(code, name, outputItem, workplaceCounterCategories, personalCounterCategories);
		
	}
	
	/**
	 * 複製する
	 * @param newCode コード
	 * @param newName 名称
	 * @return
	 * @throws CloneNotSupportedException 
	 */
	public ScheduleTableOutputSetting clone(
			OutputSettingCode newCode,
			OutputSettingName newName) {
		
		return new ScheduleTableOutputSetting(
				newCode, 
				newName, 
				this.outputItem.clone(),
				new ArrayList<>(this.workplaceCounterCategories),
				new ArrayList<>(this.personalCounterCategories));
	}

}
