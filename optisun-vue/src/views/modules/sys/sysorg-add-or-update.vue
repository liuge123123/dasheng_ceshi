<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
      <el-tabs tab-position="top">
        <el-tab-pane label="机构信息">
          <el-form-item label="机构名称" prop="name">
            <el-input v-model="dataForm.name" placeholder="机构名称"></el-input>
          </el-form-item>
          <el-form-item label="机构logo" prop="logo">
            <el-upload
              ref="upload"
              action
              class="avatar-uploader"
              :http-request="uploadSectionFile"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeUploadHandle"
            >
              <img v-if="dataForm.logo" :src="dataForm.logo" class="avatar" />
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
          </el-form-item>
          <el-form-item label="机构介绍" prop="contents">
            <UEditor :config=config ref="ueditor" v-model="dataForm.contents"></UEditor>
          </el-form-item>
          <el-form-item label="小程序AppID" prop="appId">
            <el-input v-model="dataForm.appId" placeholder="小程序AppID"></el-input>
          </el-form-item>
          <!--          <el-form-item label="小程序AppSecret" prop="appSecret">-->
          <!--            <el-input v-model="dataForm.appSecret" placeholder="小程序AppSecret"></el-input>-->
          <!--          </el-form-item>-->
          <!--          <el-form-item label="微信商户号" prop="mchid">-->
          <!--            <el-input v-model="dataForm.mchid" placeholder="微信商户号id"></el-input>-->
          <!--          </el-form-item>-->
          <!--          <el-form-item label="微信支付密钥" prop="apikey">-->
          <!--            <el-input v-model="dataForm.apikey" placeholder="微信支付密钥"></el-input>-->
          <!--          </el-form-item>-->
          <el-form-item label="状态" prop="status">
            <el-switch
              v-model="dataForm.status"
              active-color="#00A854"
              inactive-color="#FF4500"
              :active-value="1"
              :inactive-value="0"
            >
            </el-switch>
          </el-form-item>
        </el-tab-pane>
        <el-tab-pane label="系统信息">
          <el-form-item label="系统名称" prop="sysTitle">
            <el-input v-model="dataForm.sysTitle" placeholder="系统名称"></el-input>
          </el-form-item>
          <el-form-item label="系统Logo" prop="sysLogo">
            <el-upload
              ref="upload"
              action
              class="avatar-uploader"
              :http-request="uploadSectionFile"
              :show-file-list="false"
              :on-success="handleLogoAvatarSuccess"
              :before-upload="beforeUploadHandle"
            >
              <img v-if="dataForm.sysLogo" :src="dataForm.sysLogo" class="avatar" />
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
          </el-form-item>
        </el-tab-pane>
      </el-tabs>
      <el-form-item style="text-align: center">
        <el-button @click="goBack">取消</el-button>
        <el-button type="primary" @click="dataFormSubmit()" :loading="loading">确定</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
