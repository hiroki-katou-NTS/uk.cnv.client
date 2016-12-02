package nts.uk.shr.web.component.filetag;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent(tagName = "scriptfile", createTag = true)
public class ScriptFile extends UIComponentBase {

    private static Set<String> FILES_BASIC = new LinkedHashSet<String>(Arrays.asList(new String[] {
            "/lib/generic/jquery/jquery-3.1.1.js",
            "/lib/generic/jqueryui/jquery-ui.js",
            "/lib/generic/lodash/lodash-4.16.6.min.js",
            "/lib/generic/knockoutjs/knockout-3.4.0.js",
            "/lib/generic/knockoutjs/knockout.mapping-2.4.1.js",
            "/lib/nittsu/iefix.js",
            "/lib/nittsu/util.js",
            "/lib/nittsu/text.js",
            "/lib/nittsu/request.js",
            "/lib/nittsu/ui/init.js",
            "/lib/nittsu/ui/notify.js",
            "/lib/nittsu/ui/validation.js",
            "/lib/nittsu/ui/jquery-ext.js",
            "/lib/nittsu/ui/ko-ext.js",
            "/lib/nittsu/ui/errors.js",
            "/lib/nittsu/ui/ui.js",
            "/lib/nittsu/ui/dialog-options.js",
            "/lib/nittsu/time.js",
            "/lib/nittsu/number.js",
    }));
    
    @SuppressWarnings("serial")
    private static Map<String, Set<String>> FILE_SETS = new HashMap<String, Set<String>>() {
        {
            this.put("BASIC", FILES_BASIC);
        }
    };

    /**
     * Return a family name
     * 
     * @return family name
     */
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
        
        ResponseWriter rw = context.getResponseWriter();
        
        String filePath = (String) this.getAttributes().get("path");
        
        if (filePath != null) {
        	writeTag(rw, FileTagsHelper.buildPath(context, filePath));
        } else {
        	String fileSet = (String) this.getAttributes().get("set");
            Optional<String> exclude = Optional.ofNullable(this.getAttributes().get("exclude"))
                    .map(o -> Optional.of((String) o))
                    .orElse(Optional.empty());
            
            writeTagSet(rw, context, fileSet, exclude);
        }
    }

    private static void writeTag(ResponseWriter rw, String filePath) {
        
        try {
            rw.write("<script type=\"text/javascript\" src=\"");
            rw.write(filePath);
            rw.write("\"></script>\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static void writeTagSet(
            ResponseWriter rw, FacesContext context, String fileSet, Optional<String> exclude) {
        
        Set<String> excludes = exclude
                .map(e -> e.split(","))
                .map(e -> new HashSet<>(Arrays.asList(e)))
                .orElse(new HashSet<>());
        
        FILE_SETS.get(fileSet).stream()
                .filter(filePath -> excludes.stream().noneMatch(ex -> filePath.contains(ex)))
                .forEach(filePath -> writeTag(rw, FileTagsHelper.buildPath(context, filePath)));
    }
}