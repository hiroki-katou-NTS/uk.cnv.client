package nts.uk.ctx.at.request.app.command.application.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.request.app.find.application.ApplicationDto;
import nts.uk.ctx.at.request.dom.application.HdsubRecLinkData;

/**
 * @author anhnm
 *
 */
@Getter
@Setter
@AllArgsConstructor
public class HdsubRecLinkDataDto {
    
 // 振休申請のID
    public String absId;
    
    // 振出申請のID
    public String recId;
    
    // 紐付けの申請
    public ApplicationDto linkApp;

    public HdsubRecLinkData toDomain() {
        return new HdsubRecLinkData(absId, recId, linkApp.toDomain());
    }
}
