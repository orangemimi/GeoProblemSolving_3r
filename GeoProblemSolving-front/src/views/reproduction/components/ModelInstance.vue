<!--  -->
<template>
  <div>
    <el-col :span="4">
      <el-row>
        <el-card shadow="never">
          <div class="process-data-title">Model Instance</div>
          <div
            v-for="(instance, index) in filterModelInstance"
            :key="index + 'instance'"
          >
            <el-card
              class="box-card"
              :class="instance.status == 0 ? 'calculating' : 'finishing'"
            >
              <div class="instance-head">
                <div class="instance-name">{{ instance.name }}</div>
                <i
                  class="el-icon-close"
                  @click="deleteInstance(instance.id)"
                ></i>
              </div>
              <div v-if="instance.status == 0" class="calculating-icon">
                <i
                  class="el-icon-loading calculating-icon"
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
              <div class="instance-head">
                <div class="instance-name">{{ instance.name }}</div>
                <i
                  class="el-icon-close"
                  @click="deleteInstance(instance.id)"
                ></i>
              </div>
              <div class="error-icon">
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
  </div>
</template>

<script>
export default {
  components: {},

  watch: {},

  computed: {},

  data() {
    return { modelInstances: [], stateList: [], record: {}, timer: "" };
  },

  methods: {
    async getModelInstances() {
      // console.log(this.stepInfo);
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
    folkInstance(instance) {
      this.instanceFolk = instance;
    },
    deleteInstance(instanceId) {
      this.deleteInstanceId = instanceId;
      this.deleteInstanceDialog = true;
    },

    async mapCreate() {
      // this.dataNodes = [];
      // this.dataNodesIntermedia = [];

      this.filterCreateMapInstances = this.filterModelInstance.filter(
        (instance) => instance.checkedForMap == true
      );

      if (this.filterCreateMapInstances == "") {
        this.$message({
          message: "There is no selected instance",
          type: "error",
        });
      } else {
        this.checkedInstances = [...this.filterCreateMapInstances];
        this.activeTabs = "conceptMap";
      }
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
        if (item.toModelInstanceList != null) {
          item.toModelInstanceList.forEach((id, index) => {
            if (id == this.deleteInstanceId) {
              item.toModelInstanceList.splice(index, 1);
              patch(`/GeoProblemSolving/r/dataItems/${item.id}`, item);
            }
          });
        }
        if (item.fromModelInstance != null) {
          if (item.fromModelInstance == this.deleteInstanceId) {
            dataItem.splice(itemIndex, 1);
            del(`/GeoProblemSolving/r/dataItems/${item.id}`);
          }
        }
      });
      this.deleteInstanceDialog = false;
    },

    handleCheckAllChange(val) {
      this.checkAll = val;
      this.filterModelInstance.forEach((item) => {
        item.checkedForMap = this.checkAll; //只改变数据的状态
      });
    },
  },

  mounted: {},
};
</script>
<style lang='scss' scoped>
</style>