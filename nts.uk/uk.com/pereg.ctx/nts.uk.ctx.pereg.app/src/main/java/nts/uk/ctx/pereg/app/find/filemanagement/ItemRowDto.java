package nts.uk.ctx.pereg.app.find.filemanagement;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.pereg.app.find.layoutdef.classification.ActionRole;
import nts.uk.shr.pereg.app.ComboBoxObject;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRowDto {
	 private String itemCode;
	 private String itemName;
	 private int dataType;
	 private Object value;
	 private String textValue;
	 private String recordId;
	 private int itemOrder;
	 private ActionRole actionRole;
	 private List<ComboBoxObject> lstComboBoxValue;
	 private boolean error;
	 
	 public ItemRowDto(String itemCode, String itemName, Object value, String textValue, int itemOrder, List<ComboBoxObject> lstComboBoxValue) {
		 this.itemCode = itemCode;
		 this.itemName = itemName;
		 this.value = value;
		 this.textValue = textValue;
		 this.itemOrder = itemOrder;
		 this.lstComboBoxValue = lstComboBoxValue;
	 }

}
