module nts.uk.at.view.kaf011.a.start {
    let __viewContext: any = window['__viewContext'] || {};
    __viewContext.ready(() => {
        __viewContext['viewModel'] = new a.viewmodel.ViewModel();
        __viewContext.bind(__viewContext['viewModel']);
        __viewContext['viewModel'].kaf000_a.start("", 1, 0, moment(new Date()).format("YYYY/MM/DD")).done(() => {

        });
    });
}