<template>
    <div class="audio-editor">
      <div style="display: flex; gap: 20px;">
        <div class="controls" style="align-items: end; flex-shrink: 0;">
          <button v-if="!isRecording" @click="startRecording">开始录音</button>
          <button v-if="isRecording" @click="stopRecording" style="background-color: red;">停止录音</button>
          <label :class="isRecording?'upload-btn disabled':'upload-btn'">
            上传音频
            <input type="file" :disabled="isRecording" accept="audio/*" @change="handleFileUpload" />
          </label>
        </div>
        <div style="flex:1; display: flex; flex-direction: column; align-items: start; flex-wrap: wrap;">
          <label style="font-weight: 500; padding: 5px 0;">录音时请朗读以下文本：</label>
          <label style="text-align: left; color: red;">早上好！ 小优，今天天气怎么样？有点冷啊~空调调低两度。下午想听点音乐——播放周杰伦的《晴天》吧。对了晚上八点记得提醒我健身哦！</label>
        </div>
      </div>
      
      <div ref="waveform" class="waveform-container"></div>
      
      <div class="time-display">
        当前选择: {{ selectedStart.toFixed(2) }}s - {{ selectedEnd.toFixed(2) }}s
        <span v-if="hasSelection">(时长: {{ (selectedEnd - selectedStart).toFixed(2) }}s)</span>
      </div>
      <div class="row-wrapper">
      <div class="controls">
         <button @click="cropAudio" :disabled="!hasSelection">裁剪</button>
        <button @click="resetSelection" :disabled="!baseData || isRecording">重置</button>
    </div>
    <div class="controls">
    <button @click="playAll" :disabled="!baseData || isRecording">播放全部</button>
    <button @click="playSelection" :disabled="!hasSelection">播放选区</button>
    <button @click="pause" :disabled="!baseData || isRecording">暂停</button>
</div>
      </div>

        <div v-if="croppedAudioUrl" class="cropped-audio">
            <h3>裁剪后的音频:</h3>
            <sy-audio audioTitle subheading backgroundColor="#bfcadb" activeColor="#4167ed" :src="croppedAudioUrl"></sy-audio>
            <!-- <audio :src="croppedAudioUrl" controls></audio> -->
        </div>
    </div>
  </template>
  
  <script>
