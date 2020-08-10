<style scoped>
.btnHoverGray:hover {
  background-color: #808695;
  color: white;
}
.echartBackground {
  width: 98%;
  margin: 20px auto;
  padding: 15px;
  background-color: #f8f8f9;
}
.footerTip {
  margin-top: 10px;
}
</style>
<template>
  <Row style="margin-top:10px">
    <div :style="{height:contentHeight+'px'}">
      <Row>
        <Row>
          <template>
            <div style="width: 80%; height: 25px;margin-left:1%">
              <span
                style="font-weight: bold; font-size:16px"
              >Procedure of iterative attempts for geo-problem solving</span>
              <template>
                <Button
                  v-if="(nodePositionBtn && procedureDrag)||(nodePositionBtn && !procedureDrag)"
                  type="warning"
                  @click="editPosition()"
                  size="small"
                  icon="md-git-commit"
                  title="Adjust the postion of nodes"
                  style="float:right;margin-left:10px;"
                >Move node</Button>

                <Button
                  v-if="removeBtn"
                  type="error"
                  size="small"
                  @click="removeStep()"
                  icon="md-remove"
                  title="Remove the activity"
                  style="float:right;margin-left:10px"
                >Remove</Button>

                <Button
                  v-if="activeBtn"
                  type="success"
                  size="small"
                  @click="activateStep()"
                  icon="md-bulb"
                  title="Active the activity"
                  style="float:right;margin-left:10px;"
                >Active</Button>

                <Button
                  v-if="addBtn"
                  type="info"
                  size="small"
                  @click="addNewStep()"
                  icon="md-add"
                  v-show="userRole == 'Manager'"
                  title="Add a new step"
                  style="float:right;margin-left:10px"
                >Add</Button>
              </template>
            </div>
          </template>
        </Row>
        <Row>
          <div class="echartBackground" :style="{height:contentHeight-70 +'px'}" id="steps"></div>
        </Row>

        <Row>
          <Col span="12">
            <h3
              class="footerTip"
            >Activities for solving geo-problem can be created and deleted here.</h3>
          </Col>
          <Col span="12">
            <h3
              class="footerTip"
            >Double click the node, and you can enter the corresponding workspace.</h3>
          </Col>

          <div style="width:100%;text-align:center;margin-top:10px">
            <!-- <Button
                class="btnHoverGray"
                @click="resetProjectTypeModalShow()"
                v-if="permissionIdentity('project_workspace_type_manage')"
            >Reset workspace type</Button>-->
            <Button class="btnHoverGray" @click="resetProjectTypeModalShow()">Reset workspace type</Button>
          </div>
        </Row>
      </Row>
    </div>
    <Modal
      v-model="slctActivateModal"
      title="Select an active activity"
      @on-ok="selectAActivity('activate')"
      ok-text="OK"
      cancel-text="Cancel"
    >
      <div
        style="font-size:14px;"
      >There are {{activeStepInfo.length}} active activities. Please choose the needed activities and activate the next activity.</div>
      <CheckboxGroup v-model="aActivitiesName" style="margin-top:10px">
        <Checkbox v-for="item in activeStepInfo" :key="item.index" :label="item.name"></Checkbox>
      </CheckboxGroup>
    </Modal>
    <Modal
      v-model="gotoworkModal"
      title="Select an active activity"
      @on-ok="selectAActivity('workspace')"
      ok-text="OK"
      cancel-text="Cancel"
    >
      <div
        style="font-size:14px;"
      >There are {{activeStepInfo.length}} active activities. Please choose an activity and go to the worksapce.</div>
      <RadioGroup v-model="workspaceName">
        <Radio
          style="margin-top:10px"
          v-for="item in activeStepInfo"
          :key="item.index"
          :label="item.name"
        ></Radio>
      </RadioGroup>
    </Modal>
    <Modal
      v-model="delModal"
      title="Delete this process"
      @on-ok="delStepGraph"
      ok-text="OK"
      cancel-text="Cancel"
    >
      <p>Do you really want to delete this step?</p>
    </Modal>

    <Modal v-model="resetProjectTypeNotice" title="Reset workspace type">
      <h2>Please confirm that all nodes have been deleted.</h2>
      <div slot="footer">
        <Button type="primary" @click="resetProjectTypeNotice=false">OK</Button>
      </div>
    </Modal>
    <Modal v-model="resetProjectTypeModel" title="Reset workspace type">
      <h2>Are you sure to reset the workspace type?</h2>
      <div slot="footer">
        <Button type="primary" @click="resetProjectType()">OK</Button>
      </div>
    </Modal>
    <Modal v-model="activityInfoModal" title="Information of the activity">
      <div>
        <label style="margin-left:20px">Activity name:</label>
        <Input v-model="showActivityInfo.name" style="width: 300px;margin-left:10px" readonly />
      </div>
      <div style="margin-top:20px">
        <label style="margin-left:20px">Activity type:</label>
        <Input v-model="showActivityInfo.type" style="width:300px;margin-left:17px" readonly />
      </div>
      <div slot="footer">
        <Button
          type="primary"
          @click="gotoThisWorkspace(showActivityInfo.type, showActivityInfo.stepID)"
        >Go to this workspace</Button>
      </div>
    </Modal>
    <Modal
      width="800px"
      v-model="createStepModal"
      title="Create a new step"
      :styles="{top: '20px'}"
    >
      <div style="height:380px;width:640px;margin-left:50px">
        <Form
          ref="createStepFormValidate"
          :model="createStepFormValidate"
          :rules="ruleValidate1"
          :label-width="120"
        >
          <FormItem label="Name:" prop="stepTitle">
            <Input v-model="createStepFormValidate.stepTitle" placeholder="Enter step name" />
          </FormItem>
          <FormItem label="Step type:" prop="stepType" style="margin-top:40px">
            <Select v-model="createStepFormValidate.stepType" placeholder="Select step type">
              <Option v-for="item in typeList" :key="item.index" :value="item">{{ item }}</Option>
            </Select>
          </FormItem>
          <FormItem label="Description:" prop="result" style="margin-top:50px">
            <Input
              v-model="createStepFormValidate.result"
              type="textarea"
              :rows="10"
              placeholder="Enter something..."
            />
          </FormItem>
        </Form>
      </div>
      <div slot="footer">
        <Button @click="createStepModal=false">Cancel</Button>
        <Button type="primary" @click="createStep('createStepFormValidate')">Submit</Button>
      </div>
    </Modal>
  </Row>
