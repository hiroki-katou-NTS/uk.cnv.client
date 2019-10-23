module cps003.g {
    let __viewContext: any = window['__viewContext'] || { ready: () => { }, bind: (viewModel: any) => { } };

    __viewContext.ready(() => {
        __viewContext['viewModel'] = new vm.ViewModel();
        __viewContext.bind(__viewContext['viewModel']);
    });
}