import UEditor from '@/components/ueditor/ueditor.vue'
export default {
  components: {UEditor},
  data () {
    return {
      config: {
        //可以在此处定义工具栏的内容
        toolbars: [
          [//'anchor', //锚点
            'undo', //撤销
            'redo', //重做
            'bold', //加粗
            'indent', //首行缩进
            // 'snapscreen', //截图
            'italic', //斜体
            'underline', //下划线
            // 'strikethrough', //删除线
            // 'subscript', //下标
            // 'fontborder', //字符边框
            // 'superscript', //上标
            'formatmatch', //格式刷
            'source', //源代码
            // 'blockquote', //引用
            'pasteplain', //纯文本粘贴模式
            'selectall', //全选
            //'print', //打印
            // 'preview', //预览
            // 'horizontal', //分隔线
            'removeformat', //清除格式
            // 'time', //时间
            //'date', //日期
            //'unlink', //取消链接
            // 'insertrow', //前插入行
            // 'insertcol', //前插入列
            //'mergeright', //右合并单元格
            //'mergedown', //下合并单元格
            //'deleterow', //删除行
            //'deletecol', //删除列
            // 'splittorows', //拆分成行
            // 'splittocols', //拆分成列
            // 'splittocells', //完全拆分单元格
            //'deletecaption', //删除表格标题
            // 'inserttitle', //插入标题
            // 'mergecells', //合并多个单元格
            //'deletetable', //删除表格
            'cleardoc', //清空文档
            // 'insertparagraphbeforetable', //"表格前插入行"
            // 'insertcode', //代码语言
            'fontfamily', //字体
            'fontsize', //字号
            'paragraph', //段落格式
            // 'simpleupload', //单图上传
            'insertimage', //多图上传
            // 'edittable', //表格属性
            //'edittd', //单元格属性
            // 'link', //超链接
            // 'emotion', //表情
            // 'spechars', //特殊字符
            // 'searchreplace', //查询替换
            //'map', //Baidu地图
            // 'gmap', //Google地图
            'insertvideo', //视频
            //'help', //帮助
            'justifyleft', //居左对齐
            'justifyright', //居右对齐
            'justifycenter', //居中对齐
            //'justifyjustify', //两端对齐
            'forecolor', //字体颜色
            'backcolor', //背景色
            // 'insertorderedlist', //有序列表
            //'insertunorderedlist', //无序列表
            // 'fullscreen', //全屏
            // 'directionalityltr', //从左向右输入
            // 'directionalityrtl', //从右向左输入
            'rowspacingtop', //段前距
            'rowspacingbottom', //段后距
            //'pagebreak', //分页
            // 'insertframe', //插入Iframe
            // 'imagenone', //默认
            // 'imageleft', //左浮动
            //'imageright', //右浮动
            // 'attachment', //附件
            'imagecenter', //居中
            // 'wordimage', //图片转存
            'lineheight', //行间距
            //'edittip ', //编辑提示
            //  'customstyle', //自定义标题
            // 'autotypeset', //自动排版
            //'webapp', //百度应用
            // 'touppercase', //字母大写
            //'tolowercase', //字母小写
            //'background', //背景
            //'template', //模板
            // 'scrawl', //涂鸦
            // 'music', //音乐
            //'inserttable', //插入表格
            //'drafts', // 从草稿箱加载
            //'charts', // 图表
          ]
        ],
        autoHeightEnabled: false,
        autoFloatEnabled: true,
        initialContent: '请输入内容',   //初始化编辑器的内容,也可以通过textarea/script给值，看官网例子
        autoClearinitialContent: true, //是否自动清除编辑器初始内容，注意：如果focus属性设置为true,这个也为真，那么编辑器一上来就会触发导致初始化的内容看不到了
        initialFrameWidth: null,
        initialFrameHeight: 650,
        sysUrl: '',
        UEDITOR_HOME_URL: 'static/ueditor/'
      },
      loading: false,
      dataForm: {
        id: 0,
        name: '',
        contents: '',
        status: '',
        logo: '',
        appId: '',
        appSecret: '',
        mchid: '',
        apikey: '',
        certPem: '',
        keyPem: '',
        sysTitle:'',
        sysLogo:''
      },
      dataRule: {
        name: [
          { required: true, message: '机构名称不能为空', trigger: 'blur' }
        ],
        contents: [
          { required: true, message: '机构介绍不能为空', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '机构状态（1启用 0 停用）不能为空', trigger: 'blur' }
        ],
        logo: [
          { required: true, message: '机构logo不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    goBack(){
      this.$emit('back', {})
    },
    init (id) {
      this.dataForm.id = id || 0
      this.visible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        if (this.dataForm.id) {
          this.loading = true
          this.$http({
            url: this.$http.adornUrl(`/sys/sysorg/info/${this.dataForm.id}`),
            method: 'get',
            params: this.$http.adornParams()
          }).then(({data}) => {
            this.loading = false
            if (data && data.code === 0) {
              this.dataForm.name = data.sysOrg.name
              this.dataForm.contents = data.sysOrg.contents
              this.dataForm.status = data.sysOrg.status
              this.dataForm.logo = data.sysOrg.logo
              this.dataForm.appId = data.sysOrg.appId
              this.dataForm.appSecret = data.sysOrg.appSecret
              this.dataForm.mchid = data.sysOrg.mchid
              this.dataForm.apikey = data.sysOrg.apikey
              this.dataForm.certPem = data.sysOrg.certPem
              this.dataForm.keyPem = data.sysOrg.keyPem
              this.dataForm.sysLogo = data.sysOrg.sysLogo
              this.dataForm.sysTitle = data.sysOrg.sysTitle
              if (this.dataForm.contents != null && this.dataForm.contents != "") {
                this.$refs.ueditor.setUEContent(this.dataForm.contents)
              }
            }else{
              this.$message.error(data.msg)
            }
          })
        }
      })
    },
    // 表单提交
    dataFormSubmit () {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.loading = true
          this.$http({
            url: this.$http.adornUrl(`/sys/sysorg/${!this.dataForm.id ? 'save' : 'update'}`),
            method: 'post',
            data: this.$http.adornData({
              'id': this.dataForm.id || undefined,
              'name': this.dataForm.name,
              'contents': this.dataForm.contents,
              'status': this.dataForm.status,
              'logo': this.dataForm.logo,
              'appId': this.dataForm.appId,
              'appSecret': this.dataForm.appSecret,
              'mchid': this.dataForm.mchid,
              'apikey': this.dataForm.apikey,
              'certPem': this.dataForm.certPem,
              'keyPem': this.dataForm.keyPem,
              'sysLogo': this.dataForm.sysLogo,
              'sysTitle':this.dataForm.sysTitle
            })
          }).then(({data}) => {
            this.loading = false
            if (data && data.code === 0) {
              this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.visible = false
                  this.$emit('back', {refresh: true})
                }
              })
            } else {
              this.$message.error(data.msg)
            }
          })
        }
      })
    },
    // 上传之前
    beforeUploadHandle(file) {
      if (
        file.type !== "image/jpg" &&
        file.type !== "image/jpeg" &&
        file.type !== "image/png" &&
        file.type !== "image/gif"
      ) {
        this.$message.error("只支持jpg、png、gif格式的图片！");
        return false;
      }
    },
    uploadSectionFile: function(param) {
      let fileObj = param.file;
      let form = new FormData();
      form.append("file", fileObj);
      let headers = { headers: { "Content-Type": "multipart/form-data" } };
      this.$http({
        url: this.$http.adornUrl(`/sys/oss/upload?token=${this.$cookie.get('token')}`),
        method: "post",
        data: form,
        headers: headers
      }).then(({ data }) => {
        param.onSuccess(data);
      });
    },
    // 图片上传成功
    handleAvatarSuccess(response, file, fileList) {
      this.dataForm.logo = response.url
    },
    // 图片上传成功
    handleLogoAvatarSuccess(response, file, fileList) {
      this.dataForm.sysLogo = response.url
    },
    //选择文件上传
    uploadSectionPayFile: function(param) {
      let fileObj = param.file;
      let form = new FormData();
      form.append("file", fileObj);
      let headers = { headers: { "Content-Type": "multipart/form-data" } };
      this.$http({
        url: this.$http.adornUrl(`/sys/oss/upload?token=${this.$cookie.get('token')}`),
        method: "post",
        data: form,
        headers: headers
      }).then(({ data }) => {
        param.onSuccess(data);
      });
    },
    // 文件上传成功
    handlePaySuccess(response, file, fileList) {
      this.dataForm.certPem = response.url
    },
    // 上传之前
    beforeUploadPayHandle(file) {
      console.log(file.type)
      if (
        file.type !== "image/jpg" &&
        file.type !== "image/jpeg" &&
        file.type !== "image/png" &&
        file.type !== "image/gif"
      ) {
        this.$message.error("只支持p12文件");
        return false;
      }
    },
  }
}
</script>
<style>
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>
