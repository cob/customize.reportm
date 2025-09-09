<template>
  <div class="report-form-row">
    <label class="report-form-label" for="argument.name">{{label}}</label>
    <input class="report-form-input" type="text" :name="label" @input="$emit('update:value', $event.target.value)">
  </div>
</template>

<script setup>
import {defineProps, defineEmits} from "vue";

defineProps({label: String, value: String})
defineEmits(['update:value'])
</script>
