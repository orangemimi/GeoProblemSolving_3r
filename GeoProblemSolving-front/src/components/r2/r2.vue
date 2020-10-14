<template>
  <div>
    <div class="content">
      <menu-left class="lineStyle"></menu-left>
      <div class="lineStyle" :style="{ width: contentWidth + 'px' }">
        <flow-chart
          :projectInfo="projectInfo"
          :userRole="userRole"
          @dblclick="doubleClick"
        ></flow-chart>
      </div>
      <div
        class="lineStyle cardRight"
        :style="{ height: contentHeight + 'px' }"
      >
        <info-right :projectInfo="projectInfo"></info-right>
      </div>
      <div style="clear: both"></div>
    </div>
    <el-dialog
      :visible.sync="resourceCollectionModal"
      :close-on-click-modal="false"
      title="Please upload the file"
      class="resourceDialog"
    >
      <el-steps
        :active="active"
        finish-status="success"
        simple
        style="margin-top: 20px"
      >
        <el-step title="Select Model"></el-step>
        <el-step title="Upload Data"></el-step>
      </el-steps>

      <div v-show="active == 0" style="height: 600px">
        <data-upload :stepInformation="stepInfo"></data-upload>
      </div>
      <div v-show="active == 1">
        <manage-tools
          @selectTools="selectTools"
          footer-hide
          :initTools="initResource.toolItemList"
        ></manage-tools>
      </div>
      <div class="stepBtn">
        <el-button @click="nextStep" v-show="active != 1">Next</el-button>
        <el-button @click="preStep" v-show="active != 0">Previous</el-button>
        <el-button @click="submitResource" v-show="active != 0"
          >Submit</el-button
        >
      </div>
    </el-dialog>

    <el-dialog
      :visible.sync="contextModal"
      :close-on-click-modal="false"
      title="Context definition & Protocal"
      class
    >
      <context-form-modal
        :projectInfo="{ projectId: projectId, userId: userInfo.userId }"
        @closeForm="closeContextForm"
      ></context-form-modal>
    </el-dialog>

    <el-dialog
      :visible.sync="resourceInheritModal"
      :close-on-click-modal="false"
      title="Get resources from the previous step"
      class="inheritResourcesDialog"
    >
      <div>
        <div>
          <div class="modalTitle2">Data Uploaded</div>
          <div style="line-height: 45px; margin-left: 10px">
            <el-checkbox
              v-model="checkDirectResourceAll"
              @change="handleCheckDirectResourceAll"
              >Select All</el-checkbox
            >
          </div>
          <div>
            <el-table
              :data="filterCheckedToDirect"
              border
              max-height="240"
              style="width: 100%"
            >
              <el-table-column prop="name" label="Instance name" width="200">
              </el-table-column>
              <el-table-column label="Selected for next" width="150">
                <template slot-scope="scope">
                  <el-checkbox
                    :checked="scope.row.checkedToNextStep"
                    @change="changeDirectChecked($event, scope.$index)"
                  ></el-checkbox>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
        <div>
          <div class="modalTitle2">Data from the last step</div>
          <div>
            <el-table
              :data="filterCheckedeToInherit"
              border
              max-height="240"
              style="width: 100%"
            >
              <el-table-column
                prop="stepBind.stepName"
                label="Previous Step"
                width="200"
              >
              </el-table-column>

              <el-table-column prop="name" label="Instance name" width="200">
              </el-table-column>
              <el-table-column label="Selected for next" width="150">
                <template slot-scope="scope">
                  <el-checkbox
                    :checked="scope.row.checkedToNextStep"
                    @change="changeChecked($event, scope.$index)"
                  ></el-checkbox>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="resourceInheritModal = false">Cancel</el-button>
        <el-button type="primary" @click="enterStep">Enter the Step</el-button>
      </span>
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
import contextFormModal from "./components/contextFormModal";
export default {
  props: {},
  components: {
    menuLeft,
    flowChart,
    infoRight,
    dataUpload,
    manageTools,
    toolModal,
    contextFormModal,
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

      initResource: { dataItemList: [], toolItemList: [] },
      // initDirectResource: [],
      // update: false,
      stepInfo: {},
      contextModal: false,
      resourceInheritModal: false,
      currentStep: [],
      lastSteps: [],
      stepProcesses: [], //all step in the flowchart
      // checkToNextStep: [],
      checkDirectResourceAll: false,
      checkInheritResourceAll: false,
      inheritResourceList: [],
      checkedToNextStepList: [],
      multipleSelection: [],
      // isIndeterminate:false
    };
  },
  computed: {
    initDirectResource: {
      get() {
        if (this.initResource.dataItemList != undefined) {
          let list = this.initResource.dataItemList.filter((item) => {
            return item.isDirect == true;
          });
          return list;
        }
      },
      set(val) {},
    },

    filterCheckedeToInherit: {
      get() {
        let dataItemList = this.initResource.dataItemList;
        if (dataItemList != undefined) {
          let last = this.lastSteps;
          dataItemList.forEach((item) => {
            item.checkedToNextStep = false;
            if (item.stepInherit != null) {
              if (item.stepInherit.some((e) => e == this.stepInfo.stepId)) {
                item.checkedToNextStep = true;
              } else {
                item.checkedToNextStep = false;
              }
            } else {
              item.checkedToNextStep = false;
            }
          });
          return dataItemList.filter((item) => {
            return (
              item.stepBind != null &&
              last.some((e) => e.stepID == item.stepBind.stepId)
            );
          });
        }
      },
      set(val) {},
    },

    filterCheckedToDirect() {
      if (this.initDirectResource != undefined) {
        let dataItemList = this.initDirectResource;
        dataItemList.forEach((item) => {
          item.checkedToNextStep = false;
          if (item.stepInherit != null) {
            if (item.stepInherit.some((e) => e == this.stepInfo.stepId)) {
              item.checkedToNextStep = true;
            } else {
              item.checkedToNextStep = false;
            }
          } else {
            item.checkedToNextStep = false;
          }
        });
        return dataItemList;
      }
    },
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

    async init() {
      await this.getProjectInfo();
      await this.getResources();
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
      let dataItem = await get(
        `/GeoProblemSolving/r/dataItems/${this.projectId}`
      );
      let modelItem = await get(
        `/GeoProblemSolving/r/toolItems/${this.projectId}`
      );

      this.initResource.toolItemList = modelItem;
      this.initResource.dataItemList = dataItem;
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

    doubleClick(val, processes) {
      console.log(val);
      this.stepInfo = val;
      this.stepProcesses = processes;
      if (val.type == "resourceCollection") {
        this.resourceCollectionModal = true;
      } else if (val.type == "contextDefinition") {
        this.contextModal = true;
      } else if (val.type == "conclusion") {
        this.$router.push({
          name: "resultInfo",
          params: {
            projectId: this.projectId,
            stepName: val.name,
            stepId: val.stepId,
            stepType: val.type,
          },
        });
      } else if (val.type == "simulationExecution") {
        let currentStep = processes.filter(
          (process) => process.stepID == val.stepId
        );
        this.currentStep = currentStep;
        let last = currentStep[0].last;
        this.lastSteps = processes.filter((process) => {
          return last.some((e) => e.name == process.name);
        });
        // console.log(a);

        last.forEach((step) => {
          if (step.id == 2 || step.id == 3 || step.id == 4) {
            this.resourceInheritModal = true;
          } else {
            this.$router.push({
              name: "simulationExecution",
              params: {
                projectId: this.projectId,
                stepName: val.name,
                stepId: val.stepId,
                stepType: val.type,
                isInherit: false,
              },
            });
          }
        });
      }
    },
    async enterStep() {
      this.initResource.dataItemList.forEach((data) => {
        if (data.hasOwnProperty("checkedToNextStep")) {
          if (data.checkedToNextStep == true) {
            //如果被选中了inherit
            if (data.stepInherit != null) {
              //如果stepInherit已经有了内容，且其中的stepList没有一个是当前step
              if (data.stepInherit.every((e) => e != this.stepInfo.stepId)) {
                data.stepInherit.push(this.stepInfo.stepId);
              }
              //如果stepInherit已有当前step,则不操作
            } else {
              //如果stepInherit=null，直接插入
              data.stepInherit = [];
              data.stepInherit.push(this.stepInfo.stepId);
            }
          } else {
            //如果没被选中
            if (data.stepInherit != null) {
              if (data.stepInherit.some((e) => e == this.stepInfo.stepId)) {
                data.stepInherit.splice(
                  data.stepInherit.findIndex(
                    (id) => id === this.stepInfo.stepId
                  ),
                  1
                );
              }
            }
          }
        }
      });
      let resource = {};
      resource["dataItemList"] = this.initResource.dataItemList;
      resource["toolItemList"] = this.initResource.toolItemList;
      let data = await post(
        `/GeoProblemSolving/r/resource/update/${this.projectId}`,
        resource
      );

      this.$router.push({
        name: this.stepInfo.type,
        params: {
          projectId: this.projectId,
          stepName: this.stepInfo.name,
          stepId: this.stepInfo.stepId,
          stepType: this.stepInfo.type,
          isInherit: true,
          // inheritResources: this.initResource.dataItemList,
        },
      });
    },
    uploadDataList(data) {
      // data.stepBind = {
      //   stepId: this.stepInfo.stepId,
      //   stepName: this.stepInfo.name,
      // };
      this.initResource.dataItemList.push(data);
      console.log(this.initResource.dataItemList);
    },
    nextStep() {
      if (this.active++ > 1) this.active = 1;
    },
    preStep() {
      if (this.active-- < 0) this.active = 0;
    },

    selectTools(val) {
      this.initResource.toolItemList = val;
      // console.log(this.toolList);
    },

    async submitModelItemList() {
      let data = await post(
        `/GeoProblemSolving/r/toolItems`,
        this.initResource.dataItemList
      );
    },

    async submitResource() {
      // await this.submitDataItemList();
      await this.submitModelItemList();

      let resource = {};
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
          `/GeoProblemSolving/r/dataItems/update/${this.projectId}`,
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
    // filterDirectData(list) {
    //   return list.filter((item) => {
    //     return item.isDirect == true;
    //   });
    // },

    closeContextForm(val) {
      this.contextModal = false;
    },

    handleCheckDirectResourceAll(val) {
      this.checkDirectResourceAll = val;
      this.initDirectResource.forEach((item) => {
        item.checkedToNextStep = this.checkDirectResourceAll; //只改变数据的状态
      });
    },
    handleCheckInheritResourceAll(val) {
      this.checkInheritResourceAll = val;
      this.inheritResourceList.forEach((item) => {
        item.checkedToNextStep = this.checkInheritResourceAll; //只改变数据的状态
      });
    },
    changeChecked(val, index) {
      this.$set(this.filterCheckedeToInherit[index], "checkedToNextStep", val);
      this.$forceUpdate();
    },
    changeDirectChecked(val, index) {
      console.log(val, index);
      this.$set(this.filterCheckedToDirect[index], "checkedToNextStep", val);
      this.$forceUpdate();
    },

    // objectSpanMethod({ row, column, rowIndex, columnIndex }) {
    //   if (columnIndex === 0) {
    //     if (rowIndex % 2 === 0) {
    //       return {
    //         rowspan: 2,
    //         colspan: 1,
    //       };
    //     } else {
    //       return {
    //         rowspan: 0,
    //         colspan: 0,
    //       };
    //     }
    //   }
    // },
  },

  async created() {
    this.getUserInfo();
    this.initSize();
  },
  mounted() {
    this.init();
  },
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
.modalTitle2 {
  font-weight: 600;
  font-size: 16px;
  margin: 10px 0;
  float: left;
}
.inheritResourcesDialog >>> .el-dialog__body {
  padding: 0 20px;
}
.modalTitle3 {
  font-weight: 600;
  font-size: 15px;
}
</style>