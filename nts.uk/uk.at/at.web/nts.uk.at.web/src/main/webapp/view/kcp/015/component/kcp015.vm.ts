/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />
import setShared = nts.uk.ui.windows.setShared;

module nts.uk.ui.at.kcp015.shared {

    export interface Parameters {
		enable: KnockoutObservable<boolean>;
        hasParams : KnockoutObservable<boolean>;
        visibleA31: KnockoutObservable<boolean>;
        visibleA32: KnockoutObservable<boolean>;
        visibleA33: KnockoutObservable<boolean>;
        visibleA34: KnockoutObservable<boolean>;
        visibleA35: KnockoutObservable<boolean>;
        visibleA36: KnockoutObservable<boolean>;
        sids      : KnockoutObservableArray<any>
        baseDate  : KnockoutObservable<string>;
    }

    const COMPONENT_NAME = 'kcp015-component';

    @handler({
        bindingName: COMPONENT_NAME
    })
    export class KCP015ComponentBindingHandler implements KnockoutBindingHandler {
        init(element: HTMLElement, valueAccessor: () => any, allBindingsAccessor: KnockoutAllBindingsAccessor, viewModel: any, bindingContext: KnockoutBindingContext): void | { controlsDescendantBindings: boolean; } {
            const name = COMPONENT_NAME;

            const selected = valueAccessor();
			const enable = allBindingsAccessor.get('enable');
            const visibleA31 = allBindingsAccessor.get('visibleA31');
            const visibleA32 = allBindingsAccessor.get('visibleA32');
            const visibleA33 = allBindingsAccessor.get('visibleA33');
            const visibleA34 = allBindingsAccessor.get('visibleA34');
            const visibleA35 = allBindingsAccessor.get('visibleA35');
            const visibleA36 = allBindingsAccessor.get('visibleA36');
            const sids = allBindingsAccessor.get('sids');
            const baseDate = allBindingsAccessor.get('baseDate');

            const params = { enable, visibleA31, visibleA32, visibleA33, visibleA34, visibleA35, visibleA36, sids, baseDate };
            const component = { name, params };

            ko.applyBindingsToNode(element, { component }, bindingContext);

            return { controlsDescendantBindings: true };
        }
    }

    @component({
        name: COMPONENT_NAME,
        template: `<!-- ko let: {text: nts.uk.resource.getText } -->
             <button tabindex="12" id="showPopup" data-bind="text: text('KCP015_1'), visible: visibleA1, enable: enable"></button>
             <div id="A1" class="popup-area popup-panel btn10">
                <div id="button-top">
                    <button tabindex="1" class="small compensation" data-bind="text: text('Com_CompensationHoliday'), click: openKDL005, visible: visibleA31Com "></button>
                    <button tabindex="3" class="small paid"         data-bind="text: text('Com_PaidHoliday'),         click: openKDL020, visible: visibleA33Com "></button>
                    <button tabindex="5" class="small exsess"       data-bind="text: text('Com_ExsessHoliday'),       click: openKDL017, visible: visibleA35Com "></button>
                </div>
                <div id="button-bot">
                    <button tabindex="2" class="small substitute"     data-bind="text: text('Com_SubstituteHoliday'), click: openKDL009, visible: visibleA32Com "></button>
                    <button tabindex="4" class="small fundedPaid"     data-bind="text: text('Com_FundedPaidHoliday'), click: openKDL029, visible: visibleA34Com "></button>
                    <button tabindex="6" class="small supportsetting" data-bind="text: text('KCP015_2'),          click: openKDL039, visible: false "></button>
                </div>
             </div><!-- /ko -->`
    })
    export class ViewModel extends ko.ViewModel {
        
        // các model này có cần không khi đã có model data khai báo trong constructor???
        visibleA1: KnockoutObservable<boolean> = ko.observable(true);
        visibleA31Com: KnockoutObservable<boolean> = ko.observable(true);
        visibleA32Com: KnockoutObservable<boolean> = ko.observable(true);
        visibleA33Com: KnockoutObservable<boolean> = ko.observable(true);
        visibleA34Com: KnockoutObservable<boolean> = ko.observable(true);
        visibleA35Com: KnockoutObservable<boolean> = ko.observable(true);
        visibleA36Com: KnockoutObservable<boolean> = ko.observable(true);

