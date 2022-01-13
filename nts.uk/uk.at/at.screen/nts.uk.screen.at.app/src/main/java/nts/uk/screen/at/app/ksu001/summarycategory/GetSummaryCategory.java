package nts.uk.screen.at.app.ksu001.summarycategory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.aggregation.app.find.schedulecounter.personal.PersonalCounterCategoryDto;
import nts.uk.ctx.at.aggregation.app.find.schedulecounter.personal.PersonalCounterFinder;
import nts.uk.ctx.at.aggregation.app.find.schedulecounter.wkpcounter.WorkplaceCounterCategoryDto;
import nts.uk.ctx.at.aggregation.app.find.schedulecounter.wkpcounter.WorkplaceCounterFinder;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.PersonalCounter;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.PersonalCounterCategory;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.WorkplaceCounter;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.WorkplaceCounterCategory;

/**
 *  集計カテゴリを取得する
 *  UKDesign.UniversalK.就業.KSU_スケジュール.KSU001_個人スケジュール修正(職場別).A：個人スケジュール修正(職場別).メニュー別OCD
 * @author hoangnd
 *
 */
@Stateless
public class GetSummaryCategory {

	@Inject
	private WorkplaceCounterFinder workplaceCounterFinder;
	
	@Inject
	private PersonalCounterFinder personalCounterFinder;
	
	/**
	 * 
	 * @param displayFormat 表示形式
	 * @return
	 */
	public SummaryCategoryDto get(String displayFormat) {
		SummaryCategoryDto output = new SummaryCategoryDto();
		// 1 取得する()
		Optional<WorkplaceCounter> workplaceCounterOp = Optional.empty();
		List<WorkplaceCounterCategoryDto> workplaceCounterCategorys = 
					workplaceCounterFinder.findById();
		if (!CollectionUtil.isEmpty(workplaceCounterCategorys)) {
			workplaceCounterOp = Optional.of(WorkplaceCounter.create(
					workplaceCounterCategorys.stream()
											 .filter(WorkplaceCounterCategoryDto::isUse)
											 .map(x -> EnumAdaptor.valueOf(x.getValue(), WorkplaceCounterCategory.class))
											 .collect(Collectors.toList())
											 
					));
		}
		if (workplaceCounterOp.isPresent()) {
			output.setUseCategoriesWorkplace(EnumAdaptor.convertToValueNameList(WorkplaceCounterCategory.class, workplaceCounterOp.get().getUseCategories()));
		}
		
		// 2 スケジュール個人計情報を取得する()
		Optional<PersonalCounter> personalCounterOp = Optional.empty();
		
		List<PersonalCounterCategoryDto> personalCounterCategory =
				personalCounterFinder.findById();
		
		if (!CollectionUtil.isEmpty(personalCounterCategory)) {
			
			personalCounterOp = Optional.of(PersonalCounter.create(
					personalCounterCategory.stream()
										   .filter(PersonalCounterCategoryDto::isUse)
								   		   .map(x -> EnumAdaptor.valueOf(x.getValue(), PersonalCounterCategory.class))
								   		   .collect(Collectors.toList())
								   		   ));
		}
		if (personalCounterOp.isPresent()) {
			
			output.setUseCategoriesPersonal(EnumAdaptor.convertToValueNameList(PersonalCounterCategory.class, personalCounterOp.get().getUseCategories()));
		}
		
		return output;
	}
}
