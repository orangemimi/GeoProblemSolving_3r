<template>
  <div class="main" :style="{ height: contentHeight + 'px' }">
    <el-row :gutter="20" :style="{ height: contentHeight + 'px' }">
      <el-col :span="4">
        <el-card class="process_data" shadow="never">
          <div class="process_data_title">
            <el-select v-model="typeSelected" style="width: 160px">
              <el-option
                v-for="item in typeOptions"
                :key="item.index"
                :value="item"
                >{{ item }}</el-option
              >
            </el-select>
            <div class="toolList" :style="{ height: contentHeight + 'px' }">
              <vue-scroll :ops="ops">
                <el-row>Tools selected</el-row>
                <el-row class="tool_card">
                  <el-col
                    :span="12"
                    v-for="tool in filterSelectedTools"
                    :key="tool.index"
                  >
                    <div style="margin: 5px" @click="useTool(tool)">
                      <tool-card
                        :toolFrom="tool"
                        :isOpenTool="isOpenTool"
                      ></tool-card>
                    </div>
                  </el-col>
                </el-row>
              </vue-scroll>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-tabs v-model="activeTabs" type="border-card">
          <el-tab-pane label="Model Item" name="modelItem">
            <el-row>
              <el-card class="process_data" shadow="never">
                <div :style="{ height: contentHeight - 20 + 'px' }">
                  <vue-scroll :ops="ops">
                    <model-item-info
                      :pageParamsFrom="pageParams"
                      :currentModelInfo="currentModelInfo"
                      :selectedResources="selectedResources"
                      :instanceFolk="instanceFolk"
                      @modelInstance="saveModelInstance"
                    ></model-item-info>
                  </vue-scroll>
                </div>
              </el-card>
            </el-row>
          </el-tab-pane>
          <el-tab-pane label="Concept Map" name="conceptMap">
            <el-row>
              <el-card>
                <div
                  class="mxGraph"
                  :style="{ height: contentHeight - 20 + 'px' }"
                >
                  <mx-graph :sendXml="sendXml"></mx-graph>
                </div>
              </el-card>
            </el-row>
          </el-tab-pane>
        </el-tabs>
      </el-col>
      <el-col :span="4">
        <el-row>
          <el-card class="process_data" shadow="never">
            <div class="process_data_title">Data Resources</div>
            <div>
              <div
                v-for="(data, index) in selectedResources.dataItemList"
                :key="index"
              >
                <div class="data_info">
                  <div class="data_info_name">{{ data.name }}</div>
                  <div class="data_info_hover">
                    <i
                      class="el-icon-download"
                      @click="downloadDataResource(data)"
                    ></i>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </el-row>
        <el-row>
          <el-card shadow="never">
            <div class="process_data_title">Model Instance</div>
            <div
              v-for="(instance, index) in filterModelInstance"
              :key="index + 'instance'"
            >
              <el-card
                class="box-card"
                :class="instance.status == 0 ? 'calculating' : 'finishing'"
              >
                <div class="instance_head">
                  <div class="instance_name">{{ instance.name }}</div>
                  <i
                    class="el-icon-close"
                    @click="deleteInstance(instance.id)"
                  ></i>
                </div>
                <div v-if="instance.status == 0" class="calculating_icon">
                  <i
                    class="el-icon-loading calculating_icon"
                    style="
                      float: left;
                      width: 20px;
                      size: 20px;
                      line-height: 20px;
                      font-weight: 600;
                    "
                  ></i>
                  <div style="float: left; width: 10px">calculating</div>
                </div>
                <div v-else>
                  <el-checkbox
                    v-model="instance.checkedForMap"
                    @change="$forceUpdate()"
                  ></el-checkbox>
                  <i
                    class="el-icon-connection"
                    @click="folkInstance(instance)"
                  ></i>
                </div>
              </el-card>

              <el-card class="box-card" v-if="instance.status == -1">
                <div class="instance_head">
                  <div class="instance_name">{{ instance.name }}</div>
                  <i
                    class="el-icon-close"
                    @click="deleteInstance(instance.id)"
                  ></i>
                </div>
                <div class="error_icon">
                  <i
                    class="el-icon-warning"
                    style="
                      float: left;
                      width: 20px;
                      size: 20px;
                      line-height: 20px;
                      font-weight: 600;
                    "
                  ></i>
                  <div style="float: left; width: 10px">ERROR</div>
                </div>
              </el-card>
            </div>
            <el-checkbox v-model="checkAll" @change="handleCheckAllChange"
              >Select All</el-checkbox
            >
            <el-button @click="mapCreate">Create Map</el-button>
          </el-card>
        </el-row>
      </el-col>
    </el-row>

    <div>
      <el-dialog
        title="Warning"
        :visible.sync="deleteInstanceDialog"
        width="30%"
      >
        <span
          >If you delete this instance, linked resources would also be
          deleted</span
        >
        <span slot="footer" class="dialog-footer">
          <el-button @click="deleteInstanceDialog = false">Cancle</el-button>
          <el-button type="primary" @click="handleDeleteInstanceDialogClose"
            >Confirm</el-button
          >
        </span>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import toolsSelected from "./components/toolsSelected";
