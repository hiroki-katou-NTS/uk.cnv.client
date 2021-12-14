module nts.uk.at.view.kdl020.a.start {
    let __viewContext: any = window['__viewContext'] || {};
    __viewContext.ready(() => {

        let vm =  __viewContext['viewModel'] = new nts.uk.at.view.kdl020.a.screenModel.ViewModel();
        __viewContext['viewModel'].start().done(() => {
            __viewContext.bind(vm); 
           
        });
    });
}