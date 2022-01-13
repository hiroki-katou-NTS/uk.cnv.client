<template>
    <nts-time-editor
        v-bind:class="'item-' + item.optionalItemNo"
        v-model="item.time"
        v-bind:show-title="false"
        v-bind:record-id="index"
        time-input-type="time-duration"
        v-bind:columns="{ input: 'col-10' }"
    />
</template>