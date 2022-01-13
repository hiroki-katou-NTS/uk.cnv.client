module nts.uk.ui.at.kdw013.approvedlist {
    @handler({
        bindingName: 'kdw013-approveds'
    })
    export class Kdw013ApprovedBindingHandler implements KnockoutBindingHandler {
        init = (element: HTMLElement, componentName: () => string, allBindingsAccessor: KnockoutAllBindingsAccessor, __: any, bindingContext: KnockoutBindingContext): { controlsDescendantBindings: boolean; } => {
            const name = componentName();
            const params = { ...allBindingsAccessor() };

            ko.applyBindingsToNode(element, { component: { name, params } });

            return { controlsDescendantBindings: true };
        }
    }

    type Kdw013ApprovedParams = {
        mode: KnockoutObservable<boolean>;
        confirmers: KnockoutObservableArray<calendar.Employee>;
        $settings: KnockoutObservable<a.StartProcessDto | null>;
};

@component({
    name: 'kdw013-approveds',
    template: `
             <div data-bind="ntsAccordion: { active: 0}">
                <h3>
                    <label data-bind="i18n: 'KDW013_6'"></label>
                </h3>
                <div class='fc-employees'>
                    <ul data-bind="foreach: { data: $component.params.confirmers, as: 'item' }">
                        <li class="item">
                            <div data-bind="text: item.code"></div>
                            <div data-bind="text: item.name"></div>
                        </li>
                   </ul>
                </div>
            </div>
           `
})
export class Kdw013ApprovedComponent extends ko.ViewModel {
    constructor(public params: Kdw013ApprovedParams) {
        super();
    }
}
    
    
}
