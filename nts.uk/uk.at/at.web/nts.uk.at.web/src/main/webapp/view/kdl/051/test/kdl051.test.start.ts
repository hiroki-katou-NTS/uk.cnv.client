module nts.uk.at.view.kdl051.test.start {
    let __viewContext: any = window['__viewContext'] || {};
    __viewContext.ready(() => {

        let vm =  __viewContext['viewModel'] = new test.screenModel.ViewModel();
        __viewContext['viewModel'].start().done(() => {
            __viewContext.bind(__viewContext['viewModel']); 
           
        });
    });
}