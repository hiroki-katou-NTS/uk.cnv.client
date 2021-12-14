package nts.uk.ctx.cloud.operate.infra.web.component;

import java.io.IOException;

import javax.enterprise.inject.spi.CDI;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringEscapeUtils;

import lombok.AllArgsConstructor;
import nts.arc.system.ServerSystemProperties;
//import nts.uk.shr.com.context.AppContexts;
//import nts.uk.shr.com.context.LoginUserContext;
//import nts.uk.shr.com.context.loginuser.SelectedLanguage;
//import nts.uk.shr.com.context.loginuser.role.LoginUserRoles;
//import nts.uk.shr.com.i18n.TextResource;
//import nts.uk.shr.com.operation.SystemOperationSetting;
//import nts.uk.shr.com.operation.SystemOperationSetting.SystemOperationMode;
//import nts.uk.shr.com.operation.SystemOperationSetting.SystemStopMode;
//import nts.uk.shr.com.operation.SystemOperationSetting.SystemStopType;
//import nts.uk.shr.com.program.ProgramsManager;
//import nts.uk.shr.com.program.WebAppId;
//import nts.uk.shr.infra.web.component.env.ViewContextEnvWriter;
import nts.uk.ctx.cloud.operate.infra.web.component.env.ViewContextEnvWriterNoLogin;
import nts.uk.shr.com.constants.DefaultSettingKeys;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.LanguageConsts;
import nts.uk.shr.infra.i18n.resource.I18NResourcesForUK;

@FacesComponent(tagName = "viewcontext_cloud", createTag = true)
public class ViewContextCloud extends UIComponentBase {

	private static final String VALUE_FORMAT = "'{0}'";

	@AllArgsConstructor
	public enum SystemOperationMode {

		/** 業務運用中 */
		RUNNING(0),
		/** 利用停止前段階 */
		IN_PROGRESS(1),
		/** 利用停止中 */
		STOP(2);

		public final int value;
	}

	@AllArgsConstructor
	public enum SystemStopMode {

		/** 担当者モード */
		PERSON_MODE(1),
		/** 管理者モード */
		ADMIN_MODE(2);

		public final int value;
	}

	@AllArgsConstructor
	public enum SystemStopType {

		/** 全体 */
		ALL_SYSTEM(1),
		/** 会社 */
		COMPANY(2);

		public final int value;
	}

	@Override
	public String getFamily() {
		return this.getClass().getName();
	}


	/**
	 * Render beginning of component
	 *
	 * @param context
	 *            FacesContext
	 * @throws IOException
	 *             IOException
	 */
	@Override
	public void encodeBegin(FacesContext context) throws IOException {

		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		String requestedPath = request.getServletPath();
		String queryString = request.getQueryString();
		String applicationContextPath = context.getExternalContext().getApplicationContextPath();

		ResponseWriter rw = context.getResponseWriter();
		rw.write("<script type=\"text/javascript\">window.__viewContext = {");

		writeRootPath(requestedPath, rw);
		rw.write(",");

		writeProgramInfo(requestedPath, queryString, rw, applicationContextPath);
		rw.write(",");
		writeLoginPersonInfo(rw);
		rw.write(",");

		rw.write("};");
		rw.write("__viewContext.primitiveValueConstraints = __viewContext.primitiveValueConstraints || {};");

		CDI.current().select(ViewContextEnvWriterNoLogin.class).get().write(rw);
		rw.write("</script>");

		rw.write(this.getHtmlToLoadResources());

	}

	private String getHtmlToLoadResources() {
		I18NResourcesForUK i18n = CDI.current().select(I18NResourcesForUK.class).get();

		String companyId = DefaultSettingKeys.COMPANY_ID;
		String languageId = LanguageConsts.DEFAULT_LANGUAGE_ID;
		if (AppContexts.user().hasLoggedIn()) {
			companyId = AppContexts.user().companyId();
			languageId = AppContexts.user().language().basicLanguageId();
		}
		String systemId = "COM";
		String version = i18n.getVersionOfCurrentCompany();
		String companyVersion = createEtagString(companyId, languageId, systemId, version);
		return "<script src=\"/nts.uk.com.web/webapi/i18n/resources/screen?v=" + companyVersion + "\"></script>";
	}

	private String createEtagString(String companyId, String languageId, String systemId, String version) {
		StringBuilder builder = new StringBuilder();
		builder.append(companyId);
		builder.append("_");
		builder.append(languageId);
		builder.append("_");
		builder.append(systemId);
		builder.append("_");
		builder.append(version);
		return builder.toString();
	}

	private void writeProgramInfo (String requestedPath, String queryString, ResponseWriter rw, String applicationContextPath) throws IOException {
//		WebAppId webApi = Arrays.asList(WebAppId.values()).stream()
//				.filter(w -> applicationContextPath.indexOf(w.name) >= 0).findFirst().orElse(WebAppId.COM);

		StringBuilder builder = new StringBuilder();
//		ProgramsManager.find(webApi, requestedPath).ifPresent(pr -> {
//			builder.append("webapi: " + formatValue(pr.getAppId().name));
//			builder.append(", programId: " + formatValue(pr.getPId()));
//			String programName = TextResource.localize(pr.getPName());
//			builder.append(", programName: " + formatValue(programName));
//			builder.append(", path: " + formatValue(pr.getPPath()));
//			if (queryString != null) {
//				builder.append(", queryString: " + formatValue(queryString));
//			}
//		});

		writeOperationSetting(builder);

		if(builder.length() > 0){
			builder.append(", ");
		}

		builder.append("isDebugMode: " + ServerSystemProperties.isDebugMode() + ",");

		rw.write("program: {" + builder.toString() + "}");
	}

	private String formatValue(String value){
		if(value == null){
			return null;
		}

		String escapeMsg = StringEscapeUtils.escapeEcmaScript(value);
		return VALUE_FORMAT.replace("{0}", escapeMsg);
	}

	private void writeOperationSetting(StringBuilder builder) {
		if(builder.length() > 0){
			builder.append(", ");
		}
		builder.append("operationSetting: { ");
		builder.append("mode: " +  SystemStopMode.ADMIN_MODE.value);
		builder.append(", type: " + SystemStopType.COMPANY.value);
		builder.append(", message: " + null);
		builder.append(", state: " + SystemOperationMode.RUNNING.value);
		builder.append("} ");
	}

	private void writeLoginPersonInfo (ResponseWriter rw) throws IOException {
		rw.write("user: {}");
	}

	private void writeRootPath(String requestedPath, ResponseWriter rw) throws IOException {
		String rootPath = requestedPath.replaceAll("[^/]", "").substring(1).replaceAll("/", "../");

		rw.write("rootPath: " + formatValue(rootPath));
	}
}
