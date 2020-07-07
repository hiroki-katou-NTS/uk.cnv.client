/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />


@bean()
class KDP004GViewModel extends ko.ViewModel {

	retry: KnockoutObservable<number> = ko.observable(0);
	errorMessage: KnockoutObservable<string> = ko.observable('');

	created(params: any) {
		let vm = this;
		vm.$window.storage('ModelGParam').then((param: any) => {
			vm.retry(param.retry);
			vm.errorMessage(vm.$i18n.message(param.errorMessage));
		});
	}

	public closeDialog(actionName: string) {
		let vm = this;
		vm.$window.close({ actionName: actionName });
	}
}