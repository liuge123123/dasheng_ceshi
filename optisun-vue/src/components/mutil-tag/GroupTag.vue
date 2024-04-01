<template>
  <div>
    <el-tag class="tag" size="small" v-for="(item, index) in tags" :key="index" :type="type">
      {{item}}
    </el-tag>
  </div>
</template>

<script>
  export default {
    name: "GroupTag",
    props: {
      data: {
        type: String
      },
      labels: {
        type: Array
      },
      type: {
        type: String
      }
    },
    computed: {
      tags(){
        if(this.data && this.data != ''){
          let tagArr = this.data.split(',')
          if(this.labels) {
            const that = this
            var res = []
            tagArr = tagArr.map(item => {
              that.labels.forEach(function (em, index) {
                if (item == em.groupId) {
                  res.push(em.groupName)
                }
                if (item == em.labelId) {
                  res.push(em.labelName)
                }
              })
              return res
            })
          }
          return res
        }
        return []
      }
    }
  }
</script>

<style lang="scss" scoped>
  .tag{
    margin: 2px 5px 3px 0;
    &:last-child{
      margin-right: 0;
    }
  }
</style>
