<template>
  <div class="main">
    <!-- <el-button @click="createFilefromParam">test</el-button> -->
    <el-row class="title">
      <el-col>{{ modelIntroduction.name }}</el-col>
    </el-row>

    <el-row>
      <el-col :span="6">
        <p class="des">{{ modelIntroduction.description }}</p>
      </el-col>
      <el-col :span="2" :offset="12" class="save-btn">
        <el-button plain type="primary" @click="invokeTest">
          <i class="el-icon-setting"></i>&nbsp;invoke
        </el-button>
      </el-col>
    </el-row>

    <el-divider></el-divider>

    <el-row
      class="state-container"
      v-for="(state, index) in stateList"
      :key="index"
    >
      <el-col class="leftContainer" :span="5">
        <el-col :offset="1" :span="22">
          <div class="modelState">
            <p class="state-name">{{ state.name }}</p>
            <p class="state-desc">{{ state.description }}</p>
          </div>
        </el-col>
      </el-col>
      <el-col class="dataContainer" :span="18" :offset="1">
        <div class="_params-group">
          <el-row v-if="inEventList(state).length !== 0" class="stateTitle"
            >Input</el-row
          >
          <el-divider class="stateTitleDivider"></el-divider>
          <div class="events">
            <el-row
              v-for="(modelInEvent, inEventIndex) in inEventList(state)"
              :key="inEventIndex"
              class="event"
            >
              <el-row>
                <el-col :span="17" class="_event-desc">
                  <span class="event_name" :title="modelInEvent.name">
                    <span
                      v-show="modelInEvent.optional == 'False'"
                      style="color: red"
                      >*</span
                    >
                    {{ modelInEvent.name }}
                  </span>
                  <p class="event_desc" :title="modelInEvent.description">
                    {{ modelInEvent.description }}
                  </p>
                </el-col>

                <el-row v-if="modelInEvent.datasetItem[0].type == `internal`">
                  <div v-if="filterUdxNode(modelInEvent)">
                    <el-table
                      border
                      :data="filterUdxNode(modelInEvent)[0].UdxNode"
                    >
                      <el-table-column
                        prop="name"
                        label="Parameter"
                        width="180"
                      ></el-table-column>
                      <el-table-column
                        prop="description"
                        label="Description"
                        width="180"
                      ></el-table-column>
                      <el-table-column
                        prop="type"
                        label="Type"
                      ></el-table-column>
                      <el-table-column label="Value">
                        <template slot-scope="scope">
                          <el-input v-model="scope.row.value"></el-input>
                        </template>
                      </el-table-column>
                    </el-table>
                  </div>
                  <div v-else>
                    <el-select
                      v-model="modelInEvent.url"
                      clearable
                      placeholder="Please select data"
                      @change="selectDatatoModel($event, index, inEventIndex)"
                    >
                      <el-option
                        v-for="(item, dataIndex) in resources.dataItemList"
                        :key="dataIndex"
                        :label="item.name"
                        :value="item.url"
                      ></el-option>
                    </el-select>
                  </div>
                </el-row>
                <el-col :span="6" :offset="1" v-else>
                  <el-select
                    v-model="modelInEvent.url"
                    clearable
                    placeholder="Please select data"
                    @change="selectDatatoModel($event, index, inEventIndex)"
                  >
                    <el-option
                      v-for="(item, dataIndex) in resources.dataItemList"
                      :key="dataIndex"
                      :label="item.name"
                      :value="item.url"
                    ></el-option>
                  </el-select>
                </el-col>
              </el-row>
              <el-row>
                <el-divider class="eventDivider"></el-divider>
              </el-row>
            </el-row>
          </div>
        </div>

        <div class="_params-group">
          <el-row v-if="outEventList(state).length !== 0" class="stateTitle"
            >Output</el-row
          >
          <div class="events">
            <el-row
              v-for="(modelOutEvent, outEventIndex) in outEventList(state)"
              :key="outEventIndex"
              class="event"
            >
              <el-row>
                <el-col :span="17" class="_event-desc">
                  <span class="event_name" :title="modelOutEvent.name">{{
                    modelOutEvent.name
                  }}</span>
                  <p class="event_desc" :title="modelOutEvent.eventDesc">
                    {{ modelOutEvent.description }}
                  </p>
                </el-col>
                <el-col :span="6" :offset="1">
                  <div class="_btn-group">
                    <el-button
                      size="small"
                      plain
                      round
                      type="warning"
                      @click="download(modelOutEvent)"
                      :disabled="status"
                      >Download</el-button
                    >

                    <el-button
                      size="small"
                      plain
                      round
                      type="warning"
                      @click="bind(modelOutEvent)"
                      :disabled="status"
                      >Bind</el-button
                    >
                  </div>
                </el-col>
              </el-row>
            </el-row>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import file from "@/components/dataTemplate/File";
