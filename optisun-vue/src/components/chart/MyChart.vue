<template>
  <div class="mychart-container">
    <v-chart class="mychart" :options="opts"/>
  </div>
</template>

<script>
  export default {
    name: "MyChat",
    props: {
      options: Object
    },
    computed: {
      opts(){
        return this.options
      }
    }
  }
</script>

<style lang="scss" scoped>
  .mychart-container {
    position: relative;
    .mychart {
      width: 100%;
      height: 100%;
    }
  }
</style>
