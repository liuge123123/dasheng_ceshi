<template>
    <div class="edit-container">
      <quill-editor
        @change="onEditorChange"
        placeholder="文档内容"
        v-model="inputContents"
        ref="myQuillEditor"
        class="quill-editor"
        :options="editorOption"
      ></quill-editor>
      <!-- 图片 -->
      <el-upload
        class="upload-image"
        action
        :http-request="uploadDetailFile"
        :before-upload="imageUploadBeforeHandle"
        :on-success="imageSuccessHandle"
        :show-file-list="false"
      ></el-upload>
      <!--视频-->
      <el-upload action  class="upload-video"
          :http-request="uploadDetailFile"
          :before-upload="voiceUploadBeforeHandle"
          :on-success="voiceSuccessHandle"
          :limit="1" list-type="text" :show-file-list="false">
      </el-upload>
      <!-- 上传文件 -->
      <el-upload :show-upload-list="false"
                 class="upload-file"
                 type="drag" action
                 :http-request="uploadDetailFile"
                 :before-upload="fileUploadBeforeHandle"
                 :on-success="fileSuccessHandle"
      >
      </el-upload>
    </div>
</template>

<script>
  import  { quillEditor,Quill} from  "vue-quill-editor"; // 调用编辑器
  import  "quill/dist/quill.core.css";
  import  "quill/dist/quill.snow.css";
  import  "quill/dist/quill.bubble.css";

  // 这里引入修改过的video模块并注册
  import Video from './video'
  Quill.register(Video, true)

  // 自定义插入a链接
  var Link = Quill.import('formats/link');
  class FileBlot extends Link {  // 继承Link Blot
    static create(value) {
      let node = undefined
      if (value&&!value.href){  // 适应原本的Link Blot
        node = super.create(value);
      }
      else{  // 自定义Link Blot
        node = super.create(value.href);
        // node.setAttribute('download', value.innerText);  // 左键点击即下载
        node.innerText = value.innerText;
        node.download = value.innerText;
      }
      return node;
    }
  }
  FileBlot.blotName = 'link';
  FileBlot.tagName = 'A';
  Quill.register(FileBlot);

  // 标题
  const titleConfig = {
    'ql-bold': '加粗',
    'ql-color': '颜色',
    'ql-font': '字体',
    'ql-code': '插入代码',
    'ql-italic': '斜体',
    'ql-link': '添加链接',
    'ql-background': '背景颜色',
    'ql-size': '字体大小',
    'ql-strike': '删除线',
    'ql-script': '上标/下标',
    'ql-underline': '下划线',
    'ql-blockquote': '引用',
    'ql-header': '标题',
    'ql-indent': '缩进',
    'ql-list': '列表',
    'ql-align': '文本对齐',
    'ql-direction': '文本方向',
    'ql-code-block': '代码块',
    'ql-formula': '公式',
    'ql-image': '图片',
    'ql-video': '视频',
    'ql-clean': '清除字体样式',
    'ql-upload': '文件'
  };
  // 工具栏配置
  const toolbarOptions = [
    ["bold", "italic", "underline", "strike"], // toggled buttons
    ["blockquote", "code-block"],

    [{ header: 1 }, { header: 2 }], // custom button values
    [{ list: "ordered" }, { list: "bullet" }],
    [{ script: "sub" }, { script: "super" }], // superscript/subscript
    [{ indent: "-1" }, { indent: "+1" }], // outdent/indent
    [{ direction: "rtl" }], // text direction

    [{ size: ["small", false, "large", "huge"] }], // custom dropdown
    [{ header: [1, 2, 3, 4, 5, 6, false] }],

    [{ color: [] }, { background: [] }], // dropdown with defaults from theme
    [{ font: [] }],
    [{ align: [] }],
    ['link', 'image', 'video','upload'],
    ["clean"] // remove formatting button
  ];

  export default {
    name: "quillEditor-assembly",
    props: {
      id: {
        type: String,
        default: 'QuillEditor'
      },
      value: {
        type: String
      }
    },
    components: {
      quillEditor
    },
    mounted() {
      this.addQuillTitle()
    },
    data () {
      return {
        inputContents:'',
        // 富文本
        editorOption: {
          placeholder: "",
          theme: "snow", // or 'bubble'
          modules: {
            toolbar: {
              container: toolbarOptions, // 工具栏
              handlers: {
                //图片上传
                image: function(value) {
                  if (value) {
                    // 触发input框选择图片文件
                    document.querySelector(".upload-image input").click();
                  }
                  else {
                    this.quill.format('image', false);
                  }
                },
                //视频上传
                video: function(value){
                  if (value) {
                    // 触发input框选择视频文件
                    document.querySelector('.upload-video input').click()
                  }
                  else {
                    this.quill.format('video', false);
                  }
                },
                //文件上传
                upload: value => {
                  if (value) {
                    document.querySelector('.upload-file input').click()
                  }
                }
              }
            }
          }
        },
      }
     },
    methods:{
      //上传文件到后台
      uploadDetailFile: function(param) {
        // 自定义文件上传
        let fileObj = param.file;
        let form = new FormData();
        form.append("file", fileObj);
        let headers = { headers: { "Content-Type": "multipart/form-data" } };
        this.$http({
          url: this.$http.adornUrl(
            `/sys/oss/upload?token=${this.$cookie.get("token")}`
          ),
          method: "post",
          data: form,
          headers: headers
        }).then(({ data }) => {
          param.onSuccess(data);
        });
      },
      // 图片上传之前
      imageUploadBeforeHandle(file) {
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
      // 图片上传成功
      imageSuccessHandle(response, file, fileList) {
        // 获取富文本组件实例
        let quill = this.$refs.myQuillEditor.quill;
        var url = response.url;
        if (response.code === 0 && url !== null) {
          // 获取光标所在位置
          let length = quill.getSelection().index;
          // 插入图片
          quill.insertEmbed(length, "image", url);
          // 调整光标到最后
          quill.setSelection(length + 1);
        } else {
          this.$message.error("图片插入失败");
        }
      },
      //上传视频之前
      voiceUploadBeforeHandle(file){
        var self = this;
        var type = file.type;
        var size = file.size;
        if (type != 'video/mp4'&&type!='audio/mpeg') {
          self.$message({
            message: "只能上传mp4格式的视频",
            type: 'error'
          });
          return false;
        } else if (size > 1024 * 1024 * 300) {
          self.$message({
            message: "文件大小不能超过300M",
            type: 'error'
          });
          return false;
        }
      },
      // 视频上传成功
      voiceSuccessHandle(response, file, fileList) {
        // 获取富文本组件实例
        let quill = this.$refs.myQuillEditor.quill
        var url = response.url
        if (response.code === 0 && url !== null) {
          // 获取光标所在位置
          let length = quill.getSelection().index
          // 插入视频
          quill.insertEmbed(length, 'video', url)
          // 调整光标到最后
          quill.setSelection(length + 1)
        } else {
          this.$message.error('视频插入失败')
        }
      },
      //文件上传 之前
      fileUploadBeforeHandle(file){
        var self = this;
        var type = file.type;
        var size = file.size;
        console.log(type)
        // if (type != 'video/mp4'&&type!='audio/mpeg') {
        //   self.$message({
        //     message: "只能上传mp4格式的视频",
        //     type: 'error'
        //   });
        //   return false;
        // } else if (size > 1024 * 1024 * 300) {
        //   self.$message({
        //     message: "文件大小不能超过300M",
        //     type: 'error'
        //   });
        //   return false;
        // }
      },
      //文件上传成功
      fileSuccessHandle(response, file, fileList) {
        // 获取富文本组件实例
        console.log(file)
        let quill = this.$refs.myQuillEditor.quill
        var url = response.url
        if (response.code === 0 && url !== null) {
          // 获取光标所在位置
          let length = quill.getSelection().index
          let fileNameLength = file.name.length
          // 插入文件
          //quill.insertText(length,file.name,'api');
          quill.insertEmbed(length, 'link', {href:url, innerText:file.name}, 'api')
          // 调整光标到最后
          quill.setSelection(length + fileNameLength)
        } else {
          this.$message.error('文件插入失败')
        }
      },
      onEditorChange({editor, html, text}) {
        this.$emit('input', html)
      },
      //中文按钮
      addQuillTitle() {
        const oToolBar = document.querySelector('.ql-toolbar');
        const aButton = oToolBar.querySelectorAll('button');
        const aPicker = oToolBar.querySelectorAll('.ql-picker');
        aButton.forEach(function (item) {
          if (item.className === 'ql-script') {
            item.value === 'sub' ? item.title = '下标' : item.title = '上标';
          } else if (item.className === 'ql-indent') {
            item.value === '+1' ? item.title = '向右缩进' : item.title = '向左缩进';
          } else {
            item.title = titleConfig[item.classList[0]];
          }
        });
        aPicker.forEach(function (item) {
          item.title = titleConfig[item.classList[0]];
        });
      }
    },
    watch: {
      value(newVal, oldVal) {
        this.inputContents = newVal
      }
    }
  }
</script>

<style>
  .ql-snow.ql-toolbar .ql-upload{
    background: url("~@/assets/img/upload.svg");
    background-size: 16px 16px;
    background-position: center center;
    background-repeat:no-repeat;
    /*background: red;*/
  }
  .upload-image,
  .upload-file,
  .upload-video{
    width: 0;
    height: 0;
    display: none;
  }
  .edit-container {
    height: 100%;
  }

  .quill-editor {
    line-height: normal !important;
    height: calc(100% - 90px);
  /*overflow: visible;*/
  .ql-editor {
    /*min-height: 650px;*/
  }
  }
</style>
