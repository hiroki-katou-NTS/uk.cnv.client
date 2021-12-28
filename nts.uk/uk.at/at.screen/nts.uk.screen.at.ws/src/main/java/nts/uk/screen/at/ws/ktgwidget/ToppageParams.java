package nts.uk.screen.at.ws.ktgwidget;

import lombok.Data;

@Data
public class ToppageParams {
	int closingId;
    int currentOrNextMonth;
    int processingYm;
    String startDate;
    String endDate;
}
