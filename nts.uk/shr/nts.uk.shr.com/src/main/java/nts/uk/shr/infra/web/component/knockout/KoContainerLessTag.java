package nts.uk.shr.infra.web.component.knockout;

import java.io.IOException;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

/**
 * Knockout.js コンテナレステンプレートを生成する
 */
@FacesComponent(tagName = "ko", createTag = true)
public class KoContainerLessTag extends UIComponentBase {

    @Override
    public String getFamily() {
        return this.getClass().getName();
    }

    /**
     * Render beginning of component
     *
     * @param context FacesContext
     * @throws IOException IOException
     */
    @Override
    public void encodeBegin(FacesContext context) throws IOException {

        Helper.writeBegin(
        		(String) getAttributes().get("tag"),
        		(String) getAttributes().get("bind"),
        		context.getResponseWriter());
    }

    /**
     * Render ending of component
     *
     * @param context FacesContext
     * @throws IOException IOException
     */
    @Override
    public void encodeEnd(FacesContext context) throws IOException {
        
        Helper.writeEnd(context.getResponseWriter());
    }
}
