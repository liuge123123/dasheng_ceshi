<template>
  <div>
    <el-form :inline="true" :model="dataForm" size="small" class="disSb">
      <el-form-item>
        <el-upload
          :action="url"
          :before-upload="beforeUploadHandle"
          :on-success="successHandle"
          :on-error="errorHandle"
          :show-file-list="false"
          :data="{groupId: dataForm.groupId}">
          <el-button type="primary" :loading="loading">上传图片</el-button>
        </el-upload>
      </el-form-item>
<!--      <el-form-item>-->
<!--        <el-select v-model="dataForm.selectGroupId" placeholder="移动至" @change="selectRes">-->
<!--          <el-option-->
<!--            v-for="item in groups"-->
<!--            :key="item.groupId"-->
<!--            :label="item.groupName"-->
<!--            :value="item.groupId">-->
<!--          </el-option>-->
<!--        </el-select>-->
<!--      </el-form-item>-->
      <el-form-item>
        <el-input
          placeholder="请输入关键字查询"
          v-model="dataForm.key"
          class="selectBtn" size="small">
          <i
            slot="suffix"
            class="el-icon-search"
            @click="searchHandle"
          ></i>
        </el-input>
      </el-form-item>
    </el-form>
    <div v-if="dataList && dataList.length > 0" class="img-list-wrap" v-loading="dataListLoading">
      <div class="img-item" v-for="(item, index) in dataList" :key="index">
        <el-image :ref="'imgItem' + index" class="img" fit="contain" :src="item.url" :preview-src-list="imgUrls"
                  :z-index="9999"></el-image>
        <div class="img-actions">
          <i class="action el-icon-zoom-in" @click="previewHandle(item, index)"></i>
          <i class="action el-icon-check" @click="fileSelectHandle(item, index)"></i>
          <i class="action el-icon-delete" @click="deleteHandle(item, index)"></i>
        </div>
        <el-checkbox class="img-check" v-if="item.checked" :checked="true"
                     @change="fileSelectHandle(item, index)"></el-checkbox>
      </div>
    </div>
    <div v-else class="img-list-no" v-loading="dataListLoading">
      <el-empty description="暂无图片"></el-empty>
    </div>
    <el-pagination
      @size-change="sizeChangeHandle"
      @current-change="currentChangeHandle"
      :current-page="pageIndex"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="pageSize"
      :total="totalPage"
      layout="total, prev, pager, next">
    </el-pagination>
  </div>
</template>