import allTools from "./allTools";
import toolCard from "./components/toolCard";
import { get, del, post, patch } from "../../axios";
import modelItemInfo from "./components/modelItemInfo";
// import conceptMap from "./conceptMap";
import mxGraph from "@/components/utils/mxGraph/mxGraph";
export default {
  props: {},
  components: {
    toolsSelected,
    allTools,
    toolCard,
    modelItemInfo,
    // conceptMap,
    mxGraph,
  },
  // watch: {
  //   inheritResources: {
  //     handler(val) {
  //       console.log(val);
  //     },
  //     deep: true,
  //   },
  // },
  computed: {
    pageParams() {
      let data = {
        pid: this.projectId,
        userId: this.userInfo.userId,
        userName: this.userInfo.userName,
        stepName: this.$route.params.stepName,
        stepId: this.$route.params.stepId,
        stepType: this.$route.params.stepType,
      };
      return data;
    },
    filterSelectedTools() {
      let tools = this.selectedResources.toolItemList;
      let type = this.typeSelected;
      if (type == "All") {
        return tools;
      } else {
      }
    },
    filterModelInstance() {
      let data = this.modelInstances;
      return data.filter((item) => (item.statesJson = JSON.parse(item.states)));
    },
    //create Map instance

    filterDirectDataResource() {
      return this.selectedResources.dataItemList.filter(
        (data) => data.isDirect == true
      );
    },
    filterIndirectDataResource() {
      return this.selectedResources.dataItemList.filter(
        (data) => data.isDirect == false
      );
    },
    filterInheritResources() {
      let list = this.selectedResources.dataItemList.filter((data) => {
        return (
          data.hasOwnProperty("stepInherit") &&
          data.stepInherit.some((e) => e == this.stepInfo.stepId)
        );
      });
      console.log(list);
      return list;
    },
  },
  data() {
    return {
      userInfo: this.$store.getters.userInfo,
      projectId: this.$route.params.projectId,
      stepInfo: {
        stepName: this.$route.params.stepName,
        stepId: this.$route.params.stepId,
        stepType: this.$route.params.stepType,
      },
      // inheritResources: this.$route.params.inheritResources,
      contentWidth: 0,
      contentHeight: 0,
      selectedResources: {},
      ops: {
        bar: {
          background: "#808695",
        },
      },
      isOpenTool: false,
      typeSelected: "All",
      typeOptions: [
        "All",
        "General step",
        "Context definition & resource collection",
        "Data processing",
        "Data visualization",
        "Geographic model construction",
        "Model effectiveness evaluation",
        "Geographical simulation",
        "Quantitative and qualitative analysis",
        "Decision-making for management",
        "Others",
      ],
      panelList: [],
      jupyterModal: false,
      currentModelInfo: {
        toolUrl: "",
        toolId: "",
      },
      modelUrl: "",
      modelDoi: "16e31602-bd05-4da4-bd01-cb7582c21ae8",
      // pageParams: {},
      modelInstances: [],
      instanceFolk: {},
      checkedInstances: [],
      dataNodes: [],
      dataNodesIntermedia: [],
      mxNodes: [],
      sendXml: "",
      xml: "", //save to mongodb
      updateXml: false,
      flowChartSent: {},

      //getOutputs
      record: {},
      timer: {},
      stateList: [],
      checkAll: false,
      checkedForMap: [],
      nextStartIndex: 0,
      deleteInstanceDialog: false,
      deleteInstanceId: "",
      filterCreateMapInstances: [],

      activeTabs: "modelItem",
    };
  },
  methods: {
    initSize() {
      //   this.contentWidth = window.innerWidth - 450;
      this.contentHeight = window.innerHeight - 260;
    },
    async init() {
      // this.getPageParams();
      await this.getSelectedResources();
      await this.getModelInstances();
      await this.getMap();
    },

    // getPageParams() {

    // },
    async getSelectedResources() {
      let { data } = await get(
        `/GeoProblemSolving/r/resource/get/${this.projectId}`
      );
      this.$set(this, "selectedResources", data);
      if (this.$route.params.isInherit) {
        this.selectedResources.dataItemList = data.dataItemList.filter(
          (item) =>
            item.stepInherit != null &&
            item.stepInherit.some((id) => id == this.stepInfo.stepId)
        );
      }
      console.log(this.selectedResources.dataItemList);
    },

    //get modelinstance
    async getModelInstances() {
      console.log(this.stepInfo);
      let { data } = await get(
        `/GeoProblemSolving/r/modelInstance/get/${this.stepInfo.stepId}`
      );
      this.modelInstances = data;
      this.modelInstances.forEach((item) => {
        if (item.status != 2) {
          this.getOutputs(item);
        }
      });
    },

    async getOutputs(instance) {
      this.stateList = JSON.parse(instance.states);
      // console.log(instance, this.stateList);?
      let refreshForm = {};
      refreshForm["ip"] = instance.ip;
      refreshForm["port"] = instance.port;
      refreshForm["tid"] = instance.tid;
      //获得结果
      this.record = {};
      this.timer = setInterval(async () => {
        if (this.record.status == 2) {
          clearInterval(this.timer);
          await this.getStateEventOut(this.record, instance);
          return;
        } else {
          let { data } = await post(
            "/GeoProblemSolving/modelTask/getRecord",
            instance
          );
          this.record = data;
        }
      }, 2000);
    },

    async getStateEventOut(record, instance) {
      let outList = this.stateList;
      let outputUrl = record.outputs;
      outList.forEach((state, index) => {
        state.Event.forEach((event, eventIndex) => {
          outputUrl.forEach((el) => {
            if (el.statename == state.name && el.event == event.name) {
              this.$set(this.stateList[index].Event[eventIndex], "url", el.url);
            }
          });
        });
      });
      // console.log(this.stateList);
      let stepResource = {
        states: JSON.stringify(this.stateList),
        status: record.status, //2 finish
      };
      let { data } = await post(
        `/GeoProblemSolving/r/modelInstance/update/${instance.id}`,
        stepResource
      );
      this.saveModelInstance(data);
    },

    useTool(toolInfo) {
      if (toolInfo.toolName == "Jupyter notebook") {
        this.jupyterModal = true;
        return;
      }
      this.activeTabs = "modelItem";
      this.currentModelInfo.toolId = toolInfo.tid;
      this.currentModelInfo.toolUrl = toolInfo.toolUrl;
    },

    //findindexof--改进
    saveModelInstance(instance) {
      this.modelInstances.forEach((item, index) => {
        if (item.id == instance.id) {
          this.modelInstances.splice(index, 1);
        }
      });
      this.modelInstances.push(instance);
    },

    folkInstance(instance) {
      this.instanceFolk = instance;
    },

    async handleDeleteInstanceDialogClose() {
      //删除instance
      //view更新
      this.modelInstances.forEach((item, index) => {
        if (item.id == this.deleteInstanceId) {
          this.modelInstances.splice(index, 1);
        }
      });
      await del(
        `/GeoProblemSolving/r/modelInstance/delete/${this.deleteInstanceId}`
      );

      //删除与之相关的dataitem
      let resource = this.selectedResources;
      let dataItem = resource.dataItemList;
      dataItem.forEach((item, itemIndex) => {
        if (
          item.hasOwnProperty("toModelInstanceList") &&
          item.toModelInstanceList != null
        ) {
          item.toModelInstanceList.forEach((id, index) => {
            if (id == this.deleteInstanceId) {
              item.toModelInstanceList.splice(index, 1);
            }
          });
        }
        if (item.hasOwnProperty("fromModelInstance")) {
          if (item.fromModelInstance == this.deleteInstanceId) {
            dataItem.splice(itemIndex, 1);
          }
        }
      });
      // console.log(resource.dataItemList);

      let data = await post(
        `/GeoProblemSolving/r/resource/update/${this.projectId}`,
        resource
      );
      this.deleteInstanceDialog = false;
    },

    deleteInstance(instanceId) {
      this.deleteInstanceId = instanceId;
      this.deleteInstanceDialog = true;
    },

    handleCheckAllChange(val) {
      this.checkAll = val;
      this.filterModelInstance.forEach((item) => {
        item.checkedForMap = this.checkAll; //只改变数据的状态
      });
    },

    getModelDoi(url) {
      let arr = url.split("/");
      this.modelDoi = arr[arr.length - 1];
    },

    downloadDataResource(data) {
      window.open(data.url);
    },

    async mapCreate() {
      this.dataNodes = [];
      this.dataNodesIntermedia = [];

      this.filterCreateMapInstances = this.filterModelInstance.filter(
        (instance) => instance.checkedForMap == true
      );

      if (this.filterCreateMapInstances == "") {
        this.$message({
          message: "There is no selected instance",
          type: "error",
        });
      } else {
        this.checkedInstances = JSON.parse(
          JSON.stringify(this.filterCreateMapInstances)
        );
        let checkedInstances = this.checkedInstances;

        let directDataResource = JSON.parse(
          JSON.stringify(this.filterDirectDataResource)
        );
        let inDirectDataResource = JSON.parse(
          JSON.stringify(this.filterIndirectDataResource)
        );

        //direct data as input
        this.getNodeLinkInstance(directDataResource, checkedInstances);
        this.getNodeLinkInstance(inDirectDataResource, checkedInstances);
        this.mxNodes = [];
        this.getNodes(this.dataNodes);
        this.createXml();

        await this.createFlowChart();
        this.activeTabs = "conceptMap";
        // this.getNodes();
      }
    },

    getNodeLinkInstance(dataResource, checkedInstances) {
      checkedInstances.forEach((instance) => {
        dataResource.forEach((data) => {
          let toModelInstanceList = data.toModelInstanceList;
          let fromModelInstance = data.fromModelInstance;
          if (toModelInstanceList != null) {
            toModelInstanceList.forEach((id) => {
              if (instance.id == id) {
                data.hasOwnProperty("to") ? "" : (data["to"] = []);
                instance.hasOwnProperty("from") ? "" : (instance["from"] = []);
                data["to"].push(instance);
                instance["from"].push(data);
                if (!data.hasOwnProperty("from")) {
                  this.dataNodes.includes(data)
                    ? ""
                    : this.dataNodes.push(data);
                }
              }
            });
          }
          if (fromModelInstance != null) {
            if (instance.id == fromModelInstance) {
              this.dataNodesIntermedia.includes(data)
                ? ""
                : this.dataNodesIntermedia.push(data);
              data.hasOwnProperty("from") ? "" : (data["from"] = []);
              instance.hasOwnProperty("to") ? "" : (instance["to"] = []);
              data["from"].push(instance);
              instance["to"].push(data);
            }
          }
        });
      });
    },

    getNodes(dataNodes) {
      this.nextStartIndex = 2;
      dataNodes.forEach((node, index) => {
        node.mxIndex = this.nextStartIndex;
        node.vertex = "1";
        this.mxNodes.push(node);

        if (node.hasOwnProperty("to") && node.to != []) {
          this.nextStartIndex += node.to.length * 2;
        } else {
          this.nextStartIndex += 2;
        }
        this.getNextInstance(node.to, node.mxIndex);
      });
    },

    getNextInstance(instances, sourceNodeIndex) {
      instances.forEach((nextInstance, nextIndex) => {
        let lineNode = {
          mxIndex: this.nextStartIndex - 1,
          source: sourceNodeIndex,
          target: this.nextStartIndex,
          edge: "1",
        };
        this.mxNodes.push(lineNode);

        nextInstance.mxIndex = this.nextStartIndex; //mxTargetIndex=mxIndex
        nextInstance.mxSourceIndex = sourceNodeIndex;
        nextInstance.vertex = "1";
        this.mxNodes.push(nextInstance);

        if (nextInstance.hasOwnProperty("to") && nextInstance.to != []) {
          // for (let i = 0; i < nextInstance.to.length; i++) {
          this.nextStartIndex += nextInstance.to.length * 2;
          // }
        } else {
          this.nextStartIndex += 2;
        }
        if (nextInstance.hasOwnProperty("to")) {
          this.getNextInstance(nextInstance.to, nextInstance.mxIndex);
        }
      });
    },

    createXml() {
      let nodes = this.mxNodes;
      console.log(this.mxNodes);
      let xml = "";

      let dataNodeStyle =
        "fillColor=#f8f5ec;strokeColor=rgb(200, 200, 200);strokeWidth=1;shape=ellipse;align=center;imageAlign=center;imageVerticalAlign=top";
      let modelNodeStyle =
        "fillColor=transparent;strokeColor=#000000;strokeWidth=1;shape=rectangle;align=center;imageAlign=center;imageVerticalAlign=top";
      let directNodes = nodes.filter((node) => {
        return node.isDirect == true;
      });

      directNodes.forEach((node, index) => {
        xml += this.xmlVertex(
          node.mxIndex,
          node.name,
          dataNodeStyle,
          index * 100,
          index * 400
        );
      });
      nodes.forEach((node, index) => {
        if (node.hasOwnProperty("vertex")) {
          if (node.hasOwnProperty("isDirect") && node.isDirect == false) {
            xml += this.xmlVertex(
              node.mxIndex,
              node.name,
              dataNodeStyle,
              index * 60,
              300
            );
          } else if (!node.hasOwnProperty("isDirect")) {
            xml += this.xmlVertex(
              node.mxIndex,
              node.name,
              modelNodeStyle,
              index * 60,
              150
            );
          }
        } else if (node.hasOwnProperty("edge")) {
          xml += this.xmlEdge(node.mxIndex, node.source, node.target);
        }
      });
      this.xml = xml;
      console.log(this.xml);
      this.sendXml = `<mxGraphModel><root><mxCell id="0"/><mxCell id="1" parent="0"/>${xml}</root></mxGraphModel>`;
    },
    xmlVertex(id, title, style, x, y) {
      let stepId = this.$route.params.stepId;
      let content = `<mxCell id="${stepId}+${id}" title="${title}" vertex="1" parent="1" style="${style}"><mxGeometry x="${x}" y="${y}" width="150" height="100" as="geometry" /></mxCell>`;
      return content;
    },
    xmlEdge(id, source, target) {
      let stepId = this.$route.params.stepId;
      let content = `<mxCell id="${stepId}+${id}"  source="${stepId}+${source}" target="${stepId}+${target}" edge="1" parent="1"><mxGeometry relative="1" as="geometry"/></mxCell>`;
      return content;
    },

    async getMap() {
      let data = await get(
        `/GeoProblemSolving/r/flowcharts/one/${this.stepInfo.stepId}`
      );
      if (data == null) {
        this.updateXml = false;
      } else {
        this.getSelectedInstances(data.modelInstanceIdList);
        this.updateXml = true;
        this.sendXml = `<mxGraphModel><root><mxCell id="0"/><mxCell id="1" parent="0"/>${data.mapXml}</root></mxGraphModel>`;
        // console.log(this.sendXml);
      }
    },
    getSelectedInstances(modelInstanceIdList) {
      this.modelInstances.forEach((item) => {
        if (modelInstanceIdList.some((id) => id == item.id)) {
          item.checkedForMap = true;
        }
      });
    },

    async createFlowChart() {
      let instances = Array.from(this.filterCreateMapInstances, ({ id }) => id);

      let flowChartSent = {
        modelInstanceIdList: instances,
        mapXml: this.xml,
      };

      if (this.updateXml) {
        let data = await patch(
          `/GeoProblemSolving/r/flowcharts/${this.stepInfo.stepId}`,
          flowChartSent
        );
        if (data != null) {
          this.$message({
            message: "You have update this map successfully",
            type: "success",
          });
        }
      } else {
        flowChartSent.stepId = this.stepInfo.stepId;
        flowChartSent.stepName = this.stepInfo.stepName;
        flowChartSent.type = this.stepInfo.stepType;
        flowChartSent.userId = this.userInfo.userId;
        flowChartSent.pid = this.projectId;

        let data = await post(`/GeoProblemSolving/r/flowcharts`, flowChartSent);
        if (data != null) {
          this.$message({
            message: "You have created this map successfully",
            type: "success",
          });
        }
        this.updateXml = true;
      }
    },
  },
  created() {
    this.initSize();
    if (this.userInfo) {
      this.init();
    }
  },
  mounted() {},
};
</script>
<style lang='scss' scoped>
.main {
  margin: 20px 20px;
}
.instance_name {
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
  word-break: break-all;
  float: left;
  width: 200px;
  text-align: left;
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

  //  -webkit-transform: translateY(-4px);
  //     -ms-transform: translateY(-4px);
  //     transform: translateY(-4px);
  -webkit-transition: all 0.2s ease-out;
  transition: all 0.2s ease-out;
  .data_info_hover {
    display: block;
    float: left;
    // right: 5px;
    color: rgb(45, 54, 92);
    // clear: both;
  }
}
.box-card {
  height: 100px;
  margin-bottom: 10px;
}
.finishing {
  // background-color: aqua;
}
.calculating {
  background-color: rgba(205, 231, 211, 0.2);
  border-color: rgba(205, 231, 211, 0.2);
  box-shadow: 0px 0px 5px rgb(127, 148, 131);
  border-bottom: 2px solid rgb(27, 73, 38);
  text-align: center;
}
.calculating_icon {
  color: rgb(30, 44, 35);
  font-weight: 600;
  text-align: center;
}
.error {
  background-color: rgba(231, 205, 205, 0.2);
  border-color: rgba(231, 205, 205, 0.2);
  box-shadow: 0px 0px 5px rgb(148, 127, 127);
  border-bottom: 2px solid rgb(73, 27, 27);
  text-align: center;
}
.error_icon {
  color: rgb(202, 37, 37);
  font-weight: 600;
  text-align: center;
}
</style>