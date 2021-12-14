package nts.uk.file.com.app.equipment.data;

public enum EquipmentDataReportType {
	EXCEL(0), 
	CSV(1);
	
	public final int value;
	
	private EquipmentDataReportType(int value) {
		this.value = value;
	}
}
