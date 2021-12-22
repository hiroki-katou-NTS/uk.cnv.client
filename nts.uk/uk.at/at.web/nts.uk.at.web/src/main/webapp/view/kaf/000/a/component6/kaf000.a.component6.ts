module nts.uk.at.view.kaf000.a.component6.viewmodel {

    @component({
        name: 'kaf000-a-component6',
        template: `
			<com:ko-if bind="if: approvalRootStateShort().length != 0 && approvalRootDisp">
	            <div id="kaf000-a-component6" data-bind="click: openApproverDetail">
					<div style="display: inline-block;" data-bind="if: approvalRootStateShort().length != 0 && approvalRootDisp">
						<div data-bind="foreach: approvalRootStateShort" style="padding-top: 5px;">
							<div class="approver-block" style="width: 112px;">
								<div style="height: 24px;">
									<div class="limited-label" style="vertical-align: middle;" data-bind="ntsFormLabel:{}, text: $component.getApproverLabel($index())"></div>
								</div>
								<div style="min-width: 112px;">
									<div class="limited-label" style="vertical-align: middle;" data-bind="text: $data.approverName"></div>
								</div>
								<div data-bind="if: $data.representerName()" style="min-width: 112px;">
									<div class="limited-label" style="vertical-align: middle;" data-bind="text: '(' + $data.representerName() + ')'"></div>
								</div>
							</div>
						</div>
					</div>
					<div style="display: inline-block; width: 8px;" data-bind="if: approvalRootStateShort().length != 0 && approvalRootDisp">
						<div data-bind="if: numberApprover() > 5">
							<div data-bind="text: $i18n('KAF000_62')"></div>
						</div>
					</div>
				</div>
			</com:ko-if>
        `
    })
    class Kaf000AComponent6ViewModel extends ko.ViewModel {
        appDispInfoStartupOutput: any;
		isAgentMode: KnockoutObservable<boolean>;
		approvalRootStateShort: KnockoutObservableArray<ApproverInforShort>;
		numberApprover: KnockoutObservable<number>;
		approvalRootDisp: KnockoutObservable<boolean>;
		
        created(params: any) {
            const vm = this;
            vm.appDispInfoStartupOutput = params.appDispInfoStartupOutput;
			vm.isAgentMode = params.isAgentMode ? params.isAgentMode : ko.observable(false);
			vm.approvalRootStateShort = ko.observableArray([]);
			vm.numberApprover = ko.observable(0);
			vm.approvalRootDisp = ko.observable(false);
			
            vm.appDispInfoStartupOutput.subscribe(value => {
				let approvalRootStateShort = [],
					listPhase: any = value.appDispInfoWithDateOutput.opListApprovalPhaseState;
				for(let phase of listPhase) {
					if(_.isEmpty(_.flatMap(phase.listApprovalFrame, frame => frame.listApprover))) {
						approvalRootStateShort.push(new ApproverInforShort('', ''));	
					} else {
						for(let frame of phase.listApprovalFrame) {
							for(let approver of frame.listApprover) {
								approvalRootStateShort.push(new ApproverInforShort(approver.approverName, approver.representerName));
							}
						}
					}
				}
				vm.numberApprover(_.size(approvalRootStateShort));
				vm.approvalRootStateShort(_.take(approvalRootStateShort, 5));
				vm.approvalRootDisp(!vm.isAgentMode() || _.size(value.appDispInfoNoDateOutput.employeeInfoLst)<=1);  
            });
        }

		openApproverDetail() {
			const vm = this;
			if (!vm.approvalRootDisp()) {
				return;
			}
			let appDispInfoStartupOutput = vm.appDispInfoStartupOutput(),
				isAgentMode = vm.isAgentMode(),
				approvalRootState = vm.appDispInfoStartupOutput().appDispInfoWithDateOutput.opListApprovalPhaseState,
				dParam = { appDispInfoStartupOutput, isAgentMode, approvalRootState };
			vm.$window.modal('/view/kaf/000/d/index.xhtml', dParam);
		}
		
		getApproverLabel(index: string) {
			const vm = this;
			return vm.$i18n("KAF000_9", [index+1]);	
		}
		
		getMaxWidth() {
			const vm = this;
			switch(vm.numberApprover()) {
				case 1: return "calc(100% - 30px)";
				case 2: return "calc(50% - 15px)";
				case 3: return "calc(33% - 10px)";
				case 4: return "calc(25% - 8px)";
				default: return "calc(20% - 6px)";
			}
		}
    }

	class ApproverInforShort {
		
		approverName: KnockoutObservable<string>;
		representerName: KnockoutObservable<string>;
		
		constructor(approverName: string, representerName: string) {
			this.approverName = ko.observable(approverName);
			this.representerName = ko.observable(representerName);
		}
	}
}