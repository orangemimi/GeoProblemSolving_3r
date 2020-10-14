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

          <div><mx-graph :sendXml="newMap"></mx-graph></div>
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
import mxGraph from "@/components/utils/mxGraph/mxGraph";
export default {
  components: {
    mxGraph,
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
  },
  data() {
    return {
      result: this.resultInfo,
      stepList: this.simulationStepList,
      allInstanceList: this.allInstances,
      allMapList: this.allMaps,
      sendXml: "",
    };
  },
  computed: {
    filterCheckedInstanceList() {
      let instances = this.allInstanceList;
      let maps = this.allMapList;
      instances.forEach((item) => {
        if (
          maps.some((map) =>
            map.modelInstanceIdList.some((id) => id == item.id)
          )
        ) {
          item.checkedForMap = true;
        }
      });
      return instances;
    },
    newMap() {
      this.sendXml = "";
      this.allMaps.forEach((map) => {
        this.sendXml = `<mxGraphModel><root><mxCell id="0"/><mxCell id="1" parent="0"/>${map.mapXml}</root></mxGraphModel>`;
      });
      console.log(this.sendXml);
      return this.sendXml;
    },
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
        this.allMapList = val;
      },
      deep: true,
    },
  },
  methods: {
    getStepInstance(step, instances) {
      return instances.filter((instance) => instance.stepId === step.stepId);
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