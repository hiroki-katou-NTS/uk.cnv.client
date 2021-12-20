module cps003.a {
    import setShared = nts.uk.ui.windows.setShared;
    let __viewContext: any = window['__viewContext'] || { ready: () => { }, bind: (viewModel: any) => { } };

    __viewContext.ready(() => {
        __viewContext.transferred.ifPresent((data: any) => {
            setShared("CPS003A_PARAMS", data);
        });

        __viewContext['viewModel'] = new vm.ViewModel();
        __viewContext.bind(__viewContext['viewModel']);
    });
}