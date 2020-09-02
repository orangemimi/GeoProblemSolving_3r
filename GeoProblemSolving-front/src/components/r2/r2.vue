<template>
  <div>
    <div class="content">
      <menu-left class="lineStyle"></menu-left>
      <div class="lineStyle" :style="{width:contentWidth+'px'}">
        <flow-chart :projectInfo="projectInfo" :userRole="userRole" @dblclick="doubleClick"></flow-chart>
      </div>
      <div class="lineStyle cardRight" :style="{height:contentHeight+'px'}">
        <info-right :projectInfo="projectInfo"></info-right>
      </div>
      <div style="clear: both;"></div>
    </div>
    <el-dialog
      :visible.sync="resourceCollectionModal"
      :close-on-click-modal="false"
      title="Please upload the file"
      class="resourceDialog"
    >
      <el-steps :active="active" finish-status="success" simple style="margin-top: 20px">
        <el-step title="Select Model"></el-step>
        <el-step title="Upload Data"></el-step>
      </el-steps>

      <div v-show="active==0" style="height:600px">
        <manage-tools
          :userId="userInfo.userId"
          @selectTools="selectTools"
          footer-hide
          :initTools="initResource.toolItemList"
        ></manage-tools>
      </div>
      <div v-show="active==1">
        <data-upload @uploadDataList="uploadDataList" :initDataItems="initDirectResource"></data-upload>
      </div>
      <div class="stepBtn">
        <el-button @click="nextStep" v-show="active!=1">Next</el-button>
        <el-button @click="preStep" v-show="active!=0">Previous</el-button>
        <el-button @click="submitResource" v-show="active!=0">Submit</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import menuLeft from "./components/menuLeft";
import flowChart from "./components/flowChart";
import dataUpload from "./components/dataUpload";
import infoRight from "./components/infoRight";
import { post, get } from "../../axios";
import manageTools from "./components/manageTools";
import toolModal from "@/components/common/tools/toolModal";
export default {
  props: {},
  components: {
    menuLeft,
    flowChart,
    infoRight,
    dataUpload,
    manageTools,
    toolModal,
  },
  data() {
    return {
      projectId: this.$route.params.projectId,
      userInfo: this.$store.getters.userInfo,
      projectInfo: {},
      userRole: "Visitor",
      contentWidth: 0,
      contentHeight: 0,
      resourceCollectionModal: false,
      active: 0,
      resourceInfo: {
        pid: "",
        userId: "",
        dataItemList: [],
        toolItemList: [],
      },
      initResource: { toolItemList: [], toolItemList: [] },
      initDirectResource: [],
      update: false,
      stepInfo: {},
    };
  },

  methods: {
    initSize() {
      this.contentWidth = window.innerWidth - 450;
      this.contentHeight = window.innerHeight - 150;
    },
    async getUserInfo() {
      if (this.userInfo == {} || this.userInfo == undefined) {
        let data = await post("/GeoProblemSolving/user/state");
        userInfo.userState = true;
        this.userInfo = userInfo;
      }
    },

    async getProjectInfo() {
      if (
        JSON.stringify(this.projectInfo) == "{}" ||
        this.projectInfo == undefined
      ) {
        let { data } = await this.axios.get(
          `/GeoProblemSolving/project/inquiry?key=projectId&value=${this.projectId}`
        );
        if (data == "Offline") {
          parent.location.href = "/GeoProblemSolving/login";
        }
        if (data != "None" && data != "Fail") {
          this.projectInfo = data[0];
          this.identifyUserRole();
          // this.stepInfo.stepId = this.projectInfo.projectId;

          this.$store.commit("setProjectInfo", data[0]);
        } else {
          // console.log(data);
        }
      } else {
        this.identifyUserRole();
      }
    },

    async getResources() {
      let { data } = await this.axios.get(
        `/GeoProblemSolving/r/resource/get/${this.projectId}`
      );
      if (data.data == null) {
        this.initResource.toolItemList = [];
        this.initResource.dataItemList = [];
        this.initDirectResource = [];
        this.update = false;
      } else {
        this.initResource = data.data.data;
        this.initDirectResource = this.filterDirectData(
          this.initResource.dataItemList
        );
        this.update = true;
      }
    },

    identifyUserRole() {
      //Manager|Member|Visitor
      var thisUserId = this.userInfo.userId;
      var projectInfo = this.projectInfo;
      var members = projectInfo.members;
      var managerId = projectInfo.managerId;
      if (managerId == thisUserId) {
        this.userRole = "Manager";
      } else {
        for (var i = 0; i < members.length; i++) {
          var userId = members[i].userId;
          if (userId == thisUserId) {
            this.userRole = "Member";
          }
        }
      }
    },

    changeProjectInfo(newProjectInfo) {
      this.projectInfo = newProjectInfo;
      parent.vm.projectInfo = newProjectInfo;
    },

    doubleClick(val) {
      this.stepInfo = val;
      // console.log(val);
      if (val.type == "Resource collection") {
        this.resourceCollectionModal = true;
     
      } else if (val.type == "Model construction") {
        this.$router.push({
          name: "modelConstruction",
          params: {
            projectId: this.projectId,
            stepName: val.name,
            stepId: val.stepID,
            stepType: val.type,
          },
        });
      }
    },

    uploadDataList(list) {
      this.initResource.dataItemList = list;
      this.initDirectResource = this.filterDirectData(
        this.initResource.dataItemList
      );
    },
    nextStep() {
      this.active += 1;
    },
    preStep() {
      this.active -= 1;
    },

    selectTools(val) {
      this.initResource.toolItemList = val;
      // console.log(this.toolList);
    },

    async submitResource() {
      let resource = this.resourceInfo;
      resource["dataItemList"] = this.initResource.dataItemList;
      resource["toolItemList"] = this.initResource.toolItemList;
      if (this.update == false) {
        resource["pid"] = this.projectId;
        resource["userId"] = this.userInfo.userId;
        let data = await post(`/GeoProblemSolving/r/resource/save`, resource);
        if (data.msg == "成功") {
          this.$message({
            message: "You have collect your resource successfully",
            type: "success",
          });
        }
        this.active == 0;
      } else {
        let data = await post(
          `/GeoProblemSolving/r/resource/update/${this.projectId}`,
          resource
        );
        if (data.msg == "成功") {
          this.$message({
            message: "You have update your resource successfully",
            type: "success",
          });
          this.active == 0;
        }
      }
      this.resourceCollectionModal = false;
    },
    filterDirectData(list) {
      return list.filter((item) => {
        return item.isDirect == true;
      });
    },
  },
  async created() {
    this.getUserInfo();
    this.getProjectInfo();
    this.initSize();
    this.getResources();
  },
  mounted() {},
};
</script>
<style lang='scss' scoped>
.content {
  min-width: 1200px;
}
.lineStyle {
  float: left;
}
.cardRight {
  width: 350px;
}
.resourceDialog {
  >>> .el-dialog__body {
    padding: 0 10px;
  }
}
.stepBtn {
  position: absolute;
  bottom: 10px;
  right: 10px;
}
</style>