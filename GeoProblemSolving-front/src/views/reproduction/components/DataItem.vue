<template>
  <div class="main">
    <el-row
      class="state-container"
      v-for="(state, index) in stateList"
      :key="index"
    >
      <el-col class="leftContainer" :span="24">
          <div class="modelState">
            <p class="state-name">{{ state.name }}</p>
          </div>
      </el-col>
      <el-col class="dataContainer" :span="22" :offset="1">
        <div class="params-group">
          <el-row v-if="inEventList(state).length !== 0" class="stateTitle"
            >Input</el-row
          >
          <div class="event">
            <div
              class="event-desc"
              v-for="(modelInEvent, inEventIndex) in inEventList(state)"
              :key="inEventIndex"
              ref="toolItemList"
            >
              <el-card class="event_name" :title="modelInEvent.name">
                <div
                  v-show="modelInEvent.optional == 'False'"
                  style="color: red"
                >
                  *
                </div>
                {{ modelInEvent.name }}
              </el-card>
            </div>
          </div>
        </div>

        <div class="params-group">
          <el-row v-if="outEventList(state).length !== 0" class="stateTitle"
            >Output</el-row
          >
          <div class="event">
            <el-col
              :span="24"
              class="event-desc"
              v-for="(modelOutEvent, outEventIndex) in outEventList(state)"
              :key="outEventIndex"
              ref="toolItemList"
            >
              <el-card class="event_name" :title="modelOutEvent.name">{{
                modelOutEvent.name
              }}</el-card>
            </el-col>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import file from "@/components/dataTemplate/File";
import { get, del, post, put, patch } from "@/axios";
export default {
  props: {
    cell: {
      type: Object,
    },
  },
  watch: {
    cell: {
      handler(val) {
        // console.log(val);
        if (val != "") {
          this.currentModel = val;
          this.init();
        }
      },
      deep: true,
    },

    pageParamsFrom: {
      handler(val) {
        this.pageParams = val;
      },
      deep: true,
    },
  },
  data() {
    return {
      doi: "",
      currentModel: this.cell,
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
  },

  methods: {
    init2() {
      return new Promise(function (reslove, reject) {
        // 模拟异步，加载组件
        setTimeout(function () {
          console.log("等了1秒钟");
          reslove(1000);
        }, 1000);
      });
    },

    async init() {
      this.getModelDoi();
      await this.getModelInfo();
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
    },
  },
};
</script>

<style lang="scss" scoped>
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
  // box-shadow: 0px 0px 4px rgb(203, 207, 212);
  width: 100%;
  .modelState {
    color: rgb(37, 44, 66);
    font-size: 14px;
  }
}

.stateTitle {
  font-size: 16px;
  font-weight: 600;
  color: rgb(87, 173, 253);
  font-style: italic;
}

.event {
  .event:hover {
    background-color: #c4d9f734;
  }
  .event_name {
    font-size: 14px;
    font-weight: 600;
    /* padding: 10px 0; */
  }
  >>> .el-card__body {
    padding: 5px;
    width: 200px;
  }
  .event-desc:hover {
    cursor: pointer;
  }
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