import { get, del, post, put } from "../../../axios";
export default {
  props: {
    pageParamsFrom: {
      type: Object,
    },

    currentModelInfo: {
      type: Object,
    },

    selectedResources: {
      type: Object,
    },
    instanceFolk: { type: Object },
  },
  watch: {
    currentModelInfo: {
      handler(val) {
        // console.log(val);
        if (val != "") {
          this.currentModel = val;
          this.status = false;
          this.init();
        }
      },
      deep: true,
    },

    selectedResources: {
      handler(val) {
        this.resources = val;
        // this.dataList = this.resources.dataItemList;
        // this.toolList = this.resources.toolItemList;
      },
      deep: true,
    },
    pageParamsFrom: {
      handler(val) {
        this.pageParams = val;
      },
      deep: true,
    },
    instanceFolk: {
      async handler(val) {
        this.instanceFolkData = val;
        this.status = false;
        this.stateList = this.instanceFolkData.statesJson;
        this.modelIntroduction = {
          name: this.instanceFolkData.name,
          description: this.instanceFolkData.description,
        };
        // console.log(JSON.parse(this.instanceFolkData.md5));
        this.md5 = this.instanceFolkData.md5;
        this.currentModel = {
          toolId: this.instanceFolkData.toolId,
          toolUrl: this.instanceFolkData.toolUrl,
        };
        this.modelInstance = val;
        await this.initTask();
      },
      deep: true,
    },
  },
  data() {
    return {
      doi: "",
      currentModel: this.currentModelInfo,
      modelIntroduction: {},
      ordinaryStateList: {},
      modelInstance: {},
      datasetItem: [],
      timer: {},
      fullscreenLoading: {},
      componentDisables: false,
      inputFile: false,
      recordList: [],
      md5: "",
      invokeForm: {
        ip: "",
        port: "",
        pid: "",
        username: "",
        inputs: [
          {
            statename: "",
            event: "",
            url: "",
            tag: "",
          },
        ],
        outputs: [
          {
            statename: "",
            event: "",
            template: {
              type: "", //id|none
              value: "", //if tyoe=none value=""
            },
          },
        ],
      },
      status: true,
      record: {},
      // page info
      pageParams: this.pageParamsFrom,

      uploadFileForm: new FormData(),
      resources: this.selectedResources,
      // dataList: this.resources.dataItemList,
      // toolList: this.resources.toolItemList,

      instanceFolkData: {},
      stateListFolk: [],
      currentInstance: {},
      stateList: [],
      // internalEvents: [],
    };
  },

  computed: {
    stateList2: {
      get() {
        let stateList = this.ordinaryStateList;
        let datasetItem = this.datasetItem;
        for (let i = 0; i < stateList.length; i++) {
          let events = stateList[i].Event;
          for (let j = 0; j < events.length; j++) {
            if (events[j].type == "response") {
              let event = events[j];
              let template = {};
              if (event.hasOwnProperty("ResponseParameter")) {
                template = datasetItem.filter((dataset) => {
                  return (
                    dataset.name === event.ResponseParameter[0].datasetReference
                  );
                });
              } else if (event.hasOwnProperty("ControlParameter")) {
                template = datasetItem.filter((dataset) => {
                  return (
                    dataset.name === event.ControlParameter[0].datasetReference
                  );
                });
              }
              events[j]["datasetItem"] = template;
            } else if (events[j].type == "noresponse") {
              //如果是输出
              let event = events[j];
              let template = {};
              if (event.hasOwnProperty("DispatchParameter")) {
                template = datasetItem.filter((dataset) => {
                  return (
                    dataset.name === event.DispatchParameter[0].datasetReference
                  );
                });
              }
              events[j]["datasetItem"] = template;
            }
          }
        }
        return stateList;
      },
      set(newValue) {
        return newValue;
      },
    },

    // filterDirectDataResource() {
    //   return this.resources.dataItemList.filter(
    //     (data) => data.isDirect == true
    //   );
    // },
    // filterIndirectDataResource() {
    //   return this.resources.dataItemList.filter(
    //     (data) => data.isDirect == false
    //   );
    // },
  },

  methods: {
    test() {
      this.save();
    },

    async init() {
      this.getModelDoi();
      this.initLoading();
      await this.getModelInfo();
      await this.initTask();
      this.fullscreenLoading.close();
    },

    getModelDoi() {
      let arr = this.currentModel.toolUrl.split("/");
      this.doi = arr[arr.length - 1];
    },

    async getModelInfo() {
      let data = await get(
        `/GeoProblemSolving/modelTask/getModelBehavior/${this.doi}`
      ); //获得模型所有信息
      this.md5 = data.md5;
      this.modelIntroduction = data;
      this.ordinaryStateList =
        data.mdlJson.ModelClass[0].Behavior[0].StateGroup[0].States[0].State;
      this.datasetItem =
        data.mdlJson.ModelClass[0].Behavior[0].RelatedDatasets[0].DatasetItem;
      //预处理过程 STEP0
      this.stateList = Object.assign([], this.stateList2);
    },

    async initTask() {
      //get task ip port ...
      let data2 = await get(
        `/GeoProblemSolving/modelTask/createTask/${this.md5}/${this.pageParams.userId}`
      );
      this.invokeForm.ip = data2.ip;
      this.invokeForm.port = data2.port;
      this.invokeForm.pid = data2.pid;
      this.invokeForm.username = this.pageParams.userId;
    },

    initLoading() {
      this.fullscreenLoading = this.$loading({
        lock: true,
        text: "Initialization",
        spinner: "el-icon-loading",
        background: "rgba(0, 0, 0, 0.7)",
      });
    },
    //invoke --form表单创建
    getStateEvent() {
      let stateList = this.stateList;
      let input = [];
      let output = [];
      for (let i = 0; i < stateList.length; i++) {
        let events = stateList[i].Event;
        for (let j = 0; j < events.length; j++) {
          //判断数据类型 如果是input--对应url
          let detail = {};
          detail["statename"] = stateList[i].name;
          detail["event"] = events[j].name;
          if (events[j].type == "response") {
            if (events[j].hasOwnProperty("url")) {
              detail["tag"] = events[j].name;
              detail["url"] = events[j].url;
              input.push(detail);
            } else {
              continue;
            }
          } else if (events[j].type == "noresponse") {
            let template = {};
            let outputTemplate = events[j].datasetItem;
            console.log(outputTemplate);
            //如果是external template["type"] = id,不然为空
            if (outputTemplate[0].type === "external") {
              template = {
                type: "id",
                value: outputTemplate[0].externalId,
              };
            } else {
              template = {
                type: "none",
                value: "",
              };
            }
            detail["template"] = template;
            output.push(detail);
          }
        }
      }
      this.invokeForm.inputs = input;
      this.invokeForm.outputs = output;
    },

    async invokeTest() {
      await this.createFilefromParam();
      this.getStateEvent();
      //测试数据没有弄 直接运行 根据ip+id
      //invoke
      let data = await post(
        `/GeoProblemSolving/modelTask/invoke`,
        this.invokeForm
      );
      let refreshForm = {};
      refreshForm["ip"] = this.invokeForm.ip;
      refreshForm["port"] = this.invokeForm.port;
      refreshForm["tid"] = data;
      if (data == null) {
        this.$message({
          message: "You have run the model failed",
          type: "error",
        });
        let record = {
          status: -1, //0 calculating
          ...refreshForm,
        };
        await this.emitInstance(record);
        // this.status = false;
      } else {
        let record = {
          status: 0, //0 calculating
          ...refreshForm,
        };
        await this.emitInstance(record);
        this.status = false;
        this.getOutputs(refreshForm);
      }
    },

    async emitInstance(record) {
      //在运行时 instance的创建
      if (record.status == 0) {
        let stepResource = {
          states: JSON.stringify(this.stateList),
          name: this.modelIntroduction.name,
          description: this.modelIntroduction.description,
          ...this.currentModel,
          ...this.pageParams,
          stepDescription: "",
          ...this.pageParams,
          md5: this.md5,
          ...record,
        };
        let { data } = await post(
          `/GeoProblemSolving/r/modelInstance/save`,
          stepResource
        );
        this.modelInstance = data;
      } else if (record.status == 2) {
        let stepResource = {
          states: JSON.stringify(this.stateList),
          status: record.status, //2 finish
        };
        let { data } = await post(
          `/GeoProblemSolving/r/modelInstance/update/${this.modelInstance.id}`,
          stepResource
        );
        this.modelInstance = data;
      } else if (record.status == -1) {
        //报错
        let stepResource = {
          states: JSON.stringify(this.stateList),
          status: record.status, //2 finish
        };
        let { data } = await post(
          `/GeoProblemSolving/r/modelInstance/update/${this.modelInstance.id}`,
          stepResource
        );
        this.modelInstance = data;
      }
      this.$emit("modelInstance", this.modelInstance);
    },

    async createFilefromParam() {
      let stateList = this.stateList;
      for (let i = 0; i < stateList.length; i++) {
        let events = stateList[i].Event;
        for (let j = 0; j < events.length; j++) {
          //判断如果是参数的话，重新绑定成为一个文件 之后上传 返回url绑定到mdl中去
          if (
            events[j].type == "response" &&
            events[j].datasetItem[0].hasOwnProperty("UdxDeclaration") &&
            events[j].datasetItem[0].UdxDeclaration[0].UdxNode != "" &&
            !events[
              j
            ].datasetItem[0].UdxDeclaration[0].UdxNode[0].UdxNode[0].hasOwnProperty(
              "UdxNode"
            )
          ) {
            let content = "";
            let uploadFileForm = new FormData();

            let udxNodeList =
              events[j].datasetItem[0].UdxDeclaration[0].UdxNode[0].UdxNode;
            for (let k = 0; k < udxNodeList.length; k++) {
              if (udxNodeList[k].hasOwnProperty("value")) {
                // content += `<XDO name="${udxNodeList[k].name}" kernelType="${udxNodeList[k].type}" value="${udxNodeList[k].value}" />`;
                content += `<XDO name="${udxNodeList[k].name}" kernelType="string" value="${udxNodeList[k].value}" />`;
              }
            }
            if (content != "") {
              content = "<Dataset> " + content + " </Dataset>";
              let file = new File([content], events[j].name + ".xml", {
                type: "text/plain",
              });
              uploadFileForm.append("file", file);

              // this.createConfigFile();
              await this.submitUpload(i, j, uploadFileForm);
            }
          }
        }
      }
    },

    async getOutputs(refreshForm) {
      //获得结果
      this.record = {};
      this.timer = setInterval(async () => {
        if (this.record.status == 2) {
          clearInterval(this.timer);
          await this.getStateEventOut(this.record);
          await this.getDataResourceLinkInstance();
          return;
        } else {
          let { data } = await post(
            "/GeoProblemSolving/modelTask/getRecord",
            refreshForm
          );
          this.record = data;
        }
      }, 2000);
    },

    async submitUpload(stateIndex, eventIndex, uploadFileForm) {
      let data = await post(
        `/GeoProblemSolving/dataContainer/uploadSingle`,
        uploadFileForm
      );
      let resultId = `http://221.226.60.2:8082/data?uid=${data}`;
      this.$set(this.stateList[stateIndex].Event[eventIndex], "url", resultId);
    },

    async getStateEventOut(record) {
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
      await this.emitInstance(record);
    },

    async getDataResourceLinkInstance() {
      let stateList = this.stateList;
      let directDataResource = this.resources.dataItemList;
      // let inDirectDataResource = this.filterIndirectDataResource;
      console.log(this.modelInstance);

      stateList.forEach((state) => {
        state.Event.forEach((event) => {
          directDataResource.forEach((data) => {
            if (event.url == data.url) {
              if (
                !data.hasOwnProperty("toModelInstanceList") ||
                data["toModelInstanceList"] == null
              ) {
                data["toModelInstanceList"] = [];
              }

              data["toModelInstanceList"].push(this.modelInstance.id);
              // break;
            }
          });
        });
      });
      console.log(this.resources.dataItemList);
      await this.updateResource();
    },

    async updateResource() {
      console.log(this.resources);
      let data = await post(
        `/GeoProblemSolving/r/resource/update/${this.modelInstance.pid}`,
        this.resources
      );
    },

    download(event) {
      window.open(event.url);
    },

    async bind(event) {
      let resource = this.resources;
      let url = event.url;
      let fileName = event.name;
      this.transationDataItemList = {
        url: event.url,
        name: event.name,
        isDirect: false,
        fromModelInstance: this.modelInstance.id,
        stepBind: {
          stepId: this.pageParams.stepId,
          stepName: this.pageParams.stepName,
        },
      };
      resource.dataItemList.push(this.transationDataItemList);

      let data = await post(
        `/GeoProblemSolving/r/resource/update/${resource.pid}`,
        resource
      );

      if (data.msg == "成功") {
        this.$message({
          message: "You have collect your resource successfully",
          type: "success",
        });
      }
    },

    inEventList(state) {
      return state.Event.filter((value) => {
        return value.type === "response";
      });
    },

    outEventList(state) {
      return state.Event.filter((value) => {
        return value.type === "noresponse";
      });
    },

    filterUdxNode(event) {
      if (event.datasetItem[0].hasOwnProperty("UdxDeclaration")) {
        if (event.datasetItem[0].UdxDeclaration[0].UdxNode != "") {
          if (
            event.datasetItem[0].UdxDeclaration[0].UdxNode[0].UdxNode[0].hasOwnProperty(
              "UdxNode"
            )
          ) {
            return false;
          } else {
            let udxNode = event.datasetItem[0].UdxDeclaration[0].UdxNode;
            return udxNode;
          }
        }
      }
    },

    selectDatatoModel(value, stateIndex, eventIndex) {
      this.$set(this.stateList[stateIndex].Event[eventIndex], "url", value);
      //   this.newStateList();
    },
  },

  mounted() {},

  beforeDestroy() {
    clearInterval(this.timer);
  },
  components: {
    file,
  },
};
</script>

