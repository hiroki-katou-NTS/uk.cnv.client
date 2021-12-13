module cps007.a {
    import setShared = nts.uk.ui.windows.setShared;

    let __viewContext: any = window['__viewContext'] || {};
    __viewContext.ready(function() {
        __viewContext.transferred.ifPresent((data: any) => {
            setShared("CPS007A_PARAMS", data);
        });

        __viewContext['viewModel'] = new vm.ViewModel();
        __viewContext.bind(__viewContext['viewModel']);
    });
}