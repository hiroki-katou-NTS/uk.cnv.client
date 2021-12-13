package nts.uk.shr.infra.web.component.filetag;

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

import org.apache.logging.log4j.util.Strings;

@FacesComponent(tagName = "scriptfile", createTag = true)
public class ScriptFile extends UIComponentBase {

    public static Set<String> FILES_BASIC = new LinkedHashSet<String>(Arrays.asList(new String[] {
            "/lib/generic/jquery/jquery-3.1.1.js",
            "/lib/generic/jqueryui/jquery-ui.js",
            "/lib/generic/jquery/jquery.steps.js",
            "/lib/generic/jqueryplugin/jquery.blockui.js",
            "/lib/generic/jqueryplugin/jquery.fileDownload.js",
            "/lib/generic/spectrum/spectrum.js",
            "/lib/generic/momentjs/moment.js",
            "/lib/generic/momentjs/ja.js",
            "/lib/generic/lodash/lodash-4.16.6.min.js",
            "/lib/generic/knockoutjs/knockout-3.4.1.js",
            "/lib/generic/knockoutjs/knockout.mapping-2.4.1.js",
            "/lib/generic/jquery/datepicker.js",
            "/lib/generic/jquery/datepicker.ja-JP.js",
            "/lib/generic/jquery/jquery.cookie.min.js",
            "/lib/nittsu/nts.uk.com.web.nittsu.bundles.js"
    }));
    
    public static Set<String> FILES_IGNITE = new LinkedHashSet<String>(Arrays.asList(new String[] {
            "/lib/generic/igniteui/js/infragistics.core.js",
            "/lib/generic/igniteui/js/infragistics.lob.js",
            "/lib/generic/igniteui/js/extensions/infragistics.ui.combo.knockout-extensions.js",
            "/lib/generic/igniteui/js/extensions/infragistics.ui.tree.knockout-extensions.js",
    }));
    
    public static Set<String> FILES_FULLCALENDAR = new  LinkedHashSet<String>(Arrays.asList(new String[] {
    	"/lib/generic/fullcalendar/main.min.js",
    	"/lib/generic/fullcalendar/locales/ja.js",
    	"/lib/generic/fullcalendar/locales/vi.js"
    }));
    
    @SuppressWarnings("serial")
    private static Map<String, Set<String>> FILE_SETS = new HashMap<String, Set<String>>() {
        {
            this.put("BASIC", FILES_BASIC);
            this.put("IGNITE", FILES_IGNITE);
            this.put("FULLCALENDAR", FILES_FULLCALENDAR);
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
        
        String of = (String) this.getAttributes().get("of");
        
        if (filePath != null) {
        	if(Strings.isEmpty(of)){
        		writeTag(rw, FileTagsHelper.buildPath(context, filePath));
        	}else{
        		writeTag(rw, FileTagsHelper.buildPathOf(of, filePath));
        	}
        	
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
                .forEach(filePath -> writeTag(rw, FileTagsHelper.buildPathUsingComWeb(context, filePath)));
    }
}