<style scoped>
.main {
  position: relative;
}

.state-desc {
  margin: 0px 0px 15px 0px;
  padding: 4px 0px;
  line-height: 2;
  background-color: #f3f3f3;
  font-size: 16px;
  font-style: italic;
}
.el-tabs__item {
  font-size: 16px;
}
.el-tabs__item:hover {
  color: #00bbd8;
  background-color: #b5dce244;
}
.el-tabs__item.is-active {
  color: #00bbd8;
}
.el-tabs__active-bar {
  background-color: #00bbd8;
}
.leftContainer {
  background-color: rgba(142, 200, 255, 0.2);
  border-radius: 5px;
  box-shadow: 0px 0px 4px rgb(203, 207, 212);
  padding: 20px 0;
}
.modelState {
  color: rgb(37, 44, 66);
  font-size: 18px;
  font-family: "微软雅黑";
  margin: 1% 0;
}
.stateTitle {
  font-size: 20px;
  font-weight: 600;
  color: rgb(87, 173, 253);
  font-style: italic;
}
.stateTitleDivider.el-divider--horizontal {
  height: 2px;
  margin: 10px 0;
}
.stateTitleDivider.el-divider {
  background-color: rgb(140, 144, 148);
}
.event {
  padding: 15px 0 0 50px;
}
.event:hover {
  background-color: #c4d9f734;
}
.event_name {
  font-size: 16px;
  font-weight: 600;
  /* padding: 10px 0; */
}
.event_desc {
  font-size: 14px;
  font-style: italic;
  margin: 10px 0;
  color: rgb(94, 94, 94);
  word-wrap: break-word;
}
.eventDivider.el-divider--horizontal {
  margin: 10px 0;
}
.des {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  /* !autoprefixer: off */
  -webkit-box-orient: vertical;
  font-size: 14px;
}
.title {
  font-weight: 600;
  font-size: 20px;
  margin: 20px 0 10px 0;
}
</style>