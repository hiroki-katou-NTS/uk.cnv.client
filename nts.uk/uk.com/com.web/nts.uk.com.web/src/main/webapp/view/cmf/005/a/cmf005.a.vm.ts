module nts.uk.com.view.cmf005.a.viewmodel {

        export class ScreenModel {
            constructor() {
            }
            
            /**
             * request to delete data screen
             */
            deleteDataScreen(): void {
                nts.uk.request.jump("/view/cmf/005/b/index.xhtml");
            }
            
    }
}