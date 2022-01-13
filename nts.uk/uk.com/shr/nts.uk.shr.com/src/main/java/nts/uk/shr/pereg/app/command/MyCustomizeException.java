package nts.uk.shr.pereg.app.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import nts.arc.error.BusinessException;
@Getter
@Setter
public class MyCustomizeException extends BusinessException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private List<String> errorLst = new ArrayList<>();
	
	private String itemName;
	
	private Map<String, String> recordIdBySid = new HashMap<>();

	public MyCustomizeException(String messageId, List<String> errorLst) {
		super(messageId);
		this.errorLst.addAll(errorLst);
	}
	
	public MyCustomizeException(String messageId, Map<String, String> recordIdBySid) {
		super(messageId);
		if(!recordIdBySid.isEmpty()) {
			this.recordIdBySid.putAll(recordIdBySid);
		}
		
	}
	
	public MyCustomizeException(String messageId, List<String> errorLst, String itemName) {
		super(messageId);
		this.errorLst.addAll(errorLst);
		this.itemName = itemName;
	}
	
	public MyCustomizeException(String messageId, List<String> parameterIds, List<String> errorLst, String itemName) {
		super(messageId, parameterIds.stream().toArray(String[]::new));
		this.errorLst.addAll(errorLst);
		this.itemName = itemName;
	}

}
