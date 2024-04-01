<template>
  <transition name="fade">
    <router-view></router-view>
  </transition>
</template>

<script>
  export default {
  }
</script>
<style>
  ::-webkit-scrollbar {
    width: 8px;
    height: 8px;
    background-color: #fff;
  }
  ::-webkit-scrollbar-thumb {
    -webkit-box-shadow: inset 0 0 6px rgba(0, 0, 0, .3);
    background-color: rgba(0, 0, 0, .1)
  }
</style>
