<template>
  <div class="mainContain" v-if="JSON.stringify(currentEvent) != '{}'">
    <el-row>
      <div class="data">
        <div class="dataTitle">State name:</div>
        <div>{{ currentEvent.state.name }}</div>
      </div>
      <div class="data">
        <div class="dataTitle">State description:</div>
        <div>{{ currentEvent.state.description }}</div>
      </div>
      <div class="data">
        <div class="dataTitle">State type:</div>
        <div>{{ currentEvent.state.type }}</div>
      </div>
      <div class="data">
        <div class="dataTitle">Event name:</div>
        <div>{{ currentEvent.name }}</div>
      </div>
      <div class="data">
        <div class="dataTitle">Event description:</div>
        <div>{{ currentEvent.description }}</div>
      </div>
      <div class="data">
        <div class="dataTitle">Event type:</div>
        <div>{{ currentEvent.type }}</div>
      </div>
    </el-row>

    <el-row>
      <el-divider class="eventDivider"></el-divider>
    </el-row>
    <el-row>
      <el-row v-if="currentEvent.datasetItem.type == `internal`">
        <vue-scroll style="height: 300px; width: 100%">
          <div v-if="filterEvent">
            <el-table
              border
              :data="filterEvent[0].UdxNode"
              size="mini"
              class="table"
            >
              <el-table-column type="expand" width="30">
                <template slot-scope="props">
                  <el-form
                    label-position="top"
                    inline
                    class="demo-table-expand"
                    size="mini"
                  >
                    <el-form-item label="Parameter">
                      <span>{{ props.row.name }}</span>
                    </el-form-item>
                    <el-form-item label="Type">
                      <span>{{ props.row.type }}</span>
                    </el-form-item>
                    <el-form-item label="Description">
                      <span>{{ props.row.description }}</span>
                    </el-form-item>
                  </el-form>
                </template>
              </el-table-column>
              <el-table-column
                prop="name"
                label="Parameter"
                width="130"
              ></el-table-column>
              <el-table-column label="Value" width="100">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.value"></el-input>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <div v-else>
            <!-- {{ currentEvent.value }} -->
            <el-select
              v-model="currentEvent.value"
              clearable
              placeholder="Please select data"
            >
              <el-option
                v-for="(item, dataIndex) in dataItemList"
                :key="dataIndex"
                :label="item.name"
                :value="item.url"
              ></el-option>
            </el-select>
          </div>
        </vue-scroll>
      </el-row>
      <el-row v-else>
        <!-- {{ currentEvent.value }} -->
        <el-select
          v-model="currentEvent.value"
          clearable
          placeholder="Please select data"
        >
          <el-option
            v-for="(item, dataIndex) in dataItemList"
            :key="dataIndex"
            :label="item.name"
            :value="item.url"
          ></el-option>
        </el-select>
      </el-row>
      <el-row>
        <el-button size="small" type="success" round @click="submitResource"
          >Submit</el-button
        >
      </el-row>
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
        if (val != "") {
          this.doi = val.doi;
          this.currentCell = val;
          this.init();
        }
      },
      deep: true,
    },
  },

  computed: {
    filterEvent() {
      let event = this.currentEvent;
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
  data() {
    return {
      doi: "",
      modelIntroduction: {},
      modelInstance: {},
      md5: "",
      stateList: [],
      stateInView: {},
      currentCell: this.cell,
      dataItemList: [],
      projectId: this.$route.params.projectId,
      currentEvent: {},
    };
  },

  methods: {
    async init() {
      await this.getModelInfo();
      await this.getResources();
    },

    async getModelInfo() {
      let data = await get(
        `/GeoProblemSolving/modelTask/ModelBehavior/${this.doi}`
      ); //获得模型所有信息
      this.md5 = data.md5;
      this.modelIntroduction = data;
      this.stateList = data.convertMdlJson;
      this.currentEvent = this.convertStateList();
    },
    convertStateList() {
      if (this.stateList == "") {
        return;
      }
      let stateList = this.stateList;
      let currentCell = this.currentCell;
      let current;
      let event = stateList.forEach((state) => {
        if (state.name == currentCell.stateName) {
          let events = state.Event;
          current = events.filter((event) => {
            return event.name == currentCell.name;
          });
          current = { state, Event, ...current[0] };
        }
      });
      console.log(current);
      return current;
    },

    async getResources() {
      let data = await get(`/GeoProblemSolving/r/dataItems/${this.projectId}`);
      this.dataItemList = data;
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

    async submitResource() {
      
    },
  },
};
</script>

<style lang="scss" scoped>
.mainContain {
  width: 100%;
}
.data {
  font-size: 15px;
  .dataTitle {
    font-weight: 600;
  }
}
.eventDivider {
  >>> .el-divider--horizontal {
    margin: 5px 0;
  }
}
.table {
  >>> .el-table__expanded-cell {
    background-color: rgba(230, 239, 255, 0.4);
    padding: 0 10px;
    font-size: 14px;
  }
  >>> .el-form-item__label {
    font-weight: 600;
    font-style: italic;
    color: rgb(58, 63, 73);
  }
  >>> .el-form-item {
    margin-bottom: 5px;
  }
}
</style>