module nts.uk.at.view.kdp010.a {
    import block = nts.uk.ui.block;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
	import viewModelscreenG = nts.uk.at.view.kdp010.g.viewmodel;
    export module viewmodel {
		
        export class ScreenModel {
			screenModelG = new viewModelscreenG.ScreenModel();
			constructor(){};

            public openGDialog(): void {
				let self = this;
                nts.uk.ui.windows.sub.modal("/view/kdp/010/g/index.xhtml").onClosed(() => {
                	if(getShared("KDP010G")){
						setShared("KDP010G", false);
						self.screenModelG.start();
					}
                });
            }

            public goToBScreen(): void {
                nts.uk.request.jump("/view/kdp/010/b/index.xhtml");
            }

			public goToKScreen(): void {
                nts.uk.request.jump("/view/kdp/010/k/index.xhtml");
            }

			public goToCScreen(): void {
                nts.uk.request.jump("/view/kdp/010/c/index.xhtml");
            }

            public goToDScreen(): void {
                nts.uk.request.jump("/view/kdp/010/d/index.xhtml");
            }

			public goToEScreen(): void {
                nts.uk.request.jump("/view/kdp/010/e/index.xhtml");
            }

			public goToFScreen(): void {
                nts.uk.request.jump("/view/kdp/010/f/index.xhtml");
            }

			public openCDL015(): void {
				let self = this;
                nts.uk.ui.windows.sub.modal('com', "/view/cdl/015/a/index.xhtml").onClosed(() => {
                	
                });
			}
        }
    }

    __viewContext.ready(function() {
        var screenModel = new viewmodel.ScreenModel();
		screenModel.screenModelG.start().done(function() {
        	__viewContext.bind(screenModel);
			$(document).ready(function() {
            	$('#X1_3').focus();
            });
		});    
    });
}