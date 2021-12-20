package nts.uk.ctx.at.shared.dom.vacation.setting.subst;

/**
 * 期限日の管理方法
 * @author Hieult
 *
 */
public enum ManageDeadline {
	//締めで管理する
	MANAGE_BY_TIGHTENING( 0 , "KMF001_328", "KMF001_328"),
	//発生日を基準にして管理する
	MANAGE_BY_BASE_DATE(1, "KMF001_329" ,"KMF001_329") ;
	
	/** The value. */
	public int value;

	/** The name id. */
	public String nameId;

	/** The description. */
	public String description;

	/** The Constant values. */
	private final static ManageDeadline[] values = ManageDeadline.values();

	private ManageDeadline(int value, String nameId, String description) {
		this.value = value;
		this.nameId = nameId;
		this.description = description;
	}
	
	public static ManageDeadline valueOf(Integer value) {
		// Invalid object.
		if (value == null) {
			return null;
		}

		// Find value.
		for (ManageDeadline val : ManageDeadline.values) {
			if (val.value == value) {
				return val;
			}
		}

		// Not found.
		return null;
	}
}
