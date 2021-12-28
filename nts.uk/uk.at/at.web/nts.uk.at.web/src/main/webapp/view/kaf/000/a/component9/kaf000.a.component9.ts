module nts.uk.at.view.kaf000.a.component9.viewmodel {

    @component({
        name: 'kaf000-a-component9',
        template: `
			<div id="kaf000-a-component9" style="display: inline-block; margin-left: 5px;" data-bind="if: params">
				<div data-bind="if: displayBack">
					<a class="goback link-button" style="background-color: #f1fdfb;height: 30px !important;" data-bind="click: backtoPre, text: $i18n('KAF000_53')"></a>
				</div>
			</div>
        `
    })
    class Kaf000AComponent9ViewModel extends ko.ViewModel {
		params: any = null;
		displayBack: KnockoutObservable<boolean> = ko.pureComputed(() => {
			if(nts.uk.request.location.current.isFromMenu) {
				return false;
			}
			if(history.length<=1) {
				return false;
			}
			return true;
		});
        created(params: any) {
            const vm = this;
			vm.params = params;
			
        }

		backtoPre() {
            if (document.referrer) {
                window.location = document.referrer;
            } else {
                window.history.back();
            }
		}
    }
}