<template>
  <el-dialog class="voice-clone-dialog" :title="title" :visible.sync="visible" width="800px" @close="handleClose" @open="handleOpen" :close-on-click-modal="false">
    <el-form ref="form" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="声音名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入声音名称"></el-input>
      </el-form-item>
        <crop-voice ref="cropVoice" />
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="handleCancel">取 消</el-button>
      <el-button type="primary" @click="handleSubmit">确 定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import Api from '@/apis/api';
import CropVoice from './CropVoice.vue';

export default {
  components: { CropVoice },
  name: 'VoiceCloneDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: ''
    },
    form: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      rules: {
        name: [
          { required: true, message: '请输入声音名称', trigger: 'blur' }
        ],
      }
    }
  },
  computed: {
    isTypeDisabled() {
      // 如果有id，说明是编辑模式，禁用类型选择
      return !!this.form.id
    }
  },
  methods: {
    handleClose() {
      this.$refs.form.clearValidate();
      this.$refs.cropVoice.reset();
      this.$emit('cancel');
    },
    handleCancel() {
      this.$refs.form.clearValidate();
      this.$refs.cropVoice.reset();
      this.$emit('cancel');
    },
    handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          let audioUrl = this.$refs.cropVoice.getAudioUrl();
          if (!audioUrl) {
            this.$message.error('请先裁剪音频')
            return;
            }

        let _loading = this.$loading({
        lock: true,
        text: '声音复刻中...',
        target: document.querySelector(".voice-clone-dialog DIV"), // 指定目标元素
        spinner: 'el-icon-loading',
        background: 'rgba(255, 255, 255, 0.7)'
      });

            try {
            // 从Blob URL获取Blob对象
            const response = await fetch(audioUrl);
            const blob = await response.blob();
            
            // 创建FormData并添加文件
            const formData = new FormData();
            formData.append('file', blob, 'cropped-audio.wav');
            // 添加其他可能需要的数据
            formData.append('filename', 'cropped-audio.wav');
            formData.append('name', this.form.name);




      Api.timbre.cloneVoice(formData, (res) => {
        _loading.close()
        res = res.data
        if (res.code === 0) {
          this.$message.success('声音复刻成功')
        } else {
          this.$message.error(res.msg || '文件上传失败')
        }
      })
    } catch (error) {
      _loading.close()
      console.error('上传错误:', error);
      this.$message.error('上传失败: ' + error.message);
    }
  }
      })
    },

    handleOpen() {
      // 重置上传相关状态
      this.uploadProgress = 0
      this.uploadStatus = ''
      this.isUploading = false
      // 重置表单中的文件相关字段
      if (!this.form.id) {  // 只在新增时重置
        this.form.firmwarePath = ''
        this.form.size = 0
        // 重置上传组件
        this.$nextTick(() => {
          if (this.$refs.upload) {
            this.$refs.upload.clearFiles()
          }
        })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.upload-demo {
  text-align: left;
}

.el-upload__tip {
  line-height: 1.2;
  padding-top: 5px;
  color: #909399;
}

.hint-text {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #979db1;
  font-size: 11px;
}
</style>