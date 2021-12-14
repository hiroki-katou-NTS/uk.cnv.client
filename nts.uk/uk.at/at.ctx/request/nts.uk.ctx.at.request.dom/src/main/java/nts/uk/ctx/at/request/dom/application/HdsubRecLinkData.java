package nts.uk.ctx.at.request.dom.application;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author anhnm
 *
 */
@Getter
@Setter
@AllArgsConstructor
public class HdsubRecLinkData {
    
    // 振休申請のID
    public String absId;
    
    // 振出申請のID
    public String recId;
    
    // 紐付けの申請
    public Application linkApp;

}
