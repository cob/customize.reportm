

<template>
  <div>
    <div class="report-form-overlay"/>

    <div class="report-form-dialog">
      <div class="report-form-content">
        <div class="dialog-header">
          <div class="close-button" @click="$emit('close')">X</div>
          <slot name="dialog-header"/>
        </div>

        <div class="dialog-body">
          <slot name="dialog-body"/>
        </div>

        <div class="dialog-footer">
          <slot name="dialog-footer"/>
        </div>
      </div>
    </div>
  </div>

</template>

<script setup>
import {defineEmits, onMounted, onUnmounted} from "vue";

const emits = defineEmits(['close'])

function closeListener(ev) {
  if (ev.key === 'Escape') {
    emits('close')
  }
}

onMounted(() => {
  document.addEventListener('keydown', closeListener)
})

onUnmounted(() => {
  document.removeEventListener('keydown', closeListener)
})
</script>

<style lang="scss">
.report-form-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9;

  width: 100%;
  height: 100vh;
  opacity: 0.8;
  background-color: #aaa;
}

.report-form-dialog {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 10;

  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;


  .report-form-content {
    border-radius: 8px;
    padding: 0 20px;

    width: 600px;
    background-color: #fff;
    box-shadow: 10px 10px 10px #aaa;
  }

  .dialog-header {
    position: relative;

    margin-bottom: 10px;
    padding: 10px 0 0;

    .close-button {
      color: #ddd;
      position: absolute;
      top: 25px;
      right: 0;

      font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
      font-size: 1.1em;
      font-weight: bold;
      cursor: pointer;
    }
  }

  .dialog-body {
    padding-right: 5px;
    flex: 1;

    display: flex;
    flex-direction: column;
  }

  .dialog-footer {
    border-top: 1px solid #ccc;
    padding: 20px 0;

    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
  }
}
</style>
