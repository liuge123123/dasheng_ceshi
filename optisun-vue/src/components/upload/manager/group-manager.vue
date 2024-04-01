<template>
  <div class="group-list" v-loading="dataListLoading">
    <div class="group-title">
      <div class="text">分组</div>
      <i class="icon el-icon-plus" @click="addHandle"></i>
    </div>
    <el-menu class="group-menu" :default-active="activeIndex">
      <el-menu-item v-for="(item, index) in dataList" :key="index" :index="item.groupId + ''">
        <div class="menu-item" @click="selectMenuHandle(item)">
          <div class="name">{{ item.groupName }}</div>
          <div class="actions" v-if="index > 1">
            <i class="el-icon-edit" @click="editHandle(item)"></i>
            <i class="el-icon-delete" @click="deleteHandle(item)"></i>
          </div>
        </div>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script>
export default {
  name: "group-manager",
  props:{
    groupType:{
      type: String,
      default: 'image'
    }
  },
  data() {
    return {
      activeIndex: '-1',
      dataList: [],
      dataListLoading: false
    }
  },
  computed: {},
  mounted() {
    this.dataList.push({groupId: -1, groupName: '全部'})
    this.dataList.push({groupId: 0, groupName: '未分组'})
    this.getDataList()
  },
  methods: {
    selectMenuHandle(item){
      this.$emit('change', item)
    },
    getDataList() {
      this.dataListLoading = true
      this.$http({
        url: this.$http.adornUrl('/img/shopuploadgroup/imgGroups'),
        method: 'get',
        params:this.$http.adornParams({
          'groupType': this.groupType,
        })
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.dataList.splice(2, this.dataList.length - 2)
          data.data.forEach(item => {
            this.dataList.push({
              groupId: item.groupId,
              groupName: item.groupName
            })
          })
          this.$emit('change', this.dataList[0])
        }
        this.dataListLoading = false
      })
    },
    addHandle() {
      this.$prompt('请输入分组名称', '添加分组', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^[\da-z\u2E80-\u9FFF]{2,21}$/i,
        inputErrorMessage: '分组名称必须为中英文字符，且长度必须在2-20个字符之间',
        inputValidator: (value) => {       // 点击按钮时，对文本框里面的值进行验证
          if(!value) {
            return '分组名称必须为中英文字符，且长度必须在2-20个字符之间';
          }
        }
      }).then(({value}) => {
        this.dataFormSubmit(null, value)
      }).catch(() => {

      })
    },
    editHandle(item) {
      this.$prompt('请输入分组名称', '修改分组', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValue: item.groupName,
        inputPattern: /^[\da-z\u2E80-\u9FFF]{2,21}$/i,
        inputErrorMessage: '分组名称必须为中英文字符，且长度必须在2-20个字符之间',
        inputValidator: (value) => {       // 点击按钮时，对文本框里面的值进行验证
          if(!value) {
            return '分组名称必须为中英文字符，且长度必须在2-20个字符之间';
          }
        }
      }).then(({value}) => {
        this.dataFormSubmit(item.groupId, value)
      }).catch(() => {

      })
    },
    deleteHandle(item) {
      var ids = [item.groupId]
      this.$confirm(`确定删除该分组，该分组图片将转移到未分组?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/img/shopuploadfile/delete'),
          method: 'post',
          data: this.$http.adornData(ids, false)
        }).then(({data}) => {
          this.dataListLoading = false
          if (data && data.code === 0) {
            this.$message({
              message: '操作成功',
              type: 'success',
              duration: 1500
            })
            this.getDataList()
          } else {
            this.$message.error(data.msg)
          }
        })
      })
    },
    // 表单提交
    dataFormSubmit(id, value) {
      this.dataListLoading = true
      this.$http({
        url: this.$http.adornUrl(`/img/shopuploadgroup/${!id ? 'save' : 'update'}`),
        method: 'post',
        data: this.$http.adornData({
          'groupId': id || undefined,
          'groupType': this.groupType,
          'groupName': value
        })
      }).then(({data}) => {
        this.dataListLoading = false
        if (data && data.code === 0) {
          this.$message({
            message: '操作成功',
            type: 'success',
            duration: 1500
          })
          this.getDataList()
        } else {
          this.$message.error(data.msg)
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
.group-list {
  overflow-y: auto;
  position: relative;
  border: 1px solid #eeeeee;

  &::-webkit-scrollbar {
    width: 1px;
  }

  .group-title {
    background: #eef1f6;
    padding: 10px 20px;
    display: flex;

    .text {
      flex: 1;
    }

    .icon {
      line-height: 32px;
      cursor: pointer;
    }
  }

  .group-menu {
    border-right: none;
    .menu-item{
      display: flex;
      .name{
        flex: 1;
      }
      .actions{
        display: none;
      }
      &:hover{
        .actions{
          display: block;
        }
      }
    }
  }
}
</style>
