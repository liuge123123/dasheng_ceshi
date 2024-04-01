<template>
  <div>
    <el-tag class="tag" size="small" v-for="(item, index) in tags" :key="index">
      {{item}}
    </el-tag>
  </div>
</template>

<script>
  export default {
    name: "MutilTag",
    props: {
      data: {
        type: String
      },
      labels: {
        type: Object
      }
    },
    computed: {
      tags(){
        if(this.data && this.data != ''){
          let tagArr = this.data.split(',')
          if(this.labels) {
            tagArr = tagArr.map(item => {
              return this.labels[item]
            })
          }
          return tagArr
        }
        return []
      }
    }
  }
</script>

<style lang="scss" scoped>
  .tag{
    margin-right: 5px;
    &:last-child{
      margin-right: 0;
    }
  }
</style>
