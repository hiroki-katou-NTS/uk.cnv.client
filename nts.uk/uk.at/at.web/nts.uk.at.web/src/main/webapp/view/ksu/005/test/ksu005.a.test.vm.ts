module nts.uk.at.view.ksu005.a.test {
	import setShare = nts.uk.ui.windows.setShared;
	// Import
	export module viewmodel {
		export class ScreenModel {	
            currentScreen: any = null;	
			public startPage(): JQueryPromise<any> {
				let self = this,
					dfd = $.Deferred();

				dfd.resolve();
				return dfd.promise();
            }
            
            openDialog(): void {
				let self = this;				
				self.currentScreen = nts.uk.ui.windows.sub.modal('/view/ksu/005/a/index.xhtml');
			}
		}	
	
	}	
}