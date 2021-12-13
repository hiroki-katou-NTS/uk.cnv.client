module nts.uk.com.view.cps018 {
    export module viewmodel {
        export class ScreenModel {

            constructor() {
                var self = this;
            }

            startPage(): JQueryPromise<any> {
                let self = this;
                let dfd = $.Deferred();
                dfd.resolve();
                return dfd.promise();
            }

            openCAS001() {
                nts.uk.request.jump("/view/cas/001/a/index.xhtml", { isFromCPS018: true });
            }

            openCPS001() {
                nts.uk.request.jump("/view/cps/001/a/index.xhtml", { isFromCPS018: true });
            }

            openCPS002() {
                nts.uk.request.jump("/view/cps/002/a/index.xhtml", { isFromCPS018: true });
            }

            openCPS003() {
                nts.uk.request.jump("/view/cps/003/a/index.xhtml", { isFromCPS018: true });
            }

            openCPS007() {
                nts.uk.request.jump("/view/cps/007/a/index.xhtml", { isFromCPS018: true });
            }

            openCPS008() {
                nts.uk.request.jump("/view/cps/008/a/index.xhtml", { isFromCPS018: true });
            }

            openCPS009() {
                nts.uk.request.jump("/view/cps/009/a/index.xhtml", { isFromCPS018: true });
            }
        }
    }
}
