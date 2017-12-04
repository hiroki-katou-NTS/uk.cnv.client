package find.person.info.item;

import java.util.List;

import lombok.Getter;

@Getter
public class ItemTypeStateDto {

	protected int itemType;
	
	public static ItemTypeStateDto createSetItemDto(List<String> items) {
		return SetItemDto.createFromJavaType(items);
	};

	public static ItemTypeStateDto createSingleItemDto(DataTypeStateDto dataTypeState) {
		return SingleItemDto.createFromJavaType(dataTypeState);
	};
	
}