</template>
<script>
import echarts from "echarts";
export default {
  props: {
    userRole: {
      type: String,
    },
    projectInfo: {
      type: Object,
    },
  },

  watch: {
    projectInfo: {
      handler(val) {
        this.scopeInfo = val;
        this.scopeId = this.scopeInfo.projectId;
        this.getProcessSteps();
        this.showSteps();
      },
      deep: true,
    },
  },

  data() {
    return {
      scopeInfo: this.projectInfo,
      scrollOps: {
        bar: {
          background: "lightgrey",
        },
      },
      formValidate0: {
        stepTitle: "",
        stepType: "",
      },
      //button
      addBtn: false,
      activeBtn: false,
      removeBtn: false,
      workspaceBtn: false,
      nodePositionBtn: false,
      // 添加/编辑step
      createStepFormValidate: {
        stepTitle: "",
        stepType: "",
        result: "",
      },
      ruleValidate1: {
        stepTitle: [
          { required: true, message: "Please enter name...", trigger: "blur" },
        ],
        stepType: [
          { required: true, message: "Please select type...", trigger: "blur" },
        ],
      },
      contentHeight: "",
      processStructure: [],
      activeStepInfo: [],
      slctActiveStepInfo: [],
      aActivitiesName: [],
      workspaceName: "",
      typeList: [
        "Context definition & Protocal",
        "Data processing",
        "Data visualization",
        "Model construction",
        "Result analysis",
        "Result presentation",
      ],

      // 步骤逻辑图
      stepChart: null,
      // 选择的步骤
      selectedStep: [],
      delModal: false,
      // 创建步骤
      createStepModal: false,
      slctActivateModal: false,
      gotoworkModal: false,
      // 双击展示活动信息
      activityInfoModal: false,
      showActivityInfo: {},
      //资源继承
      existingResources: [],
      targetKeys: [],
      listStyle: { width: "280px", height: "375px" },
      // 工具
      personalTools: [],
      publicTools: [],
      personalToolsets: [],
      publicToolsets: [],
      selectStepTools: [],
      selectStepToolsets: [],
      // step 结构信息
      procedureDrag: true,
      nodeData: [],
      scopeId: "",
      resetProjectTypeNotice: false,
      resetProjectTypeModel: false,
      categroy: 0,
    };
  },
  created() {
    this.initSize();
    this.init();
    this.selfArrayContain();
  },

  mounted() {
    //vue生命周期
    this.btnFunction();
  },

  methods: {
    initSize() {
      this.contentHeight = window.innerHeight - 200;
    },
    //初始化函数，作用是控制侧边栏的高度，设置右边通知栏弹出时候的距顶高度以及延迟的时间
    init() {
      this.scopeId = this.scopeInfo.projectId;
      window.addEventListener("resize", this.initSize);
      window.addEventListener("resize", this.updateStepchart);
    },

    //自定义contain函数
    selfArrayContain() {
      Array.prototype.contains = function (obj) {
        let i = this.length;
        while (i--) {
          if (
            (this[i].tid != undefined && this[i].tid === obj.tid) ||
            (this[i].tsId != undefined && this[i].tsId === obj.tsId) ||
            (this[i].id != undefined && this[i].id === obj.id) ||
            (this[i].stepId != undefined && this[i].stepId === obj.stepId)
          ) {
            return true;
          }
        }
        return false;
      };
    },

    updateStepchart() {
      // 重新渲染
      this.selectedStep = [];
      this.stepChart.dispose();
      this.stepChart = null;
      this.showSteps();
      this.btnFunction();
    },

    getProcessSteps() {
      this.processStructure = [];
      if (
        this.scopeInfo.solvingProcess != undefined &&
        this.scopeInfo.solvingProcess != ""
      ) {
        try {
          this.processStructure = JSON.parse(this.scopeInfo.solvingProcess);
        } catch (err) {
          return;
        }
        // get active activities information
        this.activeStepInfo = [];
        for (let i = 0; i < this.processStructure.length; i++) {
          if (this.processStructure[i].activeStatus) {
            this.activeStepInfo.push(this.processStructure[i]);
          }
        }
      }
    },

    btnFunction() {
      if (
        this.processStructure.length <= 0 ||
        this.processStructure.length == undefined
      ) {
        this.addBtn = true;
        this.activeBtn = false;
        this.removeBtn = false;
        this.workspaceBtn = false;
        this.nodePositionBtn = false;
      } else {
        this.workspaceBtn = true;
        this.nodePositionBtn = true;
        if (this.selectedStep.length == 0) {
          this.addBtn = false;
          this.activeBtn = false;
          this.removeBtn = false;
        } else {
          this.addBtn = true;
          this.removeBtn = true;
          if (this.activeStepInfo.length == 0) {
            this.activeBtn = true;
          } else {
            let count = 0;
            for (let i = 0; i < this.selectedStep.length; i++) {
              for (let j = 0; j < this.activeStepInfo.length; j++) {
                if (
                  this.selectedStep[i].stepId == this.activeStepInfo[j].stepID
                ) {
                  count++;
                }
              }
            }
            if (
              count <= this.activeStepInfo.length &&
              this.selectedStep.length - count == 1
            ) {
              this.activeBtn = true;
            } else {
              this.activeBtn = false;
            }
          }
        }
      }
    },
    // 进入具体的step页面
    enterStep(type, stepId) {
      if (type == 0) {
        this.$router.push({
          name: "contextDefinition",
          params: { stepId: stepId },
        });
      } else if (type == 1) {
        this.$router.push({
          name: "r_dataProcessing",
          params: { stepId: stepId },
        });
      } else if (type == 2) {
        this.$router.push({
          name: "visualization",
          params: { stepId: stepId },
        });
      } else if (type == 3) {
        this.$router.push({
          name: "modelBuild",
          params: { stepId: stepId },
        });
      } else if (type == 4) {
        this.$router.push({
          name: "modelEvaluation",
          params: { stepId: stepId },
        });
      } else if (type == 5) {
        this.$router.push({
          name: "simulation",
          params: { stepId: stepId },
        });
      } else if (type == 6) {
        this.$router.push({
          name: "analysis",
          params: { stepId: stepId },
        });
      } else if (type == 7) {
        this.$router.push({
          name: "decisionMaking",
          params: { stepId: stepId },
        });
      }
    },

    showSteps() {
      this.selectedStep = [];
      let option = {
        animationDurationUpdate: 500,
        animationEasingUpdate: "quinticInOut",
        legend: {
          show: true,
          data: [
            {
              name: "Context definition & protocal",
              icon: "circle",
            },
            {
              name: "Data processing",
              icon: "circle",
            },
            {
              name: "Data visualization",
              icon: "circle",
            },
            {
              name: "Model construction",
              icon: "circle",
            },
            {
              name: "Result analysis",
              icon: "circle",
            },
            {
              name: "Result presentation",
              icon: "circle",
            },
          ],
        },
        series: [
          {
            id: "procedure",
            type: "graph",
            layout: "none",
            legendHoverLink: true,
            roam: this.procedureDrag,
            label: {
              normal: {
                show: true,
              },
            },
            edgeSymbol: ["circle", "arrow"],
            edgeSymbolSize: [4, 10],
            focusNodeAdjacency: true,
            data: [],
            categories: [
              {
                name: "Context definition & Protocal",
              },
              {
                name: "Data processing",
              },
              {
                name: "Data visualization",
              },
              {
                name: "Model construction",
              },
              {
                name: "Result analysis",
              },
              {
                name: "Result presentation",
              },
            ],
            links: [],
            lineStyle: {
              normal: {
                opacity: 1,
                width: 5,
                curveness: 0.1,
              },
            },
          },
        ],
      };

      this.nodeData = [];
      if (this.processStructure.length > 0) {
        for (let i = 0; i < this.processStructure.length; i++) {
          //get data
          let datum = {
            name: this.processStructure[i].name,
            index: this.processStructure[i].id,
            stepId: this.processStructure[i].stepID,
            x: this.processStructure[i].x,
            y: this.processStructure[i].y,
            category: this.processStructure[i].category,
            symbolSize: 45,
          };
          if (this.processStructure[i].activeStatus) {
            datum.symbolSize = 60;
            this.nodeData.push(datum);
            this.selectedStep.push({
              stepId: this.processStructure[i].stepID,
              id: this.processStructure[i].id,
              name: this.processStructure[i].name,
            });
          } else {
            this.nodeData.push(datum);
          }

          //get links
          for (let j = 0; j < this.processStructure[i].next.length; j++) {
            option.series[0].links.push({
              source: this.processStructure[i].name,
              target: this.processStructure[i].next[j].name,
            });
          }
        }
      }
      option.series[0].data = this.nodeData;
      if (this.stepChart == null) {
        this.stepChart = echarts.init(document.getElementById("steps"));
      } else {
        this.stepChart.off("click");
        this.stepChart.off("dblclick");
      }
      this.stepChart.setOption(option);

      let _this = this;
      // 单击选择步骤
      this.stepChart.on("click", function (params) {
        if (_this.procedureDrag) {
          if (option.series[0].data[params.data.index].symbolSize == 45) {
            option.series[0].data[params.data.index].symbolSize = 60;
            _this.formValidate0.stepTitle = params.data.name;
            _this.formValidate0.stepType = _this.getStepType(
              params.data.category
            );

            // record the selected step nodes
            _this.selectedStep.push({
              stepId: params.data.stepId,
              id: params.data.index,
              name: params.data.name,
            });
          } else if (
            option.series[0].data[params.data.index].symbolSize == 60
          ) {
            option.series[0].data[params.data.index].symbolSize = 45;

            // remove these not selected step nodes
            for (let i = 0; i < _this.selectedStep.length; i++) {
              if (_this.selectedStep[i].stepId == params.data.stepId) {
                _this.selectedStep.splice(i, 1);
                break;
              }
            }
          }
          _this.stepChart.setOption(option);
          _this.btnFunction();
        }
      });
      // 双击切换当前步骤
      this.stepChart.on("dblclick", function (params) {
        if (_this.procedureDrag) {
          // _this.enterStep(params.data.category, params.data.stepId);
          _this.activityInfoModal = true;
          let stepType = _this.getStepType(params.data.category);
          let activity = {
            stepID: params.data.stepId,
            name: params.data.name,
            type: stepType,
          };
          _this.showActivityInfo = activity;
        }
      });
    },

    editPosition() {
      this.procedureDrag = !this.procedureDrag;

      this.stepChart.setOption({
        animationDurationUpdate: this.procedureDrag ? 500 : 0,
        series: [
          {
            id: "procedure",
            roam: this.procedureDrag,
          },
        ],
      });

      // node的拖拽功能
      let _this = this;
      try {
        this.stepChart.setOption({
          // https://www.echartsjs.com/zh/tutorial.html#小例子：自己实现拖拽
          graphic: echarts.util.map(_this.nodeData, function (
            dataItem,
            dataIndex
          ) {
            let x = dataItem.x;
            let y = dataItem.y;
            let item = [x, y];
            let nodePosition = _this.stepChart.convertToPixel(
              { seriesIndex: 0 },
              item
            );

            return {
              type: "circle",
              shape: {
                r: 20,
              },
              position: nodePosition,
              invisible: true,
              draggable: !_this.procedureDrag,
              z: 100,
              ondrag: echarts.util.curry(function () {
                let position = _this.stepChart.convertFromPixel(
                  { seriesIndex: 0 },
                  this.position
                );
                _this.nodeData[dataIndex].x = position[0];
                _this.nodeData[dataIndex].y = position[1];
                _this.stepChart.setOption({
                  series: [
                    {
                      id: "procedure",
                      data: _this.nodeData,
                    },
                  ],
                });
              }, dataIndex),
            };
          }),
        });
      } catch (ex) {
        this.$Nothis.info({
          desc: "ERROR!",
        });
        tice;
      }
    },

    getStepType(category) {
      let type;
      if (category == 0) {
        type = "Context definition & Protocal";
      } else if (category == 1) {
        type = "Data processing";
      } else if (category == 2) {
        type = "Data visualization";
      } else if (category == 3) {
        type = "Model construction";
      } else if (category == 4) {
        type = "Result analysis";
      } else if (category == 5) {
        type = "Result presentation";
      }
      return type;
    },

    getStepCategroy(type) {
      let category;
      if (type == "Context definition & Protocal") {
        category = 0;
      } else if (type == "Data processing") {
        category = 1;
      } else if (type == "Data visualization") {
        category = 2;
      } else if (type == "Model construction") {
        category = 3;
      } else if (type == "Result analysis") {
        category = 4;
      } else if (type == "Result presentation") {
        category = 5;
      }
      this.category = category;
    },

    activateStep(activities) {
      // 多个激活的活动
      this.slctActiveStepInfo = [];
      if (this.activeStepInfo.length > 1) {
        if (activities == undefined || activities.length == 0) {
          this.slctActivateModal = true;
          return;
        } else if (activities.length > 0) {
          for (let i = 0; i < activities.length; i++) {
            for (let j = 0; j < this.activeStepInfo.length; j++) {
              if (activities[i] == this.activeStepInfo[j].name) {
                this.slctActiveStepInfo.push(this.activeStepInfo[j]);
              }
            }
          }
        }
      } else if (this.activeStepInfo.length == 1) {
        this.slctActiveStepInfo.push(this.activeStepInfo[0]);
      }

      // 新激活的活动
      let nextnode = {};
      let activityChangeList = [];
      for (let i = 0; i < this.selectedStep.length; i++) {
        for (let j = 0; j < this.processStructure.length; j++) {
          if (this.processStructure[j].stepID == this.selectedStep[i].stepId) {
            if (!this.processStructure[j].activeStatus) {
              this.processStructure[j].activeStatus = true;
              activityChangeList.push({
                stepId: this.processStructure[j].stepID,
                activeStatus: this.processStructure[j].activeStatus,
              });

              // 前后继承关系
              for (let k = 0; k < this.slctActiveStepInfo.length; k++) {
                let lastnode = {
                  name: this.slctActiveStepInfo[k].name,
                  id: this.slctActiveStepInfo[k].id,
                };
                if (!this.processStructure[j].last.contains[lastnode]) {
                  this.processStructure[j].last.push(lastnode);
                }
              }

              nextnode = {
                name: this.selectedStep[i].name,
                id: this.selectedStep[i].id,
              };
            }
            break;
          }
        }
      }

      // 使处于非激活状态的活动
      for (let i = 0; i < this.slctActiveStepInfo.length; i++) {
        for (let j = 0; j < this.processStructure.length; j++) {
          if (
            this.slctActiveStepInfo[i].stepID == this.processStructure[j].stepID
          ) {
            if (!this.processStructure[j].next.contains[nextnode]) {
              this.processStructure[j].next.push(nextnode);
            }

            this.processStructure[j].activeStatus = false;
            activityChangeList.push({
              stepId: this.processStructure[j].stepID,
              activeStatus: this.processStructure[j].activeStatus,
            });

            break;
          }
        }
      }
      // update scopeInfo/activeStepInfo/selectedStep/graph
      this.slctActiveStepInfo = [];
      this.scopeInfo.solvingProcess = JSON.stringify(this.processStructure);
      this.getProcessSteps();
      // 重新渲染
      this.updateStepchart();
      // 更新Step
      this.updateSteps();
      this.updateStepContent(activityChangeList);
    },

    addNewStep() {
      // 选择父节点检测
      if (
        this.processStructure.length != 0 &&
        (this.selectedStep.length == undefined || this.selectedStep.length == 0)
      ) {
        this.$Notice.info({
          desc: "Please select at least one node in advance!",
        });
        return;
      }

      // 获取可继承的资源
      //   this.getInheritResource();
      // 创建步骤模态框
      if (this.processStructure.length > 0) {
        this.createStepModal = true;
      }
    },

    getInheritResource() {
      this.existingResources = this.getMockData();
    },

    async getMockData() {
      let mockData = [];
      let selectedRes = [];

      // 前驱步骤的资源
      for (let i = 0; i < this.selectedStep.length; i++) {
        let selectedStepId = this.selectedStep[i].stepId;
        let selectedStepName = this.selectedStep[i].name;

        let data = await this.axios.get(
          `/GeoProblemSolving/folder/findByFileType?scopeId= ${selectedStepId}&type=all`
        );
        if (data !== "Fail") {
          selectedRes = data;
          for (let j = 0; j < selectedRes.length; j++) {
            mockData.push({
              key: mockData.length.toString(),
              name: selectedRes[j].name,
              type: selectedRes[j].type,
              resourceId: selectedRes[j].resourceId,
              source: selectedStepName,
            });
          }
        } else {
          selectedRes = [];
        }
      }
      return mockData;
    },

    getTargetKeys() {
      let mockData = [];
      if (this.existingResources.length > 0) {
        for (let i = 0; i < this.targetKeys.length; i++) {
          mockData.push({
            key: this.targetKeys[i],
            name: this.existingResources[this.targetKeys[i]].name,
            type: this.existingResources[this.targetKeys[i]].type,
            resourceId: this.existingResources[this.targetKeys[i]].resourceId,
            source: this.existingResources[this.targetKeys[i]].source,
          });
        }
      }
      return mockData;
    },

    handleChange(newTargetKeys) {
      this.targetKeys = newTargetKeys;
    },

    filterMethod(data, query) {
      return data.type.indexOf(query) > -1;
    },

    resourceRender(item) {
      // return item.type + " - " + item.name;
      return `<span title="${item.type} - ${item.source}">${item.name}</span>`;
    },

    createStep(name) {
      this.$refs[name].validate((valid) => {
        if (valid) {
          this.createStepModal = false;
          // 重复命名检测
          for (let i = 0; i < this.processStructure.length; i++) {
            if (
              this.createStepFormValidate.stepTitle ==
              this.processStructure[i].name
            ) {
              this.$Notice.info({
                desc: "The name of new step should not be different!",
              });
              return;
            }
          }
          this.createStepContent();
        } else {
        }
      });
    },

    async createStepContent() {
      // 新步骤的拓展工具、基本信息、资源（数据）
      //   this.filterShowListByType(this.createStepFormValidate.stepType);

      let Step = {};
      Step["name"] = this.createStepFormValidate.stepTitle;
      Step["type"] = this.createStepFormValidate.stepType;
      Step["description"] = this.createStepFormValidate.result;
      Step["projectId"] = this.scopeId;
      Step["creator"] = this.$store.getters.userId;
      Step["toolList"] = this.selectStepTools;
      Step["toolsetList"] = this.selectStepToolsets;
      Step["activeStatus"] = true;
      Step["content"] = {};

      let { data } = await this.axios.post(
        "/GeoProblemSolving/step/create",
        Step
      );

      if (data == "Offline") {
        parent.location.href = "/GeoProblemSolving/login";
      } else if (data === "Fail") {
        this.$Message.info("Fail");
      } else {
        this.createStepGraph(data);
        // 更新新Step的资源
        this.copyResource(data);
      }
    },

    createStepGraph(id) {
      if (this.processStructure.length == 0) {
        // 新步骤的类别
        this.getStepCategroy(this.createStepFormValidate.stepType);
        var nodeCategory = this.category;
        // create step node
        var newStepNode = {
          id: 0,
          stepID: id,
          name: this.createStepFormValidate.stepTitle,
          category: nodeCategory,
          last: [],
          next: [],
          x: 600,
          y: 200,
          level: 0,
          end: true,
          activeStatus: true,
        };

        let newNode;
        newNode.stepID = id;

        this.processStructure.push(newStepNode);

        // 更新scopeInfo
        this.scopeInfo.solvingProcess = JSON.stringify(this.processStructure);
        this.getProcessSteps();
        // 重新渲染
        this.updateStepchart();
        // 存储Step
        this.updateSteps();
      } else if (this.selectedStep.length > 0) {
        //  计算新增节点的属性信息
        let lastNode = [];
        let nodeLevel = 0;
        let nodeY = 0;

        let activityChangeList = [];

        for (let i = 0; i < this.selectedStep.length; i++) {
          lastNode.push({
            name: this.selectedStep[i].name,
            id: this.selectedStep[i].id,
          });
          if (
            this.processStructure[this.selectedStep[i].id].level >= nodeLevel
          ) {
            nodeLevel =
              this.processStructure[this.selectedStep[i].id].level + 1;
          }
          // modify original step node
          this.processStructure[this.selectedStep[i].id].next.push({
            name: this.createStepFormValidate.stepTitle,
            id: this.processStructure.length,
          });
          this.processStructure[this.selectedStep[i].id].end = false;
          // calculate y
          if (this.processStructure[i].last == []) {
            nodeY = 200;
          } else {
            let sumY = 0;
            for (let j = 0; j < this.selectedStep.length; j++) {
              sumY += this.processStructure[this.selectedStep[j].id].y;
            }
            nodeY = sumY / this.selectedStep.length;
          }
          //inactivate selected step
          this.processStructure[this.selectedStep[i].id].activeStatus = false;
          activityChangeList.push({
            stepId: this.processStructure[this.selectedStep[i].id].stepID,
            activeStatus: this.processStructure[this.selectedStep[i].id]
              .activeStatus,
          });
        }
        let isOverlap = false;
        // 统计每层的节点数
        let levelNum = [];
        for (let i = 0; i < this.processStructure.length; i++) {
          if (this.processStructure[i].level == nodeLevel) {
            levelNum.push(this.processStructure[i].id);
          }
        }
        // 节点重复检测
        for (let i = 0; i < levelNum.length; i++) {
          if (Math.abs(this.processStructure[levelNum[i]].y - nodeY) < 30) {
            isOverlap = true;
            break;
          }
        }
        // 新步骤的类别
        this.getStepCategroy(this.createStepFormValidate.stepType);
        var nodeCategory = this.category;
        // create step node
        var newStepNode = {
          id: this.processStructure.length,
          stepID: id,
          name: this.createStepFormValidate.stepTitle,
          category: nodeCategory,
          last: lastNode,
          next: [],
          x: 0,
          y: nodeY,
          level: nodeLevel,
          end: true,
          activeStatus: true,
        };
        this.processStructure.push(newStepNode);
        levelNum.push(newStepNode.id);
        // 如果重叠，修改y坐标
        if (isOverlap) {
          for (let i = 0; i < levelNum.length; i++) {
            this.processStructure[levelNum[i]].y =
              (400 / (levelNum.length + 1)) * (i + 1);
          }
          isOverlap = false;
        }
        // calculate x
        let maxLevel = 0;
        for (let i = 0; i < this.processStructure.length; i++) {
          if (this.processStructure[i].level > maxLevel) {
            maxLevel = this.processStructure[i].level;
          }
        }
        for (let i = 0; i < this.processStructure.length; i++) {
          this.processStructure[i].x =
            (800 / (maxLevel + 1)) * (this.processStructure[i].level + 1);
        }
        // 更新scopeInfo
        this.scopeInfo.solvingProcess = JSON.stringify(this.processStructure);
        this.getProcessSteps();
        // 重新渲染
        this.updateStepchart();
        // 更新Step
        this.updateSteps();
        this.updateStepContent(activityChangeList);
      } else {
        this.$Notice.info({
          desc: "Select at least one step as source(s), please!",
        });
      }
    },

    delStepGraph() {
      if (this.$store.getters.userInfo.userId == this.scopeInfo.managerId) {
        let currentIndex = this.selectedStep[0].id;

        if (this.processStructure[currentIndex].end) {
          let selectedStepId = this.processStructure[currentIndex].stepID;
          // 删除step节点
          if (currentIndex > 0) {
            // 处理被删除节点的前驱节点
            for (
              let i = 0;
              i < this.processStructure[currentIndex].last.length;
              i++
            ) {
              let lastIndex = this.processStructure[currentIndex].last[i].id;
              if (this.processStructure[lastIndex].next.length === 1) {
                this.processStructure[lastIndex].next = [];
                this.processStructure[lastIndex].end = true;
              } else if (this.processStructure[lastIndex].next.length > 1) {
                for (
                  let j = 0;
                  j < this.processStructure[lastIndex].next.length;
                  j++
                ) {
                  if (
                    this.processStructure[lastIndex].next[j].name ===
                    this.selectedStep[0].name
                  ) {
                    this.processStructure[lastIndex].next.splice(j, 1);
                  }
                }
              }
            }
            // 删除节点
            this.processStructure.splice(currentIndex, 1);

            for (let i = currentIndex; i < this.processStructure.length; i++) {
              let originalID = this.processStructure[i].id;
              // 当前节点id
              if (originalID !== i) {
                this.processStructure[i].id = i;
              }

              let originalName = this.processStructure[i].name;
              // 前驱节点的 next id
              for (let j = 0; j < this.processStructure[i].last.length; j++) {
                let lastIndex = this.processStructure[i].last[j].id;
                let lastnode = this.processStructure[lastIndex];
                for (let k = 0; k < lastnode.next.length; k++) {
                  if (lastnode.next[k].name === originalName) {
                    this.processStructure[lastIndex].next[k].id = i;
                  }
                }
              }

              // 后继节点的 last id
              for (let j = 0; j < this.processStructure[i].next.length; j++) {
                let nextIndex = this.processStructure[i].next[j].id - 1;
                let nextnode = this.processStructure[nextIndex];
                for (let k = 0; k < nextnode.last.length; k++) {
                  if (nextnode.last[k].name === originalName) {
                    this.processStructure[nextIndex].last[k].id = i;
                  }
                }
              }
            }
          } else if (currentIndex === 0) {
            // 删除节点
            this.processStructure.splice(currentIndex, 1);
          }

          // 更新scopeInfo
          this.scopeInfo.solvingProcess = JSON.stringify(this.processStructure);
          this.getProcessSteps();
          // 重新渲染
          this.updateStepchart();
          this.updateSteps();
          //删除数据库
          this.delStepContent(selectedStepId);
        } else {
          this.$Notice.info({
            desc:
              "The selected step " +
              this.selectedStep[0].name +
              " can not be removed. Because it has the next activities.",
          });
        }
      }
    },

    copyResource(stepId) {
      let selectResource = [];
      selectResource = this.getTargetKeys();
      if (selectResource.length > 0) {
        // 继承资源的数据id的集合
        let addFileList = [];
        for (let i = 0; i < selectResource.length; i++) {
          addFileList.push(selectResource[i].resourceId);
        }
        let addFileListStr = addFileList.toString();

        this.axios
          .get(
            "/GeoProblemSolving/folder/shareToFolder" +
              "?addFileList=" +
              addFileListStr +
              "&folderId=" +
              stepId
          )
          .then((res) => {
            this.shareModal = false;
            if (res.data == "Offline") {
              this.$store.commit("userLogout");
              this.$router.push({ name: "Login" });
            } else if (res.data == "Fail") {
              this.$Message.error(
                "Failed to get resources from previous activities."
              );
            }
          })
          .catch((err) => {
            // console.log(err.data);
          });
      }
    },

    filterShowListByType(stepType) {
      let toolList = this.filterDuplicateTools();
      this.selectStepTools = this.getFilterToolResult(toolList, stepType);

      let toolsetList = this.filterDuplicateToolsets();
      this.selectStepToolsets = this.getFilterToolsetResult(
        toolsetList,
        stepType
      );
    },

    resetProjectType() {
      let obj = new URLSearchParams();
      obj.append("projectId", this.scopeId);
      obj.append("type", "");
      obj.append("stepId", "");
      let data = this.axios.post("/GeoProblemSolving/project/update", obj);
      this.resetProjectTypeModel = false;
      if (data == "Offline") {
        parent.location.href = "/GeoProblemSolving/login";
      } else if (data != "Fail") {
        this.$store.commit("setProjectInfo", data);
        this.$emit("changeProjectInfo", data);
      } else {
        this.$Message.error("Set type failed.");
      }
    },

    async updateSteps() {
      let obj = new URLSearchParams();
      obj.append("solvingProcess", JSON.stringify(this.processStructure));
      var updateurl = "";

      obj.append("projectId", this.scopeId);
      console.log(obj.get("projectId"));
      let { data } = await this.axios.post(
        "/GeoProblemSolving/project/update",
        obj
      );
      if (data == "Offline") {
        parent.location.href = "/GeoProblemSolving/login";
      } else if (data != "Fail") {
        this.$store.commit("setProjectInfo", data);
        // this.parent.vm.projectInfo = data;
        // 如果是项目下的步骤，需要更新sessionStorage
        sessionStorage.setItem("projectInfo", JSON.stringify(data));
        this.$Notice.info({
          desc: "Project update successfully!",
        });
      } else {
        this.$Message.error("Update subproject failed.");
      }
    },

    updateStepContent(activityChangeList) {
      for (var i = 0; i < activityChangeList.length; i++) {
        let obj = new URLSearchParams();
        obj.append("activeStatus", activityChangeList[i].activeStatus);
        obj.append("stepId", activityChangeList[i].stepId);
        this.axios
          .post("/GeoProblemSolving/step/update", obj)
          .then((res) => {
            if (res.data == "Offline") {
              this.$store.commit("userLogout");
              this.$router.push({
                name: "Login",
              });
            } else if (res.data != "Fail") {
              // this.$Notice.info({
              //   desc: "Update successfully!"
              // });
            } else {
              // this.$Message.error("Update step failed.");
            }
          })
          .catch((err) => {
            console.log(err.data);
          });
      }
    },

    gotoThisWorkspace(type, stepID) {
      this.getStepCategroy(type);
      this.enterStep(this.category, stepID);
    },
  },

  beforeRouteLeave(to, from, next) {
    next();
  },
  beforeDestroy: function () {
    window.removeEventListener("resize", this.initSize);
  },
};
</script>
