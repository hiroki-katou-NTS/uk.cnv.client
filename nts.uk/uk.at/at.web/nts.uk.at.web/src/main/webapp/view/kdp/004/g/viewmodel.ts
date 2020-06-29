/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />


@bean()
class KDP004GViewModel extends ko.ViewModel {
	systemDate: KnockoutObservable<any> = ko.observable(moment.utc());

	tabs = {
		tabs: ko.observableArray([{
			id: '1',
			title: 'レイアウト１'
		}, {
			id: '2',
			title: 'レイアウト2'
		}, {
			id: '3',
			title: 'レイアウト3'
		}, {
			id: '4',
			title: 'レイアウト4'
		}, {
			id: '5',
			title: 'レイアウト5'
		}]),
		selected: ko.observable('1')
	};

	isUsed: KnockoutObservable<boolean> = ko.observable(false);

	layoutsSetting: KnockoutObservable<any> = ko.observable(
		{ layout1Title: ko.observable('layout-1'), layout1Visible: ko.observable(true) }
	);

	dateText() {
		let vm = this;
		return vm.systemDate().format("YYYY/MM/DD(dddd)");


	}

	public getNotUseMessage() {
		let vm = this;

		return vm.$i18n.message('Msg_1619', [this.$i18n.text('KDP002_3')]);

	}



	timeText() {
		let vm = this;
		return vm.systemDate().format("HH:ss");
	}

	created(params: any) {
		let vm = this;

	}
}