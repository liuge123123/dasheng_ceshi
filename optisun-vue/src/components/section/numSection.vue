<template>
  <el-input-number v-model="num1" :min="1"></el-input-number>--
  <el-input-number v-model="num2" :min="1"></el-input-number>
</template>

<script>
  export default {
    props: {
      numSection: {
        type: String
      }
    },
    data() {
      return {
        num1: 1,
        num2: 1
      };
    },
    methods: {

    },
    computed:{
      tags(){

      }
    }
  };
</script>

<style scoped>

</style>
