package nts.uk.screen.at.app.ksu001.summarycategory;

import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.enums.EnumConstant;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SummaryCategoryDto {
	// 職場計.利用カテゴリ一覧
	public List<EnumConstant> useCategoriesWorkplace = Collections.emptyList();
	
	// 個人計.利用カテゴリ一覧
	public List<EnumConstant> useCategoriesPersonal = Collections.emptyList();

	
	
}
