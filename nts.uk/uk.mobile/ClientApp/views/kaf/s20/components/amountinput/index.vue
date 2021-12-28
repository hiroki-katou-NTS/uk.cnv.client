<template>
    <nts-number-editor
            v-bind:class="'item-' + item.optionalItemNo"
            v-model="item.amount"
            v-bind:show-title="false"
            v-bind:record-id="index"
            v-bind:columns="{ input: 'col-10' }"
    />
</template>