module cps001.d.MainScreen {
    let __viewContext: any = window['__viewContext'] || {};
    __viewContext.ready(() => {
        __viewContext['viewModel'] = new vm.ViewModel();

        __viewContext.bind(__viewContext['viewModel']);

    });
}