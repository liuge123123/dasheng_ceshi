<template>
  <div class="edit-container">
    <quill-editor @change="onEditorChange" ref="myTextEditor" v-model="content" :options="defaultConfig"
                  class="quill-editor"></quill-editor>
    <!-- 弹窗, 上传文件 -->
    <upload v-if="uploadVisible" ref="upload" @refreshDataList="uploadSuccess"></upload>
  </div>
</template>

<script>
  import 'quill/dist/quill.core.css'
  import 'quill/dist/quill.snow.css'
  import 'quill/dist/quill.bubble.css'

  import Upload from './oss-upload'
  import {Quill, quillEditor} from 'vue-quill-editor'

  import { ImageDrop } from 'quill-image-drop-module'
  import ImageResize from 'quill-image-resize-module'
  Quill.register('modules/imageDrop', ImageDrop)
  Quill.register('modules/imageResize', ImageResize)

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
    ['bold', 'italic', 'underline', 'strike'],        // toggled buttons
    ['blockquote', 'code-block'],

    [{'align': []}, {'list': 'ordered'}, {'list': 'bullet'},{'script': 'sub'}, {'script': 'super'},{'indent': '-1'}, {'indent': '+1'},{'direction': 'rtl'}],
    [{'color': []}, {'background': []}],
    [{'size': ['small', false, 'large', 'huge']}],  // custom dropdown
    [{'header': [1, 2, 3, 4, 5, 6, false]}],
    ['link', 'image', 'video'],
    ['clean']                                         // remove formatting button
  ]

  export default {
    name: "MyQuillEditor",
    components: {
      quillEditor,
      Upload
    },
    props: {
      id: {
        type: String,
        default: 'QuillEditor'
      },
      config: {
        type: Object
      },
      value: {
        type: String
      }
    },
    data() {
      return {
        updateUrl: '',
        content: '',
        editor: null,
        uploadVisible: false,
        defaultConfig: {
          theme: 'snow',
          placeholder: '编辑内容',
          modules: {
            imageResize: {
              modules: [ 'Resize', 'DisplaySize', 'Toolbar' ],
              displayStyles:{
                backgroundColor:'black',
                border:'none',
                color:'white'
              },
            },
            toolbar: {
              container: toolbarOptions,  // 工具栏
              handlers: {
                'image': (value) => {
                  if (value) {
                    this.uploadHandle()
                  } else {
                    this.quill.format('image', false);
                  }
                }

                // 'image': function () {
                //   QuillWatch.emit(this.quill.id)
                // }


              }
            }
          }
        }
      }
    },
    mounted() {
      this.addQuillTitle()
    },
    destoryed() {
    },
    methods: {
      // 上传文件
      uploadHandle() {
        this.uploadVisible = true
        this.$nextTick(() => {
          this.$refs.upload.init()
        })
      },
      uploadSuccess(res) {
        // 获取富文本组件实例
        let quill = this.$refs.myTextEditor.quill
        // 如果上传成功
        if (res && res.length > 0)  {
          res.forEach(item => {
            // 获取光标所在位置
            let length = quill.getSelection(true).index;
            // 插入图片，res为服务器返回的图片链接地址
            quill.insertEmbed(length, 'image', item.response.url);
            // 调整光标到最后
            quill.setSelection(length + 1)
          })

        } else {
          // 提示信息，需引入Message
          this.$message.error('图片插入失败')
        }
      },
      onEditorChange({editor, html, text}) {
        this.$emit('input', html)
      },
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
      },
    },
    watch: {
      value(newVal, oldVal) {
        this.content = newVal
      }
    }
  }
</script>

<style lang="scss">
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
  .ql-snow .ql-picker.ql-header,
  .ql-snow .ql-picker.ql-size{
    border: 1px solid #eeeeee;
  }

  .ql-snow .ql-tooltip[data-mode="link"]::before {
    content: "请输入链接地址:";
  }

  .ql-snow .ql-tooltip.ql-editing a.ql-action::after {
    border-right: 0px;
    content: "保存";
    padding-right: 0px;
  }

  .ql-snow .ql-tooltip[data-mode="video"]::before {
    content: "请输入视频地址:";
  }

  .ql-snow .ql-picker.ql-size .ql-picker-label::before,
  .ql-snow .ql-picker.ql-size .ql-picker-item::before {
    content: "14px";
  }

  .ql-snow .ql-picker.ql-size .ql-picker-label[data-value="small"]::before,
  .ql-snow .ql-picker.ql-size .ql-picker-item[data-value="small"]::before {
    content: "10px";
  }

  .ql-snow .ql-picker.ql-size .ql-picker-label[data-value="large"]::before,
  .ql-snow .ql-picker.ql-size .ql-picker-item[data-value="large"]::before {
    content: "18px";
  }

  .ql-snow .ql-picker.ql-size .ql-picker-label[data-value="huge"]::before,
  .ql-snow .ql-picker.ql-size .ql-picker-item[data-value="huge"]::before {
    content: "32px";
  }

  .ql-snow .ql-picker.ql-header .ql-picker-label::before,
  .ql-snow .ql-picker.ql-header .ql-picker-item::before {
    content: "文本";
  }

  .ql-snow .ql-picker.ql-header .ql-picker-label[data-value="1"]::before,
  .ql-snow .ql-picker.ql-header .ql-picker-item[data-value="1"]::before {
    content: "标题1";
  }

  .ql-snow .ql-picker.ql-header .ql-picker-label[data-value="2"]::before,
  .ql-snow .ql-picker.ql-header .ql-picker-item[data-value="2"]::before {
    content: "标题2";
  }

  .ql-snow .ql-picker.ql-header .ql-picker-label[data-value="3"]::before,
  .ql-snow .ql-picker.ql-header .ql-picker-item[data-value="3"]::before {
    content: "标题3";
  }

  .ql-snow .ql-picker.ql-header .ql-picker-label[data-value="4"]::before,
  .ql-snow .ql-picker.ql-header .ql-picker-item[data-value="4"]::before {
    content: "标题4";
  }

  .ql-snow .ql-picker.ql-header .ql-picker-label[data-value="5"]::before,
  .ql-snow .ql-picker.ql-header .ql-picker-item[data-value="5"]::before {
    content: "标题5";
  }

  .ql-snow .ql-picker.ql-header .ql-picker-label[data-value="6"]::before,
  .ql-snow .ql-picker.ql-header .ql-picker-item[data-value="6"]::before {
    content: "标题6";
  }

  .ql-snow .ql-picker.ql-font .ql-picker-label::before,
  .ql-snow .ql-picker.ql-font .ql-picker-item::before {
    content: "标准字体";
  }

  .ql-snow .ql-picker.ql-font .ql-picker-label[data-value="serif"]::before,
  .ql-snow .ql-picker.ql-font .ql-picker-item[data-value="serif"]::before {
    content: "衬线字体";
  }

  .ql-snow .ql-picker.ql-font .ql-picker-label[data-value="monospace"]::before,
  .ql-snow .ql-picker.ql-font .ql-picker-item[data-value="monospace"]::before {
    content: "等宽字体";
  }
</style>
