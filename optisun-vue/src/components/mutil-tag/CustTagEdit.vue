<template>
  <div>
    <el-tag class="tag" size="small" v-for="(item, index) in res.selected" :key="index" closable @close="tagSelected(item)">
      {{item.labelValue}}
    </el-tag>
    <el-tag class="tag" size="small" effect="plain" type="info" @click="dialogVisible = true">+ 请选择</el-tag>
    <el-dialog title="选择标签" :visible.sync="dialogVisible" width="30%">
      <div style="height: 400px; overflow: auto">
        <div class="tag-panel" v-for="(item, index) in res.all" :key="index">
          <div class="title">{{item.groupName}}</div>
          <div class="body">
            <el-tag :effect="item1.selected ? 'dark' : 'light'" @click="tagSelected(item1)" class="tag" size="small" v-for="(item1, index1) in item.list" :key="index1">{{item1.labelValue}}</el-tag>
          </div>
        </div>
      </div>
      <div slot="footer">
        <el-button size="small" type="primary" @click="dialogVisible = false">确定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
  export default {
    name: "MutilTag",
    props: {
      value: {
        type: String
      }
    },
    data(){
      return {
        allTags: [],
        dialogVisible: false
      }
    },
    computed: {
      res(){
        let selected = []
        let all = []
        if(this.allTags && this.allTags.length > 0){
          let groups = {}
          let selectedArr = []
          if(this.value && this.value != '') {
            selectedArr = this.value.split(',')
          }
          for(let i = 0; i < this.allTags.length; i++){
            let item = this.allTags[i]
            // 选中
            if(this.isSelected(selectedArr, item.labelValueId) != -1){
              item.selected = true
              selected.push(item)
            }else{
              item.selected = false
            }

            // 分组
            let groupId = item.labelId
            if(groups.hasOwnProperty(groupId)){
              groups[groupId].list.push(item)
            }else{
              let list = []
              list.push(item)
              groups[groupId] = {
                groupId: item.labelId,
                groupName: item.labelKey,
                list: list
              }
            }
          }
          for(let key in groups){
            all.push(groups[key])
          }
        }
        return {
          selected: selected,
          all: all
        }
      }
    },
    created(){
      this.getAllLabels()
    },
    methods: {
      isSelected(arr, id){
        for(let i = 0; i < arr.length; i++){
          if(arr[i] == id){
            return i
          }
        }
        return -1
      },
      getAllLabels(){
        this.$http({
          url: this.$http.adornUrl('/servicesys/slabel/getAllTags'),
          method: 'get',
          params: this.$http.adornParams({
          })
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.allTags = data.data
          }
        })
      },
      tagSelected(item){
        let id = item.labelValueId
        let selectedIds = this.value
        let arr = []
        if(selectedIds && selectedIds != ''){
          arr = selectedIds.split(',')
          let index = this.isSelected(arr, id)
          if(index != -1){
            arr.splice(index, 1)
          }else{
            arr.push(id)
          }
        }else{
          arr.push(id)
        }
        this.$emit('input', arr.join(','))
      }
    }
  }
</script>

<style lang="scss" scoped>
  .tag{
    margin-right: 5px;
    cursor: pointer;
    &:last-child{
      margin-right: 0;
    }
  }
  .tag-panel {
    margin-top: 10px;
    &:first-child {
      margin-top: 0;
    }
  }
</style>
