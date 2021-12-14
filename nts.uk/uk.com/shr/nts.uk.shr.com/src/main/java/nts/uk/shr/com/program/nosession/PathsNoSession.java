package nts.uk.shr.com.program.nosession;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.val;
import nts.arc.security.csrf.PathsToCheckCsrf;
import nts.uk.shr.com.program.ProgramsManager;

public class PathsNoSession implements PathsToCheckCsrf {

	public static PathsNoSession WEB_APIS = PathsNoSession.define(
			"/sample/.*",
			"/public/.*",
			"/develop/.*",
			"/ntscommons/arc/task/async/.*",
			"/ntscommons/arc/filegate/.*",
			"/ntscommons/arc/diagnose/.*",
			"/nts/jobdstributor/worker/.*",
			"/shr/infra/file/storage/.*",
			"/operation/stop/.*",
			"/i18n/.*",
			"/server/time/.*",
			"/sys/portal/webmenu/username",
			"/ctx/sys/gateway/login/.*",
			"/ctx/sys/gateway/sendmail/.*",
			"/ctx/sys/gateway/changepassword/.*",
			"/ctx/sys/gateway/singlesignon/saml/.*",
			"/ctx/sys/gateway/url/.*",
			"/validate/constraints/.*",
			"/enums/map",
			"/sys/portal/webmenu/logout",
			"/ctx/sys/gateway/kdp/login/.*",
			"/at/record/stamp/.*",
			"/ctx/cld/.*",
			"/view-context/user",
			"/nr/process/.*"
			);

	public static PathsNoSession WEB_SCREENS = PathsNoSession.define(
			"/view/sample/.*",
			"/view/common/error/.*",
			"/view/spr/index.xhtml",
			"/view/url/index.xhtml",
			"/view/ccg/033/index.xhtml",
			"/ccg/033/a",
			"/sessiontimeout/index.xhtml",
			"/view/cld/.*",
			ProgramsManager.CCG007A.getPPath(),
			ProgramsManager.CCG007B.getPPath(),
			ProgramsManager.CCG007C.getPPath(),
			ProgramsManager.CCG007D.getPPath(),
			ProgramsManager.CCG007E.getPPath(),
			ProgramsManager.CCG007F.getPPath(),
			ProgramsManager.CCG007G.getPPath(),
			ProgramsManager.CCG007H.getPPath(),
			ProgramsManager.CCG007I.getPPath(),
			ProgramsManager.KDW003A.getPPath(),
			ProgramsManager.CCGS33.getPPath(),
			ProgramsManager.KDP003A.getPPath(),
			ProgramsManager.KDP003F.getPPath(),
			ProgramsManager.KDP004A.getPPath(),
			ProgramsManager.KDP005A.getPPath()
			);


	private final Set<Pattern> patterns;

	private PathsNoSession(Set<Pattern> patterns) {
		this.patterns = patterns;
	}

	public static PathsNoSession define(String... patternStrings) {
		val patterns = Arrays.asList(patternStrings).stream()
                .map(regex -> Pattern.compile(regex))
                .collect(Collectors.toSet());
		return new PathsNoSession(patterns);
	}

    /**
     * Return true if the request requires session.
     *
     * @param requestedRootRelativePath requestedRootRelativePath
     * @param getRequestedPath getRequestedPath
     * @return true if the request requires session
     */
    public boolean sessionRequired(String requestedRootRelativePath) {

        if (requestedRootRelativePath == null) {
            return false;
        }

        String pathToBeChecked = requestedRootRelativePath.toLowerCase();

        return this.patterns.stream()
                .noneMatch(p -> {
                	return p.matcher(pathToBeChecked).matches();
                });
    }

	@Override
	public Set<Pattern> patternsPathNoCheck() {
		return this.patterns;
	}
}
