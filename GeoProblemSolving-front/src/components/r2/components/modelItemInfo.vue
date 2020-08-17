<template>
  <div class="main">
    <!-- <el-button @click="createFilefromParam">test</el-button> -->
    <el-row class="title">
      <el-col>{{modelIntroduction.name}}</el-col>
    </el-row>

    <el-row>
      <el-col :span="6">
        <p class="des">{{modelIntroduction.description}}</p>
      </el-col>
      <el-col :span="2" :offset="12" class="save-btn">
        <el-button plain type="primary" @click="invokeTest">
          <i class="el-icon-setting"></i>&nbsp;invoke
        </el-button>
      </el-col>
    </el-row>

    <el-divider></el-divider>
    <el-row class="state-container" v-for="(state,index) in stateList" :key="index">
      <el-col class="leftContainer" :span="5">
        <el-col :offset="1" :span="22">
          <div class="modelState">
            <p class="state-name">{{state.name}}</p>
            <p class="state-desc">{{state.description}}</p>
          </div>
        </el-col>
      </el-col>
      <el-col class="dataContainer" :span="18" :offset="1">
        <div class="_params-group">
          <el-row v-if="inEventList(state).length!==0" class="stateTitle">Input</el-row>
          <el-divider class="stateTitleDivider"></el-divider>
          <div class="events">
            <el-row
              v-for="(modelInEvent,inEventIndex) in inEventList(state)"
              :key="inEventIndex"
              class="event"
            >
              <el-row>
                <el-col :span="17" class="_event-desc">
                  <span class="event_name" :title="modelInEvent.name">
                    <span v-show="modelInEvent.optional=='False'" style="color:red">*</span>
                    {{modelInEvent.name}}
                  </span>
                  <p
                    class="event_desc"
                    :title="modelInEvent.description"
                  >{{modelInEvent.description}}</p>
                </el-col>

                <el-col :span="6" :offset="1" v-if="modelInEvent.datasetItem[0].type != `internal`">
                  <el-select
                    v-model="modelInEvent.url"
                    clearable
                    placeholder="Please select data"
                    @change="selectDatatoModel($event,index,inEventIndex)"
                  >
                    <el-option
                      v-for="(item,dataIndex) in dataList"
                      :key="dataIndex"
                      :label="item.name"
                      :value="item.url"
                    ></el-option>
                    <!-- {{item.value}} -->
                  </el-select>
                </el-col>
              </el-row>
              <el-row v-if="modelInEvent.datasetItem[0].type == `internal`">
                <div v-if="filterUdxNode(modelInEvent)">
                  <el-table border :data="filterUdxNode(modelInEvent)[0].UdxNode">
                    <el-table-column prop="name" label="Parameter" width="180"></el-table-column>
                    <el-table-column prop="description" label="Description" width="180"></el-table-column>
                    <el-table-column prop="type" label="Type"></el-table-column>
                    <el-table-column label="Value">
                      <template slot-scope="scope">
                        <el-input v-model="scope.row.value"></el-input>
                      </template>
                    </el-table-column>
                  </el-table>
                </div>
              </el-row>
              <el-row>
                <el-divider class="eventDivider"></el-divider>
              </el-row>
            </el-row>
          </div>
        </div>

        <div class="_params-group">
          <el-row v-if="outEventList(state).length!==0" class="stateTitle">Output</el-row>
          <div class="events">
            <el-row
              v-for="(modelOutEvent,outEventIndex) in outEventList(state)"
              :key="outEventIndex"
              class="event"
            >
              <el-row>
                <el-col :span="17" class="_event-desc">
                  <span class="event_name" :title="modelOutEvent.name">{{modelOutEvent.name}}</span>
                  <p
                    class="event_desc"
                    :title="modelOutEvent.eventDesc"
                  >{{modelOutEvent.description}}</p>
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
                    >Download</el-button>

                    <el-button
                      size="small"
                      plain
                      round
                      type="warning"
                      @click="bind(modelOutEvent)"
                      :disabled="status"
                    >Bind</el-button>
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
    modelDoi: {
      type: String,
    },
    selectedData: {
      type: Array,
    },
  },
  watch: {
    modelDoi: {
      handler(val) {
        this.doi = val;
      },
      deep: true,
    },
    selectedData: {
      handler(val) {
        this.dataList = val;
        console.log(val);
      },
      deep: true,
    },
  },
  data() {
    return {
      doi: this.modelDoi,
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
      userInfo: {},
      bindFileName: "",
      paramInput: "",
      uploadFileForm: new FormData(),
      dataList: this.selectedData,
      selectDataValue: "",
      // internalEvents: [],
    };
  },
  computed: {
    stateList() {
      let stateList = this.ordinaryStateList;
      let datasetItem = this.datasetItem;
      for (let i = 0; i < stateList.length; i++) {
        let events = stateList[i].Event;
        for (let j = 0; j < events.length; j++) {
          //   events[j]["url"] = "";
          if (events[j].type == "response") {
            let template = datasetItem.filter((dataset) => {
              return (
                dataset.name === events[j].ResponseParameter[0].datasetReference
              );
            });
            events[j]["datasetItem"] = template;
          }
        }
      }
      return stateList;
    },
  },

  methods: {
    test() {
      this.save();
    },

    getStepInfo() {
      if (
        this.$route.params.groupID == undefined ||
        this.$route.params.groupID == ""
      ) {
        var href = window.location.href;
        var url = href.split("&");

        for (var i = 0; i < url.length; i++) {
          if (/groupID/.test(url[i])) {
            this.pageParams.pageId = url[i].match(/groupID=(\S*)/)[1];
            continue;
          }

          if (/userID/.test(url[i])) {
            this.pageParams.userId = url[i].match(/userID=(\S*)/)[1];
            continue;
          }

          if (/userName/.test(url[i])) {
            this.pageParams.userName = url[i].match(/userName=(\S*)/)[1];
            continue;
          }
        }
      } else {
        this.pageParams.pageId = this.$route.params.groupID;
        this.pageParams.userId = this.$route.params.userID;
        this.pageParams.userName = this.$route.params.userName;
      }
    },

    getUserInfo() {
      this.userInfo = JSON.parse(sessionStorage.getItem("userInfo"));
      if (this.userInfo == {}) {
        this.axios
          .get(
            "/GeoProblemSolving/user/inquiry" +
              "?key=" +
              "userId" +
              "&value=" +
              this.pageParams.userId
          )
          .then((res) => {
            if (res.data != "Fail" && res.data != "None") {
              this.$set(this, "userInfo", res.data);
            }
          })
          .catch((err) => {});
      }
    },

    async init() {
      this.initLoading();
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
      let data2 = await this.axios.get(
        `/GeoProblemSolving/modelTask/createTask/${this.md5}/${this.pageParams.userId}`
      );
      let creatTaskResult = data2.data.data;

      this.invokeForm.ip = creatTaskResult.ip;
      this.invokeForm.port = creatTaskResult.port;
      this.invokeForm.pid = creatTaskResult.pid;
      this.invokeForm.username = this.pageParams.userId;
      this.fullscreenLoading.close();
    },

    //获得上传到数据容器的数据的id
    getNewStateList(data) {
      this.stateList = data;
      this.getStateEvent();
    },

    //invoke --form表单创建
    getStateEvent() {
      try {
        let stateList = this.stateList;
        let datasetItem = this.datasetItem;
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
            } else {
              //如果是output --对应template
              let outputTemplate = datasetItem.filter((dataset) => {
                return (
                  dataset.name ===
                  events[j].DispatchParameter[0].datasetReference
                );
              });
              let template = {};
              //如果是external template["type"] = id,不然为空
              if (outputTemplate[0].type === "external") {
                template["type"] = outputTemplate[0].id;
                template["value"] = outputTemplate[0].externalId;
              } else {
                template["type"] = "none";
                template["value"] = "";
              }
              detail["template"] = template;
              output.push(detail);
            }
          }
        }
        this.invokeForm.inputs = input;
        this.invokeForm.outputs = output;
      } catch (e) {
        if (e.message != "breakForEach") throw e;
      }
    },

    async invokeTest() {
      this.loading();
      this.createFilefromParam();
      //测试数据没有弄 直接运行 根据ip+id
      //invoke
      let { data } = await this.axios.post(
        "/GeoProblemSolving/modelTask/invoke",
        this.invokeForm
      );
      let invokeResultId = data.data;
      let refreshForm = {};
      refreshForm["ip"] = this.invokeForm.ip;
      refreshForm["port"] = this.invokeForm.port;
      refreshForm["tid"] = data.data;
      this.status = false;
      this.getOutputs(refreshForm);
    },

    async createFilefromParam() {
      let stateList = this.stateList;
      for (let i = 0; i < stateList.length; i++) {
        let events = stateList[i].Event;
        for (let j = 0; j < events.length; j++) {
          if (
            events[j].type == "response" &&
            events[j].datasetItem[0].hasOwnProperty("UdxDeclaration") &&
            events[j].datasetItem[0].UdxDeclaration[0].UdxNode != ""
          ) {
            let content = "";
            this.uploadFileForm = new FormData();

            let udxNodeList =
              events[j].datasetItem[0].UdxDeclaration[0].UdxNode[0].UdxNode;
            for (let k = 0; k < udxNodeList.length; k++) {
              content += `<XDO name="${udxNodeList[k].name}" kernelType="${udxNodeList[k].type}" value="${udxNodeList[k].value}" />`;
            }

            content = "<Dataset> " + content + " </Dataset>";

            let file = new File([content], events[j].name + ".xml", {
              type: "text/plain",
            });
            this.uploadFileForm.append("files", file);
            this.createConfigFile();
            await this.submitUpload(i, j);
          }
        }
      }
    },
    createConfigFile() {
      let configContent = "<UDXZip><Name>";
      configContent += "<add value='" + file.name + "' />";
      configContent += "</Name>";
      // let data = event.data[0];
      configContent += "<DataTemplate type='none'>";
      configContent += "</DataTemplate>";
      configContent += "</UDXZip>";
      let configFile = new File([configContent], "config.udxcfg", {
        type: "text/plain",
      });
      this.uploadFileForm.append("files", configFile);
    },

    async submitUpload(stateIndex, eventIndex) {
      let data = await post(
        `/GeoProblemSolving/modelTask/uploadFileForm`,
        this.uploadFileForm
      );

      let resultId = `http://111.229.14.128:8899/data?uid=${data}`;
      this.$set(this.stateList[stateIndex].Event[eventIndex], "url", resultId);
      console.log(this.stateList);
    },

    async getOutputs(refreshForm) {
      //获得结果
      this.timer = setInterval(async () => {
        if (this.record.status == 2) {
          this.fullscreenLoading.close();
          clearInterval(this.timer);
          let outputUrl = this.record.outputs;
          this.getStateEventOut(outputUrl);
          return;
        } else {
          let { data } = await this.axios.post(
            "/GeoProblemSolving/modelTask/getRecord",
            refreshForm
          );
          this.record = data.data.data;
        }
      }, 2000);
    },

    getStateEventOut(outputUrl) {
      console.log(outputUrl);
      let outList = this.stateList;
      outList.forEach((state, index) => {
        state.Event.forEach((event, eventIndex) => {
          outputUrl.forEach((el) => {
            if (el.statename == state.name && el.event == event.name) {
              this.$set(this.stateList[index].Event[eventIndex], "url", el.url);
            }
          });
        });
      });
    },

    download(event) {
      window.open(event.url);
    },

    dataURItoBlob(event) {
      this.urlToBlob(event.url, (blob) => {
        let file = new File([blob], this.bindFileName);
        let formData = new FormData();

        formData.append("file", file);
        formData.append("description", event.description);
        formData.append("type", "toolData");
        formData.append("uploaderId", this.pageParams.userId);
        formData.append("privacy", "private");
        formData.append("folderId", this.pageParams.pageId);

        this.axios
          .post("/GeoProblemSolving/folder/uploadToFolder", formData)
          .then((res) => {
            console.log(res);
            if (res.data.uploaded != null) {
              this.$message({
                message: "You have binded the resource Successfully!",
                type: "success",
              });
            }
          })
          .catch(function (err) {
            console.log(err);
          });
      });
    },

    urlToBlob(the_url, callback) {
      let xhr = new XMLHttpRequest();
      xhr.open("get", the_url, true);
      xhr.send();
      var that = this;
      xhr.onreadystatechange = function () {
        if (this.readyState === 4) {
          let headers = xhr.getAllResponseHeaders();
          //打印文件名，这里打印的是编码（因为兼容不同语言的数据文件名字）后的，前端用unescape('xxx')去解码，解码后是对应的名字
          that.bindFileName = headers.split(";")[1].split("=")[1];
        }
      };

      xhr.onload = function () {
        if (this.status == 200) {
          if (callback) {
            callback(this.response);
          }
        }
      };
    },

    async bind(event) {
      this.dataURItoBlob(event);
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
        let udxNode = event.datasetItem[0].UdxDeclaration[0].UdxNode;
        return udxNode;
      }
    },

    // internalEventList(event) {
    //   let datasetItem = this.datasetItem;
    //   return datasetItem.filter((dataset) => {
    //     return (
    //       dataset.name === event.ResponseParameter[0].datasetReference &&
    //       dataset.type === "internal"
    //     );
    //   });
    // },
    // otherEventList(event) {
    //   let datasetItem = this.datasetItem;
    //   return datasetItem.filter((dataset) => {
    //     return (
    //       dataset.name === event.ResponseParameter[0].datasetReference &&
    //       dataset.type === "external"
    //     );
    //   });
    // },

    loading() {
      this.fullscreenLoading = this.$loading({
        lock: true,
        text: "Calculating",
        spinner: "el-icon-loading",
        background: "rgba(0, 0, 0, 0.7)",
      });
    },
    initLoading() {
      this.fullscreenLoading = this.$loading({
        lock: true,
        text: "Initialization",
        spinner: "el-icon-loading",
        background: "rgba(0, 0, 0, 0.7)",
      });
    },

    async saveRecords(modelInstanceId) {
      let data = await this.axios.post(
        "/GeoProblemSolving/modelItem/saveRecord",
        {
          modelInstanceId: modelInstanceId,
          userId: this.userId,
          stepId: this.stepId,
        }
      );
    },

    async getAllRecords() {
      let stepId = this.stepId;
      //根据用户ID查找所欲的modelInstance
      let data = await this.axios.get(
        `/GeoProblemSolving/modelItem/getAllRecords/${stepId}`
      );
      if (data.status == "200") {
        console.log("返回记录成功");
        // this.recordList = data.data.data;
        let recordList = data.data.data;
        console.log(recordList);
        for (let i = 0; i < recordList.length; i++) {
          let modelInstanceId = recordList[i].modelInstanceId;
          console.log(modelInstanceId);
          let item = await this.axios.get(
            `/GeoProblemSolving/modelItem/getModelInstance/${modelInstanceId}`
          );
          console.log(item);
          this.recordList.push(item);
        }
      } else {
        console.log("返回记录失败");
      }
      console.log(this.recordList);
    },

    selectDatatoModel(value, stateIndex, eventIndex) {
      //   console.log(value, index);
      this.$set(this.stateList[stateIndex].Event[eventIndex], "url", value);
      this.newStateList();
    },
  },

  mounted() {
    // debugger;
    this.getStepInfo();
    this.getUserInfo();
    this.init();
  },

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