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
              ref="inputItemList"
            >
              <el-card :title="modelInEvent.name">
                <div
                  v-show="
                    modelInEvent.optional == 'False' ||
                    modelInEvent.optional == 'false'
                  "
                  class="event_option"
                >
                  *
                </div>
                <div class="event_name">
                  {{ modelInEvent.name }}
                </div>
              </el-card>
            </div>
          </div>
        </div>

        <div class="params-group">
          <el-row v-if="outEventList(state).length !== 0" class="stateTitle"
            >Output</el-row
          >
          <div class="event">
            <div
              class="event-desc"
              v-for="(modelOutEvent, outEventIndex) in outEventList(state)"
              :key="outEventIndex"
              ref="outputItemList"
            >
              <el-card :title="modelOutEvent.name">
                <div class="event_name">
                  {{ modelOutEvent.name }}
                </div>
              </el-card>
            </div>
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
          this.doi = val.doi;
          this.init();
        }
      },
      deep: true,
    },

    stateInView: {
      handler(state) {
        this.$emit("getState", state);
      },
      deep: true,
    },
  },
  data() {
    return {
      doi: "",
      modelIntroduction: {},
      modelInstance: {},
      md5: "",
      stateList: [],
      stateInView: {},
    };
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
      await this.getModelInfo();
    },

    async getModelInfo() {
      let data = await get(
        `/GeoProblemSolving/modelTask/ModelBehavior/${this.doi}`
      ); //获得模型所有信息
      this.md5 = data.md5;
      this.modelIntroduction = data;
      this.stateList = data.convertMdlJson;
      this.$emit("getStateList", this.stateList);
    },

    inEventList(state) {
      let events = state.Event.filter((value) => {
        return value.type === "response";
      });
      this.stateInView = state;
      return events;
    },

    outEventList(state) {
      let events = state.Event.filter((value) => {
        return value.type === "noresponse";
      });
      this.stateInView = state;
      // this.$emit("getOutEvent", events);
      return events;
    },

    filterUdxNode(event) {
      if (event.datasetItem.hasOwnProperty("UdxDeclaration")) {
        if (event.datasetItem.UdxDeclaration[0].UdxNode != "") {
          if (
            event.datasetItem.UdxDeclaration[0].UdxNode[0].UdxNode[0].hasOwnProperty(
              "UdxNode"
            )
          ) {
            return false;
          } else {
            let udxNode = event.datasetItem.UdxDeclaration[0].UdxNode;
            return udxNode;
          }
        }
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.main {
  position: relative;
  width: 200px;
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
  .event_option {
    color: red;
    float: left;
    width: 10px;
    font-size: 14px;
    font-weight: 600;
  }
  .event_name {
    font-size: 14px;
    font-weight: 600;
    padding-left: 10px;

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