		// biến này phục vụ enable trên màn KSU002- Nếu ko có param thì mặc định là true
		enable:KnockoutObservable<boolean>;

        // Nếu đã khai báo model data ở đây thì các model từ dòng 65 đến dòng 71 để làm gì???
        constructor(private data: Parameters) {
            super();

            let vm = this;

            // component đã mount đâu mà binding???
            $('#A1').ntsPopup({
                position: {
                    my: 'left top',
                    at: 'left bottom+3',
                    of: $('#showPopup')
                }
            });

            // câu lệnh này có chắc chạy được???
            $('#showPopup').click(function() {
                $('#A1').ntsPopup("toggle");
            });
            
            // Không thấy sử dụng gì với các biến này???
            const {enable, visibleA31, visibleA32, visibleA33, visibleA34, visibleA35, visibleA36, sids, baseDate } = vm.data;
			if(enable != undefined){
				vm.enable = enable;
			}else{
				vm.enable = ko.observable(true);
			}
            
            vm.getSetting();
        }

        // ????? hook này không dùng để làm gì cả???
        created() {
            const vm = this;
            const { data } = vm;
        }

        public getSetting(): JQueryPromise<void> {
            let vm = this;
            // Sao lại phải dùng Promise ở đây?
            let dfd = $.Deferred<void>();


            // thử thay bằng vm.$blockui('grayout');
            nts.uk.ui.block.grayout();

            // thử thay bằng vm.$ajax('at', 'screen/at/kcp015/get').then(() => {});
            nts.uk.request.ajax("at", "screen/at/kcp015/get").done((data: IData) => {

                if (vm.data.visibleA31() == false) {
                    vm.visibleA31Com(false);
                } else if (data.subLeaveUseDivision == false) {
                    vm.visibleA31Com(false);
                } else {
                    vm.visibleA31Com(true);
                }

                if (vm.data.visibleA32() == false) {
                    vm.visibleA32Com(false);
                } else if (data.dvisionOfZhenxiuUse == false) {
                    vm.visibleA32Com(false);
                } else {
                    vm.visibleA32Com(true);
                }

                if (vm.data.visibleA33() == false) {
                    vm.visibleA33Com(false);
                } else if (data.clsOfAnnualHoliday == false) {
                    vm.visibleA33Com(false);
                } else {
                    vm.visibleA33Com(true);
                }

                if (vm.data.visibleA34() == false) {
                    vm.visibleA34Com(false);
                } else if (data.divisionOfAnnualHoliday == false) {
                    vm.visibleA34Com(false);
                } else {
                    vm.visibleA34Com(true);
                }

                if (vm.data.visibleA35() == false) {
                    vm.visibleA35Com(false);
                } else if (data.overtimeUseCls60H == false) {
                    vm.visibleA35Com(false);
                } else {
                    vm.visibleA35Com(true);
                }

                if (vm.data.visibleA36() == false) {
                    vm.visibleA36Com(false);
                } else {
                    vm.visibleA36Com(true);
                }
                
                // Đoạn này thử chuyển sang computed xem có hợp lý hơn không???
                if (!vm.visibleA31Com() && !vm.visibleA33Com() && !vm.visibleA35Com() && !vm.visibleA32Com() && !vm.visibleA34Com() && !vm.visibleA36Com()) {
                    vm.visibleA1(false);
                }

                // Thay vì xử lý ui ở viewmodel thế này, hãy đưa nó vào 1 custom binding xem?
                if (!vm.visibleA31Com() && !vm.visibleA33Com() && !vm.visibleA35Com()) {
                    $('#button-bot').css("margin-top", "0px");
                }

                // Thay vì xử lý ui ở viewmodel thế này, hãy đưa nó vào 1 custom binding xem?
                if (!vm.visibleA32Com() && !vm.visibleA34Com() && !vm.visibleA36Com()) {
                    $('#button-bot').css("margin-top", "0px");
                }
                
                dfd.resolve();
            }).fail(function() {
                dfd.reject();
            }).always((data) => {
                nts.uk.ui.block.clear();
            });
            return dfd.promise();
        }

