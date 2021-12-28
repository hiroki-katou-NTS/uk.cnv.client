///<reference path="../../../../lib/nittsu/viewcontext.d.ts"/>
module nts.uk.at.view.kaf020.a {
    import AppInitParam = nts.uk.at.view.kaf000.shr.viewmodel.AppInitParam;

    const PATH_API = {
        optionalSetting: 'ctx/at/request/application/optionalitem/optionalItemAppSetting',
    };

    @bean()
    export class Kaf020AViewModel extends ko.ViewModel {
        optionalItemAppSet: KnockoutObservableArray<OptionalItemAppSet> = ko.observableArray([]);
        empLst: Array<string> = [];
        dateLst: Array<string> = [];
        baseDate: string;
        isAgentMode: KnockoutObservable<boolean> = ko.observable(false);
		screenCode: number = null;


        created(params: AppInitParam) {
            const vm = this;
			if(nts.uk.request.location.current.isFromMenu) {
				sessionStorage.removeItem('nts.uk.request.STORAGE_KEY_TRANSFER_DATA');	
			} else {
				if(!_.isNil(__viewContext.transferred.value)) {
					vm.isFromOther = true;
					params = __viewContext.transferred.value;
				}
			}

            if (!_.isEmpty(params)) {
				if (!nts.uk.util.isNullOrUndefined(params.screenCode)) {
					vm.screenCode = params.screenCode;
				}
                if (!_.isEmpty(params.employeeIds)) {
                    vm.empLst = params.employeeIds;
                }
                if (!_.isEmpty(params.baseDate)) {
                    vm.baseDate = params.baseDate;
                    let paramDate = moment(params.baseDate).format('YYYY/MM/DD');
                    vm.dateLst = [paramDate];
                }
                if (params.isAgentMode) {
                    vm.isAgentMode(params.isAgentMode);
                }
            }

            vm.$ajax(PATH_API.optionalSetting).done((data: Array<OptionalItemAppSet>) => {
                if (data.length == 0) {
                    vm.$dialog.error({messageId: "Msg_1694"});
                } else if (data.length == 1 && (!params || !params.fromB)) {
                    vm.detail(data[0]);
                } else {
                    data = _.sortBy(data, ["code"]);
                    vm.optionalItemAppSet(data);
                    $('#fixed-table').focus();
                }
            });
        }

        mounted() {
            $("#fixed-table").ntsFixedTable({height: 450, width: 1150});
            setTimeout(() => {
                $("#pg-name").text("KAF020A " + nts.uk.resource.getText("KAF020_1"));
            }, 300);
        }


        detail(optionalItem: any) {
            const vm = this;
            vm.$jump('../b/index.xhtml', {
                optionalItem: optionalItem,
                empLst: vm.empLst,
                dateLst: vm.dateLst,
                isAgentMode: vm.isAgentMode(),
                baseDate: vm.baseDate,
				screenCode: vm.screenCode
            });
        }

        goBack() {
            const vm = this;
            vm.$jump('../../001/a/index.xhtml');
        }
    }


    interface OptionalItemAppSet {
        code: string,
        name: string,
        useAtr: number,
        note: string,
        settingItems: Array<OptItemSet>;
    }

    interface OptItemSet {
        no: number,
        dispOrder: number
    }
}