import WaveSurfer from 'wavesurfer.js';
  import RecordPlugin from 'wavesurfer.js/dist/plugins/record.js';
  import RegionsPlugin from 'wavesurfer.js/dist/plugins/regions.js';
  
  export default {
    name: 'CropVoice',
    data() {
      return {
        wavesurfer: null,
        recorder: null,
        regions: null,
        isRecording: false,
        selectedStart: 0,
        selectedEnd: 0,
        audioBlob: null,
        currentRegion: null,
        croppedAudioUrl: null,
        audioContext: null,
        baseData: null,
      };
    },
    computed: {
      hasSelection() {
        return this.selectedEnd > this.selectedStart;
      }
    },
    mounted() {
      this.initWaveSurfer();
    },
    beforeDestroy() {
      if (this.wavesurfer) {
        this.wavesurfer.destroy();
      }
      if (this.recorder) {
        this.recorder.stopRecording();
      }
        if (this.audioContext) {
        this.audioContext.close();
        }
        if (this.croppedAudioUrl) {
        URL.revokeObjectURL(this.croppedAudioUrl);
        }
    },
    methods: {

      reset(){
        if(this.isRecording){
          this.stopRecording(); 
          setTimeout(() => {
            this.clearSelection();
            // this.wavesurfer.empty();
            this.wavesurfer.destroy();
            this.initWaveSurfer();
          }, 500);
        }else{
          this.clearSelection();
          // this.wavesurfer.empty();
          this.wavesurfer.destroy();
          this.initWaveSurfer();
        }
      },
      initWaveSurfer() {
        this.wavesurfer = WaveSurfer.create({
          container: this.$refs.waveform,
          waveColor: '#4F4A85',
          progressColor: '#383351',
          cursorColor: 'transparent',
          cursorWidth: 0,
          barWidth: null,
          barRadius: null,
          barGap: null,
          height: 100,
          plugins: [
            RecordPlugin.create(),
            RegionsPlugin.create({
                dragSelection: false // 完全禁用拖动创建选区
            })
          ]
        });
  
        this.recorder = this.wavesurfer.getActivePlugins()[0];
        this.regions = this.wavesurfer.getActivePlugins()[1];
  
        this.recorder.on('record-end', (blob) => {
          this.audioBlob = blob;
          this.baseData = this.audioBlob;
          this.wavesurfer.load(URL.createObjectURL(blob));
          this.isRecording = false;
        });
  
        this.wavesurfer.on('ready', () => {
            if(!this.isRecording && !this.currentRegion) {
                this.createFullRegion();
            }
        });
      },
      
      createFullRegion() {
        // 清除所有现有区域
        this.regions.clearRegions();
        
        const duration = this.wavesurfer.getDuration();
        
        // 创建覆盖整个音频的区域
        this.currentRegion = this.regions.addRegion({
            id: 'full-region',
            start: 0,
            end: duration,
            color: 'rgba(255, 0, 0, 0.1)',
            drag: true,
            resize: true
        });
        
        // 更新当前选择
        this.updateSelectedRegion(this.currentRegion);
        
        // 监听区域更新（虽然我们禁用了调整大小，但保留以防万一）
        this.currentRegion.on('update-end', () => {
            this.updateSelectedRegion(this.currentRegion);
        });
      },
      
      updateSelectedRegion(region) {
        if (region) {
          this.selectedStart = region.start;
          this.selectedEnd = region.end;
        } else {
          this.clearSelection();
        }
      },
      
      clearSelection() {
        this.selectedStart = 0;
        this.selectedEnd = 0;
        if (this.currentRegion) {
          this.currentRegion.remove();
          this.currentRegion = null;
        }
      },
      resetSelection() {
        // this.wavesurfer.load(URL.createObjectURL(this.baseData));
        this.createFullRegion();
        if (this.croppedAudioUrl) {
            URL.revokeObjectURL(this.croppedAudioUrl);
            this.croppedAudioUrl = null;
        }
    },
    async cropAudio() {
      if (!this.hasSelection) return;
      
      try {
        // 获取原始音频Buffer
        const arrayBuffer = await this.getAudioBuffer();
        
        // 创建AudioContext
        this.audioContext = new (window.AudioContext || window.webkitAudioContext)();
        const audioBuffer = await this.audioContext.decodeAudioData(arrayBuffer);
        
        // 计算裁剪参数
        const startSample = Math.floor(this.selectedStart * audioBuffer.sampleRate);
        const endSample = Math.floor(this.selectedEnd * audioBuffer.sampleRate);
        const length = endSample - startSample;
        
        // 创建新的AudioBuffer
        const newBuffer = this.audioContext.createBuffer(
          audioBuffer.numberOfChannels,
          length,
          audioBuffer.sampleRate
        );
        
        // 复制选中的数据
        for (let channel = 0; channel < audioBuffer.numberOfChannels; channel++) {
          const channelData = audioBuffer.getChannelData(channel);
          const newChannelData = newBuffer.getChannelData(channel);
          for (let i = 0; i < length; i++) {
            newChannelData[i] = channelData[startSample + i];
          }
        }
        
        // 转换为Blob
        const blob = await this.audioBufferToWav(newBuffer);
        this.croppedAudioUrl = URL.createObjectURL(blob);
        
        // 可选：加载裁剪后的音频到wavesurfer
        // this.wavesurfer.load(this.croppedAudioUrl);
        
      } catch (error) {
        console.error('裁剪音频失败:', error);
      }
    },
    
    async getAudioBuffer() {
      if (this.audioBlob) {
        return await this.audioBlob.arrayBuffer();
      } else {
        // 如果是上传的文件，需要重新获取
        const response = await fetch(this.wavesurfer.getMediaElement().src);
        return await response.arrayBuffer();
      }
    },
    
    audioBufferToWav(buffer) {
      return new Promise((resolve) => {
        const numOfChan = buffer.numberOfChannels;
        const length = buffer.length * numOfChan * 2 + 44;
        const bufferWav = new ArrayBuffer(length);
        const view = new DataView(bufferWav);
        
        // 写入WAV头部
        this.writeString(view, 0, 'RIFF');
        view.setUint32(4, 36 + buffer.length * numOfChan * 2, true);
        this.writeString(view, 8, 'WAVE');
        this.writeString(view, 12, 'fmt ');
        view.setUint32(16, 16, true);
        view.setUint16(20, 1, true);
        view.setUint16(22, numOfChan, true);
        view.setUint32(24, buffer.sampleRate, true);
        view.setUint32(28, buffer.sampleRate * 2 * numOfChan, true);
        view.setUint16(32, numOfChan * 2, true);
        view.setUint16(34, 16, true);
        this.writeString(view, 36, 'data');
        view.setUint32(40, buffer.length * numOfChan * 2, true);
        
        // 写入PCM数据
        let offset = 44;
        for (let i = 0; i < buffer.numberOfChannels; i++) {
          const channel = buffer.getChannelData(i);
          for (let j = 0; j < channel.length; j++) {
            const sample = Math.max(-1, Math.min(1, channel[j]));
            view.setInt16(offset, sample < 0 ? sample * 0x8000 : sample * 0x7FFF, true);
            offset += 2;
          }
        }
        
        resolve(new Blob([view], { type: 'audio/wav' }));
      });
    },
    
    writeString(view, offset, string) {
      for (let i = 0; i < string.length; i++) {
        view.setUint8(offset + i, string.charCodeAt(i));
      }
    },
      startRecording() {
        this.isRecording = true;
        this.clearSelection();
        this.recorder.startRecording().catch((error) => {
          console.error('录音启动失败:', error);
          this.isRecording = false;
        });
      },
      
      stopRecording() {
        this.recorder.stopRecording();
      },
      
      playSelection() {
        if (!this.hasSelection) return;
        this.wavesurfer.play(this.selectedStart, this.selectedEnd);
      },
      
      playAll() {
        this.wavesurfer.play();
      },
      
      pause() {
        this.wavesurfer.pause();
      },
      
      handleFileUpload(event) {
        const file = event.target.files[0];
        if (!file) return;
        
        this.baseData = file;
        const url = URL.createObjectURL(file);
        this.wavesurfer.load(url);
        this.clearSelection();
      },
      getAudioUrl() {
        return this.croppedAudioUrl;
      },
    }
  };
  </script>
  
  <style scoped>
  .audio-editor {
    max-width: 800px;
    margin: 0 auto;
    /* padding: 20px; */
    font-family: Arial, sans-serif;
  }
  
  .row-wrapper{
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .controls {
    /* margin-bottom: 20px; */
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }
  
  .controls button, .controls .upload-btn {
    padding: 8px 12px;
    background-color: #4F4A85;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    /* min-width: 100px; */
    text-align: center;
  }
  
  .controls .upload-btn.disabled {
    background-color: #cccccc;
    cursor: not-allowed;
  }

  .controls button:disabled {
    background-color: #cccccc;
    cursor: not-allowed;
  }
  
  .waveform-container {
    margin: 20px 0 10px 0;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  }
  
  .time-display {
    margin-bottom: 20px;
    font-size: 14px;
    color: #666;
    text-align: right;
  }
  
  input[type="file"] {
    display: none;
  }
  
  .upload-btn {
    display: inline-block;
  }
  .cropped-audio {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 20px;
    /* padding: 15px; */
    background-color: #f5f5f5;
    border-radius: 4px;
  }

.cropped-audio a {
  display: inline-block;
  margin-top: 10px;
  color: #4F4A85;
  text-decoration: none;
}

.cropped-audio a:hover {
  text-decoration: underline;
}
  </style>