<script>
export default {
  name: "image-manager",
  data() {
    return {
      url: '',
      loading: false,
      dataList: [],
      pageIndex: 1,
      pageSize: 20,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      dataForm: {
        key: '',
        groupId: -1,
        selectGroupId:''
      },
      selectList: [],
      groups:[]
    }
  },
  computed: {
    imgUrls() {
      return this.dataList.map(item => {
        return item.url
      })
    }
  },
  mounted() {
    this.url = this.$http.adornUrl(`/sys/oss/upload?token=${this.$cookie.get('token')}`)
    this.searchHandle()
   // this.getGroups()
  },
  methods: {
    resetSelected(){
      this.selectList = []
      for (let i = 0; i < this.dataList.length; i++) {
        this.dataList[i].checked = false
        this.$set(this.dataList, i, this.dataList[i])
      }
    },
    /***
     * 获取选择的id
     * @returns {(string|*)[]}
     */
    getSelectedId(){
      return this.dataList.filter(item => {
        return item.checked
      }).map(item => {
        return item.fileId
      })
    },
    getSelected(){
      return this.dataList.filter(item => {
        return item.checked
      }).map(item => {
        return item.url
      })
    },
    getSelectedItems(){
      return this.dataList.filter(item => {
        return item.checked
      }).map(item => {
        return item
      })
    },
    fileSelectHandle(item, index) {
      let checked = item.checked
      item.checked = !checked
      this.$set(this.dataList, index, item)
      if(!checked){
        this.selectList.push(item.url)
      }else{
        for (let i = 0; i < this.selectList.length; i++) {
          if(item.url == this.selectList[i]){
            this.selectList.splice(i, 1)
            break
          }
        }
      }
      this.$emit('change', this.selectList)
    },
    previewHandle(item, index) {
      let el = this.$refs[`imgItem${index}`][0]
      el.clickHandler()
    },
    // 上传之前
    beforeUploadHandle(file) {
      if (file.type !== 'image/jpg' && file.type !== 'image/jpeg' && file.type !== 'image/png' && file.type !== 'image/gif') {
        this.$message.error('只支持jpg、png、gif格式的图片！')
        return false
      }
      this.loading = true
    },
    // 上传成功
    successHandle(response, file, fileList) {
      this.loading = false
      if (response && response.code === 0) {
        this.imgUrl = response.url
        this.searchHandle()
      } else {
        this.$message.error(response.msg)
      }
    },
    errorHandle(err, file, fileList) {
      this.loading = false
      this.$message.error("文件上传失败")
    },
    groupChange(group){
      this.dataForm.groupId = group.groupId
      this.searchHandle()
    },
    searchHandle() {
      this.pageIndex = 1
      this.getDataList()
    },
    // 获取数据列表
    getDataList() {
      this.dataListLoading = true
      this.$http({
        url: this.$http.adornUrl('/sys/oss/list'),
        method: 'get',
        params: this.$http.adornParams({
          'page': this.pageIndex,
          'limit': this.pageSize,
          'key': this.dataForm.key,
          'type':1
        })
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.dataList = data.page.list
          this.totalPage = data.page.totalCount
        } else {
          this.dataList = []
          this.totalPage = 0
        }
        this.dataListLoading = false
      })
    },
    // 每页数
    sizeChangeHandle(val) {
      this.pageSize = val
      this.pageIndex = 1
      this.getDataList()
    },
    // 当前页
    currentChangeHandle(val) {
      this.pageIndex = val
      this.getDataList()
    },
    // 多选
    selectionChangeHandle(val) {
      this.dataListSelections = val
    },
    // 删除
    deleteHandle(item) {
      var ids = [item.fileId]
      this.$confirm(`确定删除该图片?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/sys/oss/delete'),
          method: 'post',
          data: this.$http.adornData(ids, false)
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.$message({
              message: '操作成功',
              type: 'success',
              duration: 1500,
              onClose: () => {
                this.searchHandle()
              }
            })
          } else {
            this.$message.error(data.msg)
          }
        })
      }).catch(() => {
      })
    },
    //获取组数据
    // getGroups() {
    //   this.$http({
    //     url: this.$http.adornUrl('/img/shopuploadgroup/imgGroups'),
    //     method: 'get',
    //     params: this.$http.adornParams({
    //       groupType: 'image'
    //     })
    //   }).then(({data}) => {
    //     console.log(data)
    //     this.groups = data.data
    //   })
    // },
    //图片数据迁移
    selectRes(event, item) {
      let selected = this.getSelectedItems()
      if (selected.length==0) {
        this.$message({
          message: '您还没有选择任何文件',
          type: 'error'
        })
        return false
      }
      let ids = selected.map(item => {
        return item.fileId
      })

      this.$confirm(`确定移动选中的文件吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl(`/img/shopuploadfile/fileMove`),
          method: 'post',
          data: this.$http.adornData({
            'ids': ids,
            'groupId': event
          })
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.$message({
              message: '移动成功',
              type: 'success',
              duration: 1500,
              onClose: () => {
                this.getDataList()
              }
            })
          } else {
            this.$message.error(data.msg)
          }
        })
      }).catch(() => {
      })
    },
  }
}
</script>

<style scoped lang="scss">
.img-list-wrap {
  display: flex;
  flex-wrap: wrap;
  height: 440px;

  .img-item {
    width: 100px;
    height: 100px;
    border: 1px solid #eeeeee;
    margin: 5px;
    position: relative;

    &:nth-child(5n+1) {
      margin-left: 0;
    }

    &:nth-child(5n) {
      margin-right: 0;
    }

    .img {
      width: 100%;
      height: 100%;
    }

    .img-check {
      position: absolute;
      right: 0;
      top: 0;
      line-height: 1;
    }

    .img-actions {
      background: rgba(0, 0, 0, 0.5);
      position: absolute;
      left: 0;
      top: 0;
      width: 100%;
      height: 100%;
      display: none;
      text-align: center;
      color: #FFFFFF;
      line-height: 100px;

      .action {
        font-size: 16px;
        font-weight: bold;
        padding: 5px;
        cursor: pointer;
      }
    }

    &:hover {
      .img-actions {
        display: block;
      }
    }
  }
}
.img-list-no{
  display: flex;
  justify-content: center;
  align-items: center;
  height: 440px;
}
</style>
