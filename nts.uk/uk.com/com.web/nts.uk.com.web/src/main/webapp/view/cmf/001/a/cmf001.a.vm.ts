module nts.uk.com.view.cmf001.a {
    export module viewmodel {
        export class ScreenModel {
            constructor() {
            }
            
            /**
             * request to create creation screen
             */
            importScreen(): void {
                let self = this;
                nts.uk.request.jump("/view/cmf/001/x/index.xhtml");
            }
            
            /**
             * request to create creation screen
             */
            settingScreen(): void {
                let self = this;
                nts.uk.request.jump("/view/cmf/001/b/index.xhtml");
            }
        }
    }
}