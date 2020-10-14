<template>
  <div style="height: 600px">
    <el-row>
      <el-row>
        <el-col :span="5">
          <el-upload
            action
            :auto-upload="true"
            :file-list="fileList"
            :show-file-list="false"
            :on-change="onChange"
            ref="upload"
            :http-request="submitUpload"
            :on-remove="handleRemove"
          >
            <el-button round size="small" type="primary"
              >Choose Files</el-button
            >
          </el-upload>
        </el-col>
      </el-row>
      <el-row>
        <vue-scroll :ops="ops" style="height: 400px; width: 300px">
          <div v-for="(item, index) in dataItemList" :key="index" class="files">
            <div class="file_name">{{ item.name }}</div>
            <i class="el-icon-close" @click="remove(item)"></i>
          </div>
        </vue-scroll>
      </el-row>
    </el-row>
  </div>
</template>
<script>
import X2JS from "x2js"; //xml数据处理插件
import { get, del, post, put } from "../../../axios";
export default {
  props: {
    stepInformation: {
      type: Object,
    },
  },

  data() {
    return {
      projectId: this.$route.params.projectId,
      userInfo: this.$store.getters.userInfo,
      uploadFileForm: new FormData(), //上传文件的form
      fileList: [], //el-upload上传的文件列表,
      dataItemList: [],
      file: {},
      ops: {
        bar: {
          background: "#808695",
        },
      },
      stepInfo: this.stepInformation,
    };
  },

  watch: {
    stepInformation: {
      handler(val) {
        this.stepInfo = val;
      },
      deep: true,
    },
  },

  methods: {
    async getDataItem() {
      let dataItem = await get(
        `/GeoProblemSolving/r/dataItems/${this.projectId}`
      );
    
      this.dataItemList = dataItem.filter((item) => {
        return item.isDirect == true;
      });
    },

    download() {
      let url = `/GeoProblemSolving/dataContainer/download/${this.eventUrl.value}`;
      window.open(url);
    },

    //上传文件发生改变时
    onChange(file, fileList) {
      this.file = file;
      this.fileList = fileList;
    },

    handleRemove(file, fileList) {
      this.dataItemList = this.dataItemList.filter((item) => {
        return item.name != file.name;
      });
    },

    async remove(resource) {
      await del(`/GeoProblemSolving/r/dataItems/${resource.id}`);
      this.dataItemList.splice(
        this.dataItemList.findIndex((item) => item.id === resource.id),
        1
      );
    },

    //上传文件到服务器
    async submitUpload() {
      this.uploadFileForm = new FormData();
      this.uploadFileForm.append("file", this.file.raw);

      let uid = await post(
        `/GeoProblemSolving/dataContainer/uploadSingle`,
        this.uploadFileForm
      );
      let list = {
        userId: this.userInfo.userId,
        pid: this.projectId,

        url: `http://221.226.60.2:8082/data?uid=${uid}`,
        name: this.file.name,
        isDirect: true, //if true -- 是直接上传的数据    --false是中间数据
        stepBind: {
          stepId: this.stepInfo.stepId,
          stepName: this.stepInfo.name,
        },
      };

      let data = await post(`/GeoProblemSolving/r/dataItems`, list);
      this.dataItemList.push(data);
    },
  },
  mounted() {
    this.getDataItem();
  },
};
</script>

<style>
.submitBtn {
  position: absolute;
  top: 0px;
  left: 110px;
}

.file_name {
  float: left;
  width: 270px;
}
.files {
  margin: 5px 0;
  font-size: 16px;
  padding: 3px 0px 3px 10px;
}
.files:hover {
  cursor: pointer;
  transition: all 0.2s ease-out;
  color: #176edf;
  background-color: rgba(120, 159, 231, 0.1);
}
</style>
