<template>
  <div>
    <!-- <vue-scroll style="height: 350px"> -->
    <!-- <div v-for="(data, index) in dataItemList" :key="index">
          <div class="data_info">
            <div class="data_info_name">{{ data.name }}</div>
            <div class="data_info_hover">
              <i
                class="el-icon-download"
                @click="downloadDataResource(data)"
              ></i>
            </div>
          </div>
        </div> -->

    <el-row class="row-style">
      <div class="tools_container">
        <!-- <div>Tools Select</div> -->
        <manage-tools
          @selectTools="selectTools"
          :initTools="toolItemList"
          footer-hide
        ></manage-tools>
      </div>
      <el-divider direction="vertical" class="container_divider"></el-divider>
      <div class="data_container">
        <!-- <div>Data Upload</div> -->
        <data-upload></data-upload>
      </div>
    </el-row>
    <!-- </vue-scroll> -->
  </div>
</template>

<script>
import { get, post, patch } from "@/axios";

import dataUpload from "./DataUpload";
import manageTools from "./ManageTools";
export default {
  components: {
    dataUpload,
    manageTools,
  },

  data() {
    return {
      dataItemList: [],
      projectId: this.$route.params.projectId,
      dataItemList: [],
      toolItemList: [],
    };
  },

  methods: {
    downloadDataResource(data) {
      window.open(data.url);
    },

    async getResources() {
      let dataItem = await get(
        `/GeoProblemSolving/r/dataItems/${this.projectId}`
      );
      let modelItem = await get(
        `/GeoProblemSolving/r/toolItems/${this.projectId}`
      );

      this.toolItemList = modelItem;
      this.dataItemList = dataItem;
    },
  },
  async mounted() {
    await this.getResources();
  },
};
</script>

<style lang='scss' scoped>
.row-style {
  margin: 0 10px;
  height: 450px;
}
.data_info {
  height: 20px;
}
.data_info_hover {
  display: none;
}
.data_info_name {
  float: left;
  width: 220px;
}
.data_info:hover {
  cursor: pointer;
  background-color: rgba(99, 142, 197, 0.2);

  -webkit-transition: all 0.2s ease-out;
  transition: all 0.2s ease-out;
  .data_info_hover {
    display: block;
    float: left;
    color: rgb(45, 54, 92);
  }
}

.tools_container {
  // width: 40px;
  float: left;
}
.container_divider {
  float: left;
  height: 95%;
}
.data_container {
  // width: 300px;
  float: left;
  margin-left: 15px;
}
</style>