        // A1_10_2
        openKDL005() {
            let vm = this;

            let empIds = vm.data.sids();
            let baseDate = moment().format('YYYYMMDD');
            let param: any = {
                employeeIds: empIds,
                baseDate: baseDate
            };

            nts.uk.ui.windows.setShared('KDL005_DATA', empIds);
            $('#A1_10_1').ntsPopup('hide');
            if (empIds.length > 1) {
                nts.uk.ui.windows.sub.modal("/view/kdl/005/a/index.xhtml", {  width: 1160, height: 640 });
            } else {
                nts.uk.ui.windows.sub.modal("/view/kdl/005/a/index.xhtml",{  width: 860, height: 640 });
            }
        }

        // A1_10_3
        openKDL009() {
            let vm = this;

            let empIds = vm.data.sids();
            let baseDate = moment().format('YYYYMMDD');
            var param: any = {
                employeeIds: empIds,
                baseDate: baseDate
            };

            /**
             * thử thay bằng đoạn code sau
             * vm.$window
             *    .shared('KDL009_DATA', param)
             *    .then(() => $('#A1_10_1').ntsPopup('hide'))
             *    .then(() => {
             *        const { employeeIds } = param;
             * 
             *        if(employeeIds.length > 1){
             *            vm.$window.modal("/view/kdl/009/a/index.xhtml",{width: 1100, height: 650});
             *        } else {
             *            vm.$window.modal("/view/kdl/009/a/index.xhtml",{width: 770, height: 650});
             *        }
             *    });
             * Những đoạn code dùng cấu trúc cũ thay tương tự.
             */
            nts.uk.ui.windows.setShared('KDL009_DATA', param.employeeIds);
            $('#A1_10_1').ntsPopup('hide');
            if (param.employeeIds.length > 1) {
                nts.uk.ui.windows.sub.modal("/view/kdl/009/a/index.xhtml",{width: 1100, height: 650});
            } else {
                nts.uk.ui.windows.sub.modal("/view/kdl/009/a/index.xhtml",{width: 770, height: 650});
            }
        }

        // A1_10_4
        openKDL020() {
            let vm = this;
            $('#A1_10_1').ntsPopup('hide');

			setShared('KDL020_DATA', vm.data.sids());
			if (vm.data.sids().length > 1)
				nts.uk.ui.windows.sub.modal("/view/kdl/020/a/index.xhtml",{  width: 1040, height: 660 });
			else
				nts.uk.ui.windows.sub.modal("/view/kdl/020/a/index.xhtml",{  width: 730, height: 660 });
        }

        // A1_10_5
        openKDL029() {
            let vm = this;
            $('#A1_10_1').ntsPopup('hide');
			nts.uk.ui.windows.setShared('KDL029_DATA', vm.data.sids());
			if (vm.data.sids().length > 1)
				nts.uk.ui.windows.sub.modal("/view/kdl/029/a/index.xhtml",{  width: 1060, height: 600 });
			else
				nts.uk.ui.windows.sub.modal("/view/kdl/029/a/index.xhtml",{  width: 710, height: 600 });
        }
        
        openKDL017() {
            let vm = this;
            let data = {employeeIds: vm.data.sids(), baseDate: moment(new Date()).format("YYYYMMDD")}
            setShared('KDL017_PARAM', data);
            if(data.employeeIds.length > 1) {
                nts.uk.ui.windows.sub.modal("/view/kdl/017/a/multiple.xhtml");
            } else {
                nts.uk.ui.windows.sub.modal("/view/kdl/017/a/single.xhtml");
            }
        }

        openKDL039() {
            let vm = this;
            let sids = vm.data.sids();
            let baseDate = vm.data.baseDate();
            // to do
        }
    }

    interface IData {
        clsOfAnnualHoliday: boolean;     // 年休の使用区分
        divisionOfAnnualHoliday: boolean;// 積立年休使用区分
        overtimeUseCls60H: boolean;      // 60H超休使用区分
        dvisionOfZhenxiuUse: boolean;    // 振休使用区分
        subLeaveUseDivision: boolean;    // 代休使用区分
    }
}