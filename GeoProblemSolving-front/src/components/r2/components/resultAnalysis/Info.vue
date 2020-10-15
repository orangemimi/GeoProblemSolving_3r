<template>
  <div>
    <el-row>
      <el-col :span="4" :offset="1">
        <div>
          <el-card>
            <div>
              <div>Name:</div>
              <div>{{ result.name }}</div>
            </div>
            <div>
              <div>Description:</div>
              <div>{{ result.description }}</div>
            </div>
            <div>
              <div>Created time:</div>
              <div>{{ result.createTime }}</div>
            </div>
            <div>
              <div>Update time:</div>
              <div>{{ result.updateTime }}</div>
            </div>
          </el-card>
        </div>
      </el-col>
      <el-col :span="16" :offset="1">
        <el-card>
          <div class="title">Simulation Construction</div>
          <div v-for="(step, index) in simulationStepList" :key="index">
            <div>{{ step.name }}</div>
            <!-- 过滤step对应的instance -->
            <el-table
              :data="getStepInstance(step, filterCheckedInstanceList)"
              border
              style="width: 100%"
            >
              <el-table-column prop="name" label="Instance name" width="580">
              </el-table-column>
              <el-table-column label="Selected for map" width="180">
                <template slot-scope="scope">
                  <el-checkbox
                    v-model="scope.row.checkedForMap"
                    disabled
                  ></el-checkbox>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <div>
            <map-create
              :dataItems="allDataItemList"
              :createMapInstances="checkedInstances"
              :initXml="initXml"
              @emitxml="emitxml"
            ></map-create>
          </div>
          <div>
            <div class="title">Simulation Result</div>
            <div v-html="result.detail"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { post, get } from "@/axios";
// import mxGraph from "@/components/utils/mxGraph/mxGraph";

import mapCreate from "./../mapCreate";
export default {
  components: {
    // mxGraph,
    mapCreate,
  },
  props: {
    resultInfo: {
      type: Object,
    },
    simulationStepList: {
      type: Array,
    },
    allInstances: {
      type: Array,
    },
    allMaps: {
      type: Array,
    },
    allDataItems: {
      type: Array,
    },
  },
  data() {
    return {
      result: this.resultInfo,
      stepList: this.simulationStepList,
      allInstanceList: this.allInstances,
      allMapList: this.allMaps,
      allDataItemList: this.allDataItems,
      initXml: "",
    };
  },

  watch: {
    resultInfo: {
      handler(val) {
        this.result = val;
      },
      deep: true,
    },
    simulationStepList: {
      handler(val) {
        this.stepList = val;
      },
      deep: true,
    },
    allInstances: {
      handler(val) {
        this.allInstanceList = val;
      },
      deep: true,
    },
    allMaps: {
      handler(val) {
        console.log(val);
        this.allMapList = val;
      },
      deep: true,
    },
    allInstances: {
      handler(val) {
        this.allInstanceList = val;
      },
      deep: true,
    },
    allDataItems: {
      handler(val) {
        console.log(val);
        this.allDataItemList = val;
      },
      deep: true,
    },
  },

  computed: {
    filterCheckedInstanceList() {
      let instances = this.allInstanceList;
      let maps = this.allMapList;
      instances.forEach((item) => {
        item.checkedForMap = "";
        if (
          maps.some((map) =>
            map.modelInstanceIdList.some((id) => id == item.id)
          )
        ) {
          item.checkedForMap = true;
        }
      });
      console.log(instances);
      return instances;
    },

    checkedInstances() {
      console.log(this.filterCheckedInstanceList);
      // let instances = JSON.parse(
      //   JSON.stringify(this.filterCheckedInstanceList)
      // );
      let instances = [...this.filterCheckedInstanceList];

      instances = instances.filter((item) => {
        return item.checkedForMap == true;
      });
      console.log(instances);
      return instances;
    },
  },

  methods: {
    getStepInstance(step, instances) {
      return instances.filter((instance) => instance.stepId === step.stepId);
    },

    emitxml(val) {
      console.log(val);
    },
  },
};
</script>

<style>
.title {
  margin: 10px;
  font-size: 30px;
  font-weight: 600;
}
</style>