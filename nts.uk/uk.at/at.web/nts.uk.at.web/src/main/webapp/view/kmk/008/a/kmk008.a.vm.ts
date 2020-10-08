module nts.uk.at.view.kmk008.a {
	const API = {
		START: "screen/at/kmk008/a/get",
	};

	@bean()
	export class ScreenModel extends ko.ViewModel{
		langId: KnockoutObservable<string> = ko.observable('ja');
		specicalConditionApplicationUse: KnockoutObservable<boolean> = ko.observable(false);

		constructor() {
			super();
			const vm = this;
		}

		created() {
			const vm = this;
			_.extend(window, {vm});

			vm.$blockui("grayout");

			// Call init API
			vm.$ajax(API.START).done((data) => {
				vm.specicalConditionApplicationUse(data.specicalConditionApplicationUse);
			}).fail(function(res) {
			}).always(() => {
				vm.$blockui("clear");
			});
		}

		mounted() {
			$("#A1_5").focus();
		}

		exportExcel(): void {
			let vm = this;
			let params: any = {
			   date: null,
			   mode: 2
			 };

			nts.uk.ui.windows.setShared("CDL028_INPUT", params);

			nts.uk.ui.windows.sub.modal('com', '/view/cdl/028/a/index.xhtml').onClosed(() => {
				var result = nts.uk.ui.windows.getShared('CDL028_A_PARAMS');
			   	if (result.status) {
				   	vm.$blockui("grayout");
					let langId = vm.langId();
					let startDate = moment.utc(result.startDateFiscalYear, "YYYY/MM/DD");
					let endDate = moment.utc(result.endDateFiscalYear, "YYYY/MM/DD");
					vm.saveAsExcel(langId, startDate, endDate).done(function() {

					}).fail(function(error) {
						// nts.uk.ui.dialog.alertError({ messageId: error.messageId });
						vm.$dialog.error({messageId: error.messageId});
					}).always(function() {
						vm.$blockui("clear");
					});
			   	}

				vm.$blockui("clear");
			});
		}

		saveAsExcel(languageId: string, startDate: any, endDate: any): JQueryPromise<any> {
			let program = nts.uk.ui._viewModel.kiban.programName().split(" ");
			let domainType = "KMK008";
			if (program.length > 1){
				program.shift();
				domainType = domainType + program.join(" ");
			}

			return nts.uk.request.exportFile('/masterlist/report/print', { domainId: "RegisterTime",
				domainType: domainType,
				languageId: languageId,
				reportType: 0,
				startDate: startDate,
				endDate: endDate  });
		}

		openScreenB0():void {
			const vm = this;
			vm.$jump("/view/kmk/008/b/index.xhtml", { "laborSystemAtr": 0 });
		}

		openScreenB1():void {
			const vm = this;
			vm.$jump("/view/kmk/008/b/index.xhtml", { "laborSystemAtr": 1 });
		}

		openScreenF():void {
			const vm = this;
			vm.$jump("/view/kmk/008/f/index.xhtml");
		}

		openDialogScreenG():void {
			nts.uk.ui.windows.sub.modal("/view/kmk/008/g/index.xhtml").onClosed(() => {});
		}

		openDialogScreenH():void {
			nts.uk.ui.windows.sub.modal("/view/kmk/008/h/index.xhtml").onClosed(() => {});
		}

		openDialogScreenI():void {
			nts.uk.ui.windows.sub.modal("/view/kmk/008/i/index.xhtml").onClosed(() => {});
		}

		openDialogScreenJ():void {
			nts.uk.ui.windows.sub.modal("/view/kmk/008/j/index.xhtml").onClosed(() => {});
		}

		openScreenCMM024():void {
			const vm = this;
			vm.$jump("/view/cmm/024/a/index.xhtml");
		}

		openScreenKMP021():void {
			const vm = this;
			vm.$jump("/view/kmp/021/a/index.xhtml");
		}
	}
}
