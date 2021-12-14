module nts.uk.at.view.kdl029.a.start {
    let __viewContext: any = window['__viewContext'] || {};
    __viewContext.ready(() => {

        let vm =  __viewContext['viewModel'] = new nts.uk.at.view.kdl029.a.screenModel.ViewModel();
            __viewContext.bind(__viewContext['viewModel']); 
    });
}