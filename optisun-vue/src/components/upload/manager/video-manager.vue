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
          <el-button type="primary" :loading="loading">上传视频</el-button>
        </el-upload>
      </el-form-item>
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
    <div class="img-container" v-if="dataList && dataList.length > 0">
      <div class="img-list-wrap" v-loading="dataListLoading">
        <div class="img-item" v-for="(item, index) in dataList" :key="index">
          <video-player :ref="'imgItem' + index" class="img" :src="item.url" :z-index="9999" controls></video-player>
          <div class="img-actions">
            <i class="action el-icon-check" @click="fileSelectHandle(item, index)"></i>
            <i class="action el-icon-delete" @click="deleteHandle(item, index)"></i>
          </div>
          <el-checkbox class="img-check" :checked="true" v-if="item.checked"
                       @change="fileSelectHandle(item, index)"></el-checkbox>
        </div>
      </div>
    </div>
    <div v-else v-loading="dataListLoading" class="img-list-no">
      <el-empty description="暂无视频"></el-empty>
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
import VideoPlayer from '@/components/common/video-player'
export default {
  name: "image-manager",
  components: {VideoPlayer},
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
        groupId: -1
      },
      selectList: []
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
  },
  methods: {
    resetSelected(){
      this.selectList = []
      for (let i = 0; i < this.dataList.length; i++) {
        this.dataList[i].checked = false
        this.$set(this.dataList, i, this.dataList[i])
      }
    },
    getSelected(){
      return this.dataList.filter(item => {
        return item.checked
      }).map(item => {
        return item.url
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
      if (file.type !== 'video/mp4') {
        this.$message.error('只支持mp4格式的视频！')
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
          'type':'3'
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
      var ids = [item.id]
      this.$confirm(`确定删除该视频?`, '提示', {
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
    }
  }
}
</script>

<style scoped lang="scss">
.img-container{
  height: 440px;
  overflow-y: auto;
  overflow-x: hidden;
  &::-webkit-scrollbar {
    width: 1px;
  }
}
.img-list-wrap {
  display: flex;
  flex-wrap: wrap;

  .img-item {
    width: 100px;
    margin: 4px;
    position: relative;

    &:nth-child(5n+1) {
      margin-left: 0;
    }

    &:nth-child(5n) {
      margin-right: 0;
    }

    .img {
      width: 100px;
      height: 100px;
    }

    .img-check {
      position: absolute;
      right: 0;
      top: 0;
    }

    .img-actions {
      text-align: center;
      display: flex;
      background: rgba(255, 255, 255, 0.3);
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      border-bottom-left-radius: 4px;
      border-bottom-right-radius: 4px;
      .action {
        flex: 1;
        font-size: 16px;
        font-weight: bold;
        padding: 5px;
        cursor: pointer;
        color: #FFFFFF;
        font-size: 12px;
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
