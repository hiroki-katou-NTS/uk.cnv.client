import { Vue } from '@app/provider';
import { LanguageBar } from '@app/plugins';
import { component } from '@app/core/component';

import { NavbarComponent } from './nav';

@component({
    route: '/documents',
    template: `<div id="document_index" class="row">
        <div class="col-md-2">
            <lang-bar />
            <hr />
            <navi-bar />
        </div>
        <router-view :class="'col-md-10'" />
    </div>`,
    components: {
        'lang-bar': LanguageBar,
        'navi-bar': NavbarComponent
    },
    resource: {
        en: {
            documents: 'Documents',
            MarkdownComponent: 'Markdown language'
        },
        vi: {
            documents: 'Tài liệu',
            MarkdownComponent: 'Ngôn ngữ hạ đánh dấu'            
        }
    }
})
export class DocumentIndex extends Vue { }