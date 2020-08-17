<template>
  <div style="height:600px">
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
            <el-button round size="small" type="primary">Choose Files</el-button>
          </el-upload>
        </el-col>
        <el-col>
          <el-button round size="small"  @click="getData" class="submitBtn">OK</el-button>
        </el-col>
      </el-row>
      <el-row>
        <vue-scroll :ops="ops" style="height:400px;width:300px">
          <div v-for="(item,index) in dataItemList" :key="index" class="files">
            <div class="file_name">{{item.name}}</div>
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
  data() {
    return {
      uploadFileForm: new FormData(), //上传文件的form
      fileList: [], //el-upload上传的文件列表,
      fileListUrl: [],
      dataItemList: [],
      resultUrl: "",
      file: {},
      ops: {
        bar: {
          background: "#808695",
        },
      },
    };
  },
  //   watch: {
  // dataItemList: {
  //   handler(val) {
  //     this.$emit("uploadDataList", val);
  //   },
  //   deep: true,
  // },
  //   },
  methods: {
    download() {
      let url = `/GeoProblemSolving/dataItem/download/${this.eventUrl.value}`;
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
    remove(file) {
      this.dataItemList = this.dataItemList.filter((item) => {
        return item.name != file.name;
      });
    },

    //上传文件到服务器
    async submitUpload() {
      this.uploadFileForm = new FormData();
      this.uploadFileForm.append("file", this.file.raw);

      let data = await post(
        `/GeoProblemSolving/dataItem/uploadSingle`,
        this.uploadFileForm
      );
    //   console.log(data);
    //   this.$message({
    //     message: "Upload Successfully!",
    //     type: "success",
    //   });
      let index = 0;
      let resultUrl = `http://221.226.60.2:8082/data?uid=${data}`;
      let name = this.file.name;
      let arr = {
        url: resultUrl,
        name: name,
        index: index,
      };
      this.dataItemList.push(arr);
      //   this.$set(this.dataItemList, this.dataItemList);
      index++;
      console.log(this.dataItemList, index);
    },

    getData() {
      this.$emit("uploadDataList", this.dataItemList);
    },
  },

  